package pro.chenggang.plugin.springcloud.gateway.option;

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

    ;

    private int order;

    FilterOrderEnum(int order) {
        this.order = order;
    }

    public int getOrder() {
        return order;
    }
}
