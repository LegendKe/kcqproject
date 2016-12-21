package com.djx.pin.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
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
import com.djx.pin.adapter.MyHelperDetailAdapter;
import com.djx.pin.base.OldBaseActivity;
import com.djx.pin.beans.MyHelperOfflineDetailInfo;
import com.djx.pin.beans.StaticBean;
import com.djx.pin.improve.common.PhotoShowActivity;
import com.djx.pin.myview.CircleImageView;
import com.djx.pin.personal.ui.PersonalDataActivity;
import com.djx.pin.serverapiconfig.ServerAPIConfig;
import com.djx.pin.utils.AndroidAsyncHttp;
import com.djx.pin.utils.DateUtils;
import com.djx.pin.utils.LogUtil;
import com.djx.pin.utils.QiniuUtils;
import com.djx.pin.utils.ToastUtil;
import com.djx.pin.widget.CustomImageView;
import com.djx.pin.widget.NineGridLayout;
import com.google.gson.Gson;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import cz.msebera.android.httpclient.Header;

/**
 * Created by Administrator on 2016/7/21 0021.
 */
public class MyHelperOffLineDetailActivity extends OldBaseActivity implements View.OnClickListener, MyHelperDetailAdapter.SetListener{
    protected final static String TAG = MyHelperOffLineDetailActivity.class.getSimpleName();

    LinearLayout ll_Back_MHDA;
    TextView tv_StopTask_MHDA, tv_UserName_MHDA, tv_Time_MHDA, tv_TaskCode_MHDA, tv_TaskReward_MHDA,
            tv_DoTaskCount_MHDA, tv_TaskLife_MHDA, tv_Content_MHDA, tv_ShowAllContent_MHDA, tv_TaskDo_MHDA,
            tv_TaskComplete_MHDA, tv_QiangDanCount_MHDA;
    CircleImageView cimg_Avatar_MHDA;
    ImageView img_TaskState_MHDA;
    PullToRefreshListView lv_DoTaskPerson_MHDA;

