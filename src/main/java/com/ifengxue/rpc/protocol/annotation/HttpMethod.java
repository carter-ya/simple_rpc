package com.ifengxue.rpc.protocol.annotation;

import java.lang.annotation.*;

/**
 * 标记在json-rpc中的方法名称
 *
 * Created by LiuKeFeng on 2017-04-29.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface HttpMethod {
    /**
     * 方法名称
     * @return
     */
    String value();

    /**
     * 方法描述
     * @return
     */
    String description() default "";
}
