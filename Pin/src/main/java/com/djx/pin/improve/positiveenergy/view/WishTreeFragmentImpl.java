package com.djx.pin.improve.positiveenergy.view;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.djx.pin.R;
import com.djx.pin.activity.GoHomeMainActivity;
import com.djx.pin.activity.WindowThrowMainActivity;
import com.djx.pin.application.PinApplication;
import com.djx.pin.beans.CivilizationInfo;
import com.djx.pin.beans.EventBeans;
import com.djx.pin.improve.base.adapter.BaseRecyAdapter;
import com.djx.pin.improve.base.fragment.BaseRecyListFragment;
import com.djx.pin.improve.positiveenergy.LostChildActivity;
import com.djx.pin.improve.positiveenergy.adapter.WishTreeAdapter;
import com.djx.pin.improve.positiveenergy.presenter.WishTreePressenterImpl;
import com.djx.pin.utils.myutils.ConstantUtils;
import com.djx.pin.utils.myutils.ScreenTools;

import java.util.List;

import de.greenrobot.event.EventBus;
import de.greenrobot.event.Subscribe;
import de.greenrobot.event.ThreadMode;

/**
 * Created by 柯传奇 on 2016/12/8 0008.
 */
public class WishTreeFragmentImpl extends BaseRecyListFragment implements WishTreeFramentView, View.OnClickListener {

    private WishTreePressenterImpl presenter;
    private WishTreeAdapter wishTreeAdapter;
    private Double latitude,longitude;
    private boolean isFirstLocation = true;
    private View headView_copy;
    private int view_hei;
    private ObjectAnimator moveIn;
    private ObjectAnimator moveOut,moveOut_alpha;
    private boolean flag_out = false;
    private PinApplication myApp;

    @Subscribe(threadMode = ThreadMode.MainThread)
    public void onEvent(EventBeans eventBeans){
        switch (eventBeans.style){
            case ConstantUtils.EVENT_LOCATION_FINISH:
                if (isFirstLocation) {//首次加载
                    isFirstLocation = false;
                    latitude = myApp.getBdLocation().getLatitude();
                    longitude = myApp.getBdLocation().getLongitude();
                    presenter.loadData(0, latitude, longitude);
                }else if (!isFirstLocation && isRefresh) {//刷新
                    if(!myApp.isMapOnLocation){//地图页不在30s倒计时
                        myApp.stopLocationClient();
                    }
                    latitude = myApp.getBdLocation().getLatitude();
                    longitude = myApp.getBdLocation().getLongitude();
                    if (latitude != 0 && longitude != 0) {
                        presenter.loadData(0, latitude, longitude);
                    }
                }
                break;

        }
    }

    @Override
    protected void initView() {
        super.initView();
        myApp = PinApplication.getMyApp();
        myApp.startLocationClient();//开启定位
        presenter = new WishTreePressenterImpl(getContext(),this);
        EventBus.getDefault().register(this);
        initHeadView();

    }

    private void initHeadView() {
        view_hei = ScreenTools.instance(context).dip2px(110);
        //为控制时headview隐藏显示效果，添加了headivew_copy，使之重叠
        headView_copy = View.inflate(context, R.layout.item_wish_tree_headview_copy, null);
        headView_copy.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, view_hei));
        headView_copy.setVisibility(View.GONE);
        framelayout_parent.addView(headView_copy);
        headView_copy.findViewById(R.id.ll_go_home).setOnClickListener(this);
        headView_copy.findViewById(R.id.ll_window_throw).setOnClickListener(this);
        headView_copy.findViewById(R.id.ll_civilization).setOnClickListener(this);
        moveIn = ObjectAnimator.ofFloat(headView_copy, "translationY", 0f, -view_hei);
        moveOut = ObjectAnimator.ofFloat(headView_copy, "translationY", -view_hei, 0f);
        moveOut_alpha = ObjectAnimator.ofFloat(headView_copy, "alpha", 1f, 0f);
        moveIn.setDuration(300);
        moveOut.setDuration(300);
        moveOut_alpha.setDuration(200);
    }

    /**
     * @return BaseRecyAdapter实现类
     */
    @Override
    protected BaseRecyAdapter getListAdapter() {
        wishTreeAdapter = new WishTreeAdapter(context);
        wishTreeAdapter.setIsAddHeadView(true);
        return wishTreeAdapter;
    }

    @Override
    public void onItemClick(int pos) {
        presenter.dealWithItemClick(wishTreeAdapter,pos);
    }

    @Override
    public void onRefresh() {
        currentPage = 0;
        isRefresh = true;
        isBottom = false;
        //刷新时先刷新位置再加载数据
        myApp.startLocationClient();//开启定位
    }

    /**
     * 显示数据
     *
     * @param list
     */
    @Override
    public void showData(List<CivilizationInfo.Result.CultureWallInfo> list) {
        if(isRefresh){
            wishTreeAdapter.addAll(list);
            Log.e("wishtree","---------------刷新---addAll()--");
            isRefresh = false;
        }else {
            Log.e("wishtree","--------------添加更多----addMoreData()--");
            wishTreeAdapter.addMoreData(list);
        }
    }

    @Override
    public void hideProgress() {
        swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void showPregress() {
        swipeRefreshLayout.setRefreshing(true);
    }

    @Override
    public void noMoreData() {
        isBottom = true;
        wishTreeAdapter.setState(ConstantUtils.STATE_NO_MORE);
    }

    @Override
    public void loadError() {
        swipeRefreshLayout.setRefreshing(false);
    }

    /**
     * recyclerView滑到底,列表加载更多
     */
    @Override
    protected void loadMoreData() {
        presenter.loadData(currentPage,latitude,longitude);
    }

    @Override
    protected OnScrollStateChangeListener getScrollStateChangeListener() {
        return new OnScrollStateChangeListener() {
            @Override
            public void onChanged(int top,int dy, int firstVisible) {

                if(top == 0){
                    headView_copy.setVisibility(View.GONE);
                }
                if (dy < -60 && firstVisibleItemPosition > 0) {//往下滑
                    if (flag_out) {
                        headView_copy.setVisibility(View.VISIBLE);
                        moveOut.start();
                        flag_out = false;
                    }
                } else if (dy > 30 && firstVisibleItemPosition > 0) {//往上滑
                    if (!flag_out) {
                        moveIn.start();
                        flag_out = true;
                    }
                }

            }

        };
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.ll_go_home:
                startActivity(new Intent(getContext(), GoHomeMainActivity.class));
                break;
            case R.id.ll_civilization:
                startActivity(new Intent(getContext(), LostChildActivity.class));
                break;
            case R.id.ll_window_throw:
                startActivity(new Intent(getContext(), WindowThrowMainActivity.class));
                break;
        }
    }
}
