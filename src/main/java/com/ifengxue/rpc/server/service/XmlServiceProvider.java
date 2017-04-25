package com.ifengxue.rpc.server.service;

import com.ifengxue.rpc.protocol.annotation.RpcService;

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
    public XmlServiceProvider(List<String> classNameList) {
        allServiceMap = new HashMap<>();
        try {
            for (String className : classNameList) {
                Class<?> clazz = Class.forName(className);
                RpcService[] rpcServices = clazz.getAnnotationsByType(RpcService.class);
                Object clazzObject = clazz.newInstance();
                for (RpcService rpcService : rpcServices) {
                    allServiceMap.put(rpcService.value().getName(), clazzObject);
                }
            }
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }
    @Override
    public Map<String, Object> findAllServices() {
        return allServiceMap;
    }
}
