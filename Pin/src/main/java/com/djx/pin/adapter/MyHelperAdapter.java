/*
package com.djx.pin.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.djx.pin.R;
import com.djx.pin.beans.MyHelperInfo;
import com.djx.pin.myview.CircleImageView;

*/
/**
 * Created by Administrator on 2016/6/29.
 *//*

public class MyHelperAdapter extends MyBaseAdapter<MyHelperInfo> implements View.OnClickListener {

    public OnFunctionChose getOnFunctionChose() {
        return onFunctionChose;
    }

    public void setOnFunctionChose(OnFunctionChose onFunctionChose) {
        this.onFunctionChose = onFunctionChose;
    }

    private OnFunctionChose onFunctionChose;

    public MyHelperAdapter(Context context, OnFunctionChose onFunctionChose) {
        super(context);
        this.onFunctionChose = onFunctionChose;
    }

    @Override
    public View getView(int position, View converView, ViewGroup parent) {
        ViewHolder vh;
        if (converView == null) {
            vh = new ViewHolder();
            converView = inflater.inflate(R.layout.lv_intem_myhelper, null);
            vh.cimg_Avatar_MyHelper = (CircleImageView) converView.findViewById(R.id.cimg_Avatar_MyHelper);
            vh.tv_Massage_MyHelper = (TextView) converView.findViewById(R.id.tv_Massage_MyHelper);
            vh.tv_Reward_MyHelper = (TextView) converView.findViewById(R.id.tv_Reward_MyHelper);
            vh.tv_HelperTime_MyHelper = (TextView) converView.findViewById(R.id.tv_HelperTime_MyHelper);

            converView.setTag(vh);
        } else {
            vh = (ViewHolder) converView.getTag();
        }
        MyHelperInfo info = getItem(position);
        vh.cimg_Avatar_MyHelper.setImageResource(info.getAvatarId());
        vh.tv_HelperTime_MyHelper.setText(info.getTime());
        vh.tv_Massage_MyHelper.setText(info.getMassage());
        vh.tv_Reward_MyHelper.setText("赏" + info.getReward());



        return converView;
    }

    //参数：1,申诉维权；2，选择接单人数；3，确认完成
    @Override
    public void onClick(View v) {
        switch (v.getId()) {

        }
    }


    public class ViewHolder {
        private CircleImageView cimg_Avatar_MyHelper;
        private TextView tv_Massage_MyHelper;
        private TextView tv_Reward_MyHelper;

        private TextView tv_HelperTime_MyHelper;

    }

    */
/**
     * 点击回调接口
     *//*

    public interface OnFunctionChose {
        void setOnFunctionChose(int which, View v);
    }
}
*/
