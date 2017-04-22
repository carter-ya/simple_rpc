package com.ifengxue.rpc.util;

import com.ifengxue.rpc.server.filter.Interceptor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 拦截器工具类
 *
 * Created by LiuKeFeng on 2017-04-21.
 */
public final class InterceptorUtil {
    /**
     * 获取按{@link Interceptor#compareTo(Interceptor)}排序好的前置拦截器
     * @param interceptors
     * @return
     */
    public static List<Interceptor> getBeforeInterceptors(List<Interceptor> interceptors) {
       return getSpecifyTypeFilters(interceptors, Interceptor.InterceptorTypeEnum.BEFORE);
    }

    /**
     * 获取按{@link Interceptor#compareTo(Interceptor)}排序好的后置拦截器
     * @param interceptors
     * @return
     */
    public static List<Interceptor> getAfterInterceptors(List<Interceptor> interceptors) {
        return getSpecifyTypeFilters(interceptors, Interceptor.InterceptorTypeEnum.AFTER);
    }

    /**
     * 获取按{@link Interceptor#compareTo(Interceptor)}排序好的异常拦截器
     * @param interceptors
     * @return
     */
    public static List<Interceptor> getExceptionInterceptors(List<Interceptor> interceptors) {
        return getSpecifyTypeFilters(interceptors, Interceptor.InterceptorTypeEnum.EXCEPTION);
    }

    /**
     * 获取指定类型的拦截器，并按默认的顺序进行排序
     * @param interceptors
     * @param interceptorTypeEnum 拦截器类型
     * @return
     */
    private static List<Interceptor> getSpecifyTypeFilters(List<Interceptor> interceptors, Interceptor.InterceptorTypeEnum interceptorTypeEnum) {
        List<Interceptor> subInterceptors = new ArrayList<>(interceptors.size());
        interceptors.forEach(filter -> {
            for (Interceptor.InterceptorTypeEnum tempType : filter.getInterceptorTypeEnums()) {
                if (tempType == interceptorTypeEnum) {
                    subInterceptors.add(filter);
                    break;
                }
            }
        });
        Collections.sort(subInterceptors);
        return subInterceptors;
    }
}
