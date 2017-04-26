package com.ifengxue.rpc.client.pool;

import com.ifengxue.rpc.client.register.IRegisterCenter;
import io.netty.channel.Channel;
import org.apache.commons.pool2.impl.GenericKeyedObjectPool;
import org.apache.commons.pool2.impl.GenericKeyedObjectPoolConfig;

/**
 * 默认实现的{@link Channel}池
 *
 * Created by LiuKeFeng on 2017-04-23.
 */
public class SimpleChannelPool extends GenericKeyedObjectPool<String, Channel> implements IChannelPool {
    private final IRegisterCenter registerCenter;

    public SimpleChannelPool(IRegisterCenter registerCenter) {
        super(new SimpleBaseKeyedPooledChannelFactory(registerCenter), new EveryTimeBorrowChannelWillValidatePoolConfig());
        this.registerCenter = registerCenter;
    }

    public SimpleChannelPool(GenericKeyedObjectPoolConfig config, IRegisterCenter registerCenter) {
        super(new SimpleBaseKeyedPooledChannelFactory(registerCenter), config);
        this.registerCenter = registerCenter;
    }

    /**
     * 每次取{@link Channel} 都会测试是否可用
     */
    private static class EveryTimeBorrowChannelWillValidatePoolConfig extends GenericKeyedObjectPoolConfig {
        public EveryTimeBorrowChannelWillValidatePoolConfig() {
            super.setTestOnBorrow(true);
        }
    }
}
