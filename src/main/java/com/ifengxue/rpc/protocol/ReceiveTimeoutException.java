package com.ifengxue.rpc.protocol;

/**
 * 接收服务响应超时
 *
 * Created by LiuKeFeng on 2017-04-25.
 */
public class ReceiveTimeoutException extends RuntimeException {
    private static final long serialVersionUID = -7416307280613882910L;

    public ReceiveTimeoutException() {
    }

    public ReceiveTimeoutException(String message) {
        super(message);
    }

    public ReceiveTimeoutException(String message, Throwable cause) {
        super(message, cause);
    }

    public ReceiveTimeoutException(Throwable cause) {
        super(cause);
    }

    public ReceiveTimeoutException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
