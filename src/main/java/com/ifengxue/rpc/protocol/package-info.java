/**
 *  RPC调用的序列化与反序列化协议
 *  4字节总长度 + 1字节版本号 + 1字节请求/响应类型 + 1字节压缩类型 + 1字节序列化类型 + 12字节保留位 + 真实数据长度
 *  真实书长度为100字节，那么服务端/客户端将收到100+4+1+1+1+1+12=120字节
 * Created by LiuKeFeng on 2017-04-20.
 */
package com.ifengxue.rpc.protocol;