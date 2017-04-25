package com.ifengxue.rpc.client.register;

import org.dom4j.Element;

import java.util.List;
import java.util.NoSuchElementException;

/**
 * zookeeper实现的注册中心
 * Created by LiuKeFeng on 2017-04-25.
 */
public class ZookeeperRegisterCenter implements IRegisterCenter {
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

    }
}
