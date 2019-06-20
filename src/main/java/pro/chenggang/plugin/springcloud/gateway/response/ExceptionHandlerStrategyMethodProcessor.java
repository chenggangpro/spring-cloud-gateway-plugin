package pro.chenggang.plugin.springcloud.gateway.response;

import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.framework.autoproxy.AutoProxyUtils;
import org.springframework.aop.scope.ScopedObject;
import org.springframework.aop.scope.ScopedProxyUtils;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanInitializationException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.MethodIntrospector;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.server.ServerWebExchange;
import pro.chenggang.plugin.springcloud.gateway.response.strategy.ExceptionHandlerStrategyAdapter;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * ExceptionHandlerStrategyMethodProcessor
 * @author chenggang
 * @date 2019/06/20
 */
@Slf4j
@Configuration
public class ExceptionHandlerStrategyMethodProcessor implements ApplicationContextAware, InitializingBean {

    private ConfigurableApplicationContext applicationContext;

    private final Set<Class<?>> nonAnnotatedClasses = Collections.newSetFromMap(new ConcurrentHashMap<>(64));

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        Assert.isTrue(applicationContext instanceof ConfigurableApplicationContext,
                "ApplicationContext does not implement ConfigurableApplicationContext");
        this.applicationContext = (ConfigurableApplicationContext) applicationContext;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        DefaultListableBeanFactory beanFactory = (DefaultListableBeanFactory) this.applicationContext.getBeanFactory();
        String[] beanNames = beanFactory.getBeanNamesForType(Object.class);
        for (String beanName : beanNames) {
            if (!ScopedProxyUtils.isScopedTarget(beanName)) {
                Class<?> type = null;
                try {
                    type = AutoProxyUtils.determineTargetClass(beanFactory, beanName);
                }
                catch (Throwable ex) {
                    if (log.isDebugEnabled()) {
                        log.debug("Could not resolve target class for bean with name '" + beanName + "'", ex);
                    }
                }
                if (type != null) {
                    if (ScopedObject.class.isAssignableFrom(type)) {
                        try {
                            Class<?> targetClass = AutoProxyUtils.determineTargetClass(
                                    beanFactory, ScopedProxyUtils.getTargetBeanName(beanName));
                            if (targetClass != null) {
                                type = targetClass;
                            }
                        } catch (Throwable ex) {
                            if (log.isDebugEnabled()) {
                                log.debug("Could not resolve target bean for scoped proxy '" + beanName + "'", ex);
                            }
                        }
                    }
                    try {
                        processBean(beanName, type);
                    }
                    catch (Throwable ex) {
                        throw new BeanInitializationException("Failed to process @MessageListener " +
                                "annotation on bean with name '" + beanName + "'", ex);
                    }
                }
            }
        }
    }

    private void processBean(final String beanName, final Class<?> targetType) {
        if (!this.nonAnnotatedClasses.contains(targetType) && !isSpringContainerClass(targetType)) {
            Map<Method, ExceptionHandler> annotatedMethods = null;
            try {
                annotatedMethods = MethodIntrospector.selectMethods(targetType,
                        (MethodIntrospector.MetadataLookup<ExceptionHandler>) method ->
                                AnnotatedElementUtils.findMergedAnnotation(method, ExceptionHandler.class));
            } catch (Throwable ex) {
                // An unresolvable type in a method signature, probably from a lazy bean - let's ignore it.
                if (log.isDebugEnabled()) {
                    log.debug("Could not resolve methods for bean with name '" + beanName + "'", ex);
                }
            }
            if (CollectionUtils.isEmpty(annotatedMethods)) {
                this.nonAnnotatedClasses.add(targetType);
                if (log.isTraceEnabled()) {
                    log.trace("No @ExceptionHandler annotations found on bean class: " + targetType.getName());
                }
            } else {
                DefaultListableBeanFactory beanFactory = (DefaultListableBeanFactory) this.applicationContext.getBeanFactory();
                BeanDefinitionBuilder beanDefinitionBuilder;
                String adaptClassSimpleName;
                for (Method method : annotatedMethods.keySet()) {
                    Method methodToUse = AopUtils.selectInvocableMethod(method, this.applicationContext.getType(beanName));
                    Class<?>[] parameterTypes = methodToUse.getParameterTypes();
                    if(parameterTypes.length!=2){
                        throw new IllegalArgumentException("Exception Handler Must Have Two Parameters With ServerWebExchange And Throwable");
                    }
                    for(Class clazz : parameterTypes){
                        if(!ClassUtils.isAssignable(ServerWebExchange.class,clazz) && !ClassUtils.isAssignable(Throwable.class,clazz)){
                            throw new IllegalArgumentException("Exception Handler Parameters Must Be ServerWebExchange And Throwable");
                        }
                    }
                    ExceptionHandler exceptionHandlerAnnotation = annotatedMethods.get(method);
                    Class<? extends Throwable>[] value = exceptionHandlerAnnotation.value();
                    for(Class<? extends Throwable> adaptClass : value){
                        beanDefinitionBuilder = BeanDefinitionBuilder.rootBeanDefinition(ExceptionHandlerStrategyAdapter.class);
                        beanDefinitionBuilder.addConstructorArgValue(beanName);
                        beanDefinitionBuilder.addConstructorArgValue(targetType);
                        beanDefinitionBuilder.addConstructorArgValue(methodToUse);
                        beanDefinitionBuilder.addConstructorArgValue(adaptClass);
                        beanDefinitionBuilder.setScope(BeanDefinition.SCOPE_SINGLETON);
                        adaptClassSimpleName = adaptClass.getSimpleName();
                        beanFactory.registerBeanDefinition(adaptClassSimpleName +"HandlerStrategy",beanDefinitionBuilder.getBeanDefinition());
                        if (log.isDebugEnabled()) {
                            log.debug("Register ExceptionHandlerStrategy Bean :{} For Class :{} ", adaptClassSimpleName +"HandlerStrategy", adaptClassSimpleName);
                        }
                    }
                }
                if (log.isDebugEnabled()) {
                    log.debug(annotatedMethods.size() + " @ExceptionHandler methods processed on bean '" +
                            beanName + "': " + annotatedMethods);
                }
            }
        }
    }

    private static boolean isSpringContainerClass(Class<?> clazz) {
        return (clazz.getName().startsWith("org.springframework.") &&
                !AnnotatedElementUtils.isAnnotated(ClassUtils.getUserClass(clazz), Component.class));
    }
}
