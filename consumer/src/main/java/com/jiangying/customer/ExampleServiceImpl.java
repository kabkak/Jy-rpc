package com.jiangying.customer;

import com.jiangying.jy.rpc.springboot.starter.Annotation.JyRpcReference;
import com.jiangying.model.User;
import com.jiangying.service.UserService;
import org.springframework.stereotype.Service;

@Service
public class ExampleServiceImpl {
    @JyRpcReference
    private UserService userService;

    public void test() {
        User user = new User("小江");
        User resultUser = userService.getUser(user);
        System.out.println("consumer get User:" + resultUser.getName());
    }
}
