package com.ifengxue.rpc.server.json;

import com.ifengxue.rpc.protocol.json.JSONRequestProtocol;
import com.ifengxue.rpc.protocol.json.JSONResponseProtocol;

/**
 * json请求分发器
 *
 * Created by LiuKeFeng on 2017-04-29.
 */
public interface IJSONRequestDispatcher {
    /**
     * 请求分发
     * @param requestProtocol 请求协议
     * @return
     */
    JSONResponseProtocol dispatch(JSONRequestProtocol requestProtocol);
}
