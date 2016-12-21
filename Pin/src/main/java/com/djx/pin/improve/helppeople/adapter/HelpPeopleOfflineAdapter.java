package com.djx.pin.improve.helppeople.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.djx.pin.R;
import com.djx.pin.activity.LookOthersMassageActivity;
import com.djx.pin.beans.HelpPeopleEntity;
import com.djx.pin.improve.base.adapter.BaseRecyAdapter;
import com.djx.pin.myview.CircleImageView;
import com.djx.pin.utils.DateUtils;
import com.djx.pin.utils.QiniuUtils;

import java.util.Date;

import butterknife.Bind;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.util.TextUtils;

/**
 * Created by 柯传奇 on 2016/12/1 0001.
 */
public class HelpPeopleOfflineAdapter extends BaseRecyAdapter<HelpPeopleEntity> {

    public HelpPeopleOfflineAdapter(Context context) {
        super(context);
    }

    /**
     * 设置item数据
     *
     * @param holder
     * @param bean
     */
    @Override
    protected void onBindCommonViewHolder(RecyclerView.ViewHolder holder, final HelpPeopleEntity bean) {
        MyViewHolder viewHolder = (MyViewHolder) holder;
        viewHolder.tv_userName.setText(bean.nickname);
        if(TextUtils.isEmpty(bean.portrait)){
            viewHolder.cir_portrait.setImageResource(R.mipmap.ic_defaultcontact);
        }else {
            QiniuUtils.setAvatarByIdFrom7Niu(context,viewHolder.cir_portrait,bean.portrait);
        }
        viewHolder.tv_description.setText(bean.description);
        viewHolder.tv_address.setText(bean.address);
        viewHolder.tv_distance.setText((int)bean.distance+"km");
        viewHolder.tv_price.setText(bean.price+"");
        viewHolder.tv_start_time.setText(DateUtils.formatDate(new Date(bean.start_time),DateUtils.yyyyMMddHHmm)+"");
        viewHolder.cir_portrait.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("user_id", bean.user_id);
                Intent intent = new Intent(context, LookOthersMassageActivity.class);
                intent.putExtras(bundle);
                context.startActivity(intent);
            }
        });
    }

    /**
     * 设置item的布局
     *
     * @return
     */
    @Override
    protected CommonViewHolder setCommonViewHolder() {
        View view = View.inflate(context, R.layout.lv_intem_helperpeoplefragment, null);
        return new MyViewHolder(view);
    }

    class MyViewHolder extends CommonViewHolder{
        @Bind(R.id.tv_userName)
        TextView tv_userName;
        @Bind(R.id.img_portrait)
        CircleImageView cir_portrait;
        @Bind(R.id.tv_description)
        TextView tv_description;
        @Bind(R.id.tv_address)
        TextView tv_address;
        @Bind(R.id.tv_distance)
        TextView tv_distance;
        @Bind(R.id.tv_price)
        TextView tv_price;
        @Bind(R.id.tv_start_time)
        TextView tv_start_time;
        public MyViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
    }

}
