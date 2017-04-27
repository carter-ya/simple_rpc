package com.ifengxue.rpc.server.factory;

import com.ifengxue.rpc.demo.DemoService;
import org.junit.Test;

/**
 * Created by LiuKeFeng on 2017-04-27.
 */
public class JavassistProxyFactoryTest {
    @Test
    public void testGetInstance() {
        System.setProperty("rpc.debug", "true");
        JavassistProxyFactory.getProxyInstance(DemoService.class);
    }
}