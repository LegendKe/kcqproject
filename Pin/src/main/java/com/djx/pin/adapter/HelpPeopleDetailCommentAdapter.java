/*
package com.djx.pin.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.baidu.platform.comapi.map.L;
import com.djx.pin.R;
import com.djx.pin.beans.HelpPeopleDetailCommentInfo;
import com.djx.pin.myview.CircleImageView;
import com.djx.pin.utils.DateUtils;
import com.djx.pin.utils.LogUtil;
import com.djx.pin.utils.QiniuUtils;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

*/
/**
 * Created by Administrator on 2016/6/15.
 *//*

public class HelpPeopleDetailCommentAdapter extends MyBaseAdapter<HelpPeopleDetailCommentInfo.COMMENT> implements View.OnClickListener {


    OnCommentAvatarListener commentAvatarListener;
    public HelpPeopleDetailCommentAdapter(Context context,OnCommentAvatarListener commentAvatarListener) {
        super(context);
        this.commentAvatarListener=commentAvatarListener;

    }

    @Override
    public void addData(HelpPeopleDetailCommentInfo.COMMENT comment) {
        boolean isSame=false;
        for(int i=0;i<list.size();i++){
            if(comment.id.equals(list.get(i).id)){
                isSame=true;
            }
        }
        if(!isSame){
            list.add(comment);
        }
    }

    @Override
    public View getView(int position, View converView, ViewGroup parent) {

        ViewHodler vh = null;
        if (converView == null) {
            vh = new ViewHodler();
            converView = inflater.inflate(R.layout.lv_item_comment, null);
            vh.civ_avatar = (CircleImageView) converView.findViewById(R.id.civ_avatar);
            vh.tv_NickName = (TextView) converView.findViewById(R.id.tv_NickName);
            vh.tv_time = (TextView) converView.findViewById(R.id.tv_time);
            vh.tv_ShareCotent = (TextView) converView.findViewById(R.id.tv_CommentContent);
            converView.setTag(vh);
        } else {
            vh = (ViewHodler) converView.getTag();
        }
        HelpPeopleDetailCommentInfo.COMMENT info = getItem(position);
        vh.tv_NickName.setText(info.nickname);
        vh.tv_ShareCotent.setText(info.content);
        vh.tv_time.setText(DateUtils.formatDate(new Date(info.create_time), DateUtils.LOCALE_DATE_FORMAT));
        vh.civ_avatar.setOnClickListener(this);
        vh.civ_avatar.setTag(position);

        QiniuUtils.setAvatarByIdFrom7Niu(context,vh.civ_avatar,info.portrait);

        List<ImageView> imageViewList = new ArrayList<ImageView>();
        imageViewList.add(vh.civ_avatar);
        List<String> idList = new ArrayList<String>();
        idList.add(info.portrait);
        try {
            getImageViewUrl(imageViewList, 1, idList, 1);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return converView;
    }

    @Override
    public void onClick(View v) {
        commentAvatarListener.setOnCommentAvatarListener(v,1);

    }

    public class ViewHodler {
        private CircleImageView civ_avatar;
        private TextView tv_NickName;
        private TextView tv_time;
        private TextView tv_ShareCotent;

    }

    public interface OnCommentAvatarListener {
        void setOnCommentAvatarListener(View view, int type);
    }

}
*/
