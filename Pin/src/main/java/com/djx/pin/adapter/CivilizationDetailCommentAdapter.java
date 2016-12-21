package com.djx.pin.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.djx.pin.R;
import com.djx.pin.beans.CivilizationDetailCommentInfo;
import com.djx.pin.myview.CircleImageView;
import com.djx.pin.utils.TurnIntoTime;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/6/15.
 */
//CivilizationDetailCommentInfo.Result.Comment
public class CivilizationDetailCommentAdapter extends MyBaseAdapter<CivilizationDetailCommentInfo.Result.Comment> implements View.OnClickListener {
    OnCommentAvatarListener onCommentAvatarListener;

    public CivilizationDetailCommentAdapter(Context context, OnCommentAvatarListener onCommentAvatarListener) {
        super(context);
        this.onCommentAvatarListener = onCommentAvatarListener;
    }

    @Override
    public View getView(int position, View converView, ViewGroup parent) {

        ViewHodler vh = null;
        if (converView == null) {
            vh = new ViewHodler();
            converView = inflater.inflate(R.layout.lv_item_comment, null);
            vh.avatar = (CircleImageView) converView.findViewById(R.id.civ_avatar);
            vh.userName = (TextView) converView.findViewById(R.id.tv_NickName);
            vh.time = (TextView) converView.findViewById(R.id.tv_time);
            vh.comment = (TextView) converView.findViewById(R.id.tv_CommentContent);
            converView.setTag(vh);
        } else {
            vh = (ViewHodler) converView.getTag();
        }
        CivilizationDetailCommentInfo.Result.Comment info = getItem(position);

        vh.userName.setText(info.nickname);
        vh.comment.setText(info.content);

        vh.time.setText(TurnIntoTime.getCreateTime(info.create_time));

        List<ImageView> imageViewList = new ArrayList<ImageView>();
        imageViewList.add(vh.avatar);
        List<String> idList = new ArrayList<String>();
        idList.add(info.portrait);
        try {
            getOneImageViewUrl(vh.avatar, info.portrait, 1);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        vh.avatar.setTag(position);
        vh.avatar.setOnClickListener(this);
        return converView;
    }

    /**
     * Called when a view has been clicked.
     *
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v) {
        onCommentAvatarListener.setOnCommentAvatarListener(v,1);
    }

    public class ViewHodler {
        private CircleImageView avatar;
        private TextView userName;
        private TextView time;
        private TextView comment;
    }

    public interface OnCommentAvatarListener {
        void setOnCommentAvatarListener(View view, int type);
    }
}
