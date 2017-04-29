package com.ifengxue.rpc.client;


import com.ifengxue.rpc.protocol.ResponseProtocol;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;

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
}