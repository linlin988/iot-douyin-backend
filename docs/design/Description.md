# 项目关键部分说明



## 项目结构

### 公共模块（common-modules）  
里面有数据库对应的实体，在子服务继承该模块就能直接用了，后续会加入统一返回和各种工具（如jwt）
### 非代码文件夹（docs）  
里面放有api调用说明，各位开发好后可以在里面写入api调用相关注意事项以便于子服务间互相调用  ------||||-----------里面还有一个design文件夹，用来存放我们自己独特的设计，方便小组其他人理解你的想法。




## 数据库各个字段使用说明


### videos表
#### Integer id  
此视频的唯一id，用于标注此视频
#### String userId  
用户id，用于关联视频发布者
#### String videoUrl  
视频地址，用于记录视频链接
#### String coverUrl  
封面地址，用于记录视频封面链接
#### String title  
视频标题，用于保存视频标题
#### String description  
视频描述，用于保存视频作者对于该视频的描述
#### Integer playCount  
视频播放量，用于记录视频被播放的次数
#### Integer likeCount  
视频点赞量，用于记录视频被点赞的次数
#### String createdAt  
视频发布时刻，用于记录视频被发布时的北京时间


### users表
#### Integer id;
此用户的唯一id，用于标注用户
#### String username;
用户名，此用户的展示名称
#### String passworld;
用户密码，此用户用于登录验证的密码
#### String varchar;
用户头像，用于记录用户头像地址（原本是avatar，不知道为什么粘贴成varchar了）
#### String createdAt;
用户账号创建时刻，用于记录用户注册时的北京时间


### comments表
#### Integer id;
该评论的唯一id，用于标记该评论
#### String userId;
评论的用户id，用于记录该评论的发布者
#### String videoId;
评论的视频id，用于记录该评论属于哪个视频
#### String comments;
评论内容，用于记录评论的内容
#### String createdAt;
评论创建时刻，用于记录评论创建的北京时间


### follows表
#### Integer id;
此条关注信息的唯一id，用于标记该关注信息
#### String userId;
用户id，用于记录此条信息的主人
#### String followerId;
关注者（粉丝）id，用于记录粉丝的id（一般与userId相同）
#### String followeeId;
被关注者（博主）id，用于记录博主的id
#### String createdAt;
此条关注信息创建时刻，用于记录此条信息创建的北京时间


### likes表
#### Integer id;
此条点赞信息的唯一id，用于标记该点赞信息
#### String userId;
用户id，用于记录点赞者的id
#### String videoId;
视频id，用于记录被点赞视频的id
#### String createdAt;
此条关注信息创建时刻，用于记录此条信息创建的北京时间

