package com.iot.commonModules.common;

import lombok.Data;
import lombok.EqualsAndHashCode;

 @EqualsAndHashCode(callSuper = true)
 @Data
public class AllException extends RuntimeException {
     private Integer status; // 自定义状态码
     public AllException(String message, Integer status) {
         super(message);
         this.status = status;
     }
     public AllException(ExceptionEnum exceptionEnum){
         super(exceptionEnum.getMessage());
         this.status = exceptionEnum.getStatus();
     }
 }
enum ExceptionEnum {
    // 业务异常枚举项
    USER_NOT_FOUND(404, "用户不存在"),
    PASSWORD_ERROR(400, "密码错误"),
    USERNAME_EXIST(400, "用户名已存在"),
    PARAM_ERROR(400, "参数格式错误"),
    DB_ERROR(500, "数据库操作失败");

    private final Integer status;
    private final String message;

    ExceptionEnum(Integer status, String message) {
        this.status = status;
        this.message = message;
    }

    public Integer getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }
}
