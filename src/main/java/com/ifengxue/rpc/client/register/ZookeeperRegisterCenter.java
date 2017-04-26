package com.ifengxue.rpc.client.register;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
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
    }
}
