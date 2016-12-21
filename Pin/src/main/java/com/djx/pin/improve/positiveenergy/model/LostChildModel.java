package com.djx.pin.improve.positiveenergy.model;

import com.djx.pin.beans.LostChildInfo;

import java.util.List;

/**
 * Created by 柯传奇 on 2016/12/3 0003.
 */
public interface LostChildModel {

    /**
     * 加载数据
     */
    public void loadData(int index, OnDataCompleteListener listener);

    /**
     * 加载完成监听
     */
    public interface OnDataCompleteListener{
        public void response(List<LostChildInfo.Result.ChildInfo> list);
    }
}
