package com.jiangying.Jyrpc.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * @author jiangying
 */
public class MockProxy implements InvocationHandler {


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
        Class<?> returnType = method.getReturnType();
        if (returnType == int.class) return 1;
        if (returnType == String.class) return "hello";
        if (returnType == double.class) return 1.0;
        if (returnType == boolean.class) return true;
        return null;
    }

}
