package com.ifengxue.rpc.client;

import com.ifengxue.rpc.client.async.AsyncConsts;
import com.ifengxue.rpc.client.async.AsyncRpcFuture;
import com.ifengxue.rpc.protocol.ResponseProtocol;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
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
        String sessionID = responseProtocol.getSessionID();
        logger.info("收到服务端对客户端{}的响应", sessionID);

        AsyncRpcFuture asyncRpcFuture = AsyncConsts.ASYNC_RPC_FUTURE_SESSION_ID_MAP.get(sessionID);
        //异步调用方法
        if (asyncRpcFuture != null) {
            AsyncConsts.ASYNC_RPC_FUTURE_SESSION_ID_MAP.remove(sessionID);
            //需要服务端返回值
            if (asyncRpcFuture.getAsyncMethod().isReturnWait()) {
                asyncRpcFuture.callback(responseProtocol);
            }
        } else {
            //同步调用方法
            BlockingQueue<ResponseProtocol> blockingQueue = RpcContext.CACHED_RESPONSE_PROTOCOL_MAP.get(sessionID);
            if (blockingQueue != null) {
                blockingQueue.put(responseProtocol);
            } else {
                logger.warn("客户端[" + sessionID + "]接收到服务端响应，但是客户端已经超时退出。");
            }
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.channel().close();
        if (cause instanceof InvocationTargetException) {
            cause = cause.getCause();
        }
        logger.error("客户端接收服务端响应出错:" + cause.getMessage(), cause);
    }
}
