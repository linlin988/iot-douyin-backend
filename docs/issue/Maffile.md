# 我遇到的问题及其解决方案
## 问题1：JDK25转JDK17
### 问题描述： 
我在新建项目后启动springboot，idea一直报错unknown，询问ai以及各个社区帖子都说是lombok兼容问题  
在我尝试最新版lombok后依旧报错，这是便陷入了困境  
后面调具体日志才发现我的电脑同时安装了JDK25和JDK17，而idea一直在用JDK25  
### 解决方案：
在系统变量中设置
```
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