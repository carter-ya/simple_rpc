package com.ifengxue.rpc.client.register;

import com.ifengxue.rpc.protocol.ProtocolConsts;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.TreeCache;
import org.apache.curator.framework.recipes.cache.TreeCacheEvent;
import org.apache.curator.framework.recipes.cache.TreeCacheListener;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ConcurrentSkipListSet;

/**
 * zookeeper实现的注册中心
 * Created by LiuKeFeng on 2017-04-25.
 */
public class ZookeeperRegisterCenter implements IRegisterCenter {
    private Logger logger = LoggerFactory.getLogger(getClass());
    private CuratorFramework curatorFramework;
    private TreeCache treeCache;
    private ConcurrentMap<String, Set<ServiceNode>> serviceDeployMap = new ConcurrentHashMap<>();
    @Override
    public ServiceNode getAvailableServiceNode(String serviceNodeName) throws NoSuchElementException {
        return serviceDeployMap.getOrDefault(serviceNodeName, Collections.emptySet())
                .stream()
                .findAny()
                .orElseThrow(() -> new NoSuchElementException(serviceNodeName + "不存在"));
    }

    @Override
    public List<ServiceNode> listAvailableServiceNode(String serviceNodeName) throws NoSuchElementException {
        return new ArrayList<>(Optional.ofNullable(serviceDeployMap.get(serviceNodeName))
                        .orElseThrow(() -> new NoSuchElementException(serviceNodeName + "不存在")));
    }

    @Override
    public void init(Element registerCenterElement) {
        List<Element> elements = registerCenterElement.elements("property");
        Map<String, String> propertyMap = new HashMap<>(elements.size());
        elements.forEach(element -> propertyMap.put(element.attributeValue("name"), element.attributeValue("value")));
        String connectionString = Optional.ofNullable(propertyMap.get("connectionString"))
                .orElseThrow(() -> new IllegalStateException("缺失参数:connectionString"));
        curatorFramework = CuratorFrameworkFactory.newClient(connectionString,
                new ExponentialBackoffRetry(Integer.parseInt(propertyMap.getOrDefault("baseSleepTimeMS", "1000")),
                        Integer.parseInt(propertyMap.getOrDefault("maxRetries", "3"))));
        curatorFramework.start();
        logger.info("连接Zookeeper成功:" + connectionString);
        try {
            //读取已经注册的服务
            List<String> serviceList = curatorFramework.getChildren().forPath(ProtocolConsts.ZOOKEEPER_SERVICE_ROOT_PATH);
            for (String service : serviceList) {
                List<String> serviceDeployList = curatorFramework.getChildren()
                        .forPath(ProtocolConsts.ZOOKEEPER_SERVICE_ROOT_PATH + "/" + service);
                List<ServiceNode> serviceNodeList = new ArrayList<>(serviceDeployList.size());
                serviceDeployList.forEach(serviceDeploy -> serviceNodeList.add(new ServiceNode(
                        service,
                        serviceDeploy.substring(0, serviceDeploy.indexOf(':')),
                        Integer.parseInt(serviceDeploy.substring(serviceDeploy.indexOf(':') + 1)))));
                serviceDeployMap.put(service, new ConcurrentSkipListSet<>(serviceNodeList));
            }
            //注册服务监听器
            treeCache = new TreeCache(curatorFramework, ProtocolConsts.ZOOKEEPER_SERVICE_ROOT_PATH);
            treeCache.getListenable().addListener(new ServiceChangeListener());
            treeCache.start();
        } catch (Exception e) {
            logger.error("监听服务列表错误:" + e.getMessage(), e);
        }
    }

    @Override
    public void close() {
        treeCache.close();
        curatorFramework.close();
        logger.info("断开Zookeeper成功");
    }

    private class ServiceChangeListener implements TreeCacheListener {

        @Override
        public void childEvent(CuratorFramework client, TreeCacheEvent event) throws Exception {
            TreeCacheEvent.Type type = event.getType();
            //只关注节点增加/移除事件
            if (type != TreeCacheEvent.Type.NODE_ADDED || type != TreeCacheEvent.Type.NODE_REMOVED) {
                return;
            }

            String changePath = event.getData().getPath().replace(ProtocolConsts.ZOOKEEPER_SERVICE_ROOT_PATH, "");
            String[] changePathAry = changePath.split("/");

            if (type == TreeCacheEvent.Type.NODE_REMOVED) {
                logger.warn("移除注册服务:{}" + event.getData().getPath());
                nodeRemoved(changePathAry);
            } else {
                logger.info("新增注册服务:{}" + event.getData().getPath());
                nodeAdded(changePathAry);
            }
        }

        private void nodeRemoved(String[] changePathAry) {
            if (changePathAry.length == 1) {
                serviceDeployMap.remove(changePathAry[0]);
            } else {
                Set<ServiceNode> serviceNodeSet = serviceDeployMap.get(changePathAry[0]);
                Iterator<ServiceNode> itr = serviceNodeSet.iterator();
                while (itr.hasNext()) {
                    ServiceNode serviceNode = itr.next();
                    String connectStr = serviceNode.getHost() + ":" + serviceNode.getPort();
                    if (connectStr.equals(changePathAry[1])) {
                        itr.remove();
                        break;
                    }
                }
            }
        }

        private void nodeAdded(String[] changePathAry) {
            if (changePathAry.length == 1) {
                serviceDeployMap.put(changePathAry[0], new ConcurrentSkipListSet<>());
            } else {
                Set<ServiceNode> serviceNodeSet = serviceDeployMap.get(changePathAry[0]);
                serviceNodeSet.add(
                        new ServiceNode(
                                changePathAry[0],
                                changePathAry[1].substring(0, changePathAry[1].indexOf(':')),
                                Integer.parseInt(changePathAry[1].substring(changePathAry[1].indexOf(':') + 1))));
            }
        }
    }
}