package pro.chenggang.plugin.springcloud.gateway.config;

import com.netflix.loadbalancer.IRule;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.netflix.ribbon.RibbonClientConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import pro.chenggang.plugin.springcloud.gateway.grey.GreyDefaultRoundRule;
import pro.chenggang.plugin.springcloud.gateway.grey.GreyPredicate;
import pro.chenggang.plugin.springcloud.gateway.grey.support.PredicateFactory;
import pro.chenggang.plugin.springcloud.gateway.offline.OfflinePredicate;
import pro.chenggang.plugin.springcloud.gateway.properties.GreyProperties;

/**
 * Gateway Plugin Config
 * @author chenggang
 * @date 2019/04/25
 */
@Slf4j
@Configuration
@ConditionalOnClass(RibbonClientConfiguration.class)
@AutoConfigureBefore(RibbonClientConfiguration.class)
public class GreyDefaultRoundRuleConfig {

    @Bean
    @ConditionalOnMissingBean(PredicateFactory.class)
    public PredicateFactory predicateFactory(){
        PredicateFactory predicateFactory = new PredicateFactory();
        predicateFactory.putPredicateInitWorker(GreyPredicate.class, GreyPredicate::new);
        predicateFactory.putPredicateInitWorker(OfflinePredicate.class, OfflinePredicate::new);
        log.debug("Load Predicate Factory Success");
        return predicateFactory;
    }

    @Bean
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    @ConditionalOnProperty(prefix = GreyProperties.GREY_PROPERTIES_PREFIX,value = "grey-ribbon-rule",havingValue = "DEFAULT",matchIfMissing = true)
    public IRule ribbonRule(PredicateFactory predicateFactory,GreyProperties greyProperties) {
        GreyDefaultRoundRule greyDefaultRoundRule ;
        if(greyProperties.getEnable()){
            greyDefaultRoundRule = new GreyDefaultRoundRule(predicateFactory.getAllPredicate(GreyPredicate.class,OfflinePredicate.class));
        }else{
            greyDefaultRoundRule = new GreyDefaultRoundRule(predicateFactory.getAllPredicate(OfflinePredicate.class));
        }
        log.debug("Load Grey Default Round Rule Config Bean");
        return greyDefaultRoundRule;
    }

}
