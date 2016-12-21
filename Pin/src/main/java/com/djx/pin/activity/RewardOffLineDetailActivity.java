package com.djx.pin.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.djx.pin.R;
import com.djx.pin.base.OldBaseActivity;
import com.djx.pin.beans.MyHelperOfflineDetailInfo;
import com.djx.pin.beans.StaticBean;
import com.djx.pin.improve.common.PhotoShowActivity;
import com.djx.pin.myview.CircleImageView;
import com.djx.pin.serverapiconfig.ServerAPIConfig;
import com.djx.pin.utils.AndroidAsyncHttp;
import com.djx.pin.utils.DateUtils;
import com.djx.pin.utils.LogUtil;
import com.djx.pin.utils.QiniuUtils;
import com.djx.pin.utils.ToastUtil;
import com.djx.pin.widget.CustomImageView;
import com.djx.pin.widget.NineGridLayout;
import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;

import cz.msebera.android.httpclient.Header;

/**
 * Created by Administrator on 2016/7/21 0021.
 */
public class RewardOffLineDetailActivity extends OldBaseActivity implements View.OnClickListener {
    protected final static String TAG = RewardOffLineDetailActivity.class.getSimpleName();

    LinearLayout ll_Back_MHDA;
    TextView tv_StopTask_MHDA, tv_UserName_MHDA, tv_Time_MHDA, tv_TaskCode_MHDA, tv_TaskReward_MHDA,
            tv_TaskLife_MHDA, tv_Content_MHDA, tv_ShowAllContent_MHDA, tv_TaskDo_MHDA,
            tv_TaskComplete_MHDA, tv_TaskRewardMoney, tv_QiangDanCount_MHDA, tv_NickName_MHDA, tv_Location_MHDA, tv_Time, tv_appealdetail;
    CircleImageView cimg_Avatar_MHDA, cimg_Avatar;
    ImageView img_TaskState_MHDA, img_ShowAllContent_MHDA;

