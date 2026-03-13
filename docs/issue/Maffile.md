# 我遇到的问题及其解决方案
## 问题1：JDK25转JDK17
### 问题描述： 
我在新建项目后启动springboot，idea一直报错unknown，询问ai以及各个社区帖子都说是lombok兼容问题  
在我尝试最新版lombok后依旧报错，这是便陷入了困境  
后面调具体日志才发现我的电脑同时安装了JDK25和JDK17，而idea一直在用JDK25  
### 解决方案：
在系统变量中设置
```text
JAVA_HOME17  ： 路径
JAVA_HOME25  ： 路径
JAVA_HOME : %JAVA_HOME17%\bin
```
path里面加入%JAVA_HOME%
#### 这样设置后发现idea还是用的JDK25
查询后发现：  
在idea的设置里面将“构建工具”/“maven”里面的两个25改为17  
在“项目结构”/“项目”里的25改为17，“模块”中的25改为17，这里要注意所有模块都要改  
#### 最后重载maven就可以了

## 问题2： MP与SpringBoot的兼容性问题
### 问题描述：
```text
org.springframework.beans.factory.BeanDefinitionStoreException: Invalid bean definition with name 'userMapper' defined in file [...\UserMapper.class]: Invalid value type for attribute 'factoryBeanObjectType': java.lang.String
```
原本使用mp3.5.5，后面升级到3.5.7还是同样报错
### 解决方案：
使用springboot3专属的mp依赖
```xml
<dependency>
    <groupId>com.baomidou</groupId>
    <artifactId>mybatis-plus-spring-boot3-starter</artifactId>
    <version>3.5.7</version>  
</dependency>
```

## 问题3： “-”导致的大小写问题
### 问题描述：  
在拉取别人的代码合并后，发现原本的commonmodules用不了了，必须大写才能用。  
搜索发现Maven 在编译时，会自动把 横杠 - 后面的字母变成大写
### 解决方案：  
将common-modules重构为commonModels，避免后续出现这种情况

## 问题4： kaptcha与springboot3的部分兼容性问题
### 问题描述：
用kaptcha写serviceImpl时自动导入的是javax下的servlet，但使用过程中会报错  
搜索发现Spring Boot 3.x 已经完全迁移到 Jakarta EE 9+ 规范，Servlet API 的包名从 javax.servlet 改成了 jakarta.servlet。  
于是我改用Jakarta，但是唯独imageio.ImageIO;无法导入  
进一步搜索发现Jakarta根本没有这个工具  
### 解决方案：
除import javax.imageio.ImageIO;外都采用Jakarta包

## 问题5： spring-cloud2022与nacos3.1.1的兼容性问题
### 问题描述：
由于之前在本地使用nacos3.1.1与spring-cloud2022可以正常启动，在服务器部署nacos时也选用了3.1.1  
但是部署后发现很多问题：  
- 3.1版本将web与后端分离，一个是8080，一个是8848，两个端口都得做映射  
- 3.1版本鉴权严重，key和keyvalue为必须项，还得传生成token的公钥
- nacos启动需要十多秒
- 在我的电脑上可以访问8080，8848，在服务器也显示nacos8848运行中，但是springboot始终无法加载配置文件，不断返回500
### 解决方案：
最开始想着将就使用3.1.1，于是在启动时设置不进行版本兼容检验，最后发现还是不兼容  
于是乎改用nacos2.1.1，没有3.1.1那么复杂，启动还迅速，很适合我们的版本