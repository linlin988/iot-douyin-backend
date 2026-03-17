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

## 问题6： 阿里云服务器连接问题
### 问题描述：
在使用阿里云workbench连接服务器时，经常用着用着服务器便卡死，无法输入任何命令  
此时调用vnc也无法操作  
重启服务器又要花十来分钟，效率难以启齿
### 解决方案：
经过不断的查帖子，发现很有可能是服务器触发oom或者gc异常  
触发后由于无法向服务器发送任何命令，因此我们选择强制关机（断电）  
开机后可正常运行，此时我们运行docker容器时要加以限制，如-e JVM_XMX=256m ，避免再次死机  

## 问题7： docker部署nacos的一系列问题
### 问题描述：
按照ai给的启动方案：
```shell
docker run -d \
--name nacos \
-p 8848:8848 \
-p 9849:9849 \
-e MODE=standalone \
nacos/nacos-server:2.2.1
```
启动nacos，会一直启动失败，但是第一次启动时启动成功，除此以外再也没有成功过  
我尝试了加入启动时附带token以及各项参数（最开始成功了）
```shell
docker run -d --name nacos -e MODE=standalone -e NACOS_AUTH_ENABLE=true -e NACOS_AUTH_IDENTITY_KEY=serverIdentityKey1234567890abcdef -e NACOS_AUTH_IDENTITY_VALUE=serverIdentityValue9876543210fedcba -e NACOS_AUTH_TOKEN=VGhpc0lzTXlDdXN0b21TZWNyZXRLZXkwMTIzNDU2Nzg= -p 8848:8848 -p 9848:9848 nacos/nacos-server:v2.2.1
```
但最后都已失败告终  
接着问了各种国外的ai大模型，给出的建议都和豆包大差不差，没有解决我的痛点  
### 解决方案：
此后花费了大量时间寻找解决方案，直到看见了这样一篇帖子：
```http request
https://blog.csdn.net/weixin_30598047/article/details/148537243?ops_request_misc=elastic_search_misc&request_id=616308ceb1efdc18410c850f47d04435&biz_id=0&utm_medium=distribute.pc_search_result.none-task-blog-2~all~ElasticSearch~search_v2-1-148537243-null-null.142^v102^pc_search_result_base7&utm_term=%E4%BD%86%E5%86%85%E7%BD%AE%E6%95%B0%E6%8D%AE%E5%BA%93%20Derby%20%E5%90%AF%E5%8A%A8%E8%B6%85%E6%97%B6%20%2F%20%E6%8D%9F%E5%9D%8F%20%E2%86%92%20%E7%9B%B4%E6%8E%A5%E5%B4%A9%E6%BA%83%EF%BC%81&spm=1018.2226.3001.4187
```
在了解完Apache Derby后，终于发现
```text
在Nacos的单机模式（standalone）下，默认就采用了Derby作为其配置信息、服务元数据等内容的存储后端。

嵌入式运行：Derby数据库引擎与你的Nacos应用运行在同一个Java虚拟机（JVM）进程中，无需单独安装和启动数据库服务。
文件系统存储：所有数据库数据（表、索引、事务日志）都存储在derby-data这个文件夹的文件中。这意味着，该文件夹的完整性直接等同于数据库的完整性。
事务与锁机制：为了保证ACID特性，Derby在运行时会持有文件锁（如db.lck）。如果Nacos进程被异常终止，这些锁可能无法正确释放，导致下次启动时认为数据库仍被占用。

```
也就是说我之前不优雅的删除nacos容器（应该不是这个原因，毕竟后面删除也不优雅）或者由于触发了oom或gc异常导致nacos异常停止损坏了Derby的data，而再次运行nacos容器是不会重构derby-data的    
###### 我再仔细看了看ai对于日志的分析，其中有提到derby数据库启动失败/超时，但并未给出正确解决方案，只是不断修改参数并重复删除-启动容器
##### 方案：进入docker/../derby-data文件夹，将他删掉再重启nacos即可

## 问题8：yml配置的读取问题
### 问题描述：
在配置全局过滤器时，我想在nacos配置里面添加白名单
直接使用@value注解发现读取到的列表为空  
### 解决方案：
新建一个配置类，通过"@ConfigurationProperties"注解获取配置
```java
@Component
@ConfigurationProperties(prefix = "gateway.auth")
@Data
public class GatewayAuthProperties {

    private List<String> whiteList = new ArrayList<>();
}
```
最后再在过滤器里面注入即可

## 问题9：网关聚合各个子模块knife4j文档时遇见的问题合集
### 其一：版本问题
有的依赖用的是swagger2，和网关knife版本有冲突  
### 解决方案：
都采用openapi3的knife就好了
### 其二：配置问题
在写单体服务时用的knife基本上不用太在意配置  
但是在使用网关聚合文档时发现配置还是值得深究的  
#### 问题描述：
最开始的配置大概如下（由于没有备份，只能描述大概）：
```yml
spring:
  cloud:
    gateway:
      globalcors:
        cors-configurations:
          '[/**]':
            allowedOrigins: "*"
            allowedMethods:
              - GET
              - POST
              - PUT
              - DELETE
      discovery:
        locator:
          enabled: true
          lower-case-service-id: true
      routes:
        - id: testForMaffile
          uri: lb://testForMaffile
          predicates:
            - Path=/test/**

knife4j:
  gateway:
    enabled: true
    tags-sorter: order
    operations-sorter: order
    strategy: manual
    routes:
      - name: test模块
        service-name: testMaffile
        url: /v3/api-docs?group=default
        context-path: /test
        order: 1
```
这样配置后发现直接访问子项目的doc可以访问到，  
但是使用网关访问便报错：knife4j访问异常，控制台显示500，应该是路由没对
#### 解决过程：
查阅资料发现，在
```yml
predicates:
  - Path=/test/**
```
同级目录添加：
```yml
filters:
  - StripPrefix=1
```
也就是转发时自动去掉第一个路径（这里是test），同时注意knife4j里的context-path也一定要带上，这个是自动补路由前缀的  
目前利用网关访问子服务的knife文档已经成功了  
#### 可惜不久又发现一个问题：  
使用原接口访问子服务时会提示未登录（不管有没有token）  
很明显网关转发服务出问题了。  
可是上面改的应该是接口文档的路径才对啊？带着这个问题又去找了一些帖子，发现其中的端倪  
```yml
predicates: *
```
断言路由，该路由转发到对应子服务，
```yml
filters:
  - StripPrefix=1
```
去掉首路由后只剩后面部分路由，子服务根据剩下的路由找到对应方法，而knife4j的文档路由配置中会用context-path补全对应的路由来找到doc文档  
### 解决方案：
直白说来，就是网关服务的地址是由：服务器地址+端口+服务断言+方法路由  
之前api没有按照这种格式来写为什么也能正常使用？  
因为之前的断言路径（"/test/**"）与模块路径（"/test/**"）相同，正好StripPrefix又没有设置（默认为0）  
冥冥中的巧合（或许是设计师巧妙的设计）让一开始没有报错  
现在将StripPrefix设置为1后，断言路径（/test/**）被切去，拿接口
```text
/test/user
```
来举例，SP为0时正常转发为/test/user，可以正常访问    
为1时转发/user,返回404.需要使用/test/test/user才能访问  
因此在设计网关路由时也需要一些巧思，如断言为user表示用户模块，用户类的统一路由则不再使用user，可以改为login  
则此时的api为：服务器地址+端口+/user/login  
网关会转发：服务器地址+端口+/login，这样便完美契合




