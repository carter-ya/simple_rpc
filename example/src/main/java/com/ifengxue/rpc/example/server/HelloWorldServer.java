package com.ifengxue.rpc.example.server;

import com.ifengxue.rpc.server.ServerApp;
import org.apache.log4j.PropertyConfigurator;

/**
 * HelloWorld服务器
 * Created by LiuKeFeng on 2017-04-30.
 */
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
