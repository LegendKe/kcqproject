package com.djx.pin.beans;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.djx.pin.R;
import com.djx.pin.application.PinApplication;
import com.djx.pin.business.AppConstants;
import com.djx.pin.receiver.MyReceiveUnreadCountChangedListener;
import com.djx.pin.serverapiconfig.ServerAPIConfig;
import com.djx.pin.utils.AndroidAsyncHttp;
import com.djx.pin.utils.LogUtil;
import com.djx.pin.utils.ToastUtil;
import com.djx.pin.utils.myutils.ConstantUtils;
import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;
import de.greenrobot.event.EventBus;
import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by Administrator on 2016/6/17.
 */
public class UserInfo {
    protected final static String TAG = UserInfo.class.getSimpleName();
    private static SharedPreferences sp;

    /**
     * 获取用户信息
     *
     * @param session_id 用户的session_id
     * @param user_id    用户的user_id
     */
    public static void getUserInfo(final Context context, String session_id, String user_id) {
        sp=context.getSharedPreferences(StaticBean.USER_INFO,MODE_PRIVATE);
        AndroidAsyncHttp.get(ServerAPIConfig.Get_UserInfo + "session_id=" + session_id + "&user_id=" + user_id, null, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                String str_json = new String(bytes);
                Log.i(TAG, "UserInfo is " + str_json);
                try {
                    JSONObject obj = new JSONObject(str_json);
                    if (obj.getInt("code") != 0) {
                        ToastUtil.shortshow(context, R.string.toast_auto_login_failure);
                        sp.edit().putBoolean("isLogined",false).commit();
                        return;
                    }
                    Gson gson = new Gson();
                    UserBean user = gson.fromJson(obj.getJSONObject("result").toString(), UserBean.class);
                    if (user == null) {
                        return;
                    }

                    updateUserInfoPref(context, user);
                } catch (JSONException e) {
                    LogUtil.e( "PinApplication enter catch");
                    e.printStackTrace();
                }
            }
            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                ToastUtil.shortshow(context, R.string.toast_error_net);
                LogUtil.e("PinApplication 网络连接异常");
            }
        });

    }
    /**
     * 获取用户信息
     *
     * @param activity 调用此方法的activity
     */
    public static void getUserInfo(final Class<?> Class, final Activity activity) {
        String session_id = activity.getSharedPreferences(StaticBean.USER_INFO, MODE_PRIVATE).getString("session_id", null);
        String user_id = activity.getSharedPreferences(StaticBean.USER_INFO, MODE_PRIVATE).getString("user_id", null);

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
                        Gson gson = new Gson();
                        UserBean user = gson.fromJson(obj.getJSONObject("result").toString(), UserBean.class);
                        if (user == null) {
                            return;
                        }

                        updateUserInfoPref(activity.getApplicationContext(), user);
//
//                    public void startActivity(Class<?> Class) {
//                        Intent intent = new Intent(this, Class);
//                        this.startActivity(intent);
                        Intent intent=new Intent(activity,Class);
                        activity.startActivity(intent);
                        activity.finish();
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
    /**
     * 获取用户信息
     *
     * @param activity 调用此方法的activity
     */
    public static void getUserInfo( final Activity activity) {
        String session_id = activity.getSharedPreferences(StaticBean.USER_INFO, MODE_PRIVATE).getString("session_id", null);
        String user_id = activity.getSharedPreferences(StaticBean.USER_INFO, MODE_PRIVATE).getString("user_id", null);

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
                        Gson gson = new Gson();
                        UserBean user = gson.fromJson(obj.getJSONObject("result").toString(), UserBean.class);
                        if (user == null) {
                            return;
                        }

                        updateUserInfoPref(activity.getApplicationContext(), user);

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

    /**
     * 获取用户信息的回调接口
     * 返回值是false表示获取用户信息失败,true表示获取用户信息成功
     */
    public interface GetUserInfoCallBack {
        void callback(boolean state);
    }

    /**
     * 获取用户信息
     *
     */
    public static void getUserInfo(final Context context, final Handler handler) {
        String session_id = getSessionID(context);
        String user_id = getUserID(context);

        if (session_id == null||(session_id.equals("") ) || ( user_id == null)||user_id.equals("") ) {

            ToastUtil.shortshow(context, "请先登录应用");
        } else {
            AndroidAsyncHttp.get(ServerAPIConfig.Get_UserInfo + "session_id=" + session_id + "&user_id=" + user_id, null, new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int i, Header[] headers, byte[] bytes) {
                    String str_json = new String(bytes);
                    Log.i(TAG, str_json);
                    try {
                        JSONObject obj = new JSONObject(str_json);
                        if (obj.getInt("code") != 0) {
                            ToastUtil.shortshow(context, R.string.toast_login_failure);
                            return;
                        }
                        Gson gson = new Gson();
                        UserBean user = gson.fromJson(obj.getJSONObject("result").toString(), UserBean.class);
                        if (user == null) {
                            return;
                        }

                        updateUserInfoPref(context.getApplicationContext(), user);

                        Log.i(TAG, "mHandler");
                        Message message = new Message();//发送一个消息，该消息用于在handleMessage中区分是谁发过来的消息；
                        message.what = 1;
                        handler.sendMessage(message);
                    } catch (JSONException e) {
                        LogUtil.e(context, "enter catch");
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                    ToastUtil.shortshow(context, R.string.toast_error_net);
                    LogUtil.e(context, "网络连接异常");
                }
            });
        }
    }

    /**
     * 获取用户信息
     *
     * @param session_id          用户的session_id
     * @param user_id             用户的user_id
     * @param getUserInfoCallBack 获取用户信息回调接口
     */
    public static void getUserInfo(final Context context, String session_id, String user_id, final GetUserInfoCallBack getUserInfoCallBack) {
        AndroidAsyncHttp.get(ServerAPIConfig.Get_UserInfo + "session_id=" + session_id + "&user_id=" + user_id, null, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                String str_json = new String(bytes);

                try {
                    JSONObject obj = new JSONObject(str_json);
                    if (obj.getInt("code") != 0) {
                        ToastUtil.shortshow(context, R.string.toast_login_failure);
                        return;
                    }

                    Gson gson = new Gson();
                    UserBean user = gson.fromJson(obj.getJSONObject("result").toString(), UserBean.class);
                    if (user == null) {
                        return;
                    }
                    UserInfo.updateUserInfoPref(context, user);

                    getUserInfoCallBack.callback(true);
                } catch (JSONException e) {
                    getUserInfoCallBack.callback(false);
                    LogUtil.e(context, "enter catch");
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                ToastUtil.shortshow(context, R.string.toast_error_net);
                LogUtil.e(context, "网络连接异常");
            }
        });
    }

    public static void getUserInfo(final Context context, final GetUserInfoCallBack getUserInfoCallBack) {
        String session_id = getSessionID(context);
        String user_id = getUserID(context);
        AndroidAsyncHttp.get(ServerAPIConfig.Get_UserInfo + "session_id=" + session_id + "&user_id=" + user_id, null, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                String str_json = new String(bytes);

                try {
                    JSONObject obj = new JSONObject(str_json);
                    if (obj.getInt("code") != 0) {
                        ToastUtil.shortshow(context, R.string.toast_login_failure);
                        return;
                    }

                    Gson gson = new Gson();
                    UserBean user = gson.fromJson(obj.getJSONObject("result").toString(), UserBean.class);
                    if (user == null) {
                        return;
                    }
                    updateUserInfoPref(context, user);

                    getUserInfoCallBack.callback(true);
                } catch (JSONException e) {
                    getUserInfoCallBack.callback(false);
                    LogUtil.e(context, "enter catch");
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                ToastUtil.shortshow(context, R.string.toast_error_net);
                LogUtil.e(context, "网络连接异常");
            }
        });
    }
    public static void updateUserInfoPref(Context context, UserBean user) {
        Log.i(TAG, "updateUserInfoPref");
        SharedPreferences.Editor et = context.getSharedPreferences(StaticBean.USER_INFO, MODE_PRIVATE).edit();
        et.putString("user_id", user.getUser_id());
        et.putString("nickname", user.getNickname());
        et.putString("portrait", user.getPortrait());
        et.putInt("gender", user.getGender());
        et.putString("mobile", user.getMobile());//手机号
        et.putString("country_code", user.getCountry_code());
        et.putString("province", user.getProvince());
        et.putString("city", user.getCity());
        et.putString("district", user.getDistrict());
        et.putString("birthday", user.getBirthday());
        et.putInt("credit", user.getCredit());
        et.putInt("rank", user.getRank());
        et.putInt("point", user.getPoint());
        et.putFloat("balance", user.getBalance());
        et.putFloat("credit_balance", user.getCredit_balance());
        et.putInt("is_auth", user.getIs_auth());
        et.putInt("is_show_location", user.getIs_show_location());
        //可以通过Double.parseDouble()，方法获取经纬度
        et.putString("latitude", user.getLatitude() + "");
        et.putString("longitude", user.getLongitude() + "");
        et.putString("real_name", user.getReal_name());//身份证姓名
        et.putString("id_card", user.getId_card());
        et.putString("id_card_pic", user.getId_card_pic());
        et.putString("emergency_name", user.getEmergency_name());
        et.putString("emergency_country_code", user.getEmergency_country_code());
        et.putString("emergency_mobile", user.getEmergency_mobile());
        et.putString("session_id", user.getSession_id());
        et.putString("rongyun_token", user.getRongyun_token());
        et.putString("zhima_open_id", user.getZhima_open_id());
        et.putString("wish",user.getWish());
        Log.i(TAG, "sos_ongoing is " + user.getSos_ongoing());
        if (user.getSos_ongoing() != null) {
            if (user.getSos_ongoing().size() != 0) {
                et.putString("sos_id", user.getSos_ongoing().get(0).getId());
                et.putInt("sos_status", user.getSos_ongoing().get(0).getStatus());
            }
        }
        et.commit();
    }

    /**
     * 登陆融云,默认先刷新融云Token,然后在登陆
     * @param session_id
     */
    public static void rongLogin(final Context context, String session_id) {
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
                    setRongToken(context, obj.getString("rongyun_token"));
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
    private static void connect(final Context context, String token) {
        if (context.getApplicationInfo().packageName.equals(PinApplication.getCurProcessName(context))) {

            /**
             * IMKit SDK调用第二步,建立与服务器的连接
             */
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
                    LogUtil.e("融云登陆成功" + userid);
                    Intent intent = new Intent(AppConstants.INTENT_ACTION_LOGIN);
                    context.sendBroadcast(intent);
                    EventBus.getDefault().post(new EventBeans(ConstantUtils.RE_LOGIN));
                }

                /**
                 * 连接融云失败
                 * @param errorCode 错误码，可到官网 查看错误码对应的注释
                 */
                @Override
                public void onError(RongIMClient.ErrorCode errorCode) {
                    LogUtil.e("融云登陆失败" + errorCode);
                }
            });
        }
    }

    private static void setMessageListener(Context context) {
        Log.i(TAG, "setMessageListener");
        if (RongIM.getInstance() != null) {
            /**
             * 接收未读消息的监听器。
             *
             * @param listener          接收所有未读消息消息的监听器。
             */
            Log.i(TAG, "setOnReceiveUnreadCountChangedListener");
            RongIM.getInstance().setOnReceiveUnreadCountChangedListener(new MyReceiveUnreadCountChangedListener());
        }
    }

    /**
     * 获取七牛头像
     */
    public static void getRongPortrait(Context context, final String user_id,final String nickname, String id, int media_type) throws UnsupportedEncodingException {
        final JSONObject newObj = new JSONObject();
        try {
            newObj.put("size", 1);

            JSONArray array = new JSONArray();
            JSONObject detaileObj = new JSONObject();
            detaileObj.put("id", id);
            detaileObj.put("media_type", media_type);
            array.put(detaileObj);
            newObj.put("list", array);
            StringEntity entity = new StringEntity(newObj.toString(), "utf-8");

            AsyncHttpResponseHandler res = new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int i, Header[] headers, byte[] bytes) {
                    String str = new String(bytes);
                    try {
                        JSONObject obj = new JSONObject(str);
                        obj = obj.getJSONObject("result");
                        JSONArray array1 = obj.getJSONArray("list");
                        for (int j = 0; j < array1.length(); j++) {
                            obj = array1.getJSONObject(j);
                            Log.e("rongim","---------------refreshUserInfoCache------------------");
                            RongIM.getInstance().refreshUserInfoCache(new io.rong.imlib.model.UserInfo(user_id, nickname, Uri.parse( obj.getString("url"))));
                            //知messagectivity刷新
                            LogUtil.e("================知messagectivity刷新=========================================");
                            EventBus.getDefault().post(new EventBeans(ConstantUtils.RE_LOGIN));

                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                    Log.e("失败", "失败");
                }
            };
            AndroidAsyncHttp.post(context, ServerAPIConfig.GetQiNiuUrl, entity, " application/json;charset=UTF-8", res);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static boolean getIsLogin(Context context) {
        return context.getSharedPreferences(StaticBean.USER_INFO, MODE_PRIVATE).getBoolean("isLogined", false);
    }
    public static int getIsAuth(Context context) {
        return context.getSharedPreferences(StaticBean.USER_INFO, MODE_PRIVATE).getInt("is_auth", 0);
    }

    public static String getRealName(Context context) {
        return context.getSharedPreferences(StaticBean.USER_INFO, MODE_PRIVATE).getString("real_name", null);
    }

    public static String getUserID(Context context) {
        return context.getSharedPreferences(StaticBean.USER_INFO, MODE_PRIVATE).getString("user_id", null);
    }
    public static String getIdCard(Context context) {
        return context.getSharedPreferences(StaticBean.USER_INFO, MODE_PRIVATE).getString("id_card", null);
    }

    public static int getPoint(Context context) {
        return context.getSharedPreferences(StaticBean.USER_INFO, MODE_PRIVATE).getInt("point", -1);
    }

    public static Float getCreditBalance(Context context) {
        return context.getSharedPreferences(StaticBean.USER_INFO, MODE_PRIVATE).getFloat("credit_balance", -1);
    }
    public static int getCredit(Context context) {
        return context.getSharedPreferences(StaticBean.USER_INFO, MODE_PRIVATE).getInt("credit", -1);
    }
    public static String getPortrait(Context context) {
        return context.getSharedPreferences(StaticBean.USER_INFO, MODE_PRIVATE).getString("portrait", null);
    }

    public static String getMobile(Context context) {
        return context.getSharedPreferences(StaticBean.USER_INFO, MODE_PRIVATE).getString("mobile", "");
    }

    public static String getNickname(Context context) {
        return context.getSharedPreferences(StaticBean.USER_INFO, MODE_PRIVATE).getString("nickname", null);
    }

    public static String getSessionID(Context context) {
        return context.getSharedPreferences(StaticBean.USER_INFO, MODE_PRIVATE).getString("session_id", null);
    }

    public static String getZhiMaOpenID(Context context) {
        return context.getSharedPreferences(StaticBean.USER_INFO, MODE_PRIVATE).getString("zhima_open_id", null);
    }

    public static String getSOSId(Context context) {
        return context.getSharedPreferences(StaticBean.USER_INFO, MODE_PRIVATE).getString("sos_id", null);
    }

    public static int getSOSStatus(Context context) {
        return context.getSharedPreferences(StaticBean.USER_INFO, MODE_PRIVATE).getInt("sos_status", -1);
    }
    public static void putSOSStatus(Context context,int status) {
        SharedPreferences sp = context.getSharedPreferences(StaticBean.USER_INFO, MODE_PRIVATE);
        sp.edit().putInt("sos_status",status);
    }

    public static void clearSOSOngoing(Context context) {
        SharedPreferences.Editor et = context.getSharedPreferences(StaticBean.USER_INFO, MODE_PRIVATE).edit();
        et.putString("sos_id", "");
        et.putInt("sos_status", -1);
        et.commit();
    }

    public static void setZhiMaOpenID(Context context, String open_id) {
        SharedPreferences.Editor et = context.getSharedPreferences(StaticBean.USER_INFO, MODE_PRIVATE).edit();
        et.putString("zhima_open_id", open_id);
        et.commit();
    }

    public static void setRongToken(Context context, String rong_token) {
        SharedPreferences.Editor et = context.getSharedPreferences(StaticBean.USER_INFO, MODE_PRIVATE).edit();
        et.putString("rongyun_token", rong_token);
        et.commit();
    }

    public static Float getBalance(Context context) {
        float balance = context.getSharedPreferences(StaticBean.USER_INFO, MODE_PRIVATE).getFloat("balance", 0);
        Log.i(TAG, "balance is " + balance);
        return balance;
    }
}
