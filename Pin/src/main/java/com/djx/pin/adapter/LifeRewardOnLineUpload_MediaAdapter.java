package com.djx.pin.adapter;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.djx.pin.R;
import com.djx.pin.beans.LifeRewardOnlineDetailInfo;
import com.djx.pin.utils.QiniuUtils;
import com.djx.pin.utils.myutils.LogicUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/7/28 0028.
 */
public class LifeRewardOnLineUpload_MediaAdapter extends RecyclerView.Adapter<LifeRewardOnLineUpload_MediaAdapter.ViewHolder> {


    private static final int VIDEO = 2;//视频标记
    private Context context;
    List<LifeRewardOnlineDetailInfo.Result.Receiver> list;
    LayoutInflater inflater;
    OnLookImageListener onLookImageListener;
    View HeaderView;
    View FooterView;
    private List<LifeRewardOnlineDetailInfo.Result.Receiver> receiverList;
    private int taskStatus;//订单状态，0-发布未支付，1-发单成功，2-订单结束（停止招募）


    public LifeRewardOnLineUpload_MediaAdapter(Context context, OnLookImageListener onLookImageListener) {
        this.context = context;
        list = new ArrayList();
        inflater = LayoutInflater.from(context);
        this.onLookImageListener = onLookImageListener;
        receiverList = new ArrayList<>();
    }

    public void addData(LifeRewardOnlineDetailInfo.Result.Receiver info) {
        list.add(info);
    }


    public void addHeader(View headerView) {
        HeaderView = headerView;
        notifyItemInserted(0);
    }

    public void addFooter(View footerView) {
        FooterView = footerView;
        notifyItemInserted(list.size() + 1);
    }

    public void clear() {
        list.clear();
    }

    public List getList() {
        return list;
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
                v = inflater.inflate(R.layout.lv_item_myhelperonlineupload_viodemedia, parent, false);

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
    public void onBindViewHolder(final ViewHolder holder, int position) {
        if (position > 0 && position < list.size() + 1) {
            LifeRewardOnlineDetailInfo.Result.Receiver info = list.get(position - 1);
            receiverList.add(info);

            LogicUtils.orderStatusLogic(taskStatus, info.status, receiver_limit, rewardedNum, new LogicUtils.OrderClosedListener() {
                @Override
                public void orderClosed() {
                    holder.tv_state.setText(R.string.order_closed);
                }
            }, new LogicUtils.Order2BVerifyListener() {
                @Override
                public void order2BVerify() {
                    holder.tv_state.setText(R.string.order_2b_verify);
                }
            }, new LogicUtils.OrderNotPassListener() {
                @Override
                public void orderNotPass() {
                    holder.tv_state.setText(R.string.order_not_pass);
                }
            }, new LogicUtils.OrderRewardedListener() {
                @Override
                public void orderRewarded() {
                    holder.tv_state.setText(R.string.order_rewarded);
                }
            });


            if(mediaType == VIDEO){//视频
                holder.image.setImageDrawable(context.getResources().getDrawable(R.mipmap.ic_videoview));
                Log.i("test","content------------------------------"+info.content);

            } else if(info.media != null && info.media.size() > 0 && info.media.get(0).media_id.length()>1) {//图片
                    QiniuUtils.setImageViewByIdFrom7Niu(context, holder.image, info.media.get(0).media_id, 320, 400,null);
            }
            holder.rl_image.setTag(position - 1);
        }
    }

    /**
     * Returns the total number of items in the data set hold by the adapter.
     *
     * @return The total number of items in this adapter.
     */
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
                    if (getItemViewType(position) == 1 || getItemViewType(position) == 2) {
                        return gridLayoutManager.getSpanCount();
                    } else {
                        return 1;
                    }
                }
            });
        }
    }

    /**
     * 设置订单状态,从activity获取
     *
     * @param taskStates
     */
    public void setTaskStatus(int taskStates) {
        this.taskStatus = taskStates;
    }



    private int rewardedNum;//已打赏人数
    private int receiver_limit;
    /**
     *
     * @param confirm_num 已得赏人数
     * @param receiver_limit
     */
    public void setConfirmed2LimitNum(int confirm_num, int receiver_limit) {
        this.rewardedNum = confirm_num;
        this.receiver_limit = receiver_limit;
    }

    private int mediaType;
    public void setMediaType(int type) {
        this.mediaType = type;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView tv_state;
        public ImageView image;
        public RelativeLayout rl_image;

        public ViewHolder(View itemView) {
            super(itemView);
            tv_state = (TextView) itemView.findViewById(R.id.tv_state);
            image = (ImageView) itemView.findViewById(R.id.image);
            rl_image = (RelativeLayout) itemView.findViewById(R.id.rl_image);
            itemView.setOnClickListener(this);//设置item的点击事件
        }

        //item点击监听
        @Override
        public void onClick(View v) {
            if (getLayoutPosition() != 0) {
                onLookImageListener.setOnLookImageListener(v, 1, receiverList, getLayoutPosition()-1);
            }
        }
    }


    /**
     * 点击审核接口
     */
    public interface OnLookImageListener {
        void setOnLookImageListener(View v, int tag, List<LifeRewardOnlineDetailInfo.Result.Receiver> receiverList, int pos);
    }



}
