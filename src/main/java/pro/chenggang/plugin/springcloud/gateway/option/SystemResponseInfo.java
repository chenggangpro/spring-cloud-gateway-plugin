package pro.chenggang.plugin.springcloud.gateway.option;

/**
 * System Response Info
 * @author chenggang
 * @date 2019/01/29
 */
public enum SystemResponseInfo implements ResponseInfo {

    /**
     * Service Error
     */
    SERVICE_NOT_AVAILABLE("404","service-error"),
    /**
     * Gateway Internal Error
     */
    GATEWAY_ERROR("500","gateway-error"),
    ;

    private String code;
    private String msg;

    SystemResponseInfo(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }


    @Override
    public String getCode() {
        return this.code;
    }

    @Override
    public String getMsg() {
        return this.msg;
    }
}
