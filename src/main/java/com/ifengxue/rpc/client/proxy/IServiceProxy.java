package com.ifengxue.rpc.client.proxy;


import java.io.Serializable;
import java.lang.reflect.InvocationHandler;
import java.util.Objects;

/**
 * 服务代理接口
 *
 * Created by LiuKeFeng on 2017-04-22.
 */
public interface IServiceProxy extends InvocationHandler, Serializable {
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
