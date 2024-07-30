package com.jiangying.Jyrpc.proxy;

import com.jiangying.Jyrpc.RpcApplication;
import com.jiangying.Jyrpc.utils.TimeGetUtil;

import java.lang.reflect.Proxy;

/**
 * 获得代理对象
 */
public class ServiceProxyFactory {


    public static <T> T getProxy(Class<T> interfaceClass) {
        RpcApplication.init();
        System.out.println("获得代理对象"+TimeGetUtil.getTime());
        if (RpcApplication.getRpcProperties().isMock()) {
            return getMockProxy(interfaceClass);

        } else {
            return getServiceProxy(interfaceClass);
        }
    }

    /**
     * 创建一个代理对象，该对象实现了指定的接口。
     * <p>
     * 使用Java的动态代理机制，动态地生成一个实现了指定接口的代理类实例。
     * 这个代理类在运行时创建，它可以拦截对原始接口方法的调用，并在调用前后执行额外的逻辑。
     * 这种机制常用于实现AOP（面向切面编程）中的切面逻辑，或者对现有接口的透明封装。
     *
     * @param interfaceClass 指定的接口类，代理对象将实现这个接口。
     * @param <T>            泛型参数，指定接口的类型。
     * @return 返回一个实现了指定接口的代理对象。
     */
    public static <T> T getServiceProxy(Class<T> interfaceClass) {

        // 使用Proxy.newProxyInstance创建一个代理实例。
        return (T) Proxy.newProxyInstance(
                // 第一个参数是接口类的类加载器，确保代理类和被代理接口使用相同的类加载器。
                interfaceClass.getClassLoader(),
                // 第二个参数是一个类数组，包含需要被代理的接口，这里只有一个接口。
                new Class[]{interfaceClass},
                // 第三个参数是一个InvocationHandler实现类，它定义了代理对象在方法被调用时的行为。
                new ServiceProxy());
    }

    public static <T> T getMockProxy(Class<T> interfaceClass) {

        // 使用Proxy.newProxyInstance创建一个代理实例。
        return (T) Proxy.newProxyInstance(
                // 第一个参数是接口类的类加载器，确保代理类和被代理接口使用相同的类加载器。
                interfaceClass.getClassLoader(),
                // 第二个参数是一个类数组，包含需要被代理的接口，这里只有一个接口。
                new Class[]{interfaceClass},
                // 第三个参数是一个InvocationHandler实现类，它定义了代理对象在方法被调用时的行为。
                new MockProxy());
    }


}
