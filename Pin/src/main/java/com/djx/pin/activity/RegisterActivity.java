package com.djx.pin.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.djx.pin.R;
import com.djx.pin.base.OldBaseActivity;
import com.djx.pin.beans.StaticBean;
import com.djx.pin.beans.UserBean;
import com.djx.pin.beans.UserInfo;
import com.djx.pin.business.AppConstants;
import com.djx.pin.serverapiconfig.ServerAPIConfig;
import com.djx.pin.utils.AndroidAsyncHttp;
import com.djx.pin.utils.LogUtil;
import com.djx.pin.utils.ToastUtil;
import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Timer;
import java.util.TimerTask;

import cz.msebera.android.httpclient.Header;

/**
 * Created by Administrator on 2016/5/31.
 */
public class RegisterActivity extends OldBaseActivity implements View.OnClickListener {

    private TextView tv_Register_Cancel;
    private LinearLayout ll_Login;
    private Button bt_Register;
    private EditText et_PhoneNumber, et_PassWord, et_SMSCode;
    private TextView tv_termofservice_register,bt_SMSCode;
    private Context THIS = RegisterActivity.this;

    private Handler handler;
    private TimerTask timerTask;
    int Second = 60;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        initView();
        initEvent();
    }

    private void initEvent() {
        tv_Register_Cancel.setOnClickListener(this);
        ll_Login.setOnClickListener(this);
        bt_Register.setOnClickListener(this);
        bt_SMSCode.setOnClickListener(this);
        tv_termofservice_register.setMovementMethod(LinkMovementMethod.getInstance());
    }

    private void initView() {
        tv_Register_Cancel = (TextView) findViewById(R.id.tv_Register_Cancel);
        ll_Login = (LinearLayout) findViewById(R.id.ll_Login);
        bt_Register = (Button) findViewById(R.id.bt_Register);

        et_PhoneNumber = (EditText) findViewById(R.id.et_PhoneNumber);
        et_PassWord = (EditText) findViewById(R.id.et_PassWord);
        et_SMSCode = (EditText) findViewById(R.id.et_SMSCode);
        bt_SMSCode = (TextView) findViewById(R.id.bt_SMSCode);
        tv_termofservice_register = (TextView) findViewById (R.id.tv_termofservice_register);
        ((LinearLayout) findViewById(R.id.ll_back)).setOnClickListener(this);

        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                bt_SMSCode.setText("重新发送(" + (Second--) + ")");
                if (0 == Second) {
                    bt_SMSCode.setText("发送验证码");
                    bt_SMSCode.setClickable(true);
                    bt_SMSCode.setTextColor(0xFF1295f7);
                    timerTask.cancel();
                }
            }
        };
        timerTask = new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        handler.sendEmptyMessage(0);
                    }
                });
            }
        };
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_Register_Cancel:
                startActivity(LoginActivity.class);
                this.finish();
                break;
            case R.id.ll_Login:
                startActivity(LoginActivity.class);
                this.finish();
                break;
            case R.id.bt_Register:

                String password = et_PassWord.getText().toString().trim();
                boolean matches = password.matches("^[A-Za-z0-9]+$");
                if(!matches){
                    ToastUtil.shortshow(this,"密码只能是数字或字母");
                    return;
                }
                updataUserInfo();
                break;
            case R.id.bt_SMSCode:
                //检查手机号长度是否正确
                if (11 != et_PhoneNumber.getText().toString().length()) {
                    LogUtil.e(THIS, "手机号长度不正确");
                    ToastUtil.shortshow(THIS, R.string.toast_phone_length_error);
                    return;
                }
                getSMSCode();
                break;
            case R.id.ll_back:
                finish();
        }
    }

    /**
     * 注册用户上传用户信息
     */
    public void updataUserInfo() {
        if (!checkUserInfo()) {
            return;
        }
        AsyncHttpResponseHandler res = new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                String str_json = new String(bytes);
                try {
                    JSONObject obj = new JSONObject(str_json);
                    if (0 != obj.getInt("code")) {
                        if(2003==obj.getInt("code")){
                            ToastUtil.shortshow(THIS,R.string.toast_phone_used);
                            return;
                        }
                        if(2007==obj.getInt("code")){
                            ToastUtil.shortshow(THIS,R.string.toast_phone_error);
                            return;
                        }
                        ToastUtil.shortshow(THIS, R.string.toast_register_failer);
                        return;
                    }
                    obj = obj.getJSONObject("result");
                    //保存用户的信息
                    getSharedPreferences(StaticBean.USER_INFO, MODE_PRIVATE).edit().
                            putString("session_id",obj.getString("session_id")).
                            putString("user_id",obj.getString("user_id")).
                            putString("rongyun_token", obj.getString("rongyun_token")).
                            putBoolean("isLogined",true).commit();

                    ToastUtil.shortshow(THIS, R.string.toast_register_success);
                    UserInfo.getUserInfo(getApplicationContext(), obj.getString("session_id"), obj.getString("user_id"));
                    UserInfo.getUserInfo(getApplicationContext(), new UserInfo.GetUserInfoCallBack() {
                        @Override
                        public void callback(boolean state) {
                            UserInfo.rongLogin(getApplicationContext(), UserInfo.getSessionID(getApplicationContext()));
                            finish();
                        }
                    });
//                    startActivity(ImprovePersonalDataActivity.class);
                } catch (JSONException e) {
                    LogUtil.e(THIS, "enter catch");
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                LogUtil.e(THIS, "网络连接异常");
                ToastUtil.shortshow(THIS, R.string.toast_error_net);
            }
        };
        RequestParams params = new RequestParams();
        params.put("country_code", "0086");
        params.put("mobile", et_PhoneNumber.getText().toString());
        params.put("sms_code", et_SMSCode.getText().toString());
        params.put("password", et_PassWord.getText().toString());
        params.put("registration_id", getSharedPreferences(StaticBean.USER_INFO, MODE_PRIVATE).getString("JPush_RegistrationID", null));
        AndroidAsyncHttp.post(ServerAPIConfig.Do_Register_Phone, params, res);

    }


    private void getSMSCode() {
        AsyncHttpResponseHandler res = new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                String str_json = new String(bytes);
                try {
                    JSONObject obj = new JSONObject(str_json);
                    if (0 != obj.getInt("code")) {
                        errorCode(obj.getInt("code"));
                        return;
                    }
                    //重置短信计时时间
                    Second = 60;
                    new Timer().schedule(timerTask, 0, 1000);
                    bt_SMSCode.setClickable(false);
                    bt_SMSCode.setTextColor(0xFF8A8A8A);
                } catch (JSONException e) {
                    LogUtil.e(THIS, "enter catch");
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                LogUtil.e(THIS, "网络连接异常");
                ToastUtil.shortshow(THIS, R.string.toast_error_net);
            }
        };
        RequestParams params = new RequestParams();
        params.put("country_code", "0086");
        params.put("mobile", et_PhoneNumber.getText().toString());
        AndroidAsyncHttp.post(ServerAPIConfig.GET_Request_SMSCode, params, res);
    }

    /**
     * 检查用户输入的信息是否完全正确,正确返回true,反之返回false
     *
     * @return
     */
    private boolean checkUserInfo() {
        //检查手机号长度是否正确
        if (11 != et_PhoneNumber.getText().toString().length()) {
            LogUtil.e(THIS, "手机号长度不正确");
            ToastUtil.shortshow(THIS, R.string.toast_phone_length_error);
            return false;
        }
        //检查密码长度是否正确
        if (6 > et_PassWord.getText().toString().length() || 20 < et_PassWord.getText().toString().length()) {
            LogUtil.e(THIS, "密码长度不正确");
            ToastUtil.shortshow(THIS, R.string.toast_password_length_error);
            return false;
        }
       /* //检查二次输入的密码长度是否正确
        if (6 > et_PassWord_Confirm.getText().toString().length() || 20 < et_PassWord_Confirm.getText().toString().length()) {
            LogUtil.e(THIS, "密码长度不正确");
            ToastUtil.shortshow(THIS, R.string.toast_password_length_error);
            return false;
        }
        //检查两次输入的密码是否一致
        if (!et_PassWord.getText().toString().equals(et_PassWord_Confirm.getText().toString())) {
            LogUtil.e(THIS, "两次输入的密码不一致");
            ToastUtil.shortshow(THIS, R.string.toast_password_different_error);
            return false;
        }*/
        //检查验证码长度是否正确
        if (6 != et_SMSCode.getText().toString().length()) {
            LogUtil.e(THIS, "验证码长度不正确");
            ToastUtil.shortshow(THIS, R.string.toast_sms_length_error);
            return false;
        }
        return true;
    }


}
