package com.iot.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.iot.commonModules.entity.Comments;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CommentMapper extends BaseMapper<Comments> {
}
