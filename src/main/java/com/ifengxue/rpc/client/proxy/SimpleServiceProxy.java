package com.ifengxue.rpc.client.proxy;

import com.ifengxue.rpc.client.RpcContext;
import com.ifengxue.rpc.client.async.AsyncInvokeCallable;
import com.ifengxue.rpc.client.async.AsyncMethod;
import com.ifengxue.rpc.protocol.ReceiveTimeoutException;
import com.ifengxue.rpc.client.pool.IChannelPool;
import com.ifengxue.rpc.client.factory.ClientConfigFactory;
import com.ifengxue.rpc.protocol.RequestProtocol;
import com.ifengxue.rpc.protocol.ResponseProtocol;
import io.netty.channel.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.*;

/**
 * 默认的服务代理实现
 *
 * Created by LiuKeFeng on 2017-04-22.
 */
public class SimpleServiceProxy extends AbstractServiceProxy {
    private static final long serialVersionUID = 6567739050249606630L;
    private static final Map<Class<?>, List<AsyncMethod>> ASYNC_CLASS_METHODS_MAP = ClientConfigFactory.getInstance().getAsyncClassMethodMap();
    private Logger logger = LoggerFactory.getLogger(getClass());
    private final IChannelPool channelPool = ClientConfigFactory.getInstance().getChannelPool();
    private final long readTimeout = ClientConfigFactory.getInstance().getChannelPoolConfig().getReadTimeout();
    private final ExecutorService EXECUTOR_SERVICE = Executors.newFixedThreadPool(100, r -> {
        Thread thread = new Thread(r);
        thread.setName("Async Method Invoker");
        return thread;
    } );
    public SimpleServiceProxy(Class<?> interfaceClass, String serviceNodeName) {
        super(interfaceClass, serviceNodeName);
    }

    @Override
    protected Object invoke(Object proxy, RequestProtocol requestProtocol) throws Throwable {
        Exception exception = sendRequestProtocol(requestProtocol);
        // 抛出本地异常
        if (exception != null) {
            throw exception;
        }

        AsyncMethod asyncMethod = ASYNC_CLASS_METHODS_MAP.getOrDefault(interfaceClass, Collections.emptyList())
                .stream()
                .filter(method -> method.getMethodName().equals(requestProtocol.getMethodName()))
                .findAny()
                .orElse(null);
        if (asyncMethod != null) {
            if (asyncMethod.isReturnWait()) {
                Future<?> future = EXECUTOR_SERVICE.submit(new AsyncInvokeCallable(interfaceClass, requestProtocol));
                RpcContext.getInstance().setFuture(future);
            }
            //异步调用直接返回->不需要返回值
            return null;
        } else {
            ResponseProtocol responseProtocol = RpcContext.CACHED_RESPONSE_PROTOCOL_MAP.get(requestProtocol.getSessionID()).poll(readTimeout, TimeUnit.MILLISECONDS);
            RpcContext.CACHED_RESPONSE_PROTOCOL_MAP.remove(requestProtocol.getSessionID());
            if (responseProtocol == null) {
                throw new ReceiveTimeoutException("客户端[" + requestProtocol.getSessionID() + "]接收服务响应失败，等待时长:" + readTimeout + "ms");
            }

            // 抛出服务端抛出的异常
            if (responseProtocol.getExceptionProtocol() != null) {
                throw responseProtocol.getExceptionProtocol().asRemoteException();
            }
            //正常返回调用结果
            return responseProtocol.getInvokeResult();
        }
    }

    /**
     * 发送请求
     * @param requestProtocol
     * @return 返回异常
     */
    private Exception sendRequestProtocol(RequestProtocol requestProtocol) {
        Channel channel = null;
        Exception exception = null;
        try {
            channel = channelPool.borrowObject(serviceNodeName);
            //必须放在前面，否则会出现客户端和服务端并发访问map的情况
            RpcContext.CACHED_RESPONSE_PROTOCOL_MAP.put(requestProtocol.getSessionID(), new ArrayBlockingQueue<>(1));
            channel.writeAndFlush(requestProtocol).sync();
        } catch (NoSuchElementException e) {
            RpcContext.CACHED_RESPONSE_PROTOCOL_MAP.remove(requestProtocol.getSessionID());
            logger.error("连接池中没有[" + serviceNodeName + "]的可用连接了:" + e.getMessage());
            exception = e;
        } catch (Exception e) {
            RpcContext.CACHED_RESPONSE_PROTOCOL_MAP.remove(requestProtocol.getSessionID());
            logger.error("调用服务[" + serviceNodeName + "]失败:" + e.getMessage());
            exception = e;
        } finally {
            Optional.ofNullable(channel).ifPresent(ch -> channelPool.returnObject(serviceNodeName, ch));
        }
        return exception;
    }
}
