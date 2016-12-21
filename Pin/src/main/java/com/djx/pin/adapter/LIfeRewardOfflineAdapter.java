package com.djx.pin.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.djx.pin.R;
import com.djx.pin.beans.HelperPeopleInfo;
import com.djx.pin.beans.SOSHelpInfo;
import com.djx.pin.myview.CircleImageView;
import com.djx.pin.utils.DateUtils;
import com.djx.pin.utils.DeviceUtil;
import com.djx.pin.utils.LogUtil;
import com.djx.pin.utils.QiniuUtils;

import java.io.UnsupportedEncodingException;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by Administrator on 2016/6/23.
 */
public class LIfeRewardOfflineAdapter extends MyBaseAdapter<HelperPeopleInfo.LIST> implements View.OnClickListener {

    private AvatarLisenner avatarLisenner;
    public LIfeRewardOfflineAdapter(Context context, AvatarLisenner avatarLisenner) {
        super(context);
        this.avatarLisenner=avatarLisenner;
    }

    @Override
    public View getView(int position, View converView, ViewGroup parent) {
       ViewHolder vh;
        if (converView==null){
            vh=new ViewHolder();
            converView=inflater.inflate(R.layout.lv_intem_helperpeoplefragment,null);
            vh.img_portrait= (CircleImageView) converView.findViewById(R.id.img_portrait);
            vh.tv_description= (TextView) converView.findViewById(R.id.tv_description);
            vh.tv_address= (TextView) converView.findViewById(R.id.tv_address);
            vh.tv_distance= (TextView) converView.findViewById(R.id.tv_distance);
            vh.tv_price= (TextView) converView.findViewById(R.id.tv_price);
            vh.tv_start_time= (TextView) converView.findViewById(R.id.tv_start_time);
            vh.tv_userName = ((TextView) converView.findViewById(R.id.tv_userName));
            vh.ll_imgs = ((LinearLayout) converView.findViewById(R.id.ll_imgs));//图片

            converView.setTag(vh);
        }else {
            vh= (ViewHolder) converView.getTag();
        }
        HelperPeopleInfo.LIST info=getItem(position);
        //设置头像
        QiniuUtils.setAvatarByIdFrom7Niu(context,vh.img_portrait,info.portrait);
        vh.tv_description.setText(info.description);
        vh.tv_userName.setText(info.nickname);
        vh.tv_address.setText(info.address);
        vh.tv_distance.setText((int)info.distance+"KM");
        DecimalFormat format = new DecimalFormat("0.0");
        vh.tv_price.setText("￥" + format.format(info.price));
        vh.tv_start_time.setText(DateUtils.formatDate(new Date(info.start_time),DateUtils.yyyyMMDD)+"");
        vh.img_portrait.setOnClickListener(this);
        vh.img_portrait.setTag(position);

        //图片
        if(info.media!= null && info.media.size() > 0){
            int size = info.media.size();//图片数量
            List<HelperPeopleInfo.LIST.Media> media = info.media;
            if(size ==1){//一张图片时
                ImageView imageView = new ImageView(context);
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, DeviceUtil.getScreenHeightPX(context)/4);
                imageView.setLayoutParams(layoutParams);
                QiniuUtils.setAvatarByIdFrom7Niu(context,imageView,media.get(0).media_id);
                vh.ll_imgs.addView(imageView);
            }else if(size > 1){
                for (int i = 0; i < size && i < 4; i++) {
                    ImageView imageView = new ImageView(context);
                    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(DeviceUtil.getScreenWidthPX(context)/size,DeviceUtil.getScreenWidthPX(context)/size);
                    imageView.setLayoutParams(layoutParams);
                    QiniuUtils.setAvatarByIdFrom7Niu(context,imageView,media.get(i).media_id);
                    vh.ll_imgs.addView(imageView);
                }
            }
        }

        return converView;
    }

    @Override
    public void onClick(View v) {
        avatarLisenner.setOnAvatarLisenner(1,v);
    }

    public class ViewHolder{
        private CircleImageView img_portrait;
        private TextView tv_description;
        private TextView tv_address;
        private TextView tv_distance;
        private TextView tv_price;
        private TextView tv_start_time;
        private TextView tv_userName;
        private LinearLayout ll_imgs;

    }
    /**
     * 头像回调接口
     */
    public interface AvatarLisenner {
        /**
         * 头像回调方法
         */
        void setOnAvatarLisenner(int tag, View v);
    }


}
