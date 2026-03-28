package com.iot.content.VO;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VideoVO {

    /**
     * 视频ID，雪花算法
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;
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
     * 视频标题
     */
    private String title;
    /**
     * 视频描述
     */
    private String description;
    /**
     * 点赞数
     */
    private Long likeCount;
    /**
     * 评论数
     */
    private Long commentCount;
}
