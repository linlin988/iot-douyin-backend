package com.iot.content.VO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(name = "UserLoginVO", description = "用户登录返回结果")
public class UserVO {//登录返回vo
    @Schema(description = "用户ID", example = "1")
    private Long id;//用户id

    @Schema(description = "用户名", example = "zhangsan123")
    private String username;//用户名
    @Schema(description = "用户头像", example = "https:/....")
    private String avatar;//用户头像

    @Schema(description = "关注数", example = "1")
    private Integer followCount;//关注数

    @Schema(description = "粉丝数", example = "1")
    private Integer fanCount;//粉丝数

    @Schema(description = "登录token", example = "eyJhbGciOiJIUzI1NiJ9.xxx")
    private  String token;//token
}
