package com.djx.pin.activity;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.telephony.SmsManager;
import android.text.TextUtils;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.djx.pin.R;
import com.djx.pin.application.PinApplication;
import com.djx.pin.base.OldBaseActivity;
import com.djx.pin.beans.IDTokenInfo;
import com.djx.pin.beans.StaticBean;
import com.djx.pin.beans.UserInfo;
import com.djx.pin.receiver.MyServiceReceiver;
import com.djx.pin.serverapiconfig.ServerAPIConfig;
import com.djx.pin.utils.AndroidAsyncHttp;
import com.djx.pin.utils.LogUtil;
import com.djx.pin.utils.ToastUtil;
import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.qiniu.android.http.ResponseInfo;
import com.qiniu.android.storage.UpCompletionHandler;
import com.qiniu.android.storage.UploadManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

public class SOSRecordActivity extends OldBaseActivity {

    protected final static String TAG = SOSRecordActivity.class.getSimpleName();

    TextView tv_countdown;
    Handler handler;//倒计时handler
    int second = 30;//倒计时时间
    String path_Record,//录音保存的绝对路径
            location;//用户的位置
    private MediaRecorder mediaRecorder;
    private Context CONTEXT = SOSRecordActivity.this;
    private BDLocation bdLocation;
    IDTokenInfo idTokenInfo_Audio;
    GeoCoder mSearch = null; // 搜索模块，也可去掉地图模块独立使用

    TimerTask timeTask;
    UploadManager uploadManager;
    boolean isOK_Audio;
    SharedPreferences sp;
    private static final int RECORD_PERM = 129;

