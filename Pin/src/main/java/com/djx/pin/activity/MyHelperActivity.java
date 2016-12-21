package com.djx.pin.activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.djx.pin.R;
import com.djx.pin.adapter.MyHelperOfflineRewardAdapter;
import com.djx.pin.adapter.MyHelperOnlineRewardAdapter;
import com.djx.pin.adapter.MyHelperSOSAdapter;
import com.djx.pin.base.OldBaseActivity;
import com.djx.pin.improve.detail.OrderDetailActivity;
import com.djx.pin.beans.MyHelperListInfo;
import com.djx.pin.beans.SOSInfo;
import com.djx.pin.beans.StaticBean;
import com.djx.pin.serverapiconfig.ServerAPIConfig;
import com.djx.pin.utils.AndroidAsyncHttp;
import com.djx.pin.utils.LogUtil;
import com.djx.pin.utils.ToastUtil;
import com.djx.pin.utils.myutils.ConstantUtils;
import com.google.gson.Gson;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;

import cz.msebera.android.httpclient.Header;

/**
 * Created by Administrator on 2016/6/29.
 */
public class MyHelperActivity extends OldBaseActivity implements View.OnClickListener, AdapterView.OnItemClickListener {

    private RelativeLayout rl_Parent_MHA;
    private View v_Parent_Cover_MHA;
    private LinearLayout ll_Back_MHA, ll_All_MHA, ll_OffLine_MHA, ll_OnLine_MHA;
    private View v_All_MHA, v_OffLineLine_MHA, v_OnLineLine_MHA;
    private TextView tv_All_MHA, tv_OffLine_MHA, tv_Online_MHA;
    private PullToRefreshListView lv_MHA;
    private Context CONTEXT = MyHelperActivity.this;
    MyHelperSOSAdapter adapter_SOS;
    MyHelperOfflineRewardAdapter adapter_OfflineReward;
    MyHelperOnlineRewardAdapter adapter_OnlineReward;
    SharedPreferences sp;
    private String session_id;

    /**
     * 用来标记listview现在使用的是哪个adapter.
     * 默认等于0
     * 0表示adapter_SOS,
     * 1表示adapter_OnlineReward,
     * 2表示adapter_OnlineReward
     */
    int adapterflag = 0;

