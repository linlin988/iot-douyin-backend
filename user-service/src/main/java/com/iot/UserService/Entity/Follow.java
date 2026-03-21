package com.iot.UserService.Entity;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.io.Serializable;
import java.time.LocalDateTime;
@Data
@TableName("t_follow")
public class Follow implements Serializable {//关注实体类
    private static final long serialVersionUID=1L;
    @TableId(type=IdType.ASSIGN_ID)
    private long id;
    private long follow_user_id;//当前用户id
    private long followed_user_id;//被关注的用户id
    private LocalDateTime create_time;//创建时间
}
