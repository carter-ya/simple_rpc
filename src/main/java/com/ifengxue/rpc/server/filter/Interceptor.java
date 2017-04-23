package com.ifengxue.rpc.server.filter;

import com.ifengxue.rpc.protocol.ResponseContext;

/**
 * 拦截器接口
 *
 * Created by LiuKeFeng on 2017-04-21.
 */
public interface Interceptor extends Comparable<Interceptor> {
    enum InterceptorTypeEnum {BEFORE, AFTER, EXCEPTION}
    /**
     * 拦截器优先级:数字越大优先级越高 {@link Integer#MAX_VALUE}, {@link Integer#MIN_VALUE}
     *  默认的优先级是0
     * @return
     */
    default int getPriority() {
        return 0;
    }

    /**
     * 执行拦截器，当拦截器返回值不为<code>null</code>则结束后续执行，直接返回结果
     * @param context 响应上下文
     * @param interceptorTypeEnum 当前正在执行的拦截器类型
     * @return
     * @throws Exception
     */
    ResponseContext intercept(ResponseContext context, InterceptorTypeEnum interceptorTypeEnum) throws Exception;

    /**
     * 返回关注的拦截器类型
     * @return
     */
    InterceptorTypeEnum[] getInterceptorTypeEnums();

    /**
     * 倒序排序
     * @param interceptor
     * @return
     */
    @Override
    default int compareTo(Interceptor interceptor) {
        return interceptor.getPriority() - this.getPriority();
    }
}
