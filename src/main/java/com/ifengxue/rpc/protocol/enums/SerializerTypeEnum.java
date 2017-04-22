package com.ifengxue.rpc.protocol.enums;

import com.ifengxue.rpc.protocol.ProtocolException;
import com.ifengxue.rpc.serialize.ISerializer;
import com.ifengxue.rpc.serialize.KryoSerializer;

import java.util.Arrays;

/**
 * 序列化类型枚举
 *
 * Created by LiuKeFeng on 2017-04-21.
 */
public enum SerializerTypeEnum {
    KRYO_SERIALIZER(Byte.valueOf("1")) {
        @Override
        public ISerializer getSerializer() {
            return new KryoSerializer();
        }
    };
    private final byte type;
    private ISerializer serializer;
    SerializerTypeEnum(byte type) {
        this.type = type;
    }

    public byte getType() {
        return type;
    }

    public abstract ISerializer getSerializer();

    public static SerializerTypeEnum getEnumByType(byte type) {
        return Arrays.stream(values()).filter(typeEnum -> typeEnum.type == type).findAny().orElseThrow(() -> new ProtocolException("序列化类型[" + type + "]暂不支持！"));
    }
}
