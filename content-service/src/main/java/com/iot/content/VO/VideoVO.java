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
}
