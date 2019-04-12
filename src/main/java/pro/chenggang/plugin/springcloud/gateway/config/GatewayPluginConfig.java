package pro.chenggang.plugin.springcloud.gateway.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pro.chenggang.plugin.springcloud.gateway.filter.GatewayContextFilter;
import pro.chenggang.plugin.springcloud.gateway.properties.GatewayPluginProperties;

/**
 * Gateway Plugin Config
 * @author chenggang
 * @date 2019/01/29
 */
@Configuration
public class GatewayPluginConfig {

    @Bean
    @ConditionalOnMissingBean(GatewayPluginProperties.class)
    public GatewayPluginProperties gatewayPluginProperties(){
        return new GatewayPluginProperties();
    }

    @Bean
    @ConditionalOnMissingBean(GatewayContextFilter.class)
    public GatewayContextFilter gatewayContextFilter(GatewayPluginProperties gatewayPluginProperties){
        return new GatewayContextFilter(gatewayPluginProperties);
    }

}
