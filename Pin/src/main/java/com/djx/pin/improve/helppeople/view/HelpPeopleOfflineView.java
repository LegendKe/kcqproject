package com.djx.pin.improve.helppeople.view;

import com.djx.pin.beans.HelpPeopleEntity;

import java.util.List;

/**
 * Created by 柯传奇 on 2016/12/3 0003.
 */
public interface HelpPeopleOfflineView {

    /**
     * 显示数据
     * @param list
     */
    void showOfflineData(List<HelpPeopleEntity> list);
    void showSOSData(List<HelpPeopleEntity> list);
    void hideProgress();
    void showPregress();
    void noMoreData();
}
