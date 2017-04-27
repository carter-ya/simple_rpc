package com.ifengxue.rpc.demo;

/**
 * Created by LiuKeFeng on 2017-04-24.
 */
public interface IDemoService {
    void sayHelloWorld();
    long currentServerTime();
    String echo(String echo);
    default Object echo(Object echo) {
        return echo;
    }
    void testThrowException() throws Exception;
    ValidateBean validate(ValidateBean bean);

    String waitForMe(long sleepSecond);

    String onlyInvokeNotNeedReturn(long sleepSecond);
}
