package com.jiangying.provider;

import com.jiangying.Jyrpc.Bootstrap.ProviderBootstrap;
import com.jiangying.Jyrpc.RpcApplication;
import com.jiangying.Jyrpc.config.RpcConfig;
import com.jiangying.Jyrpc.model.ServiceMetaInfo;
import com.jiangying.Jyrpc.registry.LocalRegister;
import com.jiangying.Jyrpc.registry.Register;
import com.jiangying.Jyrpc.registry.RegisterFactory;
import com.jiangying.Jyrpc.registry.ServiceRegisterInfo;
import com.jiangying.Jyrpc.server.tcp.VertxTcpServer;
import com.jiangying.Jyrpc.utils.TimeGetUtil;
import com.jiangying.service.UserService;

import java.util.ArrayList;
import java.util.List;


public class EasyProvider {
    /**
     * 启动服务器提供服务
     *
     * @param args
     */
    public static void main(String[] args) {
        List<ServiceRegisterInfo<?>> serviceRegisterInfoList = new ArrayList<>();
        ServiceRegisterInfo<?> serviceRegisterInfo = new ServiceRegisterInfo<>(UserService.class.getName(), UserServiceImplUndo.class);
        serviceRegisterInfoList.add(serviceRegisterInfo);

        ProviderBootstrap.init(serviceRegisterInfoList);

        System.out.println(TimeGetUtil.getTime());

    }

    private static void Register(String serviceName, Class<?> implClass) {
        RpcApplication.init();
        Register register = RegisterFactory.getRegister();
        register.init();
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
        new VertxTcpServer().doStart(RpcApplication.getRpcProperties().getServerPort());
    }
}
