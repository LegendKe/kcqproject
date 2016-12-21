package com.djx.pin.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.djx.pin.beans.CivilizationInfo;
import com.djx.pin.myview.CircleImageView;

/**
 * Created by Administrator on 2016/6/14.
 */
public class LifeRewardListViewAdapter extends MyBaseAdapter<CivilizationInfo> implements View.OnClickListener {

    private int itemViewType;

    //实例化接口对象
    private ShareLisenner shareLisenner;
    private CommentLisenner commentLisenner;
    private ChatLisenner chatLisenner;
    private ImageViewLisenner imageViewLisenner;
    private CivilizationInfo lifeRewardInfo;
    private AvatarLisenner avatarLisenner;

    public LifeRewardListViewAdapter(Context context, ShareLisenner shareLisenner, CommentLisenner commentLisenner, ChatLisenner chatLisenner, ImageViewLisenner imageViewLisenner, AvatarLisenner avatarLisenner) {
        super(context);
        this.shareLisenner = shareLisenner;
        this.commentLisenner = commentLisenner;
        this.chatLisenner = chatLisenner;
        this.imageViewLisenner = imageViewLisenner;
        this.avatarLisenner = avatarLisenner;
    }

    @Override
    public int getItemViewType(int position) {
//        CivilizationInfo s = getList().get(position);
//        int number = s.getImageNumber();
//        if (number == 0) {
//            itemViewType = 0;
//        } else if (number == 1) {
//            itemViewType = 1;
//        } else if (number == 2) {
//            itemViewType = 2;
//        } else if (number == 3) {
//            itemViewType = 3;
//        } else if (number == 4) {
//            itemViewType = 4;
//        }
        return itemViewType;
    }

    @Override
    public int getViewTypeCount() {
        return 5;
    }


