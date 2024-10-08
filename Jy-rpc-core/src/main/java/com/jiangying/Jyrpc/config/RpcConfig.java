package com.jiangying.Jyrpc.config;


import com.jiangying.Jyrpc.constant.RpcConstant;
import lombok.Data;

@Data
public class RpcConfig {

    private String name = "Jy-rpc";//名称

    private String version = "1.0";// 版本号

    private String serverHost = "localhost";//服务器主机名

    private Integer serverPort = 8080; //服务器端口号

    private boolean mock = false; //是否开启mock

    private String serializer = RpcConstant.DEFAULT_SERIALIZER;//序列化

    private String loadBalancer = RpcConstant.DEFAULT_LOAD_BALANCER;//负载均衡

    private String retryStrategy = RpcConstant.DEFAULT_RETRY_STRATEGY;//重试策略

    private String TolerantStrategy = RpcConstant.DEFAULT_TOLERANT_STRATEGY; //容错机制


    private RegistryConfig registryConfig = new RegistryConfig();//注册中心配置
}
