package com.iot.commonModules.Entity;

import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 点赞表
 * @TableName t_like
 */
@Data
@TableName(value = "t_like")
public class Likes implements Serializable {
    /**
     * 点赞记录ID，雪花算法
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 点赞者ID，关联t_user.id
     */
    @TableField(value = "user_id")
    private Long userId;

    /**
     * 被点赞视频ID，关联t_video.id
     */
    @TableField(value = "video_id")
    private Long videoId;

    /**
     * 点赞时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @TableField(fill =FieldFill.UPDATE, update = "now()")
    private LocalDateTime updateTime;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}