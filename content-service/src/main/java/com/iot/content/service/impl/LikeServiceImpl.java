package com.iot.content.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.BooleanUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.iot.commonModules.DTO.LikeMessageDTO;
import com.iot.commonModules.common.Result;
import com.iot.commonModules.entity.Likes;
import com.iot.commonModules.entity.Videos;
import com.iot.commonModules.utils.UserContext;
import com.iot.content.VO.VideoVO;
import com.iot.content.mapper.LikeMapper;
import com.iot.content.mapper.VideoMapper;
import com.iot.content.service.ILikeService;
import com.iot.content.service.sendLikeMassageService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
public class LikeServiceImpl extends ServiceImpl<LikeMapper, Likes> implements ILikeService {

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Resource
    private VideoMapper videoMapper;

    @Resource
    private sendLikeMassageService likeMassageService;

    private static final String LIKE_USER_KEY = "like:";           // 点赞用户集合
    private static final String LIKE_COUNT_KEY = "like:count:";   // 点赞计数
    private static final String DIRTY_KEY = "like:dirty:video";   // 脏视频集合
    private static final String USER_LIKE_KEY = "user:like:";     // 用户点赞视频集合

    //点赞/取消点赞（只写Redis）
    @Override
    public Result likeVideo(Long videoId) {
        Long userId = UserContext.getUser();
        log.info("当前登录用户ID：{}", userId);
        Long authorId = videoMapper.selectById(videoId).getUserId();

        String userSetKey = LIKE_USER_KEY + videoId;
        String userIdStr = userId.toString();
        log.info("当前登录用户ID：{}", userIdStr);        // 判断是否已点赞
        Boolean isMember = stringRedisTemplate.opsForSet().isMember(userSetKey, userIdStr);

        if (BooleanUtil.isFalse(isMember)) {
            // 点赞：原子添加
            Long addSuccess = stringRedisTemplate.opsForSet().add(userSetKey, userIdStr);
            if (addSuccess != null && addSuccess.equals(1L)) {
                // 计数+1
                stringRedisTemplate.opsForValue().increment(LIKE_COUNT_KEY + videoId);
                // 标记脏数据
                stringRedisTemplate.opsForSet().add(DIRTY_KEY, videoId.toString());
                stringRedisTemplate.opsForSet().add(USER_LIKE_KEY + userId, videoId.toString());
            }
            //3. 发送 MQ 消息
            LikeMessageDTO dto = new LikeMessageDTO();
            dto.setVideoId(videoId);
            dto.setUserId(userId);
            dto.setAuthorId(authorId);
            dto.setIsLiked(true);
            log.debug("点赞消息：{}", dto);
            likeMassageService.sendLikeMessage(dto);
        } else {
            // 取消点赞：原子删除
            Long removeSuccess = stringRedisTemplate.opsForSet().remove(userSetKey, userIdStr);
            if (removeSuccess != null && removeSuccess.equals(1L)) {
                // 计数-1
                stringRedisTemplate.opsForValue().decrement(LIKE_COUNT_KEY + videoId);
                // 标记脏数据
                stringRedisTemplate.opsForSet().add(DIRTY_KEY, videoId.toString());
                stringRedisTemplate.opsForSet().remove(USER_LIKE_KEY + userId, videoId.toString());
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
        log.info("脏数据-非-空");
        List<Videos> updateList = new ArrayList<>();

        for (String videoIdStr : videoIdSet) {
            Long videoId = Long.valueOf(videoIdStr);
            String countKey = LIKE_COUNT_KEY + videoId;

            //
            log.info("从Redis取最新计数");
            String countStr = stringRedisTemplate.opsForValue().get(countKey);
            if (countStr == null) {
                continue;
            }
            log.info("正在更新视频 ID：{}，点赞数：{}", videoId, countStr);

            Videos video = new Videos();
            video.setId(videoId);
            video.setLikeCount((long) Integer.parseInt(countStr));
            updateList.add(video);
        }

        // 批量更新数据库
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
    public Result isLike(Long videoId) {
        Long userId = UserContext.getUser();
        Boolean isLike = stringRedisTemplate.opsForSet().isMember("like:" + videoId, userId.toString());
        return Result.success(isLike);
    }

    @Override
    public Result likeCount(Long videoId) {
        String countKey = LIKE_COUNT_KEY + videoId;
        String countStr = stringRedisTemplate.opsForValue().get(countKey);
        return Result.success(countStr);
    }

    @Override
    public List<VideoVO> getUserLikeVideoList(Long userId) {
        // 临时空实现，先让项目能启动
        String key = USER_LIKE_KEY + userId;
        Set<String> videoIdSet = stringRedisTemplate.opsForSet().members(key);

        if (videoIdSet == null) return null;

        List<Long> videoIdList = videoIdSet.stream()
                    .map(Long::valueOf)
                    .collect(Collectors.toList());

        List<Videos> videoList = videoMapper.selectList(new QueryWrapper<Videos>()
                .in("id", videoIdList).orderByDesc("create_time"));

        List<VideoVO> videoVOList = videoList.stream()
                .map(v -> new VideoVO(v.getId(), v.getVideoUrl(), v.getCoverUrl(), v.getTitle(), v.getDescription(), v.getLikeCount(), v.getCommentCount()))
                .collect(Collectors.toList());


        return videoVOList;
    }

}
