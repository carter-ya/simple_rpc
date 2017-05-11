package com.ifengxue.rpc.server;

import com.ifengxue.rpc.server.factory.ServerConfigFactory;
import com.ifengxue.rpc.server.handle.ServerSignalHandler;
import com.ifengxue.rpc.util.Param;
import com.ifengxue.rpc.util.ParamHelper;

import java.util.Map;

/**
 * 服务端启动器
 *
 * Created by LiuKeFeng on 2017-04-23.
 */
public class ServerApp {
    public static void main(String[] args) {
        Map<String, String> paramMap = ParamHelper.parse(args,
                new Param("conf", "conf/rpc_server.xml", false, "服务端配置文件路径"));
        ServerConfigFactory.initConfig(paramMap.get("conf"));
        IRpcServer rpcServer = new SimpleRpcServer();
        rpcServer.start();

        //响应退出信号
        sun.misc.Signal.handle(new sun.misc.Signal("TERM"), new ServerSignalHandler(rpcServer));
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            ServerSignalHandler serverSignalHandler = new ServerSignalHandler(rpcServer);
            serverSignalHandler.handle(new sun.misc.Signal("TERM"));
        }));
    }
}
