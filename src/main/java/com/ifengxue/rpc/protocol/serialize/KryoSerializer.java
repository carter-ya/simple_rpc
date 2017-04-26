package com.ifengxue.rpc.protocol.serialize;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

/**
 * 使用kryo实现序列化与反序列化
 *
 * Created by LiuKeFeng on 2017-04-20.
 */
public class KryoSerializer implements ISerializer {
    public <T> byte[] serialize(T object) {
        Kryo kryo = new Kryo();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        Output output = new Output(byteArrayOutputStream);
        kryo.writeClassAndObject(output, object);
        output.close();
        byte[] buffer = byteArrayOutputStream.toByteArray();
        return buffer;
    }

    public <T> T deserialize(byte[] buffer) {
        Kryo kryo = new Kryo();
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(buffer);
        Input input = new Input(byteArrayInputStream);
        input.close();
        Object object = kryo.readClassAndObject(input);
        return (T) object;
    }
}
