package com.djx.pin.improve.detail;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.djx.pin.R;
import com.djx.pin.beans.HelpPeopleDetailEntity;
import com.djx.pin.myview.CircleImageView;
import com.djx.pin.utils.QiniuUtils;
import com.djx.pin.utils.TurnIntoTime;
import com.djx.pin.utils.myutils.ConstantUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2016/7/28 0028.
 */
public class MyHelperOnLineUpload_OtherAdapter extends RecyclerView.Adapter<MyHelperOnLineUpload_OtherAdapter.ViewHolder> {


    private Context context;
    List<HelpPeopleDetailEntity.Receiver> list;
    LayoutInflater inflater;

    /**
     * 设置订单总状态
     *
     * @param taskStatus
     */
    public void setTaskStatus(int taskStatus) {
        this.taskStatus = taskStatus;
    }

    private int taskStatus;

    View HeaderView;
    View FooterView;
    Map urlMap;

    public MyHelperOnLineUpload_OtherAdapter(Context context) {
        this.context = context;
        list = new ArrayList();
        inflater = LayoutInflater.from(context);
        urlMap = new HashMap();
    }

    public void addAll(List<HelpPeopleDetailEntity.Receiver> list) {
        this.list = list;
    }


    public void addHeader(View headerView) {
        HeaderView = headerView;
        notifyItemInserted(0);
    }

    public List getList() {
        return list;
    }

    public void clear() {
        list.clear();
    }


    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return ConstantUtils.HEADVIEW_TYPE;
        } else {
            return ConstantUtils.COMMON_TYPE;
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        ViewHolder viewHolder;
        View v = null;
        switch (viewType) {
            case 1:
                v = inflater.inflate(R.layout.lv_item_myhelperonlineupload, parent, false);
                break;
            case 0:
                v = HeaderView;
                break;
        }
        viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        if (position > 0) {
            HelpPeopleDetailEntity.Receiver info = list.get(position - 1);
            holder.tv_NickName_MHDA.setText(info.nickname);

            switch (info.status) {
                case 0:
                    holder.tv_time_MHDA.setText(TurnIntoTime.getCreateTime(info.book_time));
                    if (taskStatus == 2) {//订单已结束
                        holder.tv_TaskFunction_MHDA.setText(R.string.order_closed);
                    } else {
                        if (rewardedNum > 0 && rewardedNum >= receiver_limit) {
                            //已满
                            holder.tv_TaskFunction_MHDA.setText(R.string.order_rewarded_number_fulled);
                        } else {
                            holder.tv_TaskFunction_MHDA.setText(R.string.order_2b_verify);
                        }
                    }
                    break;
                case 1:
                    holder.tv_TaskFunction_MHDA.setText(R.string.order_rewarded);
                    holder.tv_time_MHDA.setText(TurnIntoTime.getCreateTime(info.stop_time));
                    break;
                case 2:
                    if (taskStatus == 2) {//订单已结束
                        holder.tv_TaskFunction_MHDA.setText(R.string.order_closed);
                    } else {
                        holder.tv_TaskFunction_MHDA.setText(R.string.order_not_pass);
                    }
                    holder.tv_time_MHDA.setText(TurnIntoTime.getCreateTime(info.stop_time));
                    break;

            }
            holder.tv_TaskFunction_MHDA.setTag(position - 1);
            holder.tv_UploadCotent_MHDA.setText(info.content);
            QiniuUtils.setAvatarByIdFrom7Niu(context, holder.cimg_Avatar_MHDA, info.portrait);
            holder.cimg_Avatar_MHDA.setTag(position - 1);

            holder.tv_TaskFunction_MHDA.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemOrderClickListener.onClick(position - 1);
                }
            });
            holder.cimg_Avatar_MHDA.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemAvatarClickListener.onClick(position - 1);
                }
            });

        }
    }

    @Override
    public int getItemCount() {
        if (HeaderView == null) {
            return list.size();
        } else {
            return list.size() + 1;
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
                    if (getItemViewType(position) == 0) {
                        return gridLayoutManager.getSpanCount();
                    } else {
                        return 1;
                    }
                }
            });
        }
    }

    private int rewardedNum;
    private int receiver_limit;

    public void setRewarded2ReceiverLimit(int rewardedNum, int receiver_limit) {
        this.receiver_limit = receiver_limit;
        this.rewardedNum = rewardedNum;
    }

    Long limitTime;

    public void setLimitTime(long limitTime) {
        this.limitTime = limitTime;
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

    private OnItemAvatarClickListener onItemAvatarClickListener;
    private OnItemOrderClickListener onItemOrderClickListener;

    public void setOnItemAvatarClickListener(OnItemAvatarClickListener onItemAvatarClickListener) {
        this.onItemAvatarClickListener = onItemAvatarClickListener;
    }

    public void setOnItemOrderClickListener(OnItemOrderClickListener onItemOrderClickListener) {
        this.onItemOrderClickListener = onItemOrderClickListener;
    }

    public interface OnItemAvatarClickListener {
        void onClick(int pos);
    }

    public interface OnItemOrderClickListener {
        void onClick(int pos);
    }
}