package com.jiangying.Jyrpc.utils;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.lang.Dict;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import cn.hutool.setting.dialect.Props;
import cn.hutool.setting.yaml.YamlUtil;
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
        T result = null;
        // 构建配置文件名
        StringBuilder configFileBuilder = new StringBuilder("application");
        if (StrUtil.isNotBlank(environment)) {
            configFileBuilder.append("-").append(environment);
        }

        // 尝试读取 YAML 文件
        String yamlFileName = configFileBuilder.append(".yml").toString();
//      InputStream yamlStream = FileUtil.getInputStream(yamlFileName);
//        if (yamlStream != null) {
//            try (InputStreamReader reader = new InputStreamReader(yamlStream, StandardCharsets.UTF_8)) {
//                // 使用 Hutool 的 YamlUtil 从 Reader 中解析 YAML 数据
//                Dict yamlData = YamlUtil.load(reader);
//                Object o = null;
//                // 处理前缀
//                if (StrUtil.isNotBlank(prefix) && yamlData.containsKey(prefix)) {
//                     o = yamlData.get(prefix);
//                }
//                // 将处理后的 YAML 数据转换为指定的 Bean 类
//                String jsonData = JSONUtil.toJsonStr(o); // 将 YAML 数据转换为 JSON 字符串
//                return JSONUtil.toBean(jsonData, tClass); // 将 JSON 字符串转换为所需的类
//            } catch (Exception e) {
//                e.printStackTrace();
//                // 适当地处理异常（例如，记录错误，重新抛出异常等）
//            }

//        }

        Dict dict = YamlUtil.loadByPath(yamlFileName);
        if (dict != null) {
            if (StrUtil.isNotBlank(prefix) && dict.containsKey(prefix)) {
                Object o = dict.get(prefix);
                String data = JSONUtil.toJsonStr(o);
                return JSONUtil.toBean(data, tClass);
            }
        }

        configFileBuilder.setLength(configFileBuilder.length() - 4);
        String propertiesFileName = configFileBuilder.append(".properties").toString();

        Props props = new Props(propertiesFileName);

        return props.toBean(tClass, prefix);


    }

}