package com.djx.pin.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.djx.pin.R;
import com.djx.pin.beans.PurseLogItemBean;
import com.djx.pin.myview.CircleImageView;
import com.djx.pin.utils.LogUtil;
import com.djx.pin.utils.QiniuUtils;

import java.io.UnsupportedEncodingException;
import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.Formatter;
import java.util.Locale;

/**
 * Created by Administrator on 2016/6/29.
 */
public class LookPurseDetailAdapter extends MyBaseAdapter<PurseLogItemBean> {

    public LookPurseDetailAdapter(Context context) {
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
        PurseLogItemBean info = getItem(position);
//        vh.cimg_Avatar_LPDA.setImageResource(info.getPortrait());
        QiniuUtils.setAvatarByIdFrom7Niu(context,vh.cimg_Avatar_LPDA, info.getPortrait());

        vh.tv_GotReward_LPDA.setText(getPriceString(info.getType(), info));

        String msg;
        String targetTypeString = getTargetTypeString(info.getTarget_type());
        String typeString = getTypeString(info.getType());
        if (targetTypeString == null || targetTypeString.length() == 0) {
            msg = typeString;
        } else {
            msg = targetTypeString + " - " + typeString;
        }
        vh.tv_Massage_LPDA.setText(msg);

        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(info.getCreate_time());
        Formatter ft = new Formatter(Locale.CHINA);
        vh.tv_Time_LPDA.setText(ft.format("%1$tY-%1$tm-%1$td %1$tT", cal).toString());
        return converView;
    }

    public String getTargetTypeString(int targetType) {
        String ret;
        switch (targetType) {
            case 1:
                ret = "求助";
                break;
            case 2:
                ret = "文明墙";
                break;
            case 3:
                ret = "回家";
                break;
            case 4:
                ret = "抛异物";
                break;
            case 5:
                ret = "紧急求助";
                break;
            case 7:
                ret = "网络悬赏";
                break;
            default:
                ret = "";
                break;
        }
        return ret;
    }

    public String getTypeString(int type) {
        String ret;
        switch (type) {
            case 1:
                ret = "发布扣款";
                break;
            case 2:
                ret = "退款";
                break;
            case 3:
                ret = "完成奖赏";
                break;
            case 4:
                ret = "分享奖赏";
                break;
            case 5:
                ret = "充值";
                break;
            case 6:
                ret = "提现";
                break;
            case 7:
                ret = "提现退回";
                break;
            default:
                ret = "";
                break;
        }
        return ret;
    }

    public String getPriceString(int type, PurseLogItemBean info) {
        String price;
        DecimalFormat format = new DecimalFormat("0.00");
        switch (type) {
            //发布扣款
            case 1:
                price = "-" + format.format(info.getCount());
                break;
            //退款
            case 2:
                price = "+" + format.format(info.getCount());
                break;
            //完成奖赏
            case 3:
                price = "+" + format.format(info.getCount());
                break;
            //分享奖赏
            case 4:
                price = "+" + format.format(info.getCount());
                break;
            //充值
            case 5:
                price = "+" + format.format(info.getCount());
                break;
            //提现
            case 6:
                price = "-" + format.format(info.getCount());
                break;
            case 7:
                price = "+" + format.format(info.getCount());
                break;
            default:
                price = "未知";
                break;
        }
        return price;
    }

    public class ViewHolder {
        private CircleImageView cimg_Avatar_LPDA;
        private TextView tv_Massage_LPDA;
        private TextView tv_GotReward_LPDA;
        private TextView tv_Time_LPDA;
    }
}
