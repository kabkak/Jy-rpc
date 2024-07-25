package com.jiangying.Jyrpc.registry;

import com.jiangying.Jyrpc.config.RpcApplication;
import com.jiangying.Jyrpc.serializer.Serializer;
import com.jiangying.Jyrpc.spi.SpiLoader;


public class RegisterFactory {

    static {
        SpiLoader.load(Register.class);
    }


    public static Serializer getRegister() {

        //todo 获取注册中心配置
        String key = RpcApplication.getRpcProperties().getSerializer();


        return SpiLoader.getInstance(key, Register.class);
    }
}
