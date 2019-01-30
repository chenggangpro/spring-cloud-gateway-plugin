package pro.chenggang.plugin.springcloud.gateway.response.factory;

import lombok.extern.slf4j.Slf4j;
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
     * @param clazz
     * @param exceptionHandlerStrategy
     */
    public void addStrategy(Class<? extends Throwable> clazz,ExceptionHandlerStrategy exceptionHandlerStrategy){
        if(!strategyContainer.containsKey(clazz)){
            strategyContainer.put(clazz, exceptionHandlerStrategy);
            log.debug("[DefaultExceptionHandlerStrategyFactory] Add Strategy,Class:{},Strategy:{}",clazz,exceptionHandlerStrategy);
        }
    }

    /**
     * Get Strategy Size
     * @return
     */
    public int getStrategySize(){
        return strategyContainer.size()+1;
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
