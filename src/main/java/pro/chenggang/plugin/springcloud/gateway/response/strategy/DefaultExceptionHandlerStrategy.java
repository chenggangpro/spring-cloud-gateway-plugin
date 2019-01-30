package pro.chenggang.plugin.springcloud.gateway.response.strategy;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import pro.chenggang.plugin.springcloud.gateway.option.ResponseResult;
import pro.chenggang.plugin.springcloud.gateway.option.SystemResponseInfo;
import pro.chenggang.plugin.springcloud.gateway.response.ExceptionHandlerResult;

/**
 * Default ExceptionHandlerStrategy
 * @author chenggang
 * @date 2019/01/29
 */
@Slf4j
public class DefaultExceptionHandlerStrategy implements ExceptionHandlerStrategy {

    @Override
    public ExceptionHandlerResult handleException(Throwable throwable) {
        ResponseResult<String> responseResult = new ResponseResult<>(SystemResponseInfo.GATEWAY_ERROR,throwable.getMessage());
        ExceptionHandlerResult result = new ExceptionHandlerResult(HttpStatus.INTERNAL_SERVER_ERROR, JSON.toJSONString(responseResult));
        log.debug("[DefaultExceptionHandlerStrategy]Handle Exception:{},Result:{}",throwable.getMessage(),result);
        log.error("[DefaultExceptionHandlerStrategy]Log Exception In Error Level,Exception Message:{}",throwable.getMessage());
        return result;
    }
}
