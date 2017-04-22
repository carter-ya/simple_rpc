package com.ifengxue.rpc.protocol;

import com.ifengxue.rpc.protocol.enums.RequestProtocolTypeEnum;

import java.util.Arrays;
import java.util.UUID;

/**
 * 客户端请求协议
 *
 * Created by LiuKeFeng on 2017-04-20.
 */
public class RequestProtocol {
    /** PING协议 */
    public static final RequestProtocol PING_REQUEST_PROTOCOL = Builder.newBuilder().setRequestProtocolTypeEnum(RequestProtocolTypeEnum.PING).build();
    /** 请求时的sessionID */
    private String sessionID;
    /** 请求接口的完整名称 */
    private String className;
    /** 请求接口的方法名称 */
    private String methodName;
    /** 请求接口的方法的参数类型 */
    private Class<?>[] parameterTypes;
    /** 请求接口中的方法的参数 */
    private Object[] parameters;
    private RequestProtocolTypeEnum requestProtocolTypeEnum;
    private RequestProtocol() {

    }

    public String getSessionID() {
        return sessionID;
    }

    public String getClassName() {
        return className;
    }

    public String getMethodName() {
        return methodName;
    }

    public Class<?>[] getParameterTypes() {
        return parameterTypes;
    }

    public Object[] getParameters() {
        return parameters;
    }

    public RequestProtocolTypeEnum getRequestProtocolTypeEnum() {
        return requestProtocolTypeEnum;
    }

    @Override
    public String toString() {
        return "RequestProtocol{" +
                "sessionID='" + sessionID + '\'' +
                ", className='" + className + '\'' +
                ", methodName='" + methodName + '\'' +
                ", parameterTypes=" + Arrays.toString(parameterTypes) +
                ", parameters=" + Arrays.toString(parameters) +
                ", requestProtocolTypeEnum=" + requestProtocolTypeEnum +
                '}';
    }

    public static class Builder {
        private RequestProtocol requestProtocol;
        private Builder() {
            requestProtocol = new RequestProtocol();
        }
        public static Builder newBuilder() {
            return new Builder();
        }
        public Builder setSessionID(String sessionID) {
            requestProtocol.sessionID = sessionID;
            return this;
        }

        public Builder setClassName(String className) {
            requestProtocol.className = className;
            return this;
        }

        public Builder setMethodName(String methodName) {
            requestProtocol.methodName = methodName;
            return this;
        }

        public Builder setParameterTypes(Class<?>[] parameterTypes) {
            requestProtocol.parameterTypes = parameterTypes;
            return this;
        }

        public Builder setParameters(Object[] parameters) {
            requestProtocol.parameters = parameters;
            return this;
        }

        public Builder setRequestProtocolTypeEnum(RequestProtocolTypeEnum requestProtocolTypeEnum) {
            requestProtocol.requestProtocolTypeEnum = requestProtocolTypeEnum;
            return this;
        }
        public RequestProtocol build() {
            if (requestProtocol.sessionID == null) {
                requestProtocol.sessionID = UUID.randomUUID().toString();
            }
            return requestProtocol;
        }
    }
}
