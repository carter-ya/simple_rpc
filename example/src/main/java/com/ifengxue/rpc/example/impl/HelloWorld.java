package com.ifengxue.rpc.example.impl;

import com.ifengxue.rpc.example.service.IHelloWorld;
import com.ifengxue.rpc.protocol.annotation.RpcService;

/**
 * Created by LiuKeFeng on 2017-04-30.
 */
@RpcService(IHelloWorld.class)
public class HelloWorld implements IHelloWorld {
    public void sayHelloWorld() {
        System.out.println("Hello World!");
    }
}
