package com.ifengxue.rpc.protocol;

import com.ifengxue.rpc.protocol.enums.CompressTypeEnum;
import com.ifengxue.rpc.protocol.enums.RequestProtocolTypeEnum;
import com.ifengxue.rpc.protocol.enums.SerializerTypeEnum;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

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
    /** 响应的错误堆栈 */
    private Throwable responseError;
    /** 请求上下文 */
    private final RequestContext requestContext;
    /** 方法调用结果 */
    private Object invokeResult;
    /** 绑定在响应上下文中的属性：可以在所有拦截器中读取 */
    private Map<String, Object> bindAttributeMap;

    public ResponseContext(RequestContext requestContext) {
        this.requestContext = requestContext;
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
        initBindAttributeMap();
        bindAttributeMap.put(key, attribute);
        return this;
    }

    /**
     * 获取绑定到响应上下文中的参数
     * @param key
     * @param <T>
     * @return
     */
    public <T> T getBindAttribute(String key) {
        initBindAttributeMap();
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
        initBindAttributeMap();
        return (T) Optional.ofNullable(getBindAttribute(key)).orElse(def);
    }

    private void initBindAttributeMap() {
        if (bindAttributeMap == null) {
            bindAttributeMap = new HashMap<>();
        }
    }

}
