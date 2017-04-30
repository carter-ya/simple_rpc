package com.ifengxue.rpc.server.json;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.ifengxue.rpc.protocol.*;
import com.ifengxue.rpc.protocol.annotation.HttpMethod;
import com.ifengxue.rpc.protocol.annotation.Param;
import com.ifengxue.rpc.protocol.annotation.RpcService;
import com.ifengxue.rpc.protocol.enums.CompressTypeEnum;
import com.ifengxue.rpc.protocol.enums.RequestProtocolTypeEnum;
import com.ifengxue.rpc.protocol.enums.SerializerTypeEnum;
import com.ifengxue.rpc.protocol.json.JSONRequestProtocol;
import com.ifengxue.rpc.protocol.json.JSONResponseProtocol;
import com.ifengxue.rpc.protocol.json.JSONRpcError;
import com.ifengxue.rpc.protocol.json.JSONRpcMethod;
import com.ifengxue.rpc.server.factory.ServerConfigFactory;
import com.ifengxue.rpc.server.handle.IInvokeHandler;
import com.ifengxue.rpc.server.service.IServiceProvider;
import com.ifengxue.rpc.util.JSONParamConverter;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by LiuKeFeng on 2017-04-29.
 */
public class SimpleJSONRequestDispatcher implements IJSONRequestDispatcher {
    private Map<String, List<Method>> serviceMethodListMap = new HashMap<>();
    private Map<String, Object> serviceObjectMap = new HashMap<>();
    private Map<String, String> serviceDescriptionMap = new HashMap<>();
    private final IInvokeHandler invokeHandler = ServerConfigFactory.getInstance().getInvokeHandler();
    public SimpleJSONRequestDispatcher() {
        init();
    }

    @Override
    public JSONResponseProtocol dispatch(JSONRequestProtocol requestProtocol) {
        //返回所有的服务列表
        if (requestProtocol.getService().isEmpty() || "/".equals(requestProtocol.getService())) {
            return new JSONResponseProtocol(serviceDescriptionMap, null, null);
        }
        //返回指定服务的方法列表
        if (requestProtocol.getParamJSONObject() == null && serviceMethodListMap.containsKey(requestProtocol.getService())) {
            return new JSONResponseProtocol(
                    serviceMethodListMap.get(requestProtocol.getService()).stream()
                            .map(this::convertMethod2JSONRpcMethod)
                            .collect(Collectors.toList()),
                    null,
                    null);
        }
        //非法的请求
        if (requestProtocol.getParamJSONObject() == null) {
            return new JSONResponseProtocol(null,
                    new JSONRpcError(JSONRpcError.INVALID_PARAMS, JSONRpcError.INVALID_PARAMS_MESSAGE, null),
                    requestProtocol.getID());
        }
        //不存在服务
        if (!serviceMethodListMap.containsKey(requestProtocol.getService())) {
            return new JSONResponseProtocol(null,
                    new JSONRpcError(JSONRpcError.SERVICE_NOT_FOUND, JSONRpcError.SERVICE_NOT_FOUND_MESSAGE, null),
                    requestProtocol.getID());
        }

        //查找匹配的方法
        int paramSize = requestProtocol.getParamJSONObject().size();
        String methodName = requestProtocol.getMethod();
        List<Method> matchedMethodList = serviceMethodListMap.get(requestProtocol.getService())
                .stream()
                .filter(m -> Optional.ofNullable(m.getAnnotation(HttpMethod.class)).map(HttpMethod::value).orElse(m.getName()).equals(methodName))
                .filter(m -> m.getParameterTypes().length == paramSize)
                .collect(Collectors.toList());
        if (matchedMethodList.isEmpty()) {
            return new JSONResponseProtocol(null,
                    new JSONRpcError(JSONRpcError.METHOD_NOT_FOUND, JSONRpcError.METHOD_NOT_FOUND_MESSAGE, null),
                    requestProtocol.getID());
        }
        Method matchedMethod = null;
        if (matchedMethodList.size() == 1) {
            matchedMethod = matchedMethodList.get(0);
        } else {
            Set<String> argumentNames = requestProtocol.getParamJSONObject().keySet();
            for (Method method : matchedMethodList) {
                Parameter[] parameters = method.getParameters();
                Set<String> methodArgumentNames = Arrays.stream(parameters)
                        .map(parameter -> Optional.ofNullable(parameter.getAnnotation(Param.class)).map(Param::value)
                                .orElse(parameter.getName()))
                        .collect(Collectors.toSet());
                if (methodArgumentNames.containsAll(argumentNames)) {
                    matchedMethod = method;
                    break;
                }
            }
        }
        if (matchedMethod == null) {
            return new JSONResponseProtocol(null,
                    new JSONRpcError(JSONRpcError.METHOD_NOT_FOUND, JSONRpcError.METHOD_NOT_FOUND_MESSAGE, null),
                    requestProtocol.getID());
        }

        //转换请求参数为对应的参数对象
        Object[] args = new Object[paramSize];
        JSONObject paramJSONObject = requestProtocol.getParamJSONObject();
        if (paramSize > 0) {
            Parameter[] parameters = matchedMethod.getParameters();
            Class<?>[] classTypes = matchedMethod.getParameterTypes();
            for (int i = 0; i < parameters.length; i++) {
                Parameter parameter = parameters[i];
                Class<?> classType = classTypes[i];
                String paramString = paramJSONObject.getString(
                        Optional.ofNullable(parameter.getAnnotation(Param.class)).map(Param::value).orElse(parameter.getName()));
                if (paramString == null) {
                    return new JSONResponseProtocol(null,
                            new JSONRpcError(JSONRpcError.INVALID_PARAMS, JSONRpcError.INVALID_PARAMS_MESSAGE,
                                    "参数丢失[" + parameter.getName() + "]"),
                            requestProtocol.getID());
                }
                if (classType.equals(Object.class)) {
                    return new JSONResponseProtocol(null,
                            new JSONRpcError(JSONRpcError.INTERNAL_ERROR, JSONRpcError.INTERNAL_ERROR_MESSAGE,
                                    "不支持Object类型的参数"),
                            requestProtocol.getID());
                }
                args[i] = JSONParamConverter.convert(paramString, classType);
            }
        }

        //构造请求协议
        RequestContext requestContext = new RequestContext(
                ProtocolConsts.VERSION,
                RequestProtocolTypeEnum.METHOD_INVOKE,
                CompressTypeEnum.UNCOMPRESS,
                SerializerTypeEnum.JSON_RPC_SERIALIZER,
                RequestProtocol.Builder
                    .newBuilder()
                    .setSessionID(requestProtocol.getID())
                    .setParameters(args)
                    .setParameterTypes(matchedMethod.getParameterTypes())
                    .setMethodName(matchedMethod.getName())
                    /** 取到任意一个服务的接口名称即可：{@link com.ifengxue.rpc.protocol.ResponseContext#ResponseContext(RequestContext)} */
                    .setClassName(serviceObjectMap.get(requestProtocol.getService())
                            .getClass().getAnnotationsByType(RpcService.class)[0].value().getName())
                    .setRequestProtocolTypeEnum(RequestProtocolTypeEnum.METHOD_INVOKE)
                    .build());

        //在拦截器中执行方法调用
        ResponseContext responseContext = new ResponseContext(requestContext);
        try {
            invokeHandler.methodInvoke(responseContext);
        } catch (Exception e) {
            if (e instanceof InvocationTargetException) {
                e = (Exception) e.getCause();
            }
            responseContext.setResponseError(e);
        }

        //构造响应结果
        JSONResponseProtocol responseProtocol = new JSONResponseProtocol(
                Optional.ofNullable(responseContext.getInvokeResult()).map(JSON::toJSONString).orElse(null),
                Optional.ofNullable(responseContext.getResponseError())
                        .map(throwable -> throwable.getClass().getSimpleName() + ":" + responseContext.getResponseError().getMessage())
                        .orElse(null),
                responseContext.getRequestSessionID());
        return responseProtocol;
    }

