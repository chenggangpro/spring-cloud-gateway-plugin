package pro.chenggang.plugin.springcloud.gateway.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.ImportSelector;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.type.AnnotationMetadata;
import pro.chenggang.plugin.springcloud.gateway.annotation.EnableGatewayPlugin;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Gateway Plugin Configuration Seelector
 * @author chenggang
 * @date 2019/01/29
 */
@Slf4j
public class GatewayPluginConfigurationSelector implements ImportSelector {

    @Override
    public String[] selectImports(AnnotationMetadata importingClassMetadata) {
        Map<String, Object> annotationAttributes = importingClassMetadata.getAnnotationAttributes(EnableGatewayPlugin.class.getName());
        AnnotationAttributes attributes = AnnotationAttributes.fromMap(annotationAttributes);
        boolean enableRequestLog = attributes.getBoolean(EnableGatewayPlugin.ENABLE_REQUEST_LOG_ATTRIBUTE_NAME);
        boolean enableGreyRoute = attributes.getBoolean(EnableGatewayPlugin.ENABLE_GREY_ROUTE_ATTRIBUTE_NAME);
        boolean enableGlobalExceptionJsonHandler = attributes.getBoolean(EnableGatewayPlugin.ENABLE_GLOBAL_EXCEPTION_JSON_HANDLER_ATTRIBUTE_NAME);
        EnableGatewayPlugin.GreyRibbonRule greyRibbonRule = attributes.getEnum(EnableGatewayPlugin.GREY_RIBBON_RULE_ATTRIBUTE_NAME);
        List<Class> configClassList = new ArrayList<>(5);
        configClassList.add(GatewayPluginConfig.class);
        if(enableRequestLog){
            configClassList.add(RequestLogConfig.class);
        }
        if(enableGreyRoute){
            configClassList.add(GreyRouteConfig.class);
        }
        switch (greyRibbonRule){
            case DEFAULT:
                configClassList.add(GreyDefaultRuleConfig.class);
                break;
            case WeightResponse:
                configClassList.add(GreyWeightResponseRuleConfig.class);
                break;
            default:
                break;
        }
        if(enableGlobalExceptionJsonHandler){
            configClassList.add(GlobalExceptionJsonHandlerConfig.class);
        }
        return configClassList
                .stream()
                .map(Class::getName).toArray(String[]::new);
    }
}
