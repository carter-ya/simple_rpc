package com.ifengxue.rpc.client.proxy;

import com.ifengxue.rpc.client.pool.IChannelFactory;
import com.ifengxue.rpc.factory.ClientConfigFactory;
import com.ifengxue.rpc.protocol.RequestProtocol;
import io.netty.channel.Channel;
import java.util.Optional;
import java.util.concurrent.TimeoutException;

/**
 * 默认的服务代理实现
 *
 * Created by LiuKeFeng on 2017-04-22.
 */
public class SimpleServiceProxy extends AbstractServiceProxy {
    private static final long serialVersionUID = 6567739050249606630L;
    private final IChannelFactory channelFactory = ClientConfigFactory.getInstance().getChannelFactory();
    public SimpleServiceProxy(Class<?> interfaceClass, String serviceNodeName) {
        super(interfaceClass, serviceNodeName);
    }

    @Override
    protected Object invoke(Object proxy, RequestProtocol requestProtocol) throws Throwable {
        Channel channel = null;
        try {
            channel = channelFactory.borrowChannel(serviceNodeName);
            channel.writeAndFlush(requestProtocol);
        } catch (TimeoutException e) {
            e.printStackTrace();
        } catch (Throwable e) {
            e.printStackTrace();
        } finally {
            Optional.ofNullable(channel).ifPresent(ch -> channelFactory.returnChannel(serviceNodeName, ch));
        }
        //TODO:接收服务端响应
        return null;
    }
}
