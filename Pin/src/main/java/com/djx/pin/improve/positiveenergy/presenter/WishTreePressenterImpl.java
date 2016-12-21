package com.djx.pin.improve.positiveenergy.presenter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.djx.pin.activity.LoginActivity;
import com.djx.pin.beans.CivilizationInfo;
import com.djx.pin.beans.UserInfo;
import com.djx.pin.improve.base.adapter.BaseRecyAdapter;
import com.djx.pin.improve.detail.OrderDetailActivity;
import com.djx.pin.improve.positiveenergy.model.WishTreeModel;
import com.djx.pin.improve.positiveenergy.model.WishTreeModelImpl;
import com.djx.pin.improve.positiveenergy.view.WishTreeFramentView;
import com.djx.pin.utils.myutils.ConstantUtils;

import java.util.List;

/**
 * Created by 柯传奇 on 2016/12/8 0008.
 */
public class WishTreePressenterImpl implements WishTreePresenter {


    private final Context context;
    private WishTreeFramentView wishTreeFramentView;
    private WishTreeModel wishTreeModel;

    public WishTreePressenterImpl(Context context,WishTreeFramentView wishTreeFramentView) {
        this.context = context;
        this.wishTreeFramentView = wishTreeFramentView;
        wishTreeModel = new WishTreeModelImpl(context,this);
    }



    @Override
    public void loadData(int pageIndex,Double latitude,Double longitude) {
        //view显示进度条
        wishTreeFramentView.showPregress();
        //model加载数据
        wishTreeModel.loadData(pageIndex,new WishTreeModel.OnDataCompleteListener() {
            @Override
            public void response(List<CivilizationInfo.Result.CultureWallInfo> list) {
                wishTreeFramentView.showData(list);
            }
        },latitude,longitude);

    }

    /**
     * model加载完回调
     */
    @Override
    public void loadSuccess() {
        //view隐藏进度条
        wishTreeFramentView.hideProgress();
    }

    @Override
    public void loadFailed() {
        wishTreeFramentView.loadError();
    }


    @Override
    public void onNoMoreData() {
        wishTreeFramentView.noMoreData();
    }

    @Override
    public void dealWithItemClick(BaseRecyAdapter<CivilizationInfo.Result.CultureWallInfo> adapter, int pos) {
        if(!UserInfo.getIsLogin(context)){
            Intent intent = new Intent(context, LoginActivity.class);
            context.startActivity(intent);
            return;
        }
        Intent intent = new Intent(context,OrderDetailActivity.class);
        Bundle bundle=new Bundle();
        bundle.putString("id",adapter.getItem(pos).id);
        bundle.putInt("UI", ConstantUtils.WISH_TREE_DETAIL);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }

}
