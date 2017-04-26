package com.ifengxue.rpc.server.handle;

import com.ifengxue.rpc.protocol.ResponseContext;

/**
 * 调用处理接口
 *
 * Created by LiuKeFeng on 2017-04-26.
 */
public interface IInvokeHandler {
    /**
     * 方法调用处理 <br>
     * 返回值设置{@link ResponseContext#setInvokeResult(Object)} <br>
     * 异常返回值设置{@link ResponseContext#setResponseError(Throwable)}
     * @param responseContext 响应上下文
     * @throws Exception
     */
    void methodInvoke(ResponseContext responseContext) throws Exception;
}
