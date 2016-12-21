package com.djx.pin.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.djx.pin.R;
import com.djx.pin.beans.MyHelperOfflineDetailInfo;
import com.djx.pin.beans.MyHelperSOSDetailInfo;
import com.djx.pin.myview.CircleImageView;
import com.djx.pin.utils.LogUtil;

import java.io.UnsupportedEncodingException;

/**
 * Created by Administrator on 2016/7/21 0021.
 */
public class MyHelperSOSDetailAdapter extends MyBaseAdapter<MyHelperSOSDetailInfo.LIST> implements View.OnClickListener {
    SetListener setListener;

    public MyHelperSOSDetailAdapter(Context context) {
        super(context);
    }
    @Override
    public View getView(int position, View converView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if (converView == null) {
            viewHolder = new ViewHolder();
            converView = inflater.inflate(R.layout.lv_item_myhelpersosdetail, null);
            viewHolder.cimg_Avatar_MHDA = (CircleImageView) converView.findViewById(R.id.cimg_Avatar_MHDA);
            viewHolder.tv_NickName_MHDA = (TextView) converView.findViewById(R.id.tv_NickName_MHDA);
            viewHolder.tv_Location_MHDA = (TextView) converView.findViewById(R.id.tv_Location_MHDA);
            viewHolder.tv_TaskState_MHDA = (TextView) converView.findViewById(R.id.tv_TaskState_MHDA);
            viewHolder.tv_TaskState_MHDA = (TextView) converView.findViewById(R.id.tv_TaskState_MHDA);
            viewHolder.iv_complete = (ImageView) converView.findViewById(R.id.iv_complete);
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

        /**
         * 状态，0-参与，1-完成等待确认，2-获得赏金，3-完成未获得赏金，4-申诉判定完成，5-中途放弃
         */
        switch (info.status) {
            case 0:
                viewHolder.tv_TaskState_MHDA.setVisibility(View.VISIBLE);
                viewHolder.iv_complete.setVisibility(View.GONE);
                viewHolder.tv_TaskState_MHDA.setText("参与");
                break;
            case 1:
                viewHolder.tv_TaskState_MHDA.setVisibility(View.VISIBLE);
                viewHolder.iv_complete.setVisibility(View.GONE);
                viewHolder.tv_TaskState_MHDA.setText("完成等赏");
                break;
            case 2:
                viewHolder.tv_TaskState_MHDA.setVisibility(View.GONE);
                viewHolder.tv_TaskState_MHDA.setText("获得赏金");
                viewHolder.iv_complete.setVisibility(View.VISIBLE);
                break;
            case 3:
                viewHolder.tv_TaskState_MHDA.setVisibility(View.VISIBLE);
                viewHolder.iv_complete.setVisibility(View.GONE);
                viewHolder.tv_TaskState_MHDA.setText("完成等赏");
                break;
            case 4:
                viewHolder.tv_TaskState_MHDA.setVisibility(View.VISIBLE);
                viewHolder.iv_complete.setVisibility(View.GONE);
                viewHolder.tv_TaskState_MHDA.setText("申诉判定完成");
                break;
            case 5:
                viewHolder.tv_TaskState_MHDA.setVisibility(View.VISIBLE);
                viewHolder.iv_complete.setVisibility(View.GONE);
                viewHolder.tv_TaskState_MHDA.setText("放弃");
                break;
        }
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
                tv_TaskState_MHDA;
        ImageView iv_complete;
    }
}
