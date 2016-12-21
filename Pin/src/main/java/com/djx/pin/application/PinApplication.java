package com.djx.pin.application;

import android.app.ActivityManager;
import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Environment;
import android.support.multidex.MultiDex;
import android.util.Log;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.djx.pin.beans.EventBeans;
import com.djx.pin.beans.StaticBean;
import com.djx.pin.beans.UserInfo;
import com.djx.pin.business.AppConstants;
import com.djx.pin.jpush.ExampleUtil;
import com.djx.pin.service.UpLoadSOSLocationService;
import com.djx.pin.service.UpLoadSelfLocationService;
import com.djx.pin.sql.MySQLLiteOpenHelper;
import com.djx.pin.utils.LogUtil;
import com.djx.pin.utils.myutils.ConstantUtils;
import com.umeng.socialize.PlatformConfig;

import java.io.File;

import cn.jpush.android.api.JPushInterface;
import de.greenrobot.event.EventBus;
import io.rong.imkit.RongIM;

/**
 * Created by lenovo on 2016/5/27.
 */
public class PinApplication extends Application {
    protected final static String TAG = PinApplication.class.getSimpleName();

    SharedPreferences sp;

    //极光推送极光推送极光推送极光推送极光推送极光推送
    private MessageReceiver mMessageReceiver;
    public static final String MESSAGE_RECEIVED_ACTION = "com.djx.pin.MESSAGE_RECEIVED_ACTION";
    public static final String KEY_TITLE = "title";
    public static final String KEY_MESSAGE = "message";
    public static final String KEY_EXTRAS = "extras";

    //极光推送极光推送极光推送极光推送极光推送极光推送

    private static PinApplication myApp;
    private RequestQueue queue;

    private GeoCoder mSearch;
    private LocationClient mLocationClient;
    private Intent intentSOS,intentSELF;
    public boolean isMapOnLocation = true;//地图页正在30秒倒计时
    public boolean isOnSOS;//地图页正在30秒倒计时

    public MySQLLiteOpenHelper getMySQLLiteOpenHelper() {
        return mySQLLiteOpenHelper;
    }

