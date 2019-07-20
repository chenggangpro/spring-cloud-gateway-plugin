package pro.chenggang.plugin.springcloud.gateway.predicate.processor;

import org.springframework.cloud.gateway.handler.predicate.AbstractRoutePredicateFactory;
import org.springframework.cloud.gateway.route.Route;
import org.springframework.web.server.ServerWebExchange;
import pro.chenggang.plugin.springcloud.gateway.predicate.support.DynamicRouteConfig;
import pro.chenggang.plugin.springcloud.gateway.predicate.support.PreprocessResult;

import java.util.Optional;

/**
 * Pre process ServerWebExchange for dynamic route predicate
 * @author chenggang
 * @date 2019/07/17
 */
public interface DynamicRouteProcessor<T> {

    /**
     * preprocess action
     * @param exchange ServerWebExchange
     * @return process Result
     */
    Optional<PreprocessResult<T>> preprocess(ServerWebExchange exchange);

    /**
     * process to unify config for predicate
     * @param preprocessResult
     * @param route
     * @return
     */
    Optional<DynamicRouteConfig> processConfig(PreprocessResult<T> preprocessResult, Route route);

    /**
     * target predicate bean 's class
     * @return
     */
    Optional<Class< ? extends AbstractRoutePredicateFactory>> targetPredicateBeanClass();
}
