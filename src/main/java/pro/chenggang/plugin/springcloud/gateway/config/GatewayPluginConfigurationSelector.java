package pro.chenggang.plugin.springcloud.gateway.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.ImportSelector;
import org.springframework.core.type.AnnotationMetadata;

import java.util.ArrayList;
import java.util.List;

/**
 * Gateway Plugin Configuration Seelector
 * @author chenggang
 * @date 2019/01/29
 */
@Slf4j
public class GatewayPluginConfigurationSelector implements ImportSelector {

    @Override
    public String[] selectImports(AnnotationMetadata importingClassMetadata) {
        List<Class> configClassList = new ArrayList<>(6);
        configClassList.add(GatewayPluginConfig.class);
        configClassList.add(RequestResponseLogConfig.class);
        configClassList.add(GreyRouteConfig.class);
        configClassList.add(GreyDefaultRuleConfig.class);
        configClassList.add(GreyWeightResponseRuleConfig.class);
        configClassList.add(GlobalExceptionJsonHandlerConfig.class);
        return configClassList
                .stream()
                .map(Class::getName).toArray(String[]::new);
    }
}
