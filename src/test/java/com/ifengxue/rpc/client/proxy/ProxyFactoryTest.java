package com.ifengxue.rpc.client.proxy;

import org.junit.Test;

import java.util.List;

/**
 * Created by LiuKeFeng on 2017-04-22.
 */
public class ProxyFactoryTest {
    @Test
    public void testCreate() {
        List list = ProxyFactory.create(List.class, "test");
        System.out.println(list.toString());
        System.out.println(list.hashCode());
        System.out.println(list.equals(list));
    }
}
