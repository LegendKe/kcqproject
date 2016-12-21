package com.djx.pin.improve.positiveenergy.detail;

import android.content.Context;

import com.djx.pin.beans.CivilizationDetailCommentInfo;
import com.djx.pin.improve.ZhongMiAPI;
import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

/**
 * Created by 柯传奇 on 2016/12/9 0009.
 */
public class WishTreeDetailPresenter {

    private final Context context;
    WishTreeDetailFragment fragment;
    public WishTreeDetailPresenter(Context context,WishTreeDetailFragment fragment) {
        this.context = context;
        this.fragment = fragment;
    }

    public void loadData(String id,int index,int type) {

        ZhongMiAPI.getCultureWallDetail(context, id, index, type, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                fragment.loadSuccess();
                String str_json = new String(responseBody);
                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(str_json);
                    if (0 == jsonObject.getInt("code")) {
                        Gson gson = new Gson();
                        CivilizationDetailCommentInfo.Result result = gson.fromJson(jsonObject.getJSONObject("result").toString(), CivilizationDetailCommentInfo.Result.class);
                        if (result.comment.size() < 10) {
                            //无更多数据
                            fragment.onNoMoreData();
                        }
                        fragment.showData(result);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                fragment.loadFail();
            }
        });
    }
}
