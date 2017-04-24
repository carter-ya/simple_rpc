package com.ifengxue.rpc.client.proxy;

import com.ifengxue.rpc.client.pool.IChannelPool;
import com.ifengxue.rpc.factory.ClientConfigFactory;
import com.ifengxue.rpc.protocol.RequestProtocol;
import com.ifengxue.rpc.protocol.ResponseProtocol;
import io.netty.channel.Channel;

import java.util.Optional;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeoutException;

/**
 * 默认的服务代理实现
 *
 * Created by LiuKeFeng on 2017-04-22.
 */
public class SimpleServiceProxy extends AbstractServiceProxy {
    private static final long serialVersionUID = 6567739050249606630L;
    private final IChannelPool channelPool = ClientConfigFactory.getInstance().getChannelPool();
    public SimpleServiceProxy(Class<?> interfaceClass, String serviceNodeName) {
        super(interfaceClass, serviceNodeName);
    }

    @Override
    protected Object invoke(Object proxy, RequestProtocol requestProtocol) throws Throwable {
        Channel channel = null;
        try {
            channel = channelPool.borrowObject(serviceNodeName);
            channel.writeAndFlush(requestProtocol).sync();
            CACHED_RESPONSE_PROTOCOL_MAP.put(requestProtocol.getSessionID(), new ArrayBlockingQueue<>(1));
        } catch (TimeoutException e) {
            e.printStackTrace();
        } catch (Throwable e) {
            e.printStackTrace();
        } finally {
            Optional.ofNullable(channel).ifPresent(ch -> channelPool.returnObject(serviceNodeName, ch));
        }
        ResponseProtocol responseProtocol = CACHED_RESPONSE_PROTOCOL_MAP.get(requestProtocol.getSessionID()).take();
        CACHED_RESPONSE_PROTOCOL_MAP.remove(requestProtocol.getSessionID());
        if (responseProtocol.getError() != null) {
            throw responseProtocol.getError();
        }
        return responseProtocol.getInvokeResult();
    }
}
