package com.iot.tiktok.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.iot.commonModules.entity.Videos;
import com.iot.tiktok.config.AliyunOSSOperator;
import com.iot.tiktok.mapper.VideoMapper;
import com.iot.tiktok.service.VideoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;


@Service
public class VideoServiceImpl implements VideoService {

    @Autowired
    AliyunOSSOperator aliyunOSSOperator;

    @Autowired
    VideoMapper videoMapper;

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
            return video;
        }
        return null;
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
        //先获取视频信息
        Videos video = videoMapper.selectById(videoId);
        if (video != null) {
            //使用MyBatis-Plus的update方法，利用数据库的原子操作来增加播放数，确保并发安全
            Videos updateVideo = new Videos();
            updateVideo.setId(videoId);
            updateVideo.setPlayCount(video.getPlayCount() + 1);
            videoMapper.updateById(updateVideo);
            //重新查询获取最新数据
            video = videoMapper.selectById(videoId);
        }
        return video;
    }


}
