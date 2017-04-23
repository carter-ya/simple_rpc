package com.ifengxue.rpc.server;

/**
 * RPC服务器接口
 *
 * Created by LiuKeFeng on 2017-04-23.
 */
public interface IRpcServer {
    /**
     * 启动RPC服务器
     */
    void start();

    /**
     * 停止RPC服务器
     */
    void close();
}
