package com.jiangying.Jyrpc.server;

import com.jiangying.Jyrpc.config.RpcApplication;
import com.jiangying.Jyrpc.model.RpcRequest;
import com.jiangying.Jyrpc.model.RpcResponse;
import com.jiangying.Jyrpc.registry.LocalRegister;
import com.jiangying.Jyrpc.serializer.Impl.JdkSerializer;
import com.jiangying.Jyrpc.serializer.Serializer;
import com.jiangying.Jyrpc.serializer.SerializerFactory;
import io.vertx.core.Handler;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpServerRequest;
import java.io.IOException;
import java.lang.reflect.Method;

public class HttpServiceHandler implements Handler<HttpServerRequest> {
    @Override
    public void handle(HttpServerRequest httpServerRequest) {

        //获得序列化器
        Serializer serializer = SerializerFactory.getSerializer();
        //记录日记
        System.out.println( httpServerRequest.method() + "  " + httpServerRequest.uri());

        //异步处理HTTP请求
        httpServerRequest.bodyHandler(body -> {
            //反序列化
            RpcRequest rpcRequest = null;
            try {
                rpcRequest = serializer.deserialize(body.getBytes(), RpcRequest.class);

            } catch (IOException e) {
                e.printStackTrace();
            }
            //创建RpcResponse
            RpcResponse rpcResponse = new RpcResponse();
            //判断rpcRequest为空 则返回空响应
            if (rpcRequest == null) {
                rpcResponse.setMessage("rpcRequest is null");
                //序列化响应
                doResponse(httpServerRequest, rpcResponse, serializer);
                return;
            }

            try {
                //获得代理对象
                Class<?> implClass = LocalRegister.get(rpcRequest.getServiceName());
                //反射获得方法

                Method method = implClass.getMethod(rpcRequest.getMethodName(), rpcRequest.getParameterTypes());
                //反射调用方法
                Object result = method.invoke(implClass.newInstance(), rpcRequest.getArgs());

                rpcResponse.setData(result);
                rpcResponse.setDataType(result.getClass());
                rpcResponse.setMessage("success");
            } catch (Exception e) {
                e.printStackTrace();
                rpcResponse.setMessage(e.getMessage());
                rpcResponse.setException(e);

            }
            doResponse(httpServerRequest, rpcResponse, serializer);
        });

    }

    private void doResponse(HttpServerRequest httpServerRequest, RpcResponse rpcResponse, Serializer serializer) {

        httpServerRequest.response().putHeader("Content-Type", "application/json");
        try {
            byte[] serialized = serializer.serialize(rpcResponse);
            httpServerRequest.response().end(Buffer.buffer(serialized));
        } catch (IOException e) {
            e.printStackTrace();
            httpServerRequest.response().end(Buffer.buffer("serializer error"));
        }


    }
}
