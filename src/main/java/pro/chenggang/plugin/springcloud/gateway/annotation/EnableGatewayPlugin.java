package pro.chenggang.plugin.springcloud.gateway.annotation;

import org.springframework.context.annotation.Import;
import pro.chenggang.plugin.springcloud.gateway.config.GatewayPluginConfigurationSelector;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Enable Gateway Plugin
 * @author chenggang
 * @date 2019/01/29
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
@Import(GatewayPluginConfigurationSelector.class)
public @interface EnableGatewayPlugin {

}
