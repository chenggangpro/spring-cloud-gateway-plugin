package pro.chenggang.plugin.springcloud.gateway.option;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * ResponseResult
 * @author chenggang
 * @date 2019/01/29
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
public class ResponseResult<T> {

    @JSONField(ordinal = 1)
    private String code;
    @JSONField(ordinal = 2)
    private String msg;
    @JSONField(ordinal = 3)
    private T data;

    public ResponseResult(ResponseInfo responseInfo) {
        this.code = responseInfo.getCode();
        this.msg = responseInfo.getMsg();
    }

    public ResponseResult(ResponseInfo responseInfo,T data) {
        this.code = responseInfo.getCode();
        this.msg = responseInfo.getMsg();
        this.data = data;
    }

}