    /**
     * 初始化json-rpc
     */
    private void init() {
        IServiceProvider serviceProvider = ServerConfigFactory.getInstance().getServiceProvider();
        Map<String, Class<?>> serviceProxyClassMap = serviceProvider.findAllProxyClass();
        Map<String, Object> serviceProxyInstanceMap = serviceProvider.findAllServices();
        Method $echoMethod;
        try {
            $echoMethod = IEchoService.class.getMethod("$echo", String.class);
        } catch (NoSuchMethodException e) {
            throw new IllegalStateException(e);
        }
        for (Map.Entry<String, Class<?>> serviceProxyClassEntry : serviceProxyClassMap.entrySet()) {
            RpcService[] rpcServices = serviceProxyClassEntry.getValue().getAnnotationsByType(RpcService.class);
            for (RpcService rpcService : rpcServices) {
                String service = rpcService.service();
                if (service.isEmpty()) {
                    service = rpcService.value().getSimpleName();
                }
                //服务描述
                serviceDescriptionMap.put(service, rpcService.description());
                //服务实现对象
                serviceObjectMap.put(service, serviceProxyInstanceMap.get(serviceProxyClassEntry.getKey()));
                List<Method> proxyMethods = new ArrayList<>();
                for (Method method : rpcService.value().getMethods()) {
                    try {
                        Method proxyMethod = serviceProxyClassEntry.getValue().getMethod(method.getName(), method.getParameterTypes());
                        proxyMethods.add(proxyMethod);
                    } catch (NoSuchMethodException e) {
                        e.printStackTrace();
                    }
                }
                //注册回声测试方法
                proxyMethods.add($echoMethod);
                //服务提供的方法
                serviceMethodListMap.put(service, proxyMethods);
            }
        }
    }

    /**
     * Method to JSONRpcMethod
     * @param method
     * @return
     */
    private JSONRpcMethod convertMethod2JSONRpcMethod(Method method) {
        JSONRpcMethod jsonRpcMethod = new JSONRpcMethod();
        jsonRpcMethod.setMethod(Optional.ofNullable(method.getAnnotation(HttpMethod.class)).map(HttpMethod::value).orElse(method.getName()));
        jsonRpcMethod.setDescription(Optional.ofNullable(method.getAnnotation(HttpMethod.class)).map(HttpMethod::value).orElse(""));
        Class<?>[] parameterTypes = method.getParameterTypes();
        Parameter[] parameters = method.getParameters();
        List<JSONRpcMethod.Param> params = new ArrayList<>(parameters.length);
        for (int i = 0; i < parameters.length; i++) {
            Parameter parameter = parameters[i];
            Class<?> classType = parameterTypes[i];
            JSONRpcMethod.Param param = new JSONRpcMethod.Param();
            param.setParamName(Optional.ofNullable(parameter.getAnnotation(Param.class)).map(Param::value).orElse(parameter.getName()));
            param.setParamType(classType.getSimpleName());
            params.add(param);
        }
        jsonRpcMethod.setParams(params);
        return jsonRpcMethod;
    }
}
