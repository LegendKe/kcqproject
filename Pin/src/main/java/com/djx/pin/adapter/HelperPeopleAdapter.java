package com.djx.pin.adapter;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.djx.pin.R;
import com.djx.pin.beans.SOSHelpInfo;
import com.djx.pin.myview.CircleImageView;
import com.djx.pin.utils.DateUtils;
import com.djx.pin.utils.QiniuUtils;

import java.util.Date;

/**
 * Created by Administrator on 2016/6/23.
 */
public class HelperPeopleAdapter extends MyBaseAdapter<SOSHelpInfo> implements View.OnClickListener {

    private AvatarLisenner avatarLisenner;
    public HelperPeopleAdapter(Context context,AvatarLisenner avatarLisenner) {
        super(context);
        this.avatarLisenner=avatarLisenner;
    }

    @Override
    public void addData(SOSHelpInfo sosHelpInfo) {
        boolean isSame=false;
        for(int i=0;i<list.size();i++){
            if(sosHelpInfo.id==list.get(i).id){
                isSame=true;
            }
        }
        if(!isSame){
            list.add(sosHelpInfo);
        }
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
            converView.setTag(vh);
        }else {
            vh= (ViewHolder) converView.getTag();
        }
        SOSHelpInfo info=getItem(position);
        QiniuUtils.setAvatarByIdFrom7Niu(context,vh.img_portrait,info.portrait);
        vh.tv_description.setText(info.description);
        vh.tv_address.setText(info.address);
        Log.e("distance","-------------info.distance--"+info.distance);
        Log.e("distance","-------------info.tv_address--"+info.address);

        vh.tv_distance.setText((int)info.distance+"KM");

        vh.tv_price.setText("￥" + info.price+"");
        vh.tv_start_time.setText(DateUtils.formatDate(new Date(info.start_time),DateUtils.yyyyMMDD)+"");
        vh.img_portrait.setOnClickListener(this);
        vh.img_portrait.setTag(position);
        vh.tv_userName.setText(info.nickname);
        //如果是SOS求助信息则字体颜色为蓝色
        if(info.isSOS){
            vh.tv_description.setTextColor(context.getResources().getColor(R.color.text_color_focused));
            vh.tv_address.setTextColor(context.getResources().getColor(R.color.text_color_focused));
            vh.tv_distance.setTextColor(context.getResources().getColor(R.color.text_color_focused));
            vh.tv_start_time.setTextColor(context.getResources().getColor(R.color.text_color_focused));
        }else {
            vh.tv_description.setTextColor(context.getResources().getColor(R.color.text_color_black));
            vh.tv_address.setTextColor(context.getResources().getColor(R.color.text_color_normal));
            vh.tv_distance.setTextColor(context.getResources().getColor(R.color.text_color_normal));
            vh.tv_start_time.setTextColor(context.getResources().getColor(R.color.text_color_normal));
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

    /**
     * 获取头像id
     * @param id
     */
    public void getPortraitUrl(String id){
    }
}
