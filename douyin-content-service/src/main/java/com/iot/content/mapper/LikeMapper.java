package com.iot.content.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import com.iot.commonModules.entity.Likes;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface LikeMapper extends BaseMapper<Likes> {
    /**
     * 更新视频点赞数
     *
     * @param videoId
     * @param i
     */
    @Update("update t_video set like_count = like_count +#{i} where id = #{videoId}")
    boolean updateLikeCount(@Param("videoId") Long videoId,@Param("i") long i);
}
