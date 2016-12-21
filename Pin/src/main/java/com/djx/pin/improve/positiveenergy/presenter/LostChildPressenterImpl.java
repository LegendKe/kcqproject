package com.djx.pin.improve.positiveenergy.presenter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.djx.pin.activity.LoginActivity;
import com.djx.pin.activity.LostChildDetailActivity;
import com.djx.pin.beans.LostChildInfo;
import com.djx.pin.beans.UserInfo;
import com.djx.pin.improve.base.adapter.BaseRecyAdapter;
import com.djx.pin.improve.positiveenergy.model.LostChildModel;
import com.djx.pin.improve.positiveenergy.model.LostChildModelImpl;
import com.djx.pin.improve.positiveenergy.view.LostChildFramentView;

import java.util.List;

/**
 * Created by 柯传奇 on 2016/12/8 0008.
 */
public class LostChildPressenterImpl implements LostChildPresenter {


    private final Context context;
    private LostChildFramentView lostChildFramentView;
    private LostChildModelImpl lostChildModel;

    public LostChildPressenterImpl(Context context, LostChildFramentView lostChildFramentView) {
        this.context = context;
        this.lostChildFramentView = lostChildFramentView;
        lostChildModel = new LostChildModelImpl(context,this);
    }



    @Override
    public void loadData(int pageIndex) {
        //view显示进度条
        lostChildFramentView.showPregress();
        //model加载数据
        lostChildModel.loadData(pageIndex,new LostChildModel.OnDataCompleteListener() {
            @Override
            public void response(List<LostChildInfo.Result.ChildInfo> list) {
                lostChildFramentView.showData(list);
            }
        });

    }

    /**
     * model加载完回调
     */
    @Override
    public void loadSuccess() {
        //view隐藏进度条
        lostChildFramentView.hideProgress();
    }

    @Override
    public void loadFailed() {
        lostChildFramentView.loadError();
    }


    @Override
    public void onNoMoreData() {
        lostChildFramentView.noMoreData();
    }

    @Override
    public void dealWithItemClick(BaseRecyAdapter<LostChildInfo.Result.ChildInfo> adapter, int pos) {
        if(!UserInfo.getIsLogin(context)){
            Intent intent = new Intent(context, LoginActivity.class);
            context.startActivity(intent);
            return;
        }
        Intent intent = new Intent(context,LostChildDetailActivity.class);
        Bundle bundle=new Bundle();
        bundle.putString("id",adapter.getItem(pos).id);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }

}
