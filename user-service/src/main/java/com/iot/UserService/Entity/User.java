package com.iot.UserService.Entity;
import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;
import java.io.Serializable;
import java.time.LocalDateTime;
@Data
@TableName("t_user")
public class User implements Serializable{
    private static final long serialVersionUID = 1L;//Serializable的适用场景就是当自己写的java类要进行网络传输的时候 例如微服务之间的调用 后面的值也可以修改为2l等等
    @TableId(type=IdType.ASSIGN_ID)
    @JsonSerialize(using = ToStringSerializer.class) // 解决精度丢失
    private Long id;//用户id,必须用包装类才能雪花算法自动填充id
    private String username;//用户名
    private String phone;//用户手机号码
    private String password;//用户密码
    private String avatar;//头像地址
    private Integer follow_count;//关注数
    private Integer fan_count;//粉丝数
    private Integer total_liked;//总获赞数
    @TableField(fill= FieldFill.INSERT)//字段自动填充处理器中创建时间是insert时填充
    private LocalDateTime createTime;//创建时间
    @TableField(fill=FieldFill.INSERT_UPDATE)//字段自动填充处理器中更新时间是insert和update时填充
    private LocalDateTime updateTime;//更新时间
    private String nickname;//昵称
}
