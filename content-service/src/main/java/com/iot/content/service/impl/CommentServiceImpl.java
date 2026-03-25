package com.iot.content.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.iot.commonModules.DTO.pageQuery;
import com.iot.commonModules.common.Result;
import com.iot.commonModules.entity.Comments;
import com.iot.commonModules.entity.User;
import com.iot.commonModules.utils.UserContext;
import com.iot.content.DTO.CommentDTO;
import com.iot.content.VO.CommentVO;
import com.iot.content.mapper.UserMapper;
import com.iot.content.mapper.VideoMapper;
import com.iot.content.service.ICommentService;
import com.iot.content.mapper.CommentMapper;
import com.iot.commonModules.DTO.PageDTO;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import lombok.extern.slf4j.Slf4j;
@Service
@Slf4j
public class CommentServiceImpl extends ServiceImpl<CommentMapper, Comments> implements ICommentService {

   @Resource
   private UserMapper userMapper;
   @Resource
   private VideoMapper videoMapper;

    /**
     * 删除评论
     * @param commentId
     * @return
     */
    @Override
    public Result deleteComment(Long commentId) {
        Long userId = UserContext.getUser();
        
        // 1. 根据 commentId 查询评论信息
        Comments comment = getById(commentId);
        if (comment == null) {
            return Result.error(404, "评论不存在");
        }
        

        // if (!userId.equals(comment.getUserId())) {
        //     return Result.error(403, "无权删除他人的评论");
        // }
        
        // 3. 减去该视频的评论数
        videoMapper.reduceCommentCount(comment.getVideoId());
        
        // 4. 删除评论记录
        removeById(commentId);
        
        return Result.success();
    }

    /**
     * 根据视频 ID 查询评论列表
     * @param videoId
     * @param pageQuery
     * @return
     */
    @Override
    public Result listComment(Long videoId, pageQuery pageQuery) {
        //1.构建分页查询对象
        Page<Comments> page = pageQuery.toMpPageDefaultSortByCreateTimeDesc();
        
        //2.执行分页查询，根据视频 ID 查询评论
        Page<Comments> commentsPage = this.page(page, new QueryWrapper<Comments>()
                .eq("video_id", videoId));
        
        // 若为空，直接返回空结果
        if (commentsPage == null || commentsPage.getRecords().isEmpty()) {
            return Result.success(PageDTO.of(commentsPage, c -> null));
        }
       
        //3.取出评论的 userId 列表，用于批量查询用户信息 (去重)
        List<Long> userIds = commentsPage.getRecords().stream()
                .map(Comments::getUserId)
                .distinct()
                .collect(Collectors.toList());
        
        //4.根据 userId，批量查询评论人的昵称、头像
        List<User> users = userMapper.selectBatchIds(userIds);
        Map<Long, User> userMap = users.stream()
                .collect(Collectors.toMap(User::getId, u -> u));
        
        //5.转换为 CommentVO 并返回 PageDTO<CommentVO>
        PageDTO<CommentVO> voPageDTO = PageDTO.of(commentsPage, comment -> {
            User user = userMap.get(comment.getUserId());
            return new CommentVO(
                    user != null ? user.getAvatar() : "",
                    user != null ? user.getUsername() : "未知用户",
                    comment.getContent(),
                    LocalDateTimeUtil.format(comment.getCreateTime(), "yyyy-MM-dd HH:mm")
            );
        });
        
        //6.返回分页结果
        return Result.success(voPageDTO);
    }

    /**
     * 添加评论
     * @param commentDTO
     * @return
     */
    @Override
    public Result addComment(CommentDTO commentDTO) {

        Long userId = UserContext.getUser();
        log.info("当前登录用户ID：{}", userId);
        if (StrUtil.isNotBlank(commentDTO.getContent())) {
            Comments comments = BeanUtil.copyProperties(commentDTO, Comments.class);
            comments.setUserId(userId);
            Boolean result = save(comments);
            videoMapper.addCommentCount(comments.getVideoId());
            return Result.success(result);
        }else{
            return Result.error(400, "评论内容不能为空");
        }
    }
}
