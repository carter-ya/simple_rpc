package com.ifengxue.rpc.example.service;

import com.ifengxue.rpc.example.entity.City;

import java.util.List;

/**
 * Created by LiuKeFeng on 2017-05-10.
 */
public interface ICityService {
    /**
     * 添加城市
     * @param city
     * @return
     */
    int addCity(City city);

    /**
     * 根据cityID获取城市
     * @param cityID
     * @return
     */
    City getByCityID(long cityID);

    /**
     * 获取所有城市
     * @return
     */
    List<City> listAllCity();

}
