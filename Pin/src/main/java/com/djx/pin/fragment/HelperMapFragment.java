package com.djx.pin.fragment;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationConfiguration.LocationMode;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.djx.pin.R;
import com.djx.pin.activity.BundlePhoneActivity;
import com.djx.pin.activity.IdentityActivity;
import com.djx.pin.activity.LoginActivity;
import com.djx.pin.activity.LookOthersMassageActivity;
import com.djx.pin.activity.MyHelperSOSDetailActivity;
import com.djx.pin.activity.SOSCountdownActivity;
import com.djx.pin.activity.SOSSafeActivity;
import com.djx.pin.activity.SettingEmergencyPersonActivity;
import com.djx.pin.application.PinApplication;
import com.djx.pin.baidumap.MyOrientationListener;
import com.djx.pin.beans.EventBeans;
import com.djx.pin.beans.StaticBean;
import com.djx.pin.beans.UserAround;
import com.djx.pin.beans.UserAroundResult;
import com.djx.pin.beans.UserInfo;
import com.djx.pin.business.AppConstants;
import com.djx.pin.improve.helpmap.SearchActivity;
import com.djx.pin.improve.helpmap.activity.HelpPublishOfflineActivity;
import com.djx.pin.improve.helpmap.activity.HelpPublishOnlineActivity;
import com.djx.pin.myview.CircleImageView;
import com.djx.pin.serverapiconfig.ServerAPIConfig;
import com.djx.pin.sql.MySQLLiteOpenHelper;
import com.djx.pin.ui.CommonDialog;
import com.djx.pin.utils.AndroidAsyncHttp;
import com.djx.pin.utils.LogUtil;
import com.djx.pin.utils.QiniuUtils;
import com.djx.pin.utils.ToastUtil;
import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.util.TextUtils;
import de.greenrobot.event.EventBus;
import de.greenrobot.event.Subscribe;
import de.greenrobot.event.ThreadMode;

/**
 * Created by Administrator on 2016/4/26.
 */
public class HelperMapFragment extends BaseFragment implements View.OnClickListener{
    protected final static String TAG = HelperMapFragment.class.getSimpleName();
    private static final int LOCATION_SELF = 40;

    ImageView ll_User_HelperMapFragment,
            iv_search;
    GeoCoder mSearch = null; // 搜索模块，也可去掉地图模块独立使用
    Dialog dialog_HelpType,//悬赏类型Dialog
            dialog_RealName,//实名认证
            dialog_SOSWarning,//SOS警告
            dilog_EmergencyPeople,//紧急联系人
            dialog_Phone;//绑定手机号Dialgo

    View dialogView_HelpType,//悬赏类型
            dialogView_Phone,//绑定手机号
            dialogView_RealName,//实名认证
            dilogView_EmergencyPeople,//紧急联系人
            dialogView_SOSWarning;//SOS警告

    Button bt_cancle,//取消按钮
            bt_net_help,//网络悬赏
            bt_confirm_dialog_phone,//绑定手机号确定
            bt_cancle_dialog_phone,//绑定手机号取消
            bt_confirm_dialog_real_name,//实名认证确定按钮
            bt_continue_dialog_real_name,//实名认证继续按钮
            bt_confirm_dialog_sos_warning,//SOS警告确定按钮
            bt_cancle_dialog_sos_warning,//SOS警告取消按钮
            bt_confirm_dialog_emergency,//紧急联系人确定按钮
            bt_cancle_dialog_emergency,//紧急联系人取消按钮
            bt_real_help;//线下悬赏
    Intent intent_Gallery, intent_Camera;
    LayoutInflater inflater;
    SharedPreferences sp;
    private Map<String, UserAround> peopleAround = new HashMap<>();
    private int sosSource; //1、界面点击 2、耳机按键触发
    private MySQLLiteOpenHelper mySqlLite;
    private MyOrientationListener myOrientationListener;
    private int mXDirection;
    private float mCurrentAccracy;
    private double mCurrentLantitude;
    private double mCurrentLongitude;
    private final static int maxPeopleAroundLimit = 100;
    private final static int maxDistanceAroundLimit = 50;

