package com.djx.pin.adapter;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.load.engine.Resource;
import com.djx.pin.R;
import com.djx.pin.beans.PurseLogItemBean;
import com.djx.pin.beans.WithdrawLogItemBean;
import com.djx.pin.myview.CircleImageView;
import com.djx.pin.utils.QiniuUtils;

import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.Formatter;
import java.util.Locale;

/**
 * Created by Administrator on 2016/6/29.
 */
public class LookWithdrawLogAdapter extends MyBaseAdapter<WithdrawLogItemBean> {

    public LookWithdrawLogAdapter(Context context) {
        super(context);
    }

    @Override
    public View getView(int position, View converView, ViewGroup parent) {
        ViewHolder vh;
        if (converView == null) {
            vh = new ViewHolder();
            converView = inflater.inflate(R.layout.lv_intem_lookpursedetail, null);
            vh.cimg_Avatar_LPDA = (CircleImageView) converView.findViewById(R.id.cimg_Avatar_LPDA);
            vh.tv_Massage_LPDA = (TextView) converView.findViewById(R.id.tv_Massage_LPDA);
            vh.tv_GotReward_LPDA = (TextView) converView.findViewById(R.id.tv_GotReward_LPDA);
            vh.tv_Time_LPDA = (TextView) converView.findViewById(R.id.tv_Time_LPDA);
            converView.setTag(vh);
        } else {
            vh = (ViewHolder) converView.getTag();
        }
        WithdrawLogItemBean info = getItem(position);
//        vh.cimg_Avatar_LPDA.setImageResource(info.getPortrait());
        QiniuUtils.setAvatarByIdFrom7Niu(context,vh.cimg_Avatar_LPDA, info.getPortrait());

        DecimalFormat format = new DecimalFormat("0.00");
        vh.tv_GotReward_LPDA.setText(format.format(info.getCount()));

        vh.tv_Massage_LPDA.setText(getCheckedString(info.getChecked()));
        if (info.getChecked() == 0) {
            vh.tv_Massage_LPDA.setTextColor(context.getResources().getColor(R.color.withdraw_waiting));
        } else if (info.getChecked() == 1){
            vh.tv_Massage_LPDA.setTextColor(context.getResources().getColor(R.color.withdraw_pass));
        } else if (info.getChecked() == 2){
            vh.tv_Massage_LPDA.setTextColor(context.getResources().getColor(R.color.withdraw_notpass));
        } else if (info.getChecked() == 3){
            vh.tv_Massage_LPDA.setTextColor(context.getResources().getColor(R.color.withdraw_ok));
        }

        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(info.getCreate_time());
        Formatter ft = new Formatter(Locale.CHINA);
        vh.tv_Time_LPDA.setText(ft.format("%1$tY-%1$tm-%1$td %1$tT", cal).toString());
        return converView;
    }

    public String getCheckedString(int checked) {
        String ret;
        switch (checked) {
            case 0:
                ret = "提现处理中";
                break;
            case 1:
                ret = "审核已通过";
                break;
            case 2:
                ret = "审核未通过";
                break;
            case 3:
                ret = "提现成功";
                break;
            default:
                ret = "";
                break;
        }
        return ret;
    }

    public class ViewHolder {
        private CircleImageView cimg_Avatar_LPDA;
        private TextView tv_Massage_LPDA;
        private TextView tv_GotReward_LPDA;
        private TextView tv_Time_LPDA;
    }
}
