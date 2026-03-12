create table t_comment
(
    id          bigint                             not null comment '评论ID，雪花算法生成'
        primary key,
    user_id     bigint                             not null comment '评论者ID，逻辑关联t_user.id',
    video_id    bigint                             not null comment '被评论视频ID，逻辑关联t_video.id',
    content     varchar(500)                       not null comment '评论内容，非空',
    create_time datetime default CURRENT_TIMESTAMP null comment '评论时间，自动生成',
    update_time datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '更新时间，自动刷新'
)
    comment '评论表-发布/删除/评论列表查询' charset = utf8mb4;

create index idx_user_id
    on t_comment (user_id asc, create_time desc);

create index idx_video_create
    on t_comment (video_id asc, create_time desc);

create table t_follow
(
    id               bigint                             not null comment '关注记录ID，雪花算法生成'
        primary key,
    follow_user_id   bigint                             not null comment '关注者ID，逻辑关联t_user.id',
    followed_user_id bigint                             not null comment '被关注者ID，逻辑关联t_user.id',
    create_time      datetime default CURRENT_TIMESTAMP null comment '关注时间，自动生成',
    update_time      datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '更新时间，自动刷新',
    constraint uk_follow_followed
        unique (follow_user_id, followed_user_id)
)
    comment '关注表-关注/取关/粉丝统计' charset = utf8mb4;

create index idx_follow_user
    on t_follow (follow_user_id asc, create_time desc);

create index idx_followed_user
    on t_follow (followed_user_id asc, create_time desc);

create table t_like
(
    id          bigint                             not null comment '点赞记录ID，雪花算法生成'
        primary key,
    user_id     bigint                             not null comment '点赞者ID，逻辑关联t_user.id',
    video_id    bigint                             not null comment '被点赞视频ID，逻辑关联t_video.id',
    create_time datetime default CURRENT_TIMESTAMP null comment '点赞时间，自动生成',
    update_time datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '更新时间，自动刷新',
    constraint uk_user_video
        unique (user_id, video_id)
)
    comment '点赞表-点赞/取消点赞/点赞记录' charset = utf8mb4;

create index idx_user_id
    on t_like (user_id asc, create_time desc);

create index idx_video_id
    on t_like (video_id asc, create_time desc);

create table t_user
(
    id           bigint                                       not null comment '用户ID，雪花算法生成'
        primary key,
    username     varchar(50)                                  not null comment '用户名，登录用，唯一',
    phone        varchar(20)                                  null comment '手机号，注册/登录用，唯一',
    password     varchar(100)                                 not null comment '密码，BCrypt加密存储）',
    avatar       varchar(255)    default 'default_avatar.png' null comment '头像地址，对象存储（OSS）链接',
    nickname     varchar(50)     default ''                   null comment '用户昵称，可自定义',
    follow_count bigint unsigned default '0'                  null comment '关注数，冗余字段，业务层维护',
    fan_count    bigint unsigned default '0'                  null comment '粉丝数，冗余字段，业务层维护',
    total_liked  bigint unsigned default '0'                  null comment '获赞总数，冗余字段，业务层维护',
    create_time  datetime        default CURRENT_TIMESTAMP    null comment '注册时间，自动生成',
    update_time  datetime        default CURRENT_TIMESTAMP    null on update CURRENT_TIMESTAMP comment '更新时间，自动刷新',
    constraint uk_phone
        unique (phone),
    constraint uk_username
        unique (username)
)
    comment '用户表-认证/个人信息/基础统计' charset = utf8mb4;

create table t_video
(
    id            bigint                                    not null comment '视频ID，雪花算法生成'
        primary key,
    user_id       bigint                                    not null comment '发布者ID，逻辑关联t_user.id，业务层保证一致性',
    title         varchar(100)                              not null comment '视频标题，支持模糊搜索',
    description   varchar(500)    default ''                null comment '视频描述',
    video_url     varchar(255)                              not null comment '视频播放地址，对象存储（OSS）链接',
    cover_url     varchar(255)                              not null comment '视频封面地址，对象存储（OSS）链接',
    like_count    bigint unsigned default '0'               null comment '点赞数，冗余字段，业务层实时更新',
    play_count    bigint unsigned default '0'               null comment '播放数，冗余字段，查看详情时业务层+1',
    comment_count bigint unsigned default '0'               null comment '评论数，冗余字段，业务层实时更新',
    create_time   datetime        default CURRENT_TIMESTAMP null comment '发布时间，自动生成',
    update_time   datetime        default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '更新时间，自动刷新'
)
    comment '视频表-元数据/基础统计/播放地址' charset = utf8mb4;

create index idx_create_time
    on t_video (create_time desc);

create index idx_like_count
    on t_video (like_count desc);

create index idx_play_count
    on t_video (play_count desc);

create index idx_user_create
    on t_video (user_id asc, create_time desc);


