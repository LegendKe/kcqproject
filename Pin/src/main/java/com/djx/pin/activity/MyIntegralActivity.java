package com.djx.pin.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.djx.pin.R;
import com.djx.pin.adapter.MyIntegralAdapter;
import com.djx.pin.base.OldBaseActivity;
import com.djx.pin.beans.MyPointDetailBean;
import com.djx.pin.beans.MyPointItemBean;
import com.djx.pin.beans.StaticBean;
import com.djx.pin.beans.UserInfo;
import com.djx.pin.serverapiconfig.ServerAPIConfig;
import com.djx.pin.utils.AndroidAsyncHttp;
import com.djx.pin.utils.LogUtil;
import com.djx.pin.utils.ToastUtil;
import com.google.gson.Gson;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import cz.msebera.android.httpclient.Header;

/**
 * Created by Administrator on 2016/6/30.
 */
public class MyIntegralActivity extends OldBaseActivity implements View.OnClickListener, AbsListView.OnScrollListener {
    protected final static String TAG = MyIntegralActivity.class.getSimpleName();
    private LinearLayout ll_Back_MWA;
//    private ListView lv_LPDA;
    private PullToRefreshListView lv_LPDA;
    private MyIntegralAdapter adapter;
    private TextView tv_AllIntegral_MWA;
    private String session_id;
    private final int itemCntPerPage = 10;
    private boolean mIsScrollUp;

    PullToRefreshBase.OnRefreshListener onRefreshListener = new PullToRefreshBase.OnRefreshListener() {
        @Override
        public void onRefresh(PullToRefreshBase refreshView) {
            Log.i(TAG, "OnRefreshListener");
            if (mIsScrollUp) {
                Log.i(TAG, "up pull request");
                requestData(adapter.getCount());
            } else {
                Log.i(TAG, "down pull request");
                requestData(0);
            }
        }
    };
    private TextView tv_integral;

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
//    Log.i(TAG, "onScroll " + firstVisibleItem + " " + totalItemCount);
        if (firstVisibleItem == 0) {
            mIsScrollUp = false;
        } else {
            mIsScrollUp = true;
        }
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
//    Log.i(TAG, "onScrollStateChanged " + scrollState);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_myintegral);
        initView();
        initEvent();
        adapter=new MyIntegralAdapter(this);
        lv_LPDA.setAdapter(adapter);
        initData();
    }

    private void initData() {
        requestData(0);
        tv_AllIntegral_MWA.setText(UserInfo.getPoint(this) + "");

    }

    private void initEvent() {
        ll_Back_MWA.setOnClickListener(this);
        //可以上下拉刷新
        lv_LPDA.setMode(PullToRefreshBase.Mode.BOTH);
        //设置刷新监听
        lv_LPDA.setOnScrollListener(this);
        lv_LPDA.setOnRefreshListener(onRefreshListener);
    }

    private void initView() {
        ll_Back_MWA= (LinearLayout) findViewById(R.id.ll_Back_MWA);
        lv_LPDA= (PullToRefreshListView) findViewById(R.id.lv_MWA);
        tv_AllIntegral_MWA= (TextView) findViewById(R.id.tv_AllIntegral_MWA);
        tv_integral = ((TextView)findViewById(R.id.tv_integral_specification));
        tv_integral.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.ll_Back_MWA:
                this.finish();
                break;
            case R.id.tv_integral_specification:
                Intent intent = new Intent(this,TextActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("url","http://www.dujoy.cn/app/integral/index.html");
                bundle.putInt("TextContent",5);
                intent.putExtras(bundle);
                startActivity(intent);
                break;
        }
    }

    private void requestData(int index) {
        session_id = getSharedPreferences(StaticBean.USER_INFO, Context.MODE_PRIVATE).getString("session_id", null);
        if (session_id == null || session_id.equals("")) {
            ToastUtil.shortshow(getApplicationContext(), "请先登录应用");
            return;
        }
        Log.i(TAG, "session_id is " + session_id);

        AsyncHttpResponseHandler res = new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                parseResponse(bytes);
            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                Log.i(TAG,"onFailure: " + new String(bytes));
//                LogUtil.e(CONTEXT, "网络连接异常");
//                ToastUtil.shortshow(CONTEXT, new String(bytes));
                //FIXME:just for test
                parseResponse(bytes);
            }
        };
        String url = ServerAPIConfig.creditLog + "session_id=" + session_id + "&index=" + index + "&size=" + itemCntPerPage;
        Log.i(TAG,"url is " + url);
        AndroidAsyncHttp.get(url, res);
    }

    private void parseResponse(byte[] bytes)
    {
        Log.i(TAG, "ret json is " + new String(bytes));
        String str_json = new String(bytes);

        try {
            JSONObject obj = new JSONObject(str_json);
            if (0 != obj.getInt("code")) {
                errorCode(obj.getInt("code"));
                return;
            }
            if (null != obj.getJSONObject("result")) {
                LogUtil.e("no result");
            }
            Gson gson = new Gson();
            MyPointDetailBean retdetail = gson.fromJson(obj.getJSONObject("result").toString(), MyPointDetailBean.class);
            if (retdetail == null) {
                completeRefresh();
                return;
            }
            List<MyPointItemBean> retlist = retdetail.getList();
            if (retlist == null) {
                completeRefresh();
                return;
            }

            for (int i = 0; i < retlist.size(); i++) {
                if (compareTo(adapter.getList(), retlist.get(i))) {
                    retlist.remove(i);
                    i--;
                }
            }

            Log.i(TAG, "add list length is " + retlist.size());
            adapter.addDataList(retlist);
            adapter.notifyDataSetChanged();
        } catch (JSONException e) {
            LogUtil.e(this, "enter catch=" + e.toString());
            e.printStackTrace();
        }

        completeRefresh();
    }

    private void completeRefresh() {
        if (lv_LPDA.isRefreshing()) {
            LogUtil.e("share onRefreshComplete");
            lv_LPDA.onRefreshComplete();
        }
    }

    private boolean compareTo(List<MyPointItemBean> list, MyPointItemBean item) {
        int s = list.size();
        if (item != null) {
            for (int i = 0; i < s; i++) {
                if (item.getUser_id().equals(list.get(i).getUser_id())) {
                    return true;
                }
            }
        }
        return false;
    }
}
