package com.jiangying.Jyrpc.config;


import lombok.Data;

//@Component
//@ConfigurationProperties(prefix = "rpc")
@Data
public class RpcConfig {

    private String name = "Jy-rpc";//名称

    private String version = "1.0";// 版本号

    private String serverHost = "localhost";//服务器主机名

    private Integer serverPort = 8080; //服务器端口号

    private boolean mock = false; //是否开启mock

    //序列化
    private String serializer = "jdk";

    private boolean register = true;


}
