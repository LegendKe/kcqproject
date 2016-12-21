package com.djx.pin.utils;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import com.djx.pin.beans.StaticBean;
import com.djx.pin.serverapiconfig.ServerAPIConfig;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

/**
 * Created by Administrator on 2016/7/13 0013.
 */
public class BundleOrChangePhone {

    /**
     * {
     * "session_id": "8527a2b6a58b4fa963fd",
     * "sms_code": "231512",
     * "country_code": "0086",
     * "mobile": "13112345678",
     * "password": "password"
     * }
     * 绑定手机号
     */

    public static void RequsetBundlePhone(String session_id, String sms_code, String country_code, String mobile, String password, final Activity activity) {
        String url = ServerAPIConfig.BundleOrChangePhone;


        RequestParams params = new RequestParams();
        params.put("session_id", session_id);
        params.put("sms_code", sms_code);
        params.put("country_code", country_code);
        params.put("mobile", mobile);
        params.put("password", password);
        Log.e("url====", url + params.toString());
        AsyncHttpResponseHandler upHandler = new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                String str = new String(bytes);
                Log.e("str==", str);
                try {
                    JSONObject object = new JSONObject(str);
                    int code = object.getInt("code");
                    if (code == 0) {
                        ToastUtil.shortshow(activity.getApplicationContext(), "手机绑定成功");
                        activity.getSharedPreferences(StaticBean.KEY, Context.MODE_PRIVATE).edit().putBoolean("isPhoneBundle", true).commit();
                        activity.finish();
                    } else if (code == 2003) {
                        activity.getSharedPreferences(StaticBean.KEY, Context.MODE_PRIVATE).edit().putBoolean("isPhoneBundle", false).commit();
                        ToastUtil.shortshow(activity.getApplicationContext(), "手机号已被占用");
                    } else if (code == 2007) {
                        activity.getSharedPreferences(StaticBean.KEY, Context.MODE_PRIVATE).edit().putBoolean("isPhoneBundle", false).commit();
                        ToastUtil.shortshow(activity.getApplicationContext(), "手机号错误");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                Log.e("绑定手机失败", "绑定手机失败");
            }
        };
        AndroidAsyncHttp.post(url, params, upHandler);

    }

    /**
     * {
     * "session_id": "8527a2b6a58b4fa963fd",
     * "sms_code": "231512",
     * "country_code": "0086",
     * "mobile": "13112345678",
     * "password": "password"
     * }
     * 修改绑定手机号
     */

    public static void RequsetChangeBundlePhone(String session_id, String sms_code, String country_code, String mobile, String password, final Activity activity) {
        String url = ServerAPIConfig.BundleOrChangePhone;


        RequestParams params = new RequestParams();
        params.put("session_id", session_id);
        params.put("sms_code", sms_code);
        params.put("country_code", country_code);
        params.put("mobile", mobile);
        params.put("password", password);
        Log.e("url====", url + params.toString());
        AsyncHttpResponseHandler upHandler = new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                String str = new String(bytes);
                Log.e("str==", str);
                try {
                    JSONObject object = new JSONObject(str);
                    int code = object.getInt("code");
                    if (code == 0) {
                        activity.getSharedPreferences(StaticBean.KEY, Context.MODE_PRIVATE).edit().putBoolean("isPhoneChanged", true).commit();
                        ToastUtil.shortshow(activity.getApplicationContext(), "修改手机绑定成功");
                        activity.finish();
                    } else if (code == 2003) {
                        activity.getSharedPreferences(StaticBean.KEY, Context.MODE_PRIVATE).edit().putBoolean("isPhoneChanged", false).commit();
                        ToastUtil.shortshow(activity.getApplicationContext(), "手机号已被占用");
                    } else if (code == 2007) {
                        activity.getSharedPreferences(StaticBean.KEY, Context.MODE_PRIVATE).edit().putBoolean("isPhoneChanged", false).commit();
                        ToastUtil.shortshow(activity.getApplicationContext(), "手机号错误");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                Log.e("修改绑定手机失败", "修改绑定手机失败");
            }
        };
        AndroidAsyncHttp.post(url, params, upHandler);

    }

    /**
     * {
     * "session_id": "8527a2b6a58b4fa963fd",
     * "country_code": "0086",
     * "mobile": "13112345678",
     * "sms_code": "123112",
     * "password": "password"
     * }修改密码
     */
    public static void ChangePassword(String session_id, String sms_code, String country_code, String mobile, String password, final Activity activity) {
        String url = ServerAPIConfig.ChangePassword;


        RequestParams params = new RequestParams();
        params.put("session_id", session_id);
        params.put("sms_code", sms_code);
        params.put("country_code", country_code);
        params.put("mobile", mobile);
        params.put("password", password);
        Log.e("url====", url + params.toString());
        AsyncHttpResponseHandler upHandler = new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                String str = new String(bytes);
                Log.e("str==", str);
                try {
                    JSONObject object = new JSONObject(str);
                    int code = object.getInt("code");
                    if (code == 0) {
                        ToastUtil.shortshow(activity.getApplicationContext(), "修改密码成功");
                        activity.finish();
                    } else if (code == 2003) {
                        ToastUtil.shortshow(activity.getApplicationContext(), "手机号已被占用");
                    } else if (code == 2007) {
                        ToastUtil.shortshow(activity.getApplicationContext(), "手机号错误");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                Log.e("密码修改失败", "密码修改失败");
            }
        };
        AndroidAsyncHttp.post(url, params, upHandler);

    }
}
