package com.ifengxue.rpc.client.proxy;

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

/**
 * 默认的服务代理实现
 *
 * Created by LiuKeFeng on 2017-04-22.
 */
public class SimpleServiceProxy extends AbstractServiceProxy {
    private static final long serialVersionUID = 6567739050249606630L;
    private Logger logger = LoggerFactory.getLogger(getClass());
    private final IChannelPool channelPool = ClientConfigFactory.getInstance().getChannelPool();
    public SimpleServiceProxy(Class<?> interfaceClass, String serviceNodeName) {
        super(interfaceClass, serviceNodeName);
    }

    @Override
    protected Object invoke(Object proxy, RequestProtocol requestProtocol) throws Throwable {
        Channel channel = null;
        Throwable localError = null;
        try {
            channel = channelPool.borrowObject(serviceNodeName);
            //必须放在前面，否则会出现客户端和服务端并发访问map的情况
            CACHED_RESPONSE_PROTOCOL_MAP.put(requestProtocol.getSessionID(), new ArrayBlockingQueue<>(1));
            channel.writeAndFlush(requestProtocol).sync();
        } catch (NoSuchElementException e) {
            CACHED_RESPONSE_PROTOCOL_MAP.remove(requestProtocol.getSessionID());
            logger.error("连接池中没有[" + serviceNodeName + "]的可用连接了:" + e.getMessage());
            localError = e;
        } catch (Throwable e) {
            CACHED_RESPONSE_PROTOCOL_MAP.remove(requestProtocol.getSessionID());
            logger.error("调用服务[" + serviceNodeName + "]失败:" + e.getMessage());
            localError = e;
        } finally {
            Optional.ofNullable(channel).ifPresent(ch -> channelPool.returnObject(serviceNodeName, ch));
        }

        // 抛出本地异常
        if (localError != null) {
            throw localError;
        }

        ResponseProtocol responseProtocol = CACHED_RESPONSE_PROTOCOL_MAP.get(requestProtocol.getSessionID()).take();
        CACHED_RESPONSE_PROTOCOL_MAP.remove(requestProtocol.getSessionID());
        // 抛出服务端抛出的异常
        if (responseProtocol.getError() != null) {
            throw responseProtocol.getError();
        }
        //正常返回调用结果
        return responseProtocol.getInvokeResult();
    }
}
