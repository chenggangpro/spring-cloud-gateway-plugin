package pro.chenggang.plugin.springcloud.gateway.predicate;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.annotation.Configuration;

/**
 * DynamicRoute BeanFactoryPostProcessor
 * @author chenggang
 * @date 2019/07/18
 */
@Configuration
@Slf4j
public class DynamicRouteSupportBeanFactoryPostProcessor implements BeanFactoryPostProcessor {

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory configurableListableBeanFactory) throws BeansException {
        if(configurableListableBeanFactory.containsBeanDefinition("routePredicateHandlerMapping")){
            DefaultListableBeanFactory beanFactory = (DefaultListableBeanFactory) configurableListableBeanFactory;
            beanFactory.removeBeanDefinition("routePredicateHandlerMapping");
            log.debug("Remove Bean Definition Bean Name : routePredicateHandlerMapping ");
        }
    }
}
