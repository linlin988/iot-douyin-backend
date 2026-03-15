package com.iot.commonModules.exception;

import com.iot.commonModules.enums.ExceptionEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 项目所有自定义异常的统一父类
 */
@EqualsAndHashCode(callSuper = true)  // 比较时包含父类字段
@Data
public class AllException extends RuntimeException {

    private Integer status;  // 用于存 http 状态码或业务码

    // 直接传消息 + 状态码构造
    public AllException(String message, Integer status) {
        super(message);      // 给 RuntimeException 设置错误信息
        this.status = status;
    }

    // 最常用：直接用枚举构造
    public AllException(ExceptionEnum exceptionEnum) {
        super(exceptionEnum.getMessage());  // 用枚举的提示语
        this.status = exceptionEnum.getStatus();  // 用枚举的 http 状态码
    }

    // 如果以后需要更多构造方法，可以继续加
}