    SharedPreferences sp;
    Context CONTEXT = RewardOffLineDetailActivity.this;
    boolean isShowAll = false;
    Bundle bundle = null;
    int index = 0, size = 10;
    LayoutInflater inflater;
    MyHelperOfflineDetailInfo info = new MyHelperOfflineDetailInfo();
    int color_blue = 0XFF1295F7, color_gray = 0XFF8A8A8A;
    Button bt_GiveUp, bt_Complete, bt_TaskSource_MHDA;
    private TextView tv_task_grab;
    private Button bt_shensu;
    private NineGridLayout nineGridLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rewarddetailoffline);
        Log.e("RewardOff","RewardOffLineDetailActivity--onCreate()");
        initView();
        initEvent();
        getData();
    }


    private void initEvent() {
        ll_Back_MHDA.setOnClickListener(this);
        tv_StopTask_MHDA.setOnClickListener(this);
        bt_GiveUp.setOnClickListener(this);
        bt_Complete.setOnClickListener(this);
        bt_TaskSource_MHDA.setOnClickListener(this);
        cimg_Avatar_MHDA.setOnClickListener(this);
    }


    /**
     * 获取订单信息
     */
    private void getData() {
        AsyncHttpResponseHandler res = new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                String str_json = new String(bytes);
                Log.i(TAG, "str_json is " + str_json);
                try {
                    JSONObject obj = new JSONObject(str_json);
                    if (0 != obj.getInt("code")) {
                        LogUtil.e(CONTEXT, "initData 数据解析错误:code=" + obj.toString());
                        return;
                    }
                    Gson gson = new Gson();
                    info = gson.fromJson(obj.getJSONObject("result").toString(), MyHelperOfflineDetailInfo.class);
                    initOrderInfo();
                } catch (JSONException e) {
                    LogUtil.e(CONTEXT, "enter catch=" + e.toString());
                    e.printStackTrace();
                }
            }
            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                LogUtil.e(CONTEXT, "网络连接异常");
                ToastUtil.shortshow(CONTEXT, R.string.toast_error_net);
            }
        };
        String url = ServerAPIConfig.Get_RewardStatus + "session_id=" + sp.getString("session_id", null) + "&id=" + bundle.getString("id", null) + "&index=" + index + "&size=" + size + "&type=" + 2;
        AndroidAsyncHttp.get(url, res);
    }


    private void initView() {
        inflater = getLayoutInflater();
        sp = getSharedPreferences(StaticBean.USER_INFO, MODE_PRIVATE);
        tv_StopTask_MHDA = (TextView) findViewById(R.id.tv_StopTask_MHDA);
        ll_Back_MHDA = (LinearLayout) findViewById(R.id.ll_Back_MHDA);
        bundle = getIntent().getExtras();
        tv_UserName_MHDA = (TextView) findViewById(R.id.tv_UserName1_LifeReward);
        tv_Time_MHDA = (TextView) findViewById(R.id.tv_Time1_LifeReward);
        //tv_TaskCode_MHDA = (TextView) findViewById(R.id.tv_TaskCode_MHDA);
        tv_TaskReward_MHDA = (TextView) findViewById(R.id.tv_HelperPrice_LifeRewardDetail);
        tv_TaskLife_MHDA = (TextView) findViewById(R.id.tv_HelperPeriod_LifeRewardDetail);
        tv_Content_MHDA = (TextView) findViewById(R.id.tv_Content_LDA);
        tv_ShowAllContent_MHDA = (TextView) findViewById(R.id.tv_spread);
        tv_TaskDo_MHDA = (TextView) findViewById(R.id.tv_TaskDo_MHDA);
        tv_TaskComplete_MHDA = (TextView) findViewById(R.id.tv_TaskComplete_MHDA);
        tv_QiangDanCount_MHDA = (TextView) findViewById(R.id.tv_QiangDanCount_MHDA);
        tv_appealdetail = (TextView) findViewById(R.id.tv_appealdetail);
        tv_TaskRewardMoney = (TextView) findViewById(R.id.tv_TaskRewardMoney);
        cimg_Avatar_MHDA = (CircleImageView) findViewById(R.id.cimg_Avatar_HPDA);
        cimg_Avatar = (CircleImageView) findViewById(R.id.cimg_Avatar);
        img_TaskState_MHDA = (ImageView) findViewById(R.id.img_TaskState_MHDA);
        img_ShowAllContent_MHDA = (ImageView) findViewById(R.id.iv_spread);
        ((TextView) findViewById(R.id.tv_QiangDan_LifeRewardDetail)).setVisibility(View.GONE);
        ((LinearLayout) findViewById(R.id.ll_need_people_num)).setVisibility(View.GONE);

        bt_GiveUp = (Button) findViewById(R.id.bt_GiveUp);
        bt_Complete = (Button) findViewById(R.id.bt_Complete);
        bt_shensu = ((Button) findViewById(R.id.bt_shensu));
        bt_shensu.setOnClickListener(this);
        bt_TaskSource_MHDA = (Button) findViewById(R.id.bt_TaskSource_MHDA);

        tv_NickName_MHDA = (TextView) findViewById(R.id.tv_NickName_MHDA);
        tv_Location_MHDA = (TextView) findViewById(R.id.tv_Location_MHDA);
        tv_Time = (TextView) findViewById(R.id.tv_Time);
        tv_task_grab = ((TextView) findViewById(R.id.tv_TaskGrab_MHDA));

        nineGridLayout = (NineGridLayout) findViewById(R.id.imgs_9grid_layout);


    }


    /**
     * 初始化订单信息
     */
    private void initOrderInfo() {

        /**加载相应的信息*/
        tv_UserName_MHDA.setText(info.nickname);
        tv_Time_MHDA.setText(DateUtils.formatDate(new Date(info.create_time), DateUtils.yyyyMMddHHmm));
        //tv_TaskCode_MHDA.setText("" + info.id);
        tv_TaskReward_MHDA.setText(info.price + "元");
        tv_TaskLife_MHDA.setText(DateUtils.formatDate(new Date(info.start_time), DateUtils.yyyyMMddHHmm) + "至" + DateUtils.formatDate(new Date(info.end_time), DateUtils.yyyyMMddHHmm));
        tv_Content_MHDA.setText(info.description);
        tv_QiangDanCount_MHDA.setText("共有" + info.receiverList.size + "人抢单");
        tv_Time.setText(DateUtils.formatDate(new Date(info.receiverList.list.get(0).book_time), DateUtils.yyyyMMddHHmm));
        tv_NickName_MHDA.setText(info.receiverList.list.get(0).nickname);

        LogUtil.e("省市区="+info.receiverList.list.get(0).province + info.receiverList.list.get(0).city + info.receiverList.list.get(0).district);

        tv_Location_MHDA.setText(info.receiverList.list.get(0).province + info.receiverList.list.get(0).city + info.receiverList.list.get(0).district);


        LogUtil.e("status=" + info.receiverList.list.get(0).status + "=" + bundle.getString("receiver_appeal").length());
        /**
         *订单进入状态4即 4-发单者拒绝完成（发单者申诉），并且接单者申诉内容为空即接单者从未申诉过 时才可进行申诉
         */
        if (4 == info.receiverList.list.get(0).status) {
            if (0 == bundle.getString("receiver_appeal").length()) {
                tv_appealdetail.setVisibility(View.VISIBLE);
                tv_StopTask_MHDA.setVisibility(View.VISIBLE);
            } else {
                tv_appealdetail.setVisibility(View.VISIBLE);
                tv_StopTask_MHDA.setVisibility(View.VISIBLE);
                tv_StopTask_MHDA.setText("已申诉");
            }


        } else {
            tv_appealdetail.setVisibility(View.GONE);
            tv_StopTask_MHDA.setVisibility(View.GONE);
        }
        switch (info.receiverList.list.get(0).status) {

            /**订单状态在 0-已抢单*/
            case 0:

                if(2 == info.status){//订单结束
                    img_TaskState_MHDA.setImageResource(R.mipmap.task_progress_2dot_02);
                    tv_TaskDo_MHDA.setTextColor(color_blue);
                    tv_TaskDo_MHDA.setText("未选中");
                    tv_TaskComplete_MHDA.setVisibility(View.GONE);
                    tv_TaskRewardMoney.setVisibility(View.GONE);
                }else {
                    img_TaskState_MHDA.setImageResource(R.mipmap.task_progress_4dot_01);
                    tv_TaskDo_MHDA.setTextColor(color_gray);
                    tv_TaskComplete_MHDA.setTextColor(color_gray);
                    tv_TaskRewardMoney.setTextColor(color_gray);
                }

                break;

            /**订单状态在 1-签单成功（进行服务），*/
            case 1:
                img_TaskState_MHDA.setImageResource(R.mipmap.task_progress_4dot_02);
                tv_TaskDo_MHDA.setTextColor(color_blue);
                tv_TaskComplete_MHDA.setTextColor(color_gray);
                tv_TaskRewardMoney.setTextColor(color_gray);

                bt_Complete.setVisibility(View.VISIBLE);
                bt_GiveUp.setVisibility(View.VISIBLE);

                break;
            /**订单状态在 2-标记完成（待收款），*/
            case 2:
                img_TaskState_MHDA.setImageResource(R.mipmap.task_progress_4dot_03);
                tv_TaskDo_MHDA.setTextColor(color_blue);
                tv_TaskComplete_MHDA.setTextColor(color_blue);
                tv_TaskRewardMoney.setTextColor(color_gray);

                bt_Complete.setVisibility(View.GONE);
                bt_GiveUp.setVisibility(View.GONE);
                break;
            /**订单状态在 3-发单者确认完成*/
            case 3:
                img_TaskState_MHDA.setImageResource(R.mipmap.task_progress_4dot_04);
                tv_TaskDo_MHDA.setTextColor(color_blue);
                tv_TaskComplete_MHDA.setTextColor(color_blue);
                tv_TaskRewardMoney.setTextColor(color_blue);

                bt_Complete.setVisibility(View.GONE);
                bt_GiveUp.setVisibility(View.GONE);
                break;
            /**订单状态在 4-发单者拒绝完成（发单者申诉），*/
            case 4:
                img_TaskState_MHDA.setImageResource(R.mipmap.task_progress_2dot_01);
                tv_task_grab.setText("申诉中");
                tv_TaskDo_MHDA.setText("申诉完成");
                tv_TaskDo_MHDA.setTextColor(color_gray);
                bt_shensu.setVisibility(View.VISIBLE);
                tv_TaskComplete_MHDA.setVisibility(View.GONE);
                tv_TaskRewardMoney.setVisibility(View.GONE);
                bt_Complete.setVisibility(View.GONE);
                bt_GiveUp.setVisibility(View.GONE);
                break;
            /**订单状态在 5-申诉判定完成*/
            case 5://等接口,暂时不做
                img_TaskState_MHDA.setImageResource(R.mipmap.task_progress_2dot_02);
                tv_task_grab.setText("申诉中");
                tv_TaskDo_MHDA.setText("申诉完成");
                tv_TaskDo_MHDA.setTextColor(color_blue);

                tv_TaskComplete_MHDA.setVisibility(View.GONE);
                tv_TaskRewardMoney.setVisibility(View.GONE);

                bt_Complete.setVisibility(View.GONE);
                bt_GiveUp.setVisibility(View.GONE);

                bt_shensu.setVisibility(View.VISIBLE);
                bt_shensu.setBackgroundColor(Color.GRAY);
                break;
            /**订单状态在 6-抢单被拒绝*/
            case 6:
                img_TaskState_MHDA.setImageResource(R.mipmap.task_progress_2dot_02);

                tv_TaskDo_MHDA.setTextColor(color_blue);
                tv_TaskDo_MHDA.setText("未选中");

                tv_TaskComplete_MHDA.setVisibility(View.GONE);
                tv_TaskRewardMoney.setVisibility(View.GONE);

                bt_Complete.setVisibility(View.GONE);
                bt_GiveUp.setVisibility(View.GONE);
                break;
            /**订单状态在 7-接单者放弃*/
            case 7:
                img_TaskState_MHDA.setImageResource(R.mipmap.ic_takemoney_reward);//


                tv_TaskDo_MHDA.setTextColor(color_blue);
                tv_TaskComplete_MHDA.setTextColor(color_blue);
                tv_TaskComplete_MHDA.setText("放弃任务");

                tv_TaskRewardMoney.setVisibility(View.GONE);

                bt_Complete.setVisibility(View.GONE);
                bt_GiveUp.setVisibility(View.GONE);
                break;
        }
        QiniuUtils.setAvatarByIdFrom7Niu(RewardOffLineDetailActivity.this,cimg_Avatar_MHDA,info.portrait);//设置发单者头像
        QiniuUtils.setAvatarByIdFrom7Niu(RewardOffLineDetailActivity.this,cimg_Avatar,info.receiverList.list.get(0).portrait);//设置接单者头像

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
                        Intent intent = new Intent(RewardOffLineDetailActivity.this, PhotoShowActivity.class);
                        intent.putExtra("CURRENT_POS", imgPos);
                        intent.putStringArrayListExtra("IDS", ids);
                        startActivity(intent);
                    }
                }
            });
        }else {
            nineGridLayout.setVisibility(View.GONE);
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
            /**溯源*/
            case R.id.bt_TaskSource_MHDA:
                Bundle bundle_Source = new Bundle();
                bundle_Source.putString("id", info.id);
                bundle_Source.putString("receiver_id", info.receiverList.list.get(0).receiver_id);
                bundle_Source.putInt("type", 2);
                startActivity(MyHelperTaskSourceActivity.class, bundle_Source);
                break;
            /**放弃任务*/
            case R.id.bt_GiveUp:
                Bundle bundle_Appeal = new Bundle();
                bundle_Appeal.putString("id", bundle.getString("id"));
                startActivity(RewardOffLineGiveUpActivity.class, bundle_Appeal);
                break;
            /**完成任务*/
            case R.id.bt_Complete:
                compeleteTask(info.id);
                break;
            case R.id.ll_Back_MHDA:
                this.finish();
                break;
            /**申诉*/
            case R.id.tv_StopTask_MHDA:
                if (tv_StopTask_MHDA.getText().toString().equals("订单结束")) {
                    ToastUtil.shortshow(getApplicationContext(), "您的订单已结束");
                    return;
                }
                if(4 != info.receiverList.list.get(0).status){
                    ToastUtil.shortshow(CONTEXT, "发单者未申诉,你不能申诉");
                    return;
                }
                LogUtil.e("申诉内容="+ bundle.getString("receiver_appeal"));
                if (0< bundle.getString("receiver_appeal").length()) {
                    ToastUtil.shortshow(CONTEXT, "你已经申诉");
                    return;
                }
                startActivity(RewardOffLineAppealActivity.class, bundle);
                break;
            case R.id.cimg_Avatar_MHDA:
                //startActivity(PersonalDataActivity.class);
                String user_id = info.user_id;
                String nickName = info.nickname;
                Bundle bundle = new Bundle();
                bundle.putString("user_id", user_id);
                bundle.putString("nickName", nickName);
                startActivity(LookOthersMassageActivity.class, bundle);
                break;
            case R.id.bt_shensu://进入申诉页

                Bundle bundle2 = new Bundle();
                bundle2.putString("id",info.id);

                bundle2.putString("process_id",info.receiverList.list.get(0).id);
                bundle2.putInt("flag",2);//2标记接单者

                startActivity(DeclarationActivity.class, bundle2);
                break;
        }
    }


    /**
     * 求助-接单者标记完成
     *
     * @param id 订单编号
     */
    private void compeleteTask(String id) {
        AsyncHttpResponseHandler res = new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                String str_json = new String(bytes);
                try {
                    JSONObject obj = new JSONObject(str_json);
                    if (0 != obj.getInt("code")) {
                        LogUtil.e(CONTEXT, "initData 数据解析错误:code=" + obj.toString());
                        return;
                    }
                    getData();
                    ToastUtil.shortshow(CONTEXT, R.string.toast_compeletetask_success);
                } catch (JSONException e) {
                    LogUtil.e(CONTEXT, "enter catch=" + e.toString());
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
        params.put("id", id);
        AndroidAsyncHttp.post(ServerAPIConfig.Do_CompeleteTask, params, res);
    }
}
