package com.ifengxue.rpc.client;


import com.ifengxue.rpc.protocol.ResponseProtocol;

import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;

/**
 * rpc调用上下文
 *
 * Created by LiuKeFeng on 2017-04-27.
 */
public class RpcContext {
    /**
     * 存储服务端响应结果的缓存Map
     */
    public static final ConcurrentHashMap<String, BlockingQueue<ResponseProtocol>> CACHED_RESPONSE_PROTOCOL_MAP = new ConcurrentHashMap<>();
    /**
     * 不需要返回值的调用sessionID缓存Map
     */
    public static final Set<String> CACHED_NOT_NEED_RETURN_RESULT_SET = Collections.synchronizedSet(new HashSet<>());

    private static final RpcContext RPC_CONTEXT = new RpcContext();
    private static ThreadLocal<Future> threadLocal = new ThreadLocal<>();
    private RpcContext() {}

    public static RpcContext getInstance() {
        return RPC_CONTEXT;
    }

    /**
     * 获取和移除Future对象
     * @param <V>
     * @return
     */
    public <V> Future<V> getAndRemoveFuture() {
        Future<V> future = threadLocal.get();
        Optional.ofNullable(future).orElseThrow(() -> new IllegalStateException("没有异步调用！！！"));
        threadLocal.remove();
        return future;
    }

    /**
     * 设置Future对象
     * @param future
     * @param <V>
     */
    public <V> void setFuture(Future<V> future) {
        if (threadLocal.get() != null) {
            throw new IllegalStateException("上次的异步调用尚未调用getAndRemoveFuture，拒绝再次异步调用！");
        }
        threadLocal.set(future);
    }
}
