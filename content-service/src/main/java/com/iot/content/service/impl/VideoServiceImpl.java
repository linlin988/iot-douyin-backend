package com.iot.content.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.iot.commonModules.entity.Videos;
import com.iot.content.config.AliyunOSSOperator;
import com.iot.content.mapper.VideoMapper;
import com.iot.content.service.VideoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;




@Service
public class VideoServiceImpl implements VideoService {

    @Autowired
    AliyunOSSOperator aliyunOSSOperator;

    @Autowired
    VideoMapper videoMapper;


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
}
