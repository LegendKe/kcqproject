package com.djx.pin.activity;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.djx.pin.R;
import com.djx.pin.adapter.LookPurseDetailAdapter;
import com.djx.pin.base.OldBaseActivity;
import com.djx.pin.beans.PurseLogDetailBean;
import com.djx.pin.beans.PurseLogItemBean;
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
public class LookPurseDetailActivity extends OldBaseActivity implements View.OnClickListener, AbsListView.OnScrollListener {
    final String TAG = LookPurseDetailActivity.class.getSimpleName();
    private LinearLayout ll_Back_LPDA;
    private TextView tv_withdraw_log;
    private PullToRefreshListView lv_LPDA;
    private LookPurseDetailAdapter adapter;
    private String id,//求助id
            prev_user_id;//上一节点用户id,即PIN的用户user_id.
    private String session_id;
    private int content_type;
    private boolean mIsScrollUp;
    private Context CONTEXT = LookPurseDetailActivity.this;
    private final int itemCntPerPage = 10;

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

private void requestData(int index) {
    session_id = UserInfo.getSessionID(this);
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
    String url = ServerAPIConfig.balanceLog + "session_id=" + session_id + "&index=" + index + "&size=" + itemCntPerPage + "&content_type=" + content_type;
    Log.i(TAG,"url is " + url);
    AndroidAsyncHttp.get(url, res);
}

@Override
public void onScrollStateChanged(AbsListView view, int scrollState) {
//    Log.i(TAG, "onScrollStateChanged " + scrollState);
}

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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lookpursedetail);
        content_type = getIntent().getIntExtra("content_type", 1);
        initView();
        initEvent();
        adapter=new LookPurseDetailAdapter(this);
        lv_LPDA.setAdapter(adapter);
        requestData(0);
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
            PurseLogDetailBean retdetail = gson.fromJson(obj.getJSONObject("result").toString(), PurseLogDetailBean.class);

            if (retdetail == null) {
                completeRefresh();
                return;
            }

            List<PurseLogItemBean> retlist = retdetail.getList();

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
            LogUtil.e(CONTEXT, "enter catch=" + e.toString());
            e.printStackTrace();
        }

        if (lv_LPDA.isRefreshing()) {
            LogUtil.e("share onRefreshComplete");
            lv_LPDA.onRefreshComplete();
        }
    }

    private void completeRefresh() {
        if (lv_LPDA.isRefreshing()) {
            LogUtil.e("share onRefreshComplete");
            lv_LPDA.onRefreshComplete();
        }
    }

    private boolean compareTo(List<PurseLogItemBean> list, PurseLogItemBean item) {
        int s = list.size();
        if (item != null) {
            for (int i = 0; i < s; i++) {
                if (item.getTarget_id().equals(list.get(i).getTarget_id())) {
                    return true;
                }
            }
        }
        return false;
    }

    private void initEvent() {
        ll_Back_LPDA.setOnClickListener(this);
        //可以上下拉刷新
        lv_LPDA.setMode(PullToRefreshBase.Mode.PULL_FROM_END);
        //设置刷新监听
        lv_LPDA.setOnScrollListener(this);
        lv_LPDA.setOnRefreshListener(onRefreshListener);
        tv_withdraw_log.setOnClickListener(this);
    }

    private void initView() {
        ll_Back_LPDA= (LinearLayout) findViewById(R.id.ll_Back_LPDA);
        lv_LPDA= (PullToRefreshListView) findViewById(R.id.lv_LPDA);
        tv_withdraw_log = (TextView) findViewById(R.id.tv_withdraw_log);
        if (content_type == 2) {
            tv_withdraw_log.setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.ll_Back_LPDA:
                this.finish();
                break;
            case R.id.tv_withdraw_log:
                startActivity(LookWithdrawLogActivity.class);
                break;
        }
    }
}
