package com.iot.gatewayservice.exception;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.iot.commonModules.common.Result;
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * 网关全局异常处理
 */
@Component
public class GatewayExceptionHandler implements ErrorWebExceptionHandler {

    private ObjectMapper objectMapper = new ObjectMapper();


    @Override
    public Mono<Void> handle(ServerWebExchange serverWebExchange, Throwable throwable) {
        ServerHttpRequest request = serverWebExchange.getRequest();//  请求
        ServerHttpResponse response = serverWebExchange.getResponse();//  响应
        Integer code = 200;
        String message = "";
        if (throwable instanceof RuntimeException) {
            // 登录/验证码/权限 这类业务异常
            code = 401;
            message = throwable.getMessage();
        }else{// 如果是系统抛出的异常
            code = 500;
            message = "系统异常！";
        }
        Result result = Result.error(code, message);
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
        return response.writeWith(Mono.fromSupplier(() -> {
            DataBufferFactory dataBufferFactory = response.bufferFactory();
            byte[] bytes = null;
            try {
                bytes = objectMapper.writeValueAsBytes(result);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
            return dataBufferFactory.wrap(bytes);
        }));
    }

}
