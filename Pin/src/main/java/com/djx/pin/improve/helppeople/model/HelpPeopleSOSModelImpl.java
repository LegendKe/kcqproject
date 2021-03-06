package com.djx.pin.improve.helppeople.model;

import android.content.Context;

import com.djx.pin.beans.HelpPeopleEntity;
import com.djx.pin.improve.ZhongMiAPI;
import com.djx.pin.beans.PageBean;
import com.djx.pin.improve.helppeople.presenter.HelpPeopleSOSPresenter;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

/**
 * Created by 柯传奇 on 2016/12/3 0003.
 */
public class HelpPeopleSOSModelImpl implements HelpPeopleSOSModel {

    Context context;
    private HelpPeopleSOSPresenter presenter;
    private OnDataCompleteListener listener;

    public HelpPeopleSOSModelImpl(Context context, HelpPeopleSOSPresenter presenter) {
        this.context = context;
        this.presenter = presenter;
    }

    /**
     * 加载数据
     *
     * @param listener
     */
    @Override
    public void loadData(OnDataCompleteListener listener) {
        this.listener = listener;
        ZhongMiAPI.getHelpPeopleSOSList(context,mHandler);
    }


    /**
     * 请求的数据处理--解析
     */
    private AsyncHttpResponseHandler mHandler = new AsyncHttpResponseHandler() {
        @Override
        public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
            String str_json = new String(responseBody);
            JSONObject jsonObject = null;
            try {
                jsonObject = new JSONObject(str_json);
                if (0 == jsonObject.getInt("code")) {
                    Gson gson = new Gson();
                    PageBean<HelpPeopleEntity> pageBean = gson.fromJson(jsonObject.getJSONObject("result").toString(), new TypeToken<PageBean<HelpPeopleEntity>>() {}.getType());
                    listener.response(pageBean.getList());
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        @Override
        public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
        }
    };

}
