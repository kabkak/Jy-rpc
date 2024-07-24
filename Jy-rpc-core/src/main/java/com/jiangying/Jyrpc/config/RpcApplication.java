package com.jiangying.Jyrpc.config;

import cn.hutool.setting.dialect.Props;

public class RpcApplication {

    private static RpcConfig rpcProperties;

    public static void init() {

        //System.out.println(props);
        //Props jyRpc = cn.hutool.setting.dialect.Props.getProp("rpc");
        try {
            Props props = new Props("rpc.properties");
            System.out.println(props);
            rpcProperties = new RpcConfig();
            rpcProperties.setName(props.getStr("rpc.name"));
            rpcProperties.setServerHost(props.getStr("rpc.serverHost"));
            rpcProperties.setServerPort(Integer.valueOf(props.getStr("rpc.serverPort")));
            rpcProperties.setVersion(props.getStr("rpc.version"));
        } catch (Exception e) {
            rpcProperties = new RpcConfig();
        }

    }

    public static void init(RpcConfig jyRpc) {
        rpcProperties = jyRpc;
    }

    public static RpcConfig getRpcProperties() {
        if (rpcProperties == null) {
            synchronized (RpcApplication.class) {
                if (rpcProperties == null) {
                    init();
                }
            }
        }
        return rpcProperties;
    }

}
