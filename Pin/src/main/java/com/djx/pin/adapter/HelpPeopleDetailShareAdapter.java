/*
package com.djx.pin.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.djx.pin.R;
import com.djx.pin.beans.HelpPeopleDetailShareInfo;
import com.djx.pin.myview.CircleImageView;
import com.djx.pin.utils.DateUtils;
import com.djx.pin.utils.LogUtil;

import java.io.UnsupportedEncodingException;
import java.util.Date;

*/
/**
 * Created by Administrator on 2016/6/15.
 *//*

public class HelpPeopleDetailShareAdapter extends MyBaseAdapter<HelpPeopleDetailShareInfo.SHARE> implements View.OnClickListener {

    OnShareAvatarListener shareAvatarListener;
    public HelpPeopleDetailShareAdapter(Context context,OnShareAvatarListener shareAvatarListener) {
        super(context);
        this.shareAvatarListener=shareAvatarListener;
    }


    @Override
    public void addData(HelpPeopleDetailShareInfo.SHARE share) {
        boolean isSame=false;
        for(int i=0;i<list.size();i++){
            if(share.id.equals(list.get(i).id)){
                isSame=true;
            }
        }
        if(!isSame){
            list.add(share);
        }
    }


    @Override
    public View getView(int position, View converView, ViewGroup parent) {

        ViewHodler viewHodler;
        if (converView == null) {
            converView = inflater.inflate(R.layout.lv_item_share, null);
            viewHodler = new ViewHodler();
            viewHodler.cimg = (CircleImageView) converView.findViewById(R.id.cimg);
            viewHodler.tv_NickName = (TextView) converView.findViewById(R.id.tv_NickName);
            viewHodler.tv_time = (TextView) converView.findViewById(R.id.tv_time);
            viewHodler.tv_ShareCotent = (TextView) converView.findViewById(R.id.tv_ShareCotent);
            converView.setTag(viewHodler);
        } else {
            viewHodler = (ViewHodler) converView.getTag();
        }
        HelpPeopleDetailShareInfo.SHARE info = getItem(position);
        try {
            getOneImageViewUrl(viewHodler.cimg, info.portrait, 1);
        } catch (UnsupportedEncodingException e) {
            LogUtil.e(context, "enter chatch:HelpPeopleDetailShareAdapter");
            e.printStackTrace();
        }
        viewHodler.tv_NickName.setText(info.nickname + "");
        viewHodler.tv_time.setText(DateUtils.formatDate(new Date(info.create_time), DateUtils.LOCALE_DATE_FORMAT));
        viewHodler.cimg.setTag(position);
        viewHodler.cimg.setOnClickListener(this);
        switch (info.type) {
            //分享微信
            case 1:
                viewHodler.tv_ShareCotent.setText(R.string.tv_share_weixin);
                break;
            //分享QQ
            case 2:
                viewHodler.tv_ShareCotent.setText(R.string.tv_share_qq);
                break;
            //分享微博
            case 3:
                viewHodler.tv_ShareCotent.setText(R.string.tv_share_sina);
                break;
            case 4:
                viewHodler.tv_ShareCotent.setText(R.string.tv_share_pin);
                break;
        }

        return converView;
    }

    @Override
    public void onClick(View v) {
        shareAvatarListener.setOnShareAvatarListener(v,1);
    }

    public class ViewHodler {
        private CircleImageView cimg;
        private TextView tv_NickName;
        private TextView tv_time;
        private TextView tv_ShareCotent;

    }

    public interface OnShareAvatarListener {
        void setOnShareAvatarListener(View view, int type);
    }



}
*/
