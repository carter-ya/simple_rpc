package com.ifengxue.rpc.client.async;

import java.io.Serializable;

/**
 * 异步方法调用实体
 *
 * Created by LiuKeFeng on 2017-04-27.
 */
public class AsyncMethod implements Serializable {
    private static final long serialVersionUID = 7178307560474490336L;
    @Deprecated
    private Class<?> clazz;
    @Deprecated
    private String methodName;
    private boolean async;
    @Deprecated
    private boolean sent;
    private boolean returnWait;
    public AsyncMethod() {}

    public AsyncMethod(boolean async, boolean returnWait) {
        this.async = async;
        this.returnWait = returnWait;
    }

    @Deprecated
    public AsyncMethod(Class<?> clazz, String methodName, boolean async, boolean sent, boolean returnWait) {
        this.clazz = clazz;
        this.methodName = methodName;
        this.async = async;
        this.sent = sent;
        this.returnWait = returnWait;
    }

    @Deprecated
    public Class<?> getClazz() {
        return clazz;
    }

    @Deprecated
    public void setClazz(Class<?> clazz) {
        this.clazz = clazz;
    }

    @Deprecated
    public String getMethodName() {
        return methodName;
    }

    @Deprecated
    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public boolean isAsync() {
        return async;
    }

    public void setAsync(boolean async) {
        this.async = async;
    }

    @Deprecated
    public boolean isSent() {
        return sent;
    }

    @Deprecated
    public void setSent(boolean sent) {
        this.sent = sent;
    }

    public boolean isReturnWait() {
        return returnWait;
    }

    public void setReturnWait(boolean returnWait) {
        this.returnWait = returnWait;
    }

    @Override
    public String toString() {
        return "AsyncMethod{" +
                "clazz=" + clazz +
                ", methodName='" + methodName + '\'' +
                ", async=" + async +
                ", sent=" + sent +
                ", returnWait=" + returnWait +
                '}';
    }
}
