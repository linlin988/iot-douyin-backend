package com.iot.gatewayservice.enums;

import lombok.Getter;

@Getter
public enum GatewayExceptionEnum {

    // 成功响应
    SUCCESS(200, "操作成功"),

    // 通用失败
    FAIL(500, "操作失败"),

    // 未登录 / token 失效
    UNAUTHORIZED(401, "请先登录"),

    // 登录了但没权限
    FORBIDDEN(403, "无权限访问");

    private final int code;   // http 状态码或业务码
    private final String desc; // 给前端看的描述

    // 构造方法
    GatewayExceptionEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    // 根据 code 反查枚举（常用在解析响应时）
    public static GatewayExceptionEnum getByCode(int codeVal) {
        for (GatewayExceptionEnum e : values()) {
            if (e.getCode() == codeVal) {
                return e;
            }
        }
        return null;  // 没找到返回 null
    }
}