package pro.chenggang.plugin.springcloud.gateway.response.strategy;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.server.ResponseStatusException;
import pro.chenggang.plugin.springcloud.gateway.option.ResponseResult;
import pro.chenggang.plugin.springcloud.gateway.option.SystemResponseInfo;
import pro.chenggang.plugin.springcloud.gateway.response.ExceptionHandlerResult;

/**
 * Response Status Exception Handler Strategy
 * @author chenggang
 * @date 2019/01/29
 */
@Slf4j
public class ResponseStatusExceptionHandlerStrategy implements ExceptionHandlerStrategy{

    @Override
    public Class getHandleClass() {
        return ResponseStatusException.class;
    }

    @Override
    public ExceptionHandlerResult handleException(Throwable throwable) {
        ResponseStatusException responseStatusException = (ResponseStatusException) throwable;
        ResponseResult<String> responseResult = new ResponseResult<>(SystemResponseInfo.SERVICE_NOT_AVAILABLE,throwable.getMessage());
        String response = JSON.toJSONString(responseResult);
        ExceptionHandlerResult result = new ExceptionHandlerResult(responseStatusException.getStatus(),response);
        log.debug("[ResponseStatusExceptionHandlerStrategy]Handle Exception:{},Result:{}",throwable.getMessage(),result);
        return result;
    }
}
