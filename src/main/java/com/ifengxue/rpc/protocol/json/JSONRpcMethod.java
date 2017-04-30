package com.ifengxue.rpc.protocol.json;

import java.util.List;

/**
 * Created by LiuKeFeng on 2017-04-30.
 */
public class JSONRpcMethod {
    private String method;
    private String description;
    private List<Param> params;

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<Param> getParams() {
        return params;
    }

    public void setParams(List<Param> params) {
        this.params = params;
    }

    @Override
    public String toString() {
        return "JSONRpcMethod{" +
                "method='" + method + '\'' +
                ", description='" + description + '\'' +
                ", params=" + params +
                '}';
    }

    public static class Param {
        private String paramName;
        private String paramType;

        public String getParamName() {
            return paramName;
        }

        public void setParamName(String paramName) {
            this.paramName = paramName;
        }

        public String getParamType() {
            return paramType;
        }

        public void setParamType(String paramType) {
            this.paramType = paramType;
        }

        @Override
        public String toString() {
            return "Param{" +
                    "paramName='" + paramName + '\'' +
                    ", paramType='" + paramType + '\'' +
                    '}';
        }
    }
}
