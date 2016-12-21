package com.djx.pin.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.djx.pin.R;
import com.djx.pin.beans.TaskDetailInfo;
import com.djx.pin.myview.CircleImageView;

/**
 * Created by Administrator on 2016/6/29.
 */
public class TLA_TaskDetailAdapter extends MyBaseAdapter<TaskDetailInfo> {
    public TLA_TaskDetailAdapter(Context context) {
        super(context);
    }

    @Override
    public int getItemViewType(int position) {
        TaskDetailInfo info=getItem(position);
        int type;
        if (info.getTaskState().equals("已完成")) {
            type = 1;
        } else {
            type = 2;
        }
        return type;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    TaskDetailInfo info;
    ViewHolder vh;

    @Override
    public View getView(int position, View converView, ViewGroup parent) {
        int type = getItemViewType(position);
        if (converView == null) {
            switch (type) {
                case 1:
                    vh = new ViewHolder();
                    converView = inflater.inflate(R.layout.lv_intem_taskdetail1, null);
                    vh.cimg_Avatar_TaskDetail = (CircleImageView) converView.findViewById(R.id.cimg_Avatar_TaskDetail);
                    vh.tv_Massage_TaskDetail = (TextView) converView.findViewById(R.id.tv_Massage_TaskDetail);
                    vh.tv_Location_TaskDetail = (TextView) converView.findViewById(R.id.tv_Location_TaskDetail);
                    vh.tv_TaskState_TaskDetail = (TextView) converView.findViewById(R.id.tv_TaskState_TaskDetail);
                    vh.tv_TaskTime_TaskDetail = (TextView) converView.findViewById(R.id.tv_TaskTime_TaskDetail);
                    converView.setTag(vh);
                    break;

                case 2:
                    vh = new ViewHolder();
                    converView = inflater.inflate(R.layout.lv_intem_taskdetail2, null);
                    vh.cimg_Avatar_TaskDetail = (CircleImageView) converView.findViewById(R.id.cimg_Avatar_TaskDetail);
                    vh.tv_Massage_TaskDetail = (TextView) converView.findViewById(R.id.tv_Massage_TaskDetail);
                    vh.tv_Location_TaskDetail = (TextView) converView.findViewById(R.id.tv_Location_TaskDetail);
                    vh.tv_TaskState_TaskDetail = (TextView) converView.findViewById(R.id.tv_TaskState_TaskDetail);
                    vh.tv_TaskTime_TaskDetail = (TextView) converView.findViewById(R.id.tv_TaskTime_TaskDetail);
                    converView.setTag(vh);
                    break;
            }
        } else {
            switch (type) {
                case 1:
                    vh = (ViewHolder) converView.getTag();
                    break;
                case 2:
                    vh = (ViewHolder) converView.getTag();
                    break;
            }
        }

        info = getItem(position);

        vh.cimg_Avatar_TaskDetail.setImageResource(info.getAvatarId());
        vh.tv_Location_TaskDetail.setText(info.getLocation());
        vh.tv_Massage_TaskDetail.setText(info.getMassage());
        vh.tv_TaskState_TaskDetail.setText(info.getTaskState());
        vh.tv_TaskTime_TaskDetail.setText(info.getTaskTime());
        return converView;
    }

    public class ViewHolder {
        private CircleImageView cimg_Avatar_TaskDetail;
        private TextView tv_Massage_TaskDetail;
        private TextView tv_Location_TaskDetail;
        private TextView tv_TaskState_TaskDetail;
        private TextView tv_TaskTime_TaskDetail;
    }
}
