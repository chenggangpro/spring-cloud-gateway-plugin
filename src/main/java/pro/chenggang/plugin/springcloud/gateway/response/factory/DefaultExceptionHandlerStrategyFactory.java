package pro.chenggang.plugin.springcloud.gateway.response.factory;

import lombok.extern.slf4j.Slf4j;
import org.springframework.util.Assert;
import pro.chenggang.plugin.springcloud.gateway.response.strategy.DefaultExceptionHandlerStrategy;
import pro.chenggang.plugin.springcloud.gateway.response.strategy.ExceptionHandlerStrategy;

import java.util.HashMap;
import java.util.Map;

/**
 * Default ExceptionHandlerStrategy Factory
 * @author chenggang
 * @date 2019/01/29
 */
@Slf4j
public class DefaultExceptionHandlerStrategyFactory implements ExceptionHandlerStrategyFactory {

    private final Map<Class<? extends Throwable>, ExceptionHandlerStrategy> strategyContainer;

    private final ExceptionHandlerStrategy defaultExceptionHandlerStrategy;

    public DefaultExceptionHandlerStrategyFactory() {
        this(new DefaultExceptionHandlerStrategy());
    }

    public DefaultExceptionHandlerStrategyFactory(ExceptionHandlerStrategy defaultExceptionHandlerStrategy) {
        this.strategyContainer = new HashMap<>();
        this.defaultExceptionHandlerStrategy = defaultExceptionHandlerStrategy;
    }

    /**
     * addStrategy
     * @param exceptionHandlerStrategy
     */
    public void addStrategy(ExceptionHandlerStrategy exceptionHandlerStrategy){
        Assert.notNull(exceptionHandlerStrategy,"ExceptionStrategy Required");
        Class clazz = exceptionHandlerStrategy.getHandleClass();
        Assert.notNull(clazz,"ExceptionStrategy Handle Class Required");
        if(!strategyContainer.containsKey(clazz)){
            strategyContainer.put(clazz, exceptionHandlerStrategy);
            log.debug("[DefaultExceptionHandlerStrategyFactory] Add Strategy,Class:{},Strategy:{}",clazz,exceptionHandlerStrategy);
        }
    }

    @Override
    public ExceptionHandlerStrategy getStrategy(Class clazz) {
        ExceptionHandlerStrategy strategy =  strategyContainer.get(clazz);
        if(null == strategy && null != this.defaultExceptionHandlerStrategy){
            log.debug("[DefaultExceptionHandlerStrategyFactory]Get Target Exception Handler Strategy Is Null,Use Default Strategy");
            strategy = defaultExceptionHandlerStrategy;
        }
        log.debug("[DefaultExceptionHandlerStrategyFactory] Get Strategy,Exception Class Name:{},Strategy:{}",clazz.getSimpleName(),strategy.getClass().getSimpleName());
        return strategy;
    }

}
