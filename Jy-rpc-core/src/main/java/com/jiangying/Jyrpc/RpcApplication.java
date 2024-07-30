package com.jiangying.Jyrpc;

import com.jiangying.Jyrpc.config.RegistryConfig;
import com.jiangying.Jyrpc.config.RpcConfig;
import com.jiangying.Jyrpc.constant.RpcConstant;
import com.jiangying.Jyrpc.registry.Register;
import com.jiangying.Jyrpc.registry.RegisterFactory;
import com.jiangying.Jyrpc.utils.ConfigUtils;

public class RpcApplication {

    private static RpcConfig rpcConfig;

    public static void init() {
        RpcConfig newRpcConfig;
        try {
            newRpcConfig = ConfigUtils.loadConfig(RpcConfig.class, RpcConstant.DEFAULT_CONFIG_PREFIX);
            System.out.println("读取配置为: " + newRpcConfig);

        } catch (Exception e) {
            newRpcConfig = new RpcConfig();
        }
        init(newRpcConfig);
    }

    public static void init(RpcConfig jyRpc) {
        rpcConfig = jyRpc;
//todo 合并注册中心操作
//        Register register = RegisterFactory.getRegister();
//        register.init();
//        Runtime.getRuntime().addShutdownHook(new Thread(new Thread(register::destroy)));
    }

    public static RpcConfig getRpcProperties() {
        if (rpcConfig == null) {
            synchronized (RpcApplication.class) {
                if (rpcConfig == null) {
                    init();
                }
            }
        }
        return rpcConfig;
    }

}
