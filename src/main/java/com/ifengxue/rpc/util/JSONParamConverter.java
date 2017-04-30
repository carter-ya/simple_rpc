package com.ifengxue.rpc.util;

import com.alibaba.fastjson.JSON;

import java.sql.Timestamp;
import java.util.Date;
/**
 * json参数转换器
 * Created by LiuKeFeng on 2017-04-30.
 */
public class JSONParamConverter {
    /**
     * json字符串转换
     * @param param
     * @param paramType
     * @return
     */
    public static Object convert(String param, Class<?> paramType) {
        if (paramType.equals(byte.class) || paramType.equals(Byte.class)) {
            return Byte.valueOf(param);
        }
        if(paramType.equals(short.class) || paramType.equals(Short.class)) {
            return Short.valueOf(param);
        }
        if (paramType.equals(int.class) || paramType.equals(Integer.class)) {
            return Integer.valueOf(param);
        }
        if (paramType.equals(long.class) || paramType.equals(Long.class)) {
            return Long.valueOf(param);
        }
        if (paramType.equals(float.class) || paramType.equals(Float.class)) {
            return Float.valueOf(param);
        }
        if (paramType.equals(double.class) || paramType.equals(Double.class)) {
            return Double.valueOf(param);
        }
        if (paramType.equals(char.class) || paramType.equals(Character.class)) {
            return Character.valueOf(param.charAt(0));
        }
        if (paramType.equals(boolean.class) || paramType.equals(Boolean.class)) {
            return Boolean.valueOf(param);
        }
        if (paramType.equals(String.class)) {
            return param;
        }
        if (paramType.equals(Date.class) || paramType.equals(Timestamp.class)) {
            return new Timestamp(Long.valueOf(param));
        }
        return JSON.parseObject(param, paramType);
    }

}
