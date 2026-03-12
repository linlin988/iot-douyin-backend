# 项目关键部分说明



## 项目结构

### 公共模块（common-modules）  
里面有数据库对应的实体，在子服务继承该模块就能直接用了，后续会加入统一返回和各种工具（如jwt）
### 非代码文件夹（docs）  
里面放有api调用说明，各位开发好后可以在里面写入api调用相关注意事项以便于子服务间互相调用  ------||||-----------里面还有一个design文件夹，用来存放我们自己独特的设计，方便小组其他人理解你的想法。




## 数据库各个字段使用说明


### t_comment 评论表
#### Long id
评论的唯一 ID，使用雪花算法生成，用于标识每条评论
#### Long userId
评论者 ID，逻辑关联用户表t_user的 id 字段，标识这条评论的发布者
#### Long videoId
被评论视频 ID，逻辑关联视频表t_video的 id 字段，标识评论所属的视频
#### String content
评论内容，最长支持 500 个字符，不能为空
#### LocalDateTime createTime
评论创建时间，数据库自动生成，记录评论发布的北京时间
#### LocalDateTime updateTime
评论更新时间，数据库自动刷新，记录评论最后修改时间

### t_follow 关注表
#### Long id
关注记录的唯一 ID，雪花算法生成，用于标识每条关注关系
#### Long followUserId
关注者 ID（主动关注的用户），逻辑关联用户表t_user的 id
#### Long followedUserId
被关注者 ID（被关注的用户），逻辑关联用户表t_user的 id
#### LocalDateTime createTime
关注创建时间，数据库自动生成，记录关注发生的时间
#### LocalDateTime updateTime
关注记录更新时间，数据库自动刷新更新时刻

### t_like 点赞表
#### Long id
点赞记录的唯一 ID，雪花算法生成，用于标识每条点赞行为
#### Long userId
点赞者 ID，逻辑关联用户表t_user的 id，标识谁点的赞
#### Long videoId
被点赞视频 ID，逻辑关联视频表t_video的 id，标识哪个视频被点赞
#### LocalDateTime createTime
点赞时间，数据库自动生成，记录点赞行为发生的时刻
#### LocalDateTime updateTime
点赞记录更新时间，数据库自动刷新

### t_user 用户表
#### Long id
用户唯一 ID，雪花算法生成，用于标识每个用户
#### String username
用户名，用户登录使用，全局唯一，不可重复
#### String phone
手机号，用于注册、登录验证，全局唯一
#### String password
登录密码，采用 BCrypt 加密存储，不明文保存
#### String avatar
用户头像地址，存储 OSS 文件链接，默认使用default_avatar.png
#### String nickname
用户昵称，可自定义修改，用于页面展示
#### Long followCount
关注总数，记录该用户关注了多少人，业务层维护更新
#### Long fanCount
粉丝总数，记录有多少人关注该用户，业务层维护更新
#### Long totalLiked
获赞总数，该用户所有视频被点赞的总次数，业务层维护更新
#### LocalDateTime createTime
账号注册时间，数据库自动生成
#### LocalDateTime updateTime
用户信息更新时间，数据库自动刷新

### t_video 视频表
#### Long id
视频唯一 ID，雪花算法生成，用于标识每个视频
#### Long userId
视频发布者 ID，逻辑关联用户表t_user的 id
#### String title
视频标题，支持模糊搜索，最长 100 字符
#### String description
视频描述信息，可空，最长 500 字符
#### String videoUrl
视频播放地址，存储 OSS 文件链接
#### String coverUrl
视频封面图片地址，存储 OSS 文件链接
#### Long likeCount
视频点赞总数，业务层实时维护更新
#### Long playCount
视频播放次数，用户每次打开视频时业务层 + 1
#### Long commentCount
视频评论总数，业务层实时维护更新
#### LocalDateTime createTime
视频发布时间，数据库自动生成
#### LocalDateTime updateTime
视频信息更新时间，数据库自动刷新