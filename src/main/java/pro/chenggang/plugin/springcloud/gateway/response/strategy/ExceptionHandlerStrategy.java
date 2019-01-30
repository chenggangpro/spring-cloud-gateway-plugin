package pro.chenggang.plugin.springcloud.gateway.response.strategy;


import pro.chenggang.plugin.springcloud.gateway.response.ExceptionHandlerResult;

/**
 * ExceptionHandlerStrategy
 * @author chenggang
 * @date 2019/01/29
 */
public interface ExceptionHandlerStrategy<T extends Throwable> {

    /**
     * get the exception class
     * @return Class
     */
    Class<T> getHandleClass();

    /**
     * Handle Exception
     * @param throwable
     * @return ExceptionHandlerResult
     */
    ExceptionHandlerResult handleException(Throwable throwable);

}
