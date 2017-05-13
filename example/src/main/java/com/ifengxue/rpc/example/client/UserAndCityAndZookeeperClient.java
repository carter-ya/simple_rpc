package com.ifengxue.rpc.example.client;

import com.ifengxue.rpc.client.proxy.ProxyFactory;
import com.ifengxue.rpc.example.entity.City;
import com.ifengxue.rpc.example.entity.User;
import com.ifengxue.rpc.example.service.ICityService;
import com.ifengxue.rpc.example.service.IUserService;
import org.apache.log4j.PropertyConfigurator;

import java.util.List;

/**
 *
 * 用户与城市客户端
 * Created by LiuKeFeng on 2017-05-10.
 */
public class UserAndCityAndZookeeperClient {
    public static void main(String[] args) {
        PropertyConfigurator.configure("example/conf/log4j_client.properties");
        ProxyFactory.initConfig("example/conf/user_and_city_and_zookeeper_client.xml");

        String serviceName = "UserAndCityService";
        IUserService userService = ProxyFactory.create(IUserService.class, serviceName);
        ICityService cityService = ProxyFactory.create(ICityService.class, serviceName);

        User user = new User();
        user.setName("Lucy");
        user.setAge(12);
        /**
         * 带返回值调用服务
         */
        long userID = userService.addUser(user);
        System.out.println("Lucy:" + userID);

        City city = new City();
        city.setCityName("北京");
        /**
         * 带返回值调用服务
         */
        int cityID = cityService.addCity(city);
        System.out.println("北京:" + cityID);

        List<User> userList = userService.listAllUser();
        userList.forEach(System.out::println);

        try {
            /**
             * 构造不符合Bean验证的User，触发异常
             */
            userService.addUser(new User());
        } catch (Exception e) {
            //Bean验证抛出的异常
            e.printStackTrace();
        }
        System.exit(0);
    }
}
