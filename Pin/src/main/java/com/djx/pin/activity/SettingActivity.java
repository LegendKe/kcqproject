package com.djx.pin.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.djx.pin.R;
import com.djx.pin.base.OldBaseActivity;
import com.djx.pin.beans.StaticBean;
import com.djx.pin.business.AppConstants;
import com.djx.pin.serverapiconfig.ServerAPIConfig;
import com.djx.pin.service.UpLoadSOSLocationService;
import com.djx.pin.ui.CommonDialog;
import com.djx.pin.utils.AndroidAsyncHttp;
import com.djx.pin.utils.LogUtil;
import com.djx.pin.utils.ToastUtil;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;
import io.rong.imkit.RongIM;

/**
 * Created by Administrator on 2016/6/28.
 */
public class SettingActivity extends OldBaseActivity implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {
    private TextView tv_ExitAccount_SA, tv_EmergencyPersonPhone;
    private LinearLayout ll_AboutUs_SA, ll_Law_SA, ll_UserGuide_SA, ll_AccountAndSafe_SA, ll_Back_SettingActivity, ll_EmergencyPerson_SA,ll_Slidmenu_NewsVersion;

    private SharedPreferences sp;
    private Context CONTEXT = SettingActivity.this;
    private SwitchCompat cb_LocationShare;

    int old_show_location;
    int new_show_location;
    //紧急联系人号码
    String mobile;

