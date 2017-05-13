# simple rpc
这个项目初衷是为了初步了解rpc的设计细节，然后试着升级我们公司内部的rpc框架。
## 参考
- [一个轻量级分布式RPC框架](https://zhuanlan.zhihu.com/p/26017195)
- [轻量级分布式 RPC 框架](https://my.oschina.net/huangyong/blog/361751)
- 公司内部使用`SCF`框架
## 功能
1. `Bean`验证[BeanValidateInterceptor](https://github.com/liukefeng2008/simple_rpc/blob/master/src/main/java/com/ifengxue/rpc/server/interceptor/BeanValidateInterceptor.java)
2. 发布服务到`zookeeper`中[客户端](https://github.com/liukefeng2008/simple_rpc/blob/master/src/main/java/com/ifengxue/rpc/client/register/ZookeeperRegisterCenter.java),[服务端](https://github.com/liukefeng2008/simple_rpc/blob/master/src/main/java/com/ifengxue/rpc/server/register/ZookeeperRegisterCenter.java)
3. 客户端支持异步调用[AsyncRpcInvoker](https://github.com/liukefeng2008/simple_rpc/blob/master/src/main/java/com/ifengxue/rpc/client/async/AsyncRpcInvoker.java)
> 这里没有采用配置文件来声明某某方法异步调用，而是通过代码来实现。
```
    IUserService userService = ProxyFactory.create(IUserService.class, "UserService");
    //同步调用
    User user = userService.getUserByID(123L);
    //异步调用：需要返回值
    Future<User> future = AsyncRpcInvoker.asyncForResult(() -> userService.getUserByID(123L));
    //异步调用：不需要返回值
    AsyncRpcInvoker.asyncForNoneResult(() -> userService.addUser(user));
```
4. 服务端支持回声测试[IEchoService](https://github.com/liukefeng2008/simple_rpc/blob/master/src/main/java/com/ifengxue/rpc/protocol/IEchoService.java)
```
    IUserService userService = ProxyFactory.create(IUserService.class, "UserService");
    IEchoService echoService = (IEchoService) userService;
    System.out.println("echo:" + echoService.$echo("Hello World"));
```
5. 部分实现了[json-rpc](http://www.jsonrpc.org/specification)规范[SimpleJSONRequestDispatcher](https://github.com/liukefeng2008/simple_rpc/blob/master/src/main/java/com/ifengxue/rpc/server/json/SimpleJSONRequestDispatcher.java)
> 浏览器打开http://localhost:9092（自己配置的域名+端口，默认是localhost:9092)可以看到所有对外公开的json-rpc服务。
通过http://localhost:9092/XXService可以看到所有对外公开的方法和需要的参数

 - 注解[RpcService](https://github.com/liukefeng2008/simple_rpc/blob/master/src/main/java/com/ifengxue/rpc/protocol/annotation/RpcService.java)声明对外的访问路径
 - 注解[HttpMethod](https://github.com/liukefeng2008/simple_rpc/blob/master/src/main/java/com/ifengxue/rpc/protocol/annotation/HttpMethod.java)声明对外的方法名称和功能
 - 注解[Param](https://github.com/liukefeng2008/simple_rpc/blob/master/src/main/java/com/ifengxue/rpc/protocol/annotation/Param.java)声明对外的方法的参数名称
 
 ## 例子
 ### Hello World
 1. 编写接口[IHelloWorld](https://github.com/liukefeng2008/simple_rpc/blob/master/example/src/main/java/com/ifengxue/rpc/example/service/IHelloWorld.java)
 2. 编写实现[HelloWorld](https://github.com/liukefeng2008/simple_rpc/blob/master/example/src/main/java/com/ifengxue/rpc/example/impl/HelloWorld.java)
 > 这里唯一需要注意的是实现上标记了注解[RpcService](https://github.com/liukefeng2008/simple_rpc/blob/master/src/main/java/com/ifengxue/rpc/protocol/annotation/RpcService.java)，
 `value`属性标记的是对外暴露哪个服务，因为一个实现类可能不止实现了一个接口。该注解可以重复标记在一个类上。
 3. 编写服务端配置文件[hello_world_server.xml](https://github.com/liukefeng2008/simple_rpc/blob/master/example/conf/hello_world_server.xml)
 ```
 <?xml version="1.0" encoding="UTF-8"?>
 <rpc-server>
     <!-- rpc服务名称为HelloWorldService;监听localhost的9091端口 -->
     <server name="HelloWorldService" host="localhost" port="9091" />
     <!-- 对外提供服务的实现类列表 -->
     <services>
         <!-- 实现类的完整名称:包名.类名 -->
         <service class="com.ifengxue.rpc.example.impl.HelloWorld" />
     </services>
 </rpc-server>
 ```
 4. 启动服务端[HelloWorldServer](https://github.com/liukefeng2008/simple_rpc/blob/master/example/src/main/java/com/ifengxue/rpc/example/server/HelloWorldServer.java)
 ```
 public class HelloWorldServer {
     public static void main(String[] args) {
         /**
          * 服务器的配置文件
          */
         String rpcConfigPath = "example/conf/hello_world_server.xml";
         /**
          * 配置log4j
          */
         PropertyConfigurator.configure("example/conf/log4j_server.properties");
         /**
          * 启动服务器
          */
         ServerApp.main(new String[] {"--conf:" + rpcConfigPath});
     }
 }
 ```
 5. 编写客户端配置文件[hello_world_client.xml](https://github.com/liukefeng2008/simple_rpc/blob/master/example/conf/hello_world_client.xml)
 ```
 <?xml version="1.0" encoding="UTF-8"?>
 <rpc-client>
     <!-- 服务生产者注册中心：xml配置注册中心 -->
     <register-center class="com.ifengxue.rpc.client.register.XmlRegisterCenter">
         <!-- 配置HelloWorldService的服务地址 -->
         <service-nodes>
             <service-node serviceName="HelloWorldService" host="localhost" port="9091"  />
         </service-nodes>
     </register-center>
 </rpc-client>
 ```
 6. 客户端调用服务端发布的方法[HelloWorldClient](https://github.com/liukefeng2008/simple_rpc/blob/master/example/src/main/java/com/ifengxue/rpc/example/client/HelloWorldClient.java)
 ```
 public static void main(String[] args) {
         /**
          * 配置log4j
          */
         PropertyConfigurator.configure("example/conf/log4j_client.properties");
         /**
          * 客户端配置文件
          */
         String rpcConfigPath = "example/conf/hello_world_client.xml";
         /**
          * 初始化客户端配置文件
          */
         ProxyFactory.initConfig(rpcConfigPath);
         /**
          * 创建服务接口代理
          */
         IHelloWorld helloWorld = ProxyFactory.create(IHelloWorld.class, "HelloWorldService");
         /**
          * 调用服务提供的方法
          */
         helloWorld.sayHelloWorld();
         System.exit(0);
     }
```
### 回声测试[EchoServiceClient](https://github.com/liukefeng2008/simple_rpc/blob/master/example/src/main/java/com/ifengxue/rpc/example/client/EchoServiceClient.java)
### 异步调用[AsyncUserClient](https://github.com/liukefeng2008/simple_rpc/blob/master/example/src/main/java/com/ifengxue/rpc/example/client/AsyncUserClient.java)
### json-rpc服务器[UserAndCityAndSONRpcServer](https://github.com/liukefeng2008/simple_rpc/blob/master/example/src/main/java/com/ifengxue/rpc/example/server/UserAndCityAndSONRpcServer.java)