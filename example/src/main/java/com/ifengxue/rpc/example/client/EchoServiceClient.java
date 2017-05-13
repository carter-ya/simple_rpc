package com.ifengxue.rpc.example.client;

import com.ifengxue.rpc.client.proxy.ProxyFactory;
import com.ifengxue.rpc.example.service.IUserService;
import com.ifengxue.rpc.protocol.IEchoService;
import org.apache.log4j.PropertyConfigurator;

/**
 * 回声测试客户端
 *
 * Created by LiuKeFeng on 2017-05-13.
 */
public class EchoServiceClient {
    public static void main(String[] args) {
        PropertyConfigurator.configure("example/conf/log4j_client.properties");
        ProxyFactory.initConfig("example/conf/user_and_city_client.xml");

        String serviceName = "UserAndCityService";
        IUserService userService = ProxyFactory.create(IUserService.class, serviceName);

        /**
         * 进行回声测试，只需要将代理对象强转为{@link IEchoService}即可
         */
        IEchoService echoService = (IEchoService) userService;
        String echo = echoService.$echo("Hello World!");
        System.out.println("echo:" + echo);
        System.exit(0);
    }
}
