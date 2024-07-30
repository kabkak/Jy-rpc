import cn.hutool.json.JSONUtil;
import com.jiangying.Jyrpc.model.ServiceMetaInfo;
import com.jiangying.service.UserService;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class zookeeperRegistertest {


    private static String ZK_ROOT_PATH = "/rpc";
    private CuratorFramework client;


    @Before
    public void init() {

        client = CuratorFrameworkFactory
                .builder()
                .connectString("localhost:2181")
                .retryPolicy(new ExponentialBackoffRetry(Math.toIntExact(10000L), 3))
                .build();
        ZK_ROOT_PATH = ZK_ROOT_PATH + "/" + "zookeeper";

        client.start();

    }

    @Test
    public void register() throws Exception {
        ServiceMetaInfo serviceMetaInfo = new ServiceMetaInfo();
        serviceMetaInfo.setServiceName(UserService.class.getName());
        serviceMetaInfo.setServiceHost("localhost");
        serviceMetaInfo.setServicePort(8081);
        serviceMetaInfo.setServiceVersion("1.0");
        serviceMetaInfo.setServiceGroup("default");
        client.create().creatingParentContainersIfNeeded()
                .withMode(CreateMode.PERSISTENT)
                .forPath(ZK_ROOT_PATH + "/" + serviceMetaInfo.getServiceNodeKey(),
                        JSONUtil.toJsonStr(serviceMetaInfo).getBytes());
    }

    @Test
    public void unRegister() throws Exception {
        ServiceMetaInfo serviceMetaInfo = new ServiceMetaInfo();
        serviceMetaInfo.setServiceName(UserService.class.getName());
        serviceMetaInfo.setServiceHost("localhost");
        serviceMetaInfo.setServicePort(8081);
        serviceMetaInfo.setServiceVersion("1.0");
        serviceMetaInfo.setServiceGroup("default");
        client.delete().guaranteed().forPath(ZK_ROOT_PATH + "/" + serviceMetaInfo.getServiceNodeKey());
    }

    @Test
    public void serviceDiscovery() throws Exception {
        ServiceMetaInfo serviceMetaInfo1 = new ServiceMetaInfo();
        serviceMetaInfo1.setServiceName(UserService.class.getName());
        serviceMetaInfo1.setServiceHost("localhost");
        serviceMetaInfo1.setServicePort(8081);
        serviceMetaInfo1.setServiceVersion("1.0");
        serviceMetaInfo1.setServiceGroup("default");
        String serviceKey = serviceMetaInfo1.getServiceKey();
        System.out.println("路径:   " + serviceKey);
        List<String> list = client.getChildren().forPath(ZK_ROOT_PATH + "/" + serviceKey);
        System.out.println("服务器:" + list);
        List<ServiceMetaInfo> serviceMetaInfoList = new ArrayList<>();
        list.forEach(url -> {
            try {
                ///rpc/zookeeper/com.jiangying.service.UserService:1.0/localhost:8081
                String path = ZK_ROOT_PATH + "/" + serviceKey + "/" + url;
                byte[] bytes = client.getData().forPath(path);
                String json = new String(bytes);
                ServiceMetaInfo serviceMetaInfo = JSONUtil.toBean(json, ServiceMetaInfo.class);
                serviceMetaInfoList.add(serviceMetaInfo);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
        System.out.println(serviceMetaInfoList);
    }

}

