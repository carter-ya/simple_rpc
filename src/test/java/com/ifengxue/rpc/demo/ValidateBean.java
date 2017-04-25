package com.ifengxue.rpc.demo;

import javax.validation.constraints.Min;

/**
 * Created by LiuKeFeng on 2017-04-25.
 */
public class ValidateBean {
    @Min(10)
    private int age;

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }
}
