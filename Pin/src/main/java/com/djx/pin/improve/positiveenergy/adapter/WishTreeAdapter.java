package com.djx.pin.improve.positiveenergy.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.djx.pin.R;
import com.djx.pin.activity.GoHomeMainActivity;
import com.djx.pin.activity.WindowThrowMainActivity;
import com.djx.pin.beans.CivilizationInfo;
import com.djx.pin.improve.base.adapter.BaseRecyAdapter;
import com.djx.pin.improve.common.PhotoShowActivity;
import com.djx.pin.improve.positiveenergy.LostChildActivity;
import com.djx.pin.myview.CircleImageView;
import com.djx.pin.myview.timepicker.common.ResizableImageView;
import com.djx.pin.utils.DateUtils;
import com.djx.pin.utils.QiniuUtils;
import com.djx.pin.utils.myutils.ScreenTools;
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
public class WishTreeAdapter extends BaseRecyAdapter<CivilizationInfo.Result.CultureWallInfo> implements View.OnClickListener {

    public WishTreeAdapter(Context context) {
        super(context);
    }

    @Override
    protected RecyclerView.ViewHolder setHeadViewHolder() {
        View headview = View.inflate(context, R.layout.item_wish_tree_headview, null);
        headview.findViewById(R.id.ll_go_home).setOnClickListener(this);
        headview.findViewById(R.id.ll_window_throw).setOnClickListener(this);
        headview.findViewById(R.id.ll_civilization).setOnClickListener(this);
        RecyclerView.LayoutParams layoutParams = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ScreenTools.instance(context).dip2px(110));
        headview.setLayoutParams(layoutParams);
        return new HeadViewHolder(headview);
    }

    /**
     * 1-1设置item的布局
     *
     * @return
     */
    @Override
    protected CommonViewHolder setCommonViewHolder() {
        View view = View.inflate(context, R.layout.item_culture_wall, null);
        return new MyViewHolder(view);
    }
    /**
     * 1-2设置item数据
     *
     * @param holder
     * @param bean
     */
    @Override
    protected void onBindCommonViewHolder(RecyclerView.ViewHolder holder, CivilizationInfo.Result.CultureWallInfo bean) {
        final MyViewHolder viewHolder = (MyViewHolder) holder;
        viewHolder.tv_userName.setText(bean.nickname);

        if(TextUtils.isEmpty(bean.portrait)){
            Log.e("json","头像为空:"+bean.nickname+"  : "+bean.portrait);
            viewHolder.cimg_Avatar.setImageResource(R.mipmap.ic_defaultcontact);
        }else {
            QiniuUtils.setAvatarByIdFrom7Niu(context,viewHolder.cimg_Avatar,bean.portrait);
        }

        viewHolder.tv_description.setText(bean.description);
        viewHolder.tv_Time.setText(DateUtils.formatDate(new Date(bean.create_time),DateUtils.yyyyMMddHHmm)+"");
        viewHolder.tv_location.setText(bean.location);
        if(bean.media != null){
            if(bean.media.size() == 1){
                viewHolder.iv_oneimage.setVisibility(View.VISIBLE);
                viewHolder.nineGridLayout.setVisibility(View.GONE);
                final ArrayList<String> ids = new ArrayList<>();
                ids.add(bean.media.get(0).media_id);
                QiniuUtils.setOneImageByIdFrom7Niu(context, viewHolder.iv_oneimage, ids.get(0));
                viewHolder.iv_oneimage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(context, PhotoShowActivity.class);
                        intent.putExtra("CURRENT_POS",0);
                        intent.putStringArrayListExtra("IDS", ids);
                        context.startActivity(intent);
                        /*DialogUtils.showOneImage(context,ids.get(0),viewHolder.iv_oneimage);*/
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
                           /* DialogUtils.showImages(context,ids,imageViews,imgPos);*/
                            Intent intent = new Intent(context, PhotoShowActivity.class);
                            intent.putStringArrayListExtra("IDS", ids);
                            intent.putExtra("CURRENT_POS",imgPos);
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

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.ll_go_home:
                context.startActivity(new Intent(context, GoHomeMainActivity.class));
                break;
            case R.id.ll_civilization:
                context.startActivity(new Intent(context, LostChildActivity.class));
                break;
            case R.id.ll_window_throw:
                context.startActivity(new Intent(context, WindowThrowMainActivity.class));
                break;
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
        @Bind(R.id.tv_location)
        TextView tv_location;
        @Bind(R.id.imgs_9grid_layout)
        NineGridLayout nineGridLayout;
        @Bind(R.id.iv_oneimage)
        ResizableImageView iv_oneimage;

        public MyViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
    }

    class HeadViewHolder extends RecyclerView.ViewHolder{
        public HeadViewHolder(View itemView) {
            super(itemView);
        }
    }
}
