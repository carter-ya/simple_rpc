package com.ifengxue.rpc.client.async;

/**
 * 异步配置接口
 *
 * Created by LiuKeFeng on 2017-05-20.
 */
public interface IAsyncConfig {
    /** 默认的最大等待时间 */
    long DEFAULT_MAX_WAIT_TIME_MILLIS = 19_000;
    /**
     * 获取最大异步等待时间
     *
     * @return
     */
    default long getMaxWaitTimeMillis() {
        return DEFAULT_MAX_WAIT_TIME_MILLIS;
    }
}
