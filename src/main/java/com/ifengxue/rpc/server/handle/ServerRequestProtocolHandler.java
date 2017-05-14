package com.ifengxue.rpc.server.handle;

import com.ifengxue.rpc.protocol.*;
import com.ifengxue.rpc.server.factory.ServerConfigFactory;
import com.ifengxue.rpc.server.task.*;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 服务端请求协议处理器
 *
 * Created by LiuKeFeng on 2017-04-21.
 */
public class ServerRequestProtocolHandler extends SimpleChannelInboundHandler<RequestContext> {
    private static final ExecutorService EXECUTOR_SERVICE;
    private static final IInvokeHandler INVOKE_HANDLER = ServerConfigFactory.getInstance().getInvokeHandler();
    private static final IWorker[] WORKERS;
    private static final AtomicInteger SUBMIT_WORK_SEQUENCE = new AtomicInteger(0);
    private Logger logger = LoggerFactory.getLogger(getClass());

    static {
        int cpuCount = Runtime.getRuntime().availableProcessors();
        AtomicInteger workStealingThreadSequence = new AtomicInteger(0);
        EXECUTOR_SERVICE = Executors.newFixedThreadPool(cpuCount,
                r -> new Thread(r, "WorkStealingThread-" + workStealingThreadSequence.getAndIncrement()));
        WORKERS = new SimpleWorker[cpuCount];
        for (int i = 0; i < WORKERS.length; i++) {
            WORKERS[i] = new SimpleWorker();
        }
        //设置可供工作窃取的列表
        for (int i = 0; i < WORKERS.length; i++) {
            List<IWorker> workerList = new ArrayList<>(Arrays.asList(WORKERS));
            //移除“自身”，避免自己从自己的队列窃取任务
            workerList.remove(i);
            List<IWorkStealing<RunnableTaskWrapper>> workStealingList = new ArrayList<>(WORKERS.length - 1);
            workerList.forEach(workStealingList::add);
            WORKERS[i].setWorkStealingList(workStealingList);
        }
        //启动线程
        for (int i = 0; i < WORKERS.length; i++) {
            EXECUTOR_SERVICE.execute(WORKERS[i]);
        }
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RequestContext context) {
        RunnableTask task = new RunnableTask(INVOKE_HANDLER, context, ctx.channel());
        /**
         * 采用RoundRobin提交任务
         */
        int sequence = SUBMIT_WORK_SEQUENCE.incrementAndGet();
        int index = sequence % WORKERS.length;
        WORKERS[index].submitTask(task);
        if (sequence > Integer.MAX_VALUE - 10_000_000) {
            SUBMIT_WORK_SEQUENCE.set(0);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.channel().close();
        if (cause instanceof InvocationTargetException) {
            cause = cause.getCause();
        }
        logger.error("服务端处理客户端响应出错:" + cause.getMessage(), cause);
    }
}
