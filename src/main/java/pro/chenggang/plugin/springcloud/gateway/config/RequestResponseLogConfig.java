package pro.chenggang.plugin.springcloud.gateway.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pro.chenggang.plugin.springcloud.gateway.filter.RequestLogFilter;
import pro.chenggang.plugin.springcloud.gateway.filter.ResponseLogFilter;
import pro.chenggang.plugin.springcloud.gateway.properties.GatewayPluginProperties;

/**
 * Gateway Plugin Config
 * @author chenggang
 * @date 2019/01/29
 */
@Slf4j
@Configuration
public class RequestResponseLogConfig {

    @Bean
    @ConditionalOnMissingBean(RequestLogFilter.class)
    @ConditionalOnProperty(prefix = GatewayPluginProperties.GATEWAY_PLUGIN_PROPERTIES_PREFIX,value = "log-request",havingValue = "true")
    public RequestLogFilter requestLogFilter(){
        RequestLogFilter requestLogFilter = new RequestLogFilter();
        log.debug("Load Request Log Filter Config Bean");
        return requestLogFilter;
    }

    @Bean
    @ConditionalOnMissingBean(ResponseLogFilter.class)
    @ConditionalOnProperty(prefix = GatewayPluginProperties.GATEWAY_PLUGIN_PROPERTIES_PREFIX,value = "log-request",havingValue = "true")
    public ResponseLogFilter responseLogFilter(){
        ResponseLogFilter responseLogFilter = new ResponseLogFilter();
        log.debug("Load Response Log Filter Config Bean");
        return responseLogFilter;
    }

}