    @Override
    public View getView(int position, View converView, ViewGroup parent) {
//        int tpye = getItemViewType(position);
//        ViewHolder0 vh0 = null;
//        ViewHolder1 vh1 = null;
//        ViewHolder2 vh2 = null;
//        ViewHolder3 vh3 = null;
//        ViewHolder4 vh4 = null;
//        if (converView == null) {
//            switch (tpye) {
//                case 0:
//                    vh0 = new ViewHolder0();
//                    converView = inflater.inflate(R.layout.lv_intem0_lifereward, null);
//                    vh0.cmg0 = (CircleImageView) converView.findViewById(R.id.cimg_Avatar_LifeReward);
//                    vh0.userName0 = (TextView) converView.findViewById(R.id.tv_UserName0_LifeReward);
//                    vh0.time0 = (TextView) converView.findViewById(R.id.tv_Time0_LifeReward);
//                    vh0.reward0 = (TextView) converView.findViewById(R.id.tv_Reward0_LifeReward);
//                    vh0.shareNumber0 = (TextView) converView.findViewById(R.id.tv_ShareNume0_LifeReward);
//                    vh0.commentNumber0 = (TextView) converView.findViewById(R.id.tv_CommentNume0_LifeReward);
//                    vh0.helperType0 = (TextView) converView.findViewById(R.id.tv_HelperType0_LifeReward);
//                    vh0.helperAbout0 = (TextView) converView.findViewById(R.id.tv_HelperAbout0_LifeReward);
//                    vh0.location0 = (TextView) converView.findViewById(R.id.tv_Location0_LifeReward);
//                    vh0.ll_Share0_LifeReward = (LinearLayout) converView.findViewById(R.id.ll_Share0_LifeReward);
//                    vh0.ll_Comment0_LifeReward = (LinearLayout) converView.findViewById(R.id.ll_Comment0_LifeReward);
//                    vh0.ll_Chat0_LifeReward = (LinearLayout) converView.findViewById(R.id.ll_Chat0_LifeReward);
//                    converView.setTag(vh0);
//                    break;
//                case 1:
//                    vh1 = new ViewHolder1();
//                    converView = inflater.inflate(R.layout.lv_intem1_lifereward, null);
//                    vh1.cmg1 = (CircleImageView) converView.findViewById(R.id.cimg_Avatar_LifeReward);
//                    vh1.userName1 = (TextView) converView.findViewById(R.id.tv_UserName1_LifeReward);
//                    vh1.time1 = (TextView) converView.findViewById(R.id.tv_Time1_LifeReward);
//                    vh1.reward1 = (TextView) converView.findViewById(R.id.tv_Reward1_LifeReward);
//                    vh1.shareNumber1 = (TextView) converView.findViewById(R.id.tv_ShareNume1_LifeReward);
//                    vh1.commentNumber1 = (TextView) converView.findViewById(R.id.tv_CommentNume1_LifeReward);
//                    vh1.helperType1 = (TextView) converView.findViewById(R.id.tv_HelperType1_LifeReward);
//                    vh1.helperAbout1 = (TextView) converView.findViewById(R.id.tv_HelperAbout1_LifeReward);
//                    vh1.location1 = (TextView) converView.findViewById(R.id.tv_Location1_LifeReward);
//                    vh1.ll_Share1_LifeReward = (LinearLayout) converView.findViewById(R.id.ll_Share1_LifeReward);
//                    vh1.ll_Comment1_LifeReward = (LinearLayout) converView.findViewById(R.id.ll_Comment1_LifeReward);
//                    vh1.ll_Chat1_LifeReward = (LinearLayout) converView.findViewById(R.id.ll_Chat1_LifeReward);
//                    vh1.img_Imge1_LifeReward = (ImageView) converView.findViewById(R.id.img_Imge1_LifeReward);
//                    converView.setTag(vh1);
//                    break;
//                case 2:
//                    vh2 = new ViewHolder2();
//                    converView = inflater.inflate(R.layout.lv_intem2_lifereward, null);
//                    vh2.cmg2 = (CircleImageView) converView.findViewById(R.id.cimg_Avatar_LifeReward);
//                    vh2.userName2 = (TextView) converView.findViewById(R.id.tv_UserName2_LifeReward);
//                    vh2.time2 = (TextView) converView.findViewById(R.id.tv_Time2_LifeReward);
//                    vh2.reward2 = (TextView) converView.findViewById(R.id.tv_Reward2_LifeReward);
//                    vh2.shareNumber2 = (TextView) converView.findViewById(R.id.tv_ShareNume2_LifeReward);
//                    vh2.commentNumber2 = (TextView) converView.findViewById(R.id.tv_CommentNume2_LifeReward);
//                    vh2.helperType2 = (TextView) converView.findViewById(R.id.tv_HelperType2_LifeReward);
//                    vh2.helperAbout2 = (TextView) converView.findViewById(R.id.tv_HelperAbout2_LifeReward);
//                    vh2.location2 = (TextView) converView.findViewById(R.id.tv_Location2_LifeReward);
//                    vh2.ll_Share2_LifeReward = (LinearLayout) converView.findViewById(R.id.ll_Share2_LifeReward);
//                    vh2.ll_Comment2_LifeReward = (LinearLayout) converView.findViewById(R.id.ll_Comment2_LifeReward);
//                    vh2.ll_Chat2_LifeReward = (LinearLayout) converView.findViewById(R.id.ll_Chat2_LifeReward);
//                    vh2.img_Imge2_1_LifeReward = (ImageView) converView.findViewById(R.id.img_Imge2_1_LifeReward);
//                    vh2.img_Imge2_2_LifeReward = (ImageView) converView.findViewById(R.id.img_Imge2_2_LifeReward);
//                    converView.setTag(vh2);
//                    break;
//                case 3:
//                    vh3 = new ViewHolder3();
//                    converView = inflater.inflate(R.layout.lv_intem3_lifereward, null);
//                    vh3.cmg3 = (CircleImageView) converView.findViewById(R.id.cimg_Avatar_LifeReward);
//                    vh3.userName3 = (TextView) converView.findViewById(R.id.tv_UserName3_LifeReward);
//                    vh3.time3 = (TextView) converView.findViewById(R.id.tv_Time3_LifeReward);
//                    vh3.reward3 = (TextView) converView.findViewById(R.id.tv_Reward3_LifeReward);
//                    vh3.shareNumber3 = (TextView) converView.findViewById(R.id.tv_ShareNume3_LifeReward);
//                    vh3.commentNumber3 = (TextView) converView.findViewById(R.id.tv_CommentNume3_LifeReward);
//                    vh3.helperType3 = (TextView) converView.findViewById(R.id.tv_HelperType3_LifeReward);
//                    vh3.helperAbout3 = (TextView) converView.findViewById(R.id.tv_HelperAbout3_LifeReward);
//                    vh3.location3 = (TextView) converView.findViewById(R.id.tv_Location3_LifeReward);
//                    vh3.ll_Share3_LifeReward = (LinearLayout) converView.findViewById(R.id.ll_Share3_LifeReward);
//                    vh3.ll_Comment3_LifeReward = (LinearLayout) converView.findViewById(R.id.ll_Comment3_LifeReward);
//                    vh3.ll_Chat3_LifeReward = (LinearLayout) converView.findViewById(R.id.ll_Chat3_LifeReward);
//                    vh3.img_Imge3_1_LifeReward = (ImageView) converView.findViewById(R.id.img_Imge3_1_LifeReward);
//                    vh3.img_Imge3_2_LifeReward = (ImageView) converView.findViewById(R.id.img_Imge3_2_LifeReward);
//                    vh3.img_Imge3_3_LifeReward = (ImageView) converView.findViewById(R.id.img_Imge3_3_LifeReward);
//                    converView.setTag(vh3);
//                    break;
//                case 4:
//                    vh4 = new ViewHolder4();
//                    converView = inflater.inflate(R.layout.lv_intem4_lifereward, null);
//                    vh4.cmg4 = (CircleImageView) converView.findViewById(R.id.cimg_Avatar_LifeReward);
//                    vh4.userName4 = (TextView) converView.findViewById(R.id.tv_UserName4_LifeReward);
//                    vh4.time4 = (TextView) converView.findViewById(R.id.tv_Time4_LifeReward);
//                    vh4.reward4 = (TextView) converView.findViewById(R.id.tv_Reward4_LifeReward);
//                    vh4.shareNumber4 = (TextView) converView.findViewById(R.id.tv_ShareNume4_LifeReward);
//                    vh4.commentNumber4 = (TextView) converView.findViewById(R.id.tv_CommentNume4_LifeReward);
//                    vh4.helperType4 = (TextView) converView.findViewById(R.id.tv_HelperType4_LifeReward);
//                    vh4.helperAbout4 = (TextView) converView.findViewById(R.id.tv_HelperAbout4_LifeReward);
//                    vh4.location4 = (TextView) converView.findViewById(R.id.tv_Location4_LifeReward);
//                    vh4.ll_Share4_LifeReward = (LinearLayout) converView.findViewById(R.id.ll_Share4_LifeReward);
//                    vh4.ll_Comment4_LifeReward = (LinearLayout) converView.findViewById(R.id.ll_Comment4_LifeReward);
//                    vh4.ll_Chat4_LifeReward = (LinearLayout) converView.findViewById(R.id.ll_Chat4_LifeReward);
//                    vh4.img_Imge4_1_LifeReward = (ImageView) converView.findViewById(R.id.img_Imge4_1_LifeReward);
//                    vh4.img_Imge4_2_LifeReward = (ImageView) converView.findViewById(R.id.img_Imge4_2_LifeReward);
//                    vh4.img_Imge4_3_LifeReward = (ImageView) converView.findViewById(R.id.img_Imge4_3_LifeReward);
//                    vh4.img_Imge4_4_LifeReward = (ImageView) converView.findViewById(R.id.img_Imge4_4_LifeReward);
//                    converView.setTag(vh4);
//                    break;
//
//            }
//        } else {
//            switch (tpye) {
//                case 0:
//                    vh0 = (ViewHolder0) converView.getTag();
//                    break;
//                case 1:
//                    vh1 = (ViewHolder1) converView.getTag();
//                    break;
//                case 2:
//                    vh2 = (ViewHolder2) converView.getTag();
//                    break;
//                case 3:
//                    vh3 = (ViewHolder3) converView.getTag();
//                    break;
//                case 4:
//                    vh4 = (ViewHolder4) converView.getTag();
//                    break;
//            }
//        }
//        //设置资源
//         lifeRewardInfo = getItem(position);
//        switch (tpye) {
//            case 0:
//                vh0.userName0.setText(lifeRewardInfo.getUserName());
//                vh0.helperAbout0.setText(lifeRewardInfo.getHelperAbout());
//                vh0.helperType0.setText(lifeRewardInfo.getHelperType());
//                vh0.shareNumber0.setText(lifeRewardInfo.getShareNumber());
//                vh0.commentNumber0.setText(lifeRewardInfo.getCommentNumber());
//                vh0.time0.setText(lifeRewardInfo.getTime());
//                vh0.reward0.setText(lifeRewardInfo.getReward());
//                vh0.cmg0.setImageResource(lifeRewardInfo.getAvatarID());
//                vh0.location0.setText(lifeRewardInfo.getLocation());
//
//                vh0.ll_Share0_LifeReward.setOnClickListener(this);
//                vh0.ll_Comment0_LifeReward.setOnClickListener(this);
//                vh0.ll_Chat0_LifeReward.setOnClickListener(this);
//                vh0.cmg0.setOnClickListener(this);
//
//                vh0.cmg0.setTag(position);
//                vh0.ll_Share0_LifeReward.setTag(position);
//                vh0.ll_Comment0_LifeReward.setTag(position);
//                vh0.ll_Chat0_LifeReward.setTag(position);
//                break;
//            case 1:
//                vh1.userName1.setText(lifeRewardInfo.getUserName());
//                vh1.helperAbout1.setText(lifeRewardInfo.getHelperAbout());
//                vh1.helperType1.setText(lifeRewardInfo.getHelperType());
//                vh1.shareNumber1.setText(lifeRewardInfo.getShareNumber());
//                vh1.commentNumber1.setText(lifeRewardInfo.getCommentNumber());
//                vh1.time1.setText(lifeRewardInfo.getTime());
//                vh1.reward1.setText(lifeRewardInfo.getReward());
//                vh1.cmg1.setImageResource(lifeRewardInfo.getAvatarID());
//                vh1.location1.setText(lifeRewardInfo.getLocation());
//
//                vh1.ll_Share1_LifeReward.setOnClickListener(this);
//                vh1.ll_Comment1_LifeReward.setOnClickListener(this);
//                vh1.ll_Chat1_LifeReward.setOnClickListener(this);
//                vh1.img_Imge1_LifeReward.setOnClickListener(this);
//                vh1.cmg1.setOnClickListener(this);
//
//                vh1.cmg1.setTag(position);
//                vh1.ll_Share1_LifeReward.setTag(position);
//                vh1.ll_Comment1_LifeReward.setTag(position);
//                vh1.ll_Chat1_LifeReward.setTag(position);
//                vh1.img_Imge1_LifeReward.setTag(position);
//                break;
//            case 2:
//                vh2.userName2.setText(lifeRewardInfo.getUserName());
//                vh2.helperAbout2.setText(lifeRewardInfo.getHelperAbout());
//                vh2.helperType2.setText(lifeRewardInfo.getHelperType());
//                vh2.shareNumber2.setText(lifeRewardInfo.getShareNumber());
//                vh2.commentNumber2.setText(lifeRewardInfo.getCommentNumber());
//                vh2.time2.setText(lifeRewardInfo.getTime());
//                vh2.reward2.setText(lifeRewardInfo.getReward());
//                vh2.cmg2.setImageResource(lifeRewardInfo.getAvatarID());
//                vh2.location2.setText(lifeRewardInfo.getLocation());
//
//                vh2.ll_Share2_LifeReward.setOnClickListener(this);
//                vh2.ll_Comment2_LifeReward.setOnClickListener(this);
//                vh2.ll_Chat2_LifeReward.setOnClickListener(this);
//                vh2.img_Imge2_1_LifeReward.setOnClickListener(this);
//                vh2.img_Imge2_2_LifeReward.setOnClickListener(this);
//                vh2.cmg2.setOnClickListener(this);
//
//                vh2.cmg2.setTag(position);
//                vh2.ll_Share2_LifeReward.setTag(position);
//                vh2.ll_Comment2_LifeReward.setTag(position);
//                vh2.ll_Chat2_LifeReward.setTag(position);
//                vh2.img_Imge2_1_LifeReward.setTag(position);
//                vh2.img_Imge2_2_LifeReward.setTag(position);
//                break;
//            case 3:
//                vh3.userName3.setText(lifeRewardInfo.getUserName());
//                vh3.helperAbout3.setText(lifeRewardInfo.getHelperAbout());
//                vh3.helperType3.setText(lifeRewardInfo.getHelperType());
//                vh3.shareNumber3.setText(lifeRewardInfo.getShareNumber());
//                vh3.commentNumber3.setText(lifeRewardInfo.getCommentNumber());
//                vh3.time3.setText(lifeRewardInfo.getTime());
//                vh3.reward3.setText(lifeRewardInfo.getReward());
//                vh3.cmg3.setImageResource(lifeRewardInfo.getAvatarID());
//                vh3.location3.setText(lifeRewardInfo.getLocation());
//
//                vh3.ll_Share3_LifeReward.setOnClickListener(this);
//                vh3.ll_Comment3_LifeReward.setOnClickListener(this);
//                vh3.ll_Chat3_LifeReward.setOnClickListener(this);
//                vh3.img_Imge3_1_LifeReward.setOnClickListener(this);
//                vh3.img_Imge3_2_LifeReward.setOnClickListener(this);
//                vh3.img_Imge3_3_LifeReward.setOnClickListener(this);
//                vh3.cmg3.setOnClickListener(this);
//
//                vh3.cmg3.setTag(position);
//                vh3.ll_Share3_LifeReward.setTag(position);
//                vh3.ll_Comment3_LifeReward.setTag(position);
//                vh3.ll_Chat3_LifeReward.setTag(position);
//                vh3.img_Imge3_1_LifeReward.setTag(position);
//                vh3.img_Imge3_2_LifeReward.setTag(position);
//                vh3.img_Imge3_3_LifeReward.setTag(position);
//                break;
//            case 4:
//                vh4.userName4.setText(lifeRewardInfo.getUserName());
//                vh4.helperAbout4.setText(lifeRewardInfo.getHelperAbout());
//                vh4.helperType4.setText(lifeRewardInfo.getHelperType());
//                vh4.shareNumber4.setText(lifeRewardInfo.getShareNumber());
//                vh4.commentNumber4.setText(lifeRewardInfo.getCommentNumber());
//                vh4.time4.setText(lifeRewardInfo.getTime());
//                vh4.reward4.setText(lifeRewardInfo.getReward());
//                vh4.cmg4.setImageResource(lifeRewardInfo.getAvatarID());
//                vh4.location4.setText(lifeRewardInfo.getLocation());
//
//                vh4.ll_Share4_LifeReward.setOnClickListener(this);
//                vh4.ll_Comment4_LifeReward.setOnClickListener(this);
//                vh4.ll_Chat4_LifeReward.setOnClickListener(this);
//                vh4.img_Imge4_1_LifeReward.setOnClickListener(this);
//                vh4.img_Imge4_2_LifeReward.setOnClickListener(this);
//                vh4.img_Imge4_3_LifeReward.setOnClickListener(this);
//                vh4.img_Imge4_4_LifeReward.setOnClickListener(this);
//                vh4.cmg4.setOnClickListener(this);
//
//                vh4.cmg4.setTag(position);
//                vh4.ll_Share4_LifeReward.setTag(position);
//                vh4.ll_Comment4_LifeReward.setTag(position);
//                vh4.ll_Chat4_LifeReward.setTag(position);
//                vh4.img_Imge4_1_LifeReward.setTag(position);
//                vh4.img_Imge4_2_LifeReward.setTag(position);
//                vh4.img_Imge4_3_LifeReward.setTag(position);
//                vh4.img_Imge4_4_LifeReward.setTag(position);
//                break;
//        }
        return converView;
    }

