package com.iot.content.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.iot.commonModules.common.Result;
import com.iot.commonModules.entity.Likes;

public interface ILikeService extends IService<Likes> {
    Result likeVideo(Long videoId);

    Result isLike(Long videoId);

    Result likeCount(Long videoId);
    Result getUserLikeVideoList(Long userId);
}
