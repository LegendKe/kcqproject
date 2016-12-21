package com.djx.pin.activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.djx.pin.R;
import com.djx.pin.adapter.RewardSOSDetailAdapter;
import com.djx.pin.base.OldBaseActivity;
import com.djx.pin.beans.MyHelperSOSDetailInfo;
import com.djx.pin.beans.StaticBean;
import com.djx.pin.myview.CircleImageView;
import com.djx.pin.serverapiconfig.ServerAPIConfig;
import com.djx.pin.utils.AndroidAsyncHttp;
import com.djx.pin.utils.DateUtils;
import com.djx.pin.utils.QiniuUtils;
import com.djx.pin.utils.ToastUtil;
import com.google.gson.Gson;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.Date;

import cz.msebera.android.httpclient.Header;

/**
 * Created by Administrator on 2016/7/21 0021.
 */
public class RewardSOSDetailActivity extends OldBaseActivity implements View.OnClickListener {
    protected final static String TAG = RewardSOSDetailActivity.class.getSimpleName();

    LinearLayout ll_Back_MHDA, ll_ShowAllContent_MHDA;
    TextView tv_StopTask_MHDA, tv_UserName_MHDA, tv_Time_MHDA, tv_TaskReward_MHDA,
            tv_DoTaskCount_MHDA, tv_TaskLife_MHDA, tv_Content_MHDA, tv_ShowAllContent_MHDA, tv_TaskDo_MHDA,
            tv_TaskComplete_MHDA, tv_QiangDanCount_MHDA;
    CircleImageView cimg_Avatar_MHDA;
    ImageView img_TaskState_MHDA, img_ShowAllContent_MHDA;
    PullToRefreshListView lv_DoTaskPerson_MHDA;

