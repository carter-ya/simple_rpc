package com.ifengxue.rpc.factory;

import com.ifengxue.rpc.client.pool.IChannelFactory;
import com.ifengxue.rpc.client.pool.SimpleChannelFactory;
import com.ifengxue.rpc.protocol.enums.CompressTypeEnum;
import com.ifengxue.rpc.protocol.enums.SerializerTypeEnum;

/**
 * Created by LiuKeFeng on 2017-04-21.
 */
public class ClientConfigFactory {
    private static final ClientConfigFactory INSTANCE = new ClientConfigFactory();
    private ClientConfigFactory() {}
    public static ClientConfigFactory getInstance() {
        return INSTANCE;
    }

    public long compressRequestIfLengthGreaterTo() {
        return 1024 * 1024 * 10;
    }

    public SerializerTypeEnum getSerializerTypeEnum() {
        return SerializerTypeEnum.KRYO_SERIALIZER;
    }

    public CompressTypeEnum getCompressTypeEnum() {
        return CompressTypeEnum.DEFLATER;
    }

    public IChannelFactory getChannelFactory() {
        return new SimpleChannelFactory();
    }
}
