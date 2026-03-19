# 前言
#### 大家可以将自己每次的重大更新写入这里。  
将 spring boot 版本改为3.0.13  
将 nacos 版本改为2.4.0

###### 注：由网关转发的接口使用方式和直接访问子有区别属于正常现象  
###### 这个版本代码照理来说是可以拉取后直接运行，不做任何配置也可以使用接口（网关也要运行）
###### 使用网关访问：localhost:9999/content(或者是video)/你设置的路由
###### 直接访问：localhost:你的端口/你设置的路由 （注：如果设置了拦截器直接访问需要加入白名单或者加请求头）


### nacos访问
```http request
http://121.41.228.22:8848/nacos
```
### redis连接
```json
[
    
    {
        "auth": "123456",
        "host": "121.41.228.22",
        "keys_pattern": "*",
        "name": "tiktok",
        "namespace_separator": ":",
        "port": 6379,
        "ssh_port": 22,
        "timeout_connect": 60000,
        "timeout_execute": 60000
    }
]
```

### mysql连接
```text
hostname: 121.41.228.22
username: remote
password: 123456
port: 3306
```
##### 注：如果一个账号不能多台机器共用请联系管理员
# 开发规范
### 仓库与分支规范：
建公共 Git 仓库（Public）  
约定分支规则：main（主分支，仅存可运行成品）  
dev（开发分支，所有人合并代码到这里）  
feature/xxx（个人功能分支，如feature/user-auth/feature/video-upload）  
严禁直接往 main/dev 推代码，必须提 PR 合并（考核要求清晰的 Git 提交记录）。  
### 提交信息规范：
约定格式：【类型: 描述】如：  
feat: 实现用户注册接口  
fix: 修复登录Token过期问题  
docs: 补充接口文档  
每条提交只做一个功能。
### 文件目录规范：
严格按照文档推荐的包结构统一，所有人本地项目目录一致，避免合并代码时路径冲突
# 结构说明
## 现已将项目具体说明移至docs/design里的md文件

# 使用说明
## 一、commonModules和gateway相关使用说明
### commonModules
- Jwt： 看JwtUtils里的注解  
- Result： 看Result里的注解  
- Captcha： 看captchaConfig里的注解
### gateway
#### Nacos： 
docker启动nacos：
```text
docker run -d --name nacos -e MODE=standalone -e NACOS_AUTH_ENABLE=true -e NACOS_AUTH_IDENTITY_KEY=serverIdentityKey1234567890abcdef -e NACOS_AUTH_IDENTITY_VALUE=serverIdentityValue9876543210fedcba -e NACOS_AUTH_TOKEN=VGhpc0lzTXlDdXN0b21TZWNyZXRLZXkwMTIzNDU2Nzg= -p 8848:8848 -p 9848:9848 nacos/nacos-server:v2.2.1
```
使用公网访问nacos，账号密码都是nacos。  



## 二、mybatis_plus配置参考
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



## 3.knife4j:
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

