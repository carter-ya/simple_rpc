package com.ifengxue.rpc.client.proxy;

import com.ifengxue.rpc.client.RpcContext;
import com.ifengxue.rpc.client.async.AsyncRpcInvoker;
import com.ifengxue.rpc.demo.IDemoService;
import com.ifengxue.rpc.demo.ValidateBean;
import com.ifengxue.rpc.client.factory.ClientConfigFactory;
import com.ifengxue.rpc.protocol.IEchoService;
import org.apache.log4j.PropertyConfigurator;
import org.junit.Assert;
import org.junit.Test;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * Created by LiuKeFeng on 2017-04-22.
 */
public class ProxyFactoryTest {
    IDemoService demoService = ProxyFactory.create(IDemoService.class, "demo");
    static {
        try {
            PropertyConfigurator.configure(URLDecoder.decode(ProxyFactoryTest.class.getClassLoader().getResource("conf/log4j_c.properties").getFile(), "UTF-8"));
            ClientConfigFactory.initConfigFactory(URLDecoder.decode(ProxyFactoryTest.class.getClassLoader().getResource("conf/rpc_client.xml").getFile(), "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }
    /**
     *测试 {@link Object#toString()}, {@link Object#hashCode()} , {@link Object#equals(Object)} 代理
     */
    @Test
    public void testNotRpcMethodInvoke() {
        List list = ProxyFactory.create(List.class, "test");
        System.out.println(list.toString());
        System.out.println(list.hashCode());
        System.out.println(list.equals(list));
    }

    @Test
    public void testInvokeRpcMethod() throws UnsupportedEncodingException {
        demoService.sayHelloWorld();
//        System.out.println("currentServerTime:" + demoService.currentServerTime());
        System.out.println("echo:" + demoService.echo("Hello Server!"));
        try {
            demoService.testThrowException();
        } catch (Exception e) {
            e.printStackTrace();
        }
        ValidateBean bean = new ValidateBean();
        try {
            bean = demoService.validate(bean);
        } catch (Exception e) {
            System.out.println(e.getCause().getMessage());
        }
        bean.setAge(9);
        try {
            demoService.validate(bean);
        } catch (Exception e) {
            System.out.println(e.getCause().getMessage());
        }
        bean.setAge(10);
        bean = demoService.validate(bean);
        System.out.println(demoService.echo(new Date()));
        System.out.println(bean);
        IEchoService echoService = (IEchoService) demoService;
        System.out.println(echoService.$echo("Hello $echo!"));
    }

    @Test
    public void testInvokePrimitive() {
        demoService.currentServerTime();
    }

    @Test
    public void testAsyncInvoke() throws ExecutionException, InterruptedException {
        Future<String> future = AsyncRpcInvoker.asyncForResult(() -> demoService.waitForMe(5));
        TimeUnit.SECONDS.sleep(10);
        System.out.println(future.get());
    }

    @Test
    public void testOnlyInvoke() throws InterruptedException {
        AsyncRpcInvoker.asyncForNoneResult(() -> demoService.waitForMe(5));
        TimeUnit.SECONDS.sleep(10);
    }
}
