package com.ifengxue.rpc.protocol.enums;

import com.ifengxue.rpc.protocol.ProtocolException;
import com.ifengxue.rpc.protocol.serialize.ISerializer;
import com.ifengxue.rpc.protocol.serialize.KryoSerializer;

import java.util.Arrays;

/**
 * 序列化类型枚举
 *
 * Created by LiuKeFeng on 2017-04-21.
 */
public enum SerializerTypeEnum {
    KRYO_SERIALIZER(Byte.valueOf("1"), "kryo") {
        @Override
        public ISerializer getSerializer() {
            return new KryoSerializer();
        }
    },
    JSON_RPC_SERIALIZER(Byte.valueOf("2"), "json-rpc") {
        @Override
        public ISerializer getSerializer() {
            return null;
        }
    };
    private final byte type;
    private final String name;
    private ISerializer serializer;
    SerializerTypeEnum(byte type, String name) {
        this.type = type;
        this.name = name;
    }

    public byte getType() {
        return type;
    }

    public abstract ISerializer getSerializer();

    public static SerializerTypeEnum getEnumByType(byte type) {
        return Arrays.stream(values()).filter(typeEnum -> typeEnum.type == type).findAny().orElseThrow(() -> new ProtocolException("序列化类型[" + type + "]暂不支持！"));
    }

    public static SerializerTypeEnum getEnumByName(String name) {
        return Arrays.stream(values()).filter(typeEnum -> typeEnum.name.equalsIgnoreCase(name)).findAny().orElseThrow(() -> new ProtocolException("序列化类型[" + name + "]暂不支持！"));
    }
}
