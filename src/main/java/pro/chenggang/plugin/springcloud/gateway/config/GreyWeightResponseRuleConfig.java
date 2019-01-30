package pro.chenggang.plugin.springcloud.gateway.config;

import com.netflix.loadbalancer.IRule;
import com.netflix.niws.loadbalancer.DiscoveryEnabledNIWSServerList;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.cloud.netflix.ribbon.RibbonClientConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import pro.chenggang.plugin.springcloud.gateway.grey.GreyWeightResponseRule;

/**
 * Gateway Plugin Config
 * @author chenggang
 * @date 2019/01/29
 */
@Configuration
@ConditionalOnClass(DiscoveryEnabledNIWSServerList.class)
@AutoConfigureBefore(RibbonClientConfiguration.class)
public class GreyWeightResponseRuleConfig {

    @Bean
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public IRule ribbonRule() {
        return new GreyWeightResponseRule();
    }
}
