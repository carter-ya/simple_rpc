package com.ifengxue.rpc.serialize;

import org.junit.Test;

import java.time.LocalDateTime;

/**
 * Created by LiuKeFeng on 2017-04-20.
 */
public class KryoSerializerTest {
    private ISerializer serializer = new KryoSerializer();
    @Test
    public  void testSerializeAndDeSerialize() {
        LocalDateTime now = LocalDateTime.now();
        byte[] buffer = serializer.serialize(now);
        LocalDateTime serializeNow = serializer.deserialize(buffer);
        System.out.println(serializeNow);
        Throwable throwable = new Throwable();
        buffer = serializer.serialize(throwable);
        throwable = serializer.deserialize(buffer);
        throwable.printStackTrace();
        IllegalStateException illegalStateException = new IllegalStateException(throwable);
        IllegalArgumentException illegalArgumentException = new IllegalArgumentException(illegalStateException);
        buffer = serializer.serialize(illegalArgumentException);
        IllegalArgumentException newException = serializer.deserialize(buffer);
        newException.printStackTrace();
    }
}
