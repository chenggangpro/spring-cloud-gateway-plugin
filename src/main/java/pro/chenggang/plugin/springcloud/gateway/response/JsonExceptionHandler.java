package pro.chenggang.plugin.springcloud.gateway.response;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
import org.springframework.http.MediaType;
import org.springframework.http.codec.HttpMessageReader;
import org.springframework.http.codec.HttpMessageWriter;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.util.Assert;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.reactive.result.view.ViewResolver;
import org.springframework.web.server.ServerWebExchange;
import pro.chenggang.plugin.springcloud.gateway.response.factory.ExceptionHandlerStrategyFactory;
import pro.chenggang.plugin.springcloud.gateway.response.strategy.DefaultExceptionHandlerStrategy;
import pro.chenggang.plugin.springcloud.gateway.response.strategy.ExceptionHandlerStrategy;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.List;

/**
 * Json Exception Handler{@link org.springframework.boot.autoconfigure.web.reactive.error.AbstractErrorWebExceptionHandler}
 * @author chenggang
 * @date 2019/01/29
 */
@Slf4j
public class JsonExceptionHandler implements ErrorWebExceptionHandler {

    /**
     * MessageReader
     */
    private List<HttpMessageReader<?>> messageReaders = Collections.emptyList();

    /**
     * MessageWriter
     */
    private List<HttpMessageWriter<?>> messageWriters = Collections.emptyList();

    /**
     * ViewResolvers
     */
    private List<ViewResolver> viewResolvers = Collections.emptyList();

    /**
     * temporary cache exception handler result for another method
     */
    private ThreadLocal<ExceptionHandlerResult> exceptionHandlerResult = new ThreadLocal<>();

    /**
     * Strategy Factory
     */
    private ExceptionHandlerStrategyFactory exceptionHandlerStrategyFactory;

    public JsonExceptionHandler(ExceptionHandlerStrategyFactory exceptionHandlerStrategyFactory) {
        Assert.notNull(exceptionHandlerStrategyFactory, "'ExceptionHandlerStrategyFactory' must not be null");
        this.exceptionHandlerStrategyFactory = exceptionHandlerStrategyFactory;
    }

    public void setMessageReaders(List<HttpMessageReader<?>> messageReaders) {
        Assert.notNull(messageReaders, "'messageReaders' must not be null");
        this.messageReaders = messageReaders;
    }

    public void setViewResolvers(List<ViewResolver> viewResolvers) {
        this.viewResolvers = viewResolvers;
    }

    public void setMessageWriters(List<HttpMessageWriter<?>> messageWriters) {
        Assert.notNull(messageWriters, "'messageWriters' must not be null");
        this.messageWriters = messageWriters;
    }

    @Override
    public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {
        ServerHttpRequest request = exchange.getRequest();
        /*
         * get strategy
         */
        ExceptionHandlerStrategy strategy = exceptionHandlerStrategyFactory.getStrategy(ex.getClass());
        ExceptionHandlerResult result = strategy.handleException(ex);
        /*
         * log error
         */
        log.warn("[Global Exception Handler]Request Path:{},Exception Msg:{}",request.getPath(),ex.getMessage());
        /*
         * if is Default Strategy Print Stack Trace
         */
        if(strategy instanceof DefaultExceptionHandlerStrategy && log.isDebugEnabled()){
            log.debug("[Global Exception Handler]Request Path:{},Exception Detail:{}",request.getPath(), ExceptionUtils.getStackTrace(ex));
        }
        exceptionHandlerResult.set(result);
        ServerRequest newRequest = ServerRequest.create(exchange, this.messageReaders);
        return RouterFunctions.route(RequestPredicates.all(), this::renderErrorResponse).route(newRequest)
                .switchIfEmpty(Mono.error(ex))
                .flatMap((handler) -> handler.handle(newRequest))
                .flatMap((response) -> write(exchange, response));

    }

    private Mono<ServerResponse> renderErrorResponse(ServerRequest request) {
        ExceptionHandlerResult result = exceptionHandlerResult.get();
        return ServerResponse.status(result.getHttpStatus())
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .body(BodyInserters.fromObject(result.getResponseResult()));
    }

    private Mono<? extends Void> write(ServerWebExchange exchange,
                                       ServerResponse response) {
        exchange.getResponse().getHeaders()
                .setContentType(response.headers().getContentType());
        return response.writeTo(exchange, new ResponseContext());
    }

    private class ResponseContext implements ServerResponse.Context {

        @Override
        public List<HttpMessageWriter<?>> messageWriters() {
            return JsonExceptionHandler.this.messageWriters;
        }

        @Override
        public List<ViewResolver> viewResolvers() {
            return JsonExceptionHandler.this.viewResolvers;
        }

    }
}
