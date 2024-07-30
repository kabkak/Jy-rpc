package com.jiangying.Jyrpc.serializer;

import com.jiangying.Jyrpc.RpcApplication;
import com.jiangying.Jyrpc.serializer.Impl.JdkSerializer;
import com.jiangying.Jyrpc.spi.SpiLoader;


public class SerializerFactory {

    static {
        SpiLoader.load(Serializer.class);
    }

    private static final Serializer DEFAULT_SERIALIZER = new JdkSerializer();
    public static Serializer getSerializer() {

        String key = RpcApplication.getRpcProperties().getSerializer();


        return SpiLoader.getInstance(key, Serializer.class);
    }
}