    private boolean recording;
    private PinApplication myApp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sos_record);
        initView();
        initGeoCoder();
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        initRecordByPermissions();
    }

    private void initView() {
        sp = getSharedPreferences(StaticBean.USER_INFO, MODE_PRIVATE);
        tv_countdown = (TextView) findViewById(R.id.tv_countdown);
        String tempDir = PinApplication.getDefaultTempDir();
        path_Record = tempDir + "/" + System.currentTimeMillis() + ".amr";
        Log.i(TAG, "sos record path is: " + path_Record);
        timeTask = new TimerTask() {
            @Override
            public void run() {
                handler.sendEmptyMessage(0);
            }
        };

        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                tv_countdown.setText("" + second--);
                if (-1 == second) {
                    timeTask.cancel();
                    if (recording) {
                        mediaRecorder.setOnErrorListener(null);
                        mediaRecorder.setOnInfoListener(null);
                        mediaRecorder.setPreviewDisplay(null);
                        mediaRecorder.stop();// 停止录音
                        mediaRecorder.release();//释放mediaRecorder
                    }
                    //向服务器发送紧急求助
                    getIdToken_Audio();
                    Log.i("test","-1 == second");
                }
            }
        };
    }

    /**
     * 初始化录音
     */
    private void initRecord() {
        // new出MediaRecorder对象
        mediaRecorder = new MediaRecorder();
        // 设置MediaRecorder的音频源为麦克风
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        // 设置MediaRecorder录制的音频格式
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        // 设置MediaRecorder录制音频的编码为amr.
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);
        mediaRecorder.setOutputFile(path_Record);
        // 设置录制好的音频文件保存路径
        try {
            mediaRecorder.prepare();// 准备录制
            mediaRecorder.start();// 开始录制
            recording = true;
        } catch (Exception e) {
            Log.e(TAG, "initRecord enter catch");
            recording = false;
            ToastUtil.shortshow(this, "打开录音失败，请您检查录音权限是否打开");
            e.printStackTrace();
        }
    }


    /**
     * 实现倒计时功能
     */
    private void countDown() {
        new Timer().schedule(timeTask, 0, 1000);
    }

    /**
     * 上传求助信息到服务器
     *
     * @param type 1绑定芝麻信用且信用额度和信用值都大于0的,2表示未绑定芝麻信用的
     */
    private void updataInfo2Server(int type) {

        Log.i("test","updataInfo2Server--:");
        LogUtil.e("updataInfo2Server");
        /**将数据打包成json字符串*/
        JSONObject jsonObject = null;
        JSONArray jsonArray = null;
        StringEntity stringEntity = null;
        try {
            jsonObject = new JSONObject();
            jsonArray = new JSONArray();
            jsonObject.put("session_id", sp.getString("session_id", null));
            jsonObject.put("location", location);
            jsonObject.put("latitude", myApp.getBdLocation().getLatitude());
            jsonObject.put("longitude", myApp.getBdLocation().getLongitude());

            JSONObject obj = new JSONObject();
            obj.put("id", idTokenInfo_Audio.list.get(0).id);
            obj.put("media_type", 3);
            jsonArray.put(obj);
            jsonObject.put("media", jsonArray);
            stringEntity = new StringEntity(jsonObject.toString(), "UTF-8");
        } catch (Exception e) {
            LogUtil.e(CONTEXT, "enter catch");
            e.printStackTrace();
        }
        if (null == jsonObject || null == jsonArray || null == stringEntity) {
            LogUtil.e(CONTEXT, "null==jsonObject||null==jsonArray");
            return;
        }
        AsyncHttpResponseHandler res = new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                String str_json = new String(bytes);


                LogUtil.e("str_json_sos 发布=" + str_json);
                try {
                    JSONObject object = new JSONObject(str_json);
                    Log.i("test","updataInfo2Server---code:"+object.getInt("code"));
                    if (0 != object.getInt("code")) {
                        if (3102 == object.getInt("code")) {
                            ToastUtil.longshow(CONTEXT, R.string.toast_failer_sos_repeat);
                            startActivity(MainActivity.class);
                            return;
                        }
                        if (2013 == object.getInt("code")) {
                            ToastUtil.longshow(CONTEXT, R.string.toast_failer_sos1);
                            startActivity(MainActivity.class);
                            return;
                        }
                        if (2012 == object.getInt("code")) {
                            ToastUtil.longshow(CONTEXT, R.string.toast_failer_sos2);
                            startActivity(MainActivity.class);
                            return;
                        }
                        ToastUtil.longshow(CONTEXT, R.string.toast_failer_sos + " errorcode:" + object.getInt("code"));
                        return;
                    }
                    if (object.getInt("code")==0){

                        //发送短信通知紧急联系人
                        SendMassage(object.getJSONObject("result").getString("id"));
                        ToastUtil.longshow(CONTEXT, R.string.toast_success_sos);
                        sp.edit().putString("sos_id", object.getJSONObject("result").getString("id")).commit();
                        UserInfo.putSOSStatus(SOSRecordActivity.this,0);
                        startActivity(SOSSafeActivity.class);

                    }else {
                        errorCode(object.getInt("code"));



                        startActivity(MainActivity.class);
                    }
                    SOSRecordActivity.this.finish();
                } catch (JSONException e) {
                    LogUtil.e(CONTEXT, "enter catch");
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                LogUtil.e(CONTEXT, "网络连接失败");
                ToastUtil.shortshow(CONTEXT, R.string.toast_error_net);
            }
        };

        if (type == 1) {
            AndroidAsyncHttp.post(CONTEXT, ServerAPIConfig.Updata_SOS, stringEntity, "application/json;charset=UTF-8", res);
        } else {
            AndroidAsyncHttp.post(CONTEXT, ServerAPIConfig.Updata_SOS_SMS, stringEntity, "application/json;charset=UTF-8", res);

        }

    }


    /**
     * 获取音频需要的Token,并上传信息至七牛和公司服务器
     */
    public void getIdToken_Audio() {
        LogUtil.e("getIdToken_Audio");

        idTokenInfo_Audio = null;//重置
        RequestParams params = new RequestParams();
        params.put("size", 1);
        params.put("session_id", sp.getString("session_id", null));
        params.put("media_type", 3);
        AndroidAsyncHttp.post(ServerAPIConfig.GetIDToken, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                String str_json = new String(bytes);
                try {
                    JSONObject obj = new JSONObject(str_json);
                    if (0 != obj.getInt("code")) {
                        errorCode(obj.getInt("code"));
                        return;
                    }
                    idTokenInfo_Audio = new IDTokenInfo();
                    obj = obj.getJSONObject("result");
                    Gson gson = new Gson();
                    idTokenInfo_Audio = gson.fromJson(obj.toString(), IDTokenInfo.class);
                    updataAudio2Qiniu();
                } catch (JSONException e) {
                    LogUtil.e(CONTEXT, "进入catch异常");
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                ToastUtil.shortshow(CONTEXT, R.string.toast_error_net);
                LogUtil.e(CONTEXT, R.string.toast_error_net);
                idTokenInfo_Audio = null;

            }
        });

    }

    /**
     * 上传音频文件到七牛服务器
     */
    public void updataAudio2Qiniu() {
        LogUtil.e("updataAudio2Qiniu");

        if (null == idTokenInfo_Audio)
            return;
        isOK_Audio = false;//重置
        // 重用 uploadManager。一般地，只需要创建一个 uploadManager 对象
        if (null == uploadManager) {
            uploadManager = new UploadManager();
        }
        Log.i("test","path_Record:"+path_Record+"   ----idTokenInfo_Audio.list.get(0).id: "+idTokenInfo_Audio.list.get(0).id+"      idTokenInfo_Audio.list.get(0).token:  "+idTokenInfo_Audio.list.get(0).token);
        uploadManager.put(path_Record, idTokenInfo_Audio.list.get(0).id, idTokenInfo_Audio.list.get(0).token, new UpCompletionHandler() {
            @Override
            public void complete(String s, ResponseInfo responseInfo, JSONObject jsonObject) {
                if (responseInfo.isOK()) {
                    Log.i("test","updataAudio2Qiniu"+"responseInfo.isOK()------------------"+responseInfo.isOK());
                    isOK_Audio = true;
                    if (TextUtils.isEmpty(sp.getString("zhima_open_id", null)) || 0 == sp.getInt("credit", 0) || 0 == sp.getFloat("credit_balance", 0)) {
                        updataInfo2Server(2);
                    } else {
                        updataInfo2Server(1);
                    }
                } else {
                    isOK_Audio = false;
                    ToastUtil.longshow(SOSRecordActivity.this,getString(R.string.audio_failed));

                    if (TextUtils.isEmpty(sp.getString("zhima_open_id", null)) || 0 == sp.getInt("credit", 0) || 0 == sp.getFloat("credit_balance", 0)) {
                        updataInfo2Server(2);
                    } else {
                        updataInfo2Server(1);
                    }
                }
            }
        }, null);

    }


    /**
     * // 初始化经纬度与地理位置互转模块，注册事件监听
     */
    private void initGeoCoder() {
        myApp = PinApplication.getMyApp();
        mSearch = myApp.getmSearch();
        LatLng latLng = new LatLng(myApp.getBdLocation().getLatitude(), myApp.getBdLocation().getLongitude());
        ReverseGeoCodeOption option = new ReverseGeoCodeOption().location(latLng);
        mSearch.reverseGeoCode(option);
        mSearch.setOnGetGeoCodeResultListener(new OnGetGeoCoderResultListener() {
            @Override
            public void onGetGeoCodeResult(GeoCodeResult geoCodeResult) {

            }
            @Override
            public void onGetReverseGeoCodeResult(ReverseGeoCodeResult result) {
                if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
                    Toast.makeText(SOSRecordActivity.this, "抱歉，未能找到结果", Toast.LENGTH_LONG).show();
                    return;
                }
                SOSRecordActivity.this.location = result.getAddressDetail().city + "  " + result.getAddressDetail().street + "  " + result.getAddressDetail().streetNumber;
            }
        });
    }




    private String SMS_SEND_ACTIOIN = "SMS_SEND_ACTIOIN";
    private String SMS_DELIVERED_ACTION = "SMS_DELIVERED_ACTION";

    MyServiceReceiver mReceiver02, mReceiver01;

    private void SendMassage(String id) {
        /* 自定义IntentFilter为SENT_SMS_ACTIOIN Receiver */
        IntentFilter mFilter01;
        mFilter01 = new IntentFilter(SMS_SEND_ACTIOIN);
        mReceiver01 = new MyServiceReceiver();
        registerReceiver(mReceiver01, mFilter01);

/* 自定义IntentFilter为DELIVERED_SMS_ACTION Receiver */
        mFilter01 = new IntentFilter(SMS_DELIVERED_ACTION);
        mReceiver02 = new MyServiceReceiver();
        registerReceiver(mReceiver02, mFilter01);


/* 创建自定义Action常数的Intent(给PendingIntent参数之用) */
        Intent itSend = new Intent(SMS_SEND_ACTIOIN);

/* sentIntent参数为传送后接受的广播信息PendingIntent */
        PendingIntent mSendPI = PendingIntent.getBroadcast(
                getApplicationContext(),
                (int) System.currentTimeMillis(), itSend,
                PendingIntent.FLAG_UPDATE_CURRENT);

        Intent itDeliver = new Intent(SMS_DELIVERED_ACTION);
    /* deliveryIntent参数为送达后接受的广播信息PendingIntent */
        PendingIntent mDeliverPI = PendingIntent.getBroadcast(
                getApplicationContext(),
                (int) System.currentTimeMillis(), itDeliver,
                PendingIntent.FLAG_UPDATE_CURRENT);
        // 发送短信
        SmsManager smsManager = SmsManager.getDefault();
        String phoneNumber = getSharedPreferences(StaticBean.USER_INFO, Context.MODE_PRIVATE).getString("emergency_mobile", null);

        String massage = "【众觅】您好，我是『众觅』平台专属客服，您的朋友"+getSharedPreferences(StaticBean.USER_INFO,Context.MODE_PRIVATE).getString("real_name",null)+"（手机尾号"+getSharedPreferences(StaticBean.USER_INFO,Context.MODE_PRIVATE).getString("mobile",null)+"）正在求助，可能遇到了危险，现场位置信息及录音请见链接："+ServerAPIConfig.SOSShare+id+"请您尽快施以援助。";

        ArrayList<String> list = smsManager.divideMessage(massage);
//        PendingIntent pIntent = PendingIntent.getActivity(this, 0, new Intent(this, SettingEmergencyPersonActivity.class), 0);
        if (massage.length() > 70) {
            //拆分短信
            ArrayList<String> phoneList = smsManager.divideMessage(massage);
            ArrayList<PendingIntent> mSendPIList = new ArrayList<>();
            ArrayList<PendingIntent> mDeliverPIList = new ArrayList<>();
            mSendPIList.add(mSendPI);
            mDeliverPIList.add(mDeliverPI);
            //发送短信
            smsManager.sendMultipartTextMessage(phoneNumber, null, phoneList, mSendPIList, mDeliverPIList);
        } else {
            //不超过70字时使用sendTextMessage发送
            smsManager.sendTextMessage(phoneNumber, null, massage, mSendPI, mDeliverPI);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
          /* 取消注册自定义Receiver */
        if (mReceiver01 != null && mReceiver02 != null) {
            unregisterReceiver(mReceiver01);
            unregisterReceiver(mReceiver02);
        }
    }

    @AfterPermissionGranted(RECORD_PERM)
    protected void initRecordByPermissions() {
        String[] perms = { Manifest.permission.SEND_SMS, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO};
        if (EasyPermissions.hasPermissions(this, perms)) {
            try {
                initRecord();
                countDown();
            } catch (Exception e) {
                Toast.makeText(this, R.string.permissions_record_error, Toast.LENGTH_LONG).show();
            }
        } else {
            // Request one permission
            EasyPermissions.requestPermissions(this,
                    getResources().getString(R.string.str_request_sos_message),
                    RECORD_PERM, perms);
        }
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {
        Log.i(TAG, "onPermissionsGranted");
        Log.i(TAG, perms.toString());
        String[] targetPerms = { Manifest.permission.SEND_SMS, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO};
        if (requestCode == RECORD_PERM) {
            try {
                if (EasyPermissions.hasPermissions(this, targetPerms)) {
                    try {
                        initRecord();
                        countDown();
                    } catch (Exception e) {
//                        Toast.makeText(this, R.string.permissions_record_error, Toast.LENGTH_LONG).show();
                    }
                }
            } catch (Exception e) {
                Toast.makeText(this, R.string.permissions_record_error, Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
        Log.i(TAG, "onPermissionsDenied");
        if (requestCode == RECORD_PERM) {
            Toast.makeText(this, R.string.permissions_record_error, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }
}
