package com.djx.pin.improve.positiveenergy.view;

import com.djx.pin.beans.LostChildInfo;

import java.util.List;

/**
 * Created by 柯传奇 on 2016/12/8 0008.
 */
public interface LostChildFramentView {

    /**
     * 显示数据
     * @param list
     */
    void showData(List<LostChildInfo.Result.ChildInfo> list);
    void hideProgress();
    void showPregress();
    void noMoreData();
    void loadError();
}
