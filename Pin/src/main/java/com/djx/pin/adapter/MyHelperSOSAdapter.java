package com.djx.pin.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.djx.pin.R;
import com.djx.pin.beans.MyHelperInfo;
import com.djx.pin.beans.SOSInfo;
import com.djx.pin.beans.StaticBean;
import com.djx.pin.myview.CircleImageView;
import com.djx.pin.utils.DateUtils;
import com.djx.pin.utils.LogUtil;
import com.djx.pin.utils.StringUtils;

import java.io.UnsupportedEncodingException;
import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Formatter;
import java.util.Locale;

/**
 * Created by Administrator on 2016/6/29.
 */
public class MyHelperSOSAdapter extends MyBaseAdapter<SOSInfo.LIST> {


    public MyHelperSOSAdapter(Context context) {
        super(context);
    }


    @Override
    public void addData(SOSInfo.LIST info) {
        boolean isSame=false;
        for(int i=0;i<list.size();i++){
            if(info.start_time==list.get(i).start_time){
                isSame=true;
            }
        }
        if(!isSame){
            list.add(info);
        }
    }

    @Override
    public View getView(int position, View converView, ViewGroup parent) {
        ViewHolder vh;
        if (converView == null) {
            vh = new ViewHolder();
            converView = inflater.inflate(R.layout.lv_intem_myhelper, null);
            vh.cimg_Avatar_MyHelper = (CircleImageView) converView.findViewById(R.id.cimg_Avatar_MyHelper);
            vh.tv_Massage_MyHelper = (TextView) converView.findViewById(R.id.tv_Massage_MyHelper);
            vh.tv_Reward_MyHelper = (TextView) converView.findViewById(R.id.tv_Reward_MyHelper);
            vh.tv_HelperTime_MyHelper = (TextView) converView.findViewById(R.id.tv_HelperTime_MyHelper);

            converView.setTag(vh);
        } else {
            vh = (ViewHolder) converView.getTag();
        }
        SOSInfo.LIST info = getItem(position);
        try {
            getOneImageViewUrl(vh.cimg_Avatar_MyHelper,info.portrait,1);
        } catch (UnsupportedEncodingException e) {
            LogUtil.e("MyHelperSOSAdapter,enter catch 头像加载失败");
            e.printStackTrace();
        }
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(info.start_time);
        Formatter ft = new Formatter(Locale.CHINA);
        vh.tv_HelperTime_MyHelper.setText(StringUtils.friendly_time(ft.format("%1$tY-%1$tm-%1$td %1$tT", cal).toString()));
        vh.tv_Massage_MyHelper.setText(StaticBean.SOSDescription);
        DecimalFormat format = new DecimalFormat("0.00");
        vh.tv_Reward_MyHelper.setText("赏" + format.format(info.price)+ "元");
        return converView;
    }

    public class ViewHolder {
        private CircleImageView cimg_Avatar_MyHelper;
        private TextView tv_Massage_MyHelper;
        private TextView tv_Reward_MyHelper;
        private TextView tv_HelperTime_MyHelper;
    }

}
