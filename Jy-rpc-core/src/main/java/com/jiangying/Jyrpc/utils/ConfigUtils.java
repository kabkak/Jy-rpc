package com.jiangying.Jyrpc.utils;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.lang.Dict;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import cn.hutool.setting.dialect.Props;
import cn.hutool.setting.yaml.YamlUtil;
import com.jiangying.Jyrpc.config.RpcConfig;
import com.jiangying.Jyrpc.constant.RpcConstant;
import org.yaml.snakeyaml.Yaml;

import javax.naming.Name;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Map;

public class ConfigUtils {

    /**
     * 加载配置对象
     *
     * @param tClass
     * @param prefix
     * @param <T>
     * @return
     */
    public static <T> T loadConfig(Class<T> tClass, String prefix) {
        return loadConfig(tClass, prefix, "");
    }

    /**
     * 加载配置对象，支持区分环境
     *
     * @param tClass
     * @param prefix
     * @param environment
     * @param <T>
     * @return
     */
    public static <T> T loadConfig(Class<T> tClass, String prefix, String environment) {
        T yamlConfig = null;
        T propertiesConfig = null;

        // 构建配置文件名
        StringBuilder configFileBuilder = new StringBuilder("application");
        if (StrUtil.isNotBlank(environment)) {
            configFileBuilder.append("-").append(environment);
        }
        // 尝试读取 properties 文件
        String propertiesFileName = configFileBuilder.append(".properties").toString();
        if (FileUtil.exist(propertiesFileName)) {
            Props props = new Props(propertiesFileName);
            propertiesConfig = props.toBean(tClass, prefix);

            configFileBuilder.setLength(configFileBuilder.length() - 11);
        }
        // 尝试读取 YML 文件
        String yalFileName = configFileBuilder.append(".yml").toString();
        if (FileUtil.exist(yalFileName)) {
            Dict ymlDict = YamlUtil.loadByPath(yalFileName);
            if (StrUtil.isNotBlank(prefix) && ymlDict.containsKey(prefix)) {
                Object o = ymlDict.get(prefix);
                String data = JSONUtil.toJsonStr(o);
                yamlConfig = JSONUtil.toBean(data, tClass);
                System.out.println("ymlConfig: " + yamlConfig);
            }
        } else {
            // 尝试读取 YML 文件
            configFileBuilder.setLength(configFileBuilder.length() - 4);
            String yamlFileName = configFileBuilder.append(".yaml").toString();
            Dict yamlDict = YamlUtil.loadByPath(yamlFileName);
            if (yamlDict != null) {
                if (StrUtil.isNotBlank(prefix) && yamlDict.containsKey(prefix)) {
                    Object o = yamlDict.get(prefix);
                    String data = JSONUtil.toJsonStr(o);
                    yamlConfig = JSONUtil.toBean(data, tClass);
                    System.out.println("yamlConfig: " + yamlConfig);
                }
            }
        }
        System.out.println("propertiesConfig:" + propertiesConfig);
        BeanUtil.copyProperties(yamlConfig, propertiesConfig);

        return propertiesConfig;

    }

}