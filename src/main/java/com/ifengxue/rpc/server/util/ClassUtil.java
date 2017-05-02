package com.ifengxue.rpc.server.util;

import java.lang.annotation.Annotation;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 类工具
 * Created by LiuKeFeng on 2017-05-01.
 */
public class ClassUtil {
    /**
     * 从类集合中过滤出所有的接口
     * @param classes
     * @return
     */
    public static List<Class<?>> findAllInterfaces(List<Class<?>> classes) {
        return classes.stream().filter(Class::isInterface).collect(Collectors.toList());
    }

    /**
     * 从类集合中过滤出不是接口的类
     * @param classes
     * @return
     */
    public static List<Class<?>> findAllNotInterfaceClasses(List<Class<?>> classes) {
        return classes.stream().filter(clazz -> !clazz.isInterface()).collect(Collectors.toList());
    }

    /**
     * 从类集合中过滤出定义了指定注解的类
     * @param classes
     * @param annotationType 指定的注解
     * @return
     */
    public static List<Class> findAllClassWithAnnotatedBy(List<Class<?>> classes, Class<? extends Annotation> annotationType) {
        return classes.stream().filter(clazz -> clazz.getAnnotation(annotationType) != null).collect(Collectors.toList());
    }
}
