package com.ifengxue.rpc.server.register;

import org.dom4j.Element;

import java.io.Closeable;
import java.io.IOException;

/**
 * 服务端的注册中心，用于发布服务
 *
 * Created by LiuKeFeng on 2017-04-26.
 */
public interface IRegisterCenter extends Closeable {
    /**
     * 注册服务
     * @param serviceName 服务名称
     * @param host 主机地址
     * @param port 开放端口
     */
    void register(String serviceName, String host, int port);

    /**
     * 取消注册
     */
    void unregister();

    /**
     * 初始化注册中心
     * @param registerElement
     */
    void init(Element registerElement);

    @Override
    void close();
}
