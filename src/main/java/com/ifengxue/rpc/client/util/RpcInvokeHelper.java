package com.ifengxue.rpc.client.util;

import com.ifengxue.rpc.client.RpcContext;
import com.ifengxue.rpc.protocol.ReceiveTimeoutException;
import com.ifengxue.rpc.protocol.ResponseProtocol;

import java.rmi.RemoteException;
import java.util.concurrent.TimeUnit;

/**
 * Created by LiuKeFeng on 2017-04-27.
 */
public class RpcInvokeHelper {
    /**
     * 锁住当前线程直到有结果
     * @param sessionID 请求sessionID
     * @param readTimeout 等待超时时间
     * @return
     * @throws RemoteException
     * @throws InterruptedException
     */
    public static Object blockGetResult(String sessionID, long readTimeout) throws RemoteException, InterruptedException {
        ResponseProtocol responseProtocol = RpcContext.CACHED_RESPONSE_PROTOCOL_MAP.get(sessionID).poll(readTimeout, TimeUnit.MILLISECONDS);
        RpcContext.CACHED_RESPONSE_PROTOCOL_MAP.remove(sessionID);
        if (responseProtocol == null) {
            throw new ReceiveTimeoutException("客户端[" + sessionID + "]接收服务响应失败，等待时长:" + readTimeout + "ms");
        }

        // 抛出服务端抛出的异常
        if (responseProtocol.getExceptionProtocol() != null) {
            throw responseProtocol.getExceptionProtocol().asRemoteException();
        }
        //正常返回调用结果
        return responseProtocol.getInvokeResult();
    }
}
