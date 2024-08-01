package com.jiangying.Jyrpc.proxy;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.jiangying.Jyrpc.RpcApplication;
import com.jiangying.Jyrpc.config.RegistryConfig;
import com.jiangying.Jyrpc.config.RpcConfig;
import com.jiangying.Jyrpc.fault.retry.RetryStrategy;
import com.jiangying.Jyrpc.fault.retry.RetryStrategyFactory;
import com.jiangying.Jyrpc.loadbalancer.LoadBalancer;
import com.jiangying.Jyrpc.loadbalancer.LoadBalancerFactory;
import com.jiangying.Jyrpc.model.RpcRequest;
import com.jiangying.Jyrpc.model.RpcResponse;
import com.jiangying.Jyrpc.model.ServiceMetaInfo;
import com.jiangying.Jyrpc.registry.Register;
import com.jiangying.Jyrpc.registry.RegisterFactory;
import com.jiangying.Jyrpc.serializer.Serializer;
import com.jiangying.Jyrpc.serializer.SerializerFactory;
import com.jiangying.Jyrpc.server.tcp.VertxTcpClient;


import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;

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
                .args(args).build();
        RpcConfig rpcConfig = RpcApplication.getRpcProperties();
        Register register = RegisterFactory.getRegister();
        register.init();
        // 从注册中心获取服务元信息列表
        String key = rpcRequest.getServiceName() + ":" + rpcConfig.getVersion();
        List<ServiceMetaInfo> serviceMetaInfos = register.serviceDiscovery(key);

        LoadBalancer loadBalancer = LoadBalancerFactory.getLoadBalancer();
        //todo 负载均衡
        try {
            new HashMap<>().put("methodName", rpcRequest.getServiceName());
            ServiceMetaInfo serviceMetaInfo = loadBalancer.select(new HashMap<>(), serviceMetaInfos);
            RetryStrategy retryStrategy = RetryStrategyFactory.getRetryStrategy();
            System.out.println(serviceMetaInfo);
            RpcResponse rpcResponse = retryStrategy.retry(() -> VertxTcpClient.doRequest(rpcRequest, serviceMetaInfo));
            //RpcResponse rpcResponse = VertxTcpClient.doRequest(rpcRequest, serviceMetaInfo);

            return rpcResponse.getData();
        } catch (Exception e) {
            e.printStackTrace();
            // 如果发生异常，返回null
            return null;
        }
    }


}
