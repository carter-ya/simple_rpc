package com.ifengxue.rpc.server;

import com.ifengxue.rpc.factory.ServerConfigFactory;
import com.ifengxue.rpc.protocol.*;
import com.ifengxue.rpc.protocol.enums.RequestProtocolTypeEnum;
import com.ifengxue.rpc.server.filter.Interceptor;
import com.ifengxue.rpc.server.service.IServiceProvider;
import com.ifengxue.rpc.util.InterceptorUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.List;

/**
 * 服务端请求协议处理器
 *
 * Created by LiuKeFeng on 2017-04-21.
 */
public class ServerRequestProtocolHandler extends SimpleChannelInboundHandler<RequestContext> {
    private final List<Interceptor> beforeInterceptors;
    private final List<Interceptor> afterInterceptors;
    private final List<Interceptor> exceptionInterceptors;
    private final IServiceProvider serviceProvider;
    private Logger logger = LoggerFactory.getLogger(getClass());
    public ServerRequestProtocolHandler() {
        List<Interceptor> interceptors = ServerConfigFactory.getInstance().getAllInterceptor();
        beforeInterceptors = InterceptorUtil.getBeforeInterceptors(interceptors);
        afterInterceptors = InterceptorUtil.getAfterInterceptors(interceptors);
        exceptionInterceptors = InterceptorUtil.getExceptionInterceptors(interceptors);
        serviceProvider = ServerConfigFactory.getInstance().getServiceProvider();
    }
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RequestContext context) throws Exception {
        logger.info("开始处理客户端:{}方法调用", context.getRequestProtocol().getSessionID());
        ResponseContext responseContext = new ResponseContext(context);
        responseContext.bindAttribute(ResponseContext.REQUEST_IN_TIME_MILLIS_KEY, System.currentTimeMillis());
        // 执行前置拦截器
        invokeInterceptors(responseContext, beforeInterceptors, Interceptor.InterceptorTypeEnum.BEFORE);

        // 调用真实方法
        if (context.getRequestProtocolTypeEnum() == RequestProtocolTypeEnum.METHOD_INVOKE &&
                responseContext.getInvokeResult() == null &&
                responseContext.getResponseError() == null) {
            Object realServiceImpl = serviceProvider.findAllServices().get(responseContext.getRequestClassName());
            if (realServiceImpl == null) {
                responseContext.setResponseError(new ProtocolException("请求的服务[" + responseContext.getRequestClassName() + "]不存在！"));
            } else {
                try {
                    Method invokeMethod = Class.forName(responseContext.getRequestClassName())
                            .getMethod(responseContext.getRequestMethodName(), responseContext.getRequestParameterTypes());
                    if (invokeMethod == null) {
                        responseContext.setResponseError(
                                new ProtocolException("请求的方法[" + responseContext.getRequestClassName() + "." +
                                        responseContext.getRequestMethodName() + "]不存在！"));
                    } else {
                        responseContext.setInvokeResult(invokeMethod.invoke(realServiceImpl, responseContext.getRequestParameters()));
                    }
                } catch (Exception e) {
                    responseContext.setResponseError(e);
                }
            }
        }
        // 执行后置拦截器
        if (responseContext.getInvokeResult() == null && responseContext.getResponseError() == null) {
            invokeInterceptors(responseContext, afterInterceptors, Interceptor.InterceptorTypeEnum.AFTER);
        }

        // 执行异常拦截器
        if (responseContext.getResponseError() != null) {
            invokeInterceptors(responseContext, exceptionInterceptors, Interceptor.InterceptorTypeEnum.EXCEPTION);
        }
        responseContext.bindAttribute(ResponseContext.REQUEST_OUT_TIME_MILLIS_KEY, System.currentTimeMillis());

        //响应客户端最终结果
        ctx.writeAndFlush(responseContext);
        logger.info("客户端:{}请求耗时:{}ms", context.getRequestProtocol().getSessionID(),
                (Long)responseContext.getBindAttribute(ResponseContext.REQUEST_OUT_TIME_MILLIS_KEY) - (Long)responseContext.getBindAttribute(ResponseContext.REQUEST_IN_TIME_MILLIS_KEY));
    }

    private void invokeInterceptors(ResponseContext context, List<Interceptor> interceptors, Interceptor.InterceptorTypeEnum interceptorTypeEnum) {
        for (Interceptor interceptor : interceptors) {
            try {
                interceptor.intercept(context, interceptorTypeEnum);
                if (context.getInvokeResult() != null) {
                    break;
                }
            } catch (Throwable throwable) {
                logger.error("执行" + interceptorTypeEnum + "类型拦截器出错:" + throwable.getMessage(), throwable);
                context.setResponseError(throwable);
                break;
            }
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.channel().close();
        logger.error("服务端处理客户端响应出错:" + cause.getMessage(), cause);
    }
}
