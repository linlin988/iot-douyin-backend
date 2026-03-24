# 项目关键部分说明



## mybatis_plus配置参考
### 服务器基础配置（可选，按需调整）

#### 连接池配置（默认HikariCP，性能最优）
hikari:
maximum-pool-size: 10 # 最大连接数
minimum-idle: 5 # 最小空闲连接数
connection-timeout: 30000 # 连接超时时间（ms）

### MyBatis-Plus 核心配置
mybatis-plus:
#### 实体类扫描包（替换为你项目的entity包路径）
type-aliases-package: com.iot.tiktok.entity
#### Mapper XML文件位置（若需要自定义SQL时配置）
mapper-locations: classpath:mapper/**/*.xml
#### 全局配置
global-config:
db-config:
#### 主键生成策略：ASSIGN_ID（雪花算法），与实体类@TableId(type = IdType.ASSIGN_ID)对应
id-type: ASSIGN_ID
#### 表名前缀（可选，若实体类名不含T前缀可配置，如表t_user对应User实体）
#### table-prefix: t_
#### 配置项
configuration:
开启驼峰命名自动转换（数据库下划线→实体类驼峰，如user_id→userId）
map-underscore-to-camel-case: true
关闭MyBatis二级缓存
cache-enabled: false
控制台打印SQL日志
log-impl: org.apache.ibatis.logging.stdout.StdOutImpl
查询时列名为空返回null，而非空字符串
call-setters-on-nulls: true
JDBC类型为空时的默认值
jdbc-type-for-null: NULL

### MyBatis-Plus 插件配置（分页插件核心）
mybatis-plus:
configuration:
plugin:
pagination:
#### 开启分页插件
#### 分页查询使用方法
#### 1.分页查询，new Page()的两个参数分别是：页码、每页大小
Page<User> p = userService.page(new Page<>(2, 2));
#### 2.获取分页数据，p.getRecords()方法返回当前页数据
List<User> records = p.getRecords();
#### 3.分页参数，Page.of()方法返回一个Page对象，包含页码和每页大小
Page<User> page = Page.of(pageNo, pageSize);
#### 4.排序参数, 通过OrderItem来指定

### 实体类使用方法
1.前端传入一个pageQuery对象，包含页码、每页大小、排序字段、是否升序
如果超出这些属性，定义一个Query对象继承PageQuery，并添加自定义属性
2.PageQuery对象里有方法可以转换为MybatisPlus的Page对象，方便传入Service层进行分页查询
3.查询以后，结果封装在Page对象里，包含分页数据、总条数、总页数等信息
4.Page对象里有方法可以转换为PageDTO<VO>,方便返回给前端



## knife4j:
### 接口文档的配置，
### 引入依赖，写入yml后即可通过注解生成接口文档
##### 开启增强模式
enable: true
##### 标题
title: iot-douyin-backend接口文档
##### 描述
description: 后端接口文档
##### 联系人
contact: 开发者
##### 版本号
version: 1.0
