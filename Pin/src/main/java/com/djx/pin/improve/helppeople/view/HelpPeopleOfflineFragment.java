package com.djx.pin.improve.helppeople.view;

import android.text.TextUtils;

import com.djx.pin.activity.LoginActivity;
import com.djx.pin.application.PinApplication;
import com.djx.pin.beans.EventBeans;
import com.djx.pin.beans.HelpPeopleEntity;
import com.djx.pin.beans.UserInfo;
import com.djx.pin.improve.base.adapter.BaseRecyAdapter;
import com.djx.pin.improve.base.fragment.BaseRecyListFragment;
import com.djx.pin.improve.helppeople.adapter.HelpPeopleOfflineAdapter;
import com.djx.pin.improve.helppeople.presenter.HelpPeopleOfflinePresenterImpl;
import com.djx.pin.improve.helppeople.presenter.HelpPeopleSOSPresenterImpl;
import com.djx.pin.utils.myutils.ConstantUtils;

import java.util.List;

import de.greenrobot.event.EventBus;
import de.greenrobot.event.Subscribe;
import de.greenrobot.event.ThreadMode;

/**
 * Created by 柯传奇 on 2016/12/3 0003.
 */
public class HelpPeopleOfflineFragment extends BaseRecyListFragment implements HelpPeopleOfflineView{


    private static final String TAG = "Offline";
    private HelpPeopleOfflinePresenterImpl presenter;
    private boolean isFirstLocation = true;//当完成一次定位后才加载数据
    private int price_min,price_max,distance,order;
    private HelpPeopleSOSPresenterImpl preventerSos;
    private boolean isSOSShow = true;//是否展示sos列表总开关
    private HelpPeopleOfflineAdapter helpAdapter;
    private PinApplication myApp;
    private double latitude,longitude;


    @Subscribe(threadMode = ThreadMode.MainThread)
    public void onEvent(EventBeans eventBeans){
        switch (eventBeans.style){
            case ConstantUtils.EVENT_LOCATION_FINISH:
                if(isFirstLocation){
                    isFirstLocation = false;
                    isRefresh = true;
                    isBottom = false;
                    latitude = myApp.getBdLocation().getLatitude();
                    longitude = myApp.getBdLocation().getLongitude();
                    //首次让presenter去加载数据
                    presenter.loadData(latitude,longitude,0,0,0,0,0);
                    loadSOSList();
                }
                if (!isFirstLocation && isRefresh) {//刷新
                    if(!myApp.isMapOnLocation){
                        myApp.stopLocationClient();
                    }
                    latitude = myApp.getBdLocation().getLatitude();
                    longitude = myApp.getBdLocation().getLongitude();
                    if (latitude != 0 && longitude != 0) {
                        loadSOSList();
                        presenter.loadData(latitude,longitude,0,price_min,price_max,distance,order);
                    }
                }
                break;
            case ConstantUtils.EVENT_ORDER_CHANGE:
                if(!isFirstLocation){
                    isRefresh = true;
                    isBottom = false;
                    price_min = eventBeans.price_min;
                    price_max= eventBeans.price_max;
                    distance = eventBeans.distance;
                    order = eventBeans.order;
                    presenter.loadData(latitude,longitude,0,price_min,price_max,distance,order);
                }
                break;
        }
    }

    @Override
    protected void initView() {
        super.initView();
        //让presenter去加载数据
        presenter = new HelpPeopleOfflinePresenterImpl(getContext(),this);
        preventerSos = new HelpPeopleSOSPresenterImpl(getContext(),this);
        EventBus.getDefault().register(this);
        myApp = PinApplication.getMyApp();
    }

    /**
     * @return BaseRecyAdapter实现类
     */
    @Override
    protected BaseRecyAdapter getListAdapter() {
        helpAdapter = new HelpPeopleOfflineAdapter(context);
        return helpAdapter;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    /**
     * 显示数据
     *
     * @param list
     */
    @Override
    public void showOfflineData(List<HelpPeopleEntity> list) {
        if(isRefresh){
            helpAdapter.addAll(list);
            isRefresh = false;
        }else {
            helpAdapter.addMoreData(list);
        }
    }

    @Override
    public void showSOSData(List<HelpPeopleEntity> list) {
        List lists = helpAdapter.getLists();
        lists.addAll(0,list);
        helpAdapter.addAll(lists);
    }


    @Override
    protected void loadMoreData() {
        presenter.loadData(latitude,longitude,currentPage,price_min,price_max,distance,order);
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
        helpAdapter.setState(ConstantUtils.STATE_NO_MORE);
    }


    @Override
    public void onRefresh() {

        currentPage = 0;
        isRefresh = true;
        isBottom = false;
        //刷新时先刷新位置再加载数据
        myApp.startLocationClient();//开启定位
    }

    @Override
    public void onItemClick(int pos) {
        if(!UserInfo.getIsLogin(getContext())){
            startActivity(LoginActivity.class);
        }else {
            if(!TextUtils.isEmpty(helpAdapter.getItem(pos).location)){//location为sos特有,不为空,说明是sos
                preventerSos.dealWithItemClick(helpAdapter,pos);
            }else {
                presenter.dealWithItemClick(helpAdapter,pos);
            }
        }
    }

    private void loadSOSList(){
        if(isSOSShow){
            preventerSos.loadData();
        }
    }
}