    RewardSOSDetailAdapter adapter;
    SharedPreferences sp;
    Context CONTEXT = RewardSOSDetailActivity.this;
    Bundle bundle = null;
    int index = 0, size = 10;
    LayoutInflater inflater;
    MyHelperSOSDetailInfo info = new MyHelperSOSDetailInfo();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rewarddetailsos);
        initView();
        initEvent();
        initData();
        adapter = new RewardSOSDetailAdapter(this);
        lv_DoTaskPerson_MHDA.setAdapter(adapter);
        lv_DoTaskPerson_MHDA.setMode(PullToRefreshBase.Mode.BOTH);
        lv_DoTaskPerson_MHDA.setOnRefreshListener(refreshListener);
    }

    PullToRefreshBase.OnRefreshListener2<ListView> refreshListener = new PullToRefreshBase.OnRefreshListener2<ListView>() {
        @Override
        public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
            index = 0;
            adapter.clear();
            getData();
        }
        @Override
        public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
            index = index + 1;
            getData();
        }
    };

    private void initEvent() {
        ll_Back_MHDA.setOnClickListener(this);
        tv_StopTask_MHDA.setOnClickListener(this);
    }


    /**
     * 获取订单信息
     */
    private void getData() {
        AsyncHttpResponseHandler res = new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                String str_json = new String(bytes);
                Log.i(TAG,str_json);
                try {
                    JSONObject obj = new JSONObject(str_json);


                    if (0 != obj.getInt("code")) {
                        Log.i(TAG, "initData 数据解析错误:code=" + obj.toString());
                        return;
                    }
                    Gson gson = new Gson();
                    info = gson.fromJson(obj.getJSONObject("result").toString(), MyHelperSOSDetailInfo.class);
                    adapter.addDataList(info.receiverList.list);
                    adapter.notifyDataSetChanged();
                    if (lv_DoTaskPerson_MHDA.isRefreshing()) {
                        lv_DoTaskPerson_MHDA.onRefreshComplete();
                    }
                } catch (JSONException e) {
                    if (lv_DoTaskPerson_MHDA.isRefreshing()) {
                        lv_DoTaskPerson_MHDA.onRefreshComplete();
                    }
                    Log.i(TAG, "enter catch=" + e.toString());
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                Log.i(TAG, "网络连接异常");
                ToastUtil.shortshow(CONTEXT, R.string.toast_error_net);
                if (lv_DoTaskPerson_MHDA.isRefreshing()) {
                    lv_DoTaskPerson_MHDA.onRefreshComplete();
                }
            }
        };
        String url = ServerAPIConfig.Do_SOSDetail + "session_id=" + sp.getString("session_id", null) + "&id=" + bundle.getString("id", null) + "&index=" + index + "&size=" + size;
        AndroidAsyncHttp.get(url, res);
    }

    /**
     * 获取订单信息
     */
    private void initData() {
        AsyncHttpResponseHandler res = new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                String str_json = new String(bytes);
                try {
                    JSONObject obj = new JSONObject(str_json);
                    if (0 != obj.getInt("code")) {
                        Log.i(TAG, "initData 数据解析错误:code=" + obj.toString());
                        return;
                    }
                    Gson gson = new Gson();
                    info = gson.fromJson(obj.getJSONObject("result").toString(), MyHelperSOSDetailInfo.class);

                    addHeadView();
                    adapter.addDataList(info.receiverList.list);
                    if (lv_DoTaskPerson_MHDA.isRefreshing()) {
                        lv_DoTaskPerson_MHDA.onRefreshComplete();
                    }

                } catch (JSONException e) {
                    if (lv_DoTaskPerson_MHDA.isRefreshing()) {
                        lv_DoTaskPerson_MHDA.onRefreshComplete();
                    }
                    Log.i(TAG, "enter catch=" + e.toString());
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                if (lv_DoTaskPerson_MHDA.isRefreshing()) {
                    lv_DoTaskPerson_MHDA.onRefreshComplete();
                }
                Log.i(TAG, "网络连接异常");
                ToastUtil.shortshow(CONTEXT, R.string.toast_error_net);
            }
        };
        String url = ServerAPIConfig.Do_SOSDetail + "session_id=" + sp.getString("session_id", null) + "&id=" + bundle.getString("id", null) + "&index=" + index + "&size=" + size ;
        AndroidAsyncHttp.get(url, res);
    }


    private void initView() {
        inflater = getLayoutInflater();
        sp = getSharedPreferences(StaticBean.USER_INFO, MODE_PRIVATE);
        tv_StopTask_MHDA = (TextView) findViewById(R.id.tv_StopTask_MHDA);
        ll_Back_MHDA = (LinearLayout) findViewById(R.id.ll_Back_MHDA);
        lv_DoTaskPerson_MHDA = (PullToRefreshListView) findViewById(R.id.lv_DoTaskPerson_MHDA);
        bundle = getIntent().getExtras();



    }

    View headView;

    private void addHeadView() {
        AbsListView.LayoutParams layoutParams = new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT, AbsListView.LayoutParams.WRAP_CONTENT);
        headView = LayoutInflater.from(this).inflate(R.layout.headview_myhelperdetailsos, lv_DoTaskPerson_MHDA, false);
        headView.setLayoutParams(layoutParams);
        initHeaderView();
        ListView listView = lv_DoTaskPerson_MHDA.getRefreshableView();
        listView.addHeaderView(headView);
    }

    /**
     * 初始化headView
     */
    private void initHeaderView() {
        /**找到对应的控件*/
        ll_ShowAllContent_MHDA = (LinearLayout) headView.findViewById(R.id.ll_ShowAllContent_MHDA);
        tv_UserName_MHDA = (TextView) headView.findViewById(R.id.tv_UserName_MHDA);
        tv_Time_MHDA = (TextView) headView.findViewById(R.id.tv_Time_MHDA);
        tv_TaskReward_MHDA = (TextView) headView.findViewById(R.id.tv_TaskReward_MHDA);
        tv_DoTaskCount_MHDA = (TextView) headView.findViewById(R.id.tv_DoTaskCount_MHDA);
        tv_TaskLife_MHDA = (TextView) headView.findViewById(R.id.tv_TaskLife_MHDA);
        tv_Content_MHDA = (TextView) headView.findViewById(R.id.tv_Content_MHDA);
        tv_ShowAllContent_MHDA = (TextView) headView.findViewById(R.id.tv_ShowAllContent_MHDA);
        tv_TaskDo_MHDA = (TextView) headView.findViewById(R.id.tv_TaskDo_MHDA);
        tv_TaskComplete_MHDA = (TextView) headView.findViewById(R.id.tv_TaskComplete_MHDA);
        tv_QiangDanCount_MHDA = (TextView) headView.findViewById(R.id.tv_QiangDanCount_MHDA);
        cimg_Avatar_MHDA = (CircleImageView) headView.findViewById(R.id.cimg_Avatar_MHDA);
        img_TaskState_MHDA = (ImageView) headView.findViewById(R.id.img_TaskState_MHDA);
        img_ShowAllContent_MHDA = (ImageView) headView.findViewById(R.id.img_ShowAllContent_MHDA);


        /**设置点击事件*/
        ll_ShowAllContent_MHDA.setOnClickListener(this);
        cimg_Avatar_MHDA.setOnClickListener(this);

        /**加载相应的信息*/
        tv_UserName_MHDA.setText(info.nickname);
        tv_Time_MHDA.setText(DateUtils.formatDate(new Date(info.start_time), DateUtils.yyyyMMDD));
        tv_QiangDanCount_MHDA.setText("共有" + info.receiverList.total + "人抢单");
        QiniuUtils.setAvatarByIdFrom7Niu(this,cimg_Avatar_MHDA,info.portrait);

        /**
         * SOS状态，0-发布，1-我安全了，2-确认完成，3-拒绝完成
         */
        switch (info.status) {
            case 0:
                if (0 == info.receiverList.total) {
                    img_TaskState_MHDA.setImageResource(R.mipmap.ic_nonedotask);
                }
                if (0 < info.receiverList.total) {
                    img_TaskState_MHDA.setImageResource(R.mipmap.ic_dotask);
                }
                break;
            case 1:
                img_TaskState_MHDA.setImageResource(R.mipmap.ic_completetask);
                break;
            case 2:
                img_TaskState_MHDA.setImageResource(R.mipmap.ic_completetask);
                break;
            case 3:
                img_TaskState_MHDA.setImageResource(R.mipmap.ic_completetask);
                break;
        }
    }

    /**
     * Called when a view has been clicked.
     *
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_Back_MHDA:
                this.finish();
                break;
        }
    }
}
