package com.jiangying.Jyrpc.fault.tolerant.Impl;


import com.jiangying.Jyrpc.fault.tolerant.TolerantStrategy;
import com.jiangying.Jyrpc.model.RpcResponse;

import java.util.Map;

public class FailSilentTolerantStrategy implements TolerantStrategy {
    RpcResponse defaultResponse;

    public FailSilentTolerantStrategy(RpcResponse defaultResponse) {
        this.defaultResponse = defaultResponse;
    }

    public FailSilentTolerantStrategy() {
        this.defaultResponse = new RpcResponse();
    }


    @Override
    public RpcResponse doTolerant(Map<String, Object> map, Exception e) {
        return defaultResponse;
    }
}
