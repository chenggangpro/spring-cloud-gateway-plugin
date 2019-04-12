package pro.chenggang.plugin.springcloud.gateway.config;

import com.netflix.loadbalancer.IRule;
import com.netflix.niws.loadbalancer.DiscoveryEnabledNIWSServerList;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.netflix.ribbon.RibbonClientConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import pro.chenggang.plugin.springcloud.gateway.grey.GreyWeightResponseRule;
import pro.chenggang.plugin.springcloud.gateway.properties.GreyProperties;

/**
 * Gateway Plugin Config
 * @author chenggang
 * @date 2019/01/29
 */
@Configuration
@ConditionalOnClass(DiscoveryEnabledNIWSServerList.class)
@AutoConfigureBefore(RibbonClientConfiguration.class)
@ConditionalOnProperty(prefix = GreyProperties.GREY_PROPERTIES_PREFIX,value = "enable",havingValue = "true")
public class GreyWeightResponseRuleConfig {

    @Bean
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    @ConditionalOnProperty(prefix = GreyProperties.GREY_PROPERTIES_PREFIX,value = "greyRibbonRule",havingValue = "WEIGHT_RESPONSE")
    public IRule ribbonRule() {
        return new GreyWeightResponseRule();
    }
}
