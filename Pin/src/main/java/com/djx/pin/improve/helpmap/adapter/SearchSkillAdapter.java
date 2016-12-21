package com.djx.pin.improve.helpmap.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.djx.pin.R;
import com.djx.pin.beans.SkillSearchEntity;
import com.djx.pin.myview.CircleImageView;
import com.djx.pin.utils.QiniuUtils;

import java.text.DecimalFormat;
import java.util.List;

/**
 * Created by 柯传奇 on 2016/10/21 0021.
 */

public class SearchSkillAdapter extends RecyclerView.Adapter<SearchSkillAdapter.ItemViewHolder>{

    private List<SkillSearchEntity.SkillBean> lists;
    private Context context;
    private ItemClickListener itemClickListener;
    private ReverseGeoCodeResult.AddressComponent addressDetail;

    public SearchSkillAdapter(Context context) {
        this.context = context;
    }


    public void addAll(List<SkillSearchEntity.SkillBean> lists){
        this.lists = lists;
        notifyDataSetChanged();
    }
    public void addMoreData(List<SkillSearchEntity.SkillBean> list) {
        this.lists.addAll(list);
        notifyDataSetChanged();
    }

    @Override
    public SearchSkillAdapter.ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        return new ItemViewHolder(View.inflate(context, R.layout.item_search_skill, null));

    }


    @Override
    public void onBindViewHolder(final SearchSkillAdapter.ItemViewHolder holder, final int position) {

        DecimalFormat df = new DecimalFormat("###.00");
        SkillSearchEntity.SkillBean skillBean = lists.get(position);
        holder.tv_content.setText(skillBean.skill_descr);
        DecimalFormat df2 = new DecimalFormat("###");
        holder.tv_money.setText("¥" + df2.format(skillBean.price) + " /次");
        holder.tv_distance.setText(df.format(skillBean.distance)+"km");
        if (!TextUtils.isEmpty(skillBean.portrait)) {
            QiniuUtils.setAvatarByIdFrom7Niu(context, holder.iv_avatar, skillBean.portrait);
        }else {
            holder.iv_avatar.setImageResource(R.mipmap.ic_defaultcontact);
        }

    }


    @Override
    public int getItemCount() {
        if(lists != null){
            return lists.size();
        }else {
            return 0;
        }
    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public SkillSearchEntity.SkillBean getItem(int position) {
        return lists.get(position);
    }


    class ItemViewHolder extends RecyclerView.ViewHolder{

        TextView tv_content,tv_address,tv_money,tv_distance;
        CircleImageView iv_avatar;


        public ItemViewHolder(View itemView) {
            super(itemView);
            tv_content = ((TextView) itemView.findViewById(R.id.tv_content));
            tv_address = ((TextView) itemView.findViewById(R.id.tv_address));
            tv_money = ((TextView) itemView.findViewById(R.id.tv_money));
            iv_avatar = ((CircleImageView) itemView.findViewById(R.id.iv_avatar));
            tv_distance = ((TextView) itemView.findViewById(R.id.tv_distance));
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(itemClickListener != null){
                        itemClickListener.onItemClick(getLayoutPosition(),v);
                    }
                }
            });
        }

    }


    /**
     * 声明一个item的点击事件接口
     */
    public interface ItemClickListener
    {
        void onItemClick(int position, View itemView);
    }


}
