package com.ifengxue.rpc.client.pool;

import io.netty.channel.Channel;

import java.util.concurrent.TimeoutException;

/**
 * {@link Channel} 工厂
 *
 * Created by LiuKeFeng on 2017-04-22.
 */
public interface IChannelFactory {
    /**
     * 获取一个{@link Channel}
     * @param serviceNodeName 提供服务的节点名称
     * @return
     * @see ##borrowChannel(String, int)
     */
    default Channel borrowChannel(String serviceNodeName) throws TimeoutException {
        return borrowChannel(serviceNodeName, -1);
    }

    /**
     * 获取一个{@link Channel}
     * @param serviceNodeName 提供服务的节点名称
     * @param timeout 获取{@link Channel}时允许的最大超时时间。-1为不设置超时时间
     * @return
     * @throws TimeoutException 在timeout时间内依然没获取到{@link Channel}
     */
    Channel borrowChannel(String serviceNodeName, int timeout) throws TimeoutException;

    /**
     * 释放{@link Channel}
     * @param serviceNodeName 提供服务的节点名称
     * @param channel
     */
    void returnChannel(String serviceNodeName, Channel channel);
}