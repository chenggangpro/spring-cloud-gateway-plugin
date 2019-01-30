package pro.chenggang.plugin.springcloud.gateway.context;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Grey Context For Grey Route
 * @author chenggang
 * @date 2019/01/29
 */
@Setter
@Getter
@ToString
public class GreyContext {

    public static final String CACHE_GREY_CONTEXT = "cacheGreyContext";
    /**
     * ServiceId
     */
    private String serviceId;
    /**
     * Version
     */
    private String version;
    /**
     * Matched The Grey Rule
     */
    private boolean matched;
}
