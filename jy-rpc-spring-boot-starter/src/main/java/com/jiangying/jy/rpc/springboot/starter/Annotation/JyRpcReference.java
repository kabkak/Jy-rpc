package com.jiangying.jy.rpc.springboot.starter.Annotation;

import com.jiangying.Jyrpc.constant.RpcConstant;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface JyRpcReference {

    /**
     * 服务接口类
     * @return
     */
    Class<?> interfaceClass() default void.class;

    /**
     * 服务版本
     * @return
     */
    String serviceVersion() default RpcConstant.DEFAULT_SERVICE_VERSION;

    /**
     * 负载均衡策略
     * @return
     */
    String loadBalancer() default RpcConstant.DEFAULT_LOAD_BALANCER;

    /**
     * 重试策略
     * @return
     */
    String retryStrategy() default RpcConstant.DEFAULT_RETRY_STRATEGY;

    /**
     * 容错策略
     * @return
     */
    String tolerantStrategy() default RpcConstant.DEFAULT_TOLERANT_STRATEGY;

    /**
     * 是否mock
     * @return
     */
    boolean mock() default false;
}
