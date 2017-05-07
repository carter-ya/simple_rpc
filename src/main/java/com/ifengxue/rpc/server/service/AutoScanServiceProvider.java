package com.ifengxue.rpc.server.service;

import com.ifengxue.rpc.protocol.annotation.RpcService;
import com.ifengxue.rpc.server.factory.JavassistProxyFactory;
import com.ifengxue.rpc.server.util.ClassLoadUtil;
import com.ifengxue.rpc.server.util.ClassUtil;
import org.apache.commons.collections.map.HashedMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * 自动扫描jar包，class文件来查找对外提供服务的接口
 *
 * Created by LiuKeFeng on 2017-05-06.
 */
public class AutoScanServiceProvider implements IServiceProvider {
    private Logger logger = LoggerFactory.getLogger(getClass());
    private Map<String, Object> allServiceMap = new HashedMap();
    private Map<String, Class<?>> allProxyClassMap = new HashMap<>();

    public AutoScanServiceProvider() {
        String classpath = System.getProperty("rpc.service.classpath");
        String[] jarPaths = System.getProperty("rpc.service.jarpaths", "").split(",");
        autoScan(classpath, jarPaths);
    }

    @Override
    public Map<String, Object> findAllServices() {
        return Collections.unmodifiableMap(allServiceMap);
    }

    @Override
    public Map<String, Class<?>> findAllProxyClass() {
        return Collections.unmodifiableMap(allProxyClassMap);
    }

    /**
     * 自动扫描使用{@link RpcService}注解的类
     * @param classpath
     * @param jarPaths
     */
    private void autoScan(String classpath, String...jarPaths) {
        List<Class<?>> classListInClasspath = Optional.ofNullable(classpath)
                .map(ClassLoadUtil::loadClassFromClasspath)
                .orElse(Collections.emptyList());
        List<Class<?>> classListInJarPaths = ClassLoadUtil.loadClassFromJarPaths(jarPaths);
        List<Class<?>> rpcServiceClassListInClasspath = ClassUtil.findAllClassWithAnnotatedBy(classListInClasspath, RpcService.class);
        List<Class<?>> rpcServiceClassListInJarPaths = ClassUtil.findAllClassWithAnnotatedBy(classListInJarPaths, RpcService.class);
        List<Class<?>> rpcServiceClassList = new ArrayList<>(rpcServiceClassListInClasspath.size() + rpcServiceClassListInJarPaths.size());
        rpcServiceClassList.addAll(rpcServiceClassListInClasspath);
        rpcServiceClassList.addAll(rpcServiceClassListInJarPaths);

        for (Class<?> rpcServiceClass : rpcServiceClassList) {
            Object proxyInstance = JavassistProxyFactory.getProxyInstance(rpcServiceClass);
            Class<?> proxyClass = proxyInstance.getClass();
            RpcService[] rpcServices = proxyClass.getAnnotationsByType(RpcService.class);
            for (RpcService rpcService : rpcServices) {
                allProxyClassMap.put(rpcService.value().getName(), proxyClass);
                allServiceMap.put(rpcService.value().getName(), proxyInstance);
                logger.info("Service:{}", rpcService.value().getName());
            }
        }
    }
}
