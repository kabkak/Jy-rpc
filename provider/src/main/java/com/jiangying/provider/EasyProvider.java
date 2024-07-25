package com.jiangying.provider;

import com.jiangying.Jyrpc.config.RpcApplication;
import com.jiangying.Jyrpc.config.RpcConfig;
import com.jiangying.Jyrpc.registry.LocalRegister;
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

        LocalRegister.register(UserService.class.getName(), UserServiceImpl.class);
        RpcApplication.init();
        System.out.println(TimeGetUtil.getTime());


        HttpServer httpServer = new VertxHttpServer();

        httpServer.doStart(RpcApplication.getRpcProperties().getServerPort());

    }
}
