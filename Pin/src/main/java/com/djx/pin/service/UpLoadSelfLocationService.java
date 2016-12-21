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
import com.djx.pin.serverapiconfig.ServerAPIConfig;
import com.djx.pin.utils.AndroidAsyncHttp;
import com.djx.pin.utils.LogUtil;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Timer;
import java.util.TimerTask;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;
import de.greenrobot.event.EventBus;
import de.greenrobot.event.Subscribe;
import de.greenrobot.event.ThreadMode;

/**
 * Created by Administrator on 2016/8/19 0019.
 */
public class UpLoadSelfLocationService extends Service {

    private long oldTime = 0;
    private Timer timer;
    private PinApplication myApp;
    private BDLocation oldUpLoadLocation;//上一次上传自己位置时的位置

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
        Log.i("baidu","self_upload_service创建----------onCreate()");
    }

    @Subscribe(threadMode = ThreadMode.MainThread)
    public void onEvent(EventBeans eventBeans){
        if(eventBeans.style == 1){//表示已更新一次定位
            if(!myApp.isMapOnLocation){//如果地图页正在进行30秒倒计时定位,则不关闭
                myApp.stopLocationClient();
            }
        }
    }


    final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 1) {
                long duration = System.currentTimeMillis() - oldTime;
                if (duration > 10000 && myApp.getBdLocation() != null) {
                    myApp.startLocationClient();
                    if(oldUpLoadLocation == null){
                        upLoadLocation();
                    }else {
                        Log.i("baidu","根据位置上传自己的位置信息");
                        if ((myApp.getBdLocation().getLatitude() == oldUpLoadLocation.getLatitude() && myApp.getBdLocation().getLongitude() == oldUpLoadLocation.getLongitude())) {//若最新定位位置和上一上传的的位置相同时,则不上传
                            return;
                        } else {
                            upLoadLocation();//sos上传自己位置信息
                        }
                    }
                }
            }
        }
    };


    TimerTask timerTask;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        upLoadSelfLocation();
        return super.onStartCommand(intent, flags, startId);
    }

    private void upLoadSelfLocation() {
        boolean isLogined = getSharedPreferences(StaticBean.USER_INFO, Context.MODE_PRIVATE).getBoolean("isLogined", false);
        if (isLogined) {//如果已登录且位置共享,每10s向服务器上传自己的最新位置
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
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (timer != null) {
            timer.cancel();
            timer.purge();
            timer = null;
        }
        myApp.stopLocationClient();
        EventBus.getDefault().unregister(this);
        Log.i("baidu","self_upload_service销毁----------onDestroy");
    }

    /**
     * 用户更新定位
     */
    public void upLoadLocation() {
        LogUtil.e("-service中---位置已改变-------upLoadLocation---正在上传自己位置信息");
        String session_id = getSharedPreferences(StaticBean.USER_INFO, Context.MODE_PRIVATE).getString("session_id", null);
        String url = ServerAPIConfig.LocationShare;
        final JSONObject object = new JSONObject();
        try {
            object.put("session_id", session_id);
            object.put("is_show_location", 1);
            BDLocation bdLocation = myApp.getBdLocation();
            oldUpLoadLocation = bdLocation;
            object.put("latitude", bdLocation.getLatitude());
            object.put("longitude", bdLocation.getLongitude());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        StringEntity entity = new StringEntity(object.toString(), "utf-8");
        AsyncHttpResponseHandler res = new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                oldTime = System.currentTimeMillis();
            }
            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
            }
        };
        AndroidAsyncHttp.post(this, url, entity, "application/json;charset=UTF-8", res);
    }

}
