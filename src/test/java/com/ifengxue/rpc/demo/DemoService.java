package com.ifengxue.rpc.demo;

import com.ifengxue.rpc.protocol.annotation.RpcService;

/**
 * Created by LiuKeFeng on 2017-04-24.
 */
@RpcService(IDemoService.class)
public class DemoService implements IDemoService {
    @Override
    public void sayHelloWorld() {
        System.out.println("Hello World!");
    }

    @Override
    public long currentServerTime() {
        return System.currentTimeMillis();
    }

    @Override
    public String echo(String echo) {
        return echo;
    }

    @Override
    public void testThrowException() throws Exception {
        throw new IllegalStateException("我喜欢抛异常");
    }

    @Override
    public ValidateBean validate(ValidateBean bean) {
        return bean;
    }
}
