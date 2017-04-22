package com.ifengxue.rpc.factory;

import com.ifengxue.rpc.server.filter.Interceptor;

import java.util.Collections;
import java.util.List;

/**
 * 服务端配置工厂
 *
 * Created by LiuKeFeng on 2017-04-21.
 */
public class ServerConfigFactory {
    public static List<Interceptor> getAllInterceptor() {
        return Collections.emptyList();
    }
}
