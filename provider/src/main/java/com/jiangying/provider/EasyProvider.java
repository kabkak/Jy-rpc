package com.jiangying.provider;

import com.jiangying.Jyrpc.registry.LocalRegister;
import com.jiangying.Jyrpc.server.HttpServer;
import com.jiangying.Jyrpc.server.Impl.VertxHttpServer;



public class EasyProvider {
    /**
     * 简答提供服务
     *
     * @param args
     */
    public static void main(String[] args) {
        LocalRegister.register("UserService", UserServiceImpl.class);

        //todo 提供服务
        HttpServer httpServer = new VertxHttpServer();
        httpServer.doStart(8080);

    }
}
