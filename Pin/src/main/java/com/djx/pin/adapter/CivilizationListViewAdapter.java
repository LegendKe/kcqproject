/*
package com.djx.pin.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.djx.pin.R;
import com.djx.pin.beans.CivilizationInfo;
import com.djx.pin.myview.CircleImageView;
import com.djx.pin.utils.QiniuUtils;
import com.djx.pin.utils.TurnIntoTime;

import java.util.ArrayList;
import java.util.List;

*/
/**
 * Created by Administrator on 2016/6/14.
 *//*

public class CivilizationListViewAdapter extends MyBaseAdapter<CivilizationInfo.Result.CultureWallInfo> implements View.OnClickListener {


    private int itemViewType;

    //实例化接口对象
    private ShareLisenner shareLisenner;
    private CommentLisenner commentLisenner;
    private ChatLisenner chatLisenner;
    private ImageViewLisenner imageViewLisenner;
    private AvatarLisenner avatarLisenner;

    private CivilizationInfo.Result.CultureWallInfo lists;

    public CivilizationListViewAdapter(Context context, ShareLisenner shareLisenner, CommentLisenner commentLisenner, ChatLisenner chatLisenner, ImageViewLisenner imageViewLisenner, AvatarLisenner avatarLisenner) {
        super(context);
        this.shareLisenner = shareLisenner;
        this.commentLisenner = commentLisenner;
        this.chatLisenner = chatLisenner;
        this.imageViewLisenner = imageViewLisenner;
        this.avatarLisenner = avatarLisenner;
    }

    @Override
    public int getItemViewType(int position) {

        CivilizationInfo.Result.CultureWallInfo lists = list.get(position);

        int number = lists.media.size();
        if (number == 0) {
            itemViewType = 0;
        } else if (number == 1) {
            itemViewType = 1;
        } else if (number == 2) {
            itemViewType = 2;
        } else if (number == 3) {
            itemViewType = 3;
        } else if (number == 4) {
            itemViewType = 4;
        }
        return itemViewType;
    }

    @Override
    public int getViewTypeCount() {
        return 5;
    }


    @Override
    public View getView(int position, View converView, ViewGroup parent) {
        final int tpye = getItemViewType(position);
        ViewHolder0 vh0 = null;
        ViewHolder1 vh1 = null;
        ViewHolder2 vh2 = null;
        ViewHolder3 vh3 = null;
        ViewHolder4 vh4 = null;
        if (converView == null) {
            switch (tpye) {
                case 0:
                    vh0 = new ViewHolder0();
                    converView = inflater.inflate(R.layout.lv_intem0_civilization, null);
                    vh0.cmg0 = (CircleImageView) converView.findViewById(R.id.cimg_Avatar0_Civilization);

                    vh0.userName0 = (TextView) converView.findViewById(R.id.tv_UserName0_Civilization);
                    vh0.time0 = (TextView) converView.findViewById(R.id.tv_Time0_Civilization);
                    vh0.reward0 = (TextView) converView.findViewById(R.id.tv_Reward0_Civilization);
                    vh0.shareNumber = (TextView) converView.findViewById(R.id.tv_ShareNumber_Civilization);
                    vh0.commentNumber = (TextView) converView.findViewById(R.id.tv_CommentNumber_Civilization);
                    vh0.helperType0 = (TextView) converView.findViewById(R.id.tv_HelperType0_Civilization);
                    vh0.helperAbout0 = (TextView) converView.findViewById(R.id.tv_HelperAbout0_Civilization);
                    vh0.location0 = (TextView) converView.findViewById(R.id.tv_Location0_Civilization);

                    vh0.ll_Share_Civilization = (LinearLayout) converView.findViewById(R.id.ll_Share_Civilization);
                    vh0.ll_Comment_Civilization = (LinearLayout) converView.findViewById(R.id.ll_Comment_Civilization);

                    vh0.ll_Chat_Civilization = (LinearLayout) converView.findViewById(R.id.ll_Chat_Civilization);
                    converView.setTag(vh0);
                    break;
                case 1:
                    vh1 = new ViewHolder1();
                    converView = inflater.inflate(R.layout.lv_intem1_civilization, null);
                    vh1.cmg1 = (CircleImageView) converView.findViewById(R.id.cimg_Avatar1_Civilization);

                    vh1.userName1 = (TextView) converView.findViewById(R.id.tv_UserName1_Civilization);
                    vh1.time1 = (TextView) converView.findViewById(R.id.tv_Time1_Civilization);
                    vh1.reward1 = (TextView) converView.findViewById(R.id.tv_Reward1_Civilization);
                    vh1.shareNumber = (TextView) converView.findViewById(R.id.tv_ShareNumber_Civilization);
                    vh1.commentNumber = (TextView) converView.findViewById(R.id.tv_CommentNumber_Civilization);
                    vh1.helperType1 = (TextView) converView.findViewById(R.id.tv_HelperType1_Civilization);
                    vh1.helperAbout1 = (TextView) converView.findViewById(R.id.tv_HelperAbout1_Civilization);
                    vh1.location1 = (TextView) converView.findViewById(R.id.tv_Location1_Civilization);

                    vh1.ll_Share_Civilization = (LinearLayout) converView.findViewById(R.id.ll_Share_Civilization);
                    vh1.ll_Comment_Civilization = (LinearLayout) converView.findViewById(R.id.ll_Comment_Civilization);
                    vh1.ll_Chat_Civilization = (LinearLayout) converView.findViewById(R.id.ll_Chat_Civilization);

                    vh1.img_Imge1_Civilization = (ImageView) converView.findViewById(R.id.img_Imge1_Civilization);
                    converView.setTag(vh1);
                    break;
                case 2:
                    vh2 = new ViewHolder2();
                    converView = inflater.inflate(R.layout.lv_intem2_civilization, null);
                    vh2.cmg2 = (CircleImageView) converView.findViewById(R.id.cimg_Avatar2_Civilization);
                    vh2.userName2 = (TextView) converView.findViewById(R.id.tv_UserName2_Civilization);
                    vh2.time2 = (TextView) converView.findViewById(R.id.tv_Time2_Civilization);
                    vh2.reward2 = (TextView) converView.findViewById(R.id.tv_Reward2_Civilization);
                    vh2.shareNumber = (TextView) converView.findViewById(R.id.tv_ShareNumber_Civilization);
                    vh2.commentNumber = (TextView) converView.findViewById(R.id.tv_CommentNumber_Civilization);
                    vh2.helperType2 = (TextView) converView.findViewById(R.id.tv_HelperType2_Civilization);
                    vh2.helperAbout2 = (TextView) converView.findViewById(R.id.tv_HelperAbout2_Civilization);
                    vh2.location2 = (TextView) converView.findViewById(R.id.tv_Location2_Civilization);

                    vh2.ll_Share_Civilization = (LinearLayout) converView.findViewById(R.id.ll_Share_Civilization);
                    vh2.ll_Comment_Civilization = (LinearLayout) converView.findViewById(R.id.ll_Comment_Civilization);
                    vh2.ll_Chat_Civilization = (LinearLayout) converView.findViewById(R.id.ll_Chat_Civilization);

                    vh2.img_Imge2_1_Civilization = (ImageView) converView.findViewById(R.id.img_Imge2_1_Civilization);
                    vh2.img_Imge2_2_Civilization = (ImageView) converView.findViewById(R.id.img_Imge2_2_Civilization);
                    converView.setTag(vh2);
                    break;
                case 3:
                    vh3 = new ViewHolder3();
                    converView = inflater.inflate(R.layout.lv_intem3_civilization, null);
                    vh3.cmg3 = (CircleImageView) converView.findViewById(R.id.cimg_Avatar3_Civilization);

                    vh3.userName3 = (TextView) converView.findViewById(R.id.tv_UserName3_Civilization);
                    vh3.time3 = (TextView) converView.findViewById(R.id.tv_Time3_Civilization);
                    vh3.reward3 = (TextView) converView.findViewById(R.id.tv_Reward3_Civilization);
                    vh3.shareNumber = (TextView) converView.findViewById(R.id.tv_ShareNumber_Civilization);
                    vh3.commentNumber = (TextView) converView.findViewById(R.id.tv_CommentNumber_Civilization);
                    vh3.helperType3 = (TextView) converView.findViewById(R.id.tv_HelperType3_Civilization);
                    vh3.helperAbout3 = (TextView) converView.findViewById(R.id.tv_HelperAbout3_Civilization);
                    vh3.location3 = (TextView) converView.findViewById(R.id.tv_Location3_Civilization);

                    vh3.ll_Share_Civilization = (LinearLayout) converView.findViewById(R.id.ll_Share_Civilization);
                    vh3.ll_Comment_Civilization = (LinearLayout) converView.findViewById(R.id.ll_Comment_Civilization);

                    vh3.ll_Chat_Civilization = (LinearLayout) converView.findViewById(R.id.ll_Chat_Civilization);
                    vh3.img_Imge3_1_Civilization = (ImageView) converView.findViewById(R.id.img_Imge3_1_Civilization);
                    vh3.img_Imge3_2_Civilization = (ImageView) converView.findViewById(R.id.img_Imge3_2_Civilization);
                    vh3.img_Imge3_3_Civilization = (ImageView) converView.findViewById(R.id.img_Imge3_3_Civilization);
                    converView.setTag(vh3);
                    break;
                case 4:
                    vh4 = new ViewHolder4();
                    converView = inflater.inflate(R.layout.lv_intem4_civilization, null);

                    vh4.cmg4 = (CircleImageView) converView.findViewById(R.id.cimg_Avatar4_Civilization);

                    vh4.userName4 = (TextView) converView.findViewById(R.id.tv_UserName4_Civilization);
                    vh4.time4 = (TextView) converView.findViewById(R.id.tv_Time4_Civilization);
                    vh4.reward4 = (TextView) converView.findViewById(R.id.tv_Reward4_Civilization);
                    vh4.shareNumber = (TextView) converView.findViewById(R.id.tv_ShareNumber_Civilization);
                    vh4.commentNumber = (TextView) converView.findViewById(R.id.tv_CommentNumber_Civilization);
                    vh4.helperType4 = (TextView) converView.findViewById(R.id.tv_HelperType4_Civilization);
                    vh4.helperAbout4 = (TextView) converView.findViewById(R.id.tv_HelperAbout4_Civilization);
                    vh4.location4 = (TextView) converView.findViewById(R.id.tv_Location4_Civilization);

                    vh4.ll_Share_Civilization = (LinearLayout) converView.findViewById(R.id.ll_Share_Civilization);
                    vh4.ll_Comment_Civilization = (LinearLayout) converView.findViewById(R.id.ll_Comment_Civilization);

                    vh4.ll_Chat_Civilization = (LinearLayout) converView.findViewById(R.id.ll_Chat_Civilization);
                    vh4.img_Imge4_1_Civilization = (ImageView) converView.findViewById(R.id.img_Imge4_1_Civilization);
                    vh4.img_Imge4_2_Civilization = (ImageView) converView.findViewById(R.id.img_Imge4_2_Civilization);
                    vh4.img_Imge4_3_Civilization = (ImageView) converView.findViewById(R.id.img_Imge4_3_Civilization);
                    vh4.img_Imge4_4_Civilization = (ImageView) converView.findViewById(R.id.img_Imge4_4_Civilization);
                    converView.setTag(vh4);
                    break;

            }
        } else {
            switch (tpye) {
                case 0:
                    vh0 = (ViewHolder0) converView.getTag();
                    break;
                case 1:
                    vh1 = (ViewHolder1) converView.getTag();
                    break;
                case 2:
                    vh2 = (ViewHolder2) converView.getTag();
                    break;
                case 3:
                    vh3 = (ViewHolder3) converView.getTag();
                    break;
                case 4:
                    vh4 = (ViewHolder4) converView.getTag();
                    break;
            }
        }
        //设置资源
        lists = getItem(position);
        List<ImageView> imageViewList = new ArrayList<ImageView>();
        List<String> idList = new ArrayList<String>();

        switch (tpye) {
            case 0:
                vh0.userName0.setText(lists.nickname);
                vh0.helperAbout0.setText(lists.description);
                vh0.helperType0.setText("");
                vh0.shareNumber.setText(lists.share_num + "");
                vh0.commentNumber.setText(lists.comment_num + "");

                vh0.time0.setText(TurnIntoTime.getCreateTime(lists.create_time));
                vh0.reward0.setText("");
                vh0.location0.setText(lists.location);

                //分享评论聊天图片的点击事件
                vh0.ll_Share_Civilization.setOnClickListener(this);
                vh0.ll_Comment_Civilization.setOnClickListener(this);
                vh0.ll_Chat_Civilization.setOnClickListener(this);
                //头像点击事件
                vh0.cmg0.setOnClickListener(this);
                vh0.cmg0.setTag(position);
                vh0.ll_Share_Civilization.setTag(position);
                vh0.ll_Comment_Civilization.setTag(position);
                vh0.ll_Chat_Civilization.setTag(position);

                //加载图片
//                imageViewList.add(vh0.cmg0);

                for (int i = 0; i < tpye; i++) {
                    idList.add(i ,lists.media.get(i).media_id);
                }
                QiniuUtils.setAvatarByIdFrom7Niu(context,vh0.cmg0,lists.portrait);
                break;
            case 1:
                vh1.userName1.setText(lists.nickname);
                vh1.helperAbout1.setText(lists.description);
                vh1.helperType1.setText("");
                vh1.shareNumber.setText(lists.share_num + "");
                vh1.commentNumber.setText(lists.comment_num + "");

                vh1.time1.setText(TurnIntoTime.getCreateTime(lists.create_time));
                vh1.reward1.setText("");
                for (int i = 0; i < tpye; i++) {
                    idList.add(i, lists.media.get(i).media_id);
                }
                QiniuUtils.setAvatarByIdFrom7Niu(context, vh1.cmg1, lists.portrait);
                QiniuUtils.setImageViewByIdFrom7Niu(context, vh1.img_Imge1_Civilization, idList.get(0), 400, 400);

                vh1.location1.setText(lists.location);

                //分享评论聊天图片的点击时间

                vh1.ll_Share_Civilization.setOnClickListener(this);
                vh1.ll_Comment_Civilization.setOnClickListener(this);
                vh1.ll_Chat_Civilization.setOnClickListener(this);
                vh1.img_Imge1_Civilization.setOnClickListener(this);
//头像点击事件
                vh1.cmg1.setOnClickListener(this);
                vh1.cmg1.setTag(position);
                vh1.ll_Share_Civilization.setTag(position);
                vh1.ll_Comment_Civilization.setTag(position);
                vh1.ll_Chat_Civilization.setTag(position);
                vh1.img_Imge1_Civilization.setTag(position);
                break;
            case 2:
                vh2.userName2.setText(lists.nickname);
                vh2.helperAbout2.setText(lists.description);
                vh2.helperType2.setText("");
                vh2.shareNumber.setText(lists.share_num + "");
                vh2.commentNumber.setText(lists.comment_num + "");

                vh2.time2.setText(TurnIntoTime.getCreateTime(lists.create_time));
                vh2.reward2.setText("");
                vh2.location2.setText(lists.location);
                for (int i = 0; i < tpye; i++) {
                    idList.add(i ,lists.media.get(i).media_id);
                }
                QiniuUtils.setAvatarByIdFrom7Niu(context,vh2.cmg2,lists.portrait);
                QiniuUtils.setImageViewByIdFrom7Niu(context, vh2.img_Imge2_1_Civilization, idList.get(0), 320, 320);
                QiniuUtils.setImageViewByIdFrom7Niu(context, vh2.img_Imge2_2_Civilization, idList.get(1), 320, 320);
                //分享评论聊天图片的点击时间

                vh2.ll_Share_Civilization.setOnClickListener(this);
                vh2.ll_Comment_Civilization.setOnClickListener(this);
                vh2.ll_Chat_Civilization.setOnClickListener(this);
                vh2.img_Imge2_1_Civilization.setOnClickListener(this);
                vh2.img_Imge2_2_Civilization.setOnClickListener(this);
                //头像点击事件
                vh2.cmg2.setOnClickListener(this);
                vh2.cmg2.setTag(position);
                vh2.ll_Share_Civilization.setTag(position);
                vh2.ll_Comment_Civilization.setTag(position);
                vh2.ll_Chat_Civilization.setTag(position);
                vh2.img_Imge2_1_Civilization.setTag(position);
                vh2.img_Imge2_2_Civilization.setTag(position);
                break;
            case 3:
                vh3.userName3.setText(lists.nickname);
                vh3.helperAbout3.setText(lists.description);
                vh3.helperType3.setText("");
                vh3.shareNumber.setText(lists.share_num + "");
                vh3.commentNumber.setText(lists.comment_num + "");

                vh3.time3.setText(TurnIntoTime.getCreateTime(lists.create_time));
                vh3.reward3.setText("");
                vh3.location3.setText(lists.location);

                for (int i = 0; i < tpye; i++) {
                    idList.add(i ,lists.media.get(i).media_id);
                }
                QiniuUtils.setAvatarByIdFrom7Niu(context,vh3.cmg3,lists.portrait);
                QiniuUtils.setImageViewByIdFrom7Niu(context, vh3.img_Imge3_1_Civilization, idList.get(0), 320, 320);
                QiniuUtils.setImageViewByIdFrom7Niu(context, vh3.img_Imge3_2_Civilization, idList.get(1), 320, 320);
                QiniuUtils.setImageViewByIdFrom7Niu(context, vh3.img_Imge3_3_Civilization, idList.get(2), 320, 320);
                //分享评论聊天图片的点击时间
                vh3.ll_Share_Civilization.setOnClickListener(this);
                vh3.ll_Comment_Civilization.setOnClickListener(this);
                vh3.ll_Chat_Civilization.setOnClickListener(this);
                vh3.img_Imge3_1_Civilization.setOnClickListener(this);
                vh3.img_Imge3_2_Civilization.setOnClickListener(this);
                vh3.img_Imge3_3_Civilization.setOnClickListener(this);
//头像点击事件
                vh3.cmg3.setOnClickListener(this);
                vh3.cmg3.setTag(position);

                vh3.ll_Share_Civilization.setTag(position);
                vh3.ll_Comment_Civilization.setTag(position);
                vh3.ll_Chat_Civilization.setTag(position);
                vh3.img_Imge3_1_Civilization.setTag(position);
                vh3.img_Imge3_2_Civilization.setTag(position);
                vh3.img_Imge3_3_Civilization.setTag(position);
                break;
            case 4:
                vh4.userName4.setText(lists.nickname);
                vh4.helperAbout4.setText(lists.description);
                vh4.helperType4.setText("");
                vh4.shareNumber.setText(lists.share_num + "");
                vh4.commentNumber.setText(lists.comment_num + "");


                vh4.time4.setText(TurnIntoTime.getCreateTime(lists.create_time));
                vh4.reward4.setText("");
                vh4.location4.setText(lists.location);
                for (int i = 0; i < tpye; i++) {
                    idList.add(i,lists.media.get(i).media_id);
                }
                QiniuUtils.setAvatarByIdFrom7Niu(context,vh4.cmg4,lists.portrait);
                QiniuUtils.setImageViewByIdFrom7Niu(context, vh4.img_Imge4_1_Civilization, idList.get(0), 320, 320);
                QiniuUtils.setImageViewByIdFrom7Niu(context, vh4.img_Imge4_2_Civilization, idList.get(1), 320, 320);
                QiniuUtils.setImageViewByIdFrom7Niu(context, vh4.img_Imge4_3_Civilization, idList.get(2), 320, 320);
                QiniuUtils.setImageViewByIdFrom7Niu(context, vh4.img_Imge4_4_Civilization, idList.get(3), 320, 320);

//分享评论聊天图片的点击时间
                vh4.ll_Share_Civilization.setOnClickListener(this);
                vh4.ll_Comment_Civilization.setOnClickListener(this);
                vh4.ll_Chat_Civilization.setOnClickListener(this);
                vh4.img_Imge4_1_Civilization.setOnClickListener(this);
                vh4.img_Imge4_2_Civilization.setOnClickListener(this);
                vh4.img_Imge4_3_Civilization.setOnClickListener(this);
                vh4.img_Imge4_4_Civilization.setOnClickListener(this);
//头像点击事件
                vh4.cmg4.setOnClickListener(this);
                vh4.cmg4.setTag(position);

                vh4.ll_Share_Civilization.setTag(position);
                vh4.ll_Comment_Civilization.setTag(position);
                vh4.ll_Chat_Civilization.setTag(position);
                vh4.img_Imge4_1_Civilization.setTag(position);
                vh4.img_Imge4_2_Civilization.setTag(position);
                vh4.img_Imge4_3_Civilization.setTag(position);
                vh4.img_Imge4_4_Civilization.setTag(position);
                break;
        }
        return converView;
    }

    //参数：1分享；2评论；3私信
    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.ll_Share_Civilization:
                shareLisenner.setOnShareLisenner(1, v);
                break;
            case R.id.ll_Comment_Civilization:
                commentLisenner.setOnCommentLisenner(2, v);
                break;
            case R.id.ll_Chat_Civilization:
                chatLisenner.setOnChatLisenner(3, v);
                break;

            //图片点击事件
            case R.id.img_Imge1_Civilization:
                imageViewLisenner.setOnImageViewLisenner(0, v);
                break;
            case R.id.img_Imge2_1_Civilization:
                imageViewLisenner.setOnImageViewLisenner(0, v);

                break;
            case R.id.img_Imge2_2_Civilization:
                imageViewLisenner.setOnImageViewLisenner(1, v);
                break;

            case R.id.img_Imge3_1_Civilization:
                imageViewLisenner.setOnImageViewLisenner(0, v);
                break;

            case R.id.img_Imge3_2_Civilization:
                imageViewLisenner.setOnImageViewLisenner(1, v);
                break;

            case R.id.img_Imge3_3_Civilization:
                imageViewLisenner.setOnImageViewLisenner(2, v);
                break;

            case R.id.img_Imge4_1_Civilization:
                imageViewLisenner.setOnImageViewLisenner(0, v);
                break;
            case R.id.img_Imge4_2_Civilization:
                imageViewLisenner.setOnImageViewLisenner(1, v);
                break;
            case R.id.img_Imge4_3_Civilization:
                imageViewLisenner.setOnImageViewLisenner(2, v);
                break;
            case R.id.img_Imge4_4_Civilization:
                imageViewLisenner.setOnImageViewLisenner(3, v);
                break;

            //头像点击事件
            case R.id.cimg_Avatar0_Civilization:
                avatarLisenner.setOnAvatarLisenner(4, v);
                break;
            case R.id.cimg_Avatar1_Civilization:
                avatarLisenner.setOnAvatarLisenner(4, v);
                break;
            case R.id.cimg_Avatar2_Civilization:
                avatarLisenner.setOnAvatarLisenner(4, v);
                break;
            case R.id.cimg_Avatar3_Civilization:
                avatarLisenner.setOnAvatarLisenner(4, v);
                break;
            case R.id.cimg_Avatar4_Civilization:
                avatarLisenner.setOnAvatarLisenner(4, v);
                break;
        }

    }

    public class ViewHolder0 {
        private TextView userName0;
        private TextView time0;
        private TextView reward0;
        private TextView shareNumber;
        private TextView commentNumber;
        private TextView helperType0;
        private TextView helperAbout0;
        private TextView location0;
        private CircleImageView cmg0;
        private LinearLayout ll_Share_Civilization;
        private LinearLayout ll_Comment_Civilization;
        private LinearLayout ll_Chat_Civilization;
    }

    public class ViewHolder1 {
        private TextView userName1;
        private TextView time1;
        private TextView reward1;
        private TextView shareNumber;
        private TextView commentNumber;
        private TextView helperType1;
        private TextView helperAbout1;
        private TextView location1;
        private CircleImageView cmg1;
        private LinearLayout ll_Share_Civilization;
        private LinearLayout ll_Comment_Civilization;
        private LinearLayout ll_Chat_Civilization;
        private ImageView img_Imge1_Civilization;
    }

    public class ViewHolder2 {
        private TextView userName2;
        private TextView time2;
        private TextView reward2;
        private TextView shareNumber;
        private TextView commentNumber;
        private TextView helperType2;
        private TextView helperAbout2;
        private TextView location2;
        private CircleImageView cmg2;
        private LinearLayout ll_Share_Civilization;
        private LinearLayout ll_Comment_Civilization;
        private LinearLayout ll_Chat_Civilization;
        private ImageView img_Imge2_1_Civilization;
        private ImageView img_Imge2_2_Civilization;
    }

    public class ViewHolder3 {
        private TextView userName3;
        private TextView time3;
        private TextView reward3;
        private TextView shareNumber;
        private TextView commentNumber;
        private TextView helperType3;
        private TextView helperAbout3;
        private TextView location3;
        private CircleImageView cmg3;
        private LinearLayout ll_Share_Civilization;
        private LinearLayout ll_Comment_Civilization;
        private LinearLayout ll_Chat_Civilization;
        private ImageView img_Imge3_1_Civilization;
        private ImageView img_Imge3_2_Civilization;
        private ImageView img_Imge3_3_Civilization;
    }

    public class ViewHolder4 {
        private TextView userName4;
        private TextView time4;
        private TextView reward4;
        private TextView shareNumber;
        private TextView commentNumber;
        private TextView helperType4;
        private TextView helperAbout4;
        private TextView location4;
        private CircleImageView cmg4;
        private LinearLayout ll_Share_Civilization;
        private LinearLayout ll_Comment_Civilization;
        private LinearLayout ll_Chat_Civilization;
        private ImageView img_Imge4_1_Civilization;
        private ImageView img_Imge4_2_Civilization;
        private ImageView img_Imge4_3_Civilization;
        private ImageView img_Imge4_4_Civilization;

    }

    */
/**
     * 分享回调接口
     *//*

    public interface ShareLisenner {
        */
/**
         * 分享回调方法
         *//*

        void setOnShareLisenner(int tag, View v);
    }

    */
/**
     * 评论回调接口
     *//*

    public interface CommentLisenner {
        */
/**
         * 评论回调方法
         *//*

        void setOnCommentLisenner(int tag, View v);
    }

    */
/**
     * 私信回调接口
     *//*

    public interface ChatLisenner {
        */
/**
         * 私信回调方法
         *//*

        void setOnChatLisenner(int tag, View v);
    }

    */
/**
     * 图片回调接口
     *//*

    public interface ImageViewLisenner {
        */
/**
         * 图片回调方法
         *//*

        void setOnImageViewLisenner(int tag, View v);
    }

    */
/**
     * 头像回调接口
     *//*

    public interface AvatarLisenner {
        */
/**
         * 头像回调方法
         *//*

        void setOnAvatarLisenner(int tag, View v);
    }

}
*/
