package com.ifengxue.rpc.protocol.annotation;

import java.lang.annotation.*;

/**
 * 对外提供服务的接口注解
 * Created by LiuKeFeng on 2017-04-23.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Repeatable(RpcServices.class)
@Documented
public @interface RpcService {
    /**
     * 声明对外提供服务的接口名称
     * @return
     */
    Class<?> value();
}
