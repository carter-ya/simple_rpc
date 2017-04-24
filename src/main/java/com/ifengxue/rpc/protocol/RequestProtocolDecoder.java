package com.ifengxue.rpc.protocol;

import com.ifengxue.rpc.protocol.enums.CompressTypeEnum;
import com.ifengxue.rpc.protocol.enums.RequestProtocolTypeEnum;
import com.ifengxue.rpc.protocol.enums.SerializerTypeEnum;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 请求协议解析器
 *
 * Created by LiuKeFeng on 2017-04-21.
 */
public class RequestProtocolDecoder extends LengthFieldBasedFrameDecoder {
    private Logger logger = LoggerFactory.getLogger(getClass());
    public RequestProtocolDecoder() {
        this(1024 * 1024 * 10);
    }
    public RequestProtocolDecoder(int maxFrameLength) {
        // 包的总长度包含4字节长度，需要修正
        super(maxFrameLength, 0, 4, -4, 4);
    }

    @Override
    protected Object decode(ChannelHandlerContext ctx, ByteBuf in) throws Exception {
        ByteBuf decode = (ByteBuf) super.decode(ctx, in);
        if (decode == null) {
            return null;
        }
        byte version = decode.readByte();
        RequestProtocolTypeEnum requestProtocolTypeEnum = RequestProtocolTypeEnum.getEnumByType(decode.readByte());
        CompressTypeEnum compressTypeEnum = CompressTypeEnum.getEnumByType(decode.readByte());
        SerializerTypeEnum serializerTypeEnum = SerializerTypeEnum.getEnumByType(decode.readByte());
        decode.readInt();
        decode.readLong();
        byte[] buffer = new byte[decode.readableBytes()];
        decode.readBytes(buffer);
        if (compressTypeEnum != CompressTypeEnum.UNCOMPRESS) {
            buffer = compressTypeEnum.getCompress().decompress(buffer);
        }
        RequestProtocol requestProtocol = serializerTypeEnum.getSerializer().deserialize(buffer);
        RequestContext requestContext = new RequestContext(version, requestProtocolTypeEnum, compressTypeEnum, serializerTypeEnum, requestProtocol);

        return requestContext;
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.channel().close();
        logger.error("请求协议解码失败:" + cause.getMessage(), cause);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
    }
}
