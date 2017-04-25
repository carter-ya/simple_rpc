package com.ifengxue.rpc.server.interceptor;

import com.ifengxue.rpc.protocol.ResponseContext;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * bean 验证
 * Created by LiuKeFeng on 2017-04-25.
 */
public class BeanValidateInterceptor implements Interceptor {
    public static final Set<Class<?>> PRIMIVE_AND_WRAPPER_AND_STRING_CALASS_SET = new HashSet<>();
    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    static  {
        PRIMIVE_AND_WRAPPER_AND_STRING_CALASS_SET.add(byte.class);
        PRIMIVE_AND_WRAPPER_AND_STRING_CALASS_SET.add(short.class);
        PRIMIVE_AND_WRAPPER_AND_STRING_CALASS_SET.add(int.class);
        PRIMIVE_AND_WRAPPER_AND_STRING_CALASS_SET.add(long.class);
        PRIMIVE_AND_WRAPPER_AND_STRING_CALASS_SET.add(float.class);
        PRIMIVE_AND_WRAPPER_AND_STRING_CALASS_SET.add(double.class);
        PRIMIVE_AND_WRAPPER_AND_STRING_CALASS_SET.add(char.class);
        PRIMIVE_AND_WRAPPER_AND_STRING_CALASS_SET.add(boolean.class);
        PRIMIVE_AND_WRAPPER_AND_STRING_CALASS_SET.add(Byte.class);
        PRIMIVE_AND_WRAPPER_AND_STRING_CALASS_SET.add(Short.class);
        PRIMIVE_AND_WRAPPER_AND_STRING_CALASS_SET.add(Integer.class);
        PRIMIVE_AND_WRAPPER_AND_STRING_CALASS_SET.add(Long.class);
        PRIMIVE_AND_WRAPPER_AND_STRING_CALASS_SET.add(Float.class);
        PRIMIVE_AND_WRAPPER_AND_STRING_CALASS_SET.add(Double.class);
        PRIMIVE_AND_WRAPPER_AND_STRING_CALASS_SET.add(Character.class);
        PRIMIVE_AND_WRAPPER_AND_STRING_CALASS_SET.add(Boolean.class);
        PRIMIVE_AND_WRAPPER_AND_STRING_CALASS_SET.add(String.class);
    }

    @Override
    public int getPriority() {
        return Integer.MAX_VALUE - 1;
    }

    @Override
    public ResponseContext intercept(ResponseContext context, InterceptorTypeEnum interceptorTypeEnum) throws Exception {
        Object[] params = context.getRequestParameters();
        if (params == null) {
            return context;
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
        return context;
    }

    @Override
    public InterceptorTypeEnum[] getInterceptorTypeEnums() {
        return new InterceptorTypeEnum[] {InterceptorTypeEnum.BEFORE};
    }
}
