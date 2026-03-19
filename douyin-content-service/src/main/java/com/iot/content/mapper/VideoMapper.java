package com.iot.content.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.iot.commonModules.entity.Videos;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface VideoMapper extends BaseMapper<Videos> {
    @Delete("update t_video set comment_count = comment_count - 1 where id = #{videoId}")
    void reduceCommentCount(Long videoId);

    @Delete("update t_video set comment_count = comment_count + 1 where id = #{videoId}")
    void addCommentCount(Long videoId);

    void batchUpdateLikeCount(@Param("list") List<Videos> List);
}
