package com.jiangying.Jyrpc.constant;

public interface RpcConstant {

    /**
     * 默认配置文件加载前缀
     */
    String DEFAULT_CONFIG_PREFIX = "rpc";

    /**
     * 默认服务版本
     */
    String DEFAULT_SERVICE_VERSION = "1.0";

    /**
     * 默认序列化器jdk
     */
    String DEFAULT_SERIALIZER = "jdk";
    /**
     * 默认负载均衡算法
     */
    String DEFAULT_LOAD_BALANCER = "consistentHash";
    /**
     * 默认注册中心
     */
    String DEFAULT_REGISTRY = "zookeeper";

    /**
     * 默认重试策略
     */
    String DEFAULT_RETRY_STRATEGY = "noRetry";

    /**
     * 默认容错机制
     */
    String DEFAULT_TOLERANT_STRATEGY = "failFast";
}