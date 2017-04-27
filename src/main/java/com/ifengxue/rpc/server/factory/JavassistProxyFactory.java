package com.ifengxue.rpc.server.factory;

import com.ifengxue.rpc.protocol.IEchoService;
import com.ifengxue.rpc.protocol.ResponseContext;
import com.ifengxue.rpc.protocol.annotation.RpcService;
import javassist.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 使用javassist生成接口代理
 * Created by LiuKeFeng on 2017-04-27.
 */
public class JavassistProxyFactory {
    private static final ClassPool CLASS_POOL = ClassPool.getDefault();
    /**
     * 指定接口实现类的代理实例Map
     */
    private static final Map<Class<?>, Object> CLAZZ_PROXY_INSTANCE_MAP = new ConcurrentHashMap<>();
    private static final Logger LOGGER = LoggerFactory.getLogger(JavassistProxyFactory.class);

    /**
     * 返回指定类的代理实例，该实例会实现{@link IEchoService}
     * @param clazz
     * @return
     */
    public static Object getProxyInstance(Class<?> clazz) {
        return CLAZZ_PROXY_INSTANCE_MAP.computeIfAbsent(clazz, clazzKey -> {
            Object instance;
            try {
                CtClass ctClass = CLASS_POOL.get(clazzKey.getName());
                ctClass.addInterface(CLASS_POOL.get(IEchoService.class.getName()));
                ctClass.addMethod(CtMethod.make("public String $echo(String echo) {return echo;}", ctClass));
//                ctClass.addInterface(CLASS_POOL.get(IInvokeProxyService.class.getName()));
//                ctClass.addMethod(createInvokeProxyMethod(clazzKey, ctClass));
                if (Boolean.getBoolean("rpc.debug")) {
                    new File("debug/proxy_class").mkdirs();
                    LOGGER.debug("写出代理类到debug/proxy_class");
                    ctClass.debugWriteFile("debug/proxy_class");
                }
                ctClass.setName(clazzKey.getName() + "JavassistProxy");
                instance = ctClass.toClass().newInstance();
            } catch (Exception e) {
                throw new IllegalStateException(e);
            }
            return instance;
        });
    }

    //FIXME:不能创建重载的类的代理
    private static CtMethod createInvokeProxyMethod(Class<?> clazz, CtClass ctClass) throws CannotCompileException {
        RpcService[] rpcServices = clazz.getAnnotationsByType(RpcService.class);
        List<Method> publicMethods = new ArrayList<>();
        /**
         * 获取所有对外提供服务的方法
         */
        for (RpcService rpcService : rpcServices) {
            Class<?> interfaceClass = rpcService.value();
            Method[] methods = interfaceClass.getMethods();
            publicMethods.addAll(Arrays.asList(methods));
        }
        publicMethods.addAll(Arrays.asList(IEchoService.class.getMethods()));

        StringBuilder builder = new StringBuilder("public void invokeProxy(");
        builder.append(ResponseContext.class.getName()).append(" context) throws Exception {");
        builder.append("String requestMethodName = ").append("context.getRequestMethodName();");
        builder.append("Class[] requestParameterTypes = ").append("context.getRequestParameterTypes();");
        builder.append("Object[] params = ").append("context.getRequestParameters();");
        for (Method publicMethod : publicMethods) {
            String methodName = publicMethod.getName();
            Class<?>[] parameterTypes = publicMethod.getParameterTypes();
            Class<?> returnType = publicMethod.getReturnType();
            String requestParameterTypesString;
            if (parameterTypes.length != 0) {
                StringBuilder sb = new StringBuilder();
                for (Class<?> parameterType : parameterTypes) {
                    sb.append("Class.forName(\"").append(parameterType.getName()).append("\"), ");
                }
                requestParameterTypesString = sb.toString().substring(0, sb.length() - 2);
            } else {
                requestParameterTypesString = "";
            }
            //请求方法名称相同，且都是无参
            builder.append("if (requestMethodName.equals(\"").append(methodName)
                    .append("\") && ");
            if (requestParameterTypesString.isEmpty()) {
                builder.append("(requestParameterTypes.length == ").append(parameterTypes.length);
            } else {
                //请求方法名称相同，参数列表类型一致
                builder.append("java.util.Arrays.equals(requestParameterTypes, new Class[]{").append(requestParameterTypesString).append("}");
            }
            builder.append(")) {");
            boolean isVoidClass = returnType.equals(void.class) || returnType.equals(Void.class);
            boolean isPrimitiveAndNotVoidClass = !isVoidClass && returnType.isPrimitive();
            if (!isVoidClass) {
                builder.append("context.setInvokeResult(");
            }
            if (isPrimitiveAndNotVoidClass) {
                if (returnType.equals(byte.class)) {
                    builder.append("Byte");
                } else if (returnType.equals(short.class)) {
                    builder.append("Short");
                } else if (returnType.equals(int.class)) {
                    builder.append("Integer");
                } else if (returnType.equals(long.class)) {
                    builder.append("Long");
                } else if (returnType.equals(float.class)) {
                    builder.append("Float");
                } else if (returnType.equals(double.class)) {
                    builder.append("Double");
                } else if (returnType.equals(boolean.class)) {
                    builder.append("Boolean");
                } else if (returnType.equals(char.class)) {
                    builder.append("Character");
                } else {
                    throw new IllegalStateException();
                }
                builder.append(".valueOf(");
            }
            builder.append(methodName).append("(");
            if (parameterTypes.length != 0) {
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < parameterTypes.length; i++) {
                    sb.append("(").append(parameterTypes[i].getName()).append(") params[").append(i).append("], ");
                }
                builder.append(sb.toString().substring(0, sb.length() - 2));
            }
            if (!isVoidClass) {
                builder.append(")");
            }
            if (isPrimitiveAndNotVoidClass) {
                builder.append("));return;");
            } else {
                builder.append(");return;");
            }
            builder.append("}");
        }
        builder.append("}");
        return CtMethod.make(builder.toString(), ctClass);
    }
}