    private MySQLLiteOpenHelper mySQLLiteOpenHelper;


    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i(TAG, "action auto login");
        }
    };

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    @Override
    public void onCreate() {
        LogUtil.e("-------------PinApplication-应用开始--------onCreate()");
        super.onCreate();
        initBaidumap();
        login();
        this.myApp = this;
        queue = Volley.newRequestQueue(this);
        IntentFilter filter = new IntentFilter(AppConstants.INTENT_ACTION_AUTO_LOGIN);
        registerReceiver(mReceiver, filter);
        mySQLLiteOpenHelper = new MySQLLiteOpenHelper(this);

        intentSOS = new Intent(this,UpLoadSOSLocationService.class);
        intentSELF = new Intent(this,UpLoadSelfLocationService.class);
    }

    @Override
    public void onTerminate() {//程序终止时执行
        LogUtil.e("PinApplication---程序终止---onTerminate()-");
        super.onTerminate();
    }

    public void startSOSUpLoadService(){
        boolean isLogined = getSharedPreferences(StaticBean.USER_INFO, Context.MODE_PRIVATE).getBoolean("isLogined", false);
        if(isLogined){
            startService(intentSOS);
        }
    }
    public void startSelfUpLoadService(){
        boolean isLogined = getSharedPreferences(StaticBean.USER_INFO, Context.MODE_PRIVATE).getBoolean("isLogined", false);
        if(isLogined){
            startService(intentSELF);
        }
    }
    public void stopSelfUpLoadService(){
        stopService(intentSELF);
    }
    public void stopSOSUpLoadService(){
        stopService(intentSOS);
    }
    public void login() {
        initJpush();
        autoLogin();
        initRongIM();
        //rongAutoLogin();
        PlatformConfig.setSinaWeibo("4000354221", "23479352d2e4d5b6566d28915c14f123");
    }

    public static PinApplication getMyApp() {
        return myApp;
    }
    public RequestQueue getQueue() {
        return queue;
    }

    /**
     * 自动登录
     * 如果用户已经登录,则更新用户信息:
     * 更新失败说明session_id过期即登录过期,提示用户登录过期,并更改isLogined为false
     */
    private void autoLogin() {
        LogUtil.e("enter autoLogin");
        sp=getSharedPreferences(StaticBean.USER_INFO,MODE_PRIVATE);
        if(sp.getBoolean("isLogined",false)){
            UserInfo.getUserInfo(getApplicationContext(),sp.getString("session_id",null),sp.getString("user_id",null));
        }
    }

    /**
     * 初始化融云IM
     */
    private void initRongIM(){
        Log.i(TAG, "initRongIM");
        /**
         * OnCreate 会被多个进程重入，这段保护代码，确保只有您需要使用 RongIM 的进程和 Push 进程执行了 init。
         * io.rong.push 为融云 push 进程名称，不可修改。
         */
        if (getApplicationInfo().packageName.equals(getCurProcessName(getApplicationContext())) ||
                "io.rong.push".equals(getCurProcessName(getApplicationContext()))) {
            RongIM.init(this);
        }
    }

    public BDLocation getBdLocation() {
        return bdLocation;
    }

    public void setBdLocation(BDLocation bdLocation) {
        this.bdLocation = bdLocation;
    }

    public BDLocation bdLocation;

    /**
     * 初始化百度地图
     */
    private void initBaidumap() {

        SDKInitializer.initialize(this);
        //1.初始化检索
        mSearch = GeoCoder.newInstance();
        //2.初始化百度地图定位
        mLocationClient = new LocationClient(this);//声明LocationClient类
        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true);//打开GPS
        option.setCoorType("bd09ll");//设在坐标类型
        option.setScanSpan(1000);//可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于2000ms才是有效的
        option.setIsNeedAddress(true);//可选，设置是否需要地址信息，默认不需要
        mLocationClient.setLocOption(option);
        mLocationClient.registerLocationListener(new BDLocationListener() {
            @Override
            public void onReceiveLocation(BDLocation bdLocation) {
                Log.i("baidu","-------------------------appilcation--定位0--------------------------------");
                PinApplication.this.bdLocation = bdLocation;
                EventBeans eventBeans = new EventBeans(ConstantUtils.EVENT_LOCATION_FINISH, bdLocation);
                EventBus.getDefault().post(eventBeans);
            }
        });
    }

    public GeoCoder getmSearch() {
        return mSearch;
    }
    public LocationClient getmLocationClient() {
        return mLocationClient;
    }

    public void stopLocationClient(){
        Log.i("baidu","-------------------------appilcation--停止定位0--------------------------------");
        mLocationClient.stop();
    }
    public void startLocationClient(){
        if(!mLocationClient.isStarted()){
            mLocationClient.start();
        }
    }
    /**
     * 初始化极光推送
     * 1 获取极光推送设备唯一标识,如果获取失败,则默认为空字符串即""
     */
    private void initJpush(){
        Log.i(TAG, "initJpush");
        JPushInterface.setDebugMode(true); 	// 设置开启日志,发布时请关闭日志
        JPushInterface.init(this);     		// 初始化 JPush
        registerMessageReceiver();  // used for receive msg

        //获取极光推送设备唯一标识,如果获取失败,则默认为空字符串即""
        SharedPreferences sp=getSharedPreferences(StaticBean.USER_INFO,MODE_PRIVATE);
        SharedPreferences.Editor et = sp.edit();
        if(null==JPushInterface.getRegistrationID(this)||0==JPushInterface.getRegistrationID(this).length()){
            Log.i(TAG, "PinApplication,极光设备唯一标识RegistrationID获取失败");
            et.putString("JPush_RegistrationID","");
        }else{
            Log.i(TAG, "PinApplication,极光设备唯一标识RegistrationID获取成功");
            Log.i(TAG, "jpush id is --- " + JPushInterface.getRegistrationID(this));
            et.putString("JPush_RegistrationID",JPushInterface.getRegistrationID(this));
        }
        et.commit();
    }
    /**
     * 注册信息接收器
     */
    public void registerMessageReceiver() {
        mMessageReceiver = new MessageReceiver();
        IntentFilter filter = new IntentFilter();
        filter.setPriority(IntentFilter.SYSTEM_HIGH_PRIORITY);
        filter.addAction(MESSAGE_RECEIVED_ACTION);
        registerReceiver(mMessageReceiver, filter);
    }

    /**
     * 极光推送广播接收器
     */
    public class MessageReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i(TAG, "onReceive");
            if (MESSAGE_RECEIVED_ACTION.equals(intent.getAction())) {
                String messge = intent.getStringExtra(KEY_MESSAGE);
                String extras = intent.getStringExtra(KEY_EXTRAS);
                StringBuilder showMsg = new StringBuilder();
                showMsg.append(KEY_MESSAGE + " : " + messge + "\n");
                if (!ExampleUtil.isEmpty(extras)) {
                    showMsg.append(KEY_EXTRAS + " : " + extras + "\n");
                }
            }
        }
    }

    /**
     * 获得当前进程的名字
     *
     * @param context
     * @return 进程号
     */
    public static String getCurProcessName(Context context) {

        int pid = android.os.Process.myPid();

        ActivityManager activityManager = (ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE);

        for (ActivityManager.RunningAppProcessInfo appProcess : activityManager
                .getRunningAppProcesses()) {

            if (appProcess.pid == pid) {
                return appProcess.processName;
            }
        }
        return null;
    }

    public static String getDefaultTempDir() {
        String tempDir = Environment.getExternalStorageDirectory().toString() + "/zhongmi";
        File file = new File(tempDir);
        if (!file.exists()) {
            Log.w(TAG, tempDir + "is not exist, create it");
            file.mkdir();
        }
        return tempDir;
    }
}
