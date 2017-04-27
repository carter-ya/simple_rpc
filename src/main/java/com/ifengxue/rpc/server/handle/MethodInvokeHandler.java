package com.ifengxue.rpc.server.handle;

import com.ifengxue.rpc.server.factory.IInvokeProxyService;
import com.ifengxue.rpc.server.factory.ServerConfigFactory;
import com.ifengxue.rpc.protocol.ProtocolException;
import com.ifengxue.rpc.protocol.ResponseContext;
import com.ifengxue.rpc.protocol.enums.RequestProtocolTypeEnum;
import com.ifengxue.rpc.server.interceptor.Interceptor;
import com.ifengxue.rpc.server.service.IServiceProvider;
import com.ifengxue.rpc.util.InterceptorUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;

/**
 * 方法调用处理器
 *
 * Created by LiuKeFeng on 2017-04-26.
 */
public class MethodInvokeHandler implements IInvokeHandler {
    private final List<Interceptor> beforeInterceptors;
    private final List<Interceptor> afterInterceptors;
    private final List<Interceptor> exceptionInterceptors;
    private final IServiceProvider serviceProvider;
    private Logger logger = LoggerFactory.getLogger(getClass());

    public MethodInvokeHandler() {
        List<Interceptor> interceptors = ServerConfigFactory.getInstance().getAllInterceptor();
        beforeInterceptors = InterceptorUtil.getBeforeInterceptors(interceptors);
        afterInterceptors = InterceptorUtil.getAfterInterceptors(interceptors);
        exceptionInterceptors = InterceptorUtil.getExceptionInterceptors(interceptors);
        serviceProvider = ServerConfigFactory.getInstance().getServiceProvider();
    }

    @Override
    public void methodInvoke(ResponseContext responseContext) throws Exception {
        // 执行前置拦截器
        invokeInterceptors(responseContext, beforeInterceptors, Interceptor.InterceptorTypeEnum.BEFORE);

        // 调用真实方法
        if (responseContext.getRequestProtocolTypeEnum() == RequestProtocolTypeEnum.METHOD_INVOKE &&
                responseContext.getInvokeResult() == null &&
                responseContext.getResponseError() == null) {
            Object realServiceImpl = serviceProvider.findAllServices().get(responseContext.getRequestClassName());
            if (realServiceImpl == null) {
                responseContext.setResponseError(new ProtocolException("请求的服务[" + responseContext.getRequestClassName() + "]不存在！"));
            } else {
                try {
                    responseContext.setInvokeResult(responseContext.getRequestMethod().invoke(realServiceImpl, responseContext.getRequestParameters()));
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
}