    //参数：1分享；2评论；3私信
    @Override
    public void onClick(View v) {

//        switch (v.getId()) {
//            case R.id.ll_Share0_LifeReward:
//                shareLisenner.setOnShareLisenner(1, v);
//                break;
//            case R.id.ll_Comment0_LifeReward:
//                commentLisenner.setOnCommentLisenner(2, v);
//                break;
//            case R.id.ll_Chat0_LifeReward:
//                chatLisenner.setOnChatLisenner(3, v);
//                break;
//            case R.id.ll_Share1_LifeReward:
//                shareLisenner.setOnShareLisenner(1, v);
//                break;
//            case R.id.ll_Comment1_LifeReward:
//                commentLisenner.setOnCommentLisenner(2, v);
//                break;
//            case R.id.ll_Chat1_LifeReward:
//                chatLisenner.setOnChatLisenner(3, v);
//                break;
//            case R.id.ll_Share2_LifeReward:
//                shareLisenner.setOnShareLisenner(1, v);
//                break;
//            case R.id.ll_Comment2_LifeReward:
//                commentLisenner.setOnCommentLisenner(2, v);
//                break;
//            case R.id.ll_Chat2_LifeReward:
//                chatLisenner.setOnChatLisenner(3, v);
//                break;
//            case R.id.ll_Share3_LifeReward:
//                shareLisenner.setOnShareLisenner(1, v);
//                break;
//            case R.id.ll_Comment3_LifeReward:
//                commentLisenner.setOnCommentLisenner(2, v);
//                break;
//            case R.id.ll_Chat3_LifeReward:
//                chatLisenner.setOnChatLisenner(3, v);
//                break;
//            case R.id.ll_Share4_LifeReward:
//                shareLisenner.setOnShareLisenner(1, v);
//                break;
//            case R.id.ll_Comment4_LifeReward:
//                commentLisenner.setOnCommentLisenner(2, v);
//                break;
//            case R.id.ll_Chat4_LifeReward:
//                chatLisenner.setOnChatLisenner(3, v);
//                break;
//            //图片点击事件
//            case R.id.img_Imge1_LifeReward:
//                imageViewLisenner.setOnImageViewLisenner(0, v);
//                break;
//            case R.id.img_Imge2_1_LifeReward:
//                imageViewLisenner.setOnImageViewLisenner(0, v);
//
//                break;
//            case R.id.img_Imge2_2_LifeReward:
//                imageViewLisenner.setOnImageViewLisenner(1, v);
//                break;
//
//            case R.id.img_Imge3_1_LifeReward:
//                imageViewLisenner.setOnImageViewLisenner(0, v);
//                break;
//
//            case R.id.img_Imge3_2_LifeReward:
//                imageViewLisenner.setOnImageViewLisenner(1, v);
//                break;
//
//            case R.id.img_Imge3_3_LifeReward:
//                imageViewLisenner.setOnImageViewLisenner(2, v);
//                break;
//
//            case R.id.img_Imge4_1_LifeReward:
//                imageViewLisenner.setOnImageViewLisenner(0, v);
//                break;
//            case R.id.img_Imge4_2_LifeReward:
//                imageViewLisenner.setOnImageViewLisenner(1, v);
//                break;
//            case R.id.img_Imge4_3_LifeReward:
//                imageViewLisenner.setOnImageViewLisenner(2, v);
//                break;
//            case R.id.img_Imge4_4_LifeReward:
//                imageViewLisenner.setOnImageViewLisenner(3, v);
//                break;
//            //头像点击事件
//            case R.id.cimg_Avatar_LifeReward:
//                avatarLisenner.setOnAvatarLisenner(4, v);
//                break;
//
//
//        }

    }

