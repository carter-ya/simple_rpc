# simple_rpc
## 实现的功能
1. 服务端Bean参数验证(通过前置拦截器实现[BeanValidateInterceptor](https://github.com/liukefeng2008/simple_rpc/blob/master/src/main/java/com/ifengxue/rpc/server/interceptor/BeanValidateInterceptor.java))
> 开启这个功能需要在rpc_server.xml中配置这个拦截器，并给想要开启类的打注解[BeanValidate](https://github.com/liukefeng2008/simple_rpc/blob/master/src/main/java/com/ifengxue/rpc/server/annotation/BeanValidate.java)
2. 每个接口自动实现回声测试接口[IEchoService](https://github.com/liukefeng2008/simple_rpc/blob/master/src/main/java/com/ifengxue/rpc/protocol/IEchoService.java)
## 即将实现的功能
1. 客户端异步调用服务端
2. 服务端异步处理客户端请求
