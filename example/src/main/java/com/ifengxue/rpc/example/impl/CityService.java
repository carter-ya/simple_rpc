package com.ifengxue.rpc.example.impl;

import com.ifengxue.rpc.example.entity.City;
import com.ifengxue.rpc.example.service.ICityService;
import com.ifengxue.rpc.protocol.annotation.RpcService;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by LiuKeFeng on 2017-05-10.
 */
@RpcService(ICityService.class)
public class CityService implements ICityService {
    private List<City> cityList = new ArrayList<>();
    private AtomicInteger cityIDSequence = new AtomicInteger(0);

    @Override
    public int addCity(@NotNull City city) {
        city.setCityID(cityIDSequence.incrementAndGet());
        cityList.add(city);
        return city.getCityID();
    }

    @Override
    public City getByCityID(long cityID) {
        return cityList.stream()
                .filter(city -> city.getCityID() == cityID)
                .findAny()
                .orElseThrow(() -> new RuntimeException("不存在的城市:" + cityID));
    }

    @Override
    public List<City> listAllCity() {
        return cityList;
    }
}
