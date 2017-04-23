package com.ifengxue.rpc.server;

import org.junit.Test;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.concurrent.TimeUnit;

/**
 * Created by LiuKeFeng on 2017-04-23.
 */
public class ServerAppTest {
    @Test
    public void testMain() throws UnsupportedEncodingException, InterruptedException {
        ServerApp.main(new String[]{"--conf:" + URLDecoder.decode(ServerAppTest.class.getClassLoader().getResource("conf/rpc_server.xml").getFile(), "UTF-8")});
        TimeUnit.MINUTES.sleep(10);
    }
}
