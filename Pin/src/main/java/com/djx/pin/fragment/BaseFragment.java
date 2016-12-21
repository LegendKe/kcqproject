package com.djx.pin.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class BaseFragment extends Fragment {

    protected AsyncHttpResponseHandler mHandler = new AsyncHttpResponseHandler() {
        @Override
        public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
            String str_json = new String(responseBody);
            JSONObject jsonObject = null;
            try {
                jsonObject = new JSONObject(str_json);
                if (0 == jsonObject.getInt("code")) {
                    Gson gson = new Gson();
                    parseData(gson, jsonObject.getJSONObject("result").toString());
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        @Override
        public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

        }
    };

    protected void parseData(Gson gson, String json) {}//解析数据,子类重写


    /***
     * 实现Activity之间不传值跳转的方法
     */
    public void startActivity(Class<?> Class) {
        Intent intent = new Intent(getContext(), Class);
        this.startActivity(intent);
    }
    /***
     * 实现Activity之间传值跳转
     */
    public void startActivity(Class<?> Class, Bundle bundle) {
        Intent intent = new Intent(getContext(), Class);
        intent.putExtras(bundle);
        this.startActivity(intent);
    }
/**********************************************************************************************/
    /**
     * 点击并传值的接口的对象
     */
    protected FragmentCallBack callBack;
    /**
     * 点击接口对象
     */
    protected FragmentOnClick click;

    /**
     * 点击事件接口：
     */
    public interface FragmentOnClick {
        void setClickLisener(int viewTag);
    }

    /**
     * 点击并传值的接口：
     */
    public interface FragmentCallBack {
        void setCallBackLisener(int viewTag, Bundle bundle);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (activity instanceof FragmentOnClick) {
            click = (FragmentOnClick) activity;
        }
        if (activity instanceof FragmentCallBack) {
            callBack = (FragmentCallBack) activity;
        }
    }

}
