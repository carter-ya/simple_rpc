package com.ifengxue.rpc.example.service;

import com.ifengxue.rpc.example.entity.User;

import java.util.List;

/**
 * Created by LiuKeFeng on 2017-05-10.
 */
public interface IUserService {
    /**
     * 添加用户
     * @param user
     * @return 新用户的ID
     */
    long addUser(User user);

    /**
     * 通过用户ID获取用户ID
     * @param userID
     * @return
     */
    User getByUserID(long userID);

    /**
     * 获取所有用户
     * @return
     */
    List<User> listAllUser();
}
