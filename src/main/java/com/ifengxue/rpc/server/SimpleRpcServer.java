package com.ifengxue.rpc.server;

import com.ifengxue.rpc.protocol.json.JSONRequestDecoder;
import com.ifengxue.rpc.server.factory.ServerConfigFactory;
import com.ifengxue.rpc.protocol.RequestProtocolDecoder;
import com.ifengxue.rpc.protocol.ResponseProtocolEncoder;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.logging.LoggingHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 默认实现的RPC服务器
 *
 * Created by LiuKeFeng on 2017-04-23.
 */
public class SimpleRpcServer implements IRpcServer {
    private static final AtomicInteger BOSS_THREAD_SEQUENCE = new AtomicInteger(0);
    private static final AtomicInteger WORKER_THREAD_SEQUENCE  = new AtomicInteger(0);
    private Logger logger = LoggerFactory.getLogger(getClass());
    private EventLoopGroup bossGroup;
    private EventLoopGroup workerGroup;
    @Override
    public void start() {
        ServerConfigFactory configFactory = ServerConfigFactory.getInstance();
        List<Server> servers = new ArrayList<>();
        logger.info("Service name:{}", configFactory.getServiceName());
        logger.info("Service bind host:{}", configFactory.getBindHost());
        logger.info("Service bind port:{}", configFactory.getBindPort());
        servers.add(new Server(configFactory.getBindHost(), configFactory.getBindPort()));

        if (configFactory.getEnableJSONRpc()) {
            logger.info("Service json-rpc bind host:{}", configFactory.getJSONRpcBindHost());
            logger.info("Service json-prc bind port:{}", configFactory.getJSONRpcBindPort());
            servers.add(new Server(configFactory.getJSONRpcBindHost(), configFactory.getJSONRpcBindPort()));
        }

        bossGroup = new NioEventLoopGroup(0,
                r -> new Thread(r, "RpcServerBossGroup-" + BOSS_THREAD_SEQUENCE.incrementAndGet()));
        workerGroup = new NioEventLoopGroup(0,
                r -> new Thread(r, "RpcServerWorkerGroup-" + WORKER_THREAD_SEQUENCE.incrementAndGet()));
        ServerBootstrap bootstrap = new ServerBootstrap();
        bootstrap.group(bossGroup, workerGroup)
            .channel(NioServerSocketChannel.class)
            .childHandler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel ch) throws Exception {
                    boolean enableJSONRpc = ServerConfigFactory.getInstance().getEnableJSONRpc();
                    int jsonRpcBindPort = ServerConfigFactory.getInstance().getJSONRpcBindPort();
                    int currentChannelPort = ((InetSocketAddress) ch.pipeline().channel().localAddress()).getPort();
                    if (enableJSONRpc && jsonRpcBindPort == currentChannelPort) {
                        ch.pipeline()
                                .addLast(new LoggingHandler())
                                .addLast(new HttpServerCodec())
                                .addLast(new JSONRequestDecoder());
                    } else {
                        ch.pipeline()
                                .addLast(new LoggingHandler())
                                .addLast(new RequestProtocolDecoder())
                                .addLast(new ResponseProtocolEncoder())
                                .addLast(new ServerRequestProtocolHandler());
                    }
                }
            });
        try {
            for (Server server : servers) {
                Channel channel = bootstrap.bind(server.host, server.port).sync().channel();
                logger.info("Service has opened at:{}:{}", server.host, server.port);
            }
            logger.info("{} start success!", configFactory.getServiceName());
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
    private class Server {
        String host;
        int port;
        public Server(String host, int port) {
            this.host = host;
            this.port = port;
        }
    }
}
