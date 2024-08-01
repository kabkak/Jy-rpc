package com.jiangying.Jyrpc.fault.retry;

import com.jiangying.Jyrpc.RpcApplication;
import com.jiangying.Jyrpc.fault.retry.Impl.FixedIntervalRetryStrategy;
import com.jiangying.Jyrpc.loadbalancer.Impl.ConsistentHashLoadBalancer;
import com.jiangying.Jyrpc.loadbalancer.LoadBalancer;
import com.jiangying.Jyrpc.spi.SpiLoader;


public class RetryStrategyFactory {

    static {
        SpiLoader.load(RetryStrategy.class);
    }

    public static final RetryStrategy FIXED_INTERVAL_RETURN_STRATEGY = new FixedIntervalRetryStrategy();

    public static RetryStrategy getRetryStrategy() {

        // 获取负载均衡器
        String key = RpcApplication.getRpcProperties().getRetryStrategy();


        return SpiLoader.getInstance(key, RetryStrategy.class);
    }
}
