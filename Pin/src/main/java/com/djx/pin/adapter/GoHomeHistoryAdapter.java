package com.djx.pin.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.djx.pin.R;
import com.djx.pin.beans.GoHomeHistoryInfo;
import com.djx.pin.utils.DateUtils;

import java.util.Date;

/**
 * Created by Administrator on 2016/6/23.
 */
public class GoHomeHistoryAdapter extends MyBaseAdapter<GoHomeHistoryInfo.ListEle> {
    public GoHomeHistoryAdapter(Context context) {
        super(context);
    }

    @Override
    public View getView(int position, View converView, ViewGroup parent) {
        ViewHolder vh;
        if (converView==null){
            vh=new ViewHolder();
            converView=inflater.inflate(R.layout.lv_item_gohomehistory,null);
            vh.tv_ReportLocation= (TextView) converView.findViewById(R.id.tv_ReportLocation);
            vh.tv_ReportTime= (TextView) converView.findViewById(R.id.tv_ReportTime);
            vh.tv_ReportState= (TextView) converView.findViewById(R.id.tv_ReportState);
            converView.setTag(vh);
        }else {
            vh= (ViewHolder) converView.getTag();
        }
        GoHomeHistoryInfo.ListEle info=getItem(position);
        //举报地点
        vh.tv_ReportLocation.setText(info.location);
        //举报时间
        Date date=new Date(info.event_time);
        vh.tv_ReportTime.setText(DateUtils.formatDate(date,DateUtils.yyyyMMDD));
        //审核状态
        switch (info.checked){
            case 0:
                vh.tv_ReportState.setText(R.string.tv_report_state_0);
                break;
            case 1:
                vh.tv_ReportState.setText(R.string.tv_report_state_1);
                break;
            case 2:
                vh.tv_ReportState.setText(R.string.tv_report_state_2);
                break;
        }
        return converView;
    }

    public class ViewHolder {
        private TextView tv_ReportLocation;
        private TextView tv_ReportTime;
        private TextView tv_ReportState;
    }
}
