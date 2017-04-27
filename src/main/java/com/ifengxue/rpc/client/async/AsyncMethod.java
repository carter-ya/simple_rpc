package com.ifengxue.rpc.client.async;

import java.io.Serializable;

/**
 * 异步方法调用实体
 *
 * Created by LiuKeFeng on 2017-04-27.
 */
public class AsyncMethod implements Serializable {
    private static final long serialVersionUID = 7178307560474490336L;
    private Class<?> clazz;
    private String methodName;
    private boolean async;
    private boolean sent;
    private boolean returnWait;
    public AsyncMethod() {}

    public AsyncMethod(Class<?> clazz, String methodName, boolean async, boolean sent, boolean returnWait) {
        this.clazz = clazz;
        this.methodName = methodName;
        this.async = async;
        this.sent = sent;
        this.returnWait = returnWait;
    }

    public Class<?> getClazz() {
        return clazz;
    }

    public void setClazz(Class<?> clazz) {
        this.clazz = clazz;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public boolean isAsync() {
        return async;
    }

    public void setAsync(boolean async) {
        this.async = async;
    }

    public boolean isSent() {
        return sent;
    }

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
