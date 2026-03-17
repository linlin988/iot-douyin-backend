package com.iot.content.DTO;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommentDTO {

    /**
     * 被评论视频ID，关联t_video.id
     */
    @TableField(value = "video_id")
    private Long videoId;

    /**
     * 评论内容
     */
    private String content;

}
