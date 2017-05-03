package com.ifengxue.rpc.protocol.json;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.ifengxue.rpc.server.json.IJSONRequestDispatcher;
import com.ifengxue.rpc.server.json.SimpleJSONRequestDispatcher;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.multipart.Attribute;
import io.netty.handler.codec.http.multipart.HttpPostRequestDecoder;
import io.netty.handler.codec.http.multipart.InterfaceHttpData;

import java.lang.reflect.InvocationTargetException;
import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.util.*;

/**
 * json-rpc解码器
 * Created by LiuKeFeng on 2017-04-29.
 */
public class JSONRequestDecoder extends ChannelInboundHandlerAdapter {
    private IJSONRequestDispatcher dispatcher = new SimpleJSONRequestDispatcher();
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (!(msg instanceof HttpRequest)) {
            ctx.channel().close();
            return;
        }
        HttpRequest httpRequest = (HttpRequest) msg;
        Map<String, String> paramMap = new HashMap<>(3);
        String service;
        if (httpRequest.getMethod() == HttpMethod.GET) {
            QueryStringDecoder queryStringDecoder = new QueryStringDecoder(httpRequest.getUri());
            service = queryStringDecoder.path();
            queryStringDecoder.parameters().forEach((key, listValue) -> paramMap.put(key, listValue.get(0)));
        } else if (httpRequest.getMethod() == HttpMethod.POST) {
            service = httpRequest.getUri();
            HttpPostRequestDecoder postRequestDecoder = new HttpPostRequestDecoder(httpRequest);
            for (InterfaceHttpData interfaceHttpData : postRequestDecoder.getBodyHttpDatas()) {
                Attribute attribute = (Attribute) interfaceHttpData;
                paramMap.put(attribute.getName(), attribute.getValue());
            }
        } else {
            return;
        }
        String params = paramMap.get("params");
        if (params != null) {
            params = io.netty.handler.codec.base64.Base64.decode(
                    Unpooled.copiedBuffer(params.getBytes("UTF-8"))).toString(Charset.forName("UTF-8"));
        }
        JSONRequest jsonRequest = new JSONRequest(service.replaceFirst("/", ""), paramMap.get("id"), paramMap.get("method"), JSONObject.parseObject(params));
        HttpHeaders headers = httpRequest.headers();
        InetSocketAddress inetSocketAddress = (InetSocketAddress) ctx.channel().remoteAddress();
        JSONRequestProtocol protocol = new JSONRequestProtocol(inetSocketAddress, jsonRequest, headers);
        JSONResponseProtocol responseProtocol = dispatcher.dispatch(protocol);
        FullHttpResponse httpResponse = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK,
                Unpooled.wrappedBuffer(JSON.toJSONString(responseProtocol).getBytes("UTF-8")));
        httpResponse.headers().set(HttpHeaders.Names.CONTENT_TYPE, "application/json;charset=utf-8");
        httpResponse.headers().set(HttpHeaders.Names.CONTENT_LENGTH, httpResponse.content().readableBytes());
        ctx.writeAndFlush(httpResponse);
        ctx.channel().close();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        if (cause instanceof InvocationTargetException) {
            cause = cause.getCause();
        }
        cause.printStackTrace();
    }
}
