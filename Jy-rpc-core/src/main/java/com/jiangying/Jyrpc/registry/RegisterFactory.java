package com.jiangying.Jyrpc.registry;

import com.jiangying.Jyrpc.RpcApplication;
import com.jiangying.Jyrpc.registry.Impl.ZookeeperRegister;
import com.jiangying.Jyrpc.serializer.Serializer;
import com.jiangying.Jyrpc.spi.SpiLoader;


public class RegisterFactory {

    static {
        SpiLoader.load(Register.class);
    }

    public static final Register DEFAULT_REGISTER = new ZookeeperRegister();

    public static Register getRegister() {

        //todo 获取注册中心配置
        String key = RpcApplication.getRpcProperties().getRegistryConfig().getRegistry();


        return SpiLoader.getInstance(key, Register.class);
    }
}
