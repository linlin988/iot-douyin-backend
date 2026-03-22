package com.iot.content.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.iot.commonModules.common.Result;
import com.iot.commonModules.DTO.pageQuery;
import com.iot.commonModules.entity.Comments;
import com.iot.content.DTO.CommentDTO;

public interface ICommentService extends IService<Comments> {
    Result deleteComment(Long commentId);

    Result listComment(Long videoId, pageQuery pageQuery);

    Result addComment(CommentDTO commentDTO);
}
