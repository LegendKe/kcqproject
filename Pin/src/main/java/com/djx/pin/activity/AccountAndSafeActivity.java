package com.djx.pin.activity;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.djx.pin.R;
import com.djx.pin.base.OldBaseActivity;
import com.djx.pin.beans.StaticBean;
import com.djx.pin.serverapiconfig.ServerAPIConfig;
import com.djx.pin.utils.AndroidAsyncHttp;
import com.djx.pin.utils.LogUtil;
import com.djx.pin.utils.ToastUtil;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

/**
 * Created by Administrator on 2016/6/29.
 */
public class AccountAndSafeActivity extends OldBaseActivity implements View.OnClickListener {

    private LinearLayout ll_Back_AASA, ll_BundlePhone_AASA, ll_ChangePassWord_AASA, ll_ChangeBundlePhone_AASA;
    private TextView tv_PhoneNumber_AASA;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accountandsafe);
        initView();
        initEvent();
        mobile = getSharedPreferences(StaticBean.USER_INFO, Context.MODE_PRIVATE).getString("mobile", null);
        if (mobile==null||mobile.equals("")){
            tv_PhoneNumber_AASA.setText("未绑定");
        }else {
            tv_PhoneNumber_AASA.setText(mobile);
        }
    }

    String mobile;

    @Override
    protected void onStart() {
        super.onStart();
        boolean isPhoneChanged = getSharedPreferences(StaticBean.KEY, Context.MODE_PRIVATE).getBoolean("isPhoneChanged", false);
        boolean isPhoneBundle = getSharedPreferences(StaticBean.KEY, Context.MODE_PRIVATE).getBoolean("isPhoneBundle", false);

        if (isPhoneChanged) {
            getUserInfo(AccountAndSafeActivity.this);
        }

        if (isPhoneBundle){
            getUserInfo(AccountAndSafeActivity.this);

        }
    }

    private void initEvent() {
        ll_Back_AASA.setOnClickListener(this);
        ll_BundlePhone_AASA.setOnClickListener(this);
        ll_ChangePassWord_AASA.setOnClickListener(this);
        ll_ChangeBundlePhone_AASA.setOnClickListener(this);


    }

    private void initView() {
        ll_Back_AASA = (LinearLayout) findViewById(R.id.ll_Back_AASA);
        ll_BundlePhone_AASA = (LinearLayout) findViewById(R.id.ll_BundlePhone_AASA);
        ll_ChangePassWord_AASA = (LinearLayout) findViewById(R.id.ll_ChangePassWord_AASA);
        ll_ChangeBundlePhone_AASA = (LinearLayout) findViewById(R.id.ll_ChangeBundlePhone_AASA);
        tv_PhoneNumber_AASA = (TextView) findViewById(R.id.tv_PhoneNumber_AASA);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_Back_AASA:
                this.finish();
                break;
            case R.id.ll_BundlePhone_AASA:
                if (mobile.equals("") || mobile == null) {
                    startActivity(BundlePhoneActivity.class);
                } else {
                    ToastUtil.shortshow(this, "您已经绑定了手机号");
                }
                break;
            case R.id.ll_ChangePassWord_AASA:
                startActivity(ChangePassWordActivity.class);
                break;
            case R.id.ll_ChangeBundlePhone_AASA:
                startActivity(ChangeBoundsActivity.class);
                break;
        }
    }
    /**
     * 获取用户信息
     *
     * @param activity 调用此方法的activity
     */
    public void getUserInfo(final Activity activity) {
        String session_id = activity.getSharedPreferences(StaticBean.USER_INFO, Context.MODE_PRIVATE).getString("session_id", null);
        String user_id = activity.getSharedPreferences(StaticBean.USER_INFO, Context.MODE_PRIVATE).getString("user_id", null);

        if (session_id == null||(session_id.equals("") ) || ( user_id == null)||user_id.equals("") ) {

            ToastUtil.shortshow(activity, "请先登录应用");
        } else {
            AndroidAsyncHttp.get(ServerAPIConfig.Get_UserInfo + "session_id=" + session_id + "&user_id=" + user_id, null, new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int i, Header[] headers, byte[] bytes) {
                    String str_json = new String(bytes);
                    try {
                        JSONObject obj = new JSONObject(str_json);
                        if (obj.getInt("code") != 0) {
                            ToastUtil.shortshow(activity, R.string.toast_login_failure);
                            return;
                        }
                        obj = obj.getJSONObject("result");
                        SharedPreferences.Editor et = activity.getSharedPreferences(StaticBean.USER_INFO, Context.MODE_PRIVATE).edit();
                        et.putString("mobile", obj.getString("mobile"));
                        et.commit();
                        setNewPhone();
                    } catch (JSONException e) {
                        LogUtil.e(activity, "enter catch");
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                    ToastUtil.shortshow(activity, R.string.toast_error_net);
                    LogUtil.e(activity, "网络连接异常");
                }
            });

        }
    }
    public void setNewPhone(){
        mobile = getSharedPreferences(StaticBean.USER_INFO, Context.MODE_PRIVATE).getString("mobile", null);
        tv_PhoneNumber_AASA.setVisibility(View.VISIBLE);
        tv_PhoneNumber_AASA.setText(mobile);
    }
}
