package com.jiangying.Jyrpc.fault.retry;

import com.jiangying.Jyrpc.model.RpcResponse;

import java.util.concurrent.Callable;

public interface RetryStrategy {

     RpcResponse retry(Callable<RpcResponse> callable) throws Exception;
}
