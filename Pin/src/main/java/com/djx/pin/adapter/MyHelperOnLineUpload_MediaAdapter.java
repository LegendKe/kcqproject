package com.djx.pin.adapter;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.djx.pin.R;
import com.djx.pin.beans.HelpPeopleDetailEntity;
import com.djx.pin.utils.LogUtil;
import com.djx.pin.utils.QiniuUtils;
import com.djx.pin.utils.myutils.ConstantUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/7/28 0028.
 */
public class MyHelperOnLineUpload_MediaAdapter extends RecyclerView.Adapter<MyHelperOnLineUpload_MediaAdapter.ViewHolder> {

    private static final int VIDEO = 2;
    OnRewardListener onRewardListener;
    private Context context;
    List<HelpPeopleDetailEntity.Receiver> list;
    LayoutInflater inflater;
    private int taskStatus;

    private int mediaType;//视频或图片
    private View headview;

    public MyHelperOnLineUpload_MediaAdapter(Context context) {
        this.context = context;
        list = new ArrayList();
        inflater = LayoutInflater.from(context);
    }

    public void setOnRewardListener(OnRewardListener onRewardListener) {
        this.onRewardListener = onRewardListener;
    }

    public void addAll(List<HelpPeopleDetailEntity.Receiver> list) {
        this.list = list;
    }

    public List<HelpPeopleDetailEntity.Receiver> getList() {
        return list;
    }

    public void clear() {
        list.clear();
    }

    public void addHeader(View headerView) {
        this.headview = headerView;
        notifyItemInserted(0);
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
        View view;
        if (viewType == ConstantUtils.HEADVIEW_TYPE) {
            view = headview;
        } else {
            view = inflater.inflate(R.layout.lv_item_myhelperonlineupload_viodemedia, parent, false);
        }
        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {

        if (position > 0) {
            HelpPeopleDetailEntity.Receiver info = list.get(position - 1);

            switch (info.status) {
                case 0:
                    if (taskStatus == 2) {
                        holder.tv_state.setText(R.string.order_closed);
                    } else {
                        if (rewardedNum > 0 && rewardedNum >= receiver_limit) {
                            //已满
                            holder.tv_state.setText(R.string.order_rewarded_number_fulled);
                        } else {
                            holder.tv_state.setText(R.string.order_2b_verify);
                        }
                    }
                    holder.rl_image.setTag(position - 1);

                    break;
                case 1:
                    holder.tv_state.setText(R.string.order_rewarded);
                    holder.rl_image.setTag(position - 1);
                    break;
                case 2:
                    if (taskStatus == 2) {
                        holder.tv_state.setText(R.string.order_closed);
                    } else {
                        holder.tv_state.setText(R.string.order_not_pass);
                    }
                    holder.rl_image.setTag(position - 1);
                    break;

            }
            holder.rl_image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onRewardListener != null) {
                        onRewardListener.setOnRewardListener(position - 1);
                    }
                }
            });
            if (mediaType == VIDEO) {//视频
                holder.image.setImageDrawable(context.getResources().getDrawable(R.mipmap.ic_videoview));
            } else if (info.media != null && info.media.size() > 0 && info.media.get(0).media_id.length() > 1) {//图片
                QiniuUtils.setImageViewByIdFrom7Niu(context, holder.image, info.media.get(0).media_id, 320, 400,null);
            }
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
                    position = position == 0 ? gridLayoutManager.getSpanCount() : 1;
                    return position;
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        if (list == null) {
            return 1;
        }
        return list.size() + 1;
    }

    public void setTaskStatus(int taskStatus) {
        this.taskStatus = taskStatus;
    }

    /**
     * 设置类型：视频或图片
     *
     * @param mediaType
     */
    public void setMediaType(int mediaType) {
        this.mediaType = mediaType;
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
        LogUtil.e("-----------time------------" + limitTime);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView tv_state;
        public ImageView image;
        public RelativeLayout rl_image;

        public ViewHolder(View itemView) {
            super(itemView);
            tv_state = (TextView) itemView.findViewById(R.id.tv_state);
            image = (ImageView) itemView.findViewById(R.id.image);
            rl_image = (RelativeLayout) itemView.findViewById(R.id.rl_image);
        }
    }

    /**
     * 点击审核接口
     */
    public interface OnRewardListener {
        void setOnRewardListener(int pos);
    }
}
