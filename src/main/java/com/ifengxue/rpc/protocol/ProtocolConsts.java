package com.ifengxue.rpc.protocol;

/**
 * 协议包常量
 *
 * Created by LiuKeFeng on 2017-04-21.
 */
public final class ProtocolConsts {
    public static final byte VERSION = 1;
    /** 协议包头长度->包括4字节的总协议包长度 */
    public static final int PROTOCOL_PACKAGE_HEADER_LENGTH = 4 + 1 + 1 + 1 + 1 + 12;
}
