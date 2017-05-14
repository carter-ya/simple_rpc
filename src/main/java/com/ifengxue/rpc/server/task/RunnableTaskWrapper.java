package com.ifengxue.rpc.server.task;

/**
 * {@link RunnableTask} 封装
 *
 * Created by LiuKeFeng on 2017-05-14.
 */
public class RunnableTaskWrapper {
    private final RunnableTask task;
    /** 入队时间 */
    private final long enqueueNanoTime;
    /** 出队时间 */
    private long dequeueNanoTime;

    public RunnableTaskWrapper(RunnableTask task) {
        this.task = task;
        this.enqueueNanoTime = System.nanoTime();
    }

    public RunnableTask getTask() {
        return task;
    }

    public long getEnqueueNanoTime() {
        return enqueueNanoTime;
    }

    public long getDequeueNanoTime() {
        return dequeueNanoTime;
    }

    public void setDequeueNanoTime(long dequeueNanoTime) {
        this.dequeueNanoTime = dequeueNanoTime;
    }

    @Override
    public String toString() {
        return task.toString() + " enqueue at " + enqueueNanoTime + " ns.";
    }
}
