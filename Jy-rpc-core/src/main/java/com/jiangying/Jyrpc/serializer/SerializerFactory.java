package com.jiangying.Jyrpc.serializer;

import com.jiangying.Jyrpc.config.RpcApplication;
import com.jiangying.Jyrpc.serializer.Impl.JdkSerializer;
import com.jiangying.Jyrpc.spi.SpiLoader;



public class SerializerFactory {

    static {
        SpiLoader.load(Serializer.class);
    }

    private static Serializer DEFAULT_SERIALIZER = new JdkSerializer();
    public static Serializer getSerializer() {
        RpcApplication.init();
        String key = RpcApplication.getRpcProperties().getSerializer();
        return SpiLoader.getInstance(key, Serializer.class);
    }
}
