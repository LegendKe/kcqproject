package com.djx.pin.improve.helppeople.presenter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.djx.pin.improve.detail.OrderDetailActivity;
import com.djx.pin.beans.HelpPeopleEntity;
import com.djx.pin.improve.base.adapter.BaseRecyAdapter;
import com.djx.pin.improve.helppeople.model.HelpPeopleOfflineModel;
import com.djx.pin.improve.helppeople.model.HelpPeopleOfflineModelImpl;
import com.djx.pin.improve.helppeople.view.HelpPeopleOfflineView;
import com.djx.pin.utils.myutils.ConstantUtils;

import java.util.List;

/**
 * Created by 柯传奇 on 2016/12/3 0003.
 */
public class HelpPeopleOfflinePresenterImpl implements HelpPeopleOfflinePresenter{

    private final Context context;
    private HelpPeopleOfflineView helpPeopleOfflineView;
    private HelpPeopleOfflineModel helpPeopleOfflineModel;

    public HelpPeopleOfflinePresenterImpl(Context context,HelpPeopleOfflineView helpPeopleOfflineView) {
        this.context = context;
        this.helpPeopleOfflineView = helpPeopleOfflineView;
        helpPeopleOfflineModel = new HelpPeopleOfflineModelImpl(context,this);
    }



    @Override
    public void loadData(Double latitude,Double longitude,int pageIndex, int price_min, int price_max, int distance, int order) {
        //view显示进度条
        helpPeopleOfflineView.showPregress();
        //model加载数据
        helpPeopleOfflineModel.loadData(new HelpPeopleOfflineModel.OnDataCompleteListener() {
            @Override
            public void response(List<HelpPeopleEntity> list) {

                helpPeopleOfflineView.showOfflineData(list);

            }
        },latitude,longitude,pageIndex,price_min,price_max,distance,order);

    }

    /**
     * model加载完回调
     */
    @Override
    public void loadSuccess() {
        //view隐藏进度条
        helpPeopleOfflineView.hideProgress();
    }

    @Override
    public void loadFailed() {

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


    @Override
    public void onNoMoreData() {
        helpPeopleOfflineView.noMoreData();
    }
}
