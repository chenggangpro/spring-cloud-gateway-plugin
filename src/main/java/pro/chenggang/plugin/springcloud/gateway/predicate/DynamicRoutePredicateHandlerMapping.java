package pro.chenggang.plugin.springcloud.gateway.predicate;

import org.springframework.cloud.gateway.config.GlobalCorsProperties;
import org.springframework.cloud.gateway.handler.AsyncPredicate;
import org.springframework.cloud.gateway.handler.FilteringWebHandler;
import org.springframework.cloud.gateway.handler.RoutePredicateHandlerMapping;
import org.springframework.cloud.gateway.handler.predicate.AbstractRoutePredicateFactory;
import org.springframework.cloud.gateway.route.Route;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.util.Assert;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.server.ServerWebExchange;
import pro.chenggang.plugin.springcloud.gateway.predicate.processor.DynamicRouteProcessor;
import pro.chenggang.plugin.springcloud.gateway.predicate.support.DynamicRouteConfig;
import pro.chenggang.plugin.springcloud.gateway.predicate.support.PreprocessResult;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

import static org.springframework.cloud.gateway.support.ServerWebExchangeUtils.GATEWAY_HANDLER_MAPPER_ATTR;
import static org.springframework.cloud.gateway.support.ServerWebExchangeUtils.GATEWAY_PREDICATE_ROUTE_ATTR;
import static org.springframework.cloud.gateway.support.ServerWebExchangeUtils.GATEWAY_ROUTE_ATTR;

/**
 * Support Dynamic Route Predicate
 * @author chenggang
 * @date 2019/07/17
 */
@Configuration
public class DynamicRoutePredicateHandlerMapping extends RoutePredicateHandlerMapping {

    private final FilteringWebHandler webHandler;
    private final RouteLocator routeLocator;
    private final Integer managementPort;
    private DynamicRouteProcessor dynamicRouteProcessor;
    private Map<Class<? extends AbstractRoutePredicateFactory>, AbstractRoutePredicateFactory> routePredicateFactoryMap = new HashMap<>();

    public DynamicRoutePredicateHandlerMapping(FilteringWebHandler webHandler, RouteLocator routeLocator, GlobalCorsProperties globalCorsProperties, Environment environment,DynamicRouteProcessor dynamicRouteProcessor,
                                               List<AbstractRoutePredicateFactory> routePredicateFactoryList) {
        super(webHandler, routeLocator, globalCorsProperties, environment);
        this.webHandler = webHandler;
        this.routeLocator = routeLocator;

        if (environment.containsProperty("management.server.port")) {
            managementPort = new Integer(environment.getProperty("management.server.port"));
        } else {
            managementPort = null;
        }
        setOrder(1);
        setCorsConfigurations(globalCorsProperties.getCorsConfigurations());
        setDynamicRouteProcessor(dynamicRouteProcessor);
        addRoutePredicateFactory(routePredicateFactoryList);
    }

    /**
     * set dynamicRoutePreprocessor
     * @param dynamicRouteProcessor
     */
    public void setDynamicRouteProcessor(DynamicRouteProcessor dynamicRouteProcessor) {
        Assert.notNull(dynamicRouteProcessor,"DynamicRoutePreprocessor Can not be null");
        this.dynamicRouteProcessor = dynamicRouteProcessor;
    }

    /**
     * add All Predicate Factory
     * @param routePredicateFactoryList
     */
    public void addRoutePredicateFactory(List<AbstractRoutePredicateFactory> routePredicateFactoryList) {
        for(AbstractRoutePredicateFactory abstractRoutePredicateFactory : routePredicateFactoryList){
            this.routePredicateFactoryMap.put(abstractRoutePredicateFactory.getClass(),abstractRoutePredicateFactory);
        }
    }

    @Override
    protected Mono<?> getHandlerInternal(ServerWebExchange exchange) {
        // don't handle requests on the management port if set
        if (managementPort != null && exchange.getRequest().getURI().getPort() == managementPort.intValue()) {
            return Mono.empty();
        }
        exchange.getAttributes().put(GATEWAY_HANDLER_MAPPER_ATTR, getSimpleName());

        return lookupRoute(exchange)
                // .log("route-predicate-handler-mapping", Level.FINER) //name this
                .flatMap((Function<Route, Mono<?>>) r -> {
                    exchange.getAttributes().remove(GATEWAY_PREDICATE_ROUTE_ATTR);
                    if (logger.isDebugEnabled()) {
                        logger.debug("Mapping [" + getExchangeDesc(exchange) + "] to " + r);
                    }

                    exchange.getAttributes().put(GATEWAY_ROUTE_ATTR, r);
                    return Mono.just(webHandler);
                }).switchIfEmpty(Mono.empty().then(Mono.fromRunnable(() -> {
                    exchange.getAttributes().remove(GATEWAY_PREDICATE_ROUTE_ATTR);
                    if (logger.isTraceEnabled()) {
                        logger.trace("No RouteDefinition found for [" + getExchangeDesc(exchange) + "]");
                    }
                })));
    }

