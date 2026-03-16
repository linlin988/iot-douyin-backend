package com.iot.content.VO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommentVO {
    /**
     * 头像地址
     */
    private String avatar;

    /**
     * 用户昵称
     */
    private String nickname;
    /**
     * 评论内容
     */
    private String content;
    /**
     * 评论时间
     */
    private String createTime;

}

