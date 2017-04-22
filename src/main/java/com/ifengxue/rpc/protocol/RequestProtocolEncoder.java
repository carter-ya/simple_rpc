package com.ifengxue.rpc.protocol;

import com.ifengxue.rpc.factory.ClientConfigFactory;
import com.ifengxue.rpc.protocol.enums.CompressTypeEnum;
import com.ifengxue.rpc.protocol.enums.SerializerTypeEnum;
import com.ifengxue.rpc.serialize.ISerializer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * 请求协议编码器
 *
 * Created by LiuKeFeng on 2017-04-21.
 */
public class RequestProtocolEncoder extends MessageToByteEncoder<RequestProtocol> {
    private SerializerTypeEnum serializerTypeEnum = ClientConfigFactory.getInstance().getSerializerTypeEnum();
    private ISerializer serializer = serializerTypeEnum.getSerializer();
    private CompressTypeEnum compressTypeEnum = ClientConfigFactory.getInstance().getCompressTypeEnum();
    private long compressRequestIfLengthGreaterTo = ClientConfigFactory.getInstance().compressRequestIfLengthGreaterTo();
    @Override
    protected void encode(ChannelHandlerContext ctx, RequestProtocol msg, ByteBuf out) throws Exception {
        byte[] buffer = serializer.serialize(msg);
        boolean isCompress = false;
        if (buffer.length > compressRequestIfLengthGreaterTo && compressTypeEnum != CompressTypeEnum.UNCOMPRESS) {
            buffer = compressTypeEnum.getCompress().compress(buffer);
            isCompress = true;
        }
        int totalCount = ProtocolConsts.PROTOCOL_PACKAGE_HEADER_LENGTH + buffer.length;
        out.writeInt(totalCount);
        out.writeByte(ProtocolConsts.VERSION);
        out.writeByte(msg.getRequestProtocolTypeEnum().getType());
        out.writeByte(isCompress ? compressTypeEnum.getType() : CompressTypeEnum.UNCOMPRESS.getType());
        out.writeByte(serializerTypeEnum.getType());
        out.writeInt(0);
        out.writeLong(0L);
        out.writeBytes(buffer);
    }
}
