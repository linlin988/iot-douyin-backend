package com.iot.content.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.BooleanUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.iot.commonModules.common.Result;
import com.iot.commonModules.entity.Likes;
import com.iot.commonModules.entity.Videos;
import com.iot.commonModules.utils.UserContext;
import com.iot.content.mapper.LikeMapper;
import com.iot.content.mapper.VideoMapper;
import com.iot.content.service.ILikeService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
@Slf4j
public class LikeServiceImpl extends ServiceImpl<LikeMapper, Likes> implements ILikeService {

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Resource
    private VideoMapper videoMapper;

    @Resource
    private LikeMapper likeMapper;

    private static final String LIKE_USER_KEY = "like:";           // 点赞用户集合
    private static final String LIKE_COUNT_KEY = "like:count:";   // 点赞计数
    private static final String DIRTY_KEY = "like:dirty:video";   // 脏视频集合

    //点赞/取消点赞（只写Redis）
    @Override
    public Result likeVideo(Long videoId) {
        Long userId = UserContext.getUser();

        String userSetKey = LIKE_USER_KEY + videoId;
        String userIdStr = userId.toString();

        // 判断是否已点赞
        Boolean isMember = stringRedisTemplate.opsForSet().isMember(userSetKey, userIdStr);

        if (BooleanUtil.isFalse(isMember)) {
            // 点赞：原子添加
            Long addSuccess = stringRedisTemplate.opsForSet().add(userSetKey, userIdStr);
            if (addSuccess==1L) {
                // 计数+1
                stringRedisTemplate.opsForValue().increment(LIKE_COUNT_KEY + videoId);
                // 标记脏数据
                stringRedisTemplate.opsForSet().add(DIRTY_KEY, videoId.toString());
            }
        } else {
            // 取消点赞：原子删除
            Long removeSuccess = stringRedisTemplate.opsForSet().remove(userSetKey, userIdStr);
            if (removeSuccess==1L) {
                // 计数-1
                stringRedisTemplate.opsForValue().decrement(LIKE_COUNT_KEY + videoId);
                // 标记脏数据
                stringRedisTemplate.opsForSet().add(DIRTY_KEY, videoId.toString());
            }
        }

        return Result.success();
    }

    // 定时同步数据库（Write-Behind）
    @Scheduled(fixedRate = 5000) // 每5秒执行
    public void syncLikeToDB() {
        // 每次最多同步50个视频
        ArrayList<String> videoIdSet = (ArrayList<String>) stringRedisTemplate.opsForSet().pop(DIRTY_KEY, 50);

        if (CollUtil.isEmpty(videoIdSet)) {
            return;
        }

        List<Videos> updateList = new ArrayList<>();

        for (String videoIdStr : videoIdSet) {
            Long videoId = Long.valueOf(videoIdStr);
            String countKey = LIKE_COUNT_KEY + videoId;

            // 从Redis取最新计数
            String countStr = stringRedisTemplate.opsForValue().get(countKey);
            if (countStr == null) {
                continue;
            }

            Videos video = new Videos();
            video.setId(videoId);
            video.setLikeCount(Long.valueOf(Integer.valueOf(countStr)));
            updateList.add(video);
        }

        // 批量更新数据库（性能极高）
        if (CollUtil.isNotEmpty(updateList)) {
            videoMapper.batchUpdateLikeCount(updateList);
            log.info("同步点赞数成功，视频数量：{}", updateList.size());
        }
    }

//
//    @Transactional
//    @Override
//    public Result likeVideo(Long videoId) {
////        //1.获取当前登录用户
////        Long userId = UserContext.getUser();
////        //2.在redis里判断当前用户是否已经点赞过该视频
////        Boolean member = stringRedisTemplate.opsForSet().isMember("like:" + videoId, userId.toString());
////        if(BooleanUtil.isFalse(member)) {
////            //3.如果未点赞，可以进行点赞
////            //3.1 like表插入数据
////            Likes likes = new Likes();
////            likes.setUserId(userId);
////            likes.setVideoId(videoId);
////            save(likes);
////            //3.2 video表点赞数+1
////            boolean isSuccess = likeMapper.updateLikeCount(videoId, 1L);
////            //3.3 保存用户到redis的set集合
////            if (isSuccess){
////                System.out.println("进入redis");
////                System.out.println("进入redis");
////
////                stringRedisTemplate.opsForSet().add("like:" + videoId, userId.toString());
////                log.debug("存入redis");
////            }
////        }else {
////            //4.如果已点赞，取消点赞
////            //4.1 like表删除数据
////            QueryWrapper<Likes> queryWrapper = new QueryWrapper<>();
////            queryWrapper.eq("user_id", userId).eq("video_id", videoId);
////            remove(queryWrapper);
////            //4.2 video表点赞数-1
////            boolean isSuccess = likeMapper.updateLikeCount(videoId, -1L);
////            //4.3 从redis的set集合中删除用户
////            if (isSuccess){
////                stringRedisTemplate.opsForSet().remove("like:" + videoId, userId.toString());
////            }
////        }
////
////        return Result.success();
//    }
//
    @Override
    public Result isLike(Long videoId, Long userId) {
        Boolean isLike = stringRedisTemplate.opsForSet().isMember("like:" + videoId, userId.toString());
        return Result.success(isLike);
    }

    @Override
    public Result likeCount(Long videoId) {
        String countKey = LIKE_COUNT_KEY + videoId;
        String countStr = stringRedisTemplate.opsForValue().get(countKey);
        return Result.success(countStr);
    }


}
