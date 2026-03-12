package com.iot.commonModules.Entity;

import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import lombok.Data;
import org.apache.ibatis.reflection.MetaObject;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 用户表
 * @TableName t_User
 */
@Data
@TableName(value = "t_user")
public class User implements Serializable {
    /**
     * 用户ID，雪花算法
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 用户名，唯一
     */
    private String username;

    /**
     * 手机号，唯一
     */
    private String phone;

    /**
     * 密码，BCrypt加密
     */
    private String password;

    /**
     * 头像地址
     */
    private String avatar;

    /**
     * 用户昵称
     */
    private String nickname;

    /**
     * 关注数，冗余字段
     */
    @TableField(value = "follow_count")
    private Long followCount;

    /**
     * 粉丝数，冗余字段
     */
    @TableField(value = "fan_count")
    private Long fanCount;

    /**
     * 获赞总数，冗余字段
     */
    @TableField(value = "total_liked")
    private Long totalLiked;

    /**
     * 注册时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.UPDATE, update = "now()")
    private LocalDateTime updateTime;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}