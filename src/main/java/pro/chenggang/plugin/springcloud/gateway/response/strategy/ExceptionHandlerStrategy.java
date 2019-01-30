package pro.chenggang.plugin.springcloud.gateway.response.strategy;


import pro.chenggang.plugin.springcloud.gateway.response.ExceptionHandlerResult;

/**
 * ExceptionHandlerStrategy
 * @author chenggang
 * @date 2019/01/29
 */
@FunctionalInterface
public interface ExceptionHandlerStrategy {

    /**
     * Handle Exception
     * @param throwable
     * @return ExceptionHandlerResult
     */
    ExceptionHandlerResult handleException(Throwable throwable);

}
