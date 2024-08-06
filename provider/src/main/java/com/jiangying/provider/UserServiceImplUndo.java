package com.jiangying.provider;

import com.jiangying.model.User;
import com.jiangying.service.UserService;

public class UserServiceImplUndo implements UserService {

    @Override
    public User getUser(User user) {

        System.out.println("用户名: " + user.getName());

        return user;
    }

    @Override
    public String getString() {
        System.out.println("nhnhnhnhnhnhnhn");
        return "测试测试测试测试测试测试";
    }


}
