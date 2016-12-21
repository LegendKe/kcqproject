package com.djx.pin.improve.helppeople.presenter;

import com.djx.pin.beans.HelpPeopleEntity;
import com.djx.pin.improve.base.adapter.BaseRecyAdapter;

/**
 * Created by 柯传奇 on 2016/12/3 0003.
 */
public interface HelpPeopleOfflinePresenter {

    public void loadData(Double latitude,Double longitude,int pageIndex, int price_min, int price_max, int distance, int order);
    public void loadSuccess();
    public void loadFailed();
    public void dealWithItemClick(BaseRecyAdapter<HelpPeopleEntity> adapter, int pos);

    //没有更多数据
    void onNoMoreData();
}
