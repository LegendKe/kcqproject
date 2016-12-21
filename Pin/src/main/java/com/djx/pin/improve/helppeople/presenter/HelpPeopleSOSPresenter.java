package com.djx.pin.improve.helppeople.presenter;

import com.djx.pin.beans.HelpPeopleEntity;
import com.djx.pin.improve.base.adapter.BaseRecyAdapter;

/**
 * Created by 柯传奇 on 2016/12/3 0003.
 */
public interface HelpPeopleSOSPresenter {

    public void loadData();
    public void dealWithItemClick(BaseRecyAdapter<HelpPeopleEntity> adapter, int pos);
}
