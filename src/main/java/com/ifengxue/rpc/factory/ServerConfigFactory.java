package com.ifengxue.rpc.factory;

import com.ifengxue.rpc.server.filter.Interceptor;
import com.ifengxue.rpc.server.service.IServiceProvider;
import com.ifengxue.rpc.server.service.SimpleServiceProvider;

import java.util.Collections;
import java.util.List;

/**
 * 服务端配置工厂
 *
 * Created by LiuKeFeng on 2017-04-21.
 */
public class ServerConfigFactory {
    private static final ServerConfigFactory INSTANCE = new ServerConfigFactory();
    private ServerConfigFactory() {}
    public static ServerConfigFactory getInstance() {
        return INSTANCE;
    }
    public List<Interceptor> getAllInterceptor() {
        return Collections.emptyList();
    }

    public IServiceProvider getServiceProvider() {
        //TODO:指定配置文件地址
        return new SimpleServiceProvider("conf/service.txt");
    }
}
