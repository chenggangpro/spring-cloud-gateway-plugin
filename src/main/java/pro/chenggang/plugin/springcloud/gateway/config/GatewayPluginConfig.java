package pro.chenggang.plugin.springcloud.gateway.config;

import com.netflix.hystrix.HystrixObservableCommand;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.DispatcherHandler;
import pro.chenggang.plugin.springcloud.gateway.context.ContextExtraDataGenerator;
import pro.chenggang.plugin.springcloud.gateway.filter.GatewayContextFilter;
import pro.chenggang.plugin.springcloud.gateway.filter.RemoveGatewayContextFilter;
import pro.chenggang.plugin.springcloud.gateway.filter.factory.RouteHystrixGatewayFilterFactory;
import pro.chenggang.plugin.springcloud.gateway.properties.GatewayPluginProperties;
import rx.RxReactiveStreams;

/**
 * Gateway Plugin Config
 * @author chenggang
 * @date 2019/01/29
 */
@Slf4j
@Configuration
public class GatewayPluginConfig {

    @Bean
    @ConditionalOnMissingBean(GatewayPluginProperties.class)
    @ConfigurationProperties(GatewayPluginProperties.GATEWAY_PLUGIN_PROPERTIES_PREFIX)
    public GatewayPluginProperties gatewayPluginProperties(){
        return new GatewayPluginProperties();
    }

    @Bean
    @ConditionalOnBean(GatewayPluginProperties.class)
    @ConditionalOnMissingBean(GatewayContextFilter.class)
    public GatewayContextFilter gatewayContextFilter(GatewayPluginProperties gatewayPluginProperties , @Autowired(required = false) ContextExtraDataGenerator contextExtraDataGenerator){
        GatewayContextFilter gatewayContextFilter = new GatewayContextFilter(gatewayPluginProperties,contextExtraDataGenerator);
        log.debug("Load GatewayContextFilter Config Bean");
        return gatewayContextFilter;
    }

    @Bean
    @ConditionalOnBean(GatewayPluginProperties.class)
    @ConditionalOnMissingBean(RemoveGatewayContextFilter.class)
    public RemoveGatewayContextFilter removeGatewayContextFilter(){
        RemoveGatewayContextFilter gatewayContextFilter = new RemoveGatewayContextFilter();
        log.debug("Load RemoveGatewayContextFilter Config Bean");
        return gatewayContextFilter;
    }

    @Configuration
    @ConditionalOnClass({ HystrixObservableCommand.class, RxReactiveStreams.class })
    protected static class HystrixConfiguration {

        @Bean
        public RouteHystrixGatewayFilterFactory routeHystrixGatewayFilterFactory(ObjectProvider<DispatcherHandler> dispatcherHandler) {
            return new RouteHystrixGatewayFilterFactory(dispatcherHandler);
        }

    }

}
