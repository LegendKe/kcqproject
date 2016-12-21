package com.djx.pin.beans;

import java.util.List;

/**
 * Created by Administrator on 2016/6/13.
 */
public class ProvinceModule {
    private String name;
    private List<CityModel> cityList;

    public ProvinceModule() {
        super();
    }
    public ProvinceModule(String name, List<CityModel> cityList) {
        super();
        this.name = name;
        this.cityList = cityList;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<CityModel> getCityList() {
        return cityList;
    }

    public void setCityList(List<CityModel> cityList) {
        this.cityList = cityList;
    }
    @Override
    public String toString() {
        return "ProvinceModel [name=" + name + ", cityList=" + cityList + "]";
    }
}
