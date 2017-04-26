package com.ifengxue.rpc.protocol;

/**
 * 回声测试
 *
 * Created by LiuKeFeng on 2017-04-26.
 */
public interface EchoService {
    /**
     * 回声测试
     * @param echo 回声
     * @return 原阳返回
     */
    String $echo(String echo);
}
