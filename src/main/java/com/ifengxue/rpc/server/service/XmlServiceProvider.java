package com.ifengxue.rpc.server.service;

import com.ifengxue.rpc.protocol.annotation.RpcService;
import com.ifengxue.rpc.server.factory.JavassistProxyFactory;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * xml服务提供者
 *
 * Created by LiuKeFeng on 2017-04-23.
 */
public class XmlServiceProvider implements IServiceProvider {
    private final Map<String, Object> allServiceMap;
    private final Map<String, Class<?>> allProxyClassMap;
    public XmlServiceProvider(List<String> classNameList) {
        allServiceMap = new HashMap<>();
        allProxyClassMap = new HashMap<>();
        try {
            for (String className : classNameList) {
                Object proxyInstance = JavassistProxyFactory.getProxyInstance(Class.forName(className));
                Class<?> proxyClass = proxyInstance.getClass();
                RpcService[] rpcServices = proxyClass.getAnnotationsByType(RpcService.class);
                for (RpcService rpcService : rpcServices) {
                    allServiceMap.put(rpcService.value().getName(), proxyInstance);
                    allProxyClassMap.put(rpcService.value().getName(), proxyClass);
                }
            }
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }
    @Override
    public Map<String, Object> findAllServices() {
        return Collections.unmodifiableMap(allServiceMap);
    }

    @Override
    public Map<String, Class<?>> findAllProxyClass() {
        return Collections.unmodifiableMap(allProxyClassMap);
    }
}
