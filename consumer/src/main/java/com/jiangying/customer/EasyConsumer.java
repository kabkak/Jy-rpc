package com.jiangying.customer;


import com.jiangying.Jyrpc.proxy.ServiceProxyFactory;
import com.jiangying.model.User;
import com.jiangying.service.UserService;



/**
 * 简单服务消费者
 */

public class EasyConsumer {
    public static void main(String[] args) {


        UserService userService = ServiceProxyFactory.getServiceProxy(UserService.class);


        User user = new User("小江");
        //每隔0.5秒获取一次用户名
        while (true) {

            User newUser = userService.getUser(user);
            if (newUser == null) {
                System.out.println("消费者没有得到用户名");
            } else {
                System.out.println("消费者得到用户名字: " + user.getName());
            }
            try {
                Thread.sleep(15000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }
}
