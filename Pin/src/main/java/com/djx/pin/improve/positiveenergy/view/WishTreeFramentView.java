package com.djx.pin.improve.positiveenergy.view;

import com.djx.pin.beans.CivilizationInfo;

import java.util.List;

/**
 * Created by 柯传奇 on 2016/12/8 0008.
 */
public interface WishTreeFramentView {

    /**
     * 显示数据
     * @param list
     */
    void showData(List<CivilizationInfo.Result.CultureWallInfo> list);
    void hideProgress();
    void showPregress();
    void noMoreData();
    void loadError();
}
