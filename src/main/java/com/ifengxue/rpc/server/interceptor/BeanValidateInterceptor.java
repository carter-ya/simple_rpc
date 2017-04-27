package com.ifengxue.rpc.server.interceptor;

import com.ifengxue.rpc.protocol.ResponseContext;
import com.ifengxue.rpc.server.annotation.BeanValidate;
import com.ifengxue.rpc.server.factory.ServerConfigFactory;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * bean 验证
 * Created by LiuKeFeng on 2017-04-25.
 */
public class BeanValidateInterceptor implements Interceptor {
    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
    private final Map<String, Class<?>> proxyClassMap = ServerConfigFactory.getInstance().getServiceProvider().findAllProxyClass();

    @Override
    public int getPriority() {
        return Integer.MAX_VALUE - 1;
    }

    @Override
    public void intercept(ResponseContext context, InterceptorTypeEnum interceptorTypeEnum) throws Exception {
        Object[] params = context.getRequestParameters();
        if (params == null) {
            return;
        }

        //检查是否启用Bean验证
        Class<?> proxyClass = proxyClassMap.get(context.getRequestClassName());
        Method method = context.getRequestMethod();
        BeanValidate methodBeanValidate = method.getAnnotation(BeanValidate.class);
        BeanValidate classBeanValidate = proxyClass.getAnnotation(BeanValidate.class);
        if (!isEnableBeanValidate(methodBeanValidate, classBeanValidate)) {
            return;
        }

        for (Object param : params) {
            Set<ConstraintViolation<Object>> violationSet = validator.validate(param);
            if (!violationSet.isEmpty()) {
                Map<String, String> illegalArgumentMap = new HashMap<>(violationSet.size());
                for (ConstraintViolation<Object> objectConstraintViolation : violationSet) {
                    illegalArgumentMap.put(objectConstraintViolation.getPropertyPath().toString(), "[" + objectConstraintViolation.getInvalidValue() + "]" + objectConstraintViolation.getMessage());
                }
                context.setResponseError(new IllegalArgumentException(illegalArgumentMap.toString()));
                break;
            }
        }
        return;
    }

    @Override
    public InterceptorTypeEnum[] getInterceptorTypeEnums() {
        return new InterceptorTypeEnum[] {InterceptorTypeEnum.BEFORE};
    }

    private boolean isEnableBeanValidate(BeanValidate methodBeanValidate, BeanValidate classBeanValidate) {
        if (methodBeanValidate != null && methodBeanValidate.value()) {
            return true;
        }
        if (classBeanValidate != null && classBeanValidate.value()) {
            return true;
        }
        return false;
    }
}
