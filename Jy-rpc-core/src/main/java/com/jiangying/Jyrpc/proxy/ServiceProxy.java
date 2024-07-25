package com.jiangying.Jyrpc.proxy;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.jiangying.Jyrpc.config.RpcApplication;
import com.jiangying.Jyrpc.config.RpcConfig;
import com.jiangying.Jyrpc.model.RpcRequest;
import com.jiangying.Jyrpc.model.RpcResponse;
import com.jiangying.Jyrpc.serializer.Impl.JdkSerializer;
import com.jiangying.Jyrpc.serializer.Serializer;
import com.jiangying.Jyrpc.serializer.SerializerFactory;


import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * @author jiangying
 */
public class ServiceProxy implements InvocationHandler {

    String serverHost;
    Integer port;


    /**
     * 调用代理对象的方法。
     *
     * @param proxy  代理对象
     * @param method 被调用的方法
     * @param args   方法参数
     * @return 方法返回值
     * @throws Throwable 方法执行过程中抛出的异常
     */
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        // 使用JdkSerializer作为序列化工具
        Serializer serializer = SerializerFactory.getSerializer();
        System.out.println(serializer);
        // 构建RpcRequest对象，包含服务名、方法名、参数类型和参数值
        RpcRequest rpcRequest = RpcRequest.builder()
                .serviceName(method.getDeclaringClass().getName())
                .methodName(method.getName())
                .parameterTypes(method.getParameterTypes())
                .args(args)
                .build();

        // 对RpcRequest进行序列化
        byte[] serialized = serializer.serialize(rpcRequest);

        RpcConfig rpcProperties = RpcApplication.getRpcProperties();
        serverHost = rpcProperties.getServerHost();
        port = rpcProperties.getServerPort();
        serverHost = (serverHost == null ? "localhost" : serverHost);
        port = (port == null ? 8080 : port);
        try (HttpResponse httpResponse = HttpRequest.post("Http://" + serverHost + ":" + port).body(serialized).execute();) {
            // 从HTTP响应中获取结果数据
            byte[] result = httpResponse.bodyBytes();
            // 对结果数据进行反序列化，得到RpcResponse对象
            RpcResponse rpcResponse = serializer.deserialize(result, RpcResponse.class);
            // 返回RpcResponse中的数据部分
            return rpcResponse.getData();
        } catch (IOException e) {
            // 打印IO异常的堆栈跟踪
            e.printStackTrace();
        }

        // 如果发生异常，返回null
        return null;
    }

}