    @Override
    protected CorsConfiguration getCorsConfiguration(Object handler, ServerWebExchange exchange) {
        // TODO: support cors configuration via properties on a route see gh-229
        // see RequestMappingHandlerMapping.initCorsConfiguration()
        // also see https://github.com/spring-projects/spring-framework/blob/master/spring-web/src/test/java/org/springframework/web/cors/reactive/CorsWebFilterTests.java

        return super.getCorsConfiguration(handler, exchange);
    }

    //TODO: get desc from factory?
    private String getExchangeDesc(ServerWebExchange exchange) {
        StringBuilder out = new StringBuilder();
        out.append("Exchange: ");
        out.append(exchange.getRequest().getMethod());
        out.append(" ");
        out.append(exchange.getRequest().getURI());
        return out.toString();
    }

    @Override
    protected Mono<Route> lookupRoute(ServerWebExchange exchange) {
        Optional<PreprocessResult> preprocessResult = dynamicRouteProcessor.preprocess(exchange);
        return this.routeLocator
                .getRoutes()
                //individually filter routes so that filterWhen error delaying is not a problem
                .concatMap(route -> Mono
                        .just(route)
                        .filterWhen(r -> {
                            exchange.getAttributes().put(GATEWAY_PREDICATE_ROUTE_ATTR, r.getId());
                            if(!preprocessResult.isPresent()){
                                logger.trace("Dynamic Route Predicate Pre Processor Is Empty Use Route Predicate,RouteId:"+r.getId());
                                return r.getPredicate().apply(exchange);
                            }
                            Optional<DynamicRouteConfig> dynamicRouteConfig = dynamicRouteProcessor.processConfig(preprocessResult.get(), r);
                            if(!dynamicRouteConfig.isPresent()){
                                logger.trace("Dynamic Route Predicate Config Is Empty Use Route Predicate,RouteId:"+r.getId());
                                return r.getPredicate().apply(exchange);
                            }
                            Optional<Class<? extends AbstractRoutePredicateFactory>> predicateBeanClass = dynamicRouteProcessor.targetPredicateBeanClass();
                            if(!predicateBeanClass.isPresent()){
                                logger.trace("Dynamic Route Predicate Factory Bean Class Is Empty Use Route Predicate,RouteId:"+r.getId());
                                return r.getPredicate().apply(exchange);
                            }
                            DynamicRouteConfig config = dynamicRouteConfig.get();
                            AbstractRoutePredicateFactory abstractRoutePredicateFactory = routePredicateFactoryMap.get(predicateBeanClass.get());
                            if(Objects.isNull(abstractRoutePredicateFactory)){
                                logger.trace("Dynamic Route Predicate Factory Bean Not Exist Use Route Predicate,RouteId:"+r.getId());
                                return r.getPredicate().apply(exchange);
                            }
                            AsyncPredicate<ServerWebExchange> predicate = abstractRoutePredicateFactory.applyAsync(config);
                            return predicate.apply(exchange);
                        })
                        //instead of immediately stopping main flux due to error, log and swallow it
                        .doOnError(e -> logger.error("Error applying predicate for route: "+route.getId(), e))
                        .onErrorResume(e -> Mono.empty())
                )
                // .defaultIfEmpty() put a static Route not found
                // or .switchIfEmpty()
                // .switchIfEmpty(Mono.<Route>empty().log("noroute"))
                .next()
                //TODO: error handling
                .map(route -> {
                    if (logger.isDebugEnabled()) {
                        logger.debug("Route matched: " + route.getId());
                    }
                    validateRoute(route, exchange);
                    return route;
                });

		/* TODO: trace logging
			if (logger.isTraceEnabled()) {
				logger.trace("RouteDefinition did not match: " + routeDefinition.getId());
			}*/
    }

    /**
     * Validate the given handler against the current request.
     * <p>The default implementation is empty. Can be overridden in subclasses,
     * for example to enforce specific preconditions expressed in URL mappings.
     * @param route the Route object to validate
     * @param exchange current exchange
     * @throws Exception if validation failed
     */
    @Override
    @SuppressWarnings("UnusedParameters")
    protected void validateRoute(Route route, ServerWebExchange exchange) {
    }

    @Override
    protected String getSimpleName() {
        return "RoutePredicateHandlerMapping";
    }

}
