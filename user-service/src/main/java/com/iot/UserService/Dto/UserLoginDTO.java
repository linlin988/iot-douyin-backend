package com.iot.UserService.Dto;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
@Data
public class UserLoginDTO {
    @NotBlank(message = "用户名/手机号不能为空")
    private String account;
    @NotBlank(message = "用户密码不能为空")
    private String password;
}
