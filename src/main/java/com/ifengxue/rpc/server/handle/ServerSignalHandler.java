package com.ifengxue.rpc.server.handle;

import com.ifengxue.rpc.server.IRpcServer;
import sun.misc.Signal;

/**
 * 服务端退出信号响应
 *
 * Created by LiuKeFeng on 2017-04-26.
 */
public class ServerSignalHandler implements sun.misc.SignalHandler {
    private final IRpcServer rpcServer;
    private static volatile boolean isClosed = false;

    public ServerSignalHandler(IRpcServer rpcServer) {
        this.rpcServer = rpcServer;
    }

    @Override
    public void handle(Signal signal) {
        if (signal.getName().equals("TERM") && !isClosed) {
            isClosed = true;
            rpcServer.close();
        }
    }
}
