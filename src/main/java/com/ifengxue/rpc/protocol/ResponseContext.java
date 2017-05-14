package com.ifengxue.rpc.protocol;

import com.ifengxue.rpc.protocol.enums.CompressTypeEnum;
import com.ifengxue.rpc.protocol.enums.RequestProtocolTypeEnum;
import com.ifengxue.rpc.protocol.enums.SerializerTypeEnum;
import com.ifengxue.rpc.server.factory.ServerConfigFactory;
import com.ifengxue.rpc.server.service.IServiceProvider;

import java.lang.reflect.Method;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 *  响应上下文
 *
 * Created by LiuKeFeng on 2017-04-21.
 */
public class ResponseContext {
    /** 请求进入时的时间戳key */
    public static final String REQUEST_IN_TIME_MILLIS_KEY = "request_in_time_millis_key";
    /** 请求离开时的时间戳 */
    public static final String REQUEST_OUT_TIME_MILLIS_KEY = "request_out_time_millis_key";
    private static Method $echoMethod;
    /** 响应的错误堆栈 */
    private Throwable responseError;
    /** 请求上下文 */
    private RequestContext requestContext;
    /** 方法调用结果 */
    private Object invokeResult;
    /** 绑定在响应上下文中的属性：可以在所有拦截器中读取 */
    private ConcurrentMap<String, Object> bindAttributeMap = new ConcurrentHashMap<>();

    private Class<?> requestClass;
    private Method requestMethod;
    private final IServiceProvider serviceProvider = ServerConfigFactory.getInstance().getServiceProvider();
    static {
        try {
            $echoMethod = IEchoService.class.getMethod("$echo", String.class);
        } catch (NoSuchMethodException e) {
        }
    }
    private ResponseContext() {}
    public ResponseContext(RequestContext requestContext) {
        this.requestContext = requestContext;
        try {
            requestClass = Class.forName(requestContext.getRequestProtocol().getClassName());
            requestMethod = serviceProvider.findAllProxyClass().get(requestContext.getRequestProtocol().getClassName())
                    .getMethod(requestContext.getRequestProtocol().getMethodName(),
                    requestContext.getRequestProtocol().getParameterTypes());
        } catch (Exception e) {
            throw new ProtocolException("请求的服务或方法不存在:" + e.getMessage(), e);
        }
    }

    /**
     * 异常响应结果上下文
     * @param requestContext
     * @param e
     * @return
     */
    public static ResponseContext newExceptionResponseContext(RequestContext requestContext, Exception e) {
        ResponseContext responseContext = new ResponseContext();
        responseContext.requestContext = requestContext;
        responseContext.responseError = e;
        return responseContext;
    }

    public int getRequestVersion() {
        return requestContext.getVersion();
    }

    public RequestProtocolTypeEnum getRequestProtocolTypeEnum() {
        return requestContext.getRequestProtocolTypeEnum();
    }

    public CompressTypeEnum getRequestCompressTypeEnum() {
        return requestContext.getCompressTypeEnum();
    }

    public SerializerTypeEnum getRequestSerializerTypeEnum() {
        return requestContext.getSerializerTypeEnum();
    }

    public String getRequestSessionID() {
        return requestContext.getRequestProtocol().getSessionID();
    }

    public String getRequestClassName() {
        return requestContext.getRequestProtocol().getClassName();
    }

    public String getRequestMethodName() {
        return requestContext.getRequestProtocol().getMethodName();
    }

    public Class<?>[] getRequestParameterTypes() {
        return requestContext.getRequestProtocol().getParameterTypes();
    }

    public Object[] getRequestParameters() {
        return requestContext.getRequestProtocol().getParameters();
    }

    public void setResponseError(Throwable error) {
        this.responseError = error;
    }

    public Throwable getResponseError() {
        return responseError;
    }

    public Object getInvokeResult() {
        return invokeResult;
    }

    public Class<?> getRequestClass() {
        return requestClass;
    }

    public Method getRequestMethod() {
        return requestMethod;
    }

    public void setInvokeResult(Object invokeResult) {
        this.invokeResult = invokeResult;
    }

    /**
     * 绑定参数到响应上下文中，所有类型的拦截器都可以访问这个属性
     * @param key
     * @param attribute
     * @return
     */
    public ResponseContext bindAttribute(String key, Object attribute) {
        bindAttributeMap.put(key, attribute);
        return this;
    }

    /**
     * 移除绑定到响应上下文中的参数
     * @param key
     * @param <T>
     * @return 之前绑定的值，如果不存在则返回null
     */
    public <T> T removeBindAttribute(String key) {
        return (T) bindAttributeMap.remove(key);
    }

    /**
     * 移除绑定到响应上下文的参数
     * @param key
     * @param def 如果没有绑定则返回该值
     * @param <T>
     * @return
     */
    public <T> T removeBindAttribute(String key, T def) {
        return (T) Optional.ofNullable(removeBindAttribute(key)).orElse(def);
    }

    /**
     * 获取绑定到响应上下文中的参数
     * @param key
     * @param <T>
     * @return
     */
    public <T> T getBindAttribute(String key) {
        return (T) bindAttributeMap.get(key);
    }

    /**
     * 获取绑定到响应上下文中的参数
     * @param key
     * @param def 默认值
     * @param <T>
     * @return
     */
    public <T> T getBindAttribute(String key, T def) {
        return (T) Optional.ofNullable(getBindAttribute(key)).orElse(def);
    }
}
