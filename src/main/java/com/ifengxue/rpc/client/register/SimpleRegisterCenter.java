package com.ifengxue.rpc.client.register;

import java.util.List;
import java.util.NoSuchElementException;

/**
 * Created by liukefeng on 2017-04-23.
 */
public class SimpleRegisterCenter implements IRegisterCenter {
    @Override
    public ServiceNode getAvailableServiceNode(String serviceNodeName) throws NoSuchElementException {
        return null;
    }

    @Override
    public List<ServiceNode> listAvailableServiceNode(String serviceNodeName) throws NoSuchElementException {
        return null;
    }
}
