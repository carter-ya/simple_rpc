package com.ifengxue.rpc.server;

import com.ifengxue.rpc.factory.ServerConfigFactory;
import com.ifengxue.rpc.protocol.ProtocolException;
import com.ifengxue.rpc.protocol.RequestContext;
import com.ifengxue.rpc.protocol.ResponseContext;
import com.ifengxue.rpc.protocol.enums.RequestProtocolTypeEnum;
import com.ifengxue.rpc.server.filter.Interceptor;
import com.ifengxue.rpc.server.service.IServiceProvider;
import com.ifengxue.rpc.util.InterceptorUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

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
    public ServerRequestProtocolHandler() {
        List<Interceptor> interceptors = ServerConfigFactory.getInstance().getAllInterceptor();
        beforeInterceptors = InterceptorUtil.getBeforeInterceptors(interceptors);
        afterInterceptors = InterceptorUtil.getAfterInterceptors(interceptors);
        exceptionInterceptors = InterceptorUtil.getExceptionInterceptors(interceptors);
        serviceProvider = ServerConfigFactory.getInstance().getServiceProvider();
    }
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RequestContext context) throws Exception {
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

        //TODO:处理所有拦截器全报错的情况
    }

    private void invokeInterceptors(ResponseContext context, List<Interceptor> interceptors, Interceptor.InterceptorTypeEnum interceptorTypeEnum) {
        for (Interceptor interceptor : interceptors) {
            try {
                interceptor.intercept(context, interceptorTypeEnum);
                if (context.getInvokeResult() != null) {
                    break;
                }
            } catch (Throwable throwable) {
                context.setResponseError(throwable);
                break;
            }
        }
    }
}
