package com.ifengxue.rpc.protocol.json;

/**
 * Created by LiuKeFeng on 2017-04-29.
 */
public class JSONRpcError {
    public static final int SERVICE_NOT_FOUND = -32100;
    public static final String SERVICE_NOT_FOUND_MESSAGE = "Service Not Found";
    public static final int METHOD_NOT_FOUND = -32601;
    public static final String METHOD_NOT_FOUND_MESSAGE = "Method Not Found";
    public static final int INVALID_PARAMS = -32602;
    public static final String INVALID_PARAMS_MESSAGE = "Invalid params";
    public static final int INTERNAL_ERROR = -32603;
    public static final String INTERNAL_ERROR_MESSAGE = "Internal error";
    private int code;
    private String message;
    private Object data;
    public JSONRpcError() {}

    public JSONRpcError(int code, String message, Object data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "JSONRpcError{" +
                "code=" + code +
                ", message='" + message + '\'' +
                ", data=" + data +
                '}';
    }
}
