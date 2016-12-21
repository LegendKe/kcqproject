package com.djx.pin.improve.detail;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.djx.pin.R;
import com.djx.pin.activity.LookOthersMassageActivity;
import com.djx.pin.beans.HelpPeopleDetailEntity;
import com.djx.pin.myview.CircleImageView;
import com.djx.pin.utils.DateUtils;
import com.djx.pin.utils.QiniuUtils;
import com.djx.pin.utils.myutils.ConstantUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import cz.msebera.android.httpclient.util.TextUtils;

public class CommentAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private List<HelpPeopleDetailEntity.COMMENT> lists;
    private Context context;
    private View headview;

    public CommentAdapter(Context context) {
        this.context = context;
        lists = new ArrayList<>();
    }
    @Override
    public int getItemCount() {
        if(lists == null){
            return 1;
        }
        return lists.size()+1;
    }
    public void addHeader(View headerView) {
        this.headview = headerView;
        notifyItemInserted(0);
    }
    @Override
    public int getItemViewType(int position) {
        return position == 0 ? ConstantUtils.HEADVIEW_TYPE : ConstantUtils.COMMON_TYPE;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(viewType == ConstantUtils.COMMON_TYPE){
            return new CommonViewHolder(View.inflate(context,R.layout.recy_item_user, null));
        }else {
            return new CommonViewHolder(headview);
        }
    }
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        if(position != 0){
            final HelpPeopleDetailEntity.COMMENT comment = lists.get(position-1);
            if(comment != null){
                CommonViewHolder holder1 = (CommonViewHolder) holder;

                if(TextUtils.isEmpty(comment.portrait)){
                    holder1.avatar.setImageResource(R.mipmap.ic_defaultcontact);
                }else {
                    QiniuUtils.setAvatarByIdFrom7Niu(context,holder1.avatar,comment.portrait);
                }
                holder1.tv_name.setText(comment.nickname);
                holder1.tv_time.setText(DateUtils.formatDate(new Date(comment.create_time), DateUtils.LOCALE_DATE_FORMAT));
                holder1.tv_content.setText(comment.content);
                holder1.avatar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Bundle bundle = new Bundle();
                        bundle.putString("user_id", comment.user_id);
                        Intent intent = new Intent(context, LookOthersMassageActivity.class);
                        intent.putExtras(bundle);
                        context.startActivity(intent);
                    }
                });
            }
        }
    }


    class CommonViewHolder extends RecyclerView.ViewHolder {

        private TextView tv_name, tv_content, tv_time;
        private CircleImageView avatar;

        public CommonViewHolder(View itemView) {
            super(itemView);
            tv_time = ((TextView) itemView.findViewById(R.id.tv_time));
            tv_name = ((TextView) itemView.findViewById(R.id.tv_nickname));
            tv_content = ((TextView) itemView.findViewById(R.id.tv_content));
            avatar = ((CircleImageView) itemView.findViewById(R.id.avatar));
        }
    }


    public void addData(List<HelpPeopleDetailEntity.COMMENT> list){
        this.lists = list;
        notifyDataSetChanged();
    }
}
