package pro.chenggang.plugin.springcloud.gateway.properties;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Gateway Plugin Properties
 * @author chenggang
 * @date 2019/04/12
 */
@Slf4j
@Getter
@Setter
@ToString
public class GatewayPluginProperties implements InitializingBean {

    public static final String GATEWAY_PLUGIN_PROPERTIES_PREFIX = "spring.cloud.gateway.plugin.config";
    /**
     * Enable Or Disable Read Request Data 。 If true, all request body will cached
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
    /**
     * Enable Read Request Data When use discover route by serviceId
     */
    private List<String> readRequestDataServiceIdList = Collections.emptyList();
    /**
     * Enable Read Request Data by specific path
     */
    private List<String> readRequestDataPathList = Collections.emptyList();

    @Override
    public void afterPropertiesSet() throws Exception {
        if(!CollectionUtils.isEmpty(readRequestDataServiceIdList)){
            readRequestDataServiceIdList = readRequestDataServiceIdList.stream().map(String::toLowerCase).collect(Collectors.toList());
        }
        if(!CollectionUtils.isEmpty(readRequestDataPathList)){
            readRequestDataPathList = readRequestDataPathList.stream().map(String::toLowerCase).collect(Collectors.toList());
        }
    }
}
