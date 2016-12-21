package com.djx.pin.activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.djx.pin.R;
import com.djx.pin.adapter.SourceAdapter;
import com.djx.pin.base.OldBaseActivity;
import com.djx.pin.beans.SourceInfo;
import com.djx.pin.beans.StaticBean;
import com.djx.pin.serverapiconfig.ServerAPIConfig;
import com.djx.pin.utils.AndroidAsyncHttp;
import com.djx.pin.utils.LogUtil;
import com.djx.pin.utils.ScrollViewSetListViewHeight;
import com.djx.pin.utils.ToastUtil;
import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

/**
 * Created by Administrator on 2016/7/22 0022.
 */
public class MyHelperTaskSourceActivity extends OldBaseActivity implements View.OnClickListener {

    LinearLayout ll_Back_MHTSA;
    ListView lv_TaskSource_MHTSA;
    TextView tv_SourceDescribe_MHTSA;
    SourceAdapter adapter;
    Context CONTEXT = MyHelperTaskSourceActivity.this;
    SharedPreferences sp;
    Bundle bundle;
    int index = 0, size = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_myhelpertasksource);
        initView();
        initEvent();
        getData(0);
    }

    private void initEvent() {
        ll_Back_MHTSA.setOnClickListener(this);
        tv_SourceDescribe_MHTSA.setMovementMethod(ScrollingMovementMethod.getInstance());
    }

    private void initView() {
        sp = getSharedPreferences(StaticBean.USER_INFO, MODE_PRIVATE);
        bundle=getIntent().getExtras();
        ll_Back_MHTSA = (LinearLayout) findViewById(R.id.ll_Back_MHTSA);
        lv_TaskSource_MHTSA = (ListView) findViewById(R.id.lv_TaskSource_MHTSA);
        tv_SourceDescribe_MHTSA = (TextView) findViewById(R.id.tv_SourceDescribe_MHTSA);

        adapter = new SourceAdapter(this);

        lv_TaskSource_MHTSA.setAdapter(adapter);
        ScrollViewSetListViewHeight.setListViewHeightBasedOnChildren(lv_TaskSource_MHTSA);
    }

    /**
     * Called when a view has been clicked.
     *
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_Back_MHTSA:
                this.finish();
                break;
        }
    }


    private void getData(int index) {
        AsyncHttpResponseHandler res = new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                String str_json = new String(bytes);
                try {
                    JSONObject obj = new JSONObject(str_json);
                    if (0 != obj.getInt("code")) {
                        errorCode(obj.getInt("code"));
                        LogUtil.e(CONTEXT, "数据解析错误");
                        return;
                    }
                    SourceInfo info=new SourceInfo();
                    Gson gson = new Gson();
                    info = gson.fromJson(obj.getJSONObject("result").toString(), SourceInfo.class);
                    adapter.addDataList(info.list);
                    adapter.notifyDataSetChanged();
                    ScrollViewSetListViewHeight.setListViewHeightBasedOnChildren(lv_TaskSource_MHTSA);
                } catch (JSONException e) {
                    e.printStackTrace();
                    LogUtil.e(CONTEXT, "getData enter cathc=" + e.toString());
                }
            }
            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                LogUtil.e(CONTEXT, "网络连接失败");
                ToastUtil.shortshow(CONTEXT, R.string.toast_error_net);
            }
        };

        String url = ServerAPIConfig.Get_SorceList + "session_id=" + sp.getString("session_id", null) + "&id=" + bundle.getString("id") + "&receiver_id=" + bundle.getString("receiver_id") + "&index=" + index + "&size=" + 10 + "&type=" + bundle.getInt("type");
        AndroidAsyncHttp.get(url,res);
    }
}
