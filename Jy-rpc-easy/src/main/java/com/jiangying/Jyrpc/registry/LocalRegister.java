package com.jiangying.Jyrpc.registry;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class LocalRegister {
    /**
     * 注册信息存储
     */
    private static final Map<String,Class<?>> map = new ConcurrentHashMap<>();

    /**
     * 注册服务
     * @param interfaceName
     * @param implClass
     */
    public static void register(String interfaceName,Class<?> implClass)
    {
        map.put(interfaceName, implClass);
    }

    /**
     * 获取服务
     * @param interfaceName
     * @return
     */
    public static Class<?> get(String interfaceName)
    {
        return map.get(interfaceName);
    }
    /**
     * 删除服务
     */
    public static void remove(String interfaceName)
    {
        map.remove(interfaceName);
    }

}
