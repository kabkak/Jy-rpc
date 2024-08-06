package com.jiangying.jy.rpc.springboot.starter.bootstrap;

import com.jiangying.Jyrpc.RpcApplication;
import com.jiangying.Jyrpc.server.tcp.VertxTcpClient;
import com.jiangying.Jyrpc.server.tcp.VertxTcpServer;
import com.jiangying.jy.rpc.springboot.starter.Annotation.EnableJyRpc;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;

/**
 * @author jiangying
 * ioc容器初始化
 */
public class RpcInitBootStrap implements ImportBeanDefinitionRegistrar {
    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        //读取配置文件
        RpcApplication.init();
        boolean needServer = (boolean) importingClassMetadata.getAnnotationAttributes(EnableJyRpc.class.getName()).get("needServer");
        if (needServer) {
            VertxTcpServer vertxTcpServer = new VertxTcpServer();
            vertxTcpServer.doStart(RpcApplication.getRpcProperties().getServerPort());
        } else {
            System.out.println("no need server");
        }
    }
}
