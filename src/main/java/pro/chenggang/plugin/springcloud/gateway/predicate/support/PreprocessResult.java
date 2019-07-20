package pro.chenggang.plugin.springcloud.gateway.predicate.support;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Pre process ServerWebExchange Result
 * @author chenggang
 * @date 2019/07/17
 */
@Getter
@Setter
@ToString
@AllArgsConstructor
@Builder
public class PreprocessResult<T> {

    private Boolean result = false;
    private T resultData;

}
