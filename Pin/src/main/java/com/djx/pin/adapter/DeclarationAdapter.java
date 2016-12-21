
package com.djx.pin.adapter;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.djx.pin.R;
import com.djx.pin.beans.AppealDetailEntity;
import com.djx.pin.utils.QiniuUtils;
import com.djx.pin.utils.TDevice;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * 1.回调item的点击事件
 * 1.创建一个接口，定义一个抽象方法onItemClick(int position,View itemView);
 * public interface OnItemClick{
 * onItemClick(int position,View itemView);
 * }
 * 2.在适配器类中声明一个属性
 * private OnItemClickListener onItemClickListener;
 * 3.实例化这个接口的属性
 * <p/>
 * 4.在ViewHolder中给itemView设置点击事件，并在其事件方法中调用
 * onItemClickListener.onItemClick(getLayoutPosition(),view);
 */
public class DeclarationAdapter extends RecyclerView.Adapter<DeclarationAdapter.ViewHolder> implements View.OnClickListener{

    private final List<AppealDetailEntity.ResultBean.AppealMediaBean> receiver_appeal_media;
    private final List<AppealDetailEntity.ResultBean.AppealMediaBean> publisher_appeal_media;
    private AppealDetailEntity.ResultBean resultBean;
    private LayoutInflater inflater;
    private Context context;
    private int type =0;


    public DeclarationAdapter(Context context,AppealDetailEntity.ResultBean resultBean) {
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        this.resultBean = resultBean;
        publisher_appeal_media = resultBean.getPublisher_appeal_media();
        receiver_appeal_media = resultBean.getReceiver_appeal_media();
    }


    @Override
    public int getItemCount() {
        if(resultBean.getPublisher_appeal() != null && resultBean.getPublisher_appeal().length()>0){
            type =1;
            if(resultBean.getReceiver_appeal() != null && resultBean.getReceiver_appeal().length() >0){
                return 2;
            }else {
                return 1;
            }

        }
        else if(resultBean.getReceiver_appeal()!= null && resultBean.getReceiver_appeal().length()>0){
            type = 2;
            return 1;
        }
        return 0;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(inflater.inflate(R.layout.item_activity_declaration, null));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(TDevice.dip2px(context,70), ViewGroup.LayoutParams.MATCH_PARENT);
        layoutParams.setMargins(12, 0, 12, 0);
        int size = 0;
        long time = 0;

        if (position == 0) {//发布者
            int back_color = 0xffdfdfdf;
            holder.cardView.setCardBackgroundColor(back_color);
            holder.tv_name.setText("发单者("+resultBean.getPublisher_nickname()+")拒绝了付款");
            holder.tv_reason.setText("理由："+resultBean.getPublisher_appeal());
            time = resultBean.getPublisher_appeal_time();


            int color_gray = 0xff666666;
            int color_line = 0xffaaaaaa;
            holder.tv_name.setTextColor(color_gray);
            holder.tv_reason.setTextColor(color_gray);
            holder.tv_time.setTextColor(color_gray);
            holder.view_line.setBackgroundColor(color_line);
            holder.about_document.setTextColor(color_gray);

            //设置图片
            size = publisher_appeal_media.size();
            for (int i = 0; i < size; i++) {
                String media_id = publisher_appeal_media.get(i).getMedia_id();
                ImageView imageView = new ImageView(context);
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                imageView.setLayoutParams(layoutParams);
                QiniuUtils.setImageViewByIdFrom7Niu(context,imageView,media_id,240,240,null);
                holder.ll_imgs.addView(imageView);
            }
        }

        if (position == 1) {//接单者
            holder.tv_name.setText("接单者("+resultBean.getReceiver_nickname()+")提起申诉");
            holder.tv_reason.setText("理由："+resultBean.getReceiver_appeal());
            time = resultBean.getReceiver_appeal_time();

            //设置图片
            size = receiver_appeal_media.size();
            for (int i = 0; i < size; i++) {
                String media_id = receiver_appeal_media.get(i).getMedia_id();
                ImageView imageView = new ImageView(context);
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                imageView.setLayoutParams(layoutParams);
                QiniuUtils.setImageViewByIdFrom7Niu(context,imageView,media_id,240,240,null);
                holder.ll_imgs.addView(imageView);
                imageView.setTag(i);
                imageView.setOnClickListener(this);//点击图片打开
            }
        }
        if(size > 0){//如果有图片,则显示
            holder.ll_imgs.setVisibility(View.VISIBLE);
        }
        Date d = new Date(time);
        SimpleDateFormat sdf = new SimpleDateFormat("MM-dd hh:mm");//"yyyy-MM-dd hh:mm:ss
        holder.tv_time.setText(sdf.format(d));//设置时间


    }

  

    class ViewHolder extends RecyclerView.ViewHolder {

        private TextView tv_name, tv_reason, tv_time,about_document;
        private CardView cardView;
        private View view_line;
        private LinearLayout ll_imgs;

        public ViewHolder(View itemView) {
            super(itemView);
            tv_time = ((TextView) itemView.findViewById(R.id.item_tv_time_declaration));
            tv_name = ((TextView) itemView.findViewById(R.id.item_name_declaration));
            tv_reason = ((TextView) itemView.findViewById(R.id.item_reason_declaration));
            ll_imgs = ((LinearLayout) itemView.findViewById(R.id.ll_imgs));
            
            cardView = ((CardView) itemView.findViewById(R.id.cardview_declaration));
            about_document = ((TextView) itemView.findViewById(R.id.item_tv_ducument_declaration));
            view_line = itemView.findViewById(R.id.view_line);
        }
    }

    
    /**
     * Called when a view has been clicked.
     *
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v) {
        int pos = (int) v.getTag();
        ItemIMGClickListener.onIMGClick(pos);
    }
    
    private ItemIMGClickListener ItemIMGClickListener;
    public interface ItemIMGClickListener{
        void onIMGClick(int pos);
    }

    public void setItemIMGClickListener(ItemIMGClickListener ItemIMGClickListener) {
        this.ItemIMGClickListener = ItemIMGClickListener;
    }

}
