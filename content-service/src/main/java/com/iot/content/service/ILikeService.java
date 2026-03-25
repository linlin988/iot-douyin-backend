package com.iot.content.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.iot.commonModules.common.Result;
import com.iot.commonModules.entity.Likes;
import com.iot.content.VO.VideoVO;

import java.util.List;

public interface ILikeService extends IService<Likes> {
    Result likeVideo(Long videoId);

    Result isLike(Long videoId);

    Result likeCount(Long videoId);

    List<VideoVO> getUserLikeVideoList(Long userId);
}
