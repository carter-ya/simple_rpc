package com.ifengxue.rpc.protocol;

import com.ifengxue.rpc.protocol.enums.CompressTypeEnum;
import com.ifengxue.rpc.protocol.enums.SerializerTypeEnum;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 响应协议解码器
 *
 * Created by LiuKeFeng on 2017-04-22.
 */
public class ResponseProtocolDecoder extends LengthFieldBasedFrameDecoder {
    private Logger logger = LoggerFactory.getLogger(getClass());
    public ResponseProtocolDecoder() {
        this(1024 * 1024 * 10);
    }
    public ResponseProtocolDecoder(int maxFrameLength) {
        // 包的总长度包含4字节长度，需要修正
        super(maxFrameLength, 0, 4, -4, 4);
    }

    @Override
    protected Object decode(ChannelHandlerContext ctx, ByteBuf in) throws Exception {
        ByteBuf decode = (ByteBuf) super.decode(ctx, in);
        if (decode == null) {
            return null;
        }

        //读取版本号
        decode.readByte();
        //读取请求协议类型
        decode.readByte();
        CompressTypeEnum compressTypeEnum = CompressTypeEnum.getEnumByType(decode.readByte());
        SerializerTypeEnum serializerTypeEnum = SerializerTypeEnum.getEnumByType(decode.readByte());
        decode.readInt();
        decode.readLong();
        byte[] buffer = new byte[decode.readableBytes()];
        decode.readBytes(buffer);
        if (compressTypeEnum != CompressTypeEnum.UNCOMPRESS) {
            buffer = compressTypeEnum.getCompress().decompress(buffer);
        }
        ResponseProtocol responseProtocol = serializerTypeEnum.getSerializer().deserialize(buffer);
        return responseProtocol;
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.channel().close();
        logger.error("响应协议解码出错:" + cause.getMessage(), cause);
    }
}
