package com.ifengxue.rpc.server.task;

import java.util.List;

/**
 * 任务执行者
 *
 * Created by LiuKeFeng on 2017-05-14.
 */
public interface IWorker extends IWorkStealing<RunnableTaskWrapper>, Runnable {
    /**
     * 提交任务 <br>
     * 实现必须保证任务一定提交成功
     * @param task
     */
    void submitTask(RunnableTask task);

    /**
     * 从自己的工作队列拿任务或者从其他工作队列窃取任务
     * @return 如果没有则返回<code>null</code>
     */
    RunnableTaskWrapper pollOrWorkStealing();

    /**
     * 设置可被工作窃取的列表
     * @param workStealingList
     */
    void setWorkStealingList(List<IWorkStealing<RunnableTaskWrapper>> workStealingList);
}
