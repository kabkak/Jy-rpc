package com.jiangying.provider;

import com.jiangying.jy.rpc.springboot.starter.Annotation.JyRpcService;
import com.jiangying.model.User;
import com.jiangying.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@JyRpcService
public class UserServiceImpl implements UserService {
    @Override
    public User getUser(User user) {
        System.out.println("provider received: " + user);
        return user;
    }

    @Override
    public String getString() {
        return null;
    }
}
