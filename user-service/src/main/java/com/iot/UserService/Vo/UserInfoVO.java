package com.iot.UserService.Vo;
import lombok.Data;
import io.swagger.v3.oas.annotations.media.Schema;
@Data
@Schema(name = "UserLoginVO", description = "展示用户信息")
public class UserInfoVO {//展示用户信息
    @Schema(description = "用户ID", example = "1")
    private long id;//用户id

    @Schema(description = "用户名", example = "zhangsan123")
    private String username;//用户名

    @Schema(description = "用户头像", example = "https:/....")
    private String avatar;//用户头像

    @Schema(description = "关注数", example = "1")
    private Integer followCount;//关注数

    @Schema(description = "粉丝数", example = "1")
    private Integer fanCount;//粉丝数
}
