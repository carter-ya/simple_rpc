package com.ifengxue.rpc.client.async;

/**
 * 异步配置实现类
 * Created by LiuKeFeng on 2017-05-20.
 */
public class AsyncConfig implements IAsyncConfig {
    private long maxWaitTimeMillis;

    @Override
    public long getMaxWaitTimeMillis() {
        return maxWaitTimeMillis < 0 ? DEFAULT_MAX_WAIT_TIME_MILLIS : maxWaitTimeMillis;
    }

    public void setMaxWaitTimeMillis(long maxWaitTimeMillis) {
        this.maxWaitTimeMillis = maxWaitTimeMillis;
    }
}
