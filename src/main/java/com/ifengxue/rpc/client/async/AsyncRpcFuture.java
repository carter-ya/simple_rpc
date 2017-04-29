package com.ifengxue.rpc.client.async;

import com.ifengxue.rpc.protocol.ResponseProtocol;

import java.rmi.RemoteException;
import java.util.concurrent.*;

/**
 * Created by LiuKeFeng on 2017-04-28.
 */
public class AsyncRpcFuture<V> implements Future, IAsyncCallback {
    private CountDownLatch countDownLatch = new CountDownLatch(1);
    private ResponseProtocol responseProtocol;
    private AsyncMethod asyncMethod;
    private String sessionID;
    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        return false;
    }

    @Override
    public boolean isCancelled() {
        return false;
    }

    @Override
    public boolean isDone() {
        return responseProtocol != null;
    }

    @Override
    public V get() throws InterruptedException, ExecutionException {
        countDownLatch.await();
        throwExecutionExceptionIfNeeded();
        return (V) responseProtocol.getInvokeResult();
    }

    @Override
    public V get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        countDownLatch.await(timeout, unit);
        throwExecutionExceptionIfNeeded();
        return (V) responseProtocol.getInvokeResult();
    }

    @Override
    public void callback(ResponseProtocol responseProtocol) {
        this.responseProtocol = responseProtocol;
        countDownLatch.countDown();
    }

    @Override
    public void setAsyncMethod(AsyncMethod asyncMethod) {
        this.asyncMethod = asyncMethod;
    }

    @Override
    public AsyncMethod getAsyncMethod() {
        return asyncMethod;
    }

    @Override
    public void setSessionID(String sessionID) {
        this.sessionID = sessionID;
    }

    @Override
    public String getSessionID() {
        return sessionID;
    }

    private void throwExecutionExceptionIfNeeded() throws ExecutionException {
        if (responseProtocol.getExceptionProtocol() != null) {
            RemoteException remoteException = responseProtocol.getExceptionProtocol().asRemoteException();
            throw new ExecutionException(remoteException);
        }
    }
}
