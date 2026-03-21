package com.iot.UserService.Config;
import com.iot.commonModules.common.Result;
import com.iot.commonModules.common.AllException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 全局异常处理器（完全适配自定义Result类）
 * 处理所有@RestController的异常，返回标准化Result格式
 */
@Slf4j // 替换e.printStackTrace()，用日志记录异常（生产级规范）
@RestControllerAdvice
public class GlobalExceptionHandler {

    // 1. 处理自定义业务异常（你之前定义的AllException，优先处理）
    @ExceptionHandler(AllException.class)
    public Result handleAllException(AllException e) {
        // 调用Result.error(状态码, 错误消息)，匹配自定义异常的状态码和消息
        return Result.error(e.getStatus(), e.getMessage());
    }

    // 2. 处理业务运行时异常（如手动抛的RuntimeException）
    @ExceptionHandler(RuntimeException.class)
    public Result handleRuntimeException(RuntimeException e) {
        // 运行时异常默认返回500，消息用异常自带的message
        return Result.error(500, e.getMessage());
    }

    // 3. 处理参数校验异常（@Valid注解触发）
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result handleValidException(MethodArgumentNotValidException e) {
        BindingResult bindingResult = e.getBindingResult();
        // 优化：返回结构化的字段错误信息（前端更易解析），而非拼接字符串
        Map<String, String> errorMap = new HashMap<>();
        for (FieldError error : bindingResult.getFieldErrors()) {
            errorMap.put(error.getField(), error.getDefaultMessage());
        }
        // 参数校验失败默认返回400，data字段携带具体的字段错误信息
        return new Result(400, "参数校验失败", errorMap, null);
    }

    // 4. 处理所有其他未兜底的异常（最终兜底）
    @ExceptionHandler(Exception.class)
    public Result handleException(Exception e) {
        // 开发/生产都记录日志（堆栈信息），方便排查问题
        log.error("服务器内部异常", e);
        // 前端只返回通用提示，不暴露具体异常细节
        return Result.error(500, "服务器内部错误");
    }
}