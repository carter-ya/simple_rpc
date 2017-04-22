package com.ifengxue.rpc.protocol;

/**
 * 请求协议异常
 *
 * Created by LiuKeFeng on 2017-04-20.
 */
public class RequestProtocolException extends ProtocolException {
    public RequestProtocolException() {
    }

    public RequestProtocolException(String message) {
        super(message);
    }

    public RequestProtocolException(String message, Throwable cause) {
        super(message, cause);
    }

    public RequestProtocolException(Throwable cause) {
        super(cause);
    }

    public RequestProtocolException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
