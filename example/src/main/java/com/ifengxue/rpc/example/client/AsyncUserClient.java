package com.ifengxue.rpc.example.client;

import com.ifengxue.rpc.client.async.AsyncRpcInvoker;
import com.ifengxue.rpc.client.async.ReturnNoneCallable;
import com.ifengxue.rpc.client.proxy.ProxyFactory;
import com.ifengxue.rpc.example.entity.User;
import com.ifengxue.rpc.example.service.IUserService;
import org.apache.log4j.PropertyConfigurator;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * 异步服务调用
 *
 * Created by LiuKeFeng on 2017-05-12.
 */
public class AsyncUserClient {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        PropertyConfigurator.configure("example/conf/log4j_client.properties");
        ProxyFactory.initConfig("example/conf/user_and_city_client.xml");

        String serviceName = "UserAndCityService";
        IUserService userService = ProxyFactory.create(IUserService.class, serviceName);

        User user = new User();
        user.setAge(12);
        user.setName("Candy");
        /**
         * 通过{@link AsyncRpcInvoker#asyncForResult(Callable)} 进行异步调用，该调用为有返回值的调用
         */
        Future<Long> idFuture = AsyncRpcInvoker.asyncForResult(() -> userService.addUser(user));
        while (true) {
            if (idFuture.isDone()) {
                System.out.println("id:" + idFuture.get());
                break;
            } else {
                System.out.println("not done.");
            }
        }

        Future<List<User>> userListFuture = AsyncRpcInvoker.asyncForResult(() -> userService.listAllUser());
        while (true) {
            if (userListFuture.isDone()) {
                userListFuture.get().forEach(System.out::println);
                break;
            } else {
                System.out.println("not done.");
            }
        }

        /**
         * 通过{@link AsyncRpcInvoker#asyncForNoneResult(ReturnNoneCallable)} 进行异步调用，该调用为无返回值的调用
         */
        AsyncRpcInvoker.asyncForNoneResult(() -> userService.addUser(user));
        System.exit(0);
    }
}
