package com.ifengxue.rpc.example.impl;

import com.ifengxue.rpc.example.entity.User;
import com.ifengxue.rpc.example.service.IUserService;
import com.ifengxue.rpc.protocol.annotation.RpcService;
import com.ifengxue.rpc.server.annotation.BeanValidate;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by LiuKeFeng on 2017-05-10.
 */
@RpcService(IUserService.class)
@BeanValidate
public class UserService implements IUserService {
    private List<User> userList = new ArrayList<User>();
    private AtomicLong userIDSequence = new AtomicLong(0);
    public long addUser(User user) {
        user.setUserID(userIDSequence.incrementAndGet());
        userList.add(user);
        return user.getUserID();
    }

    public User getByUserID(final long userID) {
        return userList.stream()
                .filter(user -> user.getUserID() == userID)
                .findAny()
                .orElseThrow(() -> new RuntimeException("不存在的用户:" + userID));
    }

    public List<User> listAllUser() {
        return userList;
    }
}
