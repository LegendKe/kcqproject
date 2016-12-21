package com.djx.pin.activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.baidu.location.BDLocation;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.RouteLine;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.route.BikingRouteResult;
import com.baidu.mapapi.search.route.DrivingRoutePlanOption;
import com.baidu.mapapi.search.route.DrivingRouteResult;
import com.baidu.mapapi.search.route.OnGetRoutePlanResultListener;
import com.baidu.mapapi.search.route.PlanNode;
import com.baidu.mapapi.search.route.RoutePlanSearch;
import com.baidu.mapapi.search.route.TransitRouteResult;
import com.baidu.mapapi.search.route.WalkingRoutePlanOption;
import com.baidu.mapapi.search.route.WalkingRouteResult;
import com.djx.pin.R;
import com.djx.pin.application.PinApplication;
import com.djx.pin.baidumap.DrivingRouteOverlay;
import com.djx.pin.baidumap.MyOrientationListener;
import com.djx.pin.baidumap.OverlayManager;
import com.djx.pin.baidumap.WalkingRouteOverlay;
import com.djx.pin.base.OldBaseActivity;
import com.djx.pin.beans.EventBeans;
import com.djx.pin.serverapiconfig.ServerAPIConfig;
import com.djx.pin.utils.AndroidAsyncHttp;
import com.djx.pin.utils.LogUtil;
import com.djx.pin.utils.ToastUtil;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Timer;
import java.util.TimerTask;

import cz.msebera.android.httpclient.Header;
import de.greenrobot.event.EventBus;
import de.greenrobot.event.Subscribe;
import de.greenrobot.event.ThreadMode;

public class NavigationActivity extends OldBaseActivity implements View.OnClickListener, OnGetRoutePlanResultListener, RadioGroup.OnCheckedChangeListener {
    protected final static String TAG = NavigationActivity.class.getSimpleName();

    LinearLayout ll_back;
    /**
     * bundle里的
     * type等于1表示从帮人列表里跳到该界面,说明首次参与该单
     * type等于2表示从组人等赏里跳到该界面,说明已接单,但未完成任务
     */
    Bundle bundle;
    SharedPreferences sp;
    Context CONTEXT = NavigationActivity.this;

    //地图图层
    private BaiduMap mBaiduMap;
    private MapView mMapView;

    private Double latitude;
    private Double longtitude;

    //定位相关
    private ImageView iv_GetLocation;//定位按钮
    private MyLocationConfiguration.LocationMode mCurrentMode = MyLocationConfiguration.LocationMode.FOLLOWING;//当前定位模式,默认正常模式

    RadioGroup rg;
    RadioButton rb_car, rb_walk;

    /**
     * 方向传感器的监听器
     */
    private MyOrientationListener myOrientationListener;
    /**
     * 方向传感器X方向的值
     */
    private int mXDirection;

    /**
     * 当前的精度
     */
    private float mCurrentAccracy;

    /**
     * 用户位置的位置
     */
    private BDLocation mCurrentLocation;

    private boolean isFirstLoc = true;//是否首次定位,默认为首次定位


    /**
     * 路线搜索
     */
    RoutePlanSearch routePlanSearch = null;    // 搜索模块，也可去掉地图模块独立使用

    int nodeIndex = -1; // 节点索引,供浏览节点时使用

    /**
     * 路线
     */
    RouteLine route = null;

    OverlayManager routeOverlay = null;

    //更新地理位置频率,单位ms
    private int updataLocationFrequency = 5000;

    private String id;//订单id
    private int type;//订单类型

    Handler handler;//用于更新SOS的发单者和接单者位置

    Thread thread_UpDataLocation;//更新地理线程
    private PinApplication myApp;
    private Timer timerLocation;
    private final static int SELF_LOCATION = 600;
    private final static int UPLOAD_SELF_LOCATION = 700;
    private boolean isFirstLoc0 = true;


