package com.djx.pin.improve.detail;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.djx.pin.R;
import com.djx.pin.activity.MyHelperOnLineRewardStopActivity;
import com.djx.pin.beans.CivilizationDetailCommentInfo;
import com.djx.pin.beans.EventBeans;
import com.djx.pin.beans.HelpPeopleDetailEntity;
import com.djx.pin.beans.StaticBean;
import com.djx.pin.beans.UserInfo;
import com.djx.pin.improve.base.activity.BasePermissionActivity;
import com.djx.pin.improve.positiveenergy.detail.WishTreeDetailFragment;
import com.djx.pin.utils.ToastUtil;
import com.djx.pin.utils.myutils.ConstantUtils;
import com.djx.pin.utils.myutils.PopupWindowUtils;

import de.greenrobot.event.EventBus;
import de.greenrobot.event.Subscribe;
import de.greenrobot.event.ThreadMode;
import io.rong.imkit.RongIM;

/**
 * Created by 柯传奇 on 2016/11/11 0011.
 */
public class OrderDetailActivity extends BasePermissionActivity implements View.OnClickListener{


    private String prev_user_id;
    private HelpPeopleDetailEntity detailEntity;
    private CivilizationDetailCommentInfo.Result cultureWallInfo;
    private View parentView;
    private LinearLayout linearLayout;
    private TextView tv_stop_order;
    private Bundle bundle;
    private int ui;

    /**
     * @return 布局id
     */
    @Override
    protected int getContentViewId() {
        return R.layout.activity_detail_common;
    }

    @Subscribe(threadMode = ThreadMode.MainThread)
    public void onEvent(EventBeans eventBeans){
        if(eventBeans.style == ConstantUtils.ORDER_FINISH){
            tv_stop_order.setText("订单结束");
        }
    }