    public class ViewHolder0 {
        private TextView userName0;
        private TextView time0;
        private TextView reward0;
        private TextView shareNumber0;
        private TextView commentNumber0;
        private TextView helperType0;
        private TextView helperAbout0;
        private TextView location0;
        private CircleImageView cmg0;
        private LinearLayout ll_Share0_LifeReward;
        private LinearLayout ll_Comment0_LifeReward;
        private LinearLayout ll_Chat0_LifeReward;
    }

    public class ViewHolder1 {
        private TextView userName1;
        private TextView time1;
        private TextView reward1;
        private TextView shareNumber1;
        private TextView commentNumber1;
        private TextView helperType1;
        private TextView helperAbout1;
        private TextView location1;
        private CircleImageView cmg1;
        private LinearLayout ll_Share1_LifeReward;
        private LinearLayout ll_Comment1_LifeReward;
        private LinearLayout ll_Chat1_LifeReward;
        private ImageView img_Imge1_LifeReward;
    }

    public class ViewHolder2 {
        private TextView userName2;
        private TextView time2;
        private TextView reward2;
        private TextView shareNumber2;
        private TextView commentNumber2;
        private TextView helperType2;
        private TextView helperAbout2;
        private TextView location2;
        private CircleImageView cmg2;
        private LinearLayout ll_Share2_LifeReward;
        private LinearLayout ll_Comment2_LifeReward;
        private LinearLayout ll_Chat2_LifeReward;
        private ImageView img_Imge2_1_LifeReward;
        private ImageView img_Imge2_2_LifeReward;
    }

