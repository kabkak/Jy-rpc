package com.jiangying.Jyrpc.serializer.Impl;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.jiangying.Jyrpc.serializer.Serializer;

import java.io.*;

public class KryoSerializer implements Serializer {


    private static final ThreadLocal<Kryo> kryoThreadLocal = ThreadLocal.withInitial(() -> {
        Kryo kryo = new Kryo();
        kryo.setReferences(true);
        kryo.setRegistrationRequired(false);
        return kryo;
    });

    /**
     * 序列化
     *
     * @param obj
     * @param <T>
     * @return
     * @throws IOException
     */
    @Override
    public <T> byte[] serialize(T obj) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        Output output = new Output(byteArrayOutputStream);
        kryoThreadLocal.get().writeObject(output, obj);
        output.close();
        return byteArrayOutputStream.toByteArray();

    }

    /**
     * 反序列化
     *
     * @param data
     * @param clazz
     * @param <T>
     * @return
     * @throws IOException
     */

    @Override
    public <T> T deserialize(byte[] data, Class<T> clazz) throws IOException {
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(data);
        Input input = new Input(byteArrayInputStream);
        T result = kryoThreadLocal.get().readObject(input, clazz);
        input.close();
        return (T) result;
    }
}
