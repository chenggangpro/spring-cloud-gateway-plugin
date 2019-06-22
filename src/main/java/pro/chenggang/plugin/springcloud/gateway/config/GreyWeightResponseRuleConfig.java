package pro.chenggang.plugin.springcloud.gateway.config;

import com.netflix.loadbalancer.IRule;
import com.netflix.niws.loadbalancer.DiscoveryEnabledNIWSServerList;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.netflix.ribbon.RibbonClientConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import pro.chenggang.plugin.springcloud.gateway.grey.GreyPredicate;
import pro.chenggang.plugin.springcloud.gateway.grey.GreyWeightResponseRule;
import pro.chenggang.plugin.springcloud.gateway.grey.support.PredicateFactory;
import pro.chenggang.plugin.springcloud.gateway.properties.GreyProperties;

import java.util.Collections;

/**
 * Gateway Plugin Config
 * @author chenggang
 * @date 2019/01/29
 */
@Slf4j
@Configuration
@ConditionalOnClass({RibbonClientConfiguration.class, DiscoveryEnabledNIWSServerList.class})
@AutoConfigureBefore(RibbonClientConfiguration.class)
public class GreyWeightResponseRuleConfig {

    @Bean
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    @ConditionalOnProperty(prefix = GreyProperties.GREY_PROPERTIES_PREFIX,value = "grey-ribbon-rule",havingValue = "WEIGHT_RESPONSE")
    public IRule ribbonRule(PredicateFactory predicateFactory, GreyProperties greyProperties) {
        GreyWeightResponseRule greyWeightResponseRule;
        if(greyProperties.getEnable()){
            greyWeightResponseRule = new GreyWeightResponseRule(predicateFactory.getAllPredicate(GreyPredicate.class));
        }else{
            greyWeightResponseRule = new GreyWeightResponseRule(Collections.emptyList());
        }
        log.debug("Load Grey Weight Response Rule Config Bean");
        return greyWeightResponseRule;
    }
}
