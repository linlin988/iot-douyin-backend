package com.iot.content.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.iot.commonModules.DTO.PageDTO;
import com.iot.commonModules.common.Result;
import com.iot.commonModules.entity.User;
import com.iot.commonModules.entity.Videos;
import com.iot.content.VO.UserVO;
import com.iot.content.config.AliyunOSSOperator;
import com.iot.content.mapper.UserMapper;
import com.iot.content.mapper.VideoMapper;
import com.iot.content.service.VideoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Set;
import java.util.UUID;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static cn.hutool.core.lang.Console.log;

@Slf4j
@Service
public class VideoServiceImpl implements VideoService {

    @Autowired
    AliyunOSSOperator aliyunOSSOperator;

    @Autowired
    VideoMapper videoMapper;

    @Autowired
    UserMapper userMapper;

    @Autowired
    StringRedisTemplate stringRedisTemplate;




    //通过redis获取视频点赞数
    public Long getVideoLikeCount(Long videoId) {
        String redisKey = "like:" + videoId;
        Long likeCount = stringRedisTemplate.opsForSet().size(redisKey);
        return likeCount == null ? 0L : likeCount;
    }

    // 增加播放量
    public void incrementPlayCount(Long videoId) {
        String key = "video:play:count:" + videoId;
        stringRedisTemplate.opsForValue().increment(key, 1);
    }

    // 获取播放量（优先读Redis，不存在则读数据库）
    public Long getPlayCount(Long videoId) {
        String key = "video:play:count:" + videoId;
        String count = stringRedisTemplate.opsForValue().get(key);
        if (count == null) {
            Videos video = videoMapper.selectById(videoId);
            return video == null ? 0L : video.getPlayCount();
        }
        return Long.parseLong(count);
    }


    //视频上传
    @Override
    public Videos uploadVideo(MultipartFile file, MultipartFile coverfile, String title, String description, Long userId) throws Exception {

        String videoUrl = aliyunOSSOperator.upload(file.getBytes(), file.getOriginalFilename());
        String coverUrl = aliyunOSSOperator.upload(coverfile.getBytes(), coverfile.getOriginalFilename());

        Videos video = new Videos();

        video.setTitle(title);
        video.setDescription(description);
        video.setVideoUrl(videoUrl);
        video.setCoverUrl(coverUrl);
        video.setUserId(userId);
        video.setLikeCount(0L);
        video.setPlayCount(0L);
        video.setCommentCount(0L);

        //result为受影响的行数，大于0表示插入成功
        int result = videoMapper.insert(video);
        if (result > 0) {
            Long videoId = video.getId();
            Long likeCount = getVideoLikeCount(videoId);
            video.setLikeCount(likeCount);
            video.setPlayCount(getPlayCount(videoId));
            return video;
        }
        return null;
    }

    //删除视频
    @Override
    public boolean deleteVideo(Long videoId, Long userId) throws Exception {
        // 1. 查询视频信息
        Videos video = videoMapper.selectById(videoId);
        if (video == null) {
            return false;
        }

        // 2. 权限验证：检查视频是否属于当前用户
        if (!video.getUserId().equals(userId)) {
            return false;
        }

        // 3. 从OSS中删除视频文件和封面文件
        aliyunOSSOperator.delete(video.getVideoUrl());
        aliyunOSSOperator.delete(video.getCoverUrl());

        // 4. 从数据库中删除视频记录
        videoMapper.deleteById(videoId);

        // 5. 清除相关的Redis缓存
        String likeKey = "like:" + videoId;
        String playCountKey = "video:play:count:" + videoId;
        stringRedisTemplate.delete(likeKey);
        stringRedisTemplate.delete(playCountKey);

        return true;
    }

    //视频查询（分页）
    @Override
    public IPage<Videos> getVideoList(int page, int size) {
        Page<Videos> videoPage = new Page<>(page, size);
        QueryWrapper<Videos> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByDesc("create_time"); //按创建时间倒序
        videoMapper.selectPage(videoPage, queryWrapper);
        return videoPage;
    }


    //视频详情与播放数统计
    @Override
    public Videos getVideoDetail(Long videoId) {
        // 1. 先更新播放数（原子操作）
        videoMapper.incrementPlayCount(videoId);
        // 2. 再查询视频详情
        return videoMapper.selectById(videoId);
    }


    @Override
    public Result getUserWorksByUserId(Long userId) {
        QueryWrapper<Videos> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        queryWrapper.orderByDesc("create_time"); //按创建时间倒序
        List<Videos> videoList = videoMapper.selectList(queryWrapper);
        return Result.success(videoList);
    }

    @Override
    public User uploadAvatar(MultipartFile file, Long userId) throws Exception {

        // 1. 上传头像文件到阿里云OSS（和你封面图逻辑完全一样）
        String avatarUrl = aliyunOSSOperator.upload(file.getBytes(), file.getOriginalFilename());

        // 2. 构建用户实体（只更新头像）
        User user = new User();
        user.setId(userId);       // 用户ID
        user.setAvatar(avatarUrl); // 头像URL

        // 3. 更新数据库（同你视频insert风格，用update）

        int result = userMapper.updateById(user);

        // 4. 更新成功后，返回完整的用户信息（同你视频返回逻辑）
        if (result > 0) {
            return userMapper.selectById(userId);
        }

        // 5. 失败返回null
        return null;
    }

    /**
     * Redis记录播放（防重复 + 最多+1）
     */
    @Override
    public void recordPlayRedis(Long videoId, Long userId) {
        if (videoId == null || userId == null) {
            return;
        }

        // 1. 用户去重KEY：24小时内同一个用户只记一次
        String userPlayKey = "video:play:user:" + userId + ":" + videoId;
        // 2. 视频增量KEY
        String videoIncrKey = "video:play:incr:" + videoId;

        // 原子判断：如果用户没播放过，才执行+1
        Boolean ifAbsent = stringRedisTemplate.opsForValue()
                .setIfAbsent(userPlayKey, "1", 24, TimeUnit.HOURS);

        // true = 首次播放 → Redis播放量+1
        // 注意：这里用increment，天然线程安全
        if (Boolean.TRUE.equals(ifAbsent)) {
            stringRedisTemplate.opsForValue().increment(videoIncrKey, 1);
        }
        // 已播放过 → 不做任何操作
    }

    /**
     * 每小时执行一次：0 0 * * * ?
     * 同步Redis播放量到数据库
     */
    @Scheduled(cron = "0 0 * * * ?")
    public void syncPlayCountToDB() {
        // 1. 匹配所有待同步的视频播放增量KEY
        String pattern = "video:play:incr:*";
        Set<String> keys = stringRedisTemplate.keys(pattern);

        if (CollectionUtils.isEmpty(keys)) {
            return;
        }

        // 2. 批量同步到数据库
        for (String key : keys) {
            try {
                // 提取视频ID
                Long videoId = Long.parseLong(key.split(":")[3]);
                // 获取Redis中的增量
                String incrStr = stringRedisTemplate.opsForValue().get(key);
                if (incrStr == null || Long.parseLong(incrStr) <= 0) {
                    stringRedisTemplate.delete(key);
                    continue;
                }

                // 3. 数据库播放量 + 增量
                videoMapper.addPlayCount(videoId, Long.parseLong(incrStr));

                // 4. 同步完成，删除Redis增量记录
                stringRedisTemplate.delete(key);
            } catch (Exception e) {
                log.error("视频播放量同步失败", e);
            }
        }
    }
}