    @Subscribe(threadMode = ThreadMode.MainThread)
    public void onEvent(EventBeans eventBeans){
        if(eventBeans.style == 1){//表示已更新一次定位
            if(isFirstLoc0){
                isFirstLoc0 = false;
                myApp.stopLocationClient();
            }
            BDLocation location = eventBeans.bdLocation;
            // map view 销毁后不在处理新接收的位置
            if (location == null || mMapView == null) {
                return;
            }
            MyLocationData locData = new MyLocationData.Builder()
                    .accuracy(location.getRadius())
                    // 此处设置开发者获取到的方向信息，顺时针0-360
                    .direction(mXDirection)
                    .latitude(location.getLatitude())
                    .longitude(location.getLongitude()).build();
            mCurrentAccracy = location.getRadius();
            // 设置定位数据
            mBaiduMap.setMyLocationData(locData);
            mCurrentLocation = location;
            if (isFirstLoc) {
                isFirstLoc = false;
                /**type=1表示首次参与该单,需要接单,type=2表示已经接过该单了.不需要再接单了*/
                if (type == 1){
                    receiveSOSOrder();
                }
                LatLng ll = new LatLng(location.getLatitude(),
                        location.getLongitude());
                MapStatus.Builder builder = new MapStatus.Builder();
                builder.target(ll).zoom(18.0f);
                mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
                if (isLocationOK()) {
                    setNavigation(new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude()), new LatLng(latitude, longtitude));
                }
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sos_navigation);
        Log.i("baidu","--------NavigationActivity---------------");
        myApp = PinApplication.getMyApp();
        myApp.startLocationClient();
        initView();
        initEvent();
        initData();
        initMap();
        initNavigation();
        EventBus.getDefault().register(this);
    }


