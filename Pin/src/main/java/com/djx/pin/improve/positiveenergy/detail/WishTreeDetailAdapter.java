package com.djx.pin.improve.positiveenergy.detail;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.djx.pin.R;
import com.djx.pin.activity.LookOthersMassageActivity;
import com.djx.pin.beans.CivilizationDetailCommentInfo;
import com.djx.pin.beans.UserInfo;
import com.djx.pin.improve.base.adapter.BaseRecyAdapter;
import com.djx.pin.improve.common.PhotoShowActivity;
import com.djx.pin.myview.CircleImageView;
import com.djx.pin.personal.ui.PersonalDataActivity;
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
 * Created by 柯传奇 on 2016/12/9 0009.
 */
public class WishTreeDetailAdapter extends BaseRecyAdapter<CivilizationDetailCommentInfo.Result.Comment>{
    private HeadViewHolder headViewHolder;

    public WishTreeDetailAdapter(Context context) {
        super(context);
    }

    /**
     * 1-0-1设置headview的布局
     * 当需添加头时重写该方法
     *
     * @return
     */
    @Override
    protected RecyclerView.ViewHolder setHeadViewHolder() {
        View headview = View.inflate(context, R.layout.headview_wishtree_detail, null);
        RecyclerView.LayoutParams layoutParams = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        headview.setLayoutParams(layoutParams);
        headViewHolder = new HeadViewHolder(headview);
        return headViewHolder;
    }

    private CivilizationDetailCommentInfo.Result result;
    public void setAdapterData(CivilizationDetailCommentInfo.Result result) {
        this.result = result;
        setHeadViewData();
        this.addAll(result.comment);
    }

    public class HeadViewHolder extends RecyclerView.ViewHolder{

        @Bind(R.id.headview_iv_avatar)
        CircleImageView avatar;
        @Bind(R.id.headview_tv_userName)
        TextView tv_userName;
        @Bind(R.id.headview_tv_time)
        TextView tv_time;
        @Bind(R.id.tv_content)
        TextView tv_content;
        @Bind(R.id.imgs_9grid_layout)
        NineGridLayout nineGridLayout;
        @Bind(R.id.tv_location)
        TextView tv_location;

        public HeadViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
    }

    void setHeadViewData(){
        if(result != null){
            headViewHolder.tv_userName.setText(result.nickname);
            headViewHolder.tv_content.setText(result.description);
            headViewHolder.tv_location.setText(result.location);
            headViewHolder.tv_time.setText(DateUtils.formatDate(new Date(result.create_time),DateUtils.yyyyMMddHHmm)+"");

            QiniuUtils.setAvatarByIdFrom7Niu(context,headViewHolder.avatar,result.portrait);
            headViewHolder.avatar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String user_id = result.user_id;
                    Bundle bundle = new Bundle();
                    bundle.putString("user_id", user_id);
                    Intent intent;
                    if (user_id.equals(UserInfo.getUserID(context))) {
                        intent = new Intent(context, PersonalDataActivity.class);
                        intent.putExtras(bundle);
                        context.startActivity(intent);
                    } else {
                        intent = new Intent(context, LookOthersMassageActivity.class);
                        intent.putExtras(bundle);
                        context.startActivity(intent);
                    }
                }
            });
            if(result.media!= null && result.media.size()>0){
                final ArrayList<String> ids = new ArrayList<>();
                for (int i = 0; i < result.media.size(); i++) {
                    ids.add(result.media.get(i).media_id);
                }
                QiniuUtils.set9GridByIdsFrom7Niu(context,ids,result.id,headViewHolder.nineGridLayout);
                headViewHolder.nineGridLayout.setImageShowListener(new NineGridLayout.ImageShowListener() {
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
                headViewHolder.nineGridLayout.setVisibility(View.GONE);
            }
        }
    }
    class MyViewHolder extends CommonViewHolder{
        @Bind(R.id.tv_nickname)
        TextView tv_nickname;
        @Bind(R.id.avatar)
        CircleImageView avatar;
        @Bind(R.id.tv_content)
        TextView tv_content;
        @Bind(R.id.tv_time)
        TextView tv_time;
        public MyViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
    }

    /**
     * 1-1设置item的布局
     *
     * @return
     */
    @Override
    protected CommonViewHolder setCommonViewHolder() {
        MyViewHolder myViewHolder = new MyViewHolder(View.inflate(context, R.layout.recy_item_user, null));
        return myViewHolder;
    }

    /**
     * 1-2设置item数据
     *
     * @param holder
     * @param bean
     */
    @Override
    protected void onBindCommonViewHolder(RecyclerView.ViewHolder holder, final CivilizationDetailCommentInfo.Result.Comment bean) {
        MyViewHolder myViewHolder = (MyViewHolder) holder;

        if(TextUtils.isEmpty(bean.portrait)){
            myViewHolder.avatar.setImageResource(R.mipmap.ic_defaultcontact);
        }else {
            QiniuUtils.setAvatarByIdFrom7Niu(context,myViewHolder.avatar,bean.portrait);
        }
        myViewHolder.tv_content.setText(bean.content);
        myViewHolder.tv_nickname.setText(bean.nickname);
        myViewHolder.tv_time.setText(DateUtils.formatDate(new Date(bean.create_time),DateUtils.yyyyMMddHHmm)+"");
        myViewHolder.avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String user_id = bean.user_id;
                Bundle bundle = new Bundle();
                bundle.putString("user_id", user_id);
                Intent intent;
                if (user_id.equals(UserInfo.getUserID(context))) {
                    intent = new Intent(context, PersonalDataActivity.class);
                    intent.putExtras(bundle);
                    context.startActivity(intent);
                } else {
                    intent = new Intent(context, LookOthersMassageActivity.class);
                    intent.putExtras(bundle);
                    context.startActivity(intent);
                }
            }
        });
    }




}