    MyHelperDetailAdapter adapter;
    SharedPreferences sp;
    Context CONTEXT = MyHelperOffLineDetailActivity.this;
    Bundle bundle = null;
    int index = 0, size = 10;
    LayoutInflater inflater;
    Dialog dialog_OrderCompelete;//确认订单是否完成对话框
    View dialogView_OrderCompelet;//确认订单是否完成对话框的布局
    Button bt_confirm_dialog, bt_cancle_dialog;
    MyHelperOfflineDetailInfo info = new MyHelperOfflineDetailInfo();
    private NineGridLayout nineGridLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_myhelperdetailoffline);
        initView();
        initEvent();
        initDailog();
        initData();
        adapter = new MyHelperDetailAdapter(this, this);
        lv_DoTaskPerson_MHDA.setAdapter(adapter);
        lv_DoTaskPerson_MHDA.setMode(PullToRefreshBase.Mode.BOTH);
        lv_DoTaskPerson_MHDA.setOnRefreshListener(refreshListener);
    }

    PullToRefreshBase.OnRefreshListener2<ListView> refreshListener = new PullToRefreshBase.OnRefreshListener2<ListView>() {
        @Override
        public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
            index = 0;
            adapter.clear();
            getData(index);
           /* initData();*/
        }

        @Override
        public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
            index = index + 1;
            getData(index);
        }
    };


    private void initEvent() {
        ll_Back_MHDA.setOnClickListener(this);
        tv_StopTask_MHDA.setOnClickListener(this);

        bt_confirm_dialog.setOnClickListener(this);
        bt_cancle_dialog.setOnClickListener(this);
    }


    /**
     * 获取订单信息
     */
    private void getData(int pageindex) {
        AsyncHttpResponseHandler res = new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                String str_json = new String(bytes);
                try {
                    JSONObject obj = new JSONObject(str_json);


                    if (0 != obj.getInt("code")) {
                        LogUtil.e(CONTEXT, "initData 数据解析错误:code=" + obj.toString());
                        errorCode(obj.getInt("code"));
                        return;
                    }
                    Gson gson = new Gson();
                    info = gson.fromJson(obj.getJSONObject("result").toString(), MyHelperOfflineDetailInfo.class);
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
        String url = ServerAPIConfig.Get_RewardStatus + "session_id=" + sp.getString("session_id", null) + "&id=" + bundle.getString("id", null) + "&index=" + pageindex + "&size=" + size + "&type=" + 1;
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
                Log.i(TAG, "str_json is " + str_json);
                try {
                    JSONObject obj = new JSONObject(str_json);
                    if (0 != obj.getInt("code")) {
                        errorCode(obj.getInt("code"));
                        return;
                    }
                    Gson gson = new Gson();
                    info = gson.fromJson(obj.getJSONObject("result").toString(), MyHelperOfflineDetailInfo.class);

                    int receiver_limit = info.receiver_limit;
                    List<MyHelperOfflineDetailInfo.LIST> lists = info.receiverList.list;
                    int confirmedNum = 0;
                    for (int j = 0; j < lists.size(); j++) {
                        //状态，0-已抢单，1-签单成功（进行服务），2-标记完成（待收款），3-发单者确认完成，4-发单者拒绝完成（发单者申诉），5-申诉判定完成，6-抢单被拒绝，7-接单者放弃
                        int status = lists.get(j).status;
                        if(status == 1 || status == 2 || status == 3 || status == 4 || status == 5){
                            confirmedNum++;
                        }
                    }
                    adapter.setConfirmed2LimitNum(confirmedNum,receiver_limit);

                    Log.i(TAG, "info.status is " + info.status);
                    if (2 == info.status){
                        adapter.setOrderState(true);
                    }

                    initHeaderView();
                    adapter.addDataList(info.receiverList.list);
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
        String url = ServerAPIConfig.Get_RewardStatus + "session_id=" + sp.getString("session_id", null) + "&id=" + bundle.getString("id", null) + "&index=" + index + "&size=" + size + "&type=" + 1;
        AndroidAsyncHttp.get(url, res);
    }


    private void initView() {
        inflater = getLayoutInflater();
        sp = getSharedPreferences(StaticBean.USER_INFO, MODE_PRIVATE);
        tv_StopTask_MHDA = (TextView) findViewById(R.id.tv_StopTask_MHDA);
        ll_Back_MHDA = (LinearLayout) findViewById(R.id.ll_Back_MHDA);
        lv_DoTaskPerson_MHDA = (PullToRefreshListView) findViewById(R.id.lv_DoTaskPerson_MHDA);
        bundle = getIntent().getExtras();

        AbsListView.LayoutParams layoutParams = new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT, AbsListView.LayoutParams.WRAP_CONTENT);
        headView = LayoutInflater.from(this).inflate(R.layout.headview_myhelperdetailoffline, lv_DoTaskPerson_MHDA, false);
        headView.setLayoutParams(layoutParams);
        nineGridLayout = ((NineGridLayout)headView.findViewById(R.id.nineGridLayout));
        tv_UserName_MHDA = (TextView) headView.findViewById(R.id.tv_UserName1_LifeReward);
        tv_Time_MHDA = (TextView) headView.findViewById(R.id.tv_Time1_LifeReward);
        ((TextView) headView.findViewById(R.id.tv_QiangDan_LifeRewardDetail)).setVisibility(View.GONE);
        tv_TaskReward_MHDA = (TextView) headView.findViewById(R.id.tv_HelperPrice_LifeRewardDetail);
        tv_DoTaskCount_MHDA = (TextView) headView.findViewById(R.id.tv_HelperPeopleNum_LifeRewardDetail);
        tv_TaskLife_MHDA = (TextView) headView.findViewById(R.id.tv_HelperPeriod_LifeRewardDetail);
        tv_Content_MHDA = (TextView) headView.findViewById(R.id.tv_Content_LDA);
        tv_ShowAllContent_MHDA = (TextView) headView.findViewById(R.id.tv_spread);
        tv_TaskDo_MHDA = (TextView) headView.findViewById(R.id.tv_TaskDo_MHDA);
        tv_TaskComplete_MHDA = (TextView) headView.findViewById(R.id.tv_TaskComplete_MHDA);
        tv_QiangDanCount_MHDA = (TextView) headView.findViewById(R.id.tv_QiangDanCount_MHDA);
        cimg_Avatar_MHDA = (CircleImageView) headView.findViewById(R.id.cimg_Avatar_HPDA);
        img_TaskState_MHDA = (ImageView) headView.findViewById(R.id.img_TaskState_MHDA);


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

    /**
     * 初始化headView
     */
    private void initHeaderView() {

        cimg_Avatar_MHDA.setOnClickListener(this);

        /**加载相应的信息*/
        tv_UserName_MHDA.setText(info.nickname);
        tv_Time_MHDA.setText(DateUtils.formatDate(new Date(info.create_time), DateUtils.yyyyMMDD));
        //tv_TaskCode_MHDA.setText(""+info.id);
        tv_TaskReward_MHDA.setText(info.price + "元");
        tv_DoTaskCount_MHDA.setText(info.receiver_limit + "人");
        tv_TaskLife_MHDA.setText(DateUtils.formatDate(new Date(info.start_time), DateUtils.yyyyMMDD) + "至" + DateUtils.formatDate(new Date(info.end_time), DateUtils.yyyyMMDD));
        tv_Content_MHDA.setText(info.description);
        tv_QiangDanCount_MHDA.setText("共有" + info.receiver_num + "人抢单");
        //订单结束
        if (2 == info.status){
            tv_StopTask_MHDA.setText("订单结束");
        }

        switch (info.status) {
            /**订单状态在 1-发单成功（进行中）*/
            case 1:
                /**订单签单人数为0*/
                if (info.receiver_num == 0) {
                    img_TaskState_MHDA.setImageResource(R.mipmap.ic_nonedotask);
                    tv_TaskDo_MHDA.setText("已有人选" + "0" + "/" + info.receiver_limit);
                    tv_TaskComplete_MHDA.setText("已经完成" + "0" + "/" + info.receiver_limit);
                }
                /**订单签单人数为大于0*/
                if (info.receiver_num > 0) {
                    tv_TaskDo_MHDA.setText("已有人选" + info.receiver_num + "/" + info.receiver_limit);
                    int compelete_num = 0;//完成人数
                    /**已完成的人数计算:3-发单者确认完成和5-申诉判定完成算入其中,其他忽略*/
                    for (int i = 0; i < info.receiverList.list.size(); i++) {
                        if (3 == info.receiverList.list.get(i).status || 5 == info.receiverList.list.get(i).status) {
                            compelete_num = compelete_num + 1;
                        }
                    }
                    tv_TaskComplete_MHDA.setText("已经完成" + compelete_num + "/" + info.receiver_limit);

                    /**完成订单人数等于0表示无人完成,不进入完成状态,大于0是才进入完成状态*/
                    if (compelete_num == 0) {
                        img_TaskState_MHDA.setImageResource(R.mipmap.ic_alldotask);
                    }
                    if (compelete_num > 0) {
                        img_TaskState_MHDA.setImageResource(R.mipmap.ic_completetask);
                    }
                }

                break;
            /**订单状态在 2-订单结束（停止招募）*/
            case 2:
                /**订单签单人数为0*/
                if (info.receiver_num == 0) {
                    img_TaskState_MHDA.setImageResource(R.mipmap.ic_nonedotask);
                    tv_TaskDo_MHDA.setText("已有人选0");
                    tv_TaskComplete_MHDA.setText("已经完成0");
                }

                /**订单签单人数为大于0*/
                if (info.receiver_num > 0) {
                    int doing_num = 0;//进行中的人数,接单者对应的状态为:0-已抢单，1-签单成功（进行服务），2-标记完成（待收款），3-发单者确认完成，4-发单者拒绝完成（发单者申诉），5-申诉判定完成
                    /**进行中的人数计算:0-已抢单，1-签单成功（进行服务），2-标记完成（待收款），3-发单者确认完成，4-发单者拒绝完成（发单者申诉），5-申诉判定完成*/
                    for (int j = 0; j < info.receiverList.list.size(); j++) {
                        if (1 == info.receiverList.list.get(j).status || 2 == info.receiverList.list.get(j).status || 3 == info.receiverList.list.get(j).status || 4 == info.receiverList.list.get(j).status || 5 == info.receiverList.list.get(j).status) {
                            doing_num = doing_num + 1;
                        }
                    }
                    tv_TaskDo_MHDA.setText("已有人选" + doing_num + "/" + info.receiver_limit);

                    int compelete_num = 0;//已完成的人数:接单者对应的状态为:3-发单者确认完成和5-申诉判定完成算入其中
                    /**已完成的人数计算:3-发单者确认完成和5-申诉判定完成算入其中,其他忽略*/
                    for (int i = 0; i < info.receiverList.list.size(); i++) {
                        if (3 == info.receiverList.list.get(i).status || 5 == info.receiverList.list.get(i).status) {
                            compelete_num = compelete_num + 1;
                        }
                    }
                    tv_TaskComplete_MHDA.setText("已经完成" + compelete_num + "/" + info.receiver_limit);

                    /**完成订单人数等于0表示无人完成,不进入完成状态,大于0是才进入完成状态*/
                    if (compelete_num == 0) {
                        img_TaskState_MHDA.setImageResource(R.mipmap.ic_alldotask);
                    }
                    if (compelete_num > 0) {
                        img_TaskState_MHDA.setImageResource(R.mipmap.ic_completetask);
                    }

                }
                break;
        }
        Log.e("myhelp","findViewById(R.id.imgs_9grid_layout):------2---------- "+nineGridLayout);
        QiniuUtils.setAvatarByIdFrom7Niu(MyHelperOffLineDetailActivity.this,cimg_Avatar_MHDA, info.portrait);

        Log.e("json","图片------"+info.media.toString());
        if(info.media!= null && info.media.size()>0){
            final ArrayList<String> ids = new ArrayList<>();
            for (int i = 0; i < info.media.size(); i++) {
                ids.add(info.media.get(i).media_id);
            }
            QiniuUtils.set9GridByIdsFrom7Niu(this,ids,info.id,nineGridLayout);
            nineGridLayout.setImageShowListener(new NineGridLayout.ImageShowListener() {
                @Override
                public void imageShow(int imgPos,ArrayList<CustomImageView> imageViews) {
                    if (ids != null) {
                        Intent intent = new Intent(MyHelperOffLineDetailActivity.this, PhotoShowActivity.class);
                        intent.putExtra("CURRENT_POS", imgPos);
                        intent.putStringArrayListExtra("IDS", ids);
                        startActivity(intent);
                    }
                }
            });
        }else {
            nineGridLayout.setVisibility(View.GONE);
        }
        ListView listView = lv_DoTaskPerson_MHDA.getRefreshableView();
        listView.addHeaderView(headView);
    }

    /**
     * Called when a view has been clicked.
     *
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            /**订单完成对话框 是按钮*/
            case R.id.bt_confirm_dialog:
                Bundle bundle_UserInfo=new Bundle();
                bundle_UserInfo.putString("portrait",adapter.getItem(receiver_position).portrait);
                bundle_UserInfo.putString("nickname",adapter.getItem(receiver_position).nickname);
                bundle_UserInfo.putString("province",adapter.getItem(receiver_position).province);
                bundle_UserInfo.putString("city",adapter.getItem(receiver_position).city);
                bundle_UserInfo.putString("district",adapter.getItem(receiver_position).district);
                bundle_UserInfo.putString("receiver_id",adapter.getItem(receiver_position).receiver_id);
                bundle_UserInfo.putString("id",info.id);
                bundle_UserInfo.putLong("complete_time",adapter.getItem(receiver_position).complete_time);
                bundle_UserInfo.putDouble("price",info.price);
                startActivity(PayConfirmActivity.class,bundle_UserInfo);
                break;
            /**订单完成对话框 否按钮*/
            case R.id.bt_cancle_dialog:
                Bundle bundle_Appeal=new Bundle();
                bundle_Appeal.putString("id",bundle.getString("id"));
                bundle_Appeal.putString("receiver_id",adapter.getItem(receiver_position).receiver_id);
                startActivity(MyHelperOffLineAppealActivity.class, bundle_Appeal);
                break;
            case R.id.ll_Back_MHDA:
                this.finish();
                break;
            /**停止招募*/
            case R.id.tv_StopTask_MHDA:
                if (tv_StopTask_MHDA.getText().toString().equals("订单结束")) {
                    ToastUtil.shortshow(getApplicationContext(), "您的订单已结束");

                } else if(tv_StopTask_MHDA.getText().toString().equals("停止招募")){
                    //startActivity(MyHelperOffLineRewardStopActivity.class, bundle);
                    Intent intent = new Intent(MyHelperOffLineDetailActivity.this,MyHelperOffLineRewardStopActivity.class);
                    intent.putExtras(bundle);
                    startActivityForResult(intent,100);
                }

                break;
            case R.id.cimg_Avatar_MHDA:
                startActivity(PersonalDataActivity.class);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode == 100 && resultCode == 100)
        {
            tv_StopTask_MHDA.setText("订单结束");


        }
    }

    int receiver_position=0;//发单者进行相应的操作时的接单者在adapter中的位置.每次点击相应的操作receiver_position都会更新.
    @Override
    public void clickListener(int tag, View v) {//回调

        receiver_position= (int) v.getTag();

        switch (tag) {
            case 1:
                TextView textView = (TextView) v;
               /* if ("确认人员".equals(textView.getText().toString())) {
                    updataSignOrder(adapter.getItem(receiver_position).receiver_id);
                }*/
                if ("确认人员".equals(textView.getText().toString()) && !"订单结束".equals(tv_StopTask_MHDA.getText().toString().trim())) {
                    updataSignOrder(adapter.getItem(receiver_position).receiver_id);
                }
                if ("是否完成".equals(textView.getText().toString())) {
                    dialog_OrderCompelete.show();
                }
                if ("等待评价".equals(textView.getText().toString())) {
                    dialog_OrderCompelete.show();
                    Bundle bundle_UserInfo=new Bundle();
                    bundle_UserInfo.putString("portrait",adapter.getItem(receiver_position).portrait);
                    bundle_UserInfo.putString("nickname",adapter.getItem(receiver_position).nickname);
                    bundle_UserInfo.putString("province",adapter.getItem(receiver_position).province);
                    bundle_UserInfo.putString("city",adapter.getItem(receiver_position).city);
                    bundle_UserInfo.putString("district",adapter.getItem(receiver_position).district);
                    bundle_UserInfo.putString("receiver_id",adapter.getItem(receiver_position).receiver_id);
                    bundle_UserInfo.putString("id",info.id);
                    bundle_UserInfo.putLong("complete_time",adapter.getItem(receiver_position).complete_time);
                    bundle_UserInfo.putDouble("price",info.price);
                    startActivity(CommentActivity.class, bundle_UserInfo);
                }
                break;
            /**溯源*/
            case 2:
                Bundle bundle_Source=new Bundle();
                bundle_Source.putString("id",info.id);
                bundle_Source.putString("receiver_id",adapter.getItem(receiver_position).receiver_id);
                bundle_Source.putInt("type",1);
                startActivity(MyHelperTaskSourceActivity.class,bundle_Source);
                break;

            case 3://进入申诉页面
                Bundle bundle_appeal = new Bundle();
                MyHelperOfflineDetailInfo.LIST item = adapter.getItem(receiver_position);
                bundle_appeal.putString("id",bundle.getString("id"));
                bundle_appeal.putString("process_id",item.id);
                bundle_appeal.putInt("flag",1);//1标记发单者
                startActivity(DeclarationActivity.class,bundle_appeal);
                break;
            case 4://点击头像
                Bundle bundle_avater = new Bundle();
                MyHelperOfflineDetailInfo.LIST item_avater = adapter.getItem(receiver_position);
                Log.i(TAG, "nickname is " + item_avater.nickname);
                bundle_avater.putString("user_id",item_avater.receiver_id);
                bundle_avater.putString("nickname",item_avater.nickname);
                startActivity(LookOthersMassageActivity.class,bundle_avater);
        }
    }


    /**
     * 发单者选择接单者
     *
     * @param receiver_id 接单者id
     */
    private void updataSignOrder(String receiver_id) {
        AsyncHttpResponseHandler res = new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                String str_json = new String(bytes);
                try {
                    JSONObject obj = new JSONObject(str_json);
                    if (0 != obj.getInt("code")) {
                        errorCode(obj.getInt("code"));
                        LogUtil.e(CONTEXT, "updataSignOrder 数据解析错误=" + str_json.toString());
                        return;
                    }
                    ToastUtil.shortshow(CONTEXT, R.string.toast_signorder_success);
                    adapter.clear();
                    index=0;
                    getData(index);

                } catch (JSONException e) {
                    LogUtil.e(CONTEXT, "updataSignOrder enter catch");
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                LogUtil.e(CONTEXT, "网络连接异常");
                ToastUtil.shortshow(CONTEXT, R.string.toast_error_net);
            }
        };
        RequestParams params = new RequestParams();
        params.put("session_id", sp.getString("session_id", null));
        params.put("id", bundle.getString("id", null));
        params.put("receiver_id", receiver_id);
        LogUtil.e("params=" + params.toString());

        AndroidAsyncHttp.post(ServerAPIConfig.Do_SignOrder, params, res);
    }


}
