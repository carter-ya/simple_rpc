package com.ifengxue.rpc.client.async;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by LiuKeFeng on 2017-04-28.
 */
public class AsyncConsts {
    /**
     * 用于异步回调的ThreadLocal
     */
    public static final ThreadLocal<AsyncRpcFuture<?>> ASYNC_RPC_FUTURE_THREAD_LOCAL = new ThreadLocal<>();

    /**
     * 用于异步回调的Map.key为sessionID,value为future
     */
    public static final Map<String, AsyncRpcFuture<?>> ASYNC_RPC_FUTURE_SESSION_ID_MAP = new ConcurrentHashMap<>();
}
