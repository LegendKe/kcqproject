package com.djx.pin.improve.helppeople.model;

import com.djx.pin.beans.HelpPeopleEntity;

import java.util.List;

/**
 * Created by 柯传奇 on 2016/12/3 0003.
 */
public interface HelpPeopleOfflineModel {

    /**
     * 加载数据
     */
    public void loadData(OnDataCompleteListener listener,Double latitude,Double longitude,int pageIndex,int price_min,int price_max,int distance,int order);

    /**
     * 加载完成监听
     */
    public interface OnDataCompleteListener{
        public void response(List<HelpPeopleEntity> list);
    }
}
