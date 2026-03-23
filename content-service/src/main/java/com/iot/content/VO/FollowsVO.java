package com.iot.content.VO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FollowsVO {
    /**
     * 头像地址
     */
    private String avatar;

    /**
     * 用户昵称
     */
    private String nickname;
}
