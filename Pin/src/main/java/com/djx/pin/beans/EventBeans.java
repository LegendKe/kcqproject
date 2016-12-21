package com.djx.pin.beans;

import com.baidu.location.BDLocation;

import java.io.Serializable;

/**
 * Created by 柯传奇 on 2016/11/12 0012.
 */
public class EventBeans implements Serializable{
    public int style;
    public int order,distance,price_min,price_max;
    public BDLocation bdLocation;

    public EventBeans(int style, BDLocation bdLocation) {
        this.style = style;
        this.bdLocation = bdLocation;
    }

    public EventBeans() {
    }

    public EventBeans(int style, int price_min,int price_max, int distance, int order) {
        this.style = style;
        this.price_min = price_min;
        this.price_max = price_max;
        this.distance = distance;
        this.order = order;
    }

    public EventBeans(int style) {
        this.style = style;
    }
}
