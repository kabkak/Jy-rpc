package com.jiangying.Jyrpc.fault.tolerant.Impl;


import com.jiangying.Jyrpc.fault.tolerant.TolerantStrategy;
import com.jiangying.Jyrpc.model.RpcResponse;

import java.util.Map;

public class FailFastTolerantStrategy implements TolerantStrategy {


    @Override
    public RpcResponse doTolerant(Map<String, Object> map, Exception e) {
            throw new RuntimeException(e);
    }
}
