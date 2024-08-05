package com.jiangying.Jyrpc.fault.tolerant.Impl;

import com.jiangying.Jyrpc.constant.TolerantStrategyConstant;
import com.jiangying.Jyrpc.fault.tolerant.TolerantStrategy;
import com.jiangying.Jyrpc.model.RpcRequest;
import com.jiangying.Jyrpc.model.RpcResponse;
import com.jiangying.Jyrpc.model.ServiceMetaInfo;
import com.jiangying.Jyrpc.server.tcp.VertxTcpClient;

import java.util.List;
import java.util.Map;


public class FailOverTolerantStrategy implements TolerantStrategy {
    @Override
    public RpcResponse doTolerant(Map<String, Object> map, Exception e) {
        List<ServiceMetaInfo> metaInfos = (List<ServiceMetaInfo>) map.get(TolerantStrategyConstant.SERVICE_LIST);
        ServiceMetaInfo metaInfo = (ServiceMetaInfo) map.get(TolerantStrategyConstant.CURRENT_SERVICE);
        RpcRequest rpcRequest = (RpcRequest) map.get(TolerantStrategyConstant.RPC_REQUEST);

        for (ServiceMetaInfo info : metaInfos) {
            if (!info.equals(metaInfo)) {
                try {
                    return VertxTcpClient.doRequest(rpcRequest, info);
                } catch (Exception ex) {
                    System.out.println(ex);
                }
            }
        }
        throw new RuntimeException(e);

    }
}
