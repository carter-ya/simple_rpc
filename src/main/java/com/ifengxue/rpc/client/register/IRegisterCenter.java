package com.ifengxue.rpc.client.register;

import org.dom4j.Element;

import java.util.List;
import java.util.NoSuchElementException;

/**
 * 服务注册中心
 *
 * Created by liukefeng on 2017-04-23.
 */
public interface IRegisterCenter {
    /**
     * 获取可用的服务节点
     * @param serviceNodeName 节点名称
     * @return
     * @throws NoSuchElementException 没有可用的服务节点
     */
    ServiceNode getAvailableServiceNode(String serviceNodeName) throws NoSuchElementException;

    /**
     * 获取所有可用的服务节点
     * @param serviceNodeName
     * @return
     * @throws NoSuchElementException
     */
    List<ServiceNode> listAvailableServiceNode(String serviceNodeName) throws NoSuchElementException;

    /**
     * 关闭注册中心
     */
    default void close() {

    }

    /**
     * 初始化注册中心
     * @param registerCenterElement 注册中心配置节点
     */
    void init(Element registerCenterElement);
    /** 服务节点 */
    class ServiceNode implements Comparable<ServiceNode> {
        /** 节点名称 */
        private final String serviceNodeName;
        /** 节点的地址 */
        private final String host;
        /** 节点开放的端口 */
        private final int port;

        public ServiceNode(String serviceNodeName, String host, int port) {
            this.serviceNodeName = serviceNodeName;
            this.host = host;
            this.port = port;
        }

        public String getServiceNodeName() {
            return serviceNodeName;
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
            if (!serviceNodeName.equals(that.serviceNodeName)) return false;
            return host.equals(that.host);
        }

        @Override
        public int hashCode() {
            int result = serviceNodeName.hashCode();
            result = 31 * result + host.hashCode();
            result = 31 * result + port;
            return result;
        }

        @Override
        public String toString() {
            return "ServiceNode{" +
                    "serviceNodeName='" + serviceNodeName + '\'' +
                    ", host='" + host + '\'' +
                    ", port=" + port +
                    '}';
        }

        @Override
        public int compareTo(ServiceNode o) {
            String thisStr = "tcp://" + serviceNodeName + "/" + host + ":" + port;
            String otherStr = "tcp://" + o.getServiceNodeName() + "/" + o.getHost() + ":" + o.getPort();
            return thisStr.compareTo(otherStr);
        }
    }
}
