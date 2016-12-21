package com.djx.pin.improve.detail;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.djx.pin.R;
import com.djx.pin.activity.LookOthersMassageActivity;
import com.djx.pin.activity.MyHelperOffLineDetailActivity;
import com.djx.pin.activity.NavigationActivity;
import com.djx.pin.activity.RewardOffLineDetailActivity;
import com.djx.pin.activity.RewardOnLineDetailActivity;
import com.djx.pin.activity.TextActivity;
import com.djx.pin.activity.VideoPlayActivity;
import com.djx.pin.adapter.MyHelperOnLineUpload_MediaAdapter;
import com.djx.pin.beans.HelpPeopleDetailEntity;
import com.djx.pin.beans.IDTokenInfo;
import com.djx.pin.beans.StaticBean;
import com.djx.pin.beans.UserInfo;
import com.djx.pin.improve.ZhongMiAPI;
import com.djx.pin.improve.base.fragment.BasePermissionFragment;
import com.djx.pin.improve.common.PhotoShowActivity;
import com.djx.pin.improve.utils.DialogUtils;
import com.djx.pin.myview.CircleImageView;
import com.djx.pin.serverapiconfig.ServerAPIConfig;
import com.djx.pin.ui.CommonDialog;
import com.djx.pin.utils.AndroidAsyncHttp;
import com.djx.pin.utils.DateUtils;
import com.djx.pin.utils.FileUtil;
import com.djx.pin.utils.LogUtil;
import com.djx.pin.utils.QiniuUtils;
import com.djx.pin.utils.ToastUtil;
import com.djx.pin.utils.myutils.ConstantUtils;
import com.djx.pin.widget.CustomImageView;
import com.djx.pin.widget.NineGridLayout;
import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import cz.msebera.android.httpclient.Header;

/**
 * Created by 柯传奇 on 2016/11/22 0022.
 */
public class OrderDetailFragment extends BasePermissionFragment implements View.OnClickListener {

    private SwipeRefreshLayout swipeRefreshLayout;
    private LinearLayoutManager linearLayoutManager;
    private CommentAdapter commentAdapter;
    private MyHelperOnLineUpload_MediaAdapter upload_mediaAdapter;
    private NineGridLayout nineGridLayout;
    private CircleImageView headview_iv_avatar;
    private TextView headview_tv_username;
    private TextView headview_tv_time, headview_tv_money, headview_tv_needLimit, headview_tv_period, headview_tv_orderType, headview_tv_content, headview_tv_order, headview_tv_location;
    private LinearLayout headview_pin_avatar;
    private SharedPreferences sp;
    private String prev_user_id;
    private HelpPeopleDetailEntity detailEntity;
    private int total_status;
    private int taskState;
    private boolean otherFlag;
    private String id;
    private int ui_type;
    private RelativeLayout headview_rl_location;
    private RadioGroup headview_radioGroup;
    private RecyclerView recyclerView;
    private View headview;
    private GridLayoutManager gridLayoutManager;
    private HelpPeopleDetailEntity.Receiver receiverinfo;
    private RadioButton headview_rb01,headview_rb02,headview_rb03;
    private int taskType;////悬赏类型，1-照片悬赏，2-视频悬赏，3-文字悬赏（其他悬赏）
    private MyHelperOnLineUpload_OtherAdapter upload_otherAdapter;
    private int myHelpType = 3; //myHelpType: 1-分享列表，2-评论列表，3-所有接单者列表，4-查看自己抢单

    @Override
    protected int setLayoutId() {
        return R.layout.base_list_fragment;
    }

    @Override
    protected void initData() {
        initData(myHelpType);
    }

