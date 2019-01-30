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

    /**
     * Request Log Attribute Name
     */
    String ENABLE_REQUEST_LOG_ATTRIBUTE_NAME = "enableRequestLog";
    /**
     * Grey Route Attribute Name
     */
    String ENABLE_GREY_ROUTE_ATTRIBUTE_NAME = "enableGreyRoute";

    /**
     * Grey Ribbon Rule Attribute Name
     */
    String GREY_RIBBON_RULE_ATTRIBUTE_NAME = "greyRibbonRule";

    /**
     * Global Exception Json Handler Attribute Name
     */
    String ENABLE_GLOBAL_EXCEPTION_JSON_HANDLER_ATTRIBUTE_NAME = "enableGlobalExceptionJsonHandler";

    /**
     * enable request log plugin
     * default true
     * @return
     */
    boolean enableRequestLog() default true;

    /**
     * enable grey route plugin
     * default false
     * @return
     */
    boolean enableGreyRoute() default false;

    /**
     * grey ribbon Rule
     * @return
     */
    GreyRibbonRule greyRibbonRule() default GreyRibbonRule.DEFAULT;

    /**
     * enable json  global exception handler
     * default true
     * @return
     */
    boolean enableGlobalExceptionJsonHandler() default true;

    /**
     * RreyRule
     */
    enum GreyRibbonRule{
        /**
         * default grey rule based on  round rule
         */
        DEFAULT,
        /**
         * weight response rule base on WeightResponseRUle
         */
        WeightResponse,
    }
}
