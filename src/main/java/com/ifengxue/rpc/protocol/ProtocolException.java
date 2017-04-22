package com.ifengxue.rpc.protocol;

/**
 * 协议异常
 *
 * Created by LiuKeFeng on 2017-04-20.
 */
public class ProtocolException extends RuntimeException {
    public ProtocolException() {
    }

    public ProtocolException(String message) {
        super(message);
    }

    public ProtocolException(String message, Throwable cause) {
        super(message, cause);
    }

    public ProtocolException(Throwable cause) {
        super(cause);
    }

    public ProtocolException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