    @Override
    protected void initWidget() {
        EventBus.getDefault().register(this);
        findViewById(R.id.iv_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        linearLayout = (LinearLayout) findViewById(R.id.share2comment);
        linearLayout.findViewById(R.id.ll_Share_CDA).setOnClickListener(this);
        linearLayout.findViewById(R.id.ll_Comment_CDA).setOnClickListener(this);
        linearLayout.findViewById(R.id.ll_Chat_CDA).setOnClickListener(this);
        tv_stop_order = ((TextView) findViewById(R.id.tv_stop_order));
        parentView = findViewById(R.id.parent);
    }

    /**
     * 请求数据
     */
    @Override
    protected void initData() {
        bundle = getIntent().getExtras();
        ui = bundle.getInt("UI", 0);
        if(ui == ConstantUtils.WISH_TREE_DETAIL){//愿望树详情
            WishTreeDetailFragment wishTreeDetailFragment = new WishTreeDetailFragment();
            wishTreeDetailFragment.setArguments(bundle);
            replaceFragment(R.id.framelayout,wishTreeDetailFragment);
            wishTreeDetailFragment.setFragment2Activity(new Fragment2ActivityInterface() {
                @Override
                public void passData(Object o) {
                    cultureWallInfo = ((CivilizationDetailCommentInfo.Result) o);
                }
            });
            return;
        }
        prev_user_id = bundle.getString("share_user_id");
        int status = bundle.getInt("status");
        if (status == 2) {
            tv_stop_order.setText("订单结束");
        }
        OrderDetailFragment orderDetailFragment = new OrderDetailFragment();
        orderDetailFragment.setArguments(bundle);
        replaceFragment(R.id.framelayout,orderDetailFragment);
        switch (ui){
            case ConstantUtils.MY_HELP://我的求助详情
                linearLayout.setVisibility(View.GONE);
                tv_stop_order.setVisibility(View.VISIBLE);
                tv_stop_order.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //停止招募
                        if (tv_stop_order.getText().toString().equals("订单结束")) {
                            ToastUtil.shortshow(getApplicationContext(), "您的订单已结束");
                        } else {
                            startActivity(MyHelperOnLineRewardStopActivity.class, bundle);
                            OrderDetailActivity.this.finish();
                        }
                    }
                });
                break;
            case ConstantUtils.HELP_PEOPLE_ONLINE://网络悬赏详情
            case ConstantUtils.HELP_PEOPLE://帮人详情
                orderDetailFragment.setFragment2Activity(new Fragment2ActivityInterface() {
                    @Override
                    public void passData(Object o) {
                        detailEntity = ((HelpPeopleDetailEntity) o);
                    }
                });
                break;
        }
    }
    @Override
    public void onClick(View v) {
        if(v.getId() != R.id.iv_back){
            if (getSharedPreferences(StaticBean.USER_INFO, MODE_PRIVATE).getBoolean("isLogined", false) == false) {
                ToastUtil.shortshow(this, R.string.toast_non_login);
                return ;
            }
        }
        String userId;
        String detailId;
        String description;
        if(ui == ConstantUtils.WISH_TREE_DETAIL){
            if(cultureWallInfo == null){return;}
            userId = cultureWallInfo.user_id;
            detailId = cultureWallInfo.id;
            description = cultureWallInfo.description;
        }else {
            if(detailEntity == null){return;}
            userId = detailEntity.user_id;
            detailId = detailEntity.id;
            description = detailEntity.description;
        }
        //type 被评论内容类别，1-求助，2-文明墙，7-网络悬赏
        int target_type = 1;
        switch (ui){
            case ConstantUtils.HELP_PEOPLE:
                target_type = 1;
                break;
            case ConstantUtils.WISH_TREE_DETAIL:
                target_type = 2;
                break;
            case ConstantUtils.HELP_PEOPLE_ONLINE:
                target_type = 7;
                break;
        }
        switch (v.getId()){
            case R.id.ll_Chat_CDA://私信
                //检查是否是同一用户
                String user_id = getSharedPreferences(StaticBean.USER_INFO, Context.MODE_PRIVATE).getString("user_id", null);
                if (userId.equals(user_id)) {
                    ToastUtil.shortshow(this, R.string.toast_error_talk);
                    return;
                }
                startRecordByPermissions();
                break;
            case R.id.ll_Comment_CDA://评论

                PopupWindowUtils.commentPopupWindow(detailId,target_type, UserInfo.getSessionID(this), this, parentView, new PopupWindowUtils.CommentSuccedCallBack() {
                    @Override
                    public void commentSucceed() {
                        ToastUtil.shortshow(OrderDetailActivity.this,"评论成功");
                        initData();
                    }
                });
                break;
            case R.id.ll_Share_CDA://分享
                //线下时才显示pin
                if(ui == ConstantUtils.HELP_PEOPLE){
                    PopupWindowUtils.setPinSucceedCallBack(new PopupWindowUtils.PinSuccedCallBack() {
                        @Override
                        public void pinSucceedCallBack() {
                            initData();
                        }
                    },prev_user_id);
                    PopupWindowUtils.sharePopupWindow(this, detailId,target_type, UserInfo.getSessionID(this), description, parentView,true);
                }else {
                    PopupWindowUtils.sharePopupWindow(this, detailId,target_type, UserInfo.getSessionID(this), description, parentView,false);
                }
                break;
        }
    }

    @Override
    public void excuteActionContainRecordPermision() {
        super.excuteActionContainRecordPermision();
        if (RongIM.getInstance() != null) {
            try {
                //获取权限后执行的动作
                if(ui == ConstantUtils.WISH_TREE_DETAIL){
                    RongIM.getInstance().startPrivateChat(this, cultureWallInfo.user_id, cultureWallInfo.nickname);
                }else {
                    RongIM.getInstance().startPrivateChat(this, detailEntity.user_id, detailEntity.nickname);
                }
            } catch (Exception e) {
                Toast.makeText(this, R.string.record_permision_fail_string, Toast.LENGTH_LONG).show();
            }
        }
    }
}
