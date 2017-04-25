package com.ifengxue.rpc.client.proxy;

import com.ifengxue.rpc.protocol.ReceiveTimeoutException;
import com.ifengxue.rpc.client.pool.IChannelPool;
import com.ifengxue.rpc.factory.ClientConfigFactory;
import com.ifengxue.rpc.protocol.RequestProtocol;
import com.ifengxue.rpc.protocol.ResponseProtocol;
import io.netty.channel.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;

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
        Channel channel = null;
        Exception exception = null;
        try {
            channel = channelPool.borrowObject(serviceNodeName);
            //必须放在前面，否则会出现客户端和服务端并发访问map的情况
            CACHED_RESPONSE_PROTOCOL_MAP.put(requestProtocol.getSessionID(), new ArrayBlockingQueue<>(1));
            channel.writeAndFlush(requestProtocol).sync();
        } catch (NoSuchElementException e) {
            CACHED_RESPONSE_PROTOCOL_MAP.remove(requestProtocol.getSessionID());
            logger.error("连接池中没有[" + serviceNodeName + "]的可用连接了:" + e.getMessage());
            exception = e;
        } catch (Exception e) {
            CACHED_RESPONSE_PROTOCOL_MAP.remove(requestProtocol.getSessionID());
            logger.error("调用服务[" + serviceNodeName + "]失败:" + e.getMessage());
            exception = e;
        } finally {
            Optional.ofNullable(channel).ifPresent(ch -> channelPool.returnObject(serviceNodeName, ch));
        }

        // 抛出本地异常
        if (exception != null) {
            throw exception;
        }

        ResponseProtocol responseProtocol = CACHED_RESPONSE_PROTOCOL_MAP.get(requestProtocol.getSessionID()).poll(readTimeout, TimeUnit.MILLISECONDS);
        CACHED_RESPONSE_PROTOCOL_MAP.remove(requestProtocol.getSessionID());
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
