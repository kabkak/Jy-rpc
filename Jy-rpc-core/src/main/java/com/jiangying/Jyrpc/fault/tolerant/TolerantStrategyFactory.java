package com.jiangying.Jyrpc.fault.tolerant;

import com.jiangying.Jyrpc.RpcApplication;
import com.jiangying.Jyrpc.fault.retry.Impl.FixedIntervalRetryStrategy;
import com.jiangying.Jyrpc.fault.retry.RetryStrategy;
import com.jiangying.Jyrpc.model.RpcResponse;
import com.jiangying.Jyrpc.spi.SpiLoader;

import java.util.Map;


public class TolerantStrategyFactory {

    static {
        SpiLoader.load(TolerantStrategy.class);
    }

    // public static final FaultTolerant FAULT_TOLERANT = new FaultTolerant();

    public static TolerantStrategy getTolerantStrategy() {

        // 获取负载均衡器
        String key = RpcApplication.getRpcProperties().getTolerantStrategy();


        return SpiLoader.getInstance(key, TolerantStrategy.class);
    }
}
