package com.djx.pin.adapter;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.djx.pin.R;
import com.djx.pin.beans.MyHelperOfflineDetailInfo;
import com.djx.pin.myview.CircleImageView;
import com.djx.pin.utils.QiniuUtils;

import cz.msebera.android.httpclient.util.TextUtils;

/**
 * Created by Administrator on 2016/7/21 0021.
 */
public class MyHelperDetailAdapter extends MyBaseAdapter<MyHelperOfflineDetailInfo.LIST> implements View.OnClickListener {
    protected final static String TAG = MyHelperDetailAdapter.class.getSimpleName();

    private boolean isOrderEnd = false;//订单是否已经结束
    private SetListener setListener;

    public MyHelperDetailAdapter(Context context, SetListener setListener) {
        super(context);
        this.setListener = setListener;
    }
    public void setOrderState(boolean orderState){
        this.isOrderEnd = orderState;
    }
    @Override
    public View getView(int position, View converView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if (converView == null) {
            viewHolder = new ViewHolder();
            converView = inflater.inflate(R.layout.lv_intem_myhelperdetail, null);
            viewHolder.cimg_Avatar_MHDA = (CircleImageView) converView.findViewById(R.id.cimg_Avatar_MHDA);
            viewHolder.tv_NickName_MHDA = (TextView) converView.findViewById(R.id.tv_NickName_MHDA);
            viewHolder.tv_Location_MHDA = (TextView) converView.findViewById(R.id.tv_Location_MHDA);
            viewHolder.tv_TaskState_MHDA = (TextView) converView.findViewById(R.id.tv_TaskState_MHDA);
            viewHolder.tv_TaskFunction_MHDA = (TextView) converView.findViewById(R.id.tv_TaskFunction_MHDA);
            viewHolder.bt_TaskSource_MHDA = (Button) converView.findViewById(R.id.bt_TaskSource_MHDA);
            viewHolder.tv_shensu = (TextView) converView.findViewById(R.id.tv_shensu);
            converView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) converView.getTag();
        }

        MyHelperOfflineDetailInfo.LIST info = list.get(position);

