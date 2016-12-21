package com.djx.pin.improve.positiveenergy.presenter;

import com.djx.pin.beans.CivilizationInfo;
import com.djx.pin.improve.base.adapter.BaseRecyAdapter;

/**
 * Created by 柯传奇 on 2016/12/3 0003.
 */
public interface WishTreePresenter {

    void dealWithItemClick(BaseRecyAdapter<CivilizationInfo.Result.CultureWallInfo> adapter, int pos);

    void loadData(int pageIndex,Double latitude,Double longitude);
    void loadSuccess();
    void loadFailed();

    //没有更多数据
    void onNoMoreData();
}
