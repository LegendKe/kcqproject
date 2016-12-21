package com.djx.pin.activity;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import com.google.gson.Gson;
import com.djx.pin.R;
import com.djx.pin.adapter.GoHomeHistoryAdapter;
import com.djx.pin.base.OldBaseActivity;
import com.djx.pin.beans.StaticBean;
import com.djx.pin.beans.GoHomeHistoryInfo;
import com.djx.pin.pullableview.PullToRefreshLayout;
import com.djx.pin.pullableview.PullableListView;
import com.djx.pin.serverapiconfig.ServerAPIConfig;
import com.djx.pin.utils.AndroidAsyncHttp;
import com.djx.pin.utils.LogUtil;
import com.djx.pin.utils.ToastUtil;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class GoHomeHistoryActivity extends OldBaseActivity implements View.OnClickListener, PullToRefreshLayout.OnRefreshListener {

    private LinearLayout ll_back;
    private PullToRefreshLayout refresh_view;
    private PullableListView lv;
    private GoHomeHistoryAdapter adapter;
    private Context THIS = GoHomeHistoryActivity.this;
    private int size = 10;//listview中的每页数量
    private int index = 0;//listview中的分页页码

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_windowthrowhistory);
        initView();
        initEvent();
        getData(0);
    }
    private void initView() {
        ll_back = (LinearLayout) findViewById(R.id.ll_back);
        refresh_view = (PullToRefreshLayout) findViewById(R.id.refresh_view);
        lv = (PullableListView) findViewById(R.id.lv);
        lv.setCanPullDown(false);
        lv.setDivider(new ColorDrawable(0xFFF4F4F4));
        lv.setDividerHeight(30);
        adapter = new GoHomeHistoryAdapter(this);
        lv.setAdapter(adapter);
    }

    /**
     * 用来获取listview中的数据并加载到listview中
     * 首次获取数据默认页码为0
     *
     * @param index listview的页码
     */
    public void getData(int index) {
        String session_id = getSharedPreferences(StaticBean.USER_INFO, MODE_PRIVATE).getString("session_id", null);
        String url = ServerAPIConfig.Get_GoHomeList + "session_id=" + session_id + "&index=" + index + "&size=" + size;
        AsyncHttpResponseHandler res = new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                String str_json = new String(bytes);
                try {
                    JSONObject obj = new JSONObject(str_json);
                    if (0 != obj.getInt("code")) {
                        LogUtil.e(THIS, "json解析异常");
                        errorCode(obj.getInt("code"));
                        return;
                    }
                    obj = obj.getJSONObject("result");
                    Gson gson = new Gson();
                    GoHomeHistoryInfo info = gson.fromJson(obj.toString(), GoHomeHistoryInfo.class);
                    adapter.addDataList(info.list);
                    adapter.notifyDataSetChanged();
                    refresh_view.loadmoreFinish(PullToRefreshLayout.SUCCEED);
                } catch (JSONException e) {
                    LogUtil.e(THIS, "enter chatch");
                    e.printStackTrace();
                }
            }
            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                LogUtil.e(THIS, "网络连接异常");
                ToastUtil.shortshow(THIS, R.string.toast_error_net);
                refresh_view.loadmoreFinish(PullToRefreshLayout.FAIL);
            }
        };

        AndroidAsyncHttp.get(url, null, res);
    }

    private void initEvent() {
        ll_back.setOnClickListener(this);
        refresh_view.setOnRefreshListener(this);
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_back:
                finish();
                break;
        }
    }
    @Override
    public void onRefresh(PullToRefreshLayout pullToRefreshLayout) {

    }
    @Override
    public void onLoadMore(PullToRefreshLayout pullToRefreshLayout) {
        ++index;
        getData(index);
    }
}
