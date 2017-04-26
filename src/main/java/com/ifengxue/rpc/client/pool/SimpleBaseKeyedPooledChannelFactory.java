package com.ifengxue.rpc.client.pool;

import com.ifengxue.rpc.client.ClientResponseHandler;
import com.ifengxue.rpc.client.register.IRegisterCenter;
import com.ifengxue.rpc.client.factory.ClientConfigFactory;
import com.ifengxue.rpc.protocol.RequestProtocolEncoder;
import com.ifengxue.rpc.protocol.ResponseProtocolDecoder;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LoggingHandler;
import org.apache.commons.pool2.BaseKeyedPooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 默认实现的连接池
 *
 * Created by liukefeng on 2017-04-23.
 */
public class SimpleBaseKeyedPooledChannelFactory extends BaseKeyedPooledObjectFactory<String, Channel> {
    private final IRegisterCenter registerCenter;
    private final ChannelPoolConfig channelPoolConfig = ClientConfigFactory.getInstance().getChannelPoolConfig();
    private Logger logger = LoggerFactory.getLogger(getClass());
    private static final Map<Channel, EventLoopGroup> CACHED_EVENT_LOOP_GROUP = new ConcurrentHashMap<>();

    public SimpleBaseKeyedPooledChannelFactory(IRegisterCenter registerCenter) {
        this.registerCenter = registerCenter;
    }

    @Override
    public Channel create(String key) throws Exception {
        IRegisterCenter.ServiceNode serviceNode = registerCenter.getAvailableServiceNode(key);
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
                .option(ChannelOption.SO_KEEPALIVE, true)
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, channelPoolConfig.getConnectTimeout());

        ChannelFuture future = bootstrap.connect(serviceNode.getHost(), serviceNode.getPort()).sync();
        future.addListener((ChannelFutureListener)listener -> {
            if (future.isSuccess()) {
                logger.info("Service:{} new channel create success.", key);
            }
        });
        Channel channel = future.channel();
        CACHED_EVENT_LOOP_GROUP.put(channel, eventLoopGroup);
        return channel;
    }

    @Override
    public PooledObject<Channel> wrap(Channel value) {
        return new DefaultPooledObject<>(value);
    }
    @Override
    public void destroyObject(String key, PooledObject<Channel> p) throws Exception {
        InetSocketAddress inetSocketAddress = (InetSocketAddress) p.getObject().localAddress();
        p.getObject()
                .closeFuture()
                .addListener((ChannelFutureListener) listener ->
                        logger.info("Channel Service:{} localHost:{} localPort:{} destroyed success.",
                                key,
                                inetSocketAddress.getAddress().getHostAddress(),
                                inetSocketAddress.getPort()));
        Optional.of(CACHED_EVENT_LOOP_GROUP.get(p.getObject())).ifPresent(eventLoopGroup -> eventLoopGroup.shutdownGracefully());
        CACHED_EVENT_LOOP_GROUP.remove(p.getObject());
    }

    @Override
    public boolean validateObject(String key, PooledObject<Channel> p) {
        return p.getObject().isActive();
    }

    @Override
    public void activateObject(String key, PooledObject<Channel> p) throws Exception {}

    @Override
    public void passivateObject(String key, PooledObject<Channel> p) throws Exception {}

}
