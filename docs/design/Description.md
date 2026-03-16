# 项目关键部分说明



## 项目结构

### 公共模块（common-modules）  
可在子模块使用：
- Jwt
- Captcha
- Result
- Entity
### 非代码文件夹（docs）  
design文件夹，包含个人设计以及个人解决方案，便于小组互相学习。


### 网关服务（gateway-service）
- 进行服务转发，前端调用格式为：公网:9999/**
- 负载均衡


### 个人测试模块（testForMaffile）
用于测试公共模块以及网关的基本使用