package com.ifengxue.rpc.protocol.json;

import com.alibaba.fastjson.JSONObject;
import io.netty.handler.codec.http.HttpHeaders;

import java.net.InetSocketAddress;

/**
 * Created by LiuKeFeng on 2017-04-29.
 */
public class JSONRequestProtocol {
    private final InetSocketAddress inetSocketAddress;
    private final JSONRequest jsonRequest;
    private final HttpHeaders httpHeaders;

    public JSONRequestProtocol(InetSocketAddress inetSocketAddress, JSONRequest jsonRequest, HttpHeaders httpHeaders) {
        this.inetSocketAddress = inetSocketAddress;
        this.jsonRequest = jsonRequest;
        this.httpHeaders = httpHeaders;
    }

    public String getRemoteAddr() {
        return inetSocketAddress.getAddress().getHostAddress();
    }

    public int getRemotePort() {
        return inetSocketAddress.getPort();
    }

    public String getID() {
        return jsonRequest.getId();
    }

    public String getService() {
        return jsonRequest.getService();
    }

    public String getMethod() {
        return jsonRequest.getMethod();
    }

    public JSONObject getParamJSONObject() {
        return jsonRequest.getParam();
    }

    public String getHost() {
        return httpHeaders.get("Host");
    }
}
