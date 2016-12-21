package com.djx.pin.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.djx.pin.R;
import com.djx.pin.beans.ChosePeopleInfo;
import com.djx.pin.myview.CircleImageView;

/**
 * Created by Administrator on 2016/7/2 0002.
 */
public class PopChosePeopleAdapter extends MyBaseAdapter<ChosePeopleInfo> implements CompoundButton.OnCheckedChangeListener {

    private OnChosePeople onChosePeople;
    public PopChosePeopleAdapter(Context context,OnChosePeople onChosePeople) {
        super(context);
        this.onChosePeople=onChosePeople;
    }

    @Override
    public View getView(int position, View converView, ViewGroup parent) {

        ViewHolder vh;
        if (converView == null) {
            vh = new ViewHolder();
            converView = inflater.inflate(R.layout.lv_intem_chosepeople, null);
            vh.cimg_Avatar_MHA = (CircleImageView) converView.findViewById(R.id.cimg_Avatar_MHA);
            vh.tv_UserName_MHA = (TextView) converView.findViewById(R.id.tv_UserName_MHA);
            vh.tv_Location_MHA = (TextView) converView.findViewById(R.id.tv_Location_MHA);
            vh.cb_Chosed_MHA = (CheckBox) converView.findViewById(R.id.cb_Chosed_MHA);
            converView.setTag(vh);
        }else {
            vh= (ViewHolder) converView.getTag();
        }
        ChosePeopleInfo info =getItem(position);
        vh.cimg_Avatar_MHA.setImageResource(info.getAvatarId());
        vh.tv_UserName_MHA.setText(info.getUserName());
        vh.tv_Location_MHA.setText(info.getLocation());
        vh.cb_Chosed_MHA.setOnCheckedChangeListener(this);
        vh.cb_Chosed_MHA.setTag(position);
        return converView;
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (isChecked){
            onChosePeople.setOnChosePeopleListener(buttonView);
        }
    }


    public class ViewHolder {
        private CircleImageView cimg_Avatar_MHA;
        private TextView tv_UserName_MHA;
        private TextView tv_Location_MHA;
        private CheckBox cb_Chosed_MHA;
    }
    public interface OnChosePeople{
        void setOnChosePeopleListener(View v);
    }
}
