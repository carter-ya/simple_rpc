package com.ifengxue.rpc.client.register;

import org.dom4j.Element;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * 默认实现的注册中心
 *
 * Created by liukefeng on 2017-04-23.
 */
public class SimpleRegisterCenter implements IRegisterCenter {
    private Map<String, List<ServiceNode>> serviceNodeMap;
    private Random random = new Random();

    @Override
    public ServiceNode getAvailableServiceNode(String serviceNodeName) throws NoSuchElementException {
        List<ServiceNode> serviceNodes = listAvailableServiceNode(serviceNodeName);
        int size = serviceNodes.size();
        int index = random.nextInt(size);
        return serviceNodes.get(index);
    }

    @Override
    public List<ServiceNode> listAvailableServiceNode(String serviceNodeName) throws NoSuchElementException {
        return Optional.ofNullable(serviceNodeMap.get(serviceNodeName)).orElseThrow(NoSuchElementException::new);
    }

    @Override
    public void init(Element registerCenterElement) {
        List<Element> elements = registerCenterElement.element("service-nodes").elements("service-node");
        List<ServiceNode> serviceNodes = new ArrayList<>(elements.size());
        elements.stream().forEach(element -> serviceNodes.add(
                new ServiceNode(element.attributeValue("serviceName"),
                        element.attributeValue("host"),
                        Integer.parseInt(element.attributeValue("port")))));
        Map<String, List<ServiceNode>> serviceNodeMap = serviceNodes.stream().collect(Collectors.groupingBy(ServiceNode::getServiceNodeName));
        this.serviceNodeMap = new ConcurrentHashMap<>(serviceNodeMap);
    }
}
