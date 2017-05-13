package com.ifengxue.rpc.example.server;

import com.ifengxue.rpc.server.ServerApp;
import com.ifengxue.rpc.server.factory.ServerConfigFactory;
import org.apache.log4j.PropertyConfigurator;

import java.io.File;

/**
 * 同时启动json-rpc
 * Created by LiuKeFeng on 2017-05-10.
 */
public class UserAndCityAndSONRpcServer {
    public static void main(String[] args) {
        char pathSeparator = File.separatorChar;
        String baseDir = System.getProperty("user.dir") + pathSeparator + "example";
        String targetFolder = baseDir + pathSeparator + "target";
        /**
         * 设置class文件路径
         */
        System.setProperty("rpc.service.classpath", targetFolder);
        PropertyConfigurator.configure("example/conf/log4j_server.properties");
        ServerApp.main(new String[] {"--conf:example/conf/user_and_city_and_json_rpc_server.xml"});
        ServerConfigFactory configFactory = ServerConfigFactory.getInstance();
        System.out.println("json-rpc 已启动在http://" +
                configFactory.getJSONRpcBindHost() + ":" + configFactory.getJSONRpcBindPort() + ".请打开浏览器观察效果。");
    }
}
