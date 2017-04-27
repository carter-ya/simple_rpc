package com.ifengxue.rpc.client.async;

import com.ifengxue.rpc.client.factory.ClientConfigFactory;
import com.ifengxue.rpc.client.util.RpcInvokeHelper;
import com.ifengxue.rpc.protocol.RequestProtocol;

import java.util.concurrent.Callable;

/**
 * 完全异步的调用
 *
 * Created by LiuKeFeng on 2017-04-27.
 */
public class AsyncInvokeCallable implements Callable<Object> {
    private final long readTimeout = ClientConfigFactory.getInstance().getChannelPoolConfig().getReadTimeout();
    private final Class<?> interfaceClass;
    private final RequestProtocol requestProtocol;

    public AsyncInvokeCallable(Class<?> interfaceClass, RequestProtocol requestProtocol) {
        this.interfaceClass = interfaceClass;
        this.requestProtocol = requestProtocol;
    }

    @Override
    public Object call() throws Exception {
        return RpcInvokeHelper.blockGetResult(requestProtocol.getSessionID(), readTimeout);
    }
}
