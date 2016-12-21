package com.djx.pin.base.baseui;

import com.djx.pin.beans.Entity;
import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

/**
 * Created by 柯传奇 on 2016/11/17 0017.
 */
public class DetailActivity<T extends Entity> extends BaseActivity {



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

}
