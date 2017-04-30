package com.ifengxue.rpc.protocol.json;

import com.alibaba.fastjson.JSONObject;

/**
 * Created by LiuKeFeng on 2017-04-29.
 */
public class JSONRequest {
    private final String service;
    private final String id;
    private final String method;
    private final JSONObject param;

    public JSONRequest(String service, String id, String method, JSONObject param) {
        this.service = service;
        this.id = id;
        this.method = method;
        this.param = param;
    }

    public String getService() {
        return service;
    }

    public String getId() {
        return id;
    }

    public String getMethod() {
        return method;
    }

    public JSONObject getParam() {
        return param;
    }

    @Override
    public String toString() {
        return "JSONRequest{" +
                "service='" + service + '\'' +
                ", id='" + id + '\'' +
                ", method='" + method + '\'' +
                ", param=" + param +
                '}';
    }
}