    Intent intent;
    private int flag = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.anctivity_setting);
        intent = new Intent(SettingActivity.this, UpLoadSOSLocationService.class);
        initView();
        initEvent();

        mobile = getSharedPreferences(StaticBean.USER_INFO, Context.MODE_PRIVATE).getString("emergency_mobile", null);
        if (mobile == null || mobile.equals("")) {
            tv_EmergencyPersonPhone.setText("未设置");
        } else {
            tv_EmergencyPersonPhone.setText(mobile);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        boolean isEmergencyChanged = getSharedPreferences(StaticBean.ContactPerson, Context.MODE_PRIVATE).getBoolean("isEmergencyChanged", false);
        if (isEmergencyChanged) {

            getUserInfo(this);
            getSharedPreferences(StaticBean.ContactPerson, Context.MODE_PRIVATE).edit().putBoolean("isEmergencyChanged", false).commit();
            Log.e("she紧急联系人", "yes");
        } else {
            Log.e("she紧急联系人", "no");
            return;
        }
    }

    private void initEvent() {
        ll_Back_SettingActivity.setOnClickListener(this);
        ll_AboutUs_SA.setOnClickListener(this);
        ll_Law_SA.setOnClickListener(this);
        ll_UserGuide_SA.setOnClickListener(this);
        ll_AccountAndSafe_SA.setOnClickListener(this);
        ll_EmergencyPerson_SA.setOnClickListener(this);
        ll_Slidmenu_NewsVersion.setOnClickListener(this);
        tv_ExitAccount_SA.setOnClickListener(this);
        tv_EmergencyPersonPhone.setOnClickListener(this);

        cb_LocationShare.setOnCheckedChangeListener(this);

        int is_show_location = getSharedPreferences(StaticBean.USER_INFO, Context.MODE_PRIVATE).getInt("is_show_location", 0);
        old_show_location = is_show_location;
        if (is_show_location == 1) {
            cb_LocationShare.setChecked(true);
        } else {
            cb_LocationShare.setChecked(false);
        }

    }

    private void initView() {
        sp = getSharedPreferences(StaticBean.USER_INFO, Context.MODE_PRIVATE);

        ll_Back_SettingActivity = (LinearLayout) findViewById(R.id.ll_Back_SettingActivity);
        ll_AboutUs_SA = (LinearLayout) findViewById(R.id.ll_AboutUs_SA);
        ll_Law_SA = (LinearLayout) findViewById(R.id.ll_Law_SA);
        ll_UserGuide_SA = (LinearLayout) findViewById(R.id.ll_UserGuide_SA);
        ll_AccountAndSafe_SA = (LinearLayout) findViewById(R.id.ll_AccountAndSafe_SA);
        ll_EmergencyPerson_SA = (LinearLayout) findViewById(R.id.ll_EmergencyPerson_SA);
        ll_Slidmenu_NewsVersion = (LinearLayout) findViewById(R.id.ll_Slidmenu_NewsVersion);

        tv_ExitAccount_SA = (TextView) findViewById(R.id.tv_ExitAccount_SA);
        tv_EmergencyPersonPhone = (TextView) findViewById(R.id.tv_EmergencyPersonPhone);

        cb_LocationShare = (SwitchCompat) findViewById(R.id.cb_LocationShare);

    }

    /**
     * 退出登录
     */
    private void Logout() {

        AsyncHttpResponseHandler res = new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                SharedPreferences.Editor editor = sp.edit();
                editor.putBoolean("isLogined", false);
                //移除用户资料
                editor.putString("birthday", "");
                editor.putString("session_id", "");
                editor.remove("is_auth");
                editor.putString("nickname", "");
                editor.remove("id_card");
                editor.remove("country_code");
                editor.putString("portrait", "");
                editor.remove("city");
                editor.remove("point");
                editor.remove("balance");
                editor.putInt("rank", 0);
                editor.remove("id_card_pic");
                editor.remove("real_name");
                editor.remove("province");
                editor.remove("sos_id");
                editor.putInt("gender", 0);
                editor.putString("user_id", "");
                editor.remove("district");
                editor.putInt("credit", 0);
                editor.remove("rongyun_token");
                editor.putBoolean("isLogined", false);
                //退出登录时,隐藏菜单类型
                editor.putInt("slidType", 3);
                editor.commit();

                //融云退出登录
                RongIM.getInstance().getRongIMClient().logout();
                Intent intent = new Intent(AppConstants.INTENT_ACTION_LOGOUT);
                sendBroadcast(intent);
                startActivity(LoginActivity.class);
                SettingActivity.this.finish();
            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                ToastUtil.shortshow(CONTEXT, R.string.toast_error_net);
                LogUtil.e(CONTEXT, "网络连接异常");
            }
        };

        RequestParams params = new RequestParams();
        params.put("session_id", sp.getString("session_id", null));
        AndroidAsyncHttp.post(ServerAPIConfig.Do_Logout, params, res);
    }


    Bundle bundle = new Bundle();

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_Back_SettingActivity:
                this.finish();
                break;
            case R.id.ll_AboutUs_SA:
                bundle.putInt("TextContent", 1);
                bundle.putString("url", null);
                startActivity(TextActivity.class, bundle);

                break;
            case R.id.ll_Law_SA:
                bundle.putInt("TextContent", 2);
                bundle.putString("url", null);
                startActivity(TextActivity.class, bundle);

                break;
            case R.id.ll_UserGuide_SA:
                bundle.putInt("TextContent", 3);
                bundle.putString("url", null);
                startActivity(TextActivity.class, bundle);

                break;
            case R.id.ll_AccountAndSafe_SA:
                startActivity(AccountAndSafeActivity.class);
                break;
            case R.id.ll_EmergencyPerson_SA:
                startActivity(SettingEmergencyPersonActivity.class);
                break;
            case R.id.tv_ExitAccount_SA://推出账号，关闭service
                new_show_location = 0;
               /* getSharedPreferences(StaticBean.USER_INFO, Context.MODE_PRIVATE).edit().putInt("is_show_location", 0).commit();
                upLoadLocation(0);*/
                stopService(intent);
                Logout();
                break;
            case R.id.ll_Slidmenu_NewsVersion:
                startActivity(NewsVersionActivity.class);
                break;
        }
    }


    /**
     * Called when the checked state of a compound button has changed.
     *
     * @param buttonView The compound button view whose state has changed.
     * @param isChecked  The new checked state of buttonView.
     */

    @Override
    public void onCheckedChanged(final CompoundButton buttonView, boolean isChecked) {

        switch (buttonView.getId()) {
            case R.id.cb_LocationShare:
                if (isChecked) {
                    int show_flag = getSharedPreferences(StaticBean.USER_INFO, Context.MODE_PRIVATE).getInt("is_show_location",0);
                    if(show_flag == 0){
                        CommonDialog.show(SettingActivity.this, "确认", "取消", "您将向周围的人共享自己的位置信息", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                upLoadLocation(1);
                                getSharedPreferences(StaticBean.USER_INFO, Context.MODE_PRIVATE).edit().putInt("is_show_location", 1).commit();
                            }
                        }, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                buttonView.setChecked(false);
                            }
                        });
                    }

                } else {
                    getSharedPreferences(StaticBean.USER_INFO, Context.MODE_PRIVATE).edit().putInt("is_show_location", 0).commit();
                    upLoadLocation(0);
                    LogUtil.e("关闭位置共享");

                }
                break;
        }

    }

    public void upLoadLocation(int new_show_location){
        String session_id = getSharedPreferences(StaticBean.USER_INFO, Context.MODE_PRIVATE).getString("session_id", null);
        String url = ServerAPIConfig.LocationShare;
        final JSONObject object = new JSONObject();
        try {
            object.put("session_id", session_id);
            object.put("is_show_location", new_show_location);
            Double latitude = Double.valueOf(sp.getString("latitude", null));
            Double longitude = Double.valueOf(sp.getString("longitude", null));
            object.put("latitude", latitude);
            object.put("longitude", longitude);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        StringEntity entity = new StringEntity(object.toString(), "utf-8");
        AsyncHttpResponseHandler res = new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                String str = new String(bytes);
                try {
                    JSONObject object1 = new JSONObject(str);
                    if (object1.getInt("code") == 0) {

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {

            }
        };
        AndroidAsyncHttp.post(this, url, entity, "application/json;charset=UTF-8", res);
    }







    /**
     * 获取用户信息
     *
     * @param activity 调用此方法的activity
     */
    public void getUserInfo(final Activity activity) {
        String session_id = activity.getSharedPreferences(StaticBean.USER_INFO, Context.MODE_PRIVATE).getString("session_id", null);
        String user_id = activity.getSharedPreferences(StaticBean.USER_INFO, Context.MODE_PRIVATE).getString("user_id", null);

        if (session_id == null || (session_id.equals("")) || (user_id == null) || user_id.equals("")) {

            ToastUtil.shortshow(activity, "请先登录应用");
        } else {
            AndroidAsyncHttp.get(ServerAPIConfig.Get_UserInfo + "session_id=" + session_id + "&user_id=" + user_id, null, new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int i, Header[] headers, byte[] bytes) {
                    String str_json = new String(bytes);
                    try {
                        JSONObject obj = new JSONObject(str_json);
                        if (obj.getInt("code") == 0) {
                            obj = obj.getJSONObject("result");
                            tv_EmergencyPersonPhone.setText(obj.getString("emergency_mobile"));
                            Log.e("mobile", obj.getString("emergency_mobile"));
                        } else {
                            errorCode(obj.getInt("code"));
                        }
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
}
