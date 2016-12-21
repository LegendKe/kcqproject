package com.djx.pin.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.djx.pin.R;
import com.djx.pin.application.PinApplication;
import com.djx.pin.base.OldBaseActivity;
import com.djx.pin.beans.EventBeans;
import com.djx.pin.beans.StaticBean;
import com.djx.pin.beans.UserBean;
import com.djx.pin.beans.UserInfo;
import com.djx.pin.business.AppConstants;
import com.djx.pin.serverapiconfig.ServerAPIConfig;
import com.djx.pin.utils.AndroidAsyncHttp;
import com.djx.pin.utils.LogUtil;
import com.djx.pin.utils.ToastUtil;
import com.djx.pin.utils.myutils.ConstantUtils;
import com.djx.pin.utils.myutils.ScreenTools;
import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;
import de.greenrobot.event.EventBus;
import de.greenrobot.event.Subscribe;
import de.greenrobot.event.ThreadMode;
import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;

/**
 * Created by Administrator on 2016/5/21.
 */
public class LoginActivity extends OldBaseActivity implements View.OnClickListener {
    protected final static String TAG = LoginActivity.class.getSimpleName();

    private Button bt_Login;
    private ImageView im_WeiXin_Login, im_QQ_Login, im_XinLang_Login;
    private LinearLayout ll_Login_Back, ll_Register, ll_Forgot_Password;
    private TextView tv_termofservice;
    private EditText et_User_Name, et_Password;
    private Context CONTEXT = LoginActivity.this;
    SharedPreferences sp;
    private ProgressDialog progressDialog;
    private UserBean userBean;
    private boolean isFirstLocation = true;
    private boolean isLogin = false;
    private PinApplication myApp;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        myApp = PinApplication.getMyApp();
        myApp.startLocationClient();
        Log.e("loginactivity","----------onCreate()");
        initView();
        initEvent();
        EventBus.getDefault().register(this);
    }

    @Subscribe(threadMode = ThreadMode.MainThread)
    public void onEvent(EventBeans eventBeans){
        switch (eventBeans.style){
            case ConstantUtils.EVENT_LOCATION_FINISH:
                if(isFirstLocation && isLogin){
                    isFirstLocation = false;
                    //位置共享
                    upLoadLocation(1);
                }
                if(!myApp.isMapOnLocation){
                    myApp.stopLocationClient();
                }
                break;
        }
    }
    /**
     * 添加事件
     */
    private void initEvent() {
        bt_Login.setOnClickListener(this);
        im_WeiXin_Login.setOnClickListener(this);
        im_QQ_Login.setOnClickListener(this);
        im_XinLang_Login.setOnClickListener(this);
        ll_Login_Back.setOnClickListener(this);

        ll_Register.setOnClickListener(this);
        ll_Forgot_Password.setOnClickListener(this);
        tv_termofservice.setMovementMethod(LinkMovementMethod.getInstance());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        PinApplication.getMyApp().stopLocationClient();
    }

    /**
     * 初始化控件
     */
    private void initView() {
        sp = getSharedPreferences(StaticBean.USER_INFO, MODE_PRIVATE);
        bt_Login = (Button) findViewById(R.id.bt_Login);
        im_WeiXin_Login = (ImageView) findViewById(R.id.im_WeiXin_Login);
        im_QQ_Login = (ImageView) findViewById(R.id.im_QQ_Login);
        im_XinLang_Login = (ImageView) findViewById(R.id.im_XinLang_Login);
        ll_Login_Back = (LinearLayout) findViewById(R.id.ll_Login_Back);

        ll_Register = (LinearLayout) findViewById(R.id.ll_Register);
        ll_Forgot_Password = (LinearLayout) findViewById(R.id.ll_Forgot_Password);

        et_User_Name = (EditText) findViewById(R.id.et_User_Name);
        et_User_Name.setText(UserInfo.getMobile(this));
        et_Password = (EditText) findViewById(R.id.et_Password);

        tv_termofservice = (TextView) findViewById(R.id.tv_termofservice_login);

        int screenWidth = ScreenTools.instance(this).getScreenWidth();
        double per = screenWidth / 1080.0;
        Drawable drawable1 = getResources().getDrawable(R.drawable.slector_et_login_username);
        drawable1.setBounds(0, 0, (int) (48*per), (int) (48*per));//第一0是距左边距离，第二0是距上边距离，40分别是长宽
        et_User_Name.setCompoundDrawables(drawable1, null, null, null);//只放左边

        Drawable drawable2 = getResources().getDrawable(R.drawable.slector_et_login_password);
        drawable2.setBounds(0, 0, (int) (48*per), (int) (48*per));//第一0是距左边距离，第二0是距上边距离，40分别是长宽
        et_Password.setCompoundDrawables(drawable2, null, null, null);//只放左边

        ((LinearLayout) findViewById(R.id.ll_activity)).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {//[a-zA-Z]
        switch (v.getId()) {//"[^a-zA-Z0-9]"
            case R.id.bt_Login:
                String password = et_Password.getText().toString().trim();
                boolean matches = password.matches("^[A-Za-z0-9]+$");
                if(!matches){
                    ToastUtil.shortshow(this,"密码只能是数字或字母");
                    return;
                }
                phoneLogin();
                break;
            case R.id.ll_Login_Back:
                this.finish();
                break;
            case R.id.ll_Register:
                startActivity(RegisterActivity.class);
                overridePendingTransition(android.R.anim.slide_in_left,android.R.anim.slide_in_left);
                this.finish();
                break;
            case R.id.ll_Forgot_Password:
                startActivity(ForgotPassWordActivity.class);
                overridePendingTransition(android.R.anim.slide_in_left,android.R.anim.slide_in_left);
                break;

            case R.id.ll_activity:
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                break;

        }
    }


    private void phoneLogin() {
        Log.i(TAG, "phoneLogin");
        if (!checkUserInfo()) {
            return;
        }
        if(progressDialog == null){
            progressDialog = new ProgressDialog(this);
        }
        progressDialog.setMessage("登录中...");
        progressDialog.show();
        AsyncHttpResponseHandler res = new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                String str_json = new String(bytes);
                LogUtil.e("loginactivity-----phoneLogin()-----str_json"+str_json);
                try {
                    JSONObject obj = new JSONObject(str_json);
                    if (0 != obj.getInt("code")) {
                        ToastUtil.shortshow(CONTEXT, R.string.toast_login_failure);
                        errorCode(obj.getInt("code"));
                        return;
                    }
                    obj = obj.getJSONObject("result");
                    //保存用户的rongyun_token
                    sp.edit().
                            putString("session_id", obj.getString("session_id")).
                            putString("user_id", obj.getString("user_id")).
                            putString("rongyun_token", obj.getString("rongyun_token")).
                            putBoolean("isLogined", true).commit();
                    getUserInfo(obj.getString("session_id"), obj.getString("user_id"));
                } catch (JSONException e) {
                    LogUtil.e(CONTEXT, "enter catch");
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                LogUtil.e(CONTEXT, "网络连接异常");
                progressDialog.dismiss();
                ToastUtil.shortshow(CONTEXT, R.string.toast_error_net);
            }
        };
        RequestParams params = new RequestParams();
        params.put("country_code", "0086");
        params.put("mobile", et_User_Name.getText().toString());
        params.put("password", et_Password.getText().toString());
        params.put("registration_id", sp.getString("JPush_RegistrationID", null));

        AndroidAsyncHttp.post(ServerAPIConfig.Do_Login_Phone, params, res);

    }

    /**
     * 检查用户输入的信息是否正确,正确则返回true;反之false;
     *
     * @return
     */
    private boolean checkUserInfo() {

        if (11 != et_User_Name.getText().toString().length()) {
            ToastUtil.shortshow(CONTEXT, R.string.toast_phone_length_error);
            LogUtil.e(CONTEXT, "手机号长度错误");
            return false;
        }
        if (6 > et_Password.getText().toString().length() || 20 < et_Password.getText().toString().length()) {
            LogUtil.e(CONTEXT, "密码程度错误");
            ToastUtil.shortshow(CONTEXT, R.string.toast_password_length_error);
            return false;
        }
        return true;
    }


    /**
     * 获取用户信息
     *
     * @param session_id 用户的session_id
     * @param user_id    用户的user_id
     */
    public void getUserInfo(String session_id, String user_id) {
        AndroidAsyncHttp.get(ServerAPIConfig.Get_UserInfo + "session_id=" + session_id + "&user_id=" + user_id, null, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                String str_json = new String(bytes);
                try {
                    JSONObject obj = new JSONObject(str_json);
                    if (obj.getInt("code") != 0) {
                        ToastUtil.shortshow(LoginActivity.this, R.string.toast_login_failure);
                        return;
                    }
                    Gson gson = new Gson();
                    userBean = gson.fromJson(obj.getJSONObject("result").toString(), UserBean.class);
                    if (userBean != null) {
                        rongLogin(getApplicationContext(), userBean.getSession_id());
                    }
                } catch (JSONException e) {
                    LogUtil.e(LoginActivity.this, "enter catch");
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                ToastUtil.shortshow(LoginActivity.this, R.string.toast_error_net);
                LogUtil.e(LoginActivity.this, "网络连接异常");
            }
        });
    }

    public void upLoadLocation(int new_show_location){
        String session_id = getSharedPreferences(StaticBean.USER_INFO, Context.MODE_PRIVATE).getString("session_id", null);
        String url = ServerAPIConfig.LocationShare;
        final JSONObject object = new JSONObject();
        try {
            object.put("session_id", session_id);
            object.put("is_show_location", new_show_location);
            BDLocation bdLocation = PinApplication.getMyApp().getBdLocation();
            if(bdLocation == null){
                return;
            }
            object.put("latitude", bdLocation.getLatitude());
            object.put("longitude", bdLocation.getLongitude());
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
     * 登陆融云,默认先刷新融云Token,然后在登陆
     * @param session_id
     */
    public void rongLogin(final Context context, String session_id) {
        RongIM.getInstance().getRongIMClient().logout();
        AsyncHttpResponseHandler res = new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                String strJson = new String(bytes);
                Log.i(TAG, strJson);
                try {
                    JSONObject obj = new JSONObject(strJson);
                    if (0 != obj.getInt("code")) {
                        LogUtil.e("返回结果码=" + obj.getInt("code"));
                        return;
                    }
                    obj=obj.getJSONObject("result");
                    UserInfo.setRongToken(context, obj.getString("rongyun_token"));
                    connect(context, obj.getString("rongyun_token"));
                } catch (JSONException e) {
                    LogUtil.e(context, "enter catch");
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                LogUtil.e(context, "enter onFailure");
            }
        };

        RequestParams params = new RequestParams();
        params.put("session_id", session_id);
        AndroidAsyncHttp.post(ServerAPIConfig.RefreshRongToken, params, res);
    }

    /**
     * 建立与融云服务器的连接
     *
     * @param token
     */
    private void connect(final Context context, String token) {
        if (context.getApplicationInfo().packageName.equals(PinApplication.getCurProcessName(context))) {
            RongIM.connect(token, new RongIMClient.ConnectCallback() {
                @Override
                public void onTokenIncorrect() {
                    Log.e("loginactivity","----融云连接失败------onTokenIncorrect()");
                }
                @Override
                public void onSuccess(String userid) {
                    Log.e("loginactivity","----融云连接成功------onSuccess()"+userid);
                    Intent intent = new Intent(AppConstants.INTENT_ACTION_LOGIN);
                    context.sendBroadcast(intent);
                    EventBus.getDefault().post(new EventBeans(ConstantUtils.RE_LOGIN));
                    userBean.setIs_show_location(1);//默认让他开启
                    UserInfo.updateUserInfoPref(getApplicationContext(), userBean);
                    isLogin = true;
                    progressDialog.dismiss();
                    LoginActivity.this.finish();
                }
                @Override
                public void onError(RongIMClient.ErrorCode errorCode) {
                    Log.e("loginactivity","----融云连接失败------onError()"+errorCode);
                }
            });
        }
    }

}
