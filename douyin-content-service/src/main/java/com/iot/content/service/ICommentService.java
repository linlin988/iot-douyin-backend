package com.iot.content.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.iot.commonModules.common.Result;
import com.iot.commonModules.DTO.pageQuery;
import com.iot.commonModules.entity.Comments;
import com.iot.content.DTO.CommentDTO;

public interface ICommentService extends IService<Comments> {
    Result deleteComment(Long videoId);

    Result listComment(Long videoId, pageQuery pageQuery);
}
