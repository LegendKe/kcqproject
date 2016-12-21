package com.djx.pin.activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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
import com.djx.pin.beans.MyHelperSOSDetailInfo;
import com.djx.pin.beans.StaticBean;
import com.djx.pin.serverapiconfig.ServerAPIConfig;
import com.djx.pin.ui.CommonDialog;
import com.djx.pin.utils.AndroidAsyncHttp;
import com.djx.pin.utils.LogUtil;
import com.djx.pin.utils.ToastUtil;
import com.djx.pin.utils.myutils.MapUtils;
import com.google.gson.Gson;
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

public class SOSNavigationActivity extends OldBaseActivity implements View.OnClickListener, OnGetRoutePlanResultListener, RadioGroup.OnCheckedChangeListener {
    protected final static String TAG = SOSNavigationActivity.class.getSimpleName();
    private static final int SELF_LOCATION = 20;
    private static final int UPLOAD_SETF_LOCATION = 30;

    LinearLayout ll_back;
    private Timer timer;
    /**
     * bundle里的
     * type等于1表示从帮人列表里跳到该界面,说明首次参与该单
     * type等于2表示从组人等赏里跳到该界面,说明已接单,但未完成任务
     */
    Bundle bundle;
    SharedPreferences sp;
    Context CONTEXT = SOSNavigationActivity.this;
    MyHelperSOSDetailInfo info;

    //地图图层
    private BaiduMap mBaiduMap;
    private MapView mMapView;

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

    private String session_id;
    private String id;//订单id
    private int type;//订单类型
    protected boolean isFinished = false;

    AsyncHttpResponseHandler res_getData, res_updataLocation;

    Handler handler;//用于更新SOS的发单者和接单者位置
    Handler timerHandler;//用于更新SOS的发单者和接单者位置

    Thread thread_UpDataLocation;//更新地理线程

