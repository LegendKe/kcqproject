package com.djx.pin.adapter;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.djx.pin.R;
import com.djx.pin.activity.MyHelperOffLineDetailActivity;
import com.djx.pin.beans.MyHelperListInfo;
import com.djx.pin.myview.CircleImageView;
import com.djx.pin.utils.QiniuUtils;
import com.djx.pin.utils.StringUtils;

import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.Formatter;
import java.util.Locale;

import cz.msebera.android.httpclient.util.TextUtils;

/**
 * Created by Administrator on 2016/6/29.
 */
public class MyHelperOfflineRewardAdapter extends MyBaseAdapter<MyHelperListInfo.LIST> {
    protected final static String TAG = MyHelperOffLineDetailActivity.class.getSimpleName();

    public MyHelperOfflineRewardAdapter(Context context) {
        super(context);
    }


    @Override
    public void addData(MyHelperListInfo.LIST info) {
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
            vh.tv_pay= (TextView) converView.findViewById(R.id.tv_pay);

            converView.setTag(vh);
        } else {
            vh = (ViewHolder) converView.getTag();
        }
        MyHelperListInfo.LIST info = getItem(position);
        if(0==info.status){
            vh.tv_pay.setVisibility(View.VISIBLE);
        }else {
            vh.tv_pay.setVisibility(View.GONE);
        }
        if(TextUtils.isEmpty(info.portrait)){
            vh.cimg_Avatar_MyHelper.setImageResource(R.mipmap.ic_defaultcontact);
        }else {
            QiniuUtils.setAvatarByIdFrom7Niu(context,vh.cimg_Avatar_MyHelper,info.portrait);
        }

        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(info.create_time);
        Formatter ft = new Formatter(Locale.CHINA);
        String time = ft.format("%1$tY-%1$tm-%1$td %1$tT", cal).toString();
        Log.i(TAG, "time is " + time);
        vh.tv_HelperTime_MyHelper.setText(StringUtils.friendly_time(time));
        vh.tv_Massage_MyHelper.setText(info.description);
        DecimalFormat format = new DecimalFormat("0.00");
        vh.tv_Reward_MyHelper.setText("赏" + format.format(info.price)+ "元");
        return converView;
    }


    public class ViewHolder {
        private CircleImageView cimg_Avatar_MyHelper;
        private TextView tv_Massage_MyHelper;
        private TextView tv_Reward_MyHelper;
        private TextView tv_HelperTime_MyHelper;
        private TextView tv_pay;
    }

}
