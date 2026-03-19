# 问题1
Knife4j 版本和 SpringDoc/Spring Boot 版本不兼容，导致 Spring 无法找到 SpringDocConfigProperties 这个核心 Bean。
## 解决方法
不要同时引入 springdoc-openapi-ui 等原生 SpringDoc 依赖，Knife4j 已内置，冲突会导致 Bean 缺失。

# 问题 2：需要采用无物理外键的数据库设计，实现关联查询（如关注列表返回用户昵称 / 头像）
原因：微服务架构下物理外键会导致服务耦合，考虑移除物理外键，由业务层保证数据一致性
## 解决方案：
数据库表仅保留逻辑外键（如 follow 表的 follow_user_id、followed_user_id），不设置 FOREIGN KEY 约束；
在业务层手动维护数据统一性

# 问题3 表结构缺少字段，查询压力过高
## 解决方法

# 待做
## 1.实时更新点赞数
原代码问题：1. 先判断是否点赞，此时多线程并发，会产生超赞，或者负赞，
采用事务操作，数据库的锁会导致线程堵塞，响应时间太久
所以将采取redis的原子操作，并且定时将redis的数据同步到数据库
2.点赞数的redis缓存，以及定时同步到数据库
3.能够高效查询我的关注，视频的评论列表