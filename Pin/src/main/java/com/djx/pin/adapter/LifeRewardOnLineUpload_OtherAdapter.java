package com.djx.pin.adapter;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.djx.pin.R;
import com.djx.pin.beans.LifeRewardOnlineDetailInfo;
import com.djx.pin.beans.MyHelperOnLineUploadInfo;
import com.djx.pin.myview.CircleImageView;
import com.djx.pin.serverapiconfig.ServerAPIConfig;
import com.djx.pin.utils.AndroidAsyncHttp;
import com.djx.pin.utils.QiniuUtils;
import com.djx.pin.utils.TurnIntoTime;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;

/**
 * Created by Administrator on 2016/7/28 0028.
 */
public class LifeRewardOnLineUpload_OtherAdapter extends RecyclerView.Adapter<LifeRewardOnLineUpload_OtherAdapter.ViewHolder> {


    private Context context;
    List<LifeRewardOnlineDetailInfo.Result.Receiver> list;
    LayoutInflater inflater;

    View HeaderView;
    View FooterView;
    Map urlMap;

    public void setTaskStatus(int taskStatus) {
        this.taskStatus = taskStatus;
    }

    private int taskStatus = -1;//订单状态

    public LifeRewardOnLineUpload_OtherAdapter(Context context) {
        this.context = context;
        list = new ArrayList();
        inflater = LayoutInflater.from(context);
        urlMap = new HashMap();
    }

    public void addData(LifeRewardOnlineDetailInfo.Result.Receiver info) {
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
            LifeRewardOnlineDetailInfo.Result.Receiver info = list.get(position - 1);
            holder.tv_NickName_MHDA.setText(info.nickname);
            switch (info.status) {
                case 0:
                    Log.i("test","订单状态：==============================="+taskStatus);
                    if(taskStatus == 2){
                        holder.tv_TaskFunction_MHDA.setText(R.string.order_closed);
                    }else {
                        if (rewardedNum > 0 && rewardedNum >= receiver_limit) {//已打赏人已满
                            holder.tv_TaskFunction_MHDA.setText(R.string.order_rewarded_number_fulled);
                        } else {
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
                    if(taskStatus == 2){
                        holder.tv_TaskFunction_MHDA.setText(R.string.order_closed);
                    }else {
                        holder.tv_TaskFunction_MHDA.setText(R.string.order_not_pass);
                    }
                    holder.tv_time_MHDA.setText(TurnIntoTime.getCreateTime(info.stop_time));
                    break;
            }
            holder.tv_UploadCotent_MHDA.setText(info.content);
            //设置头像
            QiniuUtils.setAvatarByIdFrom7Niu(context,holder.cimg_Avatar_MHDA, info.portrait);

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
                    if (getItemViewType(position) == 1) {
                        return gridLayoutManager.getSpanCount();
                    } else {
                        return 1;
                    }
                }
            });
        }
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
