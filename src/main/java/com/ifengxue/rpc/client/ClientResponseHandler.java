package com.ifengxue.rpc.client;

import com.ifengxue.rpc.client.proxy.IServiceProxy;
import com.ifengxue.rpc.protocol.ResponseProtocol;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 客户端响应处理器
 *
 * Created by LiuKeFeng on 2017-04-22.
 */
public class ClientResponseHandler extends SimpleChannelInboundHandler<ResponseProtocol> {
    private Logger logger = LoggerFactory.getLogger(getClass());
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ResponseProtocol responseProtocol) throws Exception {
        logger.info(responseProtocol.toString());
        IServiceProxy.CACHED_RESPONSE_PROTOCOL_MAP.get(responseProtocol.getSessionID()).offer(responseProtocol);
    }
}
