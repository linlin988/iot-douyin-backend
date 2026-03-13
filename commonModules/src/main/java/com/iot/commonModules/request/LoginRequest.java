package com.iot.commonModules.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginRequest {
    @NotBlank(message = "用户名不能为空！")
    private String username;
    @NotBlank(message = "密码不能为空！")
    private String password;
    @NotBlank(message = "验证码不能为空！")
    private String captcha;//用户输入的验证码
    private String captchaKey;//前端传入的铭文，方便查redis
}