    public class ViewHolder3 {
        private TextView userName3;
        private TextView time3;
        private TextView reward3;
        private TextView shareNumber3;
        private TextView commentNumber3;
        private TextView helperType3;
        private TextView helperAbout3;
        private TextView location3;
        private CircleImageView cmg3;
        private LinearLayout ll_Share3_LifeReward;
        private LinearLayout ll_Comment3_LifeReward;
        private LinearLayout ll_Chat3_LifeReward;
        private ImageView img_Imge3_1_LifeReward;
        private ImageView img_Imge3_2_LifeReward;
        private ImageView img_Imge3_3_LifeReward;
    }

    public class ViewHolder4 {
        private TextView userName4;
        private TextView time4;
        private TextView reward4;
        private TextView shareNumber4;
        private TextView commentNumber4;
        private TextView helperType4;
        private TextView helperAbout4;
        private TextView location4;
        private CircleImageView cmg4;
        private LinearLayout ll_Share4_LifeReward;
        private LinearLayout ll_Comment4_LifeReward;
        private LinearLayout ll_Chat4_LifeReward;
        private ImageView img_Imge4_1_LifeReward;
        private ImageView img_Imge4_2_LifeReward;
        private ImageView img_Imge4_3_LifeReward;
        private ImageView img_Imge4_4_LifeReward;

    }

    /**
     * 分享回调接口
     */
    public interface ShareLisenner {
        /**
         * 分享回调方法
         */
        void setOnShareLisenner(int tag, View v);
    }

    /**
     * 评论回调接口
     */
    public interface CommentLisenner {
        /**
         * 评论回调方法
         */
        void setOnCommentLisenner(int tag, View v);
    }

    /**
     * 私信回调接口
     */
    public interface ChatLisenner {
        /**
         * 私信回调方法
         */
        void setOnChatLisenner(int tag, View v);
    }

    /**
     * 图片回调接口
     */
    public interface ImageViewLisenner {
        /**
         * 图片回调方法
         */
        void setOnImageViewLisenner(int tag, View v);
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
