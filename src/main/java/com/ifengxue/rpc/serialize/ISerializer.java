package com.ifengxue.rpc.serialize;

/**
 *  序列化与反序列化接口
 *
 * Created by LiuKeFeng on 2017-04-20.
 */
public interface ISerializer {

    /**
     *  序列化对象为byte[]
     * @param object 要被序列化的对象
     * @param <T>
     * @return
     */
    <T> byte[] serialize(T object);

    /**
     * 反序列化byte[]为对象
     * @param buffer
     * @param <T>
     * @return
     */
    <T> T deserialize(byte[] buffer);
}
