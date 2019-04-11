package pro.chenggang.plugin.springcloud.gateway.context;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.http.HttpHeaders;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

/**
 * Gateway Context Use Cache Request Content
 * @author chenggang
 * @date 2019/01/29
 */
@Getter
@Setter
@ToString
public class GatewayContext {

    public static final String CACHE_GATEWAY_CONTEXT = "cacheGatewayContext";

    /**
     * cache json body
     */
    private String requestBody;
    /**
     * cache Response Body
     */
    private Object responseBody;
    /**
     * request headers
     */
    private HttpHeaders requestHeaders;
    /**
     * cache form data
     */
    private MultiValueMap<String, String> formData;
    /**
     * cache all request data include:form data and query param
     */
    private MultiValueMap<String, String> allRequestData = new LinkedMultiValueMap<>(0);

}