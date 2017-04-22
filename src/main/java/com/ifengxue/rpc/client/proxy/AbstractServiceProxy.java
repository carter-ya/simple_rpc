package com.ifengxue.rpc.client.proxy;

import com.ifengxue.rpc.protocol.RequestProtocol;
import com.ifengxue.rpc.protocol.enums.RequestProtocolTypeEnum;
import io.netty.handler.timeout.TimeoutException;

import java.lang.reflect.Method;
import java.net.ConnectException;

/**
 * Created by LiuKeFeng on 2017-04-22.
 */
public abstract class AbstractServiceProxy implements IServiceProxy {
    private static final long serialVersionUID = 1239171968499525364L;
    protected final Class<?> interfaceClass;
    protected final String serviceNodeName;

    public AbstractServiceProxy(Class<?> interfaceClass, String serviceNodeName) {
        this.interfaceClass = interfaceClass;
        this.serviceNodeName = serviceNodeName;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (method.getName().equals("hashCode") && args == null) {
            return hashCodeProxy(proxy);
        }
        if (method.getName().equals("toString") && args == null) {
            return toStringProxy();
        }
        if (method.getName().equals("equals") && args != null && args.length == 1) {
            return equalsProxy(proxy, args[0]);
        }
        return invoke(proxy, RequestProtocol.Builder
                .newBuilder()
                .setRequestProtocolTypeEnum(RequestProtocolTypeEnum.METHOD_INVOKE)
                .setClassName(interfaceClass.getName())
                .setMethodName(method.getName())
                .setParameterTypes(method.getParameterTypes())
                .setParameters(args)
                .build());
    }

    /**
     * 子类需要实现的如何调用服务
     * @throws TimeoutException 连接超时异常
     * @throws ConnectException 连接被拒绝异常
     * @throws Throwable 其他错误
     */
    protected abstract Object invoke(Object proxy, RequestProtocol requestProtocol) throws TimeoutException, ConnectException, Throwable;

    @Override
    public Class<?> getInterfaceClass() {
        return interfaceClass;
    }

    @Override
    public String getServiceNodeName() {
        return serviceNodeName;
    }

    @Override
    public String toStringProxy() {
        StringBuilder builder = new StringBuilder();
        builder.append("Service ").append(interfaceClass.getSimpleName()).append(" is proxied by ").append(this.getClass().getSimpleName()).append("!");
        return builder.toString();
    }
}