        if(TextUtils.isEmpty(info.portrait)){
            viewHolder.cimg_Avatar_MHDA.setImageResource(R.mipmap.ic_defaultcontact);
        }else {
            QiniuUtils.setAvatarByIdFrom7Niu(context,viewHolder.cimg_Avatar_MHDA,info.portrait);
        }
        viewHolder.tv_NickName_MHDA.setText(info.nickname);
        viewHolder.tv_Location_MHDA.setText(info.province + info.city + info.district);
        switch (info.status) {
            /**0-已抢单*/
            case 0:

                if(isOrderEnd){//订单结束
                   /* viewHolder.tv_TaskFunction_MHDA.setTextColor(Color.GRAY);
                    Drawable drawable = context.getResources().getDrawable(R.drawable.shape_bt_gray);
                    viewHolder.tv_TaskFunction_MHDA.setBackgroundDrawable(drawable);*/
                    viewHolder.tv_TaskFunction_MHDA.setVisibility(View.GONE);
                    viewHolder.tv_TaskState_MHDA.setTextColor(Color.GRAY);
                    viewHolder.tv_TaskState_MHDA.setText(R.string.order_closed);
                }else{//订单未结束
                    if(confirmedNum > 0 && confirmedNum >= receiver_limit){
                        //人数已满
                        viewHolder.tv_TaskState_MHDA.setText(R.string.order_rewarded_number_fulled);
                        viewHolder.tv_TaskFunction_MHDA.setVisibility(View.GONE);
                        /*viewHolder.tv_TaskFunction_MHDA.setTextColor(Color.GRAY);
                        Drawable drawable = context.getResources().getDrawable(R.drawable.shape_bt_gray);
                        viewHolder.tv_TaskFunction_MHDA.setBackgroundDrawable(drawable);*/
                    }else{
                        //未满
                        viewHolder.tv_TaskState_MHDA.setText(R.string.tv_receiver_state_0);
                        viewHolder.tv_TaskFunction_MHDA.setText(R.string.tv_distribute_state_0);
                    }
                }
                break;
            /**1-签单成功（进行服务）*/
            case 1:
                viewHolder.tv_TaskState_MHDA.setText(R.string.tv_receiver_state_1);
                viewHolder.tv_TaskFunction_MHDA.setVisibility(View.GONE);
                break;
            /**2-标记完成（待收款）*/
            case 2:
                viewHolder.tv_TaskState_MHDA.setText(R.string.tv_receiver_state_2);
                viewHolder.tv_TaskFunction_MHDA.setText(R.string.tv_distribute_state_2);
                break;
            /**3-发单者确认完成*/
            case 3:
                viewHolder.tv_TaskState_MHDA.setText(R.string.tv_receiver_state_3);
                //viewHolder.tv_TaskFunction_MHDA.setText("已付款，是否已评价？");
                viewHolder.tv_TaskFunction_MHDA.setVisibility(View.GONE);
                break;
            /**4-发单者拒绝完成（发单者申诉）*/
            case 4:
                viewHolder.tv_TaskState_MHDA.setText(R.string.tv_receiver_state_4);
                viewHolder.tv_TaskFunction_MHDA.setVisibility(View.INVISIBLE);
                viewHolder.tv_shensu.setText(R.string.tv_distribute_state_3);
                viewHolder.tv_shensu.setVisibility(View.VISIBLE);
                viewHolder.tv_shensu.setOnClickListener(this);
                break;
            /**5-申诉判定完成*/
            case 5:
                viewHolder.tv_TaskState_MHDA.setText(R.string.tv_receiver_state_5);
                viewHolder.tv_TaskFunction_MHDA.setVisibility(View.INVISIBLE);
                viewHolder.tv_shensu.setVisibility(View.VISIBLE);
                viewHolder.tv_shensu.setTextColor(Color.GRAY);
                viewHolder.tv_shensu.setOnClickListener(this);
               /* viewHolder.tv_TaskFunction_MHDA.setText(R.string.tv_distribute_state_4);
                viewHolder.tv_TaskFunction_MHDA.setTextColor(Color.GRAY);*/
                break;
            /**6-抢单被拒绝*/
            case 6:

                if(isOrderEnd){//订单结束
                   /* viewHolder.tv_TaskFunction_MHDA.setTextColor(Color.GRAY);
                    Drawable drawable = context.getResources().getDrawable(R.drawable.shape_bt_gray);
                    viewHolder.tv_TaskFunction_MHDA.setBackgroundDrawable(drawable);*/
                    viewHolder.tv_TaskState_MHDA.setTextColor(Color.GRAY);
                    viewHolder.tv_TaskState_MHDA.setText(R.string.order_closed);
                }else{//订单未结束
                    if(confirmedNum > 0 && confirmedNum >= receiver_limit){
                        //人数已满
                        viewHolder.tv_TaskState_MHDA.setText(R.string.order_rewarded_number_fulled);

                       /* viewHolder.tv_TaskFunction_MHDA.setTextColor(Color.GRAY);
                        Drawable drawable = context.getResources().getDrawable(R.drawable.shape_bt_gray);
                        viewHolder.tv_TaskFunction_MHDA.setBackgroundDrawable(drawable);*/
                    }else{
                        //未满
                        viewHolder.tv_TaskState_MHDA.setText(R.string.tv_receiver_state_6);
                    }
                }
                viewHolder.tv_TaskFunction_MHDA.setVisibility(View.GONE);
                break;
            /**7-接单者放弃*/
            case 7:
                viewHolder.tv_TaskState_MHDA.setText(R.string.tv_receiver_state_7);
                viewHolder.tv_TaskFunction_MHDA.setVisibility(View.GONE);
                if(isOrderEnd){
                    //viewHolder.tv_TaskFunction_MHDA.setTextColor(Color.GRAY);
                    viewHolder.tv_TaskFunction_MHDA.setVisibility(View.GONE);

                    viewHolder.tv_TaskState_MHDA.setTextColor(Color.GRAY);
                    viewHolder.tv_TaskState_MHDA.setText(R.string.order_closed);
                }
                break;
        }

        viewHolder.tv_TaskFunction_MHDA.setTag(position);
        viewHolder.tv_TaskFunction_MHDA.setOnClickListener(this);
        viewHolder.bt_TaskSource_MHDA.setTag(position);
        viewHolder.bt_TaskSource_MHDA.setOnClickListener(this);
        viewHolder.cimg_Avatar_MHDA.setTag(position);
        viewHolder.cimg_Avatar_MHDA.setOnClickListener(this);

        viewHolder.tv_shensu.setTag(position);
        return converView;
    }

    /**
     * Called when a view has been clicked.
     *
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_TaskFunction_MHDA:
                setListener.clickListener(1, v);
                break;
            case R.id.bt_TaskSource_MHDA:
                setListener.clickListener(2, v);
                break;
            case R.id.tv_shensu://申诉
                setListener.clickListener(3, v);
                break;
            case R.id.cimg_Avatar_MHDA:
                Log.i(TAG, "click avater");
                setListener.clickListener(4, v);
                break;
        }

    }

    int confirmedNum;
    int receiver_limit;

    /**
     *
     * @param confirmedNum 确定人数
     * @param receiver_limit 接收人限
     */
    public void setConfirmed2LimitNum(int confirmedNum, int receiver_limit) {
        this.confirmedNum = confirmedNum;
        this.receiver_limit = receiver_limit;
    }



    public interface SetListener {
        void clickListener(int tag, View v);
    }


    public class ViewHolder {
        CircleImageView cimg_Avatar_MHDA;
        TextView tv_NickName_MHDA, tv_Location_MHDA, tv_TaskState_MHDA, tv_TaskFunction_MHDA,tv_shensu;
        Button bt_TaskSource_MHDA;
    }
}
