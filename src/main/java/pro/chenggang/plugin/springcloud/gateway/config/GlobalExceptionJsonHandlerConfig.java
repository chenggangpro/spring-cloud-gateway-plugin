package pro.chenggang.plugin.springcloud.gateway.config;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
import org.springframework.cloud.gateway.support.NotFoundException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.web.reactive.result.view.ViewResolver;
import org.springframework.web.server.ResponseStatusException;
import pro.chenggang.plugin.springcloud.gateway.response.JsonExceptionHandler;
import pro.chenggang.plugin.springcloud.gateway.response.factory.DefaultExceptionHandlerStrategyFactory;
import pro.chenggang.plugin.springcloud.gateway.response.factory.ExceptionHandlerStrategyFactory;
import pro.chenggang.plugin.springcloud.gateway.response.strategy.ExceptionHandlerStrategy;
import pro.chenggang.plugin.springcloud.gateway.response.strategy.NotFoundExceptionHandlerStrategy;
import pro.chenggang.plugin.springcloud.gateway.response.strategy.ResponseStatusExceptionHandlerStrategy;

import java.util.Collections;
import java.util.List;

/**
 * Gateway Plugin Config
 * @author chenggang
 * @date 2019/01/29
 */
@Configuration
public class GlobalExceptionJsonHandlerConfig {

    /**
     * NotFoundExceptionHandlerStrategy
     * @return
     */
    @Bean
    public ExceptionHandlerStrategy notFoundExceptionHandlerStrategy(){
        return new NotFoundExceptionHandlerStrategy();
    }


    /**
     * ResponseStatusExceptionHandlerStrategy
     * @return
     */
    @Bean
    public ExceptionHandlerStrategy responseStatusExceptionHandlerStrategy(){
        return new ResponseStatusExceptionHandlerStrategy();
    }

    /**
     * ExceptionHandlerStrategyFactory
     * @param notFoundExceptionHandlerStrategy
     * @param responseStatusExceptionHandlerStrategy
     * @return
     */
    @Bean
    @ConditionalOnMissingBean(ExceptionHandlerStrategyFactory.class)
    public ExceptionHandlerStrategyFactory exceptionHandlerStrategyFactory(ExceptionHandlerStrategy notFoundExceptionHandlerStrategy,
                                                                           ExceptionHandlerStrategy responseStatusExceptionHandlerStrategy){
        DefaultExceptionHandlerStrategyFactory factory = new DefaultExceptionHandlerStrategyFactory();
        factory.addStrategy(NotFoundException.class,notFoundExceptionHandlerStrategy);
        factory.addStrategy(ResponseStatusException.class,responseStatusExceptionHandlerStrategy);
        return factory;
    }


    /**
     * ErrorWebExceptionHandler
     * @param viewResolversProvider
     * @param serverCodecConfigurer
     * @param exceptionHandlerExceptionHandlerStrategyFactory
     * @return
     */
    @Primary
    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE)
    public ErrorWebExceptionHandler errorWebExceptionHandler(ObjectProvider<List<ViewResolver>> viewResolversProvider,
                                                             ServerCodecConfigurer serverCodecConfigurer,
                                                             ExceptionHandlerStrategyFactory exceptionHandlerExceptionHandlerStrategyFactory) {

        JsonExceptionHandler jsonExceptionHandler = new JsonExceptionHandler(exceptionHandlerExceptionHandlerStrategyFactory);
        jsonExceptionHandler.setViewResolvers(viewResolversProvider.getIfAvailable(Collections::emptyList));
        jsonExceptionHandler.setMessageWriters(serverCodecConfigurer.getWriters());
        jsonExceptionHandler.setMessageReaders(serverCodecConfigurer.getReaders());
        return jsonExceptionHandler;
    }
}
