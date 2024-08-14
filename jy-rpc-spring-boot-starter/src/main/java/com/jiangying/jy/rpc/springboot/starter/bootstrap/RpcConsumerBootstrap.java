package com.jiangying.jy.rpc.springboot.starter.bootstrap;
import com.jiangying.Jyrpc.proxy.ServiceProxyFactory;
import com.jiangying.jy.rpc.springboot.starter.Annotation.JyRpcReference;
import org.springframework.beans.factory.config.BeanPostProcessor;
import java.lang.reflect.Field;


public class RpcConsumerBootstrap implements BeanPostProcessor {
    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) {

        Class<?> beanClass = bean.getClass();
        Field[] fields = beanClass.getDeclaredFields();
        for (Field field : fields) {
            JyRpcReference jyRpcReference = field.getAnnotation(JyRpcReference.class);
            if (jyRpcReference != null) {
                Class<?> interfaceClass = jyRpcReference.interfaceClass();
                if (interfaceClass == void.class) {
                    interfaceClass = field.getType();
                }
                System.out.println("为" + interfaceClass.getName() + "注入代理对象");
                field.setAccessible(true);

                Object proxy = ServiceProxyFactory.getProxy(interfaceClass);
                try {
                    field.set(bean, proxy);
                } catch (IllegalAccessException e) {
                    System.out.println("获得代理对象失败");
                    throw new RuntimeException(e);
                } finally {
                    field.setAccessible(false);
                }

            }
        }


        return bean;
    }
}
