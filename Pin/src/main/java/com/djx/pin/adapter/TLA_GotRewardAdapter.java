package com.djx.pin.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.djx.pin.R;
import com.djx.pin.beans.GotRewardInfo;
import com.djx.pin.myview.CircleImageView;

/**
 * Created by Administrator on 2016/6/29.
 */
public class TLA_GotRewardAdapter extends MyBaseAdapter<GotRewardInfo> {
    public TLA_GotRewardAdapter(Context context) {
        super(context);
    }

    @Override
    public View getView(int position, View converView, ViewGroup parent) {
        ViewHolder vh;
        if(converView==null){
            vh=new ViewHolder();
            converView=inflater.inflate(R.layout.lv_intem_gotreward,null);
            vh.cimg_Avatar_GotReward= (CircleImageView) converView.findViewById(R.id.cimg_Avatar_GotReward);
            vh.tv_Massage_GotReward= (TextView) converView.findViewById(R.id.tv_Massage_GotReward);
            vh.tv_Reward_GotReward= (TextView) converView.findViewById(R.id.tv_Reward_GotReward);
            vh.tv_RewardState_GotReward= (TextView) converView.findViewById(R.id.tv_RewardState_GotReward);
            vh.tv_Time_GotReward= (TextView) converView.findViewById(R.id.tv_Time_GotReward);
            converView.setTag(vh);
        }else {
            vh= (ViewHolder) converView.getTag();
        }
        GotRewardInfo info =getItem(position);
        vh.cimg_Avatar_GotReward.setImageResource(info.getAvatarId());
        vh.tv_Reward_GotReward.setText("赏金+"+info.getReward());
        vh.tv_Massage_GotReward.setText(info.getMassage());
        vh.tv_RewardState_GotReward.setText(info.getRewardState());
        vh.tv_Time_GotReward.setText(info.getTime());
        return converView;
    }

    public class ViewHolder{
        private CircleImageView cimg_Avatar_GotReward;
        private TextView tv_Massage_GotReward;
        private TextView tv_Reward_GotReward;
        private TextView tv_RewardState_GotReward;
        private TextView tv_Time_GotReward;
    }
}
