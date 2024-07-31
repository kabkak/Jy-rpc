package com.jiangying.Jyrpc.loadbalancer;

import com.jiangying.Jyrpc.RpcApplication;
import com.jiangying.Jyrpc.loadbalancer.Impl.ConsistentHashLoadBalancer;
import com.jiangying.Jyrpc.registry.Impl.ZookeeperRegister;
import com.jiangying.Jyrpc.registry.Register;
import com.jiangying.Jyrpc.spi.SpiLoader;


public class LoadBalancerFactory {

    static {
        SpiLoader.load(LoadBalancer.class);
    }

    public static final LoadBalancer DEFAULT_LOAD_BALANCER = new ConsistentHashLoadBalancer();

    public static LoadBalancer getLoadBalancer() {

        // 获取负载均衡器
        String key = RpcApplication.getRpcProperties().getLoadBalancer();


        return SpiLoader.getInstance(key, LoadBalancer.class);
    }
}