    //界面控件
    private View v;
    //地图图层
    private BaiduMap mBaiduMap;
    private MapView mMapView;
    private UserAround mCurUser;
    //定位相关
    private ImageView iv_GetLocation;//定位按钮
    private LocationMode mCurrentMode = LocationMode.FOLLOWING;//当前定位模式,默认正常模式
    private boolean isFirstLoc = true,isFirstLoc0 = true;//是否首次定位,默认为首次定位


    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(AppConstants.INTENT_ACTION_LOGIN_TOMAP)) {
                Log.i(TAG, "action login to map");
                getPeopleAround(mCurrentLantitude, mCurrentLongitude);
            } else if (intent.getAction().equals(AppConstants.INTENT_ACTION_SOS)) {
                Log.i(TAG, "action sos");
                sosSource = intent.getIntExtra("sossource", 0);
                onSOS();
            }
        }
    };
    private LatLng selectedLatLng;//选择的坐标,发表动态
    private LatLng oldSelectedLatLng;//选择的坐标,发表动态
    private TextView tv_show_location;
    private ReverseGeoCodeResult.AddressComponent addressDetail;
    private int showNum = 0;//显示次数
    private int NUM = 0;
    private int NUM_LOCATION,MAP_CHANGE_NUM;
    private static int COUNT_DOWN = 100;
    private Timer timer;
    private PinApplication myApp;
    private View iv_emergency_calling;
    private View iv_Help;
    private Animation animation3;
    private Animation anim_dialog_exit;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        LogUtil.e("--HelperMapFragment------onCreateView()------");
        v = View.inflate(getContext(), R.layout.helpermapfragment_layout,null);
        myApp = PinApplication.getMyApp();
        mySqlLite = myApp.getMySQLLiteOpenHelper();
        //注册EventBus,监听application的实时位置
        EventBus.getDefault().register(this);
        myApp.startLocationClient();
        initView();
        initDialog();
        initEvent();
        initBaiduMap();
        getPeopleAround(mCurrentLantitude, mCurrentLongitude);
        return v;
    }

    @Subscribe(threadMode = ThreadMode.MainThread)
    public void onEvent(EventBeans eventBeans) {
        if(eventBeans.style == 1){
            NUM_LOCATION++;
            if(NUM_LOCATION == 15){
                Log.e("pic","------updateMarker()-"+NUM_LOCATION);
                updateMarker();
            }
            if(NUM_LOCATION == 30){
                myApp.stopLocationClient();
                myApp.isMapOnLocation = false;
            }
            BDLocation bdLocation = eventBeans.bdLocation;
            if (bdLocation != null && mBaiduMap != null) {
                MyLocationData locData = new MyLocationData.Builder()
                        .accuracy(bdLocation.getRadius())
                        // 此处设置开发者获取到的方向信息，顺时针0-360
                        .direction(mXDirection)
                        .latitude(bdLocation.getLatitude())
                        .longitude(bdLocation.getLongitude()).build();
                mCurrentAccracy = bdLocation.getRadius();
                // 设置定位数据
                mBaiduMap.setMyLocationData(locData);
                mCurrentLantitude = bdLocation.getLatitude();
                mCurrentLongitude = bdLocation.getLongitude();
                //最新定位位置更新到application中
                myApp.setBdLocation(bdLocation);
                if (isFirstLoc) {
                    isFirstLoc = false;
                    LatLng ll = new LatLng(bdLocation.getLatitude(),bdLocation.getLongitude());
                    MapStatus.Builder builder = new MapStatus.Builder();
                    builder.target(ll).zoom(16.0f);
                    mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
                    getPeopleAround(mCurrentLantitude, mCurrentLongitude);
                }
            }
        }else if(eventBeans.style == 2 && isFirstLoc0 == false){//mainactivity切换viewpager触发
            Log.i("baidu","mainactivity切换viewpager触发");
            updateMarker();
            myApp.startLocationClient();
            Log.i("baidu", "-------触碰地图---30s定位开始------locationClient.start()");
            myApp.isMapOnLocation = true;
            if(timer != null){
                timer.cancel();
                timer.purge();
            }
            timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    Message message = Message.obtain();
                    message.what = LOCATION_SELF;
                    handler.sendMessage(message);
                }
            }, 30000,30000);
        }
    }
    private void getPeopleAround(double latitude, double longitude) {
        Log.e(TAG, "getPeopleAround");
        String url = ServerAPIConfig.AroundMe;
        AsyncHttpResponseHandler res = new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                String str_json = new String(bytes);
                Log.i(TAG, str_json);
                try {
                    JSONObject obj = new JSONObject(str_json);
                    if (0 != obj.getInt("code")) {
                        return;
                    }
                    Gson gson = new Gson();
                    UserAroundResult result;
                    result = gson.fromJson(obj.getJSONObject("result").toString(), UserAroundResult.class);
                    List<UserAround> list = result.getList();
                    for (UserAround user : list) {
                        peopleAround.put(user.getUser_id(), user);
                    }
                    updateMarker();

                } catch (JSONException e) {
                    LogUtil.e(getContext(), "enter catch=" + e.toString());
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                ToastUtil.shortshow(getContext(), R.string.toast_error_net);
                LogUtil.e(getContext(), "HelperPeopleFragment,enter onFailure");
            }
        };
        RequestParams params = new RequestParams();
        if (UserInfo.getIsLogin(getContext())) {
            params.put("session_id", UserInfo.getSessionID(getContext()));
        }
        params.put("range", maxDistanceAroundLimit);
        params.put("limit", maxPeopleAroundLimit);
        params.put("latitude", latitude);
        params.put("longitude", longitude);
        AndroidAsyncHttp.get(url, params, res);
    }

    private void updatePeopleAround(double latitude, double longitude) {
        String url = ServerAPIConfig.AroundMe;
        AsyncHttpResponseHandler res = new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                String str_json = new String(bytes);
                try {
                    JSONObject obj = new JSONObject(str_json);
                    if (0 != obj.getInt("code")) {
                        LogUtil.e(getContext(), "getHelpData 数据解析错误:code=" + obj.getInt("code"));
                        return;
                    }
                    Gson gson = new Gson();
                    UserAroundResult result;
                    result = gson.fromJson(obj.getJSONObject("result").toString(), UserAroundResult.class);
                    List<UserAround> list = result.getList();
                    peopleAround.clear();
                    for (UserAround user : list) {
                        Log.i(TAG, "put new around people");
                        peopleAround.put(user.getUser_id(), user);
                    }
                    updateMarker();
                } catch (JSONException e) {
                    LogUtil.e(getContext(), "enter catch=" + e.toString());
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                ToastUtil.shortshow(getContext(), R.string.toast_error_net);
                LogUtil.e(getContext(), "HelperPeopleFragment,enter onFailure");
            }
        };
        RequestParams params = new RequestParams();
        params.put("session_id", sp.getString("session_id", null));
        params.put("range", maxDistanceAroundLimit);
        params.put("limit", maxPeopleAroundLimit);
        params.put("latitude", latitude);
        params.put("longitude", longitude);
        AndroidAsyncHttp.get(url, params, res);
    }

    ArrayList<String> ids = new ArrayList<>();
    ArrayList<LatLng> points = new ArrayList<>();
    ArrayList<String> userIds = new ArrayList<>();
    private void updateMarker() {
        mBaiduMap.clear();
        BitmapDescriptor bitmapDescriptor;
        ids.clear();
        points.clear();
        userIds.clear();
        String portrait;
        for (UserAround userAround : peopleAround.values()) {
            portrait = userAround.getPortrait();
            if(TextUtils.isEmpty(portrait)){
                ids.add("");
            }else {
                ids.add(portrait);
            }
            points.add(new LatLng(userAround.getLatitude(), userAround.getLongitude()));
            userIds.add(userAround.getUser_id());
        }
        for (int i = 0; i < userIds.size(); i++) {
            final int finalI = i;
            //先查询数据库有没有
            Cursor cursor = mySqlLite.query();
            String img_url = null;
            while (cursor.moveToNext()) {
                //使用GetString获取列中的值。参数为使用cursor.getColumnIndex("name")获取的序号。
                String img_id = cursor.getString(cursor.getColumnIndex("img_id"));
                if (img_id.equals(ids.get(finalI))) {
                    img_url = cursor.getString(cursor.getColumnIndex("img_url"));
                }
            }
            cursor.close();
            if (!TextUtils.isEmpty(img_url) && getContext()!= null) {
                View view = View.inflate(getContext(), R.layout.map_marker, null);
                CircleImageView cir_ava = (CircleImageView) view.findViewById(R.id.cir_ava);
                Picasso.with(getContext()).load(img_url).config(Bitmap.Config.RGB_565).placeholder(R.mipmap.ic_defualtavater).into(cir_ava);
                bitmapDescriptor = BitmapDescriptorFactory.fromView(view);
                //构建MarkerOption，用于在地图上添加Marker
                OverlayOptions option = new MarkerOptions()
                        .position(points.get(finalI))
                        .title(userIds.get(finalI))
                        .icon(bitmapDescriptor);
                //在地图上添加Marker，并显示
                mBaiduMap.addOverlay(option);
            } else {
                if(TextUtils.isEmpty(ids.get(i))){
                    View view = View.inflate(getContext(), R.layout.map_marker, null);
                    CircleImageView cir_ava = (CircleImageView) view.findViewById(R.id.cir_ava);
                    cir_ava.setImageResource(R.mipmap.ic_defualtavater);
                    BitmapDescriptor bitmapDescriptor2 = BitmapDescriptorFactory.fromView(view);
                    //构建MarkerOption，用于在地图上添加Marker
                    OverlayOptions option = new MarkerOptions()
                            .position(points.get(finalI))
                            .title(userIds.get(finalI))
                            .icon(bitmapDescriptor2);
                    //在地图上添加Marker，并显示
                    mBaiduMap.addOverlay(option);
                }else {
                    QiniuUtils.getOne7NiuIMGUrl(getContext(), ids.get(i), new QiniuUtils.GetOneUrlCallBack() {
                        @Override
                        public void getUrlCallBack(String imgUrl) {
                            View view = View.inflate(getContext(), R.layout.map_marker, null);
                            CircleImageView cir_ava = (CircleImageView) view.findViewById(R.id.cir_ava);
                            BitmapDescriptor bitmapDescriptor = BitmapDescriptorFactory.fromView(view);
                            //构建MarkerOption，用于在地图上添加Marker
                            OverlayOptions option = new MarkerOptions()
                                    .position(points.get(finalI))
                                    .title(userIds.get(finalI))
                                    .icon(bitmapDescriptor);
                            //在地图上添加Marker，并显示
                            mBaiduMap.addOverlay(option);
                            Picasso.with(getContext())
                                    .load(imgUrl)
                                    .config(Bitmap.Config.RGB_565)
                                    .placeholder(R.mipmap.ic_defualtavater)
                                    .error(R.mipmap.ic_defualtavater)
                                    .into(cir_ava);
                            ContentValues contentValues = new ContentValues();
                            contentValues.put("img_url", imgUrl);
                            contentValues.put("img_id", ids.get(finalI));
                            mySqlLite.insert(contentValues);
                        }
                    });
                }
            }
        }
        mBaiduMap.setOnMarkerClickListener(new BaiduMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                if (NUM == 0) {
                    //检查用户是否登录,未登录则进入登录界面;
                    if (sp.getBoolean("isLogined", false) == false) {
                        startActivity(LoginActivity.class);
                        NUM++;
                    } else {
                        UserAround user = peopleAround.get(marker.getTitle());
                        if (user == null) {
                            return false;
                        }
                        mCurUser = user;
                        Bundle bundle = new Bundle();
                        bundle.putString("user_id", mCurUser.getUser_id());
                        bundle.putString("nickName", mCurUser.getNickname());
                        Intent intent = new Intent(getContext(), LookOthersMassageActivity.class);
                        intent.putExtras(bundle);
                        startActivity(intent);
                        NUM++;
                    }
                }
                return true;
            }
        });
    }


    /**
     * 初始化Dialog
     */
    private void initDialog() {
        /**悬赏类型对话框*/
        dialog_HelpType = new Dialog(getContext(), R.style.dialog_transparent);
        dialog_HelpType.setContentView(dialogView_HelpType);
        WindowManager.LayoutParams layoutParams_HelpTye = dialog_HelpType.getWindow().getAttributes();
        layoutParams_HelpTye.gravity = Gravity.BOTTOM;
        layoutParams_HelpTye.width = getActivity().getWindowManager().getDefaultDisplay().getWidth();
        layoutParams_HelpTye.y = 0;
        dialog_HelpType.getWindow().setAttributes(layoutParams_HelpTye);

        /**绑定手机号对话框*/
        dialog_Phone = new Dialog(getContext(), R.style.dialog_transparent);
        dialog_Phone.setContentView(dialogView_Phone);
        WindowManager.LayoutParams layoutParams_Phone = dialog_Phone.getWindow().getAttributes();
        layoutParams_Phone.width = (int) (getActivity().getWindowManager().getDefaultDisplay().getWidth() * 0.9);
        layoutParams_Phone.gravity = Gravity.CENTER;
        dialog_Phone.getWindow().setAttributes(layoutParams_HelpTye);

        /**绑定手机号对话框*/
        dilog_EmergencyPeople = new Dialog(getContext(), R.style.dialog_transparent);
        dilog_EmergencyPeople.setContentView(dilogView_EmergencyPeople);
        WindowManager.LayoutParams layoutParams_EmergencyPeople = dilog_EmergencyPeople.getWindow().getAttributes();
        layoutParams_EmergencyPeople.width = (int) (getActivity().getWindowManager().getDefaultDisplay().getWidth() * 0.9);
        layoutParams_EmergencyPeople.gravity = Gravity.CENTER;
        dilog_EmergencyPeople.getWindow().setAttributes(layoutParams_EmergencyPeople);

        /**实名认证对话框*/
        dialog_RealName = new Dialog(getContext(), R.style.dialog_transparent);
        dialog_RealName.setContentView(dialogView_RealName);
        WindowManager.LayoutParams layoutParams_RealName = dialog_Phone.getWindow().getAttributes();
        layoutParams_RealName.width = (int) (getActivity().getWindowManager().getDefaultDisplay().getWidth() * 0.9);
        layoutParams_RealName.gravity = Gravity.CENTER;
        dialog_RealName.getWindow().setAttributes(layoutParams_RealName);

        /**SOS警告对话框*/
        dialog_SOSWarning = new Dialog(getContext(), R.style.dialog_transparent);
        dialog_SOSWarning.setContentView(dialogView_SOSWarning);
        WindowManager.LayoutParams layoutParams_SOSWarning = dialog_Phone.getWindow().getAttributes();
        layoutParams_SOSWarning.width = (int) (getActivity().getWindowManager().getDefaultDisplay().getWidth() * 0.9);
        layoutParams_SOSWarning.gravity = Gravity.CENTER;
        dialog_SOSWarning.getWindow().setAttributes(layoutParams_SOSWarning);
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    public void onResume() {
        mMapView.onResume();
        super.onResume();
    }

    @Override
    public void onStop() {
        super.onStop();
        NUM = 0;
        LogUtil.e("--HelperMapFragment------onStop()------");
    }
    @Override
    public void onDestroy() {
        // 关闭定位图层
        super.onDestroy();
        mBaiduMap.setMyLocationEnabled(false);
        mMapView.onDestroy();
        mMapView = null;
        getActivity().unregisterReceiver(mReceiver);
        myOrientationListener.stop();
        EventBus.getDefault().unregister(this);
    }

    private void initEvent() {
        ll_User_HelperMapFragment.setOnClickListener(this);
        iv_search.setOnClickListener(this);
        iv_GetLocation.setOnClickListener(this);
        bt_net_help.setOnClickListener(this);
        bt_real_help.setOnClickListener(this);
        bt_cancle.setOnClickListener(this);
        bt_confirm_dialog_phone.setOnClickListener(this);
        bt_cancle_dialog_phone.setOnClickListener(this);
        bt_confirm_dialog_real_name.setOnClickListener(this);
        bt_continue_dialog_real_name.setOnClickListener(this);
        bt_confirm_dialog_sos_warning.setOnClickListener(this);
        bt_cancle_dialog_sos_warning.setOnClickListener(this);
        bt_confirm_dialog_emergency.setOnClickListener(this);
        bt_cancle_dialog_emergency.setOnClickListener(this);

        IntentFilter filter = new IntentFilter(AppConstants.INTENT_ACTION_LOGIN_TOMAP);
        filter.addAction(AppConstants.INTENT_ACTION_SOS);
        getActivity().registerReceiver(mReceiver, filter);
    }


    private void initView() {
        inflater = LayoutInflater.from(getContext());
        sp = getContext().getSharedPreferences(StaticBean.USER_INFO, getContext().MODE_PRIVATE);
        ll_User_HelperMapFragment = (ImageView) v.findViewById(R.id.iv_user_avatar);
        iv_search = (ImageView) v.findViewById(R.id.iv_search);
        iv_GetLocation = (ImageView) v.findViewById(R.id.iv_GetLocation);
        mMapView = (MapView) v.findViewById(R.id.mMapView);
        mBaiduMap = mMapView.getMap();
        v.findViewById(R.id.iv_Help).setOnClickListener(this);
        iv_Help = v.findViewById(R.id.iv_Help);
        iv_emergency_calling = v.findViewById(R.id.iv_emergency_calling);
        iv_emergency_calling.setOnClickListener(this);
        tv_show_location = ((TextView) v.findViewById(R.id.tv_show_location));

        /**选择悬赏类型对话框相关控件*/
        dialogView_HelpType = inflater.inflate(R.layout.layout_dialog_helptype, null);
        bt_net_help = (Button) dialogView_HelpType.findViewById(R.id.bt_net_help);
        bt_real_help = (Button) dialogView_HelpType.findViewById(R.id.bt_real_help);
        bt_cancle = (Button) dialogView_HelpType.findViewById(R.id.bt_cancle);

        /**绑定手机号对话框*/
        dialogView_Phone = inflater.inflate(R.layout.layout_dialog_phone, null);
        bt_confirm_dialog_phone = (Button) dialogView_Phone.findViewById(R.id.bt_confirm_dialog_phone);
        bt_cancle_dialog_phone = (Button) dialogView_Phone.findViewById(R.id.bt_cancle_dialog_phone);


        /**绑定紧急联系人对话框*/
        dilogView_EmergencyPeople = inflater.inflate(R.layout.layout_dialog_emergencypeople, null);
        bt_confirm_dialog_emergency = (Button) dilogView_EmergencyPeople.findViewById(R.id.bt_confirm_dialog_emergency);
        bt_cancle_dialog_emergency = (Button) dilogView_EmergencyPeople.findViewById(R.id.bt_cancle_dialog_emergency);


        /**实名认证对话框*/
        dialogView_RealName = inflater.inflate(R.layout.layout_dialog_real_name, null);
        bt_confirm_dialog_real_name = (Button) dialogView_RealName.findViewById(R.id.bt_confirm_dialog_real_name);
        bt_continue_dialog_real_name = (Button) dialogView_RealName.findViewById(R.id.bt_continue_dialog_real_name);

        /**SOS警告对话框*/
        dialogView_SOSWarning = inflater.inflate(R.layout.layout_dialog_sos_warning, null);
        bt_confirm_dialog_sos_warning = (Button) dialogView_SOSWarning.findViewById(R.id.bt_confirm_dialog_sos_warning);
        bt_cancle_dialog_sos_warning = (Button) dialogView_SOSWarning.findViewById(R.id.bt_cancle_dialog_sos_warning);

        intent_Gallery = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent_Gallery.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        intent_Camera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if(msg.what == LOCATION_SELF){
                Log.i("baidu","--------30s定位结束--------locationClient.stop()");
                myApp.stopLocationClient();
                myApp.isMapOnLocation = false;
                timer.cancel();
                timer.purge();
                timer = null;
            }
        }
    };


    /**
     * 初始化百度地图
     */
    public void initBaiduMap() {
        myApp = PinApplication.getMyApp();
        mSearch = myApp.getmSearch();// 初始化搜索模块，注册事件监听
        mMapView.showZoomControls(false);//设置不再显示放大缩小按钮
        mBaiduMap.setMapStatus(MapStatusUpdateFactory.newMapStatus(new MapStatus.Builder().zoom(16).build()));//设置百度地图缩放等级为16级
        mBaiduMap.setMyLocationEnabled(true); // 开启定位图层
        setFollowing();
        initOritationListener();// 初始化传感器
        //触碰地图时监听
        mBaiduMap.setOnMapTouchListener(new BaiduMap.OnMapTouchListener() {
            @Override
            public void onTouch(MotionEvent motionEvent) {
                switch (motionEvent.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        myApp.startLocationClient();
                        Log.i("baidu", "-------触碰地图---30s定位开始------locationClient.start()");
                        myApp.isMapOnLocation = true;

                        if(HelperMapFragment.this.timer != null){
                            HelperMapFragment.this.timer.cancel();
                        }
                        HelperMapFragment.this.timer = new Timer();
                        HelperMapFragment.this.timer.schedule(new TimerTask() {
                            @Override
                            public void run() {
                                Message message = Message.obtain();
                                message.what = LOCATION_SELF;
                                handler.sendMessage(message);
                            }
                        }, 30000,30000);

                        if (mCurrentMode != LocationMode.NORMAL) {
                            setNormal();
                        }
                        break;
                }

            }
        });
        mBaiduMap.setOnMapStatusChangeListener(new BaiduMap.OnMapStatusChangeListener() {
            @Override
            public void onMapStatusChangeStart(MapStatus mapStatus) {
            }
            @Override
            public void onMapStatusChange(MapStatus mapStatus) {
            }

            @Override
            public void onMapStatusChangeFinish(MapStatus mapStatus) {//地图改变完成

                //获取中心点坐标
                selectedLatLng = mapStatus.target;
                getAddress(selectedLatLng);
                if (oldSelectedLatLng != null) {
                    MAP_CHANGE_NUM++;
                    //double distance = MapUtils.getDistance(selectedLatLng.latitude, selectedLatLng.longitude, oldSelectedLatLng.latitude, oldSelectedLatLng.longitude);
                    //if (distance > 200) {
                    Log.i("baidu", "-----------onMapStatusChangeFinish---------selectedLatLng.latitude:" + selectedLatLng.latitude + "       selectedLatLng.longitude: " + selectedLatLng.longitude);
                    updatePeopleAround(selectedLatLng.latitude, selectedLatLng.longitude);
                    oldSelectedLatLng = mapStatus.target;
                    //}
                }
                if (MAP_CHANGE_NUM == 0) {
                    oldSelectedLatLng = mapStatus.target;
                }

            }
        });
    }
    /**
     * @param latLng
     */
    public void getAddress(LatLng latLng) {

        ReverseGeoCodeOption reverseGeoCodeOption = new ReverseGeoCodeOption();
        reverseGeoCodeOption.location(latLng);
        if(mSearch == null){
            mSearch = myApp.getmSearch();
        }
        mSearch.reverseGeoCode(reverseGeoCodeOption);
        // 设置查询结果监听者
        mSearch.setOnGetGeoCodeResultListener(new OnGetGeoCoderResultListener() {
            @Override
            public void onGetGeoCodeResult(GeoCodeResult geoCodeResult) {
            }
            @Override
            public void onGetReverseGeoCodeResult(ReverseGeoCodeResult reverseGeoCodeResult) {
                addressDetail = reverseGeoCodeResult.getAddressDetail();
                Log.e("helpmap","addressDetail:"+addressDetail);
                if(addressDetail!= null){
                    tv_show_location.setText(addressDetail.city + "  " + addressDetail.street + "  " + addressDetail.streetNumber);
                }
            }
        });
    }

    /**
     * 初始化方向传感器
     */
    private void initOritationListener() {
        myOrientationListener = new MyOrientationListener(getContext());
        myOrientationListener
                .setOnOrientationListener(new MyOrientationListener.OnOrientationListener() {
                    @Override
                    public void onOrientationChanged(float x) {
                        mXDirection = (int) x;
                        // 构造定位数据
                        MyLocationData locData = new MyLocationData.Builder()
                                .accuracy(mCurrentAccracy)
                                // 此处设置开发者获取到的方向信息，顺时针0-360
                                .direction(mXDirection)
                                .latitude(mCurrentLantitude)
                                .longitude(mCurrentLongitude).build();
                        // 设置定位数据
                        mBaiduMap.setMyLocationData(locData);
                        MyLocationConfiguration config = new MyLocationConfiguration(
                                mCurrentMode, true, null);
                        mBaiduMap.setMyLocationConfigeration(config);
                    }
                });
        myOrientationListener.start();
    }





    /**
     * SOS紧急求助
     */
    private void onSOS() {
        //检查用户是否登录,未登录则return;
        if (sp.getBoolean("isLogined", false) == false) {
            ToastUtil.shortshow(getContext(), R.string.toast_non_login);
            return;
        }
        //检查用户是否绑定手机号
        if (null == sp.getString("mobile", null) || sp.getString("mobile", null).length() != 11) {
            dialog_Phone.show();
            return;
        }
        //检查是否设置紧急联系姓名及电话
        if (null == sp.getString("emergency_name", null) || 0 == sp.getString("emergency_name", null).length()
                || null == sp.getString("emergency_mobile", null) || 0 == sp.getString("emergency_mobile", null).length()) {
            dilog_EmergencyPeople.show();
            return;
        }

        Vibrator vibrator = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);
        vibrator.vibrate(500);

        if (sosSource == 1) {
            if (UserInfo.getCreditBalance(getContext()) <= 0) {//?
                startActivity(new Intent(getContext(), SOSCountdownActivity.class));
            } else {
                dialog_SOSWarning.show();
            }
        } else if (sosSource >= 2) {
            startActivity(new Intent(getContext(), SOSCountdownActivity.class));
        }

    }


    @Override
    public void onClick(View v) {
        Button.OnClickListener positiveListener;
        switch (v.getId()) {

            case R.id.iv_search://搜索
                if (sp.getBoolean("isLogined", false) == false) {
                    ToastUtil.shortshow(getContext(), R.string.toast_non_login);
                    return;
                }
                Intent search_intent = new Intent(getActivity(), SearchActivity.class);
                Bundle search_bundle = new Bundle();
                if (selectedLatLng != null) {
                    search_bundle.putDouble("latitude", selectedLatLng.latitude);
                    search_bundle.putDouble("longitude", selectedLatLng.longitude);
                }
                search_intent.putExtras(search_bundle);
                startActivity(search_intent);
                break;
            //点击紧急联系人对话框确定按钮
            case R.id.bt_confirm_dialog_emergency:
                startActivity(SettingEmergencyPersonActivity.class);
                dilog_EmergencyPeople.dismiss();
                break;
            //点击紧急联系人对话框取消按钮
            case R.id.bt_cancle_dialog_emergency:
                dilog_EmergencyPeople.dismiss();
                break;
            //点击显示侧滑菜单按钮:ll_User_HelperMapFragment
            case R.id.iv_user_avatar:
                Intent intent = new Intent(AppConstants.INTENT_ACTION_OPENDRAWER);
                getActivity().sendBroadcast(intent);
                break;
            //紧急求助
            case R.id.iv_emergency_calling:
                // 加载动画
              /*  Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.scales_magnify);
                iv_emergency_calling.startAnimation(animation);*/
                switch (UserInfo.getSOSStatus(getActivity())) {//sos状态，0-发布，1-我安全了
                    //发布状态
                    case 0:
                        positiveListener = new Button.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                startActivity(SOSSafeActivity.class);
                            }
                        };
                        CommonDialog.show(getActivity(), "确定", "取消", getString(R.string.dialog_already_sos_ongoing), positiveListener);
                        break;
                    //我安全了状态
                    case 1:
                        positiveListener = new Button.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Bundle bundle_SOS = new Bundle();
                                bundle_SOS.putString("id", UserInfo.getSOSId(getActivity()));
                                startActivity(MyHelperSOSDetailActivity.class, bundle_SOS);
                            }
                        };
                        CommonDialog.show(getActivity(), "确定", "取消", getString(R.string.dialog_already_safe_sos_ongoing), positiveListener);
                        break;
                    default:
                        sosSource = 1;
                        onSOS();
                        break;
                }
                break;
            //点击SOS警告对话框中的确定按钮
            case R.id.bt_confirm_dialog_sos_warning:
                dialog_SOSWarning.dismiss();
                startActivity(new Intent(getContext(), SOSCountdownActivity.class));
                break;
            //点击SOS警告对话框中的取消按钮
            case R.id.bt_cancle_dialog_sos_warning:
                dialog_SOSWarning.dismiss();
                break;
            //点击实名认证对话框中的确定按钮
            case R.id.bt_confirm_dialog_real_name:
                dialog_RealName.dismiss();
                startActivity(new Intent(getContext(), IdentityActivity.class));
                break;
            //点击实名认证对话框中的继续按钮
            case R.id.bt_continue_dialog_real_name:
                dialog_RealName.dismiss();
                startActivity(new Intent(getContext(), SOSCountdownActivity.class));
                break;
            //点击绑定手机对话框中的确定按钮
            case R.id.bt_confirm_dialog_phone:
                dialog_Phone.dismiss();
                startActivity(new Intent(getContext(), BundlePhoneActivity.class));
                break;
            //点击绑定手机对话框中的取消按钮
            case R.id.bt_cancle_dialog_phone:
                dialog_Phone.dismiss();
                break;
            //网络悬赏
            case R.id.bt_net_help:
                Intent intent1 = new Intent(getContext(), HelpPublishOnlineActivity.class);
                startActivity(intent1);
                //startActivity(new Intent(getContext(), HelpOnlineUpdataActivity.class));
                dialog_HelpType.dismiss();
                break;
            //线下悬赏
            case R.id.bt_real_help:
                //intent = new Intent(getContext(), HelpOfflineUpdataActivity.class);
                intent = new Intent(getContext(), HelpPublishOfflineActivity.class);
                Bundle bundle = new Bundle();
                bundle.putParcelable("addressDetail", addressDetail);
                bundle.putDouble("latitude", selectedLatLng.latitude);
                bundle.putDouble("longitude", selectedLatLng.longitude);
                intent.putExtras(bundle);
                startActivity(intent);
                dialog_HelpType.dismiss();
                break;
            //点击取消
            case R.id.bt_cancle:
                dialog_HelpType.dismiss();
                break;
            //点击求助按钮
            case R.id.iv_Help:
                //检查用户是否登录,未登录则return;已经实名认证的用户才可以举报
                if (sp.getBoolean("isLogined", false) == false) {
                    ToastUtil.shortshow(getContext(), R.string.toast_non_login);
                    return;
                }
                dialog_HelpType.show();
               /* if(animation3 == null){
                    animation3 = AnimationUtils.loadAnimation(getContext(), R.anim.dialog_enter);
                }
                dialogView_HelpType.startAnimation(animation3);*/
                break;

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
     * 设置地图为跟随模式
     * 默认旋转角度0,俯视角度0
     */
    private void setFollowing() {
        perfomRotate(0);//将旋转角度置0
        perfomOverlook(0);//将俯视角度置0
        mCurrentMode = LocationMode.FOLLOWING;
        iv_GetLocation.setImageResource(R.drawable.iv_getlocation_selector_following);
        //null表示使用百度地图自带的默认的图标
        mBaiduMap.setMyLocationConfigeration(
                new MyLocationConfiguration(mCurrentMode, true, null));
    }

    /**
     * 设置地图为罗盘模式
     */
    private void setCompass() {
        mCurrentMode = LocationMode.COMPASS;
        iv_GetLocation.setImageResource(R.drawable.iv_getlocation_selector_compass);
        mBaiduMap.setMyLocationConfigeration(
                new MyLocationConfiguration(mCurrentMode, true, null));
    }

    /**
     * 设置地图为普通模式
     */
    private void setNormal() {
        mCurrentMode = LocationMode.NORMAL;
        iv_GetLocation.setImageResource(R.drawable.iv_getlocation_selector_normal);
        mBaiduMap.setMyLocationConfigeration(
                new MyLocationConfiguration(mCurrentMode, true, null)
        );
    }








}