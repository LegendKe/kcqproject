package com.djx.pin.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.djx.pin.R;
import com.djx.pin.adapter.MyHelperSOSDetailAdapter;
import com.djx.pin.base.OldBaseActivity;
import com.djx.pin.beans.MyHelperSOSDetailInfo;
import com.djx.pin.beans.StaticBean;
import com.djx.pin.myview.CircleImageView;
import com.djx.pin.serverapiconfig.ServerAPIConfig;
import com.djx.pin.utils.AndroidAsyncHttp;
import com.djx.pin.utils.DateUtils;
import com.djx.pin.utils.LogUtil;
import com.djx.pin.utils.QiniuUtils;
import com.djx.pin.utils.ToastUtil;
import com.google.gson.Gson;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.List;

import cz.msebera.android.httpclient.Header;

/**
 * Created by Administrator on 2016/7/21 0021.
 */
public class MyHelperSOSDetailActivity extends OldBaseActivity implements View.OnClickListener {
    protected final static String TAG = MyHelperSOSDetailActivity.class.getSimpleName();

    LinearLayout ll_Back_MHDA;
    TextView tv_UserName_MHDA, tv_Time_MHDA, tv_TaskReward_MHDA,
            tv_DoTaskCount_MHDA, tv_TaskLife_MHDA, tv_Content_MHDA, tv_ShowAllContent_MHDA, tv_TaskDo_MHDA,
            tv_TaskComplete_MHDA, tv_QiangDanCount_MHDA;
    CircleImageView cimg_Avatar_MHDA;
    ImageView img_TaskState_MHDA, img_ShowAllContent_MHDA;
    PullToRefreshListView lv_DoTaskPerson_MHDA;

