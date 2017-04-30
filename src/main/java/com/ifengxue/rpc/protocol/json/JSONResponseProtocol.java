package com.ifengxue.rpc.protocol.json;

import com.alibaba.fastjson.annotation.JSONField;
import com.alibaba.fastjson.serializer.SerializerFeature;

import java.io.Serializable;

/**
 * Created by LiuKeFeng on 2017-04-29.
 */
public class JSONResponseProtocol implements Serializable {
    @JSONField(name = "jsonrpc")
    private String jsonRpc = "2.0";
    private Object result;
    private Object error;
    @JSONField(serialzeFeatures = SerializerFeature.WriteNullStringAsEmpty)
    private String id;
    public JSONResponseProtocol() {}

    public JSONResponseProtocol(Object result, Object error, String id) {
        this.result = result;
        this.error = error;
        this.id = id;
    }

    public String getJsonRpc() {
        return jsonRpc;
    }

    public void setJsonRpc(String jsonRpc) {
        this.jsonRpc = jsonRpc;
    }

    public Object getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public Object getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "JSONResponseProtocol{" +
                "jsonRpc='" + jsonRpc + '\'' +
                ", result='" + result + '\'' +
                ", error='" + error + '\'' +
                ", id='" + id + '\'' +
                '}';
    }
}
