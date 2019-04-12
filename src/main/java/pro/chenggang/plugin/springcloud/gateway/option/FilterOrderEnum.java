package pro.chenggang.plugin.springcloud.gateway.option;

import org.springframework.cloud.gateway.filter.NettyWriteResponseFilter;

/**
 * The Order Of Plugin Filter
 * @author chenggang
 * @date 2019/01/29
 */
public enum FilterOrderEnum {

    /**
     * Gateway Context Filter
     */
    GATEWAY_CONTEXT_FILTER(Integer.MIN_VALUE),
    /**
     * Grey Context Filter
     */
    GREY_CONTEXT_FILTER(Integer.MIN_VALUE+1),
    /**
     * Request Log Filter
     */
    REQUEST_LOG_FILTER(Integer.MIN_VALUE+2),
    /**
     * Cache Response Data Filter
     */
    RESPONSE_DATA_FILTER(NettyWriteResponseFilter.WRITE_RESPONSE_FILTER_ORDER - 1),

    ;

    private int order;

    FilterOrderEnum(int order) {
        this.order = order;
    }

    public int getOrder() {
        return order;
    }
}
