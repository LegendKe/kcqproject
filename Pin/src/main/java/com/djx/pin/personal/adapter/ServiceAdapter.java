package com.djx.pin.personal.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.djx.pin.R;
import com.djx.pin.beans.ServiceEntity;
import com.djx.pin.improve.common.PhotoShowActivity;
import com.djx.pin.utils.LogUtil;
import com.djx.pin.utils.QiniuUtils;
import com.djx.pin.widget.CustomImageView;
import com.djx.pin.widget.NineGridLayout;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by 柯传奇 on 2016/10/21 0021.
 */

public class ServiceAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private boolean writeHide = false;
    private List<ServiceEntity.ServiceBean> lists;
    private Context context;
    private ItemClickListener itemClickListener;
    private static final int TYPE_ITEM =0;  //普通Item View
    private static final int TYPE_FOOTER = 1;  //顶部FootView
    //上拉加载更多
    public static final int  PULLUP_LOAD_MORE=0;
    //正在加载中
    public static final int  LOADING_MORE=1;
    //上拉加载更多状态-默认为0
    private int load_more_status=0;
    private SkillModifyListener skillModifyListener;

    public ServiceAdapter(Context context) {
        this.context = context;
    }

    public void setWriteHide(boolean writeHide) {
        this.writeHide = writeHide;
    }

    public void addAll(List<ServiceEntity.ServiceBean> lists){
        this.lists = lists;
        notifyDataSetChanged();
    }
    public void addMoreData(List<ServiceEntity.ServiceBean> list) {
        this.lists.addAll(list);
        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(viewType == TYPE_ITEM){
            return new ItemViewHolder(View.inflate(context, R.layout.item_activity_service,null));
        }else if(viewType==TYPE_FOOTER){
            View foot_view =View.inflate(context,R.layout.recycler_load_more_layout,null);
            //这边可以做一些属性设置，甚至事件监听绑定
            //view.setBackgroundColor(Color.RED);
            FootViewHolder footViewHolder=new FootViewHolder(foot_view);
            return footViewHolder;
        }
        return null;
    }


    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {

        if(holder instanceof ItemViewHolder){
            final ItemViewHolder holder1 = (ItemViewHolder) holder;
            final ServiceEntity.ServiceBean serviceBean = lists.get(position);
            holder1.tv_title.setText(serviceBean.type);
            holder1.tv_description.setText(serviceBean.skill_descr);

            DecimalFormat df = new DecimalFormat("###.00");
            String money = df.format(serviceBean.price);
            holder1.tv_money.setText("¥"+money+" /次");
            //设置图片
            List<ServiceEntity.MEDIA> medias = serviceBean.media;


            if(medias !=null && medias.size() > 0){
                if(medias.size() == 1){//一张图布局
                    LogUtil.e("-------holder.iv_oneImage,medias.get(0).getMedia_id()--"+medias.get(0).getMedia_id());
                    QiniuUtils.setImageViewByIdFrom7Niu(context,holder1.iv_oneImage,medias.get(0).getMedia_id(),900,600,null);
                    holder1.nineGridLayout.setVisibility(View.GONE);
                    holder1.iv_oneImage.setVisibility(View.VISIBLE);

                    holder1.iv_oneImage.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(context, PhotoShowActivity.class);
                            intent.putExtra("CURRENT_POS",0);
                            String media_id = serviceBean.media.get(0).getMedia_id();
                            ArrayList<String> list = new ArrayList<>();
                            list.add(media_id);
                            intent.putStringArrayListExtra("IDS", list);
                            context.startActivity(intent);
                        }
                    });

                }else {//多张图
                    final ArrayList<String> ids = new ArrayList<>();
                    for (int i = 0; i < serviceBean.media.size(); i++) {
                        String media = serviceBean.media.get(i).getMedia_id();
                        ids.add(media);
                    }
                    QiniuUtils.set9GridByIdsFrom7Niu(context, ids,serviceBean.id,holder1.nineGridLayout);
                    holder1.iv_oneImage.setVisibility(View.GONE);
                    holder1.nineGridLayout.setVisibility(View.VISIBLE);
                    holder1.nineGridLayout.setImageShowListener(new NineGridLayout.ImageShowListener() {
                        @Override
                        public void imageShow(int imgPos,ArrayList<CustomImageView> imageViews) {
                            if (ids != null) {
                                Intent intent = new Intent(context, PhotoShowActivity.class);
                                intent.putExtra("CURRENT_POS", imgPos);
                                intent.putStringArrayListExtra("IDS", ids);
                                context.startActivity(intent);
                            }
                        }
                    });
                }
            }else {
                holder1.iv_oneImage.setVisibility(View.GONE);
                holder1.nineGridLayout.setVisibility(View.GONE);
            }
        }else if(holder instanceof FootViewHolder){
            FootViewHolder footViewHolder=(FootViewHolder)holder;
            switch (load_more_status){
                case PULLUP_LOAD_MORE:
                    footViewHolder.foot_view_item_tv.setText("上拉加载更多...");
                    break;
                case LOADING_MORE:
                    footViewHolder.foot_view_item_tv.setText("正在加载更多数据...");
                    break;
            }
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
    public int getItemViewType(int position) {
        return TYPE_ITEM;
    }

    public ServiceEntity.ServiceBean getItem(int position){
        return lists.get(position);
    }

    public void remove(int position) {
        lists.remove(position);
        notifyItemRemoved(position);
    }

    public void addItemData(int pos) {
        lists.add(lists.get(pos));
    }
    /**
     * //上拉加载更多
     * PULLUP_LOAD_MORE=0;
     * //正在加载中
     * LOADING_MORE=1;
     * //加载完成已经没有更多数据了
     * NO_MORE_DATA=2;
     * @param status
     */
    public void changeMoreStatus(int status){
        load_more_status=status;
        notifyDataSetChanged();
    }

    public void setSkillModifyListener(SkillModifyListener skillModifyListener) {
        this.skillModifyListener = skillModifyListener;
    }

    /**
     * 底部FootView布局
     */
    public class FootViewHolder extends  RecyclerView.ViewHolder{
        private TextView foot_view_item_tv;
        public FootViewHolder(View view) {
            super(view);
            foot_view_item_tv=(TextView)view.findViewById(R.id.foot_view_item_tv);
        }
    }


    class ItemViewHolder extends RecyclerView.ViewHolder{

      /*  @Bind(R.id.tv_title)*/
        TextView tv_title;
       /* @Bind(R.id.tv_description)*/
        TextView tv_description;
      /*  @Bind(R.id.imgs_9grid_layout)*/
        NineGridLayout nineGridLayout;
      /*  @Bind(R.id.iv_oneimage)*/
        ImageView iv_oneImage;
     /*   @Bind(R.id.iv_write)*/
        ImageView iv_write;
        TextView tv_money;

        public ItemViewHolder(View itemView) {
            super(itemView);
           /* ButterKnife.bind(this, itemView);*/

            tv_title = ((TextView) itemView.findViewById(R.id.tv_title));
            tv_description = ((TextView) itemView.findViewById(R.id.tv_description));
            nineGridLayout =  ((NineGridLayout) itemView.findViewById(R.id.imgs_9grid_layout));
            iv_oneImage = ((ImageView) itemView.findViewById(R.id.iv_oneimage));
            iv_write = ((ImageView) itemView.findViewById(R.id.iv_write));
            tv_money = ((TextView) itemView.findViewById(R.id.tv_money));

            iv_write.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ServiceEntity.ServiceBean serviceBean = lists.get(getLayoutPosition());
                    if(skillModifyListener!= null){
                        skillModifyListener.skillModify(serviceBean);
                    }
                }
            });

            if(writeHide){
                iv_write.setVisibility(View.INVISIBLE);
            }

        }

    }



    public interface SkillModifyListener{
        void skillModify(ServiceEntity.ServiceBean serviceBean);
    }

    public void addOnItemClickListener(ItemClickListener itemClickListener)
    {
        this.itemClickListener = itemClickListener;
    }
    /**
     * 声明一个item的点击事件接口
     */
    public interface ItemClickListener
    {
        void onItemClick(int position,View itemView);
    }


}
