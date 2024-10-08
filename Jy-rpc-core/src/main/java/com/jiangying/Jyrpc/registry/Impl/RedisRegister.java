package com.jiangying.Jyrpc.registry.Impl;

import cn.hutool.cron.CronUtil;
import cn.hutool.cron.task.Task;
import cn.hutool.json.JSONUtil;
import com.jiangying.Jyrpc.RpcApplication;
import com.jiangying.Jyrpc.config.RegistryConfig;
import com.jiangying.Jyrpc.model.ServiceMetaInfo;
import com.jiangying.Jyrpc.registry.Register;
import com.jiangying.Jyrpc.registry.RegistryServiceCache;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPubSub;

import java.util.*;
import java.util.stream.Collectors;

public class RedisRegister implements Register {
    private Jedis jedis;

    private Jedis subscribeJedis;

    private String ROOT_PATH = "/rpc";

    /**
     * 本机注册的节点 key 集合（用于维护续期）
     */
    private final Set<Map<String, String>> localNodeKeySet = new HashSet<>();

    /**
     * 注册中心服务缓存
     */
    private final RegistryServiceCache registryServiceCache = new RegistryServiceCache();


    @Override
    public void init() {

        RegistryConfig registryConfig = RpcApplication.getRpcProperties().getRegistryConfig();
        ROOT_PATH = ROOT_PATH + "/" + registryConfig.getRegistry();
        //rpc/redis
        Long timeout = registryConfig.getTimeout();
        jedis = new Jedis(registryConfig.getAddress().split(":")[0],
                Integer.parseInt(registryConfig.getAddress().split(":")[1]),
                Math.toIntExact(timeout)
        );
        subscribeJedis = new Jedis(registryConfig.getAddress().split(":")[0],
                Integer.parseInt(registryConfig.getAddress().split(":")[1]),
                Math.toIntExact(timeout)
        );
        if (registryConfig.getPassword() != null) {
            jedis.auth(registryConfig.getPassword());
            subscribeJedis.auth(registryConfig.getPassword());
        }

        jedis.select(0);
        subscribeJedis.select(0);
        //ROOT_PATH = ROOT_PATH + "/" + registryConfig.getRegistry();

        heartBeat();

    }

    @Override
    public void register(ServiceMetaInfo serviceMetaInfo) throws Exception {
        if (serviceMetaInfo == null) {
            throw new IllegalArgumentException("serviceMetaInfo is null");
        }
        //rpc/redis/com.jiangying.Jyrpc.service.UserService/1.0.0
        String key = ROOT_PATH + "/" + serviceMetaInfo.getServiceKey();

        localNodeKeySet.add(new HashMap<String, String>() {{
            put(key, serviceMetaInfo.getServiceNodeKey());
        }});
        String serviceChangeEvent = "Service 'MyRpcService' is now online";

        jedis.publish(key, "UPDATEORDELETE");

        jedis.hset(key, serviceMetaInfo.getServiceNodeKey(), JSONUtil.toJsonStr(serviceMetaInfo));
        jedis.expire(key, 30);


    }

    @Override
    public void unRegister(ServiceMetaInfo serviceMetaInfo) {
        String key = ROOT_PATH + "/" + serviceMetaInfo.getServiceKey();
        jedis.publish(key, "UPDATEORDELETE");
        localNodeKeySet.removeIf(map -> map.get(key).equals(serviceMetaInfo.getServiceNodeKey()));
        jedis.hdel(key, serviceMetaInfo.getServiceNodeKey());
    }

    @Override
    public List<ServiceMetaInfo> serviceDiscovery(String serviceKey) {
        List<ServiceMetaInfo> cachedServiceMetaInfoList = registryServiceCache.readCache();
        if (cachedServiceMetaInfoList != null) {
            return cachedServiceMetaInfoList;
        }
        cachedServiceMetaInfoList = new ArrayList<>();
        Set<String> stringSet = jedis.hkeys(ROOT_PATH + "/" + serviceKey);
        List<ServiceMetaInfo> serviceMetaInfoList = new ArrayList<>();
        if (stringSet != null && stringSet.size() > 0) {
            serviceMetaInfoList.addAll(stringSet.stream().map(key -> {
                String value = jedis.hget(ROOT_PATH + "/" + serviceKey, key);
                return JSONUtil.toBean(value, ServiceMetaInfo.class);
            }).collect(Collectors.toList()));
        }
        registryServiceCache.writeCache(serviceMetaInfoList);
        List<String> watchKey = serviceMetaInfoList.stream().map(ServiceMetaInfo::getServiceKey).distinct().collect(Collectors.toList());
        for (String key : watchKey) {
            watch(key);
        }
        cachedServiceMetaInfoList.addAll(serviceMetaInfoList);
        return serviceMetaInfoList;
    }

    @Override
    public void heartBeat() {
        //每10秒执行一次
        CronUtil.schedule("*/10 * * * * *", new Task() {
            @Override
            public void execute() {
                localNodeKeySet.forEach(map -> {
                    String key = map.keySet().iterator().next();
                    String nodeKey = map.get(key);
                    String hget = jedis.hget(key, nodeKey);
                    if (hget == null) {
                        localNodeKeySet.removeIf(m -> m.get(key).equals(nodeKey));
                        registryServiceCache.clearCache();
                        return;
                    }
                    jedis.expire(key, 30);
                });

            }
        });
        CronUtil.setMatchSecond(true);
        CronUtil.start();


    }

    @Override
    public void watch(String serviceKey) {
        String watchKey = ROOT_PATH + "/" + serviceKey;

        JedisPubSub jedisPubSub = new JedisPubSub() {
            @Override
            public void onMessage(String channel, String message) {
                System.out.println("监听到" + channel + ": " + "发生了变化");
                if ("UPDATEORDELETE".equals(message)) {
                    registryServiceCache.clearCache();
                }
            }
            @Override
            public void onSubscribe(String channel, int subscribedChannels) {
                System.out.println("Subscribed to channel " + channel);
            }

            @Override
            public void onUnsubscribe(String channel, int subscribedChannels) {
                System.out.println("Unsubscribed from channel " + channel);
            }
        };
        // 在新线程中订阅频道
        new Thread(() -> subscribeJedis.subscribe(jedisPubSub, watchKey)).start();


    }

    @Override
    public void destroy() {
        //删除本地注册的节点
        localNodeKeySet.forEach(map -> {
            String key = map.keySet().iterator().next();
            String nodeKey = map.get(key);
            jedis.hdel(key, nodeKey);
            jedis.publish(key, "UPDATEORDELETE");
        });
        jedis.close();
       // CronUtil.stop();
    }
}
