package com.jiangying.Jyrpc.registry.Impl;


import cn.hutool.core.collection.ConcurrentHashSet;
import cn.hutool.json.JSONUtil;
import com.jiangying.Jyrpc.RpcApplication;
import com.jiangying.Jyrpc.config.RegistryConfig;
import com.jiangying.Jyrpc.model.ServiceMetaInfo;
import com.jiangying.Jyrpc.registry.Register;
import com.jiangying.Jyrpc.registry.RegistryServiceCache;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.CuratorCache;
import org.apache.curator.framework.recipes.cache.CuratorCacheListener;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;


public class ZookeeperRegister implements Register {
    private String ROOT_PATH = "/rpc";
    private CuratorFramework client;

    /**
     * 服务缓存map
     */
    private final Map<String,List<ServiceMetaInfo>> registryServiceCacheMap = new ConcurrentHashMap<>();
    /**
     * 本机注册的节点 key 集合（用于维护续期）
     */
    private final Set<String> localRegisterNodeKeySet = new HashSet<>();

    /**
     * 注册中心服务缓存
     */
    private final RegistryServiceCache registryServiceCache = new RegistryServiceCache();

    /**
     * 正在监听的 key 集合
     */
    private final Set<String> watchingKeySet = new ConcurrentHashSet<>();

    @Override
    public void init() {
        RegistryConfig registryConfig = RpcApplication.getRpcProperties().getRegistryConfig();
        client = CuratorFrameworkFactory
                .builder()
                .connectString(registryConfig.getAddress())
                .retryPolicy(new ExponentialBackoffRetry(Math.toIntExact(registryConfig.getTimeout()), 3))
                .build();
        ROOT_PATH = ROOT_PATH + "/" + registryConfig.getRegistry();
        client.start();


    }

    @Override
    public void register(ServiceMetaInfo serviceMetaInfo) throws Exception {
        String registerKey = ROOT_PATH + "/" + serviceMetaInfo.getServiceNodeKey();

        // 注册到 zk 里
        client.create().creatingParentContainersIfNeeded()
                .withMode(CreateMode.EPHEMERAL)
                .forPath(registerKey, JSONUtil.toJsonStr(serviceMetaInfo).getBytes());

        localRegisterNodeKeySet.add(registerKey);

    }

    @Override
    public void unRegister(ServiceMetaInfo serviceMetaInfo) {
        String registerKey = ROOT_PATH + "/" + serviceMetaInfo.getServiceNodeKey();
        try {
            client.delete().guaranteed().forPath(registerKey);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        // 从本地缓存移除
        localRegisterNodeKeySet.remove(registerKey);
    }

    @Override
    public List<ServiceMetaInfo> serviceDiscovery(String serviceKey) {
        // 优先从缓存获取服务
        List<ServiceMetaInfo> cachedServiceMetaInfoList = registryServiceCache.readCache();
        if (cachedServiceMetaInfoList != null) {
            return cachedServiceMetaInfoList;
        }

        try {

            //client.checkExists().forPath(ZK_ROOT_PATH + "/" + serviceKey);
            List<String> list = client.getChildren().forPath(ROOT_PATH + "/" + serviceKey);
            List<ServiceMetaInfo> serviceMetaInfoList = new ArrayList<>();
            list.forEach(url -> {
                try {
                    ///rpc/zookeeper/com.jiangying.service.UserService:1.0/localhost:8081
                    String path = ROOT_PATH + "/" + serviceKey + "/" + url;
                    byte[] bytes = client.getData().forPath(path);
                    String json = new String(bytes);
                    ServiceMetaInfo serviceMetaInfo = JSONUtil.toBean(json, ServiceMetaInfo.class);
                    serviceMetaInfoList.add(serviceMetaInfo);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });
            // 写入服务缓存
            registryServiceCache.writeCache(serviceMetaInfoList);

            for (ServiceMetaInfo serviceMetaInfo : serviceMetaInfoList) {
                watch(serviceMetaInfo.getServiceNodeKey());
            }
            return serviceMetaInfoList;
        } catch (Exception e) {
            throw new RuntimeException("获取服务列表失败", e);
        }
    }

    @Override
    public void heartBeat() {
        // 不需要心跳机制，建立了临时节点，如果服务器故障，则临时节点直接丢失
    }

    /**
     * 监听（消费端）
     *
     * @param serviceNodeKey 服务节点 key
     */
    @Override
    public void watch(String serviceNodeKey) {
        String watchKey = ROOT_PATH + "/" + serviceNodeKey;
        boolean newWatch = watchingKeySet.add(watchKey);
        if (newWatch) {


            CuratorCache curatorCache = CuratorCache.build(client, watchKey);
            curatorCache.start();
            curatorCache.listenable().addListener(
                    CuratorCacheListener
                            .builder()
                            .forDeletes(childData -> registryServiceCache.clearCache())
                            .forChanges(((oldNode, node) -> registryServiceCache.clearCache()))
                            .build()
            );

        }

    }

    @Override
    public void destroy() {
        //  log.info("当前节点下线");
        // 下线节点（这一步可以不做，因为都是临时节点，服务下线，自然就被删掉了）
        //还是要做的 因为服务下线后要一段时间才能删除
        for (String key : localRegisterNodeKeySet) {
            try {
                client.delete().guaranteed().forPath(key);
            } catch (Exception e) {
                throw new RuntimeException(key + "节点下线失败");
            }
        }

        // 释放资源
        if (client != null) {
            client.close();
        }
    }

}
