package com.ifengxue.rpc.client.async;

import com.ifengxue.rpc.client.factory.ClientConfigFactory;
import com.ifengxue.rpc.client.proxy.IServiceProxy;
import com.ifengxue.rpc.protocol.ReceiveTimeoutException;
import com.ifengxue.rpc.protocol.RequestProtocol;
import com.ifengxue.rpc.protocol.ResponseProtocol;

import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

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
        ResponseProtocol responseProtocol = IServiceProxy.CACHED_RESPONSE_PROTOCOL_MAP.get(requestProtocol.getSessionID()).poll(readTimeout, TimeUnit.MILLISECONDS);
        IServiceProxy.CACHED_RESPONSE_PROTOCOL_MAP.remove(requestProtocol.getSessionID());
        if (responseProtocol == null) {
            throw new ReceiveTimeoutException("客户端[" + requestProtocol.getSessionID() + "]接收服务响应失败，等待时长:" + readTimeout + "ms");
        }

        // 抛出服务端抛出的异常
        if (responseProtocol.getExceptionProtocol() != null) {
            throw responseProtocol.getExceptionProtocol().asRemoteException();
        }
        //正常返回调用结果
        return responseProtocol.getInvokeResult();
    }
}