    private void initData() {
        latitude = getIntent().getDoubleExtra("latitude", 0);
        longtitude = getIntent().getDoubleExtra("longtitude", 0);
        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {

                switch (msg.what){

                    case SELF_LOCATION:
                        Log.i("baidu","handleMessage-------30s定位结束---");
                        myApp.stopLocationClient();
                        timerLocation.cancel();
                        timerLocation.purge();
                        timerLocation = null;
                        break;
                   /* case UPLOAD_SELF_LOCATION:
                        //如果距离在2000米以外,则30秒更新一次
                        if (2000 <= MapUtils.getDistance2(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude(), latitude, longtitude)) {
                            updataLocationFrequency = 30000;
                            LogUtil.e("进入500以外");
                        }
                        //位置在2000m以内500m以外,则10秒更新一次
                        else if (MapUtils.getDistance2(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude(), latitude, longtitude) < 500 && MapUtils.getDistance2(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude(), latitude, longtitude) > 50) {
                            updataLocationFrequency = 10000;
                            LogUtil.e("进入500-50以外");
                        }
                        //位置在500m以内2s更新一次
                        else if (MapUtils.getDistance2(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude(), latitude, longtitude) <= 500) {
                            updataLocationFrequency = 2000;
                            LogUtil.e("进入50以内");
                        }
                        break;*/
                }
            }
        };
    }

    private void initEvent() {
        ll_back.setOnClickListener(this);
        iv_GetLocation.setOnClickListener(this);
        mBaiduMap.setOnMapTouchListener(new BaiduMap.OnMapTouchListener() {
            @Override
            public void onTouch(MotionEvent motionEvent) {
                switch (motionEvent.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        Log.i("baidu","          setOnMapTouchListener-----30s倒计时-----------");
                        myApp.startLocationClient();
                        if(timerLocation != null){
                            timerLocation.cancel();
                            timerLocation.purge();
                        }
                        timerLocation = new Timer();
                        timerLocation.schedule(new TimerTask() {
                            @Override
                            public void run() {
                                Message message = Message.obtain();
                                message.what = SELF_LOCATION;
                                handler.sendMessage(message);
                            }
                        }, 30000,30000);
                        if (mCurrentMode != MyLocationConfiguration.LocationMode.NORMAL) {
                            setNormal();
                        }
                        break;
                }

            }
        });

        rg.setOnCheckedChangeListener(this);

    }

    private void initView() {
        mMapView = (MapView) findViewById(R.id.mMapView);
        mBaiduMap = mMapView.getMap();
        ll_back = (LinearLayout) findViewById(R.id.ll_back);
        iv_GetLocation = (ImageView) findViewById(R.id.iv_GetLocation);
        rg = (RadioGroup) findViewById(R.id.rg);
        rb_car = (RadioButton) findViewById(R.id.rb_car);
        rb_walk = (RadioButton) findViewById(R.id.rb_walk);
        rb_car.setChecked(true);

       /* *//**更新地理位置线程,定时更新位置*//*
        thread_UpDataLocation = new Thread(new Runnable() {
            @Override
            public void run() {
                while (!isStop) {
                    try {
                        thread_UpDataLocation.sleep(updataLocationFrequency);
                        Message message = Message.obtain();
                        message.what = UPLOAD_SELF_LOCATION;
                        handler.sendMessage(message);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });*/
    }

    /**
     * 设置导航路线
     *
     * @param startLocation
     * @param endLocation
     */
    int k = 1;

    public void setNavigation(LatLng startLocation, LatLng endLocation) {
        PlanNode startNode = PlanNode.withLocation(startLocation);
        PlanNode endNode = PlanNode.withLocation(endLocation);
        routePlanSearch.drivingSearch(new DrivingRoutePlanOption().from(startNode).to(endNode));
        //thread_UpDataLocation.start();


    }


    /**
     * 检查起始位置 结束位置是否都已经获得
     *
     * @return 起始位置 结束位置都已经获得,即不为空 返回true,反之false
     */
    private boolean isLocationOK() {
        if (null == mCurrentLocation) {
            return false;
        }
        if (0 == latitude || 0 == longtitude) {
            return false;
        }
        return true;
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            /**点击返回按钮*/
            case R.id.ll_back:
                this.finish();
                break;
            /**点击定位按钮*/
            case R.id.iv_GetLocation:
                //NORMAL表示正常模式,COMPASS表示罗盘模式,FOLLOWING表示跟随模式
                switch (mCurrentMode) {
                    //设置为跟随模式
                    case NORMAL:
                        setFollowing();
                        break;
                    //设置为跟随模式
                    case COMPASS:
                        setFollowing();
                        break;
                    //设置为罗盘模式
                    case FOLLOWING:
                        setCompass();
                        break;
                }
                break;
        }
    }

    /**
     * 初始化百度地图
     */
    public void initMap() {
        mMapView.showZoomControls(false);//设置不再显示放大缩小按钮
        mMapView.showScaleControl(false);//设置不再显示比例尺
        mBaiduMap.getUiSettings().setCompassEnabled(false);//设置不在显示指南针
        mBaiduMap.setMapStatus(MapStatusUpdateFactory.newMapStatus(new MapStatus.Builder().zoom(18).build()));//设置百度地图缩放等级为18级
        // 开启定位图层
        mBaiduMap.setMyLocationEnabled(true);
        setFollowing();
        // 初始化传感器
        initOritationListener();
        // 开启方向传感器
        myOrientationListener.start();
    }


    /**
     * 初始化导航
     */
    public void initNavigation() {
        // 初始化搜索模块，注册事件监听
        routePlanSearch = RoutePlanSearch.newInstance();
        routePlanSearch.setOnGetRoutePlanResultListener(this);
    }


    /**
     * 初始化方向传感器
     */
    private void initOritationListener() {
        myOrientationListener = new MyOrientationListener(this);
        myOrientationListener
                .setOnOrientationListener(new MyOrientationListener.OnOrientationListener() {
                    @Override
                    public void onOrientationChanged(float x) {
                        mXDirection = (int) x;

                        // 构造定位数据
                        if (mCurrentLocation == null) {
                            return;
                        }
                        MyLocationData locData = new MyLocationData.Builder()
                                .accuracy(mCurrentAccracy)
                                // 此处设置开发者获取到的方向信息，顺时针0-360
                                .direction(mXDirection)
                                .latitude(mCurrentLocation.getLatitude())
                                .longitude(mCurrentLocation.getLongitude()).build();
                        // 设置定位数据
                        mBaiduMap.setMyLocationData(locData);
                        MyLocationConfiguration config = new MyLocationConfiguration(
                                mCurrentMode, true, null);
                        mBaiduMap.setMyLocationConfigeration(config);

                    }
                });
    }


    @Override
    public void onGetWalkingRouteResult(WalkingRouteResult result) {

        if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
            ToastUtil.shortshow(CONTEXT, R.string.toast_non_navigation);
            return;
        }
        if (result.error == SearchResult.ERRORNO.AMBIGUOUS_ROURE_ADDR) {
            // 起终点或途经点地址有岐义，通过以下接口获取建议查询信息
            LogUtil.e(CONTEXT, "位置错误");
            ToastUtil.shortshow(CONTEXT, R.string.toast_error_location);
            return;
        }
        if (result.error == SearchResult.ERRORNO.NO_ERROR) {
            nodeIndex = -1;
            route = result.getRouteLines().get(0);
            WalkingRouteOverlay overlay = new MyWalkingRouteOverlay(mBaiduMap);
            mBaiduMap.setOnMarkerClickListener(overlay);
            routeOverlay = overlay;
            overlay.setData(result.getRouteLines().get(0));
            overlay.addToMap();
            overlay.zoomToSpan();
        }
    }

    @Override
    public void onGetTransitRouteResult(TransitRouteResult transitRouteResult) {

    }

    @Override
    public void onGetDrivingRouteResult(DrivingRouteResult result) {
        if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
            ToastUtil.shortshow(CONTEXT, R.string.toast_non_navigation);
            return;
        }
        if (result.error == SearchResult.ERRORNO.AMBIGUOUS_ROURE_ADDR) {
            // 起终点或途经点地址有岐义，通过以下接口获取建议查询信息
            LogUtil.e(CONTEXT, "位置错误");
            ToastUtil.shortshow(CONTEXT, R.string.toast_error_location);
            return;
        }
        if (result.error == SearchResult.ERRORNO.NO_ERROR) {
            nodeIndex = -1;
            route = result.getRouteLines().get(0);

            DrivingRouteOverlay overlay = new MyDrivingRouteOverlay(mBaiduMap);
            routeOverlay = overlay;
            mBaiduMap.setOnMarkerClickListener(overlay);
            overlay.setData(result.getRouteLines().get(0));
            overlay.addToMap();
            overlay.zoomToSpan();
        }
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        // 重置浏览节点的路线数据
        route = null;
        mBaiduMap.clear();

        PlanNode startNode = PlanNode.withLocation(new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude()));
        PlanNode endNode = PlanNode.withLocation(new LatLng(latitude, longtitude));
        switch (checkedId) {
            case R.id.rb_car:
                routePlanSearch.drivingSearch(new DrivingRoutePlanOption().from(startNode).to(endNode));
                break;
            case R.id.rb_walk:
                routePlanSearch.walkingSearch(new WalkingRoutePlanOption().from(startNode).to(endNode));
                break;
        }
    }


    // 定制RouteOverly
    private class MyDrivingRouteOverlay extends DrivingRouteOverlay {

        public MyDrivingRouteOverlay(BaiduMap baiduMap) {
            super(baiduMap);
        }

        @Override
        public BitmapDescriptor getStartMarker() {
            return null;
        }

        @Override
        public BitmapDescriptor getTerminalMarker() {
            return null;
        }

    }

    private class MyWalkingRouteOverlay extends WalkingRouteOverlay {

        public MyWalkingRouteOverlay(BaiduMap baiduMap) {
            super(baiduMap);
        }

        @Override
        public BitmapDescriptor getStartMarker() {
            return null;
        }

        @Override
        public BitmapDescriptor getTerminalMarker() {
            return null;
        }
    }

    @Override
    public void onGetBikingRouteResult(BikingRouteResult bikingRouteResult) {

    }


    /**
     * 设置地图为跟随模式
     * 默认旋转角度0,俯视角度0
     */
    private void setFollowing() {
        perfomRotate(0);//将旋转角度置0
        perfomOverlook(0);//将俯视角度置0
        mCurrentMode = MyLocationConfiguration.LocationMode.FOLLOWING;
        iv_GetLocation.setImageResource(R.drawable.iv_getlocation_selector_following);
        //null表示使用百度地图自带的默认的图标
        mBaiduMap.setMyLocationConfigeration(
                new MyLocationConfiguration(mCurrentMode, true, null));

    }

    /**
     * 设置地图为罗盘模式
     */
    private void setCompass() {
        mCurrentMode = MyLocationConfiguration.LocationMode.COMPASS;
        iv_GetLocation.setImageResource(R.drawable.iv_getlocation_selector_compass);
        mBaiduMap.setMyLocationConfigeration(
                new MyLocationConfiguration(mCurrentMode, true, null));
    }

    /**
     * 设置地图为普通模式
     */
    private void setNormal() {
        mCurrentMode = MyLocationConfiguration.LocationMode.NORMAL;
        iv_GetLocation.setImageResource(R.drawable.iv_getlocation_selector_normal);
        mBaiduMap.setMyLocationConfigeration(
                new MyLocationConfiguration(mCurrentMode, true, null)
        );
    }


    /**
     * 处理旋转 旋转角范围： -180 ~ 180 , 单位：度 逆时针旋转
     */
    private void perfomRotate(int rotateAngle) {
        MapStatus ms = new MapStatus.Builder(mBaiduMap.getMapStatus()).rotate(rotateAngle).build();
        MapStatusUpdate u = MapStatusUpdateFactory.newMapStatus(ms);
        mBaiduMap.animateMapStatus(u);
    }

    /**
     * 处理俯视 俯角范围： -45 ~ 0 , 单位： 度
     */
    private void perfomOverlook(int overlookAngle) {
        MapStatus ms = new MapStatus.Builder(mBaiduMap.getMapStatus()).overlook(overlookAngle).build();
        MapStatusUpdate u = MapStatusUpdateFactory.newMapStatus(ms);
        mBaiduMap.animateMapStatus(u);
    }

    /**
     * SOS-接单者接单
     */
    public void receiveSOSOrder() {

        AsyncHttpResponseHandler res = new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                String str_json = new String(bytes);

                try {
                    JSONObject obj = new JSONObject(str_json);
                    if (0 != obj.getInt("code")) {
                        if (3101 == obj.getInt("code")) {
                            ToastUtil.shortshow(CONTEXT, R.string.toast_err_receive_sos_order_repeat);
                            isStop = true;
                            NavigationActivity.this.finish();
                            return;
                        }
                        if (2022 == obj.getInt("code")) {
                            ToastUtil.shortshow(CONTEXT, R.string.toast_err_receive_sos_order_repeat1);
                            isStop = true;
                            NavigationActivity.this.finish();
                            return;
                        }
                        errorCode(obj.getInt("code"));
                        LogUtil.e(CONTEXT, "receiveSOSOrder 数据解析错误:code=" + obj.toString());
                        ToastUtil.shortshow(CONTEXT, R.string.toast_receivesos_failer);
                        return;
                    }
                    ToastUtil.shortshow(CONTEXT, R.string.toast_receivesos_success);
                } catch (JSONException e) {
                    LogUtil.e(CONTEXT, "enter catch=" + e.toString());
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                LogUtil.e(CONTEXT, "网络连接异常");
                ToastUtil.shortshow(CONTEXT, R.string.toast_error_net);
            }
        };
        RequestParams params = new RequestParams();
        params.put("session_id", sp.getString("session_id", null));
        params.put("id", bundle.getString("id"));
        params.put("latitude", mCurrentLocation.getLatitude());
        params.put("longitude", mCurrentLocation.getLongitude());
        params.put("location", mCurrentLocation.getAddrStr());
        AndroidAsyncHttp.post(ServerAPIConfig.Do_SOSReceive, params, res);
    }

    /**
     * SOS- 接单者完成任务
     */
    private boolean isStop = false;


    @Override
    public void onResume() {
        mMapView.onResume();
        isStop=false;
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        isStop=true;
    }

    @Override
    protected void onStop() {
        super.onStop();
        isStop=true;
    }
    @Override
    public void onDestroy() {
        // 关闭定位图层
        mBaiduMap.setMyLocationEnabled(false);
        mMapView.onDestroy();
        isStop=true;
        super.onDestroy();
        myApp.stopLocationClient();
        myOrientationListener.stop();
        EventBus.getDefault().unregister(this);
    }


}
