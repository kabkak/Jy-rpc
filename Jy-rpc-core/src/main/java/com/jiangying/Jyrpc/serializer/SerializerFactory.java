package com.jiangying.Jyrpc.serializer;

import com.jiangying.Jyrpc.config.RpcApplication;
import com.jiangying.Jyrpc.serializer.Impl.JdkSerializer;
import com.jiangying.Jyrpc.spi.SpiLoader;
import lombok.Data;

import java.util.concurrent.TimeUnit;


public class SerializerFactory {

    static {
        SpiLoader.load(Serializer.class);
    }


    public static Serializer getSerializer() {

        String key = RpcApplication.getRpcProperties().getSerializer();


        return SpiLoader.getInstance(key, Serializer.class);
    }
}
