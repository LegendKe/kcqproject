package com.djx.pin.improve.helppeople.presenter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.djx.pin.improve.detail.OrderDetailActivity;
import com.djx.pin.beans.HelpPeopleEntity;
import com.djx.pin.improve.base.adapter.BaseRecyAdapter;
import com.djx.pin.improve.helppeople.model.HelpPeopleSOSModel;
import com.djx.pin.improve.helppeople.model.HelpPeopleSOSModelImpl;
import com.djx.pin.improve.helppeople.view.HelpPeopleOfflineView;
import com.djx.pin.utils.myutils.ConstantUtils;

import java.util.List;

/**
 * Created by 柯传奇 on 2016/12/3 0003.
 */
public class HelpPeopleSOSPresenterImpl implements HelpPeopleSOSPresenter{

    private final Context context;
    private HelpPeopleOfflineView helpPeopleOfflineView;
    private HelpPeopleSOSModelImpl helpPeopleSOSlineModel;

    public HelpPeopleSOSPresenterImpl(Context context, HelpPeopleOfflineView helpPeopleOfflineView) {
        this.context = context;
        this.helpPeopleOfflineView = helpPeopleOfflineView;
        helpPeopleSOSlineModel = new HelpPeopleSOSModelImpl(context,this);
    }


    @Override
    public void loadData() {
        //model加载数据
        helpPeopleSOSlineModel.loadData(new HelpPeopleSOSModel.OnDataCompleteListener() {
            @Override
            public void response(List<HelpPeopleEntity> list) {
                helpPeopleOfflineView.showSOSData(list);
            }
        });
    }


    @Override
    public void dealWithItemClick(BaseRecyAdapter<HelpPeopleEntity> adapter, int pos) {
        HelpPeopleEntity info = adapter.getItem(pos);
        Intent intent = new Intent(context,OrderDetailActivity.class);
        Bundle bundle=new Bundle();
        bundle.putString("id",info.id);
        bundle.putString("share_user_id",info.share_user_id);
        bundle.putInt("UI", ConstantUtils.HELP_PEOPLE);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }

}
