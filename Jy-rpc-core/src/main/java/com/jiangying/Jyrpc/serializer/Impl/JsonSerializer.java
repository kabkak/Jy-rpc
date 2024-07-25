package com.jiangying.Jyrpc.serializer.Impl;


import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jiangying.Jyrpc.model.RpcRequest;
import com.jiangying.Jyrpc.model.RpcResponse;
import com.jiangying.Jyrpc.serializer.Serializer;

import java.io.IOException;

public class JsonSerializer implements Serializer {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    /**
     * Json序列化
     *
     * @param obj
     * @param <T>
     * @return
     * @throws IOException
     */
    @Override
    public <T> byte[] serialize(T obj) throws IOException {
        return OBJECT_MAPPER.writeValueAsBytes(obj);
    }

    @Override
    public <T> T deserialize(byte[] data, Class<T> clazz) throws IOException {
        T obj = OBJECT_MAPPER.readValue(data, clazz);
        if (obj instanceof RpcRequest) {
            return handleRequest((RpcRequest) obj, clazz);
        }
        if (obj instanceof RpcResponse) {
            handleResponse((RpcResponse) obj, clazz);
        }
        return obj;

    }

    private <T> T handleRequest(RpcRequest rpcRequest, Class<T> type) throws IOException {
        Class<?>[] parameterTypes = rpcRequest.getParameterTypes();
        Object[] args = rpcRequest.getArgs();
        for (int i = 0; i < args.length; i++) {
            Class<?> clazz = parameterTypes[i];
            if (!clazz.isAssignableFrom(args[i].getClass())) {
                byte[] bytes = OBJECT_MAPPER.writeValueAsBytes(args[i]);
                args[i] = OBJECT_MAPPER.readValue(bytes, clazz);
            }
        }
        return type.cast(rpcRequest);
    }

    private <T> T handleResponse(RpcResponse rpcResponse, Class<T> type) throws IOException {
        byte[] bytes = OBJECT_MAPPER.writeValueAsBytes(rpcResponse.getData());
        rpcResponse.setData(OBJECT_MAPPER.readValue(bytes, rpcResponse.getDataType()));
        return type.cast(rpcResponse);
    }

}
