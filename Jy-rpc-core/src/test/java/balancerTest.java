import com.jiangying.Jyrpc.loadbalancer.Impl.ConsistentHashLoadBalancer;
import com.jiangying.Jyrpc.loadbalancer.Impl.RandomLoadBalancer;
import com.jiangying.Jyrpc.loadbalancer.Impl.RoundRobinLoadBalancer;
import com.jiangying.Jyrpc.loadbalancer.LoadBalancer;
import com.jiangying.Jyrpc.model.ServiceMetaInfo;

import java.lang.management.ManagementPermission;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class balancerTest {
    public static void main(String[] args) {
        List<ServiceMetaInfo> serviceMetaInfoList = new ArrayList<>();
        ServiceMetaInfo serviceMetaInfo1 = new ServiceMetaInfo();
        ServiceMetaInfo serviceMetaInfo2 = new ServiceMetaInfo();
        serviceMetaInfo1.setServiceHost("127.0.0.1");
        serviceMetaInfo2.setServiceHost("127.0.0.1");
        serviceMetaInfo1.setServicePort(8081);
        serviceMetaInfo2.setServicePort(8080);
        serviceMetaInfoList.add(serviceMetaInfo1);
        serviceMetaInfoList.add(serviceMetaInfo2);

        Map<String, Object> requestParams = new HashMap<>();
        requestParams.put("n3333333333333e", "jiangy");


        LoadBalancer l = new ConsistentHashLoadBalancer();
        //LoadBalancer l = new RandomLoadBalancer();
        //LoadBalancer l = new RoundRobinLoadBalancer();
        for (int i = 0; i < 10; i++) {
            ServiceMetaInfo select = l.select(requestParams, serviceMetaInfoList);
            System.out.println(select);
        }

    }
}
