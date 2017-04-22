package com.ifengxue.rpc.client.pool;

import io.netty.channel.Channel;

import java.util.concurrent.TimeoutException;

/**
 * 默认的{@link Channel}工厂
 *
 * Created by LiuKeFeng on 2017-04-22.
 */
public class SimpleChannelFactory implements IChannelFactory {
    @Override
    public Channel borrowChannel(String serviceNodeName, int timeout) throws TimeoutException {
        return null;
    }

    @Override
    public void returnChannel(String serviceNodeName, Channel channel) {

    }
}
