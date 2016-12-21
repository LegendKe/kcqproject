package com.djx.pin.adapter;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.djx.pin.R;
import com.djx.pin.beans.RewardOnlineDetailInfo;
import com.djx.pin.myview.CircleImageView;
import com.djx.pin.utils.QiniuUtils;
import com.djx.pin.utils.TurnIntoTime;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cz.msebera.android.httpclient.util.TextUtils;

/**
 * Created by Administrator on 2016/7/28 0028.
 */
public class RewardOnLineUpload_OtherAdapter extends RecyclerView.Adapter<RewardOnLineUpload_OtherAdapter.ViewHolder> {


    private Context context;
    List<RewardOnlineDetailInfo.Result.Receiver> list;
    LayoutInflater inflater;

    View HeaderView;
    View FooterView;
    Map urlMap;

    public RewardOnLineUpload_OtherAdapter(Context context) {
        this.context = context;
        list = new ArrayList();
        inflater = LayoutInflater.from(context);
        urlMap=new HashMap();
    }

    public void addData(RewardOnlineDetailInfo.Result.Receiver info) {
        list.add(info);
    }

    public void addHeader(View headerView) {
        HeaderView = headerView;
        notifyItemInserted(0);
    }

    public void clear() {
        list.clear();
    }
    public void addFooter(View footerView) {
        FooterView = footerView;
        notifyItemInserted(list.size() + 1);
    }
    @Override
    public int getItemViewType(int position) {
        if (HeaderView == null) {
            return 0;
        } else if (position == 0) {
            return 1;
        } else if (position == list.size() + 1) {
            return 2;
        }
        return 0;

    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        ViewHolder viewHolder;
        View v = null;
        switch (viewType) {
            case 0:
                v = inflater.inflate(R.layout.lv_item_myhelperonlineupload, parent, false);

                break;
            case 1:
                v = HeaderView;

                break;
            case 2:
                v = FooterView;

                break;
        }
        viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if (position > 0 && position <= list.size()) {
            RewardOnlineDetailInfo.Result.Receiver info = list.get(position - 1);
            holder.tv_NickName_MHDA.setText(info.nickname);
            switch (info.status) {
                case 0:
                    if(orderStatus == 2){
                        holder.tv_TaskFunction_MHDA.setText(R.string.order_closed);
                    }else {
                        if(rewardedNum > 0 && rewardedNum >= receiver_limit){
                            holder.tv_TaskFunction_MHDA.setText(R.string.order_rewarded_number_fulled);
                        }else {
                            holder.tv_TaskFunction_MHDA.setText(R.string.order_2b_verify);
                        }
                    }
                    holder.tv_time_MHDA.setText(TurnIntoTime.getCreateTime(info.book_time));
                    break;

                case 1:
                    holder.tv_TaskFunction_MHDA.setText(R.string.order_rewarded);
                    holder.tv_time_MHDA.setText(TurnIntoTime.getCreateTime(info.stop_time));
                    break;
                case 2:
                    if(orderStatus == 2){
                        holder.tv_TaskFunction_MHDA.setText(R.string.order_closed);
                    }else {
                        holder.tv_TaskFunction_MHDA.setText(R.string.order_not_pass);
                    }
                    holder.tv_time_MHDA.setText(TurnIntoTime.getCreateTime(info.stop_time));
                    break;

            }
            holder.tv_UploadCotent_MHDA.setText(info.content);

            if(TextUtils.isEmpty(info.portrait)){
                holder.cimg_Avatar_MHDA.setImageResource(R.mipmap.ic_defaultcontact);
            }else {
                QiniuUtils.setAvatarByIdFrom7Niu(context,holder.cimg_Avatar_MHDA, info.portrait);
            }

        }
    }

    @Override
    public int getItemCount() {
        if (HeaderView == null) {
            return list.size();
        } else {
            return list.size() + 2;
        }
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        RecyclerView.LayoutManager manager = recyclerView.getLayoutManager();
        if (manager instanceof GridLayoutManager) {
            final GridLayoutManager gridLayoutManager = (GridLayoutManager) manager;
            gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                @Override
                public int getSpanSize(int position) {
                    if (getItemViewType(position) == 1) {
                        return gridLayoutManager.getSpanCount();
                    } else {
                        return 1;
                    }
                }
            });
        }
    }

    private int orderStatus;

    /**
     * 订单状态
     * @param status
     */
    public void setStatus(int status) {
        this.orderStatus = status;
    }

    int rewardedNum;//已得赏人数
    int receiver_limit;//接单上限
    public void setRewarded2limitNum(int rewardedNum, int receiver_limit) {
        this.rewardedNum = rewardedNum;
        this.receiver_limit = receiver_limit;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView tv_NickName_MHDA;
        public TextView tv_time_MHDA;
        public TextView tv_TaskFunction_MHDA;
        public TextView tv_UploadCotent_MHDA;
        public CircleImageView cimg_Avatar_MHDA;

        public ViewHolder(View itemView) {
            super(itemView);
            tv_NickName_MHDA = (TextView) itemView.findViewById(R.id.tv_NickName_MHDA);
            tv_time_MHDA = (TextView) itemView.findViewById(R.id.tv_time_MHDA);
            tv_TaskFunction_MHDA = (TextView) itemView.findViewById(R.id.tv_TaskFunction_MHDA);
            tv_UploadCotent_MHDA = (TextView) itemView.findViewById(R.id.tv_UploadCotent_MHDA);
            cimg_Avatar_MHDA = (CircleImageView) itemView.findViewById(R.id.cimg_Avatar_MHDA);
        }
    }

    public interface OnShareAvatarListener {
        void setOnShareAvatarListener(View view, int type);
    }
}
