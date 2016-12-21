package com.djx.pin.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.djx.pin.R;
import com.djx.pin.beans.CivilizationDetailCommentInfo;
import com.djx.pin.beans.CivilizationDetailShareInfo;
import com.djx.pin.beans.ShareTextInfo;
import com.djx.pin.myview.CircleImageView;
import com.djx.pin.utils.TurnIntoTime;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/6/15.
 *
 */
//CivilizationDetailShareInfo.Result.Share
public class CivilizationDetailShareAdapter extends MyBaseAdapter<CivilizationDetailShareInfo.Result.Share> implements View.OnClickListener {

    OnShareAvatarListener onShareAvatarListener;
    public CivilizationDetailShareAdapter(Context context,OnShareAvatarListener onShareAvatarListener) {
        super(context);
        this.onShareAvatarListener=onShareAvatarListener;
    }

    @Override
    public View getView(int position, View converView, ViewGroup parent) {

        ViewHodler vh = null;
        if (converView == null) {
            vh = new ViewHodler();
            converView = inflater.inflate(R.layout.lv_item_share, null);
            vh.avatar = (CircleImageView) converView.findViewById(R.id.cimg);
            vh.userName = (TextView) converView.findViewById(R.id.tv_NickName);
            vh.time = (TextView) converView.findViewById(R.id.tv_time);
            vh.where = (TextView) converView.findViewById(R.id.tv_ShareCotent);
            converView.setTag(vh);
        } else {
            vh = (ViewHodler) converView.getTag();
        }
        CivilizationDetailShareInfo.Result.Share info = getItem(position);

        vh.userName.setText(info.nickname);

        vh.userName.setText(info.nickname);
        if (info.type == 1) {
            vh.where.setText("分享到微信");
        } else if (info.type == 2) {
            vh.where.setText("分享到QQ");
        } else if (info.type == 3) {
            vh.where.setText("分享到微博");
        }


        vh.time.setText(TurnIntoTime.getCreateTime(info.create_time));

        List<ImageView> imageViewList = new ArrayList<ImageView>();
        imageViewList.add(vh.avatar);
        List<String> idList = new ArrayList<String>();
        idList.add(info.portrait);
        try {
            getImageViewUrl(imageViewList, 1, idList, 1);
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
        onShareAvatarListener.setOnShareAvatarListener(v,1);
    }

    public class ViewHodler {
        private CircleImageView avatar;
        private TextView userName;
        private TextView time;
        private TextView where;

    }

    public interface OnShareAvatarListener{
        void setOnShareAvatarListener(View view,int type);
    }
}
