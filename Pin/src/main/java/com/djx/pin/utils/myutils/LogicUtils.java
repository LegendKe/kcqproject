package com.djx.pin.utils.myutils;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;

import com.djx.pin.R;
import com.djx.pin.activity.IdentityActivity;
import com.djx.pin.beans.UserInfo;
import com.djx.pin.ui.CommonDialog;
import com.djx.pin.utils.LogUtil;
import com.djx.pin.utils.ToastUtil;

/**
 * Created by Administrator on 2016/10/11 0011.
 */
public class LogicUtils {


    /**
     * 实名认证判断
     *
     * @param context
     * @param afterPassedListener
     */
    public static void realNameVerify(final Context context, AfterPassedListener afterPassedListener) {

        int is_auth = UserInfo.getIsAuth(context);
        String real_name = UserInfo.getRealName(context);
        String id_card = UserInfo.getIdCard(context);

        if (is_auth == 1) {//审核通过
            afterPassedListener.realNameVerifyPassed();

        } else if (is_auth == 0 && real_name != null && real_name.length() > 0 && id_card != null && id_card.length() >= 0) {//待审核
            ToastUtil.shortshow(context, "您已提交实名认证信息请耐心等待");
            return;
        } else {
            CommonDialog.show(context, "确定", "取消", context.getString(R.string.dialog_realname_auth_now), new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, IdentityActivity.class);
                    context.startActivity(intent);
                }
            });
        }
    }

    /**
     * 实名认证未通过则进入到认证页面,不需要弹窗
     *
     * @param context
     * @param afterPassedListener
     */
    public static void realNameVerify2(final Context context, AfterPassedListener afterPassedListener) {

        int is_auth = UserInfo.getIsAuth(context);
        String real_name = UserInfo.getRealName(context);
        String id_card = UserInfo.getIdCard(context);

        if (is_auth == 1) {//审核通过
            afterPassedListener.realNameVerifyPassed();

        } else if (is_auth == 0 && real_name != null && real_name.length() > 0 && id_card != null && id_card.length() >= 0) {//待审核
            ToastUtil.shortshow(context, "您已提交实名认证信息请耐心等待");
            return;
        } else {
            Intent intent = new Intent(context, IdentityActivity.class);
            context.startActivity(intent);
        }
    }

    public interface AfterPassedListener {
        void realNameVerifyPassed();
    }


    /*public static void orderLogicJudge(int status, int receiverNum,ZeroReceiverListener zeroReceiverListener,OneMoreReceiverListener oneMoreReceiverListener) {
        // 订单状态，对于content_type=1，0-发布未支付，1-发单成功（进行中），2-订单结束（停止招募）；
        // 对于content_type=5，0-发布，1-我安全了，2-确认完成，3-拒绝完成，4-申诉判定完成；
        // 对于content_type=7，0-发布未支付，1-发单成功，2-订单结束（停止招募）


        switch (status) {
            *//**订单状态在 1-发单成功（进行中）*//*
            case 1:
                *//**订单签单人数为0*//*
                if (receiverNum == 0) {
                    *//*img_TaskState_MHDA.setImageResource(R.mipmap.ic_nonedotask);
                    tv_TaskDo_MHDA.setText("已有人选" + "0" + "/" + info.receiver_limit);
                    tv_TaskComplete_MHDA.setText("已经完成" + "0" + "/" + info.receiver_limit);*//*

                }
                *//**订单签单人数为大于0*//*
                if (receiverNum > 0) {
                    tv_TaskDo_MHDA.setText("已有人选" + info.receiver_num + "/" + info.receiver_limit);
                    int compelete_num = 0;//完成人数
                    *//**已完成的人数计算:3-发单者确认完成和5-申诉判定完成算入其中,其他忽略*//*
                    for (int i = 0; i < info.receiverList.list.size(); i++) {
                        if (3 == info.receiverList.list.get(i).status || 5 == info.receiverList.list.get(i).status) {
                            compelete_num = compelete_num + 1;
                        }
                    }
                    tv_TaskComplete_MHDA.setText("已经完成" + compelete_num + "/" + info.receiver_limit);

                    *//**完成订单人数等于0表示无人完成,不进入完成状态,大于0是才进入完成状态*//*
                    if (compelete_num == 0) {
                        img_TaskState_MHDA.setImageResource(R.mipmap.ic_alldotask);
                    }
                    if (compelete_num > 0) {
                        img_TaskState_MHDA.setImageResource(R.mipmap.ic_completetask);
                    }
                }

                break;
            *//**订单状态在 2-订单结束（停止招募）*//*
            case 2:
                *//**订单签单人数为0*//*
                if (receiverNum == 0) {
                    img_TaskState_MHDA.setImageResource(R.mipmap.ic_nonedotask);
                    tv_TaskDo_MHDA.setText("已有人选0");
                    tv_TaskComplete_MHDA.setText("已经完成0");
                }

                *//**订单签单人数为大于0*//*
                if (receiverNum > 0) {
                    int doing_num = 0;//进行中的人数,接单者对应的状态为:0-已抢单，1-签单成功（进行服务），2-标记完成（待收款），3-发单者确认完成，4-发单者拒绝完成（发单者申诉），5-申诉判定完成
                    *//**进行中的人数计算:0-已抢单，1-签单成功（进行服务），2-标记完成（待收款），3-发单者确认完成，4-发单者拒绝完成（发单者申诉），5-申诉判定完成*//*
                    for (int j = 0; j < info.receiverList.list.size(); j++) {
                        if (1 == info.receiverList.list.get(j).status || 2 == info.receiverList.list.get(j).status || 3 == info.receiverList.list.get(j).status || 4 == info.receiverList.list.get(j).status || 5 == info.receiverList.list.get(j).status) {
                            doing_num = doing_num + 1;
                        }
                    }
                    tv_TaskDo_MHDA.setText("已有人选" + doing_num + "/" + info.receiver_limit);

                    int compelete_num = 0;//已完成的人数:接单者对应的状态为:3-发单者确认完成和5-申诉判定完成算入其中
                    *//**已完成的人数计算:3-发单者确认完成和5-申诉判定完成算入其中,其他忽略*//*
                    for (int i = 0; i < info.receiverList.list.size(); i++) {
                        if (3 == info.receiverList.list.get(i).status || 5 == info.receiverList.list.get(i).status) {
                            compelete_num = compelete_num + 1;
                        }
                    }
                    tv_TaskComplete_MHDA.setText("已经完成" + compelete_num + "/" + info.receiver_limit);

                    *//**完成订单人数等于0表示无人完成,不进入完成状态,大于0是才进入完成状态*//*
                    if (compelete_num == 0) {
                        img_TaskState_MHDA.setImageResource(R.mipmap.ic_alldotask);
                    }
                    if (compelete_num > 0) {
                        img_TaskState_MHDA.setImageResource(R.mipmap.ic_completetask);
                    }

                }
                break;


        }


    }*/


    public interface ZeroReceiverListener {
        void zeroReceiver();
    }
    public interface OneMoreReceiverListener {
        void oneMoreReceiver();
    }


    /**
     *
     * @param totalStatus //订单状态，0-发布未支付，1-发单成功，2-订单结束（停止招募）
     * @param orderStatus ////状态，0-已抢单（已回答），1-已打赏，2-拒绝打赏
     */
    public static void orderStatusLogic(int totalStatus,int orderStatus,int receiver_limit,int rewardedNum,OrderClosedListener orderClosedListener,Order2BVerifyListener order2BVerifyListener,OrderNotPassListener notPassListener,OrderRewardedListener rewardedListener){

        switch (orderStatus) {//状态，0-已抢单（已回答），1-已打赏，2-拒绝打赏
            case 0://已抢单
                if (totalStatus == 2) {//订单状态，0-发布未支付，1-发单成功，2-订单结束（停止招募）
                    orderClosedListener.orderClosed();
                } else {
                    if (rewardedNum > 0 && rewardedNum >= receiver_limit) {//已打赏人已满
                        orderClosedListener.orderClosed();
                    } else {//等待被审核
                        order2BVerifyListener.order2BVerify();
                    }
                }
                break;
            case 1://已打赏
                rewardedListener.orderRewarded();
                break;
            case 2:
                if (totalStatus == 2) {//订单已关闭
                    orderClosedListener.orderClosed();
                }else {//审核未通过
                    notPassListener.orderNotPass();
                }
                break;
        }

    }

    /**
     * 订单关闭
     */
    public interface OrderClosedListener {
        void orderClosed();
    }

    /**
     * 订单待审核
     */
    public interface Order2BVerifyListener {
        void order2BVerify();
    }
    /**
     * 审核未通过
     */
    public interface OrderNotPassListener {
        void orderNotPass();
    }
    /**
     * 已得赏
     */
    public interface OrderRewardedListener {
        void orderRewarded();
    }
}
