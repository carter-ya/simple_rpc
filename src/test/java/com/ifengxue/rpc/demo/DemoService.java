package com.ifengxue.rpc.demo;

import com.ifengxue.rpc.protocol.annotation.RpcService;
import com.ifengxue.rpc.server.annotation.BeanValidate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by LiuKeFeng on 2017-04-24.
 */
@RpcService(IDemoService.class)
public class DemoService implements IDemoService {
    Logger logger = LoggerFactory.getLogger(getClass());
    @Override
    public void sayHelloWorld() {
        System.out.println("Hello World!");
    }

    @Override
    public long currentServerTime() {
        long current = System.currentTimeMillis();
        logger.info("current:" + current);
        return current;
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
    @BeanValidate
    public ValidateBean validate(ValidateBean bean) {
        return bean;
    }

}
