package com.iot.UserService.Dto;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
@Data
@Schema(name = "UserRegisterDTO", description = "用户注册接口的请求参数")//类注解
public class UserRegisterDTO {
    @Schema(
            description = "注册账号（用户名/手机号）",
            required = true, // 必填字段
            example = "zhangsan123",
            maxLength = 20 // 长度说明
    )
    @NotBlank(message = "用户名/手机号不能为空")
    private String account;

    @Schema(
            description = "登录密码",
            required = true,
            example = "123456Ab",
            pattern = "^[a-zA-Z0-9]{6,20}$"
    )
    @NotBlank(message = "用户密码不能为空")
    @Pattern(regexp = "^[a-zA-Z0-9]{6,20}$", message = "密码需为6-20位字母/数字")
    private String password;
}
