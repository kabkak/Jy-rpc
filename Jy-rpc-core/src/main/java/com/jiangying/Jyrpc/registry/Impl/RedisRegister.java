package com.jiangying.Jyrpc.registry.Impl;

import cn.hutool.json.JSONUtil;
import com.jiangying.Jyrpc.RpcApplication;
import com.jiangying.Jyrpc.config.RegistryConfig;
import com.jiangying.Jyrpc.model.ServiceMetaInfo;
import com.jiangying.Jyrpc.registry.Register;
import com.jiangying.Jyrpc.registry.RegistryServiceCache;
import redis.clients.jedis.Jedis;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class RedisRegister implements Register {
    private Jedis jedis;

    private String ROOT_PATH = "/rpc";

    /**
     * 本机注册的节点 key 集合（用于维护续期）
     */
    private final Set<String> localRegisterNodeKeySet = new HashSet<>();

    /**
     * 注册中心服务缓存
     */
    private final RegistryServiceCache registryServiceCache = new RegistryServiceCache();

    @Override
    public void init() {
        RegistryConfig registryConfig = RpcApplication.getRpcProperties().getRegistryConfig();
        ROOT_PATH = ROOT_PATH + "/" + registryConfig.getRegistry();
        Long timeout = registryConfig.getTimeout();
        jedis = new Jedis(registryConfig.getAddress().split(":")[0],
                Integer.parseInt(registryConfig.getAddress().split(":")[1]),
                Math.toIntExact(timeout)
        );
        if (registryConfig.getPassword() != null) {
            jedis.auth(registryConfig.getPassword());
        }

        jedis.select(0);
        ROOT_PATH = ROOT_PATH + "/" + registryConfig.getRegistry();

    }

    @Override
    public void register(ServiceMetaInfo serviceMetaInfo) throws Exception {
        String key = ROOT_PATH + "/" + serviceMetaInfo.getServiceKey();

        jedis.hset(key, serviceMetaInfo.getServiceNodeKey(), JSONUtil.toJsonStr(serviceMetaInfo));
        jedis.expire(key, 30);


    }

    @Override
    public void unRegister(ServiceMetaInfo serviceMetaInfo) {
        String key = ROOT_PATH + "/" + serviceMetaInfo.getServiceKey();


        jedis.hdel(key, serviceMetaInfo.getServiceNodeKey());
    }

    @Override
    public List<ServiceMetaInfo> serviceDiscovery(String serviceKey) {

        Set<String> stringSet = jedis.hkeys(ROOT_PATH + "/" + serviceKey);
        List<ServiceMetaInfo> serviceMetaInfoList = new ArrayList<>();
        if (stringSet != null && stringSet.size() > 0) {
            serviceMetaInfoList.addAll(stringSet.stream().map(key -> {
                String value = jedis.hget(ROOT_PATH + "/" + serviceKey, key);
                return JSONUtil.toBean(value, ServiceMetaInfo.class);
            }).collect(Collectors.toList()));
        }
        return serviceMetaInfoList;
    }

    @Override
    public void heartBeat() {


    }

    @Override
    public void watch(String serviceNodeKey) {

    }

    @Override
    public void destroy() {

    }
}
