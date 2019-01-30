package pro.chenggang.plugin.springcloud.gateway.response.strategy;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import pro.chenggang.plugin.springcloud.gateway.option.ResponseResult;
import pro.chenggang.plugin.springcloud.gateway.option.SystemResponseInfo;
import pro.chenggang.plugin.springcloud.gateway.response.ExceptionHandlerResult;

/**
 * Not Found Exception Handler Strategy
 * @author chenggang
 * @date 2019/01/29
 */
@Slf4j
public class NotFoundExceptionHandlerStrategy implements ExceptionHandlerStrategy{

    @Override
    public ExceptionHandlerResult handleException(Throwable throwable) {
        ResponseResult<String> responseResult = new ResponseResult<>(SystemResponseInfo.SERVICE_NOT_AVAILABLE,throwable.getMessage());
        String response = JSON.toJSONString(responseResult);
        ExceptionHandlerResult result = new ExceptionHandlerResult(HttpStatus.NOT_FOUND,response);
        log.debug("[NotFoundExceptionHandlerStrategy]Handle Exception:{},Result:{}",throwable.getMessage(),result);
        return result;
    }
}
