package com.ifengxue.rpc.protocol.enums;

import com.ifengxue.rpc.compress.DeflaterCompress;
import com.ifengxue.rpc.compress.ICompress;
import com.ifengxue.rpc.protocol.ProtocolException;

import java.util.Arrays;

/**
 * 压缩类型枚举
 *
 * Created by LiuKeFeng on 2017-04-20.
 */
public enum CompressTypeEnum {
    /** 不压缩 */
    UNCOMPRESS(Byte.valueOf("0")) {
        public ICompress getCompress() {
            throw new UnsupportedOperationException();
        }
    },
    /** deflater 压缩算法 */
    DEFLATER(Byte.valueOf("1")) {
        public ICompress getCompress() {
            return new DeflaterCompress();
        }
    };
    private final byte type;
    CompressTypeEnum(byte type) {
        this.type = type;
    }

    public byte getType() {
        return type;
    }
    /** 获取压缩算法实现 */
    public abstract ICompress getCompress();

    public static CompressTypeEnum getEnumByType(byte type) {
        return Arrays.stream(values())
                .filter(typeEnum -> typeEnum.type == type)
                .findAny()
                .orElseThrow(() -> new ProtocolException("压缩类型[" + type + "]暂时不支持！"));
    }
}
