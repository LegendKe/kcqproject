package com.djx.pin.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import com.djx.pin.R;
import com.djx.pin.application.PinApplication;
import com.djx.pin.base.baseui.BaseActivity;
import com.djx.pin.beans.EventBeans;
import com.djx.pin.beans.StaticBean;
import com.djx.pin.business.AppConstants;
import com.djx.pin.serverapiconfig.ServerAPIConfig;
import com.djx.pin.utils.AndroidAsyncHttp;
import com.djx.pin.utils.LogUtil;
import com.djx.pin.utils.ToastUtil;
import com.djx.pin.utils.myutils.ConstantUtils;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

import cz.msebera.android.httpclient.Header;
import de.greenrobot.event.EventBus;
import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;

import static com.djx.pin.beans.UserInfo.getRongPortrait;

/**
 * Created by 陈刘磊 (代号：姜饼（GingerBread）) on 2016/5/21.
 */
public class LogoActivity extends BaseActivity {

    SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LogUtil.e("--LogoActivity------onCreate()------");
        setContentView(R.layout.activity_logo);
        sp = getSharedPreferences(StaticBean.USER_INFO, MODE_PRIVATE);
        initData();
        new Handler().postDelayed(new Runnable(){
            @Override
            public void run() {
                Intent intent = new Intent(LogoActivity.this,MainActivity.class);
                startActivity(intent);
                LogoActivity.this.finish();
            }
        }, 2400);
    }

    private void initData() {
        if (sp.getBoolean("isLogined", false)) {
            String session_id = sp.getString("session_id", "");
            rongLogin(session_id);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        LogUtil.e("--LogoActivity------onStop()------");
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LogUtil.e("--LogoActivity------onDestroy()------");
    }


    /**
     * 登陆融云,默认先刷新融云Token,然后在登陆
     * @param session_id
     */
    public void rongLogin(String session_id) {
        Log.e("message","----LogoActivity-----rongLogin()----------");
        RongIM.getInstance().getRongIMClient().logout();
        AsyncHttpResponseHandler res = new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                String strJson = new String(bytes);
                try {
                    JSONObject obj = new JSONObject(strJson);
                    if (0 == obj.getInt("code")) {
                        obj=obj.getJSONObject("result");
                        sp.edit().putString("rongyun_token",obj.getString("rongyun_token")).commit();
                        connect(obj.getString("rongyun_token"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
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
    private void connect(String token) {
        Log.e("message","----LogoActivity-----connect()----------");
        if (getApplicationInfo().packageName.equals(PinApplication.getCurProcessName(getApplicationContext()))) {

            /**
             * IMKit SDK调用第二步,建立与服务器的连接
             */
            LogUtil.e("--------------------MainActivity----------RongIM.connect()-------------------------------------------------");
            RongIM.connect(token, new RongIMClient.ConnectCallback() {
                /**
                 * Token 错误，在线上环境下主要是因为 Token 已经过期，您需要向 App Server 重新请求一个新的 Token
                 */
                @Override
                public void onTokenIncorrect() {
                    LogUtil.e("--融云登陆失败:Token 已经过期");
                }

                /**
                 * 连接融云成功
                 * @param userid 当前 token
                 */
                @Override
                public void onSuccess(String userid) {
                    LogUtil.e("--融云登陆成功");
                    setRongyunUserProvider();
                    Intent intent = new Intent(AppConstants.INTENT_ACTION_LOGIN);
                    sendBroadcast(intent);
                }
                @Override
                public void onError(RongIMClient.ErrorCode errorCode) {
                    LogUtil.e("--融云登陆错误");
                }
            });
        }
    }

    AsyncHttpResponseHandler res4Portrait = null;
    private void setRongyunUserProvider() {
        Log.e("message","----LogoActivity-----setRongyunUserProvider()----------");
        //设置融云信息提供者
        RongIM.setUserInfoProvider(new RongIM.UserInfoProvider() {
            @Override
            public io.rong.imlib.model.UserInfo getUserInfo(String s) {
                RequestParams params=new RequestParams();
                params.put("user_id",s);
                AndroidAsyncHttp.get(ServerAPIConfig.Get_UserInfo,params,res4Portrait);
                return null;
            }
        }, true);

        //获取用户头像ID
        res4Portrait = new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                String strJson = new String(bytes);
                try {
                    JSONObject obj=new JSONObject(strJson);
                    if(0!=obj.getInt("code")){
                        return;
                    }
                    Log.e("message","----LogoActivity-----获取用户头像ID----------");
                    obj=obj.getJSONObject("result");
                    String portrait_id=obj.getString("portrait");
                    //如果用户头像字段异常表明用户没有设置头像,直接return;
                    if(null==portrait_id||portrait_id.length()==0){
                        RongIM.getInstance().refreshUserInfoCache(new io.rong.imlib.model.UserInfo(obj.getString("user_id"), obj.getString("nickname"), Uri.parse("")));
                        //知messagectivity刷新
                        Log.e("message","----LogoActivity-----知messagectivity刷新----------");
                        EventBus.getDefault().post(new EventBeans(ConstantUtils.RE_LOGIN));
                        return;
                    }
                    try {
                        getRongPortrait(getApplicationContext(), obj.getString("user_id"),obj.getString("nickname"),portrait_id,1);
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                ToastUtil.shortshow(getApplicationContext(),R.string.toast_error_net);
            }
        };
    }
}
