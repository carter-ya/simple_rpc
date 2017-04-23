package com.ifengxue.rpc.server;

import com.ifengxue.rpc.factory.ServerConfigFactory;
import com.ifengxue.rpc.protocol.RequestProtocolDecoder;
import com.ifengxue.rpc.protocol.ResponseProtocolEncoder;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LoggingHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 默认实现的RPC服务器
 *
 * Created by LiuKeFeng on 2017-04-23.
 */
public class SimpleRpcServer implements IRpcServer {
    private Logger logger = LoggerFactory.getLogger(getClass());
    private EventLoopGroup bossGroup;
    private EventLoopGroup workerGroup;
    @Override
    public void start() {
        ServerConfigFactory configFactory = ServerConfigFactory.getInstance();
        logger.info("Service name:{}", configFactory.getServiceName());
        String host = configFactory.getBindHost();
        logger.info("Service bind host:{}", host);
        int port = configFactory.getBindPort();
        logger.info("Service bind port:{}", port);

        bossGroup = new NioEventLoopGroup();
        workerGroup = new NioEventLoopGroup();
        ServerBootstrap bootstrap = new ServerBootstrap();
        bootstrap.group(bossGroup, workerGroup)
            .channel(NioServerSocketChannel.class)
            .childHandler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel ch) throws Exception {
                    ch.pipeline()
                            .addLast(new LoggingHandler())
                            .addLast(new RequestProtocolDecoder())
                            .addLast(new ResponseProtocolEncoder())
                            .addLast(new ServerRequestProtocolHandler());
                }
            });
        try {
            ChannelFuture channelFuture = bootstrap.bind(host, port).sync();
            logger.info("{} start success!", configFactory.getServiceName());
            channelFuture.addListener(ChannelFutureListener.CLOSE);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void close() {
        logger.warn("{} will close...", ServerConfigFactory.getInstance().getServiceName());
        bossGroup.shutdownGracefully();
        workerGroup.shutdownGracefully();
        logger.info("{} close success.", ServerConfigFactory.getInstance().getServiceName());
    }
}
