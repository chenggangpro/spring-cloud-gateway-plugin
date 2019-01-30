package pro.chenggang.plugin.springcloud.gateway.response.factory;

import pro.chenggang.plugin.springcloud.gateway.response.strategy.ExceptionHandlerStrategy;

/**
 * Exception Handler Strategy Factory
 * @author chenggang
 * @date 2019/01/29
 */
public interface ExceptionHandlerStrategyFactory {

    /**
     * Get Strategy
     * @param clazz
     * @return if strategy not exist just return null
     */
    ExceptionHandlerStrategy getStrategy(Class<? extends Throwable> clazz);

}
