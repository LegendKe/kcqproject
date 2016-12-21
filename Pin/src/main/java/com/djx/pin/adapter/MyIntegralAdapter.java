package com.djx.pin.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.djx.pin.R;
import com.djx.pin.beans.MyPointDetailBean;
import com.djx.pin.beans.MyPointItemBean;
import com.djx.pin.myview.CircleImageView;
import com.djx.pin.utils.LogUtil;
import com.djx.pin.utils.QiniuUtils;
import com.djx.pin.utils.StringUtils;

import java.io.UnsupportedEncodingException;
import java.util.Calendar;
import java.util.Formatter;
import java.util.Locale;

/**
 * Created by Administrator on 2016/6/29.
 */
public class MyIntegralAdapter extends MyBaseAdapter<MyPointItemBean> {

    public MyIntegralAdapter(Context context) {
        super(context);
    }

    @Override
    public View getView(int position, View converView, ViewGroup parent) {
        ViewHolder vh;
        if(converView==null){
            vh=new ViewHolder();
            converView=inflater.inflate(R.layout.lv_intem_myintegral,null);
            vh.cimg_Avatar_MWA= (CircleImageView) converView.findViewById(R.id.cimg_Avatar_MWA);
            vh.tv_Things_MWA= (TextView) converView.findViewById(R.id.tv_Things_MWA);
            vh.tv_GotWithdraw_MWA= (TextView) converView.findViewById(R.id.tv_GotWithdraw_MWA);
            vh.tv_Reason_MWA= (TextView) converView.findViewById(R.id.tv_Reason_MWA);
            vh.tv_Time_MWA= (TextView) converView.findViewById(R.id.tv_Time_MWA);
            converView.setTag(vh);
        }else {
            vh= (ViewHolder) converView.getTag();
        }

        MyPointItemBean info =getItem(position);
        QiniuUtils.setAvatarByIdFrom7Niu(context,vh.cimg_Avatar_MWA,info.getPortrait());
        if(info.getPoint() > 0) {
            vh.tv_GotWithdraw_MWA.setText("积分+" + info.getPoint());
        } else {
            vh.tv_GotWithdraw_MWA.setText("积分" + info.getPoint());
        }
        vh.tv_Things_MWA.setText(getTargetTypeString(info.getTarget_type()));
        vh.tv_Time_MWA.setText("2小时前");
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(info.getCreate_time());
        Formatter ft = new Formatter(Locale.CHINA);
        vh.tv_Time_MWA.setText(StringUtils.friendly_time(ft.format("%1$tY-%1$tm-%1$td %1$tT", cal).toString()));
        vh.tv_Reason_MWA.setText(getTypeString(info.getType()));
        return converView;
    }

    public class ViewHolder{
        private CircleImageView cimg_Avatar_MWA;
        private TextView tv_Things_MWA;
        private TextView tv_GotWithdraw_MWA;
        private TextView tv_Reason_MWA;
        private TextView tv_Time_MWA;
    }

    public String getTargetTypeString(int targetType)
    {
        String ret;
        switch (targetType) {
            case 1:
                ret = "线下悬赏";
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
                ret = "未知";
                break;
        }
        return ret;
    }

    public String getTypeString(int type)
    {
        String ret;
        switch (type) {
            case 1:
                ret = "每日登录";
                break;
            case 2:
                ret = "发布内容";
                break;
            case 3:
                ret = "分享内容";
                break;
            case 4:
                ret = "PIN";
                break;
            case 5:
                ret = "接单";
                break;
            case 6:
                ret = "吐槽";
                break;
            default:
                ret = "未知";
                break;
        }
        return ret;
    }
}