    int index_SOS = 0,//SOS的index
            index_OnlineReward = 0,//线下悬赏index
            index_OfflineReward = 0,//线下悬赏index
            size_SOS = 10,//SOS的size
            size_OfflineReward = 10,//线下悬赏size
            size_OnlineReward = 10;//线下悬赏size


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_myhelper);
        initView();
        initEvent();
        getSOSRewardData();
        getOfflineRewardData();
        getOnlineRewardData();
    }


    @Override
    protected void onPause() {
        super.onPause();
        adapter_OfflineReward.clear();
        adapter_OnlineReward.clear();
        adapter_SOS.clear();

        index_SOS = 0;//SOS的index
        index_OnlineReward = 0;
        index_OfflineReward = 0;
        getOfflineRewardData();
        getOnlineRewardData();
        getSOSRewardData();


    }

    private void initEvent() {
        ll_Back_MHA.setOnClickListener(this);
        ll_All_MHA.setOnClickListener(this);
        ll_OnLine_MHA.setOnClickListener(this);

        ll_OffLine_MHA.setOnClickListener(this);
        lv_MHA.setMode(PullToRefreshBase.Mode.BOTH);

        lv_MHA.setOnRefreshListener(refreshListener);
        lv_MHA.setOnItemClickListener(this);
    }

    PullToRefreshBase.OnRefreshListener2<ListView> refreshListener = new PullToRefreshBase.OnRefreshListener2() {
        @Override
        public void onPullDownToRefresh(PullToRefreshBase refreshView) {
            switch (adapterflag) {
                case 0:
                    adapter_SOS.clear();
                    index_SOS = 0;
                    getSOSRewardData();
                    break;
                case 1:
                    adapter_OfflineReward.clear();
                    index_OfflineReward = 0;
                    getOfflineRewardData();
                    break;
                case 2:
                    adapter_OnlineReward.clear();
                    index_OnlineReward = 0;
                    getOnlineRewardData();
                    break;
            }
        }

        @Override
        public void onPullUpToRefresh(PullToRefreshBase refreshView) {
            switch (adapterflag) {
                case 0:
                    index_SOS = index_SOS + 1;
                    getSOSRewardData();
                    break;
                case 1:
                    index_OfflineReward = index_OfflineReward + 1;
                    getOfflineRewardData();
                    break;
                case 2:
                    index_OnlineReward = index_OnlineReward + 1;
                    getOnlineRewardData();
                    break;
            }


        }
    };

    private void initView() {

        sp = getSharedPreferences(StaticBean.USER_INFO, MODE_PRIVATE);
        session_id = sp.getString("session_id", null);

        ll_Back_MHA = (LinearLayout) findViewById(R.id.ll_Back_MHA);
        ll_All_MHA = (LinearLayout) findViewById(R.id.ll_All_MHA);
        ll_OffLine_MHA = (LinearLayout) findViewById(R.id.ll_OffLine_MHA);
        ll_OnLine_MHA = (LinearLayout) findViewById(R.id.ll_OnLine_MHA);


        rl_Parent_MHA = (RelativeLayout) findViewById(R.id.rl_Parent_MHA);
        v_Parent_Cover_MHA = findViewById(R.id.v_Parent_Cover_MHA);

        tv_All_MHA = (TextView) findViewById(R.id.tv_All_MHA);
        tv_OffLine_MHA = (TextView) findViewById(R.id.tv_OffLine_MHA);
        tv_Online_MHA = (TextView) findViewById(R.id.tv_Online_MHA);


        v_All_MHA = findViewById(R.id.v_All_MHA);
        v_OffLineLine_MHA = findViewById(R.id.v_OffLineLine_MHA);
        v_OnLineLine_MHA = findViewById(R.id.v_OnLineLine_MHA);


        lv_MHA = (PullToRefreshListView) findViewById(R.id.lv_MHA);

        adapter_SOS = new MyHelperSOSAdapter(this);
        adapter_OfflineReward = new MyHelperOfflineRewardAdapter(this);
        adapter_OnlineReward = new MyHelperOnlineRewardAdapter(this);

        lv_MHA.setAdapter(adapter_SOS);


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_Back_MHA:
                this.finish();
                break;
            case R.id.ll_All_MHA:
                tv_All_MHA.setTextColor(getResources().getColor(R.color.text_color_focused));
                tv_OffLine_MHA.setTextColor(getResources().getColor(R.color.text_color_black));
                tv_Online_MHA.setTextColor(getResources().getColor(R.color.text_color_black));

                v_All_MHA.setVisibility(View.VISIBLE);
                v_OffLineLine_MHA.setVisibility(View.INVISIBLE);
                v_OnLineLine_MHA.setVisibility(View.INVISIBLE);

                lv_MHA.setAdapter(adapter_SOS);

                adapterflag = 0;
                break;
            case R.id.ll_OnLine_MHA:
                tv_All_MHA.setTextColor(getResources().getColor(R.color.text_color_black));
                tv_OffLine_MHA.setTextColor(getResources().getColor(R.color.text_color_black));
                tv_Online_MHA.setTextColor(getResources().getColor(R.color.text_color_focused));

                v_All_MHA.setVisibility(View.INVISIBLE);
                v_OffLineLine_MHA.setVisibility(View.INVISIBLE);
                v_OnLineLine_MHA.setVisibility(View.VISIBLE);

                lv_MHA.setAdapter(adapter_OnlineReward);
                adapterflag = 2;

                break;
            case R.id.ll_OffLine_MHA:
                tv_All_MHA.setTextColor(getResources().getColor(R.color.text_color_black));
                tv_OffLine_MHA.setTextColor(getResources().getColor(R.color.text_color_focused));
                tv_Online_MHA.setTextColor(getResources().getColor(R.color.text_color_black));

                v_All_MHA.setVisibility(View.INVISIBLE);
                v_OffLineLine_MHA.setVisibility(View.VISIBLE);
                v_OnLineLine_MHA.setVisibility(View.INVISIBLE);

                lv_MHA.setAdapter(adapter_OfflineReward);

                adapterflag = 1;
                break;
        }
    }

    /**
     * 获取帮人信息列表
     */
    public void getOnlineRewardData() {
        String url = ServerAPIConfig.MyHelper + "session_id=" + session_id + "&index=" + index_OnlineReward + "&size=" + size_OnlineReward + "&content_type=" + 7;
        AsyncHttpResponseHandler res = new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                String str_json = new String(bytes);
                try {
                    JSONObject obj = new JSONObject(str_json);
                    if (0 != obj.getInt("code")) {
                        LogUtil.e(CONTEXT, "getOnlineRewardData 数据解析错误:code=" + obj.getInt("code"));
                        errorCode(obj.getInt("code"));
                        return;
                    }
                    Gson gson = new Gson();
                    MyHelperListInfo helpinfo = gson.fromJson(obj.getJSONObject("result").toString(), MyHelperListInfo.class);
                    adapter_OnlineReward.addDataList(helpinfo.list);
                    adapter_OnlineReward.notifyDataSetChanged();
                    if (lv_MHA.isRefreshing()) {
                        lv_MHA.onRefreshComplete();
                    }
                } catch (JSONException e) {
                    if (lv_MHA.isRefreshing()) {
                        lv_MHA.onRefreshComplete();
                    }
                    LogUtil.e(CONTEXT, "enter catch=" + e.toString());
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                if (lv_MHA.isRefreshing()) {
                    lv_MHA.onRefreshComplete();
                }
                ToastUtil.shortshow(CONTEXT, R.string.toast_error_net);
                LogUtil.e(CONTEXT, "enter onFailure");
            }
        };
        AndroidAsyncHttp.get(url, res);
    }


    /**
     * 获取线下悬赏信息列表
     */
    public void getOfflineRewardData() {
        String url = ServerAPIConfig.MyHelper + "session_id=" + session_id + "&index=" + index_OfflineReward + "&size=" + size_OfflineReward + "&content_type=" + 1;
        Log.i("chuanqi","我的求助,线下悬赏:url---------------:"+url);
        AsyncHttpResponseHandler res = new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                String str_json = new String(bytes);
                try {
                    JSONObject obj = new JSONObject(str_json);
                    if (0 != obj.getInt("code")) {
                        LogUtil.e(CONTEXT, "getOfflineRewardData 数据解析错误:code=" + obj.getInt("code"));
                        errorCode(obj.getInt("code"));
                        return;
                    }
                    Gson gson = new Gson();
                    MyHelperListInfo helpinfo = new MyHelperListInfo();
                    helpinfo = gson.fromJson(obj.getJSONObject("result").toString(), MyHelperListInfo.class);
                    adapter_OfflineReward.addDataList(helpinfo.list);
                    adapter_OfflineReward.notifyDataSetChanged();
                    if (lv_MHA.isRefreshing()) {
                        lv_MHA.onRefreshComplete();
                    }
                } catch (JSONException e) {
                    if (lv_MHA.isRefreshing()) {
                        lv_MHA.onRefreshComplete();
                    }
                    LogUtil.e(CONTEXT, "enter catch=" + e.toString());
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                if (lv_MHA.isRefreshing()) {
                    lv_MHA.onRefreshComplete();
                }
                ToastUtil.shortshow(CONTEXT, R.string.toast_error_net);
                LogUtil.e(CONTEXT, "enter onFailure");
            }
        };
        AndroidAsyncHttp.get(url, res);
    }


    /**
     * 获取SOS信息列表
     */
    public void getSOSRewardData() {
        String url = ServerAPIConfig.MyHelper + "session_id=" + session_id + "&index=" + index_SOS + "&size=" + size_SOS + "&content_type=" + 5;
        LogUtil.e("sos url=" + url);
        AsyncHttpResponseHandler res = new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                String str_json = new String(bytes);

                LogUtil.e("sos str_json=" + str_json);
                try {
                    JSONObject obj = new JSONObject(str_json);
                    if (0 != obj.getInt("code")) {
                        LogUtil.e(CONTEXT, "getSOSRewardData 数据解析错误:code=" + obj.getInt("code"));
                        errorCode(obj.getInt("code"));
                        return;
                    }
                    Gson gson = new Gson();
                    SOSInfo sosInfo = new SOSInfo();
                    sosInfo = gson.fromJson(obj.getJSONObject("result").toString(), SOSInfo.class);
                    for (int j = 0; j < sosInfo.list.size(); j++) {
                        adapter_SOS.addData(j, sosInfo.list.get(j));
                    }
                    adapter_SOS.notifyDataSetChanged();
                    if (lv_MHA.isRefreshing()) {
                        lv_MHA.onRefreshComplete();
                    }
                } catch (JSONException e) {
                    if (lv_MHA.isRefreshing()) {
                        lv_MHA.onRefreshComplete();
                    }
                    LogUtil.e(CONTEXT, "enter catch=" + e.toString());
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                if (lv_MHA.isRefreshing()) {
                    lv_MHA.onRefreshComplete();
                }
                ToastUtil.shortshow(CONTEXT, R.string.toast_error_net);
                LogUtil.e(CONTEXT, "enter onFailure");
            }
        };
        AndroidAsyncHttp.get(url, res);
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        position = position - 1;

       /* receiver_limit Number
        接单者上限，只在content_type=1或7时有*/
        //内容类别，1-线下求助，5-SOS，7-网络悬赏

        switch (adapterflag) {
            case 0:
                LogUtil.e("sos 订单id=" + adapter_SOS.getItem(position).id);
                Bundle bundle_SOS = new Bundle();
                bundle_SOS.putString("id", adapter_SOS.getItem(position).id);
                startActivity(MyHelperSOSDetailActivity.class, bundle_SOS);
                break;
            case 1://线下
                LogUtil.e("offline 订单id=" + adapter_OfflineReward.getItem(position).id);
                //未支付订单直接跳到支付界面
                if (0 == adapter_OfflineReward.getItem(position).status) {
                    Bundle bundle = new Bundle();
                    bundle.putString("id", adapter_OfflineReward.getItem(position).id);
                    bundle.putInt("type", 1);
                    Double amount = adapter_OfflineReward.getItem(position).price * adapter_OfflineReward.getItem(position).receiver_limit;
                    BigDecimal val = new BigDecimal(String.valueOf(amount));
                    bundle.putFloat("amount", val.floatValue());
                    startActivity(PayTypeActivity.class, bundle);
                    return;
                }
                Bundle bundle_Offline = new Bundle();
                bundle_Offline.putString("id", adapter_OfflineReward.getItem(position).id);
                bundle_Offline.putInt("receiver_limit",adapter_OfflineReward.getItem(position).receiver_limit);
                startActivity(MyHelperOffLineDetailActivity.class, bundle_Offline);
                break;

            case 2://网络
                if (0 == adapter_OnlineReward.getItem(position).status) {//未支付订单直接跳到支付界面
                    Bundle bundle = new Bundle();
                    bundle.putString("id", adapter_OnlineReward.getItem(position).id);
                    bundle.putInt("type", 7);

                    Double amount = adapter_OnlineReward.getItem(position).price * adapter_OnlineReward.getItem(position).receiver_limit;
                    BigDecimal val = new BigDecimal(String.valueOf(amount));
                    bundle.putFloat("amount", val.floatValue());
                    startActivity(PayTypeActivity.class, bundle);
                    return;
                }
                Bundle bundle_Online = new Bundle();
                bundle_Online.putString("id", adapter_OnlineReward.getItem(position).id);
                bundle_Online.putInt("status",adapter_OnlineReward.getItem(position).status);
                bundle_Online.putInt("UI", ConstantUtils.MY_HELP);
                startActivity(OrderDetailActivity.class,bundle_Online);
                break;
        }
    }
}
