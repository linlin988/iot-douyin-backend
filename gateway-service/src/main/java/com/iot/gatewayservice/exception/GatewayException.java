package com.iot.gatewayservice.exception;

import com.iot.commonModules.enums.ExceptionEnum;
import com.iot.commonModules.exception.AllException;
import com.iot.gatewayservice.enums.GatewayExceptionEnum;

public class GatewayException extends AllException {

    // 用网关专属枚举构造
    public GatewayException(GatewayExceptionEnum gatewayExceptionEnum) {
        super(gatewayExceptionEnum.getDesc(), gatewayExceptionEnum.getCode());
        // 把枚举的描述当 message，code 当 status
    }

    // 允许自定义提示语，但状态码仍用枚举的
    public GatewayException(GatewayExceptionEnum gatewayExceptionEnum, String customMessage) {
        super(customMessage, gatewayExceptionEnum.getCode());
        // 场景：想显示更具体的错误原因
    }

    // 支持通用异常枚举（来自 common 模块）
    public GatewayException(ExceptionEnum exceptionEnum) {
        super(exceptionEnum);
        // 直接复用父类的构造
    }

    // 最基础构造，自由指定消息和状态码
    public GatewayException(String message, Integer status) {
        super(message, status);
    }
}