    private int currentNavigationMode = 0; //0 car 1 walk
    private PinApplication myApp;
    private int is_show_location;
    private Timer upLoadSelfLocationTimer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sos_navigation);
        Log.i("baidu","--------SOSNavigationActivity---------------");
        myApp = PinApplication.getMyApp();
        is_show_location = getSharedPreferences(StaticBean.USER_INFO, MODE_PRIVATE).getInt("is_show_location", 0);
        initView();
        initEvent();
        initData();
        initMap();
        initNavigation();
        initSOSOrderData();
        //注册EventBus
        EventBus.getDefault().register(this);
    }


    @Subscribe(threadMode = ThreadMode.MainThread)
    public void onEvent(EventBeans eventBeans) {
        if(eventBeans.style == 1){
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
                LatLng ll = new LatLng(location.getLatitude(),
                        location.getLongitude());
                MapStatus.Builder builder = new MapStatus.Builder();
                builder.target(ll).zoom(18.0f);
                mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));

                setNavigation(new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude()), new LatLng(info.latitude, info.longitude));

            }
        }
    }

    private void initData() {
        Log.i(TAG, "initData");
        sp = getSharedPreferences(StaticBean.USER_INFO, MODE_PRIVATE);
        session_id = sp.getString("session_id", null);
        bundle = getIntent().getExtras();
        id = bundle.getString("id");
        type = bundle.getInt("type");
        isFinished = bundle.getBoolean("isfinished", false);

        if (isFinished) {
            Log.i(TAG, "sos is finished");
        }

        res_getData = new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                String str_json = new String(bytes);
                try {
                    JSONObject obj = new JSONObject(str_json);
                    if (0 == obj.getInt("code")) {
                        Gson gson = new Gson();
                        info = gson.fromJson(obj.getJSONObject("result").toString(), MyHelperSOSDetailInfo.class);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                ToastUtil.shortshow(CONTEXT, R.string.toast_error_net);
            }
        };
        res_updataLocation = new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
            }
            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                ToastUtil.shortshow(CONTEXT, R.string.toast_error_net);
            }
        };

        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                Log.i("baidu","--------SOSNavigationActivity-------handleMessage--------");
                switch (msg.what){
                    case SELF_LOCATION://定位
                        Log.i("baidu","--------30s定位结束--------locationClient.stop()");
                        if(myApp.getmLocationClient().isStarted()){
                            myApp.getmLocationClient().stop();
                        }
                        timer.cancel();
                        timer.purge();
                        timer = null;
                        break;
                    case UPLOAD_SETF_LOCATION://上传
                        getData();
                        if (isFinished) {
                            return;
                        }
                        //如果距离在2000米以外,则30秒更新一次
                        if (2000 <= MapUtils.getDistance2(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude(), info.latitude, info.longitude)) {
                            Log.i("baidu","接单者上传自己的位置");
                            updataLocationFrequency = 30000;
                            Log.i("baidu", "2000以外");
                            updataLocation();
                        }
                        //位置在2000m以内500m以外,则10秒更新一次
                        if (MapUtils.getDistance2(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude(), info.latitude, info.longitude) < 2000 && MapUtils.getDistance2(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude(), info.latitude, info.longitude) > 500) {
                            updataLocationFrequency = 10000;
                            Log.i("baidu", "进入2000-500以外");
                            updataLocation();
                        }
                        //位置在500m以内5s更新一次
                        if (MapUtils.getDistance2(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude(), info.latitude, info.longitude) <= 50) {
                            updataLocationFrequency = 5000;
                            Log.i("baidu", "进入500以内");
                            updataLocation();
                            if (!isStop) {
                                receiverCompelete();
                            }
                        }
                        break;
                }
            }
        };
    }



    Timer timerLocation;
    private void initEvent() {
        ll_back.setOnClickListener(this);
        iv_GetLocation.setOnClickListener(this);
        mBaiduMap.setOnMapStatusChangeListener(new BaiduMap.OnMapStatusChangeListener() {
            @Override
            public void onMapStatusChangeStart(MapStatus mapStatus) {
            }

            @Override
            public void onMapStatusChange(MapStatus mapStatus) {
            }
            @Override
            public void onMapStatusChangeFinish(MapStatus mapStatus) {
                Log.i("baidu","          mBaiduMap.setOnMapTouchListener----------------");
               // myApp.startLocationClient();
                if(timerLocation != null){
                    timerLocation.cancel();
                }
                timerLocation = new Timer();
                timerLocation.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        Message message = Message.obtain();
                        message.what = SELF_LOCATION;
                        handler.sendMessage(message);
                    }
                }, 30000);

                //如果用户触摸地图,并且地图不再普通模式,则将地图模式设置为普遍模式,只有普通模式用户才可移动地图,其他模式用户不可移动地图
                if (mCurrentMode != MyLocationConfiguration.LocationMode.NORMAL) {
                    setNormal();
                }
            }
        });


        rg.setOnCheckedChangeListener(this);
        timerHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                updateNavigation(new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude()), new LatLng(info.latitude, info.longitude));
            }
        };
        setTimer();
    }

    private void initView() {
        mMapView = (MapView) findViewById(R.id.mMapView);
        mBaiduMap = mMapView.getMap();
        info = new MyHelperSOSDetailInfo();
        ll_back = (LinearLayout) findViewById(R.id.ll_back);
        iv_GetLocation = (ImageView) findViewById(R.id.iv_GetLocation);
        rg = (RadioGroup) findViewById(R.id.rg);
        rb_car = (RadioButton) findViewById(R.id.rb_car);
        rb_walk = (RadioButton) findViewById(R.id.rb_walk);
        rb_car.setChecked(true);

      /*  *//**更新地理位置线程,定时更新位置*//*
        thread_UpDataLocation = new Thread(new Runnable() {
            @Override
            public void run() {
                Log.i(TAG, "更新地理位置");
                while (!isStop) {
                    try {
                        thread_UpDataLocation.sleep(updataLocationFrequency);

                        Message message = Message.obtain();
                        message.what = UPLOAD_SETF_LOCATION;
                        handler.sendMessage(message);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });*/


        upLoadSelfLocationTimer = new Timer();
        upLoadSelfLocationTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                Message message = Message.obtain();
                message.what = UPLOAD_SETF_LOCATION;
                handler.sendMessage(message);
            }
        }, 5000);



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
        thread_UpDataLocation.start();
    }

    public void updateNavigation(LatLng startLocation, LatLng endLocation) {
        Log.i(TAG, "updateNavigation");
        route = null;
        mBaiduMap.clear();
        PlanNode startNode = PlanNode.withLocation(startLocation);
        PlanNode endNode = PlanNode.withLocation(endLocation);
        if (currentNavigationMode  == 0) {
            routePlanSearch.drivingSearch(new DrivingRoutePlanOption().from(startNode).to(endNode));
        } else if (currentNavigationMode == 1) {
            routePlanSearch.walkingSearch(new WalkingRoutePlanOption().from(startNode).to(endNode));
        }
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
        if (0 == info.latitude || 0 == info.longitude) {
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
        Log.i(TAG, "onGetDrivingRouteResult");
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
        Log.i(TAG, "onCheckedChanged");
        // 重置浏览节点的路线数据
        route = null;
        mBaiduMap.clear();

        PlanNode startNode = PlanNode.withLocation(new LatLng(myApp.getBdLocation().getLatitude(),myApp.getBdLocation().getLongitude()));
        PlanNode endNode = PlanNode.withLocation(new LatLng(info.latitude, info.longitude));
        switch (checkedId) {
            case R.id.rb_car:
                currentNavigationMode = 0;
                routePlanSearch.drivingSearch(new DrivingRoutePlanOption().from(startNode).to(endNode));
                break;
            case R.id.rb_walk:
                currentNavigationMode = 1;
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
     * SOS- 接单者完成任务
     */
    private boolean isStop = false;

    private void receiverCompelete() {
        AsyncHttpResponseHandler res = new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                String str_json = new String(bytes);
                try {
                    JSONObject obj = new JSONObject(str_json);
                    if(0 == obj.getInt("code")){
                        //通知SOSDetailActivity更换状态
                        EventBus.getDefault().post(new EventBeans(2, PinApplication.getMyApp().getBdLocation()));
                    }
                    if (0 != obj.getInt("code")) {
                        LogUtil.e(CONTEXT, "receiverCompelete 数据解析错误:code=" + str_json);
                        errorCode(obj.getInt("code"));
                        return;
                    }

                    if (!isStop) {
                        isStop = true;
                        isFinished = true;
                        Button.OnClickListener positiveListener = new Button.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                finish();
                            }
                        };
                        CommonDialog.show(SOSNavigationActivity.this, "确定", "取消", getString(R.string.dialog_enter_50m), positiveListener);
                    }

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
        params.put("session_id", session_id);
        params.put("id", id);
        params.put("location", mCurrentLocation.getAddrStr());
        params.put("latitude", mCurrentLocation.getLatitude());
        params.put("longitude", mCurrentLocation.getLongitude());
        AndroidAsyncHttp.post(ServerAPIConfig.Do_ReceiverCompelete, params, res);
    }


    /**
     * 接单者更新地理位置
     */
    private void updataLocation() {
        RequestParams params = new RequestParams();
        params.put("session_id", session_id);
        params.put("id", id);
        params.put("location", mCurrentLocation.getAddrStr());
        params.put("latitude", mCurrentLocation.getLatitude());
        params.put("longitude", mCurrentLocation.getLongitude());
        params.put("user_type", 2);
        AndroidAsyncHttp.post(ServerAPIConfig.Do_UpdataLocation, params, res_updataLocation);
    }

    /**
     * 初始化订单信息
     */
    private void initSOSOrderData() {
        AsyncHttpResponseHandler res = new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                String str_json = new String(bytes);
                try {
                    JSONObject obj = new JSONObject(str_json);
                    if (0 != obj.getInt("code")) {
                        errorCode(obj.getInt("code"));
                        LogUtil.e(CONTEXT, "initData 数据解析错误:code=" + obj.toString());
                        return;
                    }
                    Gson gson = new Gson();
                    info = gson.fromJson(obj.getJSONObject("result").toString(), MyHelperSOSDetailInfo.class);
                    if (isLocationOK()) {
                        setNavigation(new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude()), new LatLng(info.latitude, info.longitude));
                    }
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
        String url = ServerAPIConfig.Do_SOSDetail + "session_id=" + sp.getString("session_id", null) + "&id=" + bundle.getString("id", null) + "&index=" + 0 + "&size=" + 10;
        AndroidAsyncHttp.get(url, res);
    }

    /**
     * 获取订单信息
     */
    private void getData() {
        String url = ServerAPIConfig.Do_SOSDetail + "session_id=" + sp.getString("session_id", null) + "&id=" + bundle.getString("id", null) + "&index=" + 0 + "&size=" + 10;
        AndroidAsyncHttp.get(url, res_getData);
    }

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
        cancelTimer();
        super.onDestroy();
        if(!myApp.isOnSOS && is_show_location==0){//sos,或位置共享
            myApp.stopLocationClient();
        }
        myOrientationListener.stop();
        upLoadSelfLocationTimer.cancel();
        upLoadSelfLocationTimer.purge();
    }



    void setTimer()
    {
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                timerHandler.sendEmptyMessage(0);
            }
        }, 15000, 15000); // 1s后执行task,经过1s再次执行
    }

    void cancelTimer()
    {
        timer.cancel();
        timer = null;
    }


}
