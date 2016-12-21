package com.djx.pin.improve.helppeople.model;

import com.djx.pin.beans.HelpPeopleEntity;

import java.util.List;

/**
 * Created by 柯传奇 on 2016/12/3 0003.
 */
public interface HelpPeopleSOSModel {

    /**
     * 加载数据
     */
    public void loadData(OnDataCompleteListener listener);

    /**
     * 加载完成监听
     */
    public interface OnDataCompleteListener{
        public void response(List<HelpPeopleEntity> list);
    }
}
