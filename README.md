# 前言 
将 spring boot 版本改为3.0.13  
将 nacos 版本改为2.4.0  
RabbitMQ 使用3.13  
ElasticSearch 使用7.17.22

### 项目部署网址： http://maffile.top



### 目前已知具体问题：
1. 关注时存在无法关注的情况
2. 个人主页滚动条失效导致预览不了全部已发布的视频



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





