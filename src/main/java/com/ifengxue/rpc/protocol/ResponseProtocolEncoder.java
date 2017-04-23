package com.ifengxue.rpc.protocol;

import com.ifengxue.rpc.protocol.enums.CompressTypeEnum;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import io.netty.util.AttributeKey;

/**
 * 响应协议编码器
 *
 * Created by LiuKeFeng on 2017-04-23.
 */
public class ResponseProtocolEncoder extends MessageToByteEncoder<ResponseProtocol> {
    @Override
    protected void encode(ChannelHandlerContext ctx, ResponseProtocol responseProtocol, ByteBuf out) throws Exception {
        RequestContext requestContext = (RequestContext) ctx.attr(AttributeKey.valueOf(ProtocolConsts.BIND_REQUEST_PROTOCOL_TO_CONTEXT)).getAndRemove();
        byte[] buffer = requestContext.getSerializerTypeEnum().getSerializer().serialize(responseProtocol);
        if (requestContext.getCompressTypeEnum() != CompressTypeEnum.UNCOMPRESS) {
            buffer = requestContext.getCompressTypeEnum().getCompress().compress(buffer);
        }

        int totalLength = ProtocolConsts.PROTOCOL_PACKAGE_HEADER_LENGTH + buffer.length;
        out.writeInt(totalLength);
        out.writeByte(requestContext.getVersion());
        out.writeByte(requestContext.getRequestProtocolTypeEnum().getType());
        out.writeByte(requestContext.getCompressTypeEnum().getType());
        out.writeByte(requestContext.getSerializerTypeEnum().getType());
        out.writeInt(0);
        out.writeLong(0L);
        out.writeBytes(buffer);
    }
}
