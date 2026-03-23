package com.iot.content.config;


import com.iot.commonModules.common.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    // 全局捕获所有异常
    @ExceptionHandler(Exception.class)
    public Result handleException(Exception e) {
        log.error("全局异常：{}", e.getMessage());
        return Result.error(500, "服务器异常："+ e.getMessage());
    }

    // 自定义异常
    @ExceptionHandler(RuntimeException.class)
    public Result runtimeException(RuntimeException e) {
        log.error("业务异常：{}", e);
        return Result.error(500,"服务器异常"+ e.getMessage());
    }
}