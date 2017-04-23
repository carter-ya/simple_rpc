package com.ifengxue.rpc.client.pool;

import io.netty.channel.Channel;
import org.apache.commons.pool2.KeyedObjectPool;
import org.apache.commons.pool2.ObjectPool;

/**
 *  {@link Channel} 池
 * Created by LiuKeFeng on 2017-04-22.
 */
public interface IChannelPool extends KeyedObjectPool<String, Channel> {

    @Override
    void returnObject(String key, Channel obj);
}
