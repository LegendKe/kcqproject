package com.djx.pin.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.djx.pin.R;
import com.djx.pin.beans.MyHelperSOSDetailInfo;
import com.djx.pin.myview.CircleImageView;
import com.djx.pin.utils.DateUtils;
import com.djx.pin.utils.LogUtil;

import java.io.UnsupportedEncodingException;
import java.util.Date;

/**
 * Created by Administrator on 2016/7/21 0021.
 */
public class RewardSOSDetailAdapter extends MyBaseAdapter<MyHelperSOSDetailInfo.LIST> implements View.OnClickListener {
    SetListener setListener;

    public RewardSOSDetailAdapter(Context context) {
        super(context);
        this.setListener = setListener;
    }
    @Override
    public View getView(int position, View converView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if (converView == null) {
            viewHolder = new ViewHolder();
            converView = inflater.inflate(R.layout.lv_item_rewardsosdetail, null);
            viewHolder.cimg_Avatar_MHDA = (CircleImageView) converView.findViewById(R.id.cimg_Avatar_MHDA);
            viewHolder.tv_NickName_MHDA = (TextView) converView.findViewById(R.id.tv_NickName_MHDA);
            viewHolder.tv_Location_MHDA = (TextView) converView.findViewById(R.id.tv_Location_MHDA);
            viewHolder.tv_time = (TextView) converView.findViewById(R.id.tv_time);
            converView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) converView.getTag();
        }

        MyHelperSOSDetailInfo.LIST info = list.get(position);

        try {
            getOneImageViewUrl(viewHolder.cimg_Avatar_MHDA, info.portrait, 1);
        } catch (UnsupportedEncodingException e) {
            LogUtil.e("MyHelperDetailAdapter 头像加载失败");
            e.printStackTrace();
        }

        viewHolder.tv_NickName_MHDA.setText(info.nickname);
        viewHolder.tv_Location_MHDA.setText(info.location);
        viewHolder.tv_time.setText(DateUtils.formatDate(new Date(info.start_time),DateUtils.yyyyMMDD));
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
        }

    }

    public interface SetListener {
        void clickListener(int tag, View v);
    }

    public class ViewHolder {
        CircleImageView cimg_Avatar_MHDA;
        TextView tv_NickName_MHDA,
                tv_Location_MHDA,
                tv_time;
    }
}
