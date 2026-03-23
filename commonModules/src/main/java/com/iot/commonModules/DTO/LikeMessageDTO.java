package com.iot.commonModules.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class LikeMessageDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long userId;//点赞人id
    private Long videoId;//视频id
    private Long authorId;//视频作者id
    private Boolean isLiked;//点赞
}
