package com.ifengxue.rpc.protocol.annotation;

import java.lang.annotation.*;

/**
 * json-rpc参数名称注解
 *
 * Created by LiuKeFeng on 2017-04-29.
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Param {
    /**
     * 参数名称
     * @return
     */
    String value();
}
