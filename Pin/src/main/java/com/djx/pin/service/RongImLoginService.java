package com.djx.pin.service;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.djx.pin.R;
import com.djx.pin.application.PinApplication;
import com.djx.pin.beans.EventBeans;
import com.djx.pin.beans.StaticBean;
import com.djx.pin.beans.UserInfo;
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

/**
 * Created by 柯传奇 on 2016/12/7 0007.
 */
public class RongImLoginService extends Service {

    private SharedPreferences sp;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        sp = getSharedPreferences(StaticBean.USER_INFO, MODE_PRIVATE);
        if(UserInfo.getIsLogin(this)){
            Log.e("message","----RongImLoginService--登录融云---rongLogin()----------");
            rongLogin(UserInfo.getSessionID(this));
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    /**
     * 登陆融云,默认先刷新融云Token,然后在登陆
     * @param session_id
     */
    public void rongLogin(String session_id) {
        Log.e("message","----RongImLoginService-----rongLogin()----------");
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
        Log.e("message","----RongImLoginService-----connect()----------");
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
                    Intent intent = new Intent(AppConstants.INTENT_ACTION_LOGIN);
                    sendBroadcast(intent);
                    setRongyunUserProvider();
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
        Log.e("message","----RongImLoginService--设置融云信息提供者---setRongyunUserProvider()----------");
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
                    Log.e("message","----RongImLoginService-----获取用户头像ID----------");
                    obj=obj.getJSONObject("result");
                    String portrait_id=obj.getString("portrait");
                    //如果用户头像字段异常表明用户没有设置头像,直接return;
                    if(null==portrait_id||portrait_id.length()==0){
                        RongIM.getInstance().refreshUserInfoCache(new io.rong.imlib.model.UserInfo(obj.getString("user_id"), obj.getString("nickname"), Uri.parse("")));
                        //知messagectivity刷新
                        Log.e("message","----RongImLoginService-----知messagectivity刷新----------");
                        EventBus.getDefault().post(new EventBeans(ConstantUtils.RE_LOGIN));
                        return;
                    }
                    try {
                        UserInfo.getRongPortrait(getApplicationContext(), obj.getString("user_id"),obj.getString("nickname"),portrait_id,1);
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                ToastUtil.shortshow(getApplicationContext(), R.string.toast_error_net);
            }
        };
    }
}
