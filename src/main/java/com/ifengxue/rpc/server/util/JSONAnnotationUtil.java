package com.ifengxue.rpc.server.util;

import com.ifengxue.rpc.protocol.annotation.HttpMethod;
import com.ifengxue.rpc.protocol.annotation.Param;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Optional;

/**
 * json-rpc 注解工具类
 * Created by LiuKeFeng on 2017-05-02.
 */
public class JSONAnnotationUtil {
    /**
     * 获取指定方法在json-rpc中暴露的方法名
     * @param method
     * @param def
     * @return
     */
    public static String getJSONMethodName(Method method, String def) {
        return Optional
                .ofNullable(method.getAnnotation(HttpMethod.class))
                .map(HttpMethod::value)
                .orElse(def);
    }

    /**
     * 获取指定方法在json-rpc中的方法描述
     * @param method
     * @param def
     * @return
     */
    public static String getJSONMethodDescription(Method method, String def) {
        return Optional
                .ofNullable(method.getAnnotation(HttpMethod.class))
                .map(HttpMethod::description)
                .orElse(def);
    }

    /**
     * 获取指定参数在json-rpc中的参数名称
     * @param parameter
     * @param def
     * @return
     */
    public static String getJSONMethodParamName(Parameter parameter, String def) {
        return Optional
                .ofNullable(parameter.getAnnotation(Param.class))
                .map(Param::value)
                .orElse(def);
    }
}
