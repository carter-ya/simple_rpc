package com.ifengxue.rpc.client;

import com.ifengxue.rpc.protocol.ResponseProtocol;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * Created by LiuKeFeng on 2017-04-22.
 */
public class ClientResponseHandler extends SimpleChannelInboundHandler<ResponseProtocol> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ResponseProtocol msg) throws Exception {

    }
}
