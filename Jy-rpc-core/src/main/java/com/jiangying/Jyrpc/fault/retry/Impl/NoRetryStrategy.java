package com.jiangying.Jyrpc.fault.retry.Impl;

import com.jiangying.Jyrpc.fault.retry.RetryStrategy;
import com.jiangying.Jyrpc.model.RpcResponse;

import java.util.concurrent.Callable;

public class NoRetryStrategy implements RetryStrategy {

     public RpcResponse retry(Callable<RpcResponse> callable) throws Exception {

          return callable.call();
     }
}
