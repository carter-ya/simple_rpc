package com.ifengxue.rpc.example.client;

import com.ifengxue.rpc.client.proxy.ProxyFactory;
import com.ifengxue.rpc.example.service.IHelloWorld;
import org.apache.log4j.PropertyConfigurator;

/**
 * HelloWorld客户端
 * Created by LiuKeFeng on 2017-04-30.
 */
public class HelloWorldClient {
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
}
