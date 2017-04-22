package com.ifengxue.rpc.client.pool;

import io.netty.channel.Channel;
import org.apache.commons.pool2.KeyedPooledObjectFactory;
import org.apache.commons.pool2.PooledObject;

/**
 * {@link Channel}pool工厂
 *
 * Created by LiuKeFeng on 2017-04-22.
 */
public interface IChannelPoolFactory extends KeyedPooledObjectFactory<IChannelPoolFactory.ServiceNode, Channel> {

    @Override
    default boolean validateObject(ServiceNode serviceNode, PooledObject<Channel> pooledObject) {
        return pooledObject.getObject().isActive();
    }

    @Override
    default void activateObject(ServiceNode serviceNode, PooledObject<Channel> pooledObject) throws Exception {

    }

    @Override
    default void passivateObject(ServiceNode serviceNode, PooledObject<Channel> pooledObject) throws Exception {

    }

    /**
     * 服务节点封装
     */
    class ServiceNode {
        private final String serviceNode;
        private final String host;
        private final int port;

        public ServiceNode(String serviceNode, String host, int port) {
            this.serviceNode = serviceNode;
            this.host = host;
            this.port = port;
        }

        public String getServiceNode() {
            return serviceNode;
        }

        public String getHost() {
            return host;
        }

        public int getPort() {
            return port;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            ServiceNode that = (ServiceNode) o;

            if (port != that.port) return false;
            if (!serviceNode.equals(that.serviceNode)) return false;
            return host.equals(that.host);
        }

        @Override
        public int hashCode() {
            int result = serviceNode.hashCode();
            result = 31 * result + host.hashCode();
            result = 31 * result + port;
            return result;
        }

        @Override
        public String toString() {
            return "ServiceNode{" +
                    "serviceNode='" + serviceNode + '\'' +
                    ", host='" + host + '\'' +
                    ", port=" + port +
                    '}';
        }
    }
}
