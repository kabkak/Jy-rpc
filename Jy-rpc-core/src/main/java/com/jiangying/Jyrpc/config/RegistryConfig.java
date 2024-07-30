package com.jiangying.Jyrpc.config;

import com.jiangying.Jyrpc.constant.RpcConstant;
import com.jiangying.Jyrpc.registry.Impl.ZookeeperRegister;
import lombok.Data;
import org.apache.jute.compiler.generated.RccConstants;

@Data
public class RegistryConfig {

    /**
     * 注册中心类别
     */
    private String registry = RpcConstant.DEFAULT_REGISTRY;

    /**
     * 注册中心地址
     */
    private String address = "localhost:2181";

    /**
     * 用户名
     */
    private String username;

    /**
     * 密码
     */
    private String password;

    /**
     * 超时时间（单位毫秒）
     */
    private Long timeout = 10000L;
}