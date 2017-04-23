package com.ifengxue.rpc.protocol.annotation;

import java.lang.annotation.*;

/**
 * 声明对外提供服务的注解组
 *
 * Created by LiuKeFeng on 2017-04-23.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RpcServices {
    RpcService[] value();
}
