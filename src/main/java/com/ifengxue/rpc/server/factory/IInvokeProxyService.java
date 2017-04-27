package com.ifengxue.rpc.server.factory;

import com.ifengxue.rpc.protocol.ResponseContext;

/**
 * 每个服务都会自动实现这个接口
 * Created by LiuKeFeng on 2017-04-27.
 */
public interface IInvokeProxyService {
    /**
     * 调用服务中的方法
     * @param responseContext
     * @throws Exception
     */
    void invokeProxy(ResponseContext responseContext) throws Exception;
}
