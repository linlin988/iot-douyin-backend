package com.iot.content.controller;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSON;
import cn.hutool.json.JSONUtil;
import com.iot.commonModules.common.Result;
import com.iot.commonModules.DTO.pageQuery;
import com.iot.commonModules.entity.Comments;
import com.iot.content.DTO.CommentDTO;
import com.iot.content.service.ICommentService;
import io.swagger.v3.oas.annotations.Operation;
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

    /**
     * 发布评论
     * @param commentDTO
     * @return
     */
    @PostMapping("/add")
    @Operation(description = "发布评论")
    public Result addComment(@RequestBody CommentDTO commentDTO){
        log.info("发布评论：{}", commentDTO);
        return commentService.addComment(commentDTO);
    }

    /**
     * 删除我的评论
     * @param commentId
     * @return
     */
    @DeleteMapping("/delete/{id}")
    @Operation(description = "删除我的评论")
    public Result deleteComment(@PathVariable ("id") Long commentId){
        return commentService.deleteComment(commentId);
    }

    /**
     * 获取当前评论列表
     * @param videoId
     * @param pageQuery
     * @return
     */
    @PostMapping("/list/{id}")
    @Operation(description = "获取当前评论列表")
    public Result listComment(
            @PathVariable("id") Long videoId, @RequestBody pageQuery pageQuery){
        log.info("分页查询评论列表");
        Result result = commentService.listComment(videoId, pageQuery);
        log.info("分页查询评论列表结果：\n{}", JSONUtil.toJsonPrettyStr(result));
        return result;
    }


}