    @Override
    protected void initView(View view) {
        sp = getActivity().getSharedPreferences(StaticBean.USER_INFO, Context.MODE_PRIVATE);
        Bundle bundle = getArguments();
        ui_type = bundle.getInt("UI", 0);
        id = bundle.getString("id");
        taskState = bundle.getInt("status");
        prev_user_id = bundle.getString("share_user_id");
        editText = new EditText(getActivity());
        swipeRefreshLayout = ((SwipeRefreshLayout) view.findViewById(R.id.swiperefreshlayout));
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        linearLayoutManager = new LinearLayoutManager(getContext());
        gridLayoutManager = new GridLayoutManager(getContext(), 3);

        swipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_light, android.R.color.holo_red_light, android.R.color.holo_orange_light, android.R.color.holo_green_light);
        headview = View.inflate(getContext(), R.layout.activity_order_detail_headview, null);
        nineGridLayout = ((NineGridLayout) headview.findViewById(R.id.imgs_9grid_layout));
        headview_iv_avatar = ((CircleImageView) headview.findViewById(R.id.headview_iv_avatar));
        headview_tv_username = ((TextView) headview.findViewById(R.id.headview_tv_userName));
        headview_tv_time = ((TextView) headview.findViewById(R.id.headview_tv_time));
        headview_tv_money = ((TextView) headview.findViewById(R.id.headview_tv_money));
        headview_tv_needLimit = ((TextView) headview.findViewById(R.id.headview_tv_needLimit));
        headview_tv_period = ((TextView) headview.findViewById(R.id.headview_tv_period));
        headview_tv_orderType = ((TextView) headview.findViewById(R.id.headview_tv_orderType));
        headview_tv_content = ((TextView) headview.findViewById(R.id.headview_tv_content));
        headview_tv_order = ((TextView) headview.findViewById(R.id.headview_tv_order));
        headview_tv_location = (TextView) headview.findViewById(R.id.tv_Location_LifeRewardDetail);
        headview_pin_avatar = ((LinearLayout) headview.findViewById(R.id.ll_avatar_pin));
        headview_rl_location = ((RelativeLayout) headview.findViewById(R.id.headview_rl_location));
        headview_radioGroup = ((RadioGroup) headview.findViewById(R.id.rg));
        headview_rb01 = ((RadioButton) headview.findViewById(R.id.rb_01));
        headview_rb02 = ((RadioButton) headview.findViewById(R.id.rb_02));
        headview_rb03 = ((RadioButton) headview.findViewById(R.id.rb_03));


