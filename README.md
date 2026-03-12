# 前言
#### 大家可以将自己每次的重大更新写入这里。  
将spring boot 版本改为3.0.13  

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
## commonModules和gateway相关使用说明
### commonModules
Jwt： 看JwtUtils里的注解  
Result： 看Result里的注解
### gateway
#### Nacos： 
```text
1.在nacos官网下载3.1.1版本的nacos
2.将conf目录下的application.properties里的secret.key赋值VGhpc0lzTXlDdXN0b21TZWNyZXRLZXkwMTIzNDU2Nzg=  
3.在conf目录下的application.properties里的最下面新增：
nacos.core.auth.enabled=true
nacos.core.auth.server.identity.key=nacos
nacos.core.auth.server.identity.value=nacos
4.将bin目录下的set MODE改为"standalone"
现在就可以启动了，注意第三点的nacos就是你的本地登录账号密码
```
启动成功后在下面的路径获取配置压缩包，解压后导入nacos就能统一配置
```text
src/main/java/com/iot/gatewayservice/config/nacos/nacos_config_export_20260312223235.zip
```
本地有新增配置还请上传代码时附带一起提交，如果有更好的统一配置方法欢迎交流  
接下来只需要在网关路由下加入你的子服务名称，路径，断言即可被网关统一转发。  
##### 注：由于没有测试，目前不知道有没有bug
