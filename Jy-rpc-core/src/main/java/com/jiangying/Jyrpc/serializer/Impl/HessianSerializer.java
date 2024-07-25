package com.jiangying.Jyrpc.serializer.Impl;

import com.caucho.hessian.io.HessianInput;
import com.caucho.hessian.io.HessianOutput;
import com.jiangying.Jyrpc.serializer.Serializer;

import java.io.*;

public class HessianSerializer implements Serializer {


    @Override
    public <T> byte[] serialize(T obj) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        HessianOutput hessianOutput = new HessianOutput(byteArrayOutputStream);
        hessianOutput.writeObject(obj);
        return byteArrayOutputStream.toByteArray();
    }

    @Override
    public <T> T deserialize(byte[] data, Class<T> clazz) throws IOException {
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(data);
        HessianInput hi = new HessianInput(byteArrayInputStream);
        return (T) hi.readObject(clazz);
    }
}
