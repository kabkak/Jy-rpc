package com.jiangying.Jyrpc.proxy;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.jiangying.Jyrpc.RpcApplication;
import com.jiangying.Jyrpc.config.RegistryConfig;
import com.jiangying.Jyrpc.config.RpcConfig;
import com.jiangying.Jyrpc.constant.TolerantStrategyConstant;
import com.jiangying.Jyrpc.fault.retry.RetryStrategy;
import com.jiangying.Jyrpc.fault.retry.RetryStrategyFactory;
import com.jiangying.Jyrpc.fault.tolerant.TolerantStrategy;
import com.jiangying.Jyrpc.fault.tolerant.TolerantStrategyFactory;
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
import java.util.Map;

/**
 * @author jiangying
 */
public class ServiceProxy implements InvocationHandler {

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

        // 得到序列化器
        Serializer serializer = SerializerFactory.getSerializer();
        System.out.println(serializer);

        // 构建RpcRequest对象，包含服务名、方法名、参数类型和参数值
        RpcRequest rpcRequest = RpcRequest.builder()
                .serviceName(method.getDeclaringClass().getName())
                .methodName(method.getName())
                .parameterTypes(method.getParameterTypes())
                .args(args).build();
        //获得rpc配置信息
        RpcConfig rpcConfig = RpcApplication.getRpcProperties();

        // 从注册中心获取服务元信息列表
        Register register = RegisterFactory.getRegister();
        register.init();
        String key = rpcRequest.getServiceName() + ":" + rpcConfig.getVersion();
        List<ServiceMetaInfo> serviceMetaInfos = register.serviceDiscovery(key);

        //负载均衡
        LoadBalancer loadBalancer = LoadBalancerFactory.getLoadBalancer();
        HashMap<String, Object> methodNameHashMap = new HashMap<>();
        methodNameHashMap.put("methodName", rpcRequest.getServiceName());
        ServiceMetaInfo serviceMetaInfo = loadBalancer.select(methodNameHashMap, serviceMetaInfos);
        RetryStrategy retryStrategy = RetryStrategyFactory.getRetryStrategy();
        System.out.println(serviceMetaInfo);



        try {
            //重试
            RpcResponse rpcResponse = retryStrategy.retry(
                    () -> VertxTcpClient.doRequest(rpcRequest, serviceMetaInfo)
            );

            return rpcResponse.getData();
        } catch (Exception e) {
            //容错
            Map<String, Object> map = new HashMap<>();
            map.put(TolerantStrategyConstant.RPC_REQUEST, rpcRequest);
            map.put(TolerantStrategyConstant.CURRENT_SERVICE, serviceMetaInfo);
            map.put(TolerantStrategyConstant.SERVICE_LIST, serviceMetaInfos);
            TolerantStrategy tolerantStrategy = TolerantStrategyFactory.getTolerantStrategy();

            return tolerantStrategy.doTolerant(map, e);

        }
    }


}
