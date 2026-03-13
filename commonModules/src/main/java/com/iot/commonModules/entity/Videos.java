package com.iot.commonModules.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 视频表
 * @TableName t_video
 */
@Data
@TableName(value = "t_video")
public class Videos implements Serializable {
    /**
     * 视频ID，雪花算法
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 发布者ID，关联t_user.id
     */
    @TableField(value = "user_id")
    private Long userId;

    /**
     * 视频标题
     */
    private String title;

    /**
     * 视频描述
     */
    private String description;

    /**
     * 视频播放地址
     */
    @TableField(value = "video_url")
    private String videoUrl;

    /**
     * 视频封面地址
     */
    @TableField(value = "cover_url")
    private String coverUrl;

    /**
     * 点赞数，冗余字段
     */
    @TableField(value = "like_count")
    private Long likeCount;

    /**
     * 播放数，冗余字段
     */
    @TableField(value = "play_count")
    private Long playCount;

    /**
     * 评论数，冗余字段
     */
    @TableField(value = "comment_count")
    private Long commentCount;

    /**
     * 发布时间
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