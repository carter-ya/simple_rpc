package com.ifengxue.rpc.server.service;

import java.util.Map;

/**
 * 服务提供者
 *
 * Created by LiuKeFeng on 2017-04-23.
 */
public interface IServiceProvider {
    /**
     * 返回所有声称对外提供服务的对象
     * @return
     */
    Map<String, Object> findAllServices();

    /**
     * 返回所有声称对外提供服务的代理Class对象
     * @return
     */
    Map<String, Class<?>> findAllProxyClass();
}
