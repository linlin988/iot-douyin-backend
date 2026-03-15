package com.iot.commonModules.enums;

import lombok.Getter;

@Getter
public enum ExceptionEnum {

    // 400 客户端请求错误
    INVALID_FILE_TYPE(400, "无效的文件类型！"),
    INVALID_PARAM_ERROR(400, "无效的请求参数！"),
    INVALID_PHONE_NUMBER(400, "无效的手机号码"),
    INVALID_VERIFY_CODE(400, "验证码错误！"),
    INVALID_USERNAME_PASSWORD(400, "无效的用户名和密码！"),

    // 401 未授权/登录失效
    UNAUTHORIZED(401, "登录失效或未登录！");

    private final int status;     // http 状态码
    private final String message; // 给前端的提示

    // 枚举构造方法必须私有
    ExceptionEnum(int status, String message) {
        this.status = status;
        this.message = message;
    }
}