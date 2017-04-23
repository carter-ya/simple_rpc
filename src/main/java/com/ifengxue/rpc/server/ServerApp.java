package com.ifengxue.rpc.server;

import com.ifengxue.rpc.factory.ServerConfigFactory;
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
        ServerConfigFactory.initConfigFactory(paramMap.get("conf"));
        IRpcServer rpcServer = new SimpleRpcServer();
        rpcServer.start();
    }
}
