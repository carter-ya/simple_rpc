package com.ifengxue.rpc.protocol;

import io.netty.handler.codec.LengthFieldBasedFrameDecoder;

/**
 * Created by LiuKeFeng on 2017-04-22.
 */
public class ResponseProtocolDecoder extends LengthFieldBasedFrameDecoder {
    public ResponseProtocolDecoder() {
        this(1024 * 1024 * 10);
    }
    public ResponseProtocolDecoder(int maxFrameLength) {
        // 包的总长度包含4字节长度，需要修正
        super(maxFrameLength, 0, 4, -4, 4);
    }

}
