package com.ifengxue.rpc.server.interceptor;

import com.ifengxue.rpc.protocol.IEchoService;
import com.ifengxue.rpc.protocol.ResponseContext;
import com.ifengxue.rpc.protocol.enums.RequestProtocolTypeEnum;

import java.lang.reflect.Method;

/**
 * 回声测试
 *
 * Created by LiuKeFeng on 2017-04-26.
 */
public class EchoInterceptor implements Interceptor, IEchoService {
    private static Method $echoMethod;
    static {
        try {
            $echoMethod = IEchoService.class.getMethod("$echo", String.class);
        } catch (NoSuchMethodException e) {
        }
    }
    @Override
    public void intercept(ResponseContext context, InterceptorTypeEnum interceptorTypeEnum) throws Exception {
        if (context.getRequestProtocolTypeEnum() != RequestProtocolTypeEnum.METHOD_INVOKE) {
            return;
        }
        if (context.getRequestMethod().equals($echoMethod)) {
            System.out.println(context.getRequestParameters());
            context.setInvokeResult($echo(context.getRequestParameters()[0].toString()));
        }
        return;
    }

    @Override
    public String $echo(String echo) {
        return echo;
    }

    @Override
    public InterceptorTypeEnum[] getInterceptorTypeEnums() {
        return new InterceptorTypeEnum[] {InterceptorTypeEnum.BEFORE};
    }
}
