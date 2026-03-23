package com.iot.UserService.Dto;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Min;
import lombok.Data;
@Data
@Schema(name = "UserUpdateDTO", description = "用户信息修改请求参数")
public class UserUpdateDTO {
    @Schema(description = "用户昵称（对应数据库nickname）", example = "小李同学", maxLength = 20)
    private String nickname;
    @Schema(description = "用户头像URL", example = "https://xxx.com/avatar.png")
    private String avatar; // 头像地址

    @Schema(description = "关注数增量（+N=新增关注，-N=取消关注）", example = "1", minimum = "-1000", maximum = "1000")
    private Integer followCountDelta; // 增量

    @Schema(description = "粉丝数增量（+N=新增粉丝，-N=减少粉丝）", example = "1", minimum = "-1000", maximum = "1000")
    private Integer fanCountDelta; // 增量

}
