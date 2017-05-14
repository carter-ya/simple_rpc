package com.ifengxue.rpc.server.task;

import com.ifengxue.rpc.protocol.RequestContext;
import com.ifengxue.rpc.protocol.ResponseContext;
import com.ifengxue.rpc.server.handle.IInvokeHandler;
import io.netty.channel.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;

/**
 * 可以在线程中执行的任务
 *
 * Created by LiuKeFeng on 2017-05-14.
 */
public class RunnableTask {
    private Logger logger = LoggerFactory.getLogger(getClass());
    private final IInvokeHandler invokeHandler;
    private final RequestContext requestContext;
    private final Channel channel;
    private final String remoteHost;
    private final int remotePort;

    /**
     * 构造器
     * @param invokeHandler 方法调用器
     * @param requestContext 请求上下文
     * @param channel
     */
    public RunnableTask(IInvokeHandler invokeHandler, RequestContext requestContext, Channel channel) {
        this.invokeHandler = invokeHandler;
        this.requestContext = requestContext;
        this.channel = channel;
        InetSocketAddress inetSocketAddress = (InetSocketAddress) channel.remoteAddress();
        this.remoteHost = inetSocketAddress.getAddress().getHostAddress();
        this.remotePort = inetSocketAddress.getPort();
    }

    /**
     * 执行任务
     */
    public void execute() {
        ResponseContext responseContext = null;
        try {
            responseContext = new ResponseContext(requestContext);
            responseContext.bindAttribute(ResponseContext.REQUEST_IN_TIME_MILLIS_KEY, System.currentTimeMillis());
            invokeHandler.methodInvoke(responseContext);
        } catch (Exception e) {
            if (responseContext == null) {
                responseContext = ResponseContext.newExceptionResponseContext(requestContext, e);
                responseContext.bindAttribute(ResponseContext.REQUEST_IN_TIME_MILLIS_KEY, System.currentTimeMillis());
            } else {
                responseContext.setResponseError(e);
            }
            logger.error("处理客户端:" + responseContext.getRequestSessionID() + "调用失败:" + e.getMessage(), e);
        } finally {
            responseContext.bindAttribute(ResponseContext.REQUEST_OUT_TIME_MILLIS_KEY, System.currentTimeMillis());
        }

        //响应客户端调用结果
        channel.writeAndFlush(responseContext);
    }

    public IInvokeHandler getInvokeHandler() {
        return invokeHandler;
    }

    public RequestContext getRequestContext() {
        return requestContext;
    }

    public Channel getChannel() {
        return channel;
    }

    public String getRemoteHost() {
        return remoteHost;
    }

    public int getRemotePort() {
        return remotePort;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("RunnableTask for ")
                .append(remoteHost)
                .append(":")
                .append(remotePort)
                .append("[sessionID=")
                .append(requestContext.getRequestProtocol().getSessionID())
                .append(", class=")
                .append(requestContext.getRequestProtocol().getClassName())
                .append(", method=")
                .append(requestContext.getRequestProtocol().getMethodName())
                .append(", protocolType=")
                .append(requestContext.getRequestProtocol().getRequestProtocolTypeEnum())
                .append("]");
        return builder.toString();
    }
}
