package com.ifengxue.rpc.example.server;

import com.ifengxue.rpc.server.ServerApp;
import org.apache.log4j.PropertyConfigurator;

import java.io.File;

/**
 * 使用zookeeper作为注册中心，并发布服务到zookeeper中
 *
 * Created by LiuKeFeng on 2017-05-10.
 */
public class UserAndCityAndZookeeperServer {
    public static void main(String[] args) {
        char pathSeparator = File.separatorChar;
        String baseDir = System.getProperty("user.dir") + pathSeparator + "example";
        String targetFolder = baseDir + pathSeparator + "target";
        /**
         * 设置class文件路径
         */
        System.setProperty("rpc.service.classpath", targetFolder);
        PropertyConfigurator.configure("example/conf/log4j_server.properties");
        ServerApp.main(new String[] {"--conf:example/conf/user_and_city_and_zookeeper_server.xml"});
    }
}
