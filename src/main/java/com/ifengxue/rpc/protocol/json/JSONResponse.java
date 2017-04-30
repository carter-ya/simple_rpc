package com.ifengxue.rpc.protocol.json;

/**
 * json响应
 * Created by LiuKeFeng on 2017-04-29.
 */
public class JSONResponse {
    private final String id;
    private final Object invokeResult;
    private final Throwable throwable;

    public JSONResponse(String id, Object invokeResult, Throwable throwable) {
        this.id = id;
        this.invokeResult = invokeResult;
        this.throwable = throwable;
    }

    public String getId() {
        return id;
    }

    public Object getInvokeResult() {
        return invokeResult;
    }

    public Throwable getThrowable() {
        return throwable;
    }

    @Override
    public String toString() {
        return "JSONResponse{" +
                "id='" + id + '\'' +
                ", invokeResult=" + invokeResult +
                ", throwable=" + throwable +
                '}';
    }
}
