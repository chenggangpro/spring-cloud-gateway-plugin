package pro.chenggang.plugin.springcloud.gateway.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pro.chenggang.plugin.springcloud.gateway.filter.RequestLogFilter;
import pro.chenggang.plugin.springcloud.gateway.filter.ResponseLogFilter;

/**
 * Gateway Plugin Config
 * @author chenggang
 * @date 2019/01/29
 */
@Configuration
public class RequestResponseLogConfig {

    @Bean
    @ConditionalOnMissingBean(RequestLogFilter.class)
    public RequestLogFilter requestLogFilter(){
        return new RequestLogFilter();
    }

    @Bean
    @ConditionalOnMissingBean(ResponseLogFilter.class)
    public ResponseLogFilter responseLogFilter(){
        return new ResponseLogFilter();
    }

}
