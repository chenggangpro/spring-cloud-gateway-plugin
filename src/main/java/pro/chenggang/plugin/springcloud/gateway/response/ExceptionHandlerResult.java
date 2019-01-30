package pro.chenggang.plugin.springcloud.gateway.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.http.HttpStatus;

/**
 * Exception Handler Result
 * @author chenggang
 * @date 2019/01/29
 */
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class ExceptionHandlerResult {

    private HttpStatus httpStatus;

    private String responseResult;

}
