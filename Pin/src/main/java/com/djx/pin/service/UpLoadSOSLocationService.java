package com.djx.pin.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;

import com.baidu.location.BDLocation;
import com.djx.pin.application.PinApplication;
import com.djx.pin.beans.EventBeans;
import com.djx.pin.beans.StaticBean;
import com.djx.pin.beans.UserInfo;
import com.djx.pin.serverapiconfig.ServerAPIConfig;
import com.djx.pin.utils.AndroidAsyncHttp;
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

/**
 * Created by Administrator on 2016/8/19 0019.
 */
public class UpLoadSOSLocationService extends Service {

    private long oldTime = 0;
    private Timer timer;
    private PinApplication myApp;
    private BDLocation oldUpLoadLocation;//上一次上传自己位置时的位置
    private int is_show_location;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        myApp = PinApplication.getMyApp();
        EventBus.getDefault().register(this);
        Log.i("baidu", "-sos----service创建----------onCreate()");
        myApp.isOnSOS = true;
    }

    @Subscribe(threadMode = ThreadMode.MainThread)
    public void onEvent(EventBeans eventBeans){
        if(eventBeans.style == 1){//表示已更新一次定位
            //停止定位
            is_show_location = getSharedPreferences(StaticBean.USER_INFO, Context.MODE_PRIVATE).getInt("is_show_location", 0);
            if (is_show_location ==0 && !myApp.isMapOnLocation) {//位置共享已关闭,才去控制定位 ; 否则若开启,者此时上传自己位置的服务肯定也在运行(10s定位一次,10s上传一次)
                myApp.stopLocationClient();
            }
        }
    }

    final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 1) {
                if(is_show_location == 0){
                    myApp.startLocationClient();
                }
                long duration = System.currentTimeMillis() - oldTime;

                if (duration > 10000 && myApp.getBdLocation() != null) {
                    Log.i("baidu","---------------SOS上传自己的位置信息-----------------");
                    if(oldUpLoadLocation == null){
                        upLoadSOSLocation(1);
                    }else {
                        if ((myApp.getBdLocation().getLatitude() == oldUpLoadLocation.getLatitude() && myApp.getBdLocation().getLongitude() == oldUpLoadLocation.getLongitude())) {//若最新定位位置和上一上传的的位置相同时,则不上传
                            return;
                        } else {
                            upLoadSOSLocation(1);//sos上传自己位置信息
                        }
                    }
                }

            }
        }
    };

    TimerTask timerTask;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //myApp.startLocationClient();
        //2.如果已登录且位置共享,每10s向服务器上传自己的最新位置
        upLoadSelfLocation();
        return super.onStartCommand(intent, flags, startId);
    }

    private void upLoadSelfLocation() {
        timer = new Timer(true);
        if (timerTask != null) {
            timerTask.cancel();
        }
        timerTask = new TimerTask() {
            @Override
            public void run() {
                Message msg = mHandler.obtainMessage();
                msg.what = 1;
                mHandler.sendMessage(msg);
            }
        };
        timer.schedule(timerTask, 0, 10000);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        if (timer != null) {
            timer.cancel();
            timer.purge();
            timer = null;
        }
        int is_show_location = getSharedPreferences(StaticBean.USER_INFO, Context.MODE_PRIVATE).getInt("is_show_location", 0);
        if (is_show_location ==0) {//位置共享已关闭,才去控制定位 ; 否则若开启,者此时上传自己位置的服务肯定也在运行(10s定位一次,10s上传一次)
            myApp.stopLocationClient();
        }
        Log.i("baidu", "---sos--service销毁----------onDestroy");
    }


    /**
     * @param userType 用户类型，1-发单者，2-接单者
     */
    private void upLoadSOSLocation(int userType) {
        Log.i("baidu", "service---sos更新上传自己位置信息-----");
        RequestParams params = new RequestParams();
        params.put("session_id", UserInfo.getSessionID(this));
        params.put("id", UserInfo.getSOSId(getApplicationContext()));
        BDLocation bdLocation = myApp.getBdLocation();
        oldUpLoadLocation = bdLocation;
        params.put("location", bdLocation.getAddrStr());
        params.put("latitude", bdLocation.getLatitude());
        params.put("longitude", bdLocation.getLongitude());
        params.put("user_type", userType);
        AndroidAsyncHttp.post(ServerAPIConfig.Do_UpdataLocation, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                String str_json = new String(bytes);
                try {
                    JSONObject obj = new JSONObject(str_json);
                    if (0 != obj.getInt("code")) {
                        return;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
            }
        });
    }


}
