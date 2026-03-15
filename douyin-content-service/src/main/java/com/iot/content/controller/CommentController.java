package com.iot.content.controller;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.iot.commonModules.common.Result;
import com.iot.commonModules.DTO.pageQuery;
import com.iot.commonModules.entity.Comments;
import com.iot.content.DTO.CommentDTO;
import com.iot.content.service.ICommentService;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/comment")
@RestController
@Tag(name = "评论模块", description = "评论模块相关接口")
@Slf4j
public class CommentController {

    @Resource
    private ICommentService commentService;

    @PostMapping("/add")
    @Schema(description = "发布评论")
    public Result addComment(@RequestBody CommentDTO commentDTO){
        Long userId=1l;

        if (StrUtil.isBlank(commentDTO.getContent())) {
            Comments comments = BeanUtil.copyProperties(commentDTO, Comments.class);
            Boolean result = commentService.save(comments);
            return Result.success(result);
        }else{
            return Result.error(400, "评论内容不能为空");
        }
    }

    @DeleteMapping("/delete/{id}")
    @Schema(description = "删除我的评论")
    public Result deleteComment(@PathVariable ("id") Long videoId){
        return commentService.deleteComment(videoId);
    }

    @GetMapping("/list/{id}")
    @Schema(description = "获取当前评论列表")
    public Result listComment(
            @PathVariable("id") @Parameter(description = "视频 ID") Long videoId,
            @ModelAttribute pageQuery pageQuery
    ){
        return commentService.listComment(videoId, pageQuery);
    }


}
