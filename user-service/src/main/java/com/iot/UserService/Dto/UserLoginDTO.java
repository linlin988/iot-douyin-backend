package com.iot.UserService.Dto;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
@Schema(name = "UserRegisterDTO", description = "用户注册请求参数") // DTO注解
@Data
public class UserLoginDTO {
    @Schema(description = "账号（用户名/手机号）", required = true, example = "zhangsan123") // 参数注解
    @NotBlank(message = "用户名/手机号不能为空")
    private String account;

    @Schema(description = "密码", required = true, example = "123456abc", pattern = "^[a-zA-Z0-9]{6,20}$")
    @NotBlank(message = "用户密码不能为空")
    @Pattern(regexp = "^[a-zA-Z0-9]{6,20}$", message = "密码需为6-20位字母/数字")
    private String password;
}
