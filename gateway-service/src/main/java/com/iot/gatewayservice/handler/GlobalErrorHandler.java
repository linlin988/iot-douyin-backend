package com.iot.gatewayservice.handler;

import org.springframework.boot.autoconfigure.web.WebProperties;
import org.springframework.boot.autoconfigure.web.reactive.error.AbstractErrorWebExceptionHandler;
import org.springframework.boot.web.reactive.error.ErrorAttributes;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.*;
import reactor.core.publisher.Mono;

import java.util.Map;

@Component
@Order(-2)  // 优先级比默认错误处理高
public class GlobalErrorHandler extends AbstractErrorWebExceptionHandler {

    // 构造函数注入必要组件
    public GlobalErrorHandler(
            ErrorAttributes errorAttributes,
            WebProperties webProperties,          // 包含静态资源配置
            ApplicationContext context,
            ServerCodecConfigurer codecConfigurer // 序列化/反序列化工具
    ) {
        // 父类需要 Resources 对象，所以用 getResources()
        super(errorAttributes, webProperties.getResources(), context);

        // 避免 json 序列化会失败
        super.setMessageWriters(codecConfigurer.getWriters());
        super.setMessageReaders(codecConfigurer.getReaders());
    }

    // 定义错误路由：所有请求都走这里
    @Override
    protected RouterFunction<ServerResponse> getRoutingFunction(ErrorAttributes errorAttributes) {
        return RouterFunctions.route(RequestPredicates.all(), this::renderErrorResponse);
    }

    // 实际渲染错误响应的方法
    private Mono<ServerResponse> renderErrorResponse(ServerRequest request) {
        Throwable error = getError(request);  // 获取原始异常

        // 目前全部返回 500，后续可根据异常类型区分
        return ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(Map.of(
                        "code", 500,
                        "message", error.getMessage() != null ? error.getMessage() : "未知错误"
                ));
    }
}