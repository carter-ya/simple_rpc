package com.ifengxue.rpc.server.interceptor;

import com.alibaba.fastjson.JSON;
import com.ifengxue.rpc.protocol.ResponseContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 日志拦截器：记录请求,响应和异常内容的日志
 *
 * Created by LiuKeFeng on 2017-05-14.
 */
public class LoggingInterceptor implements Interceptor, IControllableLogging {
    private Logger logger = LoggerFactory.getLogger(getClass());
    private AtomicBoolean beforeLogging = new AtomicBoolean(true);
    private AtomicBoolean afterLogging = new AtomicBoolean(true);
    private AtomicBoolean exceptionLogging = new AtomicBoolean(true);
    @Override
    public int getPriority() {
        return Integer.MAX_VALUE - 2;
    }

    @Override
    public void intercept(ResponseContext context, InterceptorTypeEnum interceptorTypeEnum) throws Exception {
        if (!logger.isInfoEnabled() || (!beforeLogging.get() && !afterLogging.get() && !exceptionLogging.get())) {
            return;
        }
        Map<String, Object> map = new TreeMap<>();
        map.put("sessionID", context.getRequestSessionID());
        map.put("serializeType", context.getRequestSerializerTypeEnum());
        map.put("requestClass", context.getRequestClassName());
        map.put("requestMethod", context.getRequestMethodName());
        map.put("requestParams", JSON.toJSON(context.getRequestParameters()));
        if (beforeLogging.get() && interceptorTypeEnum == InterceptorTypeEnum.BEFORE) {
            logger.info("before logging:{}", JSON.toJSONString(map));
            return;
        }

        if (afterLogging.get() && interceptorTypeEnum == InterceptorTypeEnum.AFTER) {
            map.put("response", JSON.toJSON(context.getInvokeResult()));
            logger.info("after logging:{}", JSON.toJSONString(map));
            return;
        }

        if (exceptionLogging.get() && interceptorTypeEnum == InterceptorTypeEnum.EXCEPTION) {
            map.put("exceptionMessage", context.getResponseError().getMessage());
            logger.info("exception logging:{}", JSON.toJSONString(map));
            return;
        }
    }

    @Override
    public InterceptorTypeEnum[] getInterceptorTypeEnums() {
        return InterceptorTypeEnum.values();
    }

    @Override
    public void close() {
        closeBeforeLogging();
        closeAfterLogging();
        closeExceptionLogging();
    }

    @Override
    public void closeBeforeLogging() {
        beforeLogging.set(false);
    }

    @Override
    public void closeAfterLogging() {
        afterLogging.set(false);
    }

    @Override
    public void closeExceptionLogging() {
        exceptionLogging.set(false);
    }

    @Override
    public void open() {
        openBeforeLogging();
        openAfterLogging();
        openExceptionLogging();
    }

    @Override
    public void openBeforeLogging() {
        beforeLogging.set(true);
    }

    @Override
    public void openAfterLogging() {
        afterLogging.set(true);
    }

    @Override
    public void openExceptionLogging() {
        exceptionLogging.set(true);
    }
}
