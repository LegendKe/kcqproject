package com.djx.pin.improve.base.adapter;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.djx.pin.R;
import com.djx.pin.utils.myutils.ConstantUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 对recyclerview.adapter进行简易封装
 * Created by 柯传奇 on 2016/12/1 0001.
 */

public abstract class BaseRecyAdapter<T> extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private final RecyclerView.LayoutParams layoutParams;
    private List<T> lists;
    protected Context context;
    private int state;
    private boolean isAddHeadView = false;//默认不添加头



    public BaseRecyAdapter(Context context) {
        this.context = context;
        lists = new ArrayList<>();
        layoutParams = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    @Override
    public int getItemCount() {
        if(!isAddHeadView){
            return lists.size() == 0 ? 0 : (lists.size() + 1);//当没数据时隐藏footView
        }else {
            return lists.size() == 0 ? 1 : (lists.size() + 2);//当没数据时隐藏footView
        }
    }

    public List<T> getLists() {
        return lists;
    }

    public void addAll(List<T> lists) {
        this.lists.clear();
        this.lists.addAll(lists);
        notifyDataSetChanged();
    }
    public void addMoreData(List<T> list) {
        this.lists.addAll(list);
        notifyDataSetChanged();
    }
    public T getItem(int position){
        if(!isAddHeadView){
            return lists.get(position);
        }else {
            return lists.get(position-1);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if(!isAddHeadView){
            return position == lists.size() ? ConstantUtils.FOOTVIEW_TYPE : ConstantUtils.COMMON_TYPE;
        }else {
           if(position == 0){
               return ConstantUtils.HEADVIEW_TYPE;
           }else if(position == lists.size() +1){
               return ConstantUtils.FOOTVIEW_TYPE;
           }else {
               return ConstantUtils.COMMON_TYPE;
           }
        }
    }

    /**
     * 1-0-0设置是否添加headview
     * @param isAddHeadView
     */
    public void setIsAddHeadView(boolean isAddHeadView) {
        this.isAddHeadView = isAddHeadView;
    }
    /**
     * 1-0-1设置headview的布局
     * 当需添加头时重写该方法
     * @return
     */
    protected RecyclerView.ViewHolder setHeadViewHolder(){
        return null;
    };

    /**
     * 1-1设置item的布局
     * @return
     */
    protected abstract CommonViewHolder setCommonViewHolder();

    /**
     * 1-2设置item数据
     * @param holder
     * @param bean
     */
    protected abstract void onBindCommonViewHolder(RecyclerView.ViewHolder holder,T bean);
    /**
     * 1-3设置加载状态
     * @param state
     */
    public void setState(int state) {
        this.state = state;
    }

    /**
     * 1-4设置adapter的item点击事件
     * @param itemClickListener
     */
    public void setOnItemClickListener(OnItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        if (viewType == ConstantUtils.FOOTVIEW_TYPE) {
            View view = View.inflate(context, R.layout.list_cell_footer, null);
            view.setLayoutParams(layoutParams);
            return new FootViewHolder(view);
        }else if(viewType == ConstantUtils.COMMON_TYPE){
            return setCommonViewHolder();
        }else if(viewType == ConstantUtils.HEADVIEW_TYPE){
            return setHeadViewHolder();
        }
        return null;
    }


    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
       if(getItemViewType(position) == ConstantUtils.FOOTVIEW_TYPE){//footview
           FootViewHolder footView = (FootViewHolder) holder;
           switch (state) {
               case ConstantUtils.STATE_LOAD_MORE:
                   //footView.ll_footView.setVisibility(View.VISIBLE);
                   footView.progress.setVisibility(View.VISIBLE);
                   footView.text.setText("加载更多...");
                   break;
               case ConstantUtils.STATE_NO_MORE:
                   footView.progress.setVisibility(View.GONE);
                   footView.text.setVisibility(View.VISIBLE);
                   footView.text.setText("无更多数据");
                   break;
               case ConstantUtils.STATE_EMPTY_ITEM:
                   footView.progress.setVisibility(View.GONE);
                   footView.text.setText("暂无内容");
                   break;
               case ConstantUtils.STATE_NETWORK_ERROR:
                   footView.progress.setVisibility(View.GONE);
                   footView.text.setVisibility(View.VISIBLE);
                   footView.text.setText("加载出错了");
                   break;
               case 0:
                   break;
           }
       }else if(getItemViewType(position) == ConstantUtils.COMMON_TYPE){
           T bean;
           if(isAddHeadView){
               bean = lists.get(position-1);
           }else {
               bean = lists.get(position);
           }
           onBindCommonViewHolder(holder,bean);
       }
    }


    /**
     * CommonViewHolder
     */
    public class CommonViewHolder extends RecyclerView.ViewHolder {

        public CommonViewHolder(View itemView) {
            super(itemView);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(itemClickListener != null){
                        itemClickListener.onItemClick(getLayoutPosition());
                    }
                }
            });
        }
    }

    /**
     * footViewHolder
     */
    public class FootViewHolder extends RecyclerView.ViewHolder {
        ProgressBar progress;
        TextView text;
        public FootViewHolder(View itemView) {
            super(itemView);
            progress = (ProgressBar) itemView.findViewById(R.id.progressbar);
            text = (TextView) itemView.findViewById(R.id.tv_name);
        }
    }


    OnItemClickListener itemClickListener;
    public interface OnItemClickListener {
        void onItemClick(int pos);
    }

}
