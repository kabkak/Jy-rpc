package com.jiangying.Jyrpc.loadbalancer.Impl;

import com.jiangying.Jyrpc.loadbalancer.LoadBalancer;
import com.jiangying.Jyrpc.model.ServiceMetaInfo;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class RoundRobinLoadBalancer implements LoadBalancer {
    private final AtomicInteger atomicInteger = new AtomicInteger();

    @Override
    public ServiceMetaInfo select(Map<String, Object> requestParams, List<ServiceMetaInfo> serviceMetaInfoList) {
        if (serviceMetaInfoList == null || serviceMetaInfoList.isEmpty()) {
            return null;
        }
        if (serviceMetaInfoList.size() == 1) {
            return serviceMetaInfoList.get(0);
        }
        int index = atomicInteger.get() % serviceMetaInfoList.size();
        atomicInteger.set(index + 1);
        return serviceMetaInfoList.get(index);
    }
}
