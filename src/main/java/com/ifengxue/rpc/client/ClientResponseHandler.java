package com.ifengxue.rpc.client;

import com.ifengxue.rpc.client.proxy.IServiceProxy;
import com.ifengxue.rpc.protocol.ResponseProtocol;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.BlockingQueue;

/**
 * 客户端响应处理器
 *
 * Created by LiuKeFeng on 2017-04-22.
 */
public class ClientResponseHandler extends SimpleChannelInboundHandler<ResponseProtocol> {
    private Logger logger = LoggerFactory.getLogger(getClass());
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ResponseProtocol responseProtocol) throws Exception {
        logger.info("收到服务端对客户端{}的响应", responseProtocol.getSessionID());
        BlockingQueue<ResponseProtocol> blockingQueue = IServiceProxy.CACHED_RESPONSE_PROTOCOL_MAP.get(responseProtocol.getSessionID());
        blockingQueue.put(responseProtocol);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.channel().close();
        logger.error("客户端接收服务端响应出错:" + cause.getMessage(), cause);
    }
}
