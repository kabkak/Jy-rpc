package com.jiangying.Jyrpc.fault.retry.Impl;

import com.github.rholder.retry.*;
import com.jiangying.Jyrpc.fault.retry.RetryStrategy;
import com.jiangying.Jyrpc.model.RpcResponse;

import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;


public class FixedIntervalRetryStrategy implements RetryStrategy {
    private static final long INITIAL_BACKOFF = 1000; // 初始退避时间（毫秒）

    private static final int MAX_RETRIES = 3; // 最大重试次数

    public RpcResponse retry(Callable<RpcResponse> callable) throws Exception {

        int retries = 0;
        while (true) {
            try {
                return callable.call();
            } catch (Exception e) {

                if (++retries > MAX_RETRIES){
                    throw e;
                }
                Thread.sleep(INITIAL_BACKOFF);
            }
        }



    }
}
