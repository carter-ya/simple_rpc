package com.ifengxue.rpc.protocol.enums;

import com.ifengxue.rpc.protocol.RequestProtocolException;

import java.util.Arrays;
/**
 * 请求的协议类型
 */
public enum RequestProtocolTypeEnum {
    /**方法调用*/
    METHOD_INVOKE(Byte.valueOf("1")),
    /**PING/PONG测试*/
    PING(Byte.valueOf("2"));
    private final byte type;
    RequestProtocolTypeEnum(byte type) {
        this.type = type;
    }

    public byte getType() {
        return type;
    }

    public static RequestProtocolTypeEnum getEnumByType(byte type) {
        return Arrays.stream(values())
                .filter(typeEnum -> typeEnum.type == type)
                .findAny()
                .orElseThrow(() -> new RequestProtocolException("请求协议类型[" + type + "]暂时不支持！"));
    }
}