package pro.chenggang.plugin.springcloud.gateway.predicate.processor;

import org.springframework.cloud.gateway.handler.predicate.AbstractRoutePredicateFactory;
import org.springframework.cloud.gateway.route.Route;
import org.springframework.web.server.ServerWebExchange;
import pro.chenggang.plugin.springcloud.gateway.predicate.support.DynamicRouteConfig;
import pro.chenggang.plugin.springcloud.gateway.predicate.support.PreprocessResult;

import java.util.Optional;

/**
 * Default Dynamic Route Preprocessor all method return empty
 * @author chenggang
 * @date 2019/07/17
 */
public class DefaultDynamicRouteProcessor implements DynamicRouteProcessor {

    @Override
    public Optional<PreprocessResult> preprocess(ServerWebExchange exchange) {
        return Optional.empty();
    }

    @Override
    public Optional<DynamicRouteConfig> processConfig(PreprocessResult preprocessResult, Route route) {
        return Optional.empty();
    }

    @Override
    public Optional<Class<? extends AbstractRoutePredicateFactory>> targetPredicateBeanClass() {
        return Optional.empty();
    }
}
