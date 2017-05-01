package com.ifengxue.rpc.client.proxy;

import com.ifengxue.rpc.client.factory.ClientConfigFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.misc.Signal;

/**
 *  客户端信号处理器
 * Created by LiuKeFeng on 2017-05-01.
 */
public class ClientSignalHandler implements sun.misc.SignalHandler {
    private Logger logger = LoggerFactory.getLogger(getClass());
    private static volatile boolean isClosed = false;
    @Override
    public void handle(Signal signal) {
        if (signal.getName().equals("TERM") && !isClosed) {
            isClosed = true;
            //关闭连接池
            logger.info("开始关闭Socket连接池...");
            ClientConfigFactory.getInstance().getChannelPool().close();
            //关闭注册中心
            logger.info("开始断开注册中心...");
            ClientConfigFactory.getInstance().getRegisterCenter().close();
        }
    }
}
