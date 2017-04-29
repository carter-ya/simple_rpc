package com.ifengxue.rpc.client.proxy;

import com.ifengxue.rpc.client.RpcContext;
import com.ifengxue.rpc.client.async.AsyncConsts;
import com.ifengxue.rpc.client.async.AsyncRpcFuture;
import com.ifengxue.rpc.client.util.RpcInvokeHelper;
import com.ifengxue.rpc.client.pool.IChannelPool;
import com.ifengxue.rpc.client.factory.ClientConfigFactory;
import com.ifengxue.rpc.protocol.RequestProtocol;
import io.netty.channel.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.*;

/**
 * 默认的服务代理实现
 *
 * Created by LiuKeFeng on 2017-04-22.
 */
public class SimpleServiceProxy extends AbstractServiceProxy {
    private static final long serialVersionUID = 6567739050249606630L;
    private Logger logger = LoggerFactory.getLogger(getClass());
    private final IChannelPool channelPool = ClientConfigFactory.getInstance().getChannelPool();
    private final long readTimeout = ClientConfigFactory.getInstance().getChannelPoolConfig().getReadTimeout();
    public SimpleServiceProxy(Class<?> interfaceClass, String serviceNodeName) {
        super(interfaceClass, serviceNodeName);
    }

    @Override
    protected Object invoke(Object proxy, RequestProtocol requestProtocol) throws Throwable {
        Exception exception = sendRequestProtocol(requestProtocol);
        // 抛出本地异常
        if (exception != null) {
            throw exception;
        }

        // 执行异步调用
        AsyncRpcFuture<?> callback = AsyncConsts.ASYNC_RPC_FUTURE_THREAD_LOCAL.get();
        if (callback != null) {
            callback.setSessionID(requestProtocol.getSessionID());
            AsyncConsts.ASYNC_RPC_FUTURE_THREAD_LOCAL.remove();
            AsyncConsts.ASYNC_RPC_FUTURE_SESSION_ID_MAP
                    .put(requestProtocol.getSessionID(), callback);
            return null;
        }
        return RpcInvokeHelper.blockGetResult(requestProtocol.getSessionID(), readTimeout);
    }

    /**
     * 发送请求
     * @param requestProtocol
     * @return 返回异常
     */
    private Exception sendRequestProtocol(RequestProtocol requestProtocol) {
        Channel channel = null;
        Exception exception = null;
        try {
            channel = channelPool.borrowObject(serviceNodeName);
            //必须放在前面，否则会出现客户端和服务端并发访问map的情况
            RpcContext.CACHED_RESPONSE_PROTOCOL_MAP.put(requestProtocol.getSessionID(), new ArrayBlockingQueue<>(1));
            channel.writeAndFlush(requestProtocol).sync();
        } catch (NoSuchElementException e) {
            RpcContext.CACHED_RESPONSE_PROTOCOL_MAP.remove(requestProtocol.getSessionID());
            logger.error("连接池中没有[" + serviceNodeName + "]的可用连接了:" + e.getMessage());
            exception = e;
        } catch (Exception e) {
            RpcContext.CACHED_RESPONSE_PROTOCOL_MAP.remove(requestProtocol.getSessionID());
            logger.error("调用服务[" + serviceNodeName + "]失败:" + e.getMessage());
            exception = e;
        } finally {
            Optional.ofNullable(channel).ifPresent(ch -> channelPool.returnObject(serviceNodeName, ch));
        }
        return exception;
    }
}
