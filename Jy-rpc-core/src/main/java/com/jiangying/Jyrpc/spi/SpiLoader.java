package com.jiangying.Jyrpc.spi;

import cn.hutool.core.io.resource.ResourceUtil;
import cn.hutool.core.util.StrUtil;
import com.jiangying.Jyrpc.registry.Register;
import com.jiangying.Jyrpc.serializer.Serializer;
import lombok.extern.slf4j.Slf4j;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


@Slf4j
public class SpiLoader {

    /**
     * 系统 SPI 目录
     */
    private static final String RPC_SYSTEM_SPI_DIR = "META-INF/rpc/system/";

    /**
     * 用户自定义 SPI 目录
     */
    private static final String RPC_CUSTOM_SPI_DIR = "META-INF/rpc/custom/";

    /**
     * 扫描路径(先系统在用户,保证用户配置优先)
     */
    private static final String[] SCAN_DIRS = new String[]{RPC_SYSTEM_SPI_DIR, RPC_CUSTOM_SPI_DIR};

    /**
     * 需要动态加载的接口
     */
    private static final List<Class<?>> CLASS_LIST = Arrays.asList(Serializer.class, Register.class);

    /**
     * 保存接口的实现类:  接口名 -> (key, 实现类)
     */
    private static final Map<String, Map<String, Class<?>>> loaderMap = new ConcurrentHashMap<>();

    /**
     * 保存所有接口的单个实现类实例: 接口名-> 实现类对象
     */
    private static final Map<String, Object> instanceMap = new ConcurrentHashMap<>();

    /**
     * 加载单个接口所有的实现类
     */
    public static Map<String, Class<?>> load(Class<?> clazz) {
        log.info("加载 SPI: {}", clazz.getName());

        Map<String, Class<?>> keyClassMap = new HashMap<>();
        //读取文件在 RPC_SYSTEM_SPI_DIR 目录下的
        for (String dir : SCAN_DIRS) {

            String fileName = dir + clazz.getName();
            List<URL> resource = ResourceUtil.getResources(fileName);

            for (URL url : resource) {

                try (InputStreamReader inputStreamReader = new InputStreamReader(url.openStream());
                     BufferedReader reader = new BufferedReader(inputStreamReader)) {
                    String line;
                    //为BufferedReader在读取一个完全空白的文件时，会认为文件的第一行是空字符串（""），而不是null。

                    while ((line = reader.readLine()) != null) {
                        String trimmedLine = line.trim();
                        //|| trimmedLine.charAt(0) == '#'
                        if (trimmedLine.isBlank()) {
                            continue;
                        }
                        String[] split = trimmedLine.split("=");
                        keyClassMap.put(split[0], java.lang.Class.forName(split[1]));
                    }
                } catch (Exception e) {

                    e.printStackTrace();
                    System.out.println("加载 SPI 失败: " + clazz.getName());
                }
            }
        }
        loaderMap.put(clazz.getName(), keyClassMap);
        return keyClassMap;
    }

    public static <T> T getInstance(String key, Class<?> clazz) {
        Map<String, Class<?>> stringClassMap = loaderMap.get(clazz.getName());
        if (!stringClassMap.containsKey(key)) {
            throw new RuntimeException("SPI 不存在: " + clazz.getName() + " key: " + key);
        }
        Class<?> aClass = stringClassMap.get(key);

        if (!instanceMap.containsKey(key)) {
            try {
                Object object = aClass.newInstance();
                instanceMap.put(key, object);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        return (T) instanceMap.get(key);

    }

    /**
     * 加载所有类型
     */
    public static void loadAll() {
        log.info("加载所有 SPI");
        for (Class<?> aClass : CLASS_LIST) {
            load(aClass);
        }
    }


}
