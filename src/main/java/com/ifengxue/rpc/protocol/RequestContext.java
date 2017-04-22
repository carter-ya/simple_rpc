package com.ifengxue.rpc.protocol;

import com.ifengxue.rpc.protocol.enums.CompressTypeEnum;
import com.ifengxue.rpc.protocol.enums.RequestProtocolTypeEnum;
import com.ifengxue.rpc.protocol.enums.SerializerTypeEnum;

import java.io.Serializable;

/**
 * 请求上下文
 *
 * Created by LiuKeFeng on 2017-04-21.
 */
public class RequestContext implements Serializable {
    private static final long serialVersionUID = -3973891491859095113L;
    private int version;
    private RequestProtocolTypeEnum requestProtocolTypeEnum;
    private CompressTypeEnum compressTypeEnum;
    private SerializerTypeEnum serializerTypeEnum;
    private RequestProtocol requestProtocol;
    private RequestContext() {}

    public RequestContext(int version, RequestProtocolTypeEnum requestProtocolTypeEnum, CompressTypeEnum compressTypeEnum, SerializerTypeEnum serializerTypeEnum, RequestProtocol requestProtocol) {
        this.version = version;
        this.requestProtocolTypeEnum = requestProtocolTypeEnum;
        this.compressTypeEnum = compressTypeEnum;
        this.serializerTypeEnum = serializerTypeEnum;
        this.requestProtocol = requestProtocol;
    }
    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public RequestProtocolTypeEnum getRequestProtocolTypeEnum() {
        return requestProtocolTypeEnum;
    }

    public void setRequestProtocolTypeEnum(RequestProtocolTypeEnum requestProtocolTypeEnum) {
        this.requestProtocolTypeEnum = requestProtocolTypeEnum;
    }

    public CompressTypeEnum getCompressTypeEnum() {
        return compressTypeEnum;
    }

    public void setCompressTypeEnum(CompressTypeEnum compressTypeEnum) {
        this.compressTypeEnum = compressTypeEnum;
    }

    public SerializerTypeEnum getSerializerTypeEnum() {
        return serializerTypeEnum;
    }

    public void setSerializerTypeEnum(SerializerTypeEnum serializerTypeEnum) {
        this.serializerTypeEnum = serializerTypeEnum;
    }

    public RequestProtocol getRequestProtocol() {
        return requestProtocol;
    }

    public void setRequestProtocol(RequestProtocol requestProtocol) {
        this.requestProtocol = requestProtocol;
    }


    @Override
    public String toString() {
        return "RequestContext{" +
                "version=" + version +
                ", requestProtocolTypeEnum=" + requestProtocolTypeEnum +
                ", compressTypeEnum=" + compressTypeEnum +
                ", serializerTypeEnum=" + serializerTypeEnum +
                ", requestProtocol=" + requestProtocol +
                '}';
    }
}
