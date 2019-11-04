package pro.chenggang.plugin.springcloud.gateway.context;

import org.springframework.web.server.ServerWebExchange;

/**
 * @author: chenggang
 * @date 2019-11-04.
 */
public interface ContextExtraDataGenerator<T> {

    /**
     * generate context extra data
     * @param serverWebExchange
     * @return
     */
    GatewayContextExtraData<T> generateContextExtraData(ServerWebExchange serverWebExchange);

}
