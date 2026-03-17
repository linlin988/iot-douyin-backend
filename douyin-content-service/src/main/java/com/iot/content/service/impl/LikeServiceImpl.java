package com.iot.content.service.impl;

import cn.hutool.core.util.BooleanUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.iot.commonModules.common.Result;
import com.iot.commonModules.entity.Likes;
import com.iot.commonModules.utils.UserContext;
import com.iot.content.mapper.LikeMapper;
import com.iot.content.service.ILikeService;
import jakarta.annotation.Resource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class LikeServiceImpl extends ServiceImpl<LikeMapper, Likes> implements ILikeService {

    @Resource
    private StringRedisTemplate stringRedisTemplate;
    @Resource
    private LikeMapper likeMapper;

    @Override
    public Result likeVideo(Long videoId) {
        //1.获取当前登录用户
        Long userId = UserContext.getUser();
        //2.在redis里判断当前用户是否已经点赞过该视频
        Boolean member = stringRedisTemplate.opsForSet().isMember("like:" + videoId, userId.toString());
        if(BooleanUtil.isFalse(member)) {
            //3.如果未点赞，可以进行点赞
            //3.1 like表插入数据
            Likes likes = new Likes();
            likes.setUserId(userId);
            likes.setVideoId(videoId);
            save(likes);
            //3.2 video表点赞数+1
            boolean isSuccess = likeMapper.updateLikeCount(videoId, 1L);
            //3.3 保存用户到redis的set集合
            if (isSuccess){
                stringRedisTemplate.opsForSet().add("like:" + videoId, userId.toString());
            }
        }else {
            //4.如果已点赞，取消点赞
            //4.1 like表删除数据
            QueryWrapper<Likes> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("user_id", userId).eq("video_id", videoId);
            remove(queryWrapper);
            //4.2 video表点赞数-1
            boolean isSuccess = likeMapper.updateLikeCount(videoId, -1L);
            //4.3 从redis的set集合中删除用户
            if (isSuccess){
                stringRedisTemplate.opsForSet().remove("like:" + videoId, userId.toString());
            }
        }

        return Result.success();
    }

    @Override
    public Result isLike(Long videoId, Long userId) {
        Boolean isLike = stringRedisTemplate.opsForSet().isMember("like:" + videoId, userId.toString());
        return Result.success(isLike);
    }
}
