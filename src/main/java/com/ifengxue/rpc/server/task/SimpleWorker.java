package com.ifengxue.rpc.server.task;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;

/**
 * 简单实现的工作执行者
 *
 * Created by LiuKeFeng on 2017-05-14.
 */
public class SimpleWorker implements IWorker {
    private final BlockingDeque<RunnableTaskWrapper> workDeque;
    private List<IWorkStealing<RunnableTaskWrapper>> workStealingList;
    private Logger logger = LoggerFactory.getLogger(getClass());

    public SimpleWorker() {
        this(new LinkedBlockingDeque<>());
    }

    public SimpleWorker(BlockingDeque<RunnableTaskWrapper> workDeque) {
        this.workDeque = workDeque;
    }

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            RunnableTaskWrapper wrapper = pollOrWorkStealing();
            if (wrapper != null) {
                long beginExecuteNanoTime = System.nanoTime();
                wrapper.getTask().execute();
                long endExecuteNanoTime = System.nanoTime();

                if (logger.isInfoEnabled()) {
                    writeExecuteInfo(wrapper, beginExecuteNanoTime, endExecuteNanoTime);
                }
                continue;
            }
            //当前工作队列和其他工作队列均没有任务，休眠
            try {
                TimeUnit.MICROSECONDS.sleep(1L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public RunnableTaskWrapper workStealing() {
        try {
            return Optional.ofNullable(workDeque.pollLast(0L, TimeUnit.MICROSECONDS))
                    .map(wrapper -> {
                        wrapper.setDequeueNanoTime(System.nanoTime());
                        return wrapper;
                    })
                    .orElse(null);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void submitTask(RunnableTask task) {
        workDeque.offerLast(new RunnableTaskWrapper(task));
    }

    @Override
    public RunnableTaskWrapper pollOrWorkStealing() {
        RunnableTaskWrapper wrapper = workDeque.pollFirst();
        if (wrapper != null) {
            wrapper.setDequeueNanoTime(System.nanoTime());
            return wrapper;
        }
        for (IWorkStealing<RunnableTaskWrapper> workStealing : workStealingList) {
            wrapper = workStealing.workStealing();
            if (wrapper != null) {
                //不需要设置出队时间，因为workStealing()方法已经设置了
                return wrapper;
            }
        }
        return null;
    }

    @Override
    public void setWorkStealingList(List<IWorkStealing<RunnableTaskWrapper>> workStealingList) {
        this.workStealingList = workStealingList;
    }

    private void writeExecuteInfo(RunnableTaskWrapper wrapper, long beginExecuteNanoTime, long endExecuteNanoTime) {
        long enqueueNanoTime = wrapper.getEnqueueNanoTime();
        long dequeueNanoTime = wrapper.getDequeueNanoTime();
        String sessionID = wrapper.getTask().getRequestContext().getRequestProtocol().getSessionID();
        StringBuilder builder = new StringBuilder();
        builder.append("客户端sessionID=").append(sessionID)
                .append(",队列耗时(ns)=").append(dequeueNanoTime - enqueueNanoTime)
                .append(",执行耗时(ns)=").append(endExecuteNanoTime - beginExecuteNanoTime);
        logger.info(builder.toString());
    }
}
