package com.iot.commonModules.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 关注表
 * @TableName t_follow
 */
@Data
@TableName(value = "t_follow")
public class Follows implements Serializable {
    /**
     * 关注记录ID，雪花算法
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 关注者ID，关联t_user.id
     */
    @TableField(value = "follow_user_id")
    private Long followUserId;

    /**
     * 被关注者ID，关联t_user.id
     */
    @TableField(value = "followed_user_id")
    private Long followedUserId;

    /**
     * 关注时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.UPDATE, update = "now()")
    private LocalDateTime updateTime;

}