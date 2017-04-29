package com.ifengxue.rpc.client.async;

/**
 * 空返回值接口
 *
 * Created by LiuKeFeng on 2017-04-29.
 */
@FunctionalInterface
public interface ReturnNoneCallable {
    /**
     * 执行方法调用
     * @throws Exception
     */
    void call() throws Exception;
}
