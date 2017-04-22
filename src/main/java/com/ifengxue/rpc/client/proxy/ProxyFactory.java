package com.ifengxue.rpc.client.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 客户端服务代理工厂
 *
 * Created by LiuKeFeng on 2017-04-22.
 */
public final class ProxyFactory {
    private static final Map<Class<?>, Object> CACHED_SERVICE_PROXY_MAP = new ConcurrentHashMap<>();

    /**
     * 创建服务代理
     * @param serviceInterface 被代理的服务接口
     * @param serviceNodeName 提供服务的节点名称
     * @param <T>
     * @return
     */
    public static <T> T create(Class<T> serviceInterface, String serviceNodeName) {
        return (T) CACHED_SERVICE_PROXY_MAP.computeIfAbsent(serviceInterface, clazz -> {
            InvocationHandler handler = new SimpleServiceProxy(clazz, serviceNodeName);
            return Proxy.newProxyInstance(ProxyFactory.class.getClassLoader(), new Class<?>[]{serviceInterface}, handler);
        });
    }
}
