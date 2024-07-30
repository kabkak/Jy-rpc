package com.jiangying.provider;

import com.jiangying.Jyrpc.RpcApplication;
import com.jiangying.Jyrpc.config.RpcConfig;
import com.jiangying.Jyrpc.model.ServiceMetaInfo;
import com.jiangying.Jyrpc.registry.LocalRegister;
import com.jiangying.Jyrpc.registry.Register;
import com.jiangying.Jyrpc.registry.RegisterFactory;
import com.jiangying.Jyrpc.server.HttpServer;
import com.jiangying.Jyrpc.server.Impl.VertxHttpServer;
import com.jiangying.Jyrpc.utils.TimeGetUtil;
import com.jiangying.service.UserService;


public class EasyProvider {
    /**
     * 启动服务器提供服务
     *
     * @param args
     */
    public static void main(String[] args) {
        RpcApplication.init();
        Register();
        LocalRegister.register(UserService.class.getName(), UserServiceImpl.class);

        System.out.println(TimeGetUtil.getTime());

        HttpServer httpServer = new VertxHttpServer();

        httpServer.doStart(RpcApplication.getRpcProperties().getServerPort());

    }

    private static void Register() {
        Register register = RegisterFactory.getRegister();
        register.init();

        RpcConfig rpcConfig = RpcApplication.getRpcProperties();
        ServiceMetaInfo serviceMetaInfo = new ServiceMetaInfo();
        serviceMetaInfo.setServiceHost(rpcConfig.getServerHost());
        serviceMetaInfo.setServicePort(rpcConfig.getServerPort());
        serviceMetaInfo.setServiceName(UserService.class.getName());
        Runtime.getRuntime().addShutdownHook(new Thread(new Thread(register::destroy)));
        try {
            register.register(serviceMetaInfo);
        } catch (Exception e) {
            throw new RuntimeException("注册失败");
        }
    }
}
