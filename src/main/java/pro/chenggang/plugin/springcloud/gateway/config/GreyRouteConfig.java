package pro.chenggang.plugin.springcloud.gateway.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.cloud.gateway.config.LoadBalancerProperties;
import org.springframework.cloud.gateway.filter.LoadBalancerClientFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import pro.chenggang.plugin.springcloud.gateway.filter.GreyContextFilter;
import pro.chenggang.plugin.springcloud.gateway.filter.GreyLoadBalancerClientFilter;
import pro.chenggang.plugin.springcloud.gateway.properties.GreyProperties;

/**
 * Gateway Plugin Config
 * @author chenggang
 * @date 2019/01/29
 */
@Configuration
@ConditionalOnProperty(prefix = GreyProperties.GREY_PROPERTIES_PREFIX,value = "enable",havingValue = "true")
public class GreyRouteConfig {

    @Bean
    @ConditionalOnMissingBean(GreyProperties.class)
    public GreyProperties greyProperties(){
        return new GreyProperties();
    }

    @Primary
    @Bean
    public LoadBalancerClientFilter loadBalancerClientFilter(LoadBalancerClient client, LoadBalancerProperties properties) {
        return new GreyLoadBalancerClientFilter(client,properties);
    }

    @Bean
    @ConditionalOnMissingBean(GreyContextFilter.class)
    public GreyContextFilter greyContextFilter(GreyProperties greyProperties){
        return new GreyContextFilter(greyProperties);
    }

}
