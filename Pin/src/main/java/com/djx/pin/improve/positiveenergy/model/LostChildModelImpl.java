package com.djx.pin.improve.positiveenergy.model;

import android.content.Context;

import com.djx.pin.beans.LostChildInfo;
import com.djx.pin.improve.ZhongMiAPI;
import com.djx.pin.improve.positiveenergy.presenter.LostChildPresenter;
import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

/**
 * Created by 柯传奇 on 2016/12/8 0008.
 */
public class LostChildModelImpl implements LostChildModel {


    Context context;
    private LostChildPresenter presenter;
    private LostChildModel.OnDataCompleteListener listener;
    private int index;//分页页码

    public LostChildModelImpl(Context context, LostChildPresenter presenter) {
        this.context = context;
        this.presenter = presenter;
    }

    /**
     * 加载数据
     *
     * @param listener
     */
    @Override
    public void loadData(int index,LostChildModel.OnDataCompleteListener listener) {
        this.listener = listener;
        ZhongMiAPI.getLostChildList(context,index,mHandler);
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
                    LostChildInfo.Result result = gson.fromJson(jsonObject.getJSONObject("result").toString(), LostChildInfo.Result.class);
                    if (result.list.size() < ZhongMiAPI.PAGE_SIZE) {
                        //无更多数据
                        presenter.onNoMoreData();
                    }
                    listener.response(result.list);
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
