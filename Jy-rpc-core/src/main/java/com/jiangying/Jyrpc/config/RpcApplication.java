package com.jiangying.Jyrpc.config;

import cn.hutool.setting.dialect.Props;

import java.sql.SQLOutput;

public class RpcApplication {

    private static RpcConfig rpcProperties;

    public static void init() {


        try {
            Props props = new Props("rpc.properties");
            System.out.println(props);
            rpcProperties = new RpcConfig();
            //进行判断props.getStr 不为空则添加
            if (props.getStr("rpc.name") != null) {
                rpcProperties.setName(props.getStr("rpc.name"));
            }
            if (props.getStr("rpc.serverHost") != null) {
                rpcProperties.setServerHost(props.getStr("rpc.serverHost"));
            }
            if (props.getStr("rpc.serverPort") != null) {
                rpcProperties.setServerPort(Integer.valueOf(props.getStr("rpc.serverPort")));
            }
            if (props.getStr("rpc.version") != null) {
                rpcProperties.setVersion(props.getStr("rpc.version"));
            }
            if (props.getStr("rpc.serializer") != null) {
                rpcProperties.setSerializer(props.getStr("rpc.serializer"));
            }
            if (props.getStr("rpc.mock") != null) {
                rpcProperties.setMock(Boolean.valueOf(props.getStr("rpc.mock")));
            }
            System.out.println(rpcProperties);
        } catch (Exception e) {

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
