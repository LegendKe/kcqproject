package com.djx.pin.improve.positiveenergy.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.djx.pin.R;
import com.djx.pin.beans.LostChildInfo;
import com.djx.pin.improve.base.adapter.BaseRecyAdapter;
import com.djx.pin.improve.common.PhotoShowActivity;
import com.djx.pin.myview.CircleImageView;
import com.djx.pin.utils.DateUtils;
import com.djx.pin.utils.QiniuUtils;
import com.djx.pin.widget.CustomImageView;
import com.djx.pin.widget.NineGridLayout;

import java.util.ArrayList;
import java.util.Date;

import butterknife.Bind;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.util.TextUtils;

/**
 * Created by 柯传奇 on 2016/10/12 0012.
 */
public class LostChildAdapter extends BaseRecyAdapter<LostChildInfo.Result.ChildInfo> {

    public LostChildAdapter(Context context) {
        super(context);
    }

    /**
     * 1-1设置item的布局
     *
     * @return
     */
    @Override
    protected CommonViewHolder setCommonViewHolder() {
        View view = View.inflate(context, R.layout.lv_item_positiveenergy, null);
        return new MyViewHolder(view);
    }

    /**
     * 1-2设置item数据
     *
     * @param holder
     * @param bean
     */
    @Override
    protected void onBindCommonViewHolder(RecyclerView.ViewHolder holder, final LostChildInfo.Result.ChildInfo bean) {
        MyViewHolder viewHolder = (MyViewHolder) holder;
        viewHolder.tv_userName.setText(bean.nickname);

        if(TextUtils.isEmpty(bean.portrait)){
            viewHolder.cimg_Avatar.setImageResource(R.mipmap.ic_defaultcontact);
        }else {
            QiniuUtils.setAvatarByIdFrom7Niu(context,viewHolder.cimg_Avatar,bean.portrait);
        }
        viewHolder.tv_description.setText(bean.description);
        viewHolder.tv_Time.setText(DateUtils.formatDate(new Date(bean.create_time),DateUtils.yyyyMMddHHmm)+"");

        if(bean.media != null){
            if(bean.media.size() == 1){
                viewHolder.iv_oneimage.setVisibility(View.VISIBLE);
                viewHolder.nineGridLayout.setVisibility(View.GONE);
                QiniuUtils.setImageViewByIdFrom7Niu(context,viewHolder.iv_oneimage,bean.media.get(0).media_id,600,600,null);
                viewHolder.iv_oneimage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(context, PhotoShowActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putInt("CURRENT_POS", 0);
                        ArrayList<String> ids = new ArrayList<>();
                        ids.add(bean.media.get(0).media_id);
                        intent.putStringArrayListExtra("IDS", ids);
                        intent.putExtras(bundle);
                        context.startActivity(intent);
                    }
                });
            }else if(bean.media.size() > 1){
                viewHolder.iv_oneimage.setVisibility(View.GONE);
                viewHolder.nineGridLayout.setVisibility(View.VISIBLE);
                final ArrayList<String> ids = new ArrayList<>();
                for (int i = 0; i < bean.media.size(); i++) {
                    ids.add(bean.media.get(i).media_id);
                }
                QiniuUtils.set9GridByIdsFrom7Niu(context,ids,bean.id,viewHolder.nineGridLayout);
                viewHolder.nineGridLayout.setImageShowListener(new NineGridLayout.ImageShowListener() {
                    @Override
                    public void imageShow(int imgPos,ArrayList<CustomImageView> imageViews) {
                        if (ids != null) {
                            Intent intent = new Intent(context, PhotoShowActivity.class);
                            intent.putExtra("CURRENT_POS", imgPos);
                            intent.putStringArrayListExtra("IDS", ids);
                            context.startActivity(intent);
                        }
                    }
                });
            }else {
                viewHolder.iv_oneimage.setVisibility(View.GONE);
                viewHolder.nineGridLayout.setVisibility(View.GONE);
            }
        }else {
            viewHolder.iv_oneimage.setVisibility(View.GONE);
            viewHolder.nineGridLayout.setVisibility(View.GONE);
        }
    }

    class MyViewHolder extends CommonViewHolder{
        @Bind(R.id.tv_UserName)
        TextView tv_userName;
        @Bind(R.id.cimg_Avatar)
        CircleImageView cimg_Avatar;
        @Bind(R.id.tv_description)
        TextView tv_description;
        @Bind(R.id.tv_Time)
        TextView tv_Time;

        @Bind(R.id.imgs_9grid_layout)
        NineGridLayout nineGridLayout;
        @Bind(R.id.iv_oneimage)
        ImageView iv_oneimage;

        public MyViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
    }
}
