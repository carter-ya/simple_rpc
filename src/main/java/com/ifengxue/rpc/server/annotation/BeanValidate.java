package com.ifengxue.rpc.server.annotation;

import java.lang.annotation.*;

/**
 * 是否启用Bean自动验证
 * Created by LiuKeFeng on 2017-04-27.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
@Documented
public @interface BeanValidate {
    /**
     * true启用Bean验证;false 关闭Bean验证
     * @return
     */
    boolean value() default true;
}
