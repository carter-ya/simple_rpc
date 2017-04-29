package com.ifengxue.rpc.client.async;

import com.ifengxue.rpc.protocol.ResponseProtocol;

/**
 * 异步回调
 *
 * Created by LiuKeFeng on 2017-04-28.
 */
public interface IAsyncCallback {
    /**
     * 异步回调
     * @param responseProtocol
     */
    void callback(ResponseProtocol responseProtocol);

    /**
     * 设置异步调用方式
     * @param asyncMethod
     */
    void setAsyncMethod(AsyncMethod asyncMethod);

    /**
     * 取异步调用方式
     * @return
     */
    AsyncMethod getAsyncMethod();

    /**
     * 设置sessionID
     * @param sessionID
     */
    void setSessionID(String sessionID);

    /**
     * 获取sessionID
     * @return
     */
    String getSessionID();
}
