package com.ifengxue.rpc.server;

import com.ifengxue.rpc.factory.ServerConfigFactory;
import com.ifengxue.rpc.protocol.*;
import com.ifengxue.rpc.server.handle.IInvokeHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.util.Optional;

/**
 * 服务端请求协议处理器
 *
 * Created by LiuKeFeng on 2017-04-21.
 */
public class ServerRequestProtocolHandler extends SimpleChannelInboundHandler<RequestContext> {
    private Logger logger = LoggerFactory.getLogger(getClass());
    private final IInvokeHandler invokeHandler = ServerConfigFactory.getInstance().getInvokeHandler();
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RequestContext context) {
        logger.info("开始处理客户端:{}方法调用", context.getRequestProtocol().getSessionID());
        ResponseContext responseContext = null;
        try {
            responseContext = new ResponseContext(context);
            responseContext.bindAttribute(ResponseContext.REQUEST_IN_TIME_MILLIS_KEY, System.currentTimeMillis());
            invokeHandler.methodInvoke(responseContext);
        } catch (Exception e) {
            if (responseContext == null) {
                responseContext = ResponseContext.newExceptionResponseContext(context, e);
                responseContext.bindAttribute(ResponseContext.REQUEST_IN_TIME_MILLIS_KEY, System.currentTimeMillis());
            } else {
                responseContext.setResponseError(e);
            }
            logger.error("处理客户端:" + responseContext.getRequestSessionID() + "调用失败:" + e.getMessage(), e);
        } finally {
            responseContext.bindAttribute(ResponseContext.REQUEST_OUT_TIME_MILLIS_KEY, System.currentTimeMillis());
        }

        //响应客户端最终结果
        ctx.writeAndFlush(responseContext);
        logger.info("客户端:{}请求耗时:{}ms", responseContext.getRequestSessionID(),
                (long)responseContext.getBindAttribute(ResponseContext.REQUEST_OUT_TIME_MILLIS_KEY) - (long)responseContext.getBindAttribute(ResponseContext.REQUEST_IN_TIME_MILLIS_KEY));
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.channel().close();
        if (cause instanceof InvocationTargetException) {
            cause = cause.getCause();
        }
        logger.error("服务端处理客户端响应出错:" + cause.getMessage(), cause);
    }
}
