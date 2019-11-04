package pro.chenggang.plugin.springcloud.gateway.response.strategy;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ServerWebExchange;
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
    public Class getHandleClass() {
        return Throwable.class;
    }

    @Override
    public ExceptionHandlerResult handleException(ServerWebExchange exchange, Throwable throwable) {
        ResponseResult<String> responseResult = new ResponseResult<>(SystemResponseInfo.GATEWAY_ERROR,throwable.getMessage());
        ExceptionHandlerResult result = new ExceptionHandlerResult(HttpStatus.INTERNAL_SERVER_ERROR, JSON.toJSONString(responseResult));
        if(log.isDebugEnabled()){
            log.debug("[DefaultExceptionHandlerStrategy]Handle Exception:{}", ExceptionUtils.getStackTrace(throwable));
        }
        log.error("[DefaultExceptionHandlerStrategy]Log Exception In Error Level,Exception Message:{}",ExceptionUtils.getRootCause(throwable));
        return result;
    }
}
