package com.djx.pin.improve.helppeople.model;

import android.content.Context;

import com.djx.pin.beans.HelpPeopleEntity;
import com.djx.pin.improve.ZhongMiAPI;
import com.djx.pin.beans.PageBean;
import com.djx.pin.improve.helppeople.presenter.HelpPeopleOfflinePresenter;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

/**
 * Created by 柯传奇 on 2016/12/3 0003.
 */
public class HelpPeopleOfflineModelImpl implements HelpPeopleOfflineModel {


    private final Context context;
    private HelpPeopleOfflinePresenter presenter;
    private OnDataCompleteListener listener;

    public HelpPeopleOfflineModelImpl(Context context,HelpPeopleOfflinePresenter presenter) {
        this.presenter = presenter;
        this.context = context;
    }
    /**
     * 加载数据
     *
     * @param listener
     */
    @Override
    public void loadData(OnDataCompleteListener listener,Double latitude,Double longitude,int pageIndex,int price_min,int price_max,int distance,int order) {
        this.listener = listener;
        ZhongMiAPI.getHelpPeopleOfflineList(context,latitude,longitude,pageIndex,price_min,price_max,distance,order,mHandler);
    }

    /**
     * 请求的数据处理--解析
     */
    private AsyncHttpResponseHandler mHandler = new AsyncHttpResponseHandler() {
        @Override
        public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
            presenter.loadSuccess();
            String str_json = new String(responseBody);

            JSONObject jsonObject = null;
            try {
                jsonObject = new JSONObject(str_json);
                if (0 == jsonObject.getInt("code")) {
                    Gson gson = new Gson();
                    PageBean<HelpPeopleEntity> pageBean = gson.fromJson(jsonObject.getJSONObject("result").toString(), new TypeToken<PageBean<HelpPeopleEntity>>() {}.getType());

                    if (pageBean.getList().size() < ZhongMiAPI.PAGE_SIZE) {
                        //无更多数据
                        presenter.onNoMoreData();
                    }
                    listener.response(pageBean.getList());
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
            presenter.loadFailed();
        }
    };
}
