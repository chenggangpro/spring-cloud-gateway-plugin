package pro.chenggang.plugin.springcloud.gateway.option;

/**
 * Grey Context For Grey Route
 * @author chenggang
 * @date 2019/01/29
 */
public interface ResponseInfo {

    /**
     * get code
     * @return
     */
    String getCode();

    /**
     * get msg
     * @return
     */
    String getMsg();
}
