package com.ifengxue.rpc.client.register;

import com.ifengxue.rpc.protocol.ProtocolConsts;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.api.CuratorWatcher;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * zookeeper实现的注册中心
 * Created by LiuKeFeng on 2017-04-25.
 */
public class ZookeeperRegisterCenter implements IRegisterCenter {
    private Logger logger = LoggerFactory.getLogger(getClass());
    private CuratorFramework curatorFramework;
    //TODO: 实现zookeeper中心
    @Override
    public ServiceNode getAvailableServiceNode(String serviceNodeName) throws NoSuchElementException {
        return null;
    }

    @Override
    public List<ServiceNode> listAvailableServiceNode(String serviceNodeName) throws NoSuchElementException {
        return null;
    }

    @Override
    public void init(Element registerCenterElement) {
        List<Element> elements = registerCenterElement.elements("property");
        Map<String, String> propertyMap = new HashMap<>(elements.size());
        elements.forEach(element -> propertyMap.put(element.attributeValue("name"), element.attributeValue("value")));
        String connectionString = Optional.ofNullable(propertyMap.get("connectionString")).orElseThrow(() -> new IllegalStateException("缺失参数:connectionString"));
        curatorFramework = CuratorFrameworkFactory.newClient(connectionString,
                new ExponentialBackoffRetry(Integer.parseInt(propertyMap.getOrDefault("baseSleepTimeMS", "1000")),
                        Integer.parseInt(propertyMap.getOrDefault("maxRetries", "3"))));
        curatorFramework.start();
        logger.info("连接Zookeeper成功:" + connectionString);
        try {
            curatorFramework.getChildren().usingWatcher(new ServiceWatcher()).inBackground().forPath(ProtocolConsts.ZOOKEEPER_SERVICE_ROOT_PATH);
        } catch (Exception e) {
            logger.error("监听服务列表错误:" + e.getMessage(), e);
        }
    }

    @Override
    public void close() {
        curatorFramework.close();
        logger.info("断开Zookeeper成功");
    }

    /**
     * 服务观察者
     */
    private static class ServiceWatcher implements CuratorWatcher {
        @Override
        public void process(WatchedEvent event) throws Exception {
            //节点创建
            if (event.getType().getIntValue() == Watcher.Event.EventType.NodeCreated.getIntValue()) {

            }
            //节点删除
            if (event.getType().getIntValue() == Watcher.Event.EventType.NodeDeleted.getIntValue()) {

            }
            //子节点发生变化
            if (event.getType().getIntValue() == Watcher.Event.EventType.NodeChildrenChanged.getIntValue()) {

            }
        }
    }

    /**
     * 服务部署观察者
     */
    private static class ServiceDeployWatcher implements CuratorWatcher {

        @Override
        public void process(WatchedEvent event) throws Exception {

        }
    }
}