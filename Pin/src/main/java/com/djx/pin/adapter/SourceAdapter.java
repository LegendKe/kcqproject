package com.djx.pin.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.djx.pin.R;
import com.djx.pin.beans.SourceInfo;
import com.djx.pin.myview.CircleImageView;
import com.djx.pin.utils.DateUtils;
import com.djx.pin.utils.LogUtil;

import java.io.UnsupportedEncodingException;
import java.util.Date;

/**
 * Created by lifel on 2016/8/12.
 */
public class SourceAdapter extends MyBaseAdapter<SourceInfo.LIST> {
    public SourceAdapter(Context context) {
        super(context);
    }

    @Override
    public View getView(int position, View converView, ViewGroup parent) {

        ViewHolder viewHolder;
        if (null == converView) {
            converView = inflater.inflate(R.layout.lv_item_source, null);
            viewHolder = new ViewHolder();
            viewHolder.cimg_Avatar = (CircleImageView) converView.findViewById(R.id.cimg_Avatar);
            viewHolder.tv_NickName = (TextView) converView.findViewById(R.id.tv_NickName);
            viewHolder.tv_position = (TextView) converView.findViewById(R.id.tv_position);
            viewHolder.tv_Time = (TextView) converView.findViewById(R.id.tv_Time);
            viewHolder.tv_role = (TextView) converView.findViewById(R.id.tv_role);
            converView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) converView.getTag();
        }

        SourceInfo.LIST info=list.get(position);
        try {
            getOneImageViewUrl(viewHolder.cimg_Avatar,info.portrait,1);
        } catch (UnsupportedEncodingException e) {
            LogUtil.e("SourceAdapter 头像加载失败");
            e.printStackTrace();
        }
        viewHolder.tv_NickName.setText(info.nickname);
        viewHolder.tv_position.setText(info.province+info.city+info.district);
        viewHolder.tv_Time.setText(DateUtils.formatDate(new Date(info.create_time),DateUtils.yyyyMMDD));
        switch (info.role){
            case 0:
                viewHolder.tv_role.setText("发单");
                break;
            case 1:
                viewHolder.tv_role.setText("接单");
                break;
            case 2:
                viewHolder.tv_role.setText("PING了一下");
                break;
        }
        return converView;
    }

    private class ViewHolder {
        CircleImageView cimg_Avatar;
        TextView tv_NickName;
        TextView tv_position;
        TextView tv_Time;
        TextView tv_role;
    }
}
