package com.ifengxue.rpc.server.task;

/**
 * 工作窃取
 *
 * Created by LiuKeFeng on 2017-05-14.
 */
public interface IWorkStealing<T> {
    /**
     * 允许其他工作线程执行工作窃取
     * @return 被窃取的工作，如果没有可窃取的工作则返回<code>null</code>
     */
    T workStealing();
}
