package com.ifengxue.rpc.server.interceptor;

import java.io.Closeable;

/**
 * 可控制的日志记录
 *
 * Created by LiuKeFeng on 2017-05-14.
 */
public interface IControllableLogging extends Closeable {
    /**
     * 关闭所有的日志记录器
     */
    @Override
    void close();

    /**
     * 关闭{@link com.ifengxue.rpc.server.interceptor.Interceptor.InterceptorTypeEnum#BEFORE} 日志记录器
     */
    void closeBeforeLogging();

    /**
     * 关闭{@link com.ifengxue.rpc.server.interceptor.Interceptor.InterceptorTypeEnum#AFTER} 日志记录器
     */
    void closeAfterLogging();

    /**
     * 关闭{@link com.ifengxue.rpc.server.interceptor.Interceptor.InterceptorTypeEnum#EXCEPTION} 日志记录器
     */
    void closeExceptionLogging();

    /**
     * 打开所有的日志记录器
     */
    void open();

    /**
     * 打开{@link com.ifengxue.rpc.server.interceptor.Interceptor.InterceptorTypeEnum#BEFORE} 日志记录器
     */
    void openBeforeLogging();

    /**
     * 打开{@link com.ifengxue.rpc.server.interceptor.Interceptor.InterceptorTypeEnum#AFTER} 日志记录器
     */
    void openAfterLogging();

    /**
     * 打开{@link com.ifengxue.rpc.server.interceptor.Interceptor.InterceptorTypeEnum#EXCEPTION} 日志记录器
     */
    void openExceptionLogging();
}
