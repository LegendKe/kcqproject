package com.djx.pin.adapter;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.djx.pin.R;
import com.djx.pin.beans.RewardOnlineDetailInfo;
import com.djx.pin.utils.QiniuUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/7/28 0028.
 */
public class RewardOnLineUpload_MediaAdapter extends RecyclerView.Adapter<RewardOnLineUpload_MediaAdapter.ViewHolder> implements View.OnClickListener {


    private Context context;
    List<RewardOnlineDetailInfo.Result.Receiver> list;
    LayoutInflater inflater;
    OnLookImageListener onLookImageListener;

    View HeaderView;
    View FooterView;
    private int toatal_status;//总订单状态

    public RewardOnLineUpload_MediaAdapter(Context context,OnLookImageListener onLookImageListener) {
        this.context = context;
        list = new ArrayList();
        inflater = LayoutInflater.from(context);
        this.onLookImageListener=onLookImageListener;
    }
    public List getList(){
        return list;
    }
    public void addData(RewardOnlineDetailInfo.Result.Receiver info) {
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

    /**
     * Called when RecyclerView needs a new {@link ViewHolder} of the given type to represent
     * an item.
     * <p/>
     * This new ViewHolder should be constructed with a new View that can represent the items
     * of the given type. You can either create a new View manually or inflate it from an XML
     * layout file.
     * <p/>
     * The new ViewHolder will be used to display items of the adapter using
     * {@link #(ViewHolder, int, List)}. Since it will be re-used to display
     * different items in the data set, it is a good idea to cache references to sub views of
     * the View to avoid unnecessary {@link View#findViewById(int)} calls.
     *
     * @param parent   The ViewGroup into which the new View will be added after it is bound to
     *                 an adapter position.
     * @param viewType The view type of the new View.
     * @return A new ViewHolder that holds a View of the given view type.
     * @see #getItemViewType(int)
     * @see #onBindViewHolder(ViewHolder, int)
     */
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

    /**
     * Called by RecyclerView to display the data at the specified position. This method should
     * update the contents of the {@link ViewHolder#} to reflect the item at the given
     * position.
     * <p/>
     * Note that unlike {@link ListView}, RecyclerView will not call this method
     * again if the position of the item changes in the data set unless the item itself is
     * invalidated or the new position cannot be determined. For this reason, you should only
     * use the <code>position</code> parameter while acquiring the related data item inside
     * this method and should not keep a copy of it. If you need the position of an item later
     * on (e.g. in a click listener), use {@link ViewHolder#()} which will
     * have the updated adapter position.
     * <p/>
     * Override {@link (ViewHolder, int, List)} instead if Adapter can
     * handle effcient partial bind.
     *
     * @param holder   The ViewHolder which should be updated to represent the contents of the
     *                 item at the given position in the data set.
     * @param position The position of the item within the adapter's data set.
     */
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if (position > 0 && position < list.size() + 1) {
            RewardOnlineDetailInfo.Result.Receiver info = list.get(position - 1);
            switch (info.status) {
                case 0:
                    if(toatal_status == 2){//订单结束
                        holder.tv_state.setText(R.string.order_closed);
                    }else{
                        if(rewardedNum > 0 && rewardedNum >= receiver_limit){//有人得赏且需求人数已满
                            holder.tv_state.setText(R.string.order_rewarded_number_fulled);
                        }else {
                            holder.tv_state.setText(R.string.order_2b_verify);
                        }
                    }
                    break;
                case 1:
                    holder.tv_state.setText(R.string.order_rewarded);
                    break;
                case 2:
                    if(toatal_status == 2){
                        holder.tv_state.setText(R.string.order_closed);
                    }else{
                        holder.tv_state.setText(R.string.order_not_pass);
                    }
                    break;
            }
            if(mediaType == 2){
                holder.image.setImageResource(R.mipmap.ic_videoview);
            }else {
                QiniuUtils.setImageViewByIdFrom7Niu(context,holder.image, info.media.get(0).media_id,320,400,null);
            }
            holder.rl_image.setTag(position-1);
            holder.rl_image.setOnClickListener(this);
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

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.rl_image:
                onLookImageListener.setOnLookImageListener(v,1);
                break;
        }
    }

    /**
     * 设置订单状态
     * @param status
     */
    public void setStatus(int status) {
        this.toatal_status = status;
    }

    private int mediaType;
    /**
     * 设置media类型
     * @param mediaType
     */
    public void setMediaType(int mediaType) {
        this.mediaType = mediaType;
    }


    int rewardedNum;//已得赏人数
    int receiver_limit;//接单上限
    public void setRewarded2limitNum(int rewardedNum, int receiver_limit) {
        this.rewardedNum = rewardedNum;
        this.receiver_limit = receiver_limit;
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
     * */
    public interface OnLookImageListener{
        void setOnLookImageListener(View v,int tag);
    }
}
