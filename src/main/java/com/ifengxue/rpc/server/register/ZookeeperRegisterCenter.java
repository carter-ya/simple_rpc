package com.ifengxue.rpc.server.register;

import com.ifengxue.rpc.protocol.ProtocolConsts;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 发布服务到zookeeper
 *
 * Created by LiuKeFeng on 2017-04-26.
 */
public class ZookeeperRegisterCenter implements IRegisterCenter {
    private Logger logger = LoggerFactory.getLogger(getClass());
    private CuratorFramework curatorFramework;
    private String serviceFullPath;
    @Override
    public void register(String serviceName, String host, int port) {
        String producerPath = ProtocolConsts.ZOOKEEPER_SERVICE_ROOT_PATH + "/" + serviceName;
        logger.info("开始注册服务:" + serviceName);
        try {
            //创建永久节点
            if (curatorFramework.checkExists().forPath(ProtocolConsts.ZOOKEEPER_SERVICE_ROOT_PATH) == null) {
                curatorFramework
                        .create()
                        .creatingParentsIfNeeded()
                        .withMode(CreateMode.PERSISTENT)
                        .forPath(ProtocolConsts.ZOOKEEPER_SERVICE_ROOT_PATH);
            }
            serviceFullPath = producerPath + "/" + host + ":" + port;
            if (curatorFramework.checkExists().forPath(serviceFullPath) != null) {
                logger.info("服务节点:{}已存在，删除...", serviceFullPath);
                curatorFramework.delete().forPath(serviceFullPath);
            }
            curatorFramework.create().creatingParentContainersIfNeeded().withMode(CreateMode.EPHEMERAL).forPath(serviceFullPath);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
        logger.info("注册服务成功:" + serviceName);
    }

    @Override
    public void unregister() {
        try {
            curatorFramework.delete().forPath(serviceFullPath);
        } catch (Exception e) {
            logger.error("移除zookeeper注册失败:" + e.getMessage(), e);
        }
    }

    @Override
    public void init(Element registerElement) {
        List<Element> elements = registerElement.elements("property");
        Map<String, String> propertyMap = new HashMap<>(elements.size());
        elements.forEach(element -> propertyMap.put(element.attributeValue("name"), element.attributeValue("value")));
        String connectionString = Optional.ofNullable(propertyMap.get("connectionString")).orElseThrow(() -> new IllegalStateException("缺失参数:connectionString"));
        curatorFramework = CuratorFrameworkFactory.newClient(connectionString,
                new ExponentialBackoffRetry(Integer.parseInt(propertyMap.getOrDefault("baseSleepTimeMS", "1000")),
                        Integer.parseInt(propertyMap.getOrDefault("maxRetries", "3"))));
        curatorFramework.start();
        logger.info("服务端已连接注册中心:" + connectionString);
    }

    @Override
    public void close() {
        logger.info("关闭zookeeper连接");
        curatorFramework.close();
    }
}
