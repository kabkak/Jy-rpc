package com.jiangying.Jyrpc.fault.retry.Impl;

import com.jiangying.Jyrpc.fault.retry.RetryStrategy;
import com.jiangying.Jyrpc.model.RpcResponse;

import java.util.concurrent.Callable;

public class ExponentialBackoffRetryStrategy implements RetryStrategy {
    private static final long INITIAL_BACKOFF = 100; // 初始退避时间（毫秒）
    private static final int BACKOFF_MULTIPLIER = 2; // 退避乘数
    private static final int MAX_RETRIES = 5; // 最大重试次数

    @Override
    public RpcResponse retry(Callable<RpcResponse> callable) throws Exception {
        // 初始化退避时间
        long backoff = INITIAL_BACKOFF;
        // 初始化重试次数
        int retries = 0;

        while (true) {
            try {
                // 尝试执行RPC调用
                return callable.call();
            } catch (Exception e) {
                // 如果达到最大重试次数，则抛出异常
                if (++retries > MAX_RETRIES) {
                    throw e;
                }
                // 等待退避时间
                Thread.sleep(backoff);
                // 更新退避时间
                backoff = backoff * BACKOFF_MULTIPLIER;
            }
        }

    }
}
