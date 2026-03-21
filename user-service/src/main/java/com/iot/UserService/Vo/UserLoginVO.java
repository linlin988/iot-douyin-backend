package com.iot.UserService.Vo;
import lombok.Data;
@Data

public class UserLoginVO {//登录返回vo
    private long id;//用户id
    private String username;//用户名
    private String avatar;//用户头像
    private Integer followCount;//关注数
    private Integer fanCount;//粉丝数
    private  String token;//token
}
