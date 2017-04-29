package com.ifengxue.rpc.client.async;

import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;

/**
 * 异步调用器
 *
 * Created by LiuKeFeng on 2017-04-28.
 */
public class AsyncRpcInvoker {

    /**
     * 异步调用方法并带返回值
     * @param callable
     * @param <V>
     * @return
     * @see #asyncForNoneResult(ReturnNoneCallable)
     */
    public static <V> Future<V> asyncForResult(Callable<V> callable) {
        AsyncConsts.ASYNC_RPC_FUTURE_THREAD_LOCAL.set(new AsyncRpcFuture<>());
        AsyncConsts.ASYNC_RPC_FUTURE_THREAD_LOCAL.get().setAsyncMethod(new AsyncMethod(true, true));
        Future<V> future = AsyncConsts.ASYNC_RPC_FUTURE_THREAD_LOCAL.get();
        try {
            callable.call();
        } catch (Exception e) {
            throwRuntimeException(e);
        }
        return future;
    }

    /**
     * 异步调用方法并且无返回值
     * @param callable
     */
    public static void asyncForNoneResult(ReturnNoneCallable callable) {
        AsyncConsts.ASYNC_RPC_FUTURE_THREAD_LOCAL.set(new AsyncRpcFuture<>());
        AsyncConsts.ASYNC_RPC_FUTURE_THREAD_LOCAL.get().setAsyncMethod(new AsyncMethod(true, false));
        try {
            callable.call();
        } catch (Exception e) {
            throwRuntimeException(e);
        }
    }

    private static void throwRuntimeException(Exception e) {
        if (e instanceof InvocationTargetException) {
            e = (Exception) e.getCause();
        }
        throw new RuntimeException(e);
    }
}