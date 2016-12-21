package com.djx.pin.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.djx.pin.R;
import com.djx.pin.beans.RewardInfo;
import com.djx.pin.myview.CircleImageView;
import com.djx.pin.utils.QiniuUtils;
import com.djx.pin.utils.TurnIntoTime;

import cz.msebera.android.httpclient.util.TextUtils;

/**
 * Created by Administrator on 2016/6/29.
 */
public class MyRewardAdapter extends MyBaseAdapter<RewardInfo.Result.Lists> implements View.OnClickListener {

    public OnFunctionChose getOnFunctionChose() {
        return onFunctionChose;
    }

    public void setOnFunctionChose(OnFunctionChose onFunctionChose) {
        this.onFunctionChose = onFunctionChose;
    }

    private OnFunctionChose onFunctionChose;

    public MyRewardAdapter(Context context, OnFunctionChose onFunctionChose) {
        super(context);
        this.onFunctionChose = onFunctionChose;
    }

    @Override
    public View getView(int position, View converView, ViewGroup parent) {
        ViewHolder vh;
        if (converView == null) {
            vh = new ViewHolder();
            converView = inflater.inflate(R.layout.lv_intem_myreward, null);
            vh.cimg_Avatar_MyHelper = (CircleImageView) converView.findViewById(R.id.cimg_Avatar_MyHelper);
            vh.tv_Massage_MyHelper = (TextView) converView.findViewById(R.id.tv_Massage_MyHelper);
            vh.tv_Reward_MyHelper = (TextView) converView.findViewById(R.id.tv_Reward_MyHelper);
            vh.tv_HelperTime_MyHelper = (TextView) converView.findViewById(R.id.tv_HelperTime_MyHelper);
            vh.tv_HelperState_MyHelper = (TextView) converView.findViewById(R.id.tv_HelperState_MyHelper);
            vh.tv_Rights = (TextView) converView.findViewById(R.id.tv_Rights);
            vh.tv_CancelOrder = (TextView) converView.findViewById(R.id.tv_CancelOrder);
            vh.tv_Completed = (TextView) converView.findViewById(R.id.tv_Completed);
            vh.ll_FunctionChose = (LinearLayout) converView.findViewById(R.id.ll_FunctionChose);
            converView.setTag(vh);
        } else {
            vh = (ViewHolder) converView.getTag();
        }
        RewardInfo.Result.Lists info = getItem(position);

        if(TextUtils.isEmpty(info.portrait)){
            vh.cimg_Avatar_MyHelper.setImageResource(R.mipmap.ic_defaultcontact);
        }else {
            QiniuUtils.setAvatarByIdFrom7Niu(context,vh.cimg_Avatar_MyHelper, info.portrait);
        }
        vh.ll_FunctionChose.setVisibility(View.GONE);

        vh.tv_Massage_MyHelper.setText(info.description);
        vh.tv_Reward_MyHelper.setText("" + info.price);
        if (info.content_type == 1) {//线下悬赏
            vh.tv_HelperTime_MyHelper.setText(TurnIntoTime.getLifeTime(info.book_time));
            switch (info.process_status) {
                case 0:
                    vh.tv_HelperState_MyHelper.setText("等待发单者确认");

                    break;
                case 1:
                    if (info.status == 2) {
                        vh.tv_HelperState_MyHelper.setText("悬赏已结束");
                    } else {
                        vh.tv_HelperState_MyHelper.setText("正在服务");
                    }

                    break;
                case 2:
                    vh.tv_HelperState_MyHelper.setText("已标记完成任务");

                    break;
                case 3:
                    vh.tv_HelperState_MyHelper.setText("交易成功");

                    break;
                case 4:
                    vh.tv_HelperState_MyHelper.setText("发单人对您的服务不满意,您可进行申诉");

                    break;
                case 5:
                    vh.tv_HelperState_MyHelper.setText("申诉判定完成");

                    break;
                case 6:
                    vh.tv_HelperState_MyHelper.setText("订单关闭");

                    break;
                case 7:
                    vh.tv_HelperState_MyHelper.setText("已放弃");

                    break;
            }
        } else if (info.content_type == 7) {//网络悬赏
            vh.tv_HelperTime_MyHelper.setText(TurnIntoTime.getLifeTime(info.book_time));
            switch (info.process_status) {
                case 0:
                    //订单结束，但是已经抢单，显示悬赏已结束
                    if (info.status == 2) {
                        vh.tv_HelperState_MyHelper.setText(R.string.order_closed);
                    } else {
                        vh.tv_HelperState_MyHelper.setText(R.string.order_2b_verify);
                    }
                    break;
                case 1:
                    vh.tv_HelperState_MyHelper.setText(R.string.order_rewarded);
                    break;
                case 2:
                    if (info.status == 2) {
                        vh.tv_HelperState_MyHelper.setText(R.string.order_closed);
                    }else {
                        vh.tv_HelperState_MyHelper.setText(R.string.order_not_pass);
                    }
                    break;
            }

        } else if (info.content_type == 5) {//sos
            vh.tv_Massage_MyHelper.setText(info.nickname + context.getString(R.string.sos_desc));
            vh.tv_HelperTime_MyHelper.setText(TurnIntoTime.getLifeTime(info.start_time));
            switch (info.process_status) {
                case 0:
                    vh.tv_HelperState_MyHelper.setText("已抢单");
                    break;
                case 1:
                    vh.tv_HelperState_MyHelper.setText("完成等待确认");
                    break;
                case 2:
                    vh.tv_HelperState_MyHelper.setText("获得赏金");
                    break;
                case 3:
                    vh.tv_HelperState_MyHelper.setText("完成等赏");
                    break;
                case 4:
                    vh.tv_HelperState_MyHelper.setText("申诉判定完成");
                    break;
                case 5:
                    vh.tv_HelperState_MyHelper.setText("中途放弃");
                    break;
            }

        }
        vh.tv_Rights.setOnClickListener(this);
        vh.tv_Rights.setTag(position);

        vh.tv_Completed.setOnClickListener(this);
        vh.tv_Completed.setTag(position);

        vh.tv_CancelOrder.setOnClickListener(this);
        vh.tv_CancelOrder.setTag(position);

        return converView;
    }

    //参数：1,申诉维权；2，取消订单；3，确认完成
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_Rights:
                onFunctionChose.setOnFunctionChose(1, v);
                break;
            case R.id.tv_Completed:
                onFunctionChose.setOnFunctionChose(3, v);
                break;
            case R.id.tv_CancelOrder:
                onFunctionChose.setOnFunctionChose(2, v);
                break;
        }
    }


    public class ViewHolder {
        private CircleImageView cimg_Avatar_MyHelper;
        private TextView tv_Massage_MyHelper;
        private TextView tv_Reward_MyHelper;
        private TextView tv_HelperState_MyHelper;
        private TextView tv_HelperTime_MyHelper;
        private TextView tv_Rights;
        private TextView tv_CancelOrder;
        private TextView tv_Completed;
        private LinearLayout ll_FunctionChose;
    }

    /**
     * 点击回调接口
     */
    public interface OnFunctionChose {
        void setOnFunctionChose(int which, View v);
    }
}
