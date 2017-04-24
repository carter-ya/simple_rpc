package com.ifengxue.rpc.demo;

/**
 * Created by LiuKeFeng on 2017-04-24.
 */
public interface IDemoService {
    void sayHelloWorld();
    long currentServerTime();
    String echo(String echo);
}
