package com.iot.UserService.Entity;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.io.Serializable;
import java.time.LocalDateTime;
@Data
@TableName("t_user")
public class User implements Serializable{
    private static final long serialVersionUID = 1L;//Serializable的适用场景就是当自己写的java类要进行网络传输的时候 例如微服务之间的调用 后面的值也可以修改为2l等等
    @TableId(type=IdType.ASSIGN_ID)
    private long id;//用户id
    private String name;//用户名
    private String phone;//用户手机号码
    private String password;//用户密码
    private String avater;//头像地址
    private Integer followcount;//关注数
    private Integer fanscount;//粉丝数
    private LocalDateTime createtime;//创建时间
    private LocalDateTime uptatetime;//更新时间
}
