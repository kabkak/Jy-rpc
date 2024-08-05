package com.jiangying.Jyrpc.fault.tolerant;

import com.jiangying.Jyrpc.model.RpcResponse;

import java.util.Map;

public interface TolerantStrategy {

    RpcResponse doTolerant(Map<String, Object> map, Exception e);
}
