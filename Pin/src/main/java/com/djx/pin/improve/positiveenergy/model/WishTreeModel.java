package com.djx.pin.improve.positiveenergy.model;

import com.djx.pin.beans.CivilizationInfo;

import java.util.List;

/**
 * Created by 柯传奇 on 2016/12/3 0003.
 */
public interface WishTreeModel {

    /**
     * 加载数据
     */
    public void loadData(int index,OnDataCompleteListener listener,Double latitude,Double longitude);

    /**
     * 加载完成监听
     */
    public interface OnDataCompleteListener{
        public void response(List<CivilizationInfo.Result.CultureWallInfo> list);
    }
}
