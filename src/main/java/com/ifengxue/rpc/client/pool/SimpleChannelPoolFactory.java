package com.ifengxue.rpc.client.pool;

import com.ifengxue.rpc.client.ClientResponseHandler;
import com.ifengxue.rpc.protocol.RequestProtocolDecoder;
import com.ifengxue.rpc.protocol.RequestProtocolEncoder;
import com.ifengxue.rpc.protocol.ResponseProtocolDecoder;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LoggingHandler;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 简单的{@link Channel}池工厂
 *
 * Created by LiuKeFeng on 2017-04-22.
 */
public class SimpleChannelPoolFactory implements IChannelPoolFactory {
    private Logger logger = LoggerFactory.getLogger(getClass());
    private static final Map<Channel, EventLoopGroup> CACHED_EVENT_LOOP_GROUP = new ConcurrentHashMap<>();
    @Override
    public PooledObject<Channel> makeObject(ServiceNode serviceNode) throws Exception {
        Bootstrap bootstrap = new Bootstrap();
        EventLoopGroup eventLoopGroup = new NioEventLoopGroup(1);
        bootstrap.group(eventLoopGroup)
            .channel(NioSocketChannel.class)
            .handler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel ch) throws Exception {
                    ch.pipeline()
                            .addLast(new LoggingHandler())
                            .addLast(new RequestProtocolEncoder())
                            .addLast(new ResponseProtocolDecoder())
                            .addLast(new ClientResponseHandler());
                }
            })
            .option(ChannelOption.SO_KEEPALIVE, true);
        ChannelFuture future = bootstrap.bind(serviceNode.getHost(), serviceNode.getPort()).sync();
        Channel channel = future.channel();
        CACHED_EVENT_LOOP_GROUP.put(channel, eventLoopGroup);
        return new DefaultPooledObject<>(channel);
    }

    @Override
    public void destroyObject(ServiceNode serviceNode, PooledObject<Channel> pooledObject) throws Exception {
        InetSocketAddress inetSocketAddress = (InetSocketAddress) pooledObject.getObject().localAddress();
        pooledObject.getObject()
                .closeFuture()
                .addListener((ChannelFutureListener) listener ->
                        logger.info("Channel[Service:{}, host:{}, port:{}] localHost:{} localPort:{} destroyed success.",
                                serviceNode.getServiceNode(),
                                serviceNode.getHost(),
                                serviceNode.getPort(),
                                inetSocketAddress.getAddress().getHostAddress(),
                                inetSocketAddress.getPort()));
        Optional.of(CACHED_EVENT_LOOP_GROUP.get(pooledObject.getObject())).ifPresent(eventLoopGroup -> eventLoopGroup.shutdownGracefully());
        CACHED_EVENT_LOOP_GROUP.remove(pooledObject.getObject());
    }
}
