package com.iot.gatewayservice.enums;


import lombok.Getter;

@Getter
public enum ResultCodeEnum {
    SUCCESS(200, "操作成功"),
    FAIL(500, "操作失败"),
    UNAUTHORIZED(401, "请先登录"),    // 你需要的
    FORBIDDEN(403, "无权限访问");     // 你需要的

    private int code;
    private String desc;

    ResultCodeEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static ResultCodeEnum getByCode(int codeVal) {
        for (ResultCodeEnum resultCodeEnum : ResultCodeEnum.values()) {
            if (resultCodeEnum.getCode() == codeVal) {
                return resultCodeEnum;
            }
        }
        return null;
    }
}