    MyHelperSOSDetailAdapter adapter;
    SharedPreferences sp;
    Context CONTEXT = MyHelperSOSDetailActivity.this;
    Bundle bundle = null;
    int index = 0, size = 10;
    LayoutInflater inflater;
    Dialog dialog_OrderCompelete;//确认订单是否完成对话框
    View dialogView_OrderCompelet;//确认订单是否完成对话框的布局
    Button bt_confirm_dialog, bt_cancle_dialog, bt_confirm;
    MyHelperSOSDetailInfo info = new MyHelperSOSDetailInfo();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_myhelperdetailsos);
        initView();
        initEvent();
        initDailog();
        initData();
        adapter = new MyHelperSOSDetailAdapter(this);
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
        bt_confirm.setOnClickListener(this);
        bt_confirm_dialog.setOnClickListener(this);
        bt_cancle_dialog.setOnClickListener(this);
    }


    /**
     * 获取订单信息
     */
    private void getData() {
        Log.i(TAG, "getData");
        AsyncHttpResponseHandler res = new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                String str_json = new String(bytes);
                Log.i(TAG, "str_json is " + str_json);
                try {
                    JSONObject obj = new JSONObject(str_json);

                    if (0 != obj.getInt("code")) {
                        LogUtil.e(CONTEXT, "initData 数据解析错误:code=" + obj.toString());
                        errorCode(obj.getInt("code"));
                        return;
                    }
                    Gson gson = new Gson();
                    info = gson.fromJson(obj.getJSONObject("result").toString(), MyHelperSOSDetailInfo.class);

                    Log.i(TAG, "info.status is " + info.status);
                    Log.i(TAG, "info.receiverList.total is " + info.receiverList.total);
                    if (1 == info.status) {
                        bt_confirm.setVisibility(View.VISIBLE);
                    } else {
                        bt_confirm.setVisibility(View.GONE);
                    }
                    adapter.addDataList(info.receiverList.list);
                    adapter.notifyDataSetChanged();
                    if (lv_DoTaskPerson_MHDA.isRefreshing()) {
                        lv_DoTaskPerson_MHDA.onRefreshComplete();
                    }
                } catch (JSONException e) {
                    if (lv_DoTaskPerson_MHDA.isRefreshing()) {
                        lv_DoTaskPerson_MHDA.onRefreshComplete();
                    }
                    LogUtil.e(CONTEXT, "enter catch=" + e.toString());
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                LogUtil.e(CONTEXT, "网络连接异常");
                ToastUtil.shortshow(CONTEXT, R.string.toast_error_net);
                if (lv_DoTaskPerson_MHDA.isRefreshing()) {
                    lv_DoTaskPerson_MHDA.onRefreshComplete();
                }
            }
        };
        String url = ServerAPIConfig.Do_SOSDetail + "session_id=" + sp.getString("session_id", null) + "&id=" + bundle.getString("id", null) + "&index=" + index + "&size=" + size;
        Log.i("test","我的求助sos获取的订单信息url:  "+url);
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
                LogUtil.e("sos详情 str_json=" + str_json);

                try {
                    JSONObject obj = new JSONObject(str_json);
                    if (0 != obj.getInt("code")) {
                        LogUtil.e(CONTEXT, "initData 数据解析错误:code=" + obj.toString());
                        errorCode(obj.getInt("code"));
                        return;
                    }
                    Gson gson = new Gson();
                    info = gson.fromJson(obj.getJSONObject("result").toString(), MyHelperSOSDetailInfo.class);
                    addHeadView();
                    if (1 == info.status) {
                        bt_confirm.setVisibility(View.VISIBLE);
                    } else {
                        bt_confirm.setVisibility(View.GONE);
                    }
                    adapter.addDataList(info.receiverList.list);
                    adapter.notifyDataSetChanged();
                    if (lv_DoTaskPerson_MHDA.isRefreshing()) {
                        lv_DoTaskPerson_MHDA.onRefreshComplete();
                    }

                } catch (JSONException e) {
                    if (lv_DoTaskPerson_MHDA.isRefreshing()) {
                        lv_DoTaskPerson_MHDA.onRefreshComplete();
                    }
                    LogUtil.e(CONTEXT, "enter catch=" + e.toString());
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                if (lv_DoTaskPerson_MHDA.isRefreshing()) {
                    lv_DoTaskPerson_MHDA.onRefreshComplete();
                }
                LogUtil.e(CONTEXT, "网络连接异常");
                ToastUtil.shortshow(CONTEXT, R.string.toast_error_net);
            }
        };
        String url = ServerAPIConfig.Do_SOSDetail + "session_id=" + sp.getString("session_id", null) + "&id=" + bundle.getString("id", null) + "&index=" + index + "&size=" + size;
        Log.i(TAG,url);

        AndroidAsyncHttp.get(url, res);
    }

    private void initView() {
        inflater = getLayoutInflater();
        sp = getSharedPreferences(StaticBean.USER_INFO, MODE_PRIVATE);
        ll_Back_MHDA = (LinearLayout) findViewById(R.id.ll_Back_MHDA);
        lv_DoTaskPerson_MHDA = (PullToRefreshListView) findViewById(R.id.lv_DoTaskPerson_MHDA);
        bt_confirm = (Button) findViewById(R.id.bt_confirm);
        bundle = getIntent().getExtras();
        /**订单是否完成对话框*/
        dialogView_OrderCompelet = inflater.inflate(R.layout.layout_dialog_ordercompelete, null);
        bt_confirm_dialog = (Button) dialogView_OrderCompelet.findViewById(R.id.bt_confirm_dialog);
        bt_cancle_dialog = (Button) dialogView_OrderCompelet.findViewById(R.id.bt_cancle_dialog);


    }

    public void initDailog() {
        /**订单是否完成对话框*/
        dialog_OrderCompelete = new Dialog(this, R.style.dialog_transparent);
        dialog_OrderCompelete.setContentView(dialogView_OrderCompelet);
        WindowManager.LayoutParams layoutParams_Province = dialog_OrderCompelete.getWindow().getAttributes();
        layoutParams_Province.gravity = Gravity.CENTER;
        dialog_OrderCompelete.getWindow().setAttributes(layoutParams_Province);

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
            case 1://我安全了
                int completedNum =0;
                for (int i = 0; i < info.receiverList.list.size(); i++) {
                    if(info.receiverList.list.get(i).status == 2 || info.receiverList.list.get(i).status == 3){//状态，0-参与，1-完成等待确认，2-获得赏金，3-完成未获得赏金，4-申诉判定完成，5-中途放弃
                       completedNum++;
                    }
                }
                if(0 == info.receiverList.total){//1.如果没有人选
                    img_TaskState_MHDA.setImageResource(R.mipmap.ic_nonedotask);

                }else if(info.receiverList.total > 0 && completedNum == 0){//2.如果已有人选但没人完成
                    img_TaskState_MHDA.setImageResource(R.mipmap.ic_dotask);

                }else if(info.receiverList.total > 0 && completedNum > 0){//3.如果有人完成
                    img_TaskState_MHDA.setImageResource(R.mipmap.ic_completetask);
                }
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
            /**点击 确认完成*/
            case R.id.bt_confirm:
                dialog_OrderCompelete.show();
                break;
            /**订单完成对话框 是按钮*/
            case R.id.bt_confirm_dialog:
                if(0==info.status){
                    ToastUtil.shortshow(CONTEXT,R.string.toast_sos_err_state);
                    return;
                }
                Bundle bundle_UserInfo = new Bundle();
                bundle_UserInfo.putString("portrait", info.portrait);
                bundle_UserInfo.putString("nickname", info.nickname);
                bundle_UserInfo.putString("province", info.location);
                bundle_UserInfo.putString("city", "");
                bundle_UserInfo.putString("district", "SOS求助");
                bundle_UserInfo.putString("id", info.id);
                bundle_UserInfo.putLong("complete_time", info.start_time);

                List<MyHelperSOSDetailInfo.LIST> receiverlist = info.receiverList.list;
                int payNum = 0;
                for (int i = 0; i < receiverlist.size(); i++) {
                    int status = receiverlist.get(i).status;
                    if(status == 1){
                        payNum++;
                    }
                }
                //double payTotal = info.price - (info.price/(Math.pow(2, payNum)));
                Log.i(TAG,"payNum"+payNum);

                bundle_UserInfo.putDouble("price", info.pay_price);
                Log.i(TAG,"info.pay_price-----------------"+info.pay_price);
                startActivity(PayConfirmSOSActivity.class, bundle_UserInfo);



                break;
            /**订单完成对话框 否按钮*/
            case R.id.bt_cancle_dialog:
                Bundle bundle_Appeal = new Bundle();
                bundle_Appeal.putString("id", info.id);
                startActivity(MyHelperSOSAppealActivity.class, bundle_Appeal);
                break;
            case R.id.ll_Back_MHDA:
                this.finish();
                break;
        }
    }
}
