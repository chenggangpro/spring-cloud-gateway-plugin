package pro.chenggang.plugin.springcloud.gateway.properties;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Gateway Plugin Properties
 * @author chenggang
 * @date 2019/04/12
 */
@Slf4j
@Getter
@Setter
@ToString
@ConfigurationProperties(GatewayPluginProperties.GATEWAY_PLUGIN_PROPERTIES_PREFIX)
public class GatewayPluginProperties {

    public static final String GATEWAY_PLUGIN_PROPERTIES_PREFIX = "spring.cloud.gateway.plugin.config";
    /**
     * Enable Or Disable Read Request Data
     */
    private Boolean readRequestData = false;
    /**
     * Enable Or Disable Read Response Data
     */
    private Boolean readResponseData = false;
    /**
     * Enable Or Disable Log Request Detail
     */
    private Boolean logRequest = false;
    /**
     * Enable Or Disable Global Exception Json Handler
     */
    private Boolean exceptionJsonHandler = false;
}
