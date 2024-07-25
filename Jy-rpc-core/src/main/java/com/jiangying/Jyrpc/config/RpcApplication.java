package com.jiangying.Jyrpc.config;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.setting.dialect.Props;
import com.jiangying.Jyrpc.constant.RpcConstant;
import com.jiangying.Jyrpc.utils.ConfigUtils;

import java.sql.SQLOutput;

public class RpcApplication {

    private static RpcConfig rpcConfig;

    public static void init() {
        RpcConfig newRpcConfig;
        try {
            newRpcConfig = ConfigUtils.loadConfig(RpcConfig.class, RpcConstant.DEFAULT_CONFIG_PREFIX);
            System.out.println(newRpcConfig);

        } catch (Exception e) {
            newRpcConfig = new RpcConfig();
        }
        init(newRpcConfig);
    }

    public static void init(RpcConfig jyRpc) {
        rpcConfig = jyRpc;
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
