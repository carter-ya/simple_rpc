package com.ifengxue.rpc.client.proxy;

import com.ifengxue.rpc.protocol.ResponseProtocol;

import java.io.Serializable;
import java.lang.reflect.InvocationHandler;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 服务代理接口
 *
 * Created by LiuKeFeng on 2017-04-22.
 */
public interface IServiceProxy extends InvocationHandler, Serializable {
    /**
     * 存储服务端响应结果的缓存Map
     */
    Map<String, BlockingQueue<ResponseProtocol>> CACHED_RESPONSE_PROTOCOL_MAP = new ConcurrentHashMap<>();
    /**
     * 获取提供服务的接口类
     * @return
     */
    Class<?> getInterfaceClass();

    /**
     * 获取提供服务的节点名称
     * @return
     */
    String getServiceNodeName();

    /**
     * {@link Object#toString()}方法代理
     * @return
     */
    String toStringProxy();

    /**
     * {@link Object#equals(Object)}方法代理
     * @param other
     * @param another
     * @return
     */
    default boolean equalsProxy(Object other, Object another) {
        return Objects.equals(other, another);
    }

    /**
     * {@link Object#hashCode()}方法代理
     * @return
     */
    default int hashCodeProxy(Object object) {
        return System.identityHashCode(object);
    }
}