        switch (ui_type) {
            case ConstantUtils.MY_HELP://我的求助详情
                headview_rb02.setChecked(true);
                break;
            case ConstantUtils.HELP_PEOPLE://帮人详情
                commentAdapter = new CommentAdapter(getContext());
                commentAdapter.addHeader(headview);
                recyclerView.setLayoutManager(linearLayoutManager);
                recyclerView.setAdapter(commentAdapter);
                headview_rb02.setVisibility(View.INVISIBLE);
                headview_rb01.setChecked(true);
                break;
            case ConstantUtils.HELP_PEOPLE_ONLINE://网络悬赏详情
                break;
        }
    }

    @Override
    protected void initEvent() {
        headview_iv_avatar.setOnClickListener(this);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                initData(myHelpType);
            }
        });
        headview_tv_order.setOnClickListener(this);
        headview_tv_location.setOnClickListener(this);
        headview_radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (ui_type == ConstantUtils.MY_HELP||ui_type == ConstantUtils.HELP_PEOPLE_ONLINE) {
                    switch (checkedId) {
                        case R.id.rb_01://评论
                            if (myHelpType != 2) {
                                myHelpType = 2;
                                commentAdapter = new CommentAdapter(getContext());
                                commentAdapter.addHeader(headview);
                                recyclerView.setLayoutManager(linearLayoutManager);
                                recyclerView.setAdapter(commentAdapter);
                                initData(myHelpType);
                                headview_rb01.setChecked(true);
                                headview_rb02.setChecked(false);
                            }
                            break;
                        case R.id.rb_02://已上传
                            if (myHelpType != 3) {//1-分享列表，2-评论列表，3-所有接单者列表，4-查看自己抢单
                                myHelpType = 3;
                                //判断悬赏类型
                                switch (taskType){
                                    case 1:
                                    case 2:
                                        upload_mediaAdapter = new MyHelperOnLineUpload_MediaAdapter(getContext());
                                        upload_mediaAdapter.addHeader(headview);
                                        recyclerView.setLayoutManager(gridLayoutManager);
                                        recyclerView.setAdapter(upload_mediaAdapter);
                                        break;
                                    case 3:
                                        upload_otherAdapter = new MyHelperOnLineUpload_OtherAdapter(getContext());
                                        upload_otherAdapter.addHeader(headview);
                                        recyclerView.setLayoutManager(linearLayoutManager);
                                        recyclerView.setAdapter(upload_otherAdapter);
                                        break;
                                }
                                initData(myHelpType);
                                headview_rb01.setChecked(false);
                                headview_rb02.setChecked(true);
                            }
                            break;
                        case R.id.rb_03://分享
                            break;
                    }
                }
            }
        });
    }


    /**
     * 请求数据
     */
    protected void initData(int myHelpType) {
        switch (ui_type) {
            case ConstantUtils.MY_HELP://我的求助详情
                LogUtil.e("--------------------------------1-分享列表，2-评论列表，3-所有接单者列表，4-查看自己抢单----" + myHelpType);
                ZhongMiAPI.getHelpMeDetail(UserInfo.getSessionID(getContext()), id, 0, myHelpType, mHandler);
                break;
            case ConstantUtils.HELP_PEOPLE://帮人详情
                ZhongMiAPI.getHelpDetail(UserInfo.getSessionID(getContext()), id, 0, 2, mHandler);//类型，1-分享列表，2-评论列表
                break;
            case ConstantUtils.HELP_PEOPLE_ONLINE:
                ZhongMiAPI.getOnlineRewardDetail(UserInfo.getSessionID(getContext()), id, 0, myHelpType, mHandler);//类型，1-分享列表，2-评论列表，3-所有接单者列表，4-查看自己抢单
                break;
        }
    }

    /**
     * 解析数据,设置数据
     *
     * @param gson
     * @param json
     */
    @Override
    protected void parseData(Gson gson, String json) {
        Log.e("json", "json:-------------------" + json);
        if (swipeRefreshLayout.isRefreshing()) {
            swipeRefreshLayout.setRefreshing(false);
        }
        detailEntity = gson.fromJson(json, HelpPeopleDetailEntity.class);
        total_status = detailEntity.status;
        taskState = detailEntity.process_status;
        taskType = detailEntity.type;////悬赏类型，1-照片悬赏，2-视频悬赏，3-文字悬赏（其他悬赏）


        //1.设置headview的数据
        QiniuUtils.setAvatarByIdFrom7Niu(getActivity(), headview_iv_avatar, detailEntity.portrait);
        headview_tv_username.setText(detailEntity.nickname);
        headview_tv_content.setText(detailEntity.description);
        headview_tv_money.setText(detailEntity.price + "");
        headview_tv_needLimit.setText(detailEntity.receiver_limit + "");
        headview_tv_time.setText(DateUtils.formatDate(new Date(detailEntity.start_time), DateUtils.yyyyMMddHHmm));
        headview_tv_period.setText(DateUtils.formatDate(new Date(detailEntity.start_time), DateUtils.yyyyMMddHHmm) + " 至 " + DateUtils.formatDate(new Date(detailEntity.end_time), DateUtils.yyyyMMddHHmm));

        final ArrayList<String> ids = new ArrayList<>();
        if (detailEntity.media != null && detailEntity.media.size() > 0) {
            for (int i = 0; i < detailEntity.media.size(); i++) {
                ids.add(detailEntity.media.get(i).media_id);
            }
            QiniuUtils.set9GridByIdsFrom7Niu(getActivity(), ids, detailEntity.id, nineGridLayout);
            nineGridLayout.setImageShowListener(new NineGridLayout.ImageShowListener() {
                @Override
                public void imageShow(int imgPos,ArrayList<CustomImageView> imageViews) {
                    if (ids != null) {
                        Intent intent = new Intent(getActivity(), PhotoShowActivity.class);
                        intent.putExtra("CURRENT_POS", imgPos);
                        intent.putStringArrayListExtra("IDS", ids);
                        startActivity(intent);
                    }
                }
            });
        }
        switch (ui_type) {//根据帮人详情还是我的求助  显示隐藏headview的部分控件
            case ConstantUtils.HELP_PEOPLE://帮人
                headview_tv_location.setText(detailEntity.address);
                setOrderState();
                //加载pin头像
                DisplayMetrics dm = new DisplayMetrics();
                getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
                headview_pin_avatar.removeAllViews();
                for (int j = 0; j < detailEntity.pin.size() && j < 3; j++) {
                    com.djx.pin.myview.CircleImageView imageView = new com.djx.pin.myview.CircleImageView(getActivity());
                    imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                    int wid = (int) (dm.density * 28);
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(wid, wid);
                    imageView.setLayoutParams(params);
                    QiniuUtils.setAvatarByIdFrom7Niu(getActivity(), imageView, detailEntity.pin.get(j).portrait);
                    headview_pin_avatar.addView(imageView);
                }
                if (fragment2Activity != null) {
                    fragment2Activity.passData(detailEntity);
                }
                //2.设置适配器数据
                commentAdapter.addData(detailEntity.comment);//添加评论
                break;

            case ConstantUtils.HELP_PEOPLE_ONLINE://网络悬赏详情
                headview_tv_order.setVisibility(View.VISIBLE);
                if (fragment2Activity != null) {
                    fragment2Activity.passData(detailEntity);
                }
                setOnlineOrderState();
                setTypeData();
                break;

            case ConstantUtils.MY_HELP://我的求助
                headview_tv_order.setVisibility(View.GONE);
                setOrderState();
                setTypeData();
                break;
        }

    }


    public void setTypeData(){
        headview_rl_location.setVisibility(View.GONE);
        //2.设置适配器数据
        switch (myHelpType) {//myHelpType: 1-分享列表，2-评论列表，3-所有接单者列表，4-查看自己抢单
            case 3:
                int receiver_limit = detailEntity.receiver_limit;
                int rewardedNum =0;
                List<HelpPeopleDetailEntity.Receiver> receiverList = detailEntity.receiver;
                if(receiverList!=null){
                    for (int j = 0; j < receiverList.size(); j++) {
                        if(receiverList.get(j).status == 1){//状态，0-已抢单（已回答），1-已打赏，2-拒绝打赏
                            rewardedNum++;
                        }
                    }
                }
                switch (taskType){//悬赏类型，1-照片悬赏，2-视频悬赏，3-文字悬赏（其他悬赏）
                    case 1:
                    case 2:
                        if(upload_mediaAdapter == null){
                            upload_mediaAdapter = new MyHelperOnLineUpload_MediaAdapter(getContext());
                            upload_mediaAdapter.addHeader(headview);
                            upload_mediaAdapter.setLimitTime(detailEntity.end_time);
                            recyclerView.setLayoutManager(gridLayoutManager);
                            recyclerView.setAdapter(upload_mediaAdapter);
                        }
                        upload_mediaAdapter.setMediaType(taskType);
                        upload_mediaAdapter.setTaskStatus(total_status);
                        upload_mediaAdapter.setRewarded2ReceiverLimit(rewardedNum,receiver_limit);
                        upload_mediaAdapter.setOnRewardListener(new MyHelperOnLineUpload_MediaAdapter.OnRewardListener() {
                            @Override
                            public void setOnRewardListener(int pos) {
                                setOnReward(pos);
                            }
                        });
                        upload_mediaAdapter.addAll(detailEntity.receiver);//添加接单者上传信息
                        break;
                    case 3:
                        if(upload_otherAdapter == null){
                            upload_otherAdapter = new MyHelperOnLineUpload_OtherAdapter(getContext());
                            upload_otherAdapter.addHeader(headview);
                            upload_otherAdapter.setLimitTime(detailEntity.end_time);
                            recyclerView.setLayoutManager(linearLayoutManager);
                            recyclerView.setAdapter(upload_otherAdapter);
                        }
                        upload_otherAdapter.setTaskStatus(total_status);
                        upload_otherAdapter.setRewarded2ReceiverLimit(rewardedNum,receiver_limit);
                        upload_otherAdapter.setOnItemAvatarClickListener(new MyHelperOnLineUpload_OtherAdapter.OnItemAvatarClickListener() {
                            @Override
                            public void onClick(int pos) {
                                Bundle bundle = new Bundle();
                                bundle.putString("user_id", detailEntity.receiver.get(pos).receiver_id);
                                startActivity(LookOthersMassageActivity.class, bundle);
                            }
                        });
                        upload_otherAdapter.setOnItemOrderClickListener(new MyHelperOnLineUpload_OtherAdapter.OnItemOrderClickListener() {
                            @Override
                            public void onClick(int pos) {
                                HelpPeopleDetailEntity.Receiver receiver = detailEntity.receiver.get(pos);
                                if (total_status != 2) {//订单还未结束时
                                    if (receiver.status != 1 && receiver.status != 2) {//且 还未打赏|拒绝
                                        receiverinfo = receiver;
                                        editText.setText(receiver.content);

                                        CommonDialog.orderCheckShow(getContext(), "审核通过", "审核不通过", "文本内容", receiver.content, new CommonDialog.PositiveListener() {
                                            @Override
                                            public void onClick() {
                                                sendRewardOk();
                                            }
                                        }, new CommonDialog.NegativeListener() {
                                            @Override
                                            public void onClick() {
                                                sendRewardNo();
                                            }
                                        }, null,false, null);
                                    }
                                }
                            }
                        });
                        upload_otherAdapter.addAll(detailEntity.receiver);
                        break;
                }

                break;
            case 2:
                commentAdapter.addData(detailEntity.comment);//评论
                break;
        }
    }

    /****************************************************
     * 网络悬赏详情专用
     ****************************************************************/
    public void setOnlineOrderState() {
        if (detailEntity.user_id.equals(UserInfo.getUserID(getContext()))) {//自己
            headview_tv_order.setText("查看");
        } else if (total_status != 2) {
            if (taskState == -1) {//只有在非发单人查看时才有，订单处理状态，-1-未抢单，0-已抢单（已回答），1-已打赏，2-拒绝打赏
                headview_tv_order.setText("抢单");
            }else {
                headview_tv_order.setText("查看");
                otherFlag = true;
            }
        } else {//订单已关闭
            if(taskState != -1){//已过抢单
                headview_tv_order.setText("查看");
                otherFlag = true;
            }else {
                headview_tv_order.setText(R.string.order_closed);
                headview_tv_order.setBackgroundResource(R.drawable.ic_qiandanedbg);
            }
        }
    }

    /****************************************************
     * 帮人详情专用
     ****************************************************************/
    public void setOrderState() {
        LogUtil.e("---------------taskState-------------------" + taskState);
        if (total_status == 2) {//订单结束
            if (taskState != -1 && taskState != 6) {//已抢过单
                LogUtil.e("----------------------------------1");
                headview_tv_order.setBackgroundResource(R.drawable.ic_qiandanbg);
                headview_tv_order.setText("查看");
                otherFlag = true;
            } else {
                LogUtil.e("----------------------------------2");
                headview_tv_order.setBackgroundResource(R.drawable.ic_qiandanedbg);
                headview_tv_order.setText("已关闭");
            }
        } else if (detailEntity.user_id.equals(sp.getString("user_id", null))) {//自己单
            headview_tv_order.setBackgroundResource(R.drawable.ic_qiandanbg);
            headview_tv_order.setText("查看");
        } else {
            if (taskState != -1 && taskState != 6) {//已抢过单
                headview_tv_order.setBackgroundResource(R.drawable.ic_qiandanbg);
                headview_tv_order.setText("查看");
                otherFlag = true;//只有在非发单人查看时才有，订单处理状态，-1-未抢单，0-已抢单，1-签单成功（进行服务），
                // 2-标记完成（待收款），3-发单者确认完成，4-发单者拒绝完成（发单者申诉），5-申诉判定完成，6-抢单被拒绝，7-接单者放弃
            } else {
                headview_tv_order.setBackgroundResource(R.drawable.ic_qiandanbg);
                headview_tv_order.setText("抢单");
            }
        }
    }

    Fragment2ActivityInterface fragment2Activity;


    public void setFragment2Activity(Fragment2ActivityInterface fragment2Activity) {
        this.fragment2Activity = fragment2Activity;
    }

    /**
     * 用户抢单,
     * 点击抢单按钮执行此函数
     */
    public void acceptOrder() {
        AsyncHttpResponseHandler res = new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                String str_json = new String(bytes);
                try {
                    JSONObject obj = new JSONObject(str_json);
                    if (0 != obj.getInt("code")) {
                        ToastUtil.errorCode(getActivity(), obj.getInt("code"));
                        return;
                    }
                    //抢单成功
                    ToastUtil.shortshow(getActivity(), "抢单成功，请等待");
                    headview_tv_order.setBackgroundResource(R.drawable.ic_qiandanbg);
                    headview_tv_order.setText("查看");
                    otherFlag = true;
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                ToastUtil.shortshow(getActivity(), R.string.toast_error_net);
            }
        };
        RequestParams params = new RequestParams();
        params.put("session_id", UserInfo.getSessionID(getActivity()));
        params.put("id", id);
        params.put("prev_user_id", prev_user_id);
        AndroidAsyncHttp.post(ServerAPIConfig.Updata_AcceptOrder, params, res);
    }

    /**
     * Called when a view has been clicked.
     *
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_Location_LifeRewardDetail:
                Intent intent2 = new Intent(getContext(), NavigationActivity.class);
                intent2.putExtra("latitude", detailEntity.latitude);
                intent2.putExtra("longtitude", detailEntity.longitude);
                startActivity(intent2);
                break;
            case R.id.headview_tv_order://抢单
                if(!UserInfo.getIsLogin(getContext())){
                    ToastUtil.shortshow(getContext(),"请登录");
                    return;
                }
                if (headview_tv_order.getText().toString().trim().equals("查看")) {//是查看

                    if(ui_type == ConstantUtils.HELP_PEOPLE_ONLINE){//网络悬赏

                        if(otherFlag){//抢单者查看别人单
                            Bundle bundle = new Bundle();
                            bundle.putString("id",detailEntity.id);
                            bundle.putInt("status",detailEntity.status);//0-发布未支付，1-发单成功，2-订单结束（停止招募）
                            bundle.putInt("process_status",detailEntity.process_status);
                            startActivity(RewardOnLineDetailActivity.class,bundle);
                        }else {
                            Bundle bundle_Online = new Bundle();
                            bundle_Online.putString("id", detailEntity.id);
                            bundle_Online.putInt("status",detailEntity.status);
                            bundle_Online.putInt("UI", ConstantUtils.MY_HELP);
                            startActivity(OrderDetailActivity.class, bundle_Online);
                        }
                    }
                    else {
                        if (otherFlag) {
                            Bundle bundle_Offline = new Bundle();
                            bundle_Offline.putString("id", detailEntity.id);
                            bundle_Offline.putString("receiver_appeal", "");
                            bundle_Offline.putString("process_id", detailEntity.id);
                            bundle_Offline.putInt("status", detailEntity.status);
                            startActivity(RewardOffLineDetailActivity.class, bundle_Offline);
                        } else {
                            Bundle bundle_Offline = new Bundle();
                            bundle_Offline.putString("id", detailEntity.id);
                            bundle_Offline.putInt("receiver_limit", detailEntity.receiver_limit);
                            startActivity(MyHelperOffLineDetailActivity.class, bundle_Offline);
                        }
                    }
                } else {
                    if (taskState == 0) {
                        ToastUtil.shortshow(getActivity(), "不可重复抢单");
                        return;
                    }
                    CommonDialog.show(getActivity(), "确定", "取消", "确定抢单?", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if(ui_type == ConstantUtils.HELP_PEOPLE){//帮人
                                acceptOrder();
                            }else if(ui_type == ConstantUtils.HELP_PEOPLE_ONLINE){//网络悬赏

                                //选择照片
                                DialogUtils.AddImageDialog(context, new DialogUtils.SlectAlbumListener() {
                                    @Override
                                    public void onClick() {
                                        slectOnePicPermissions();
                                    }
                                }, new DialogUtils.SlectCameraListener() {
                                    @Override
                                    public void onClick() {
                                        startTakePhotoByPermissions();
                                    }
                                });
                            }
                        }
                    });
                }
                break;
            case R.id.headview_iv_avatar:
                Bundle bundle = new Bundle();
                bundle.putString("user_id", detailEntity.user_id);
                startActivity(LookOthersMassageActivity.class, bundle);
                break;

        }
    }



    /**
     * 网络悬赏接单
     */
    public void postRequest2Server(IDTokenInfo idTokenInfo) {
        ZhongMiAPI.postAcceptOnlineOrder(getContext(), detailEntity.id, idTokenInfo, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String str_json = new String(responseBody);
                try {
                    JSONObject obj = new JSONObject(str_json);
                    if (0 != obj.getInt("code")) {
                        ToastUtil.errorCode(getActivity(), obj.getInt("code"));
                        return;
                    }
                    //抢单成功
                    ToastUtil.shortshow(getActivity(), "抢单成功，请等待");
                    headview_tv_order.setBackgroundResource(R.drawable.ic_qiandanbg);
                    headview_tv_order.setText("查看");
                    otherFlag = true;
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

            }
        });
    }

    EditText editText;
    /*********************************************************
     * 我的求助专用
     ***************************************************************/
    public void setOnReward(int pos) {

        receiverinfo = detailEntity.receiver.get(pos);

        if (ui_type == ConstantUtils.HELP_PEOPLE_ONLINE) {//如果是网络悬赏

            if (detailEntity.type == 2) {//视频悬赏               （悬赏类型，1-照片悬赏，2-视频悬赏，3-文字悬赏（其他悬赏））
                //播放视频
                Intent intent = new Intent(getContext(), VideoPlayActivity.class);
                intent.putExtra("VIDEO_URL", receiverinfo.content);
                startActivity(intent);

            } else {//图片

                if (receiverinfo.media != null && receiverinfo.media.get(0).media_id != null) {
                    intent2ShowImage(receiverinfo.media.get(0).media_id);
                }
            }


        }else {//线下悬赏
            if (detailEntity.type == 2) {//视频悬赏               （悬赏类型，1-照片悬赏，2-视频悬赏，3-文字悬赏（其他悬赏））

                if (detailEntity.status == 2) {//订单结束  （订单状态，0-发布未支付，1-发单成功，2-订单结束（停止招募））
                    //播放视频
                    Intent intent = new Intent(getContext(), VideoPlayActivity.class);
                    intent.putExtra("VIDEO_URL", receiverinfo.content);
                    startActivity(intent);
                } else {//订单没结束
                    if (receiverinfo.status != 0) {//接单者订单状态:0-已抢单（已回答），1-已打赏，2-拒绝打赏
                        //播放视频
                        Intent intent = new Intent(getContext(), VideoPlayActivity.class);
                        intent.putExtra("VIDEO_URL", receiverinfo.content);
                        startActivity(intent);
                    } else {//抢单还未被确认
                        //待确认
                        CommonDialog.orderCheckShow(getContext(), "审核通过", "审核不通过", "视频链接", receiverinfo.content, new CommonDialog.PositiveListener() {
                            @Override
                            public void onClick() {
                                sendRewardOk();
                            }
                        }, new CommonDialog.NegativeListener() {
                            @Override
                            public void onClick() {
                                sendRewardNo();
                            }
                        }, new CommonDialog.TextContentOnClickListener() {
                            @Override
                            public void onClick() {
                                Bundle bundle = new Bundle();
                                bundle.putInt("TextContent", 4);
                                bundle.putString("url", receiverinfo.content.trim());
                                startActivity(TextActivity.class, bundle);
                            }
                        }, false, null);

                    }
                }

            } else {//图片
                if (detailEntity.status == 2) {//订单结束
                    //订单结束后浏览图片
                    if(receiverinfo.media != null && receiverinfo.media.get(0).media_id != null){
                        intent2ShowImage(receiverinfo.media.get(0).media_id);
                    }
                } else {//订单未结束审核
                    if (receiverinfo.status == 1 || receiverinfo.status == 2) {//如果订单未结束，但已打赏或拒绝
                        intent2ShowImage(receiverinfo.media.get(0).media_id);
                    } else {
                        CommonDialog.orderCheckShow(getContext(), "审核通过", "审核不通过", "图片", null, new CommonDialog.PositiveListener() {
                            @Override
                            public void onClick() {
                                sendRewardOk();
                            }
                        }, new CommonDialog.NegativeListener() {
                            @Override
                            public void onClick() {
                                sendRewardNo();
                            }
                        },null, true, receiverinfo.media.get(0).media_id);

                    }
                }
            }
        }

    }
    public void intent2ShowImage(String media_id){
        ArrayList<String> ids = new ArrayList<>();
        ids.add(media_id);
        Intent intent = new Intent(getContext(), PhotoShowActivity.class);
        Bundle bundle = new Bundle();
        bundle.putInt("CURRENT_POS", 0);
        intent.putStringArrayListExtra("IDS", ids);
        startActivity(intent);
    }


    private void sendRewardOk() {
        String url = ServerAPIConfig.RewardOk;
        RequestParams params = new RequestParams();
        params.put("session_id",UserInfo.getSessionID(getContext()));
        params.put("id", id);
        params.put("receiver_id", receiverinfo.receiver_id);
        AsyncHttpResponseHandler res = new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                String str = new String(bytes);
                try {
                    JSONObject obj = new JSONObject(str);
                    if (obj.getInt("code") == 0) {
                        ToastUtil.shortshow(getActivity(), "确定审核通过成功");
                    } else {
                        ToastUtil.errorCode(getActivity(),obj.getInt("code"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {

            }
        };
        AndroidAsyncHttp.post(url, params, res);
    }

    private void sendRewardNo() {
        String url = ServerAPIConfig.RewardNo;
        RequestParams params = new RequestParams();
        params.put("session_id", UserInfo.getSessionID(getActivity()));
        params.put("id", id);
        params.put("receiver_id", receiverinfo.receiver_id);
        AsyncHttpResponseHandler res = new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                String str = new String(bytes);
                try {
                    JSONObject obj = new JSONObject(str);
                    if (obj.getInt("code") == 0) {
                        ToastUtil.shortshow(getActivity(), "拒绝成功");
                    } else {
                        ToastUtil.errorCode(getActivity(),obj.getInt("code"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {

            }
        };
        AndroidAsyncHttp.post(url, params, res);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        LogUtil.e("onActivityResult"+requestCode);
        if (resultCode == Activity.RESULT_OK ) {
            ArrayList<String> list = new ArrayList<>();
            switch (requestCode) {
                case ConstantUtils.INTENT_IMAGE_CAPTURE:
                    list.add(path_Camera);
                    QiniuUtils.postRequestWithIMGS(getContext(), list, UserInfo.getSessionID(getContext()), 1, new QiniuUtils.PostRequestWithPics() {
                        @Override
                        public void postRequest(IDTokenInfo idTokenInfo_Pic) {
                            postRequest2Server(idTokenInfo_Pic);
                        }
                    });
                    break;
                case ConstantUtils.INTENT_READ_ALBUM:
                    if(null != data){
                        String picturePath = FileUtil.getPath(context, data.getData());
                        list.add(picturePath);
                        QiniuUtils.postRequestWithIMGS(getContext(), list, UserInfo.getSessionID(getContext()), 1, new QiniuUtils.PostRequestWithPics() {
                            @Override
                            public void postRequest(IDTokenInfo idTokenInfo_Pic) {
                                postRequest2Server(idTokenInfo_Pic);
                            }
                        });
                    }
                    break;
            }
        }
    }
}
