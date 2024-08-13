package com.jiangying.jy.rpc.springboot.starter.bootstrap;

import com.jiangying.Jyrpc.RpcApplication;
import com.jiangying.Jyrpc.config.RpcConfig;
import com.jiangying.Jyrpc.model.ServiceMetaInfo;
import com.jiangying.Jyrpc.registry.LocalRegister;
import com.jiangying.Jyrpc.registry.Register;
import com.jiangying.Jyrpc.registry.RegisterFactory;
import com.jiangying.jy.rpc.springboot.starter.Annotation.JyRpcService;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;

public class RpcProviderBootstrap implements BeanPostProcessor {
    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        Class<?> beanClass = bean.getClass();
        JyRpcService JyRpcService = beanClass.getAnnotation(JyRpcService.class);

        if (JyRpcService != null) {
            Class<?> aClass = JyRpcService.interfaceClass();
            if (aClass == void.class) {
                aClass = beanClass.getInterfaces()[0];
            }
            LocalRegister.register(aClass.getName(), beanClass);
            System.out.println("register service:" + aClass.getName());
            RpcConfig rpcConfig = RpcApplication.getRpcProperties();

            ServiceMetaInfo serviceMetaInfo = new ServiceMetaInfo();
            serviceMetaInfo.setServiceName(aClass.getName());
            serviceMetaInfo.setServiceHost(rpcConfig.getServerHost());
            serviceMetaInfo.setServicePort(rpcConfig.getServerPort());
            serviceMetaInfo.setServiceVersion(rpcConfig.getVersion());
            final Register register = RegisterFactory.getRegister();
            register.init();
            //register::destroy
            Runtime.getRuntime().addShutdownHook(new Thread(new Thread(new Runnable() {
                @Override
                public void run() {
                    register.destroy();
                }
            }
            )));
            try {
                register.register(serviceMetaInfo);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        return bean;
    }

}
