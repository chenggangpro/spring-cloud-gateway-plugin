package pro.chenggang.plugin.springcloud.gateway.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pro.chenggang.plugin.springcloud.gateway.predicate.processor.DefaultDynamicRouteProcessor;
import pro.chenggang.plugin.springcloud.gateway.predicate.processor.DynamicRouteProcessor;

/**
 * Dynamic Route Configuration
 * @author chenggang
 * @date 2019/07/18
 */
@Configuration
@Slf4j
public class DynamicRouteConfiguration {

    @Bean
    @ConditionalOnMissingBean(DynamicRouteProcessor.class)
    public DynamicRouteProcessor dynamicRoutePreprocessor(){
        return new DefaultDynamicRouteProcessor();
    }

}
