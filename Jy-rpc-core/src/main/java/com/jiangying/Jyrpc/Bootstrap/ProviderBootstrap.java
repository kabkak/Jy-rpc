package com.jiangying.Jyrpc.Bootstrap;

import com.jiangying.Jyrpc.RpcApplication;
import com.jiangying.Jyrpc.config.RpcConfig;
import com.jiangying.Jyrpc.model.ServiceMetaInfo;
import com.jiangying.Jyrpc.registry.LocalRegister;
import com.jiangying.Jyrpc.registry.Register;
import com.jiangying.Jyrpc.registry.RegisterFactory;
import com.jiangying.Jyrpc.registry.ServiceRegisterInfo;
import com.jiangying.Jyrpc.server.tcp.VertxTcpServer;

import java.util.List;

public class ProviderBootstrap {
    public static void init(List<ServiceRegisterInfo<?>> serviceRegisterInfoList) {
        RpcApplication.init();
        Register register = RegisterFactory.getRegister();
        register.init();
        //服务注册
        for (ServiceRegisterInfo<?> serviceRegisterInfo : serviceRegisterInfoList) {
            Class<?> implClass = serviceRegisterInfo.getImplClass();
            String serviceName = serviceRegisterInfo.getServiceName();

            LocalRegister.register(serviceName, implClass);
            RpcConfig rpcConfig = RpcApplication.getRpcProperties();
            ServiceMetaInfo serviceMetaInfo = new ServiceMetaInfo();
            serviceMetaInfo.setServiceHost(rpcConfig.getServerHost());
            serviceMetaInfo.setServicePort(rpcConfig.getServerPort());
            serviceMetaInfo.setServiceName(serviceName);
            Runtime.getRuntime().addShutdownHook(new Thread(new Thread(register::destroy)));
            try {
                register.register(serviceMetaInfo);
            } catch (Exception e) {
                throw new RuntimeException("注册失败");
            }
        }
       //启动服务器
        new VertxTcpServer().doStart(RpcApplication.getRpcProperties().getServerPort());
    }
}
