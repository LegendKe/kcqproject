package com.djx.pin.activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

import com.djx.pin.R;
import com.djx.pin.application.PinApplication;
import com.djx.pin.base.OldBaseActivity;
import com.djx.pin.beans.StaticBean;
import com.djx.pin.beans.UserInfo;
import com.djx.pin.serverapiconfig.ServerAPIConfig;
import com.djx.pin.utils.AndroidAsyncHttp;
import com.djx.pin.utils.LogUtil;
import com.djx.pin.utils.ToastUtil;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class SOSSafeActivity extends OldBaseActivity {
    protected final static String TAG = SOSSafeActivity.class.getSimpleName();
    private Context CONTEXT=SOSSafeActivity.this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sos_safe);
        initView();
        PinApplication.getMyApp().startSOSUpLoadService();
    }

    private void initView() {

        findViewById(R.id.bt_safe).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UpdataInfo();
            }
        });
    }


    /**
     * 调用接口上传至服务器我安全了
     */
    public void UpdataInfo(){
        SharedPreferences sp=getSharedPreferences(StaticBean.USER_INFO,MODE_PRIVATE);
        if(null==sp.getString("session_id",null)||sp.getString("session_id",null).length()==0){
            ToastUtil.shortshow(CONTEXT,R.string.toast_non_login);
            startActivity(MainActivity.class);
            SOSSafeActivity.this.finish();
            return;
        }
        AsyncHttpResponseHandler res=new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                String str_json=new String(bytes);
                try {
                    JSONObject obj=new JSONObject(str_json);
                    if(0!=obj.getInt("code")){
                        ToastUtil.errorCode(SOSSafeActivity.this,obj.getInt("code"));
                        startActivity(MainActivity.class);
                        SOSSafeActivity.this.finish();
                        return;
                    }
                    UserInfo.clearSOSOngoing(SOSSafeActivity.this);
                    PinApplication.getMyApp().stopSOSUpLoadService();
                    startActivity(MainActivity.class);
                    SOSSafeActivity.this.finish();
                } catch (JSONException e) {
                    startActivity(MainActivity.class);
                    SOSSafeActivity.this.finish();
                    LogUtil.e(CONTEXT,"enter catch");
                    e.printStackTrace();
                }
            }
            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                ToastUtil.shortshow(CONTEXT, R.string.toast_error_net);
            }
        };
        RequestParams params=new RequestParams();
        params.put("session_id",sp.getString("session_id",null));
        params.put("id",UserInfo.getSOSId(SOSSafeActivity.this));
        AndroidAsyncHttp.post(ServerAPIConfig.Updata_SOS_Safe,params,res);
    }


}
