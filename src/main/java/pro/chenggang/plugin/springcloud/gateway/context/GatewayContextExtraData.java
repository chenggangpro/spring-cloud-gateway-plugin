package pro.chenggang.plugin.springcloud.gateway.context;

/**
 * @author: chenggang
 * @date 2019-11-04.
 */
public interface GatewayContextExtraData<T> {

    /**
     * get context extra data
     * @return
     */
    T getData();
}
