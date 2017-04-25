package com.ifengxue.rpc.protocol;

import org.junit.Test;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * Created by LiuKeFeng on 2017-04-25.
 */
public class ExceptionTest {
    @Test
    public void testExceptionSerialize() {
        Throwable throwable = new Throwable("我是错误信息");
        StringWriter writer = new StringWriter();
        throwable.printStackTrace(new PrintWriter(writer));
        for (StackTraceElement stackTraceElement : throwable.getStackTrace()) {
//            System.out.println(stackTraceElement.getClassName() + "." + stackTraceElement.getMethodName() + "(" + stackTraceElement.getFileName() + ":" + stackTraceElement.getLineNumber() + ")");
        }
        throwable = new Throwable();
        throwable.setStackTrace(throwable.getStackTrace());
        throwable.printStackTrace();
    }
}
