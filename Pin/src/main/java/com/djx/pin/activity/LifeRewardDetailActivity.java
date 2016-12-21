package com.djx.pin.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.djx.pin.R;
import com.djx.pin.adapter.CivilizationDetailCommentAdapter;
import com.djx.pin.adapter.CivilizationDetailShareAdapter;
import com.djx.pin.adapter.ShowImagePagerAdapter;
import com.djx.pin.base.OldBaseActivity;
import com.djx.pin.beans.CivilizationInfo;
import com.djx.pin.beans.CommentInfo;
import com.djx.pin.myview.CircleImageView;
import com.djx.pin.myview.MyScrollView;
import com.djx.pin.utils.DateUtils;
import com.djx.pin.utils.ScreenUtils;
import com.djx.pin.utils.ScrollViewSetListViewHeight;
import com.djx.pin.utils.ToastUtil;
import com.tencent.connect.common.Constants;
import com.tencent.connect.share.QQShare;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

import uk.co.senab.photoview.PhotoView;

/**
 * Created by Administrator on 2016/6/14.
 */
public class LifeRewardDetailActivity extends OldBaseActivity implements MyScrollView.OnScrollListener, AdapterView.OnItemClickListener, View.OnClickListener, TextWatcher, ViewPager.OnPageChangeListener {

    private ListView lv_Share_LifeRewardDetail, lv_Comment_LifeRewardDetail;
    private CivilizationDetailShareAdapter shareAdapter;
    private CivilizationDetailCommentAdapter commentAdapter;
    private MyScrollView msv_ScrollView_LRDA;

    private LinearLayout ll_ViewGroup1_LRDA, ll_Top_LifeRewardDetail, ll_ViewGroup2_LRDA,
            ll_Location1_LifeRewardDetail, ll_Chat_LRDA, ll_Share_LRDA, ll_Comment_LRDA, ll_Back_LifeRewardDetail;
    private TextView tv_ShareDetail_LRDA, tv_CommentDetail_LRDA, tv_QiangDan_LifeRewardDetail, tv_Complaint_Detail;
    private int viewGroupTop;
    private View v_ShareLine_LRDA, v_CommentLine_LRDA, v_ParentCover_LRDA;

    private CircleImageView cimg_Avatar_LRDA;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initLifeReward();
//        shareAdapter = new CivilizationDetailShareAdapter(this,this);
//        commentAdapter = new CivilizationDetailCommentAdapter(this);
        //每个布局中公用的控件的初始化和事件
        initView();
        initData();
        initEvent();
        initListViewShareData();
        initListViewCommentData();
    }

    public void initData() {

    }

    @Override
    protected void onStart() {
        super.onStart();
        boolean showCommentPop = bundle.getBoolean("showCommentPop");
        if (showCommentPop) {
            v_ShareLine_LRDA.setVisibility(View.INVISIBLE);
            v_CommentLine_LRDA.setVisibility(View.VISIBLE);
            lv_Share_LifeRewardDetail.setVisibility(View.GONE);
            lv_Comment_LifeRewardDetail.setVisibility(View.VISIBLE);
            tv_ShareDetail_LRDA.setTextColor(getResources().getColor(R.color.text_color_hint));
            tv_CommentDetail_LRDA.setTextColor(getResources().getColor(R.color.text_color_black));
        }
    }

    CivilizationInfo lifeRewarInfo;
    //图片数量
    int i;
    Bundle bundle;

    private void initLifeReward() {
    }

    private void initImageViewEvent4() {
        img_Imge4_1_LifeRewardDetail.setOnClickListener(this);
        img_Imge4_2_LifeRewardDetail.setOnClickListener(this);
        img_Imge4_3_LifeRewardDetail.setOnClickListener(this);
        img_Imge4_4_LifeRewardDetail.setOnClickListener(this);

    }

    private ImageView img_Imge4_1_LifeRewardDetail, img_Imge4_2_LifeRewardDetail, img_Imge4_3_LifeRewardDetail, img_Imge4_4_LifeRewardDetail;

    private void initImageView4() {
        img_Imge4_1_LifeRewardDetail = (ImageView) findViewById(R.id.img_Imge4_1_LifeRewardDetail);
        img_Imge4_2_LifeRewardDetail = (ImageView) findViewById(R.id.img_Imge4_2_LifeRewardDetail);
        img_Imge4_3_LifeRewardDetail = (ImageView) findViewById(R.id.img_Imge4_3_LifeRewardDetail);
        img_Imge4_4_LifeRewardDetail = (ImageView) findViewById(R.id.img_Imge4_4_LifeRewardDetail);
    }

    private void initImageViewEvent3() {
        img_Imge3_1_LifeRewardDetail.setOnClickListener(this);
        img_Imge3_2_LifeRewardDetail.setOnClickListener(this);
        img_Imge3_3_LifeRewardDetail.setOnClickListener(this);
    }

    private ImageView img_Imge3_1_LifeRewardDetail, img_Imge3_2_LifeRewardDetail, img_Imge3_3_LifeRewardDetail;

    private void initImageView3() {
        img_Imge3_1_LifeRewardDetail = (ImageView) findViewById(R.id.img_Imge3_1_LifeRewardDetail);
        img_Imge3_2_LifeRewardDetail = (ImageView) findViewById(R.id.img_Imge3_2_LifeRewardDetail);
        img_Imge3_3_LifeRewardDetail = (ImageView) findViewById(R.id.img_Imge3_3_LifeRewardDetail);
    }

    private void initImageViewEvent2() {
        img_Imge2_1_LifeRewardDetail.setOnClickListener(this);
        img_Imge2_2_LifeRewardDetail.setOnClickListener(this);
    }

    private ImageView img_Imge2_1_LifeRewardDetail, img_Imge2_2_LifeRewardDetail;

    private void initImageView2() {
        img_Imge2_1_LifeRewardDetail = (ImageView) findViewById(R.id.img_Imge2_1_LifeRewardDetail);
        img_Imge2_2_LifeRewardDetail = (ImageView) findViewById(R.id.img_Imge2_2_LifeRewardDetail);
    }


    /**
     * 只有一个图片
     */
    private void initImageViewEvent1() {
        img_Imge1_LifeRewardDetail.setOnClickListener(this);
    }

    private ImageView img_Imge1_LifeRewardDetail;

    private void initImageView1() {
        img_Imge1_LifeRewardDetail = (ImageView) findViewById(R.id.img_Imge1_LifeRewardDetail);
    }

    /**
     * 添加评论数据
     */
    private void initListViewCommentData() {
        CommentInfo info = new CommentInfo(R.mipmap.login_qq, "小白", DateUtils.getCurrentDate(), "说的好说的妙说的呱呱叫");

        lv_Comment_LifeRewardDetail.setFocusable(false);
        lv_Comment_LifeRewardDetail.setAdapter(commentAdapter);
        ScrollViewSetListViewHeight.setListViewHeightBasedOnChildren(lv_Comment_LifeRewardDetail);
        tv_CommentDetail_LRDA.setText("评论  " + commentAdapter.getCount());
    }

    /**
     * 分享数据
     */
    private void initListViewShareData() {
        lv_Share_LifeRewardDetail.setAdapter(shareAdapter);
        lv_Share_LifeRewardDetail.setFocusable(false);
        ScrollViewSetListViewHeight.setListViewHeightBasedOnChildren(lv_Share_LifeRewardDetail);
        tv_ShareDetail_LRDA.setText("分享  " + shareAdapter.getCount());
    }

    /**
     * 公用控件和事件
     */
    private void initEvent() {
        msv_ScrollView_LRDA.setOnScrollListener(this);

        lv_Share_LifeRewardDetail.setOnItemClickListener(this);

        tv_ShareDetail_LRDA.setOnClickListener(this);
        tv_CommentDetail_LRDA.setOnClickListener(this);
        tv_Complaint_Detail.setOnClickListener(this);

        ll_Chat_LRDA.setOnClickListener(this);
        ll_Comment_LRDA.setOnClickListener(this);
        ll_Share_LRDA.setOnClickListener(this);
        ll_Back_LifeRewardDetail.setOnClickListener(this);

        tv_QiangDan_LifeRewardDetail.setOnClickListener(this);

        cimg_Avatar_LRDA.setOnClickListener(this);
    }

    private RelativeLayout r_Parent;

    private void initView() {
        //两个listView,分享评论
        lv_Comment_LifeRewardDetail = (ListView) findViewById(R.id.lv_Comment_LifeRewardDetail);
        lv_Share_LifeRewardDetail = (ListView) findViewById(R.id.lv_Share_LifeRewardDetail);
        //MyScrollView
        msv_ScrollView_LRDA = (MyScrollView) findViewById(R.id.msv_ScrollView_LRDA);

        ll_ViewGroup1_LRDA = (LinearLayout) findViewById(R.id.ll_ViewGroup1_LRDA);
        ll_Top_LifeRewardDetail = (LinearLayout) findViewById(R.id.ll_Top_LifeRewardDetail);
        ll_ViewGroup2_LRDA = (LinearLayout) findViewById(R.id.ll_ViewGroup2_LRDA);
        ll_Location1_LifeRewardDetail = (LinearLayout) findViewById(R.id.ll_Location1_LifeRewardDetail);

        ll_Chat_LRDA = (LinearLayout) findViewById(R.id.ll_Chat_LRDA);
        ll_Comment_LRDA = (LinearLayout) findViewById(R.id.ll_Comment_LRDA);
        ll_Share_LRDA = (LinearLayout) findViewById(R.id.ll_Share_LRDA);
        ll_Back_LifeRewardDetail = (LinearLayout) findViewById(R.id.ll_Back_LifeRewardDetail);

        r_Parent = (RelativeLayout) findViewById(R.id.r_Parent);

        //评论和分享的两个TextView
        tv_ShareDetail_LRDA = (TextView) findViewById(R.id.tv_ShareDetail_LRDA);
        tv_CommentDetail_LRDA = (TextView) findViewById(R.id.tv_CommentDetail_LRDA);
        tv_QiangDan_LifeRewardDetail = (TextView) findViewById(R.id.tv_QiangDan_LifeRewardDetail);
        tv_Complaint_Detail = (TextView) findViewById(R.id.tv_Complaint_Detail);


        v_ShareLine_LRDA = findViewById(R.id.v_ShareLine_LRDA);
        v_CommentLine_LRDA = findViewById(R.id.v_CommentLine_LRDA);
        v_ParentCover_LRDA = findViewById(R.id.v_ParentCover_LRDA);


        cimg_Avatar_LRDA = (CircleImageView) findViewById(R.id.cimg_Avatar_LRDA);


    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            ////获得ll_ViewGroup1_LRDA的顶部
            viewGroupTop = ll_Location1_LifeRewardDetail.getBottom();
        }
    }

    //监听滚动Y值变化，通过addView和removeView来实现悬停效果
    @Override
    public void onScroll(int scrollY) {
        if (scrollY >= viewGroupTop) {
            if (ll_Top_LifeRewardDetail.getParent() != ll_ViewGroup2_LRDA) {
                ll_ViewGroup2_LRDA.setVisibility(View.VISIBLE);
                lv_Share_LifeRewardDetail.setFocusable(true);
                lv_Comment_LifeRewardDetail.setFocusable(true);
                ll_ViewGroup1_LRDA.removeView(ll_Top_LifeRewardDetail);
                ll_ViewGroup2_LRDA.addView(ll_Top_LifeRewardDetail);
            }
        } else if (ll_Top_LifeRewardDetail.getParent() != ll_ViewGroup1_LRDA) {
            ll_ViewGroup2_LRDA.setVisibility(View.GONE);
            lv_Share_LifeRewardDetail.setFocusable(false);
            lv_Comment_LifeRewardDetail.setFocusable(false);
            ll_ViewGroup2_LRDA.removeView(ll_Top_LifeRewardDetail);
            ll_ViewGroup1_LRDA.addView(ll_Top_LifeRewardDetail);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        ToastUtil.shortshow(this, "点击了第" + position + "个Item");
    }

    BaseUIlisener shareListener = new BaseUIlisener();

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_ShareDetail_LRDA:
                v_ShareLine_LRDA.setVisibility(View.VISIBLE);
                v_CommentLine_LRDA.setVisibility(View.INVISIBLE);
                lv_Share_LifeRewardDetail.setVisibility(View.VISIBLE);
                lv_Comment_LifeRewardDetail.setVisibility(View.GONE);
                tv_ShareDetail_LRDA.setTextColor(getResources().getColor(R.color.text_color_black));
                tv_CommentDetail_LRDA.setTextColor(getResources().getColor(R.color.text_color_hint));
                break;
            case R.id.tv_CommentDetail_LRDA:
                v_ShareLine_LRDA.setVisibility(View.INVISIBLE);
                v_CommentLine_LRDA.setVisibility(View.VISIBLE);
                lv_Share_LifeRewardDetail.setVisibility(View.GONE);
                lv_Comment_LifeRewardDetail.setVisibility(View.VISIBLE);
                tv_ShareDetail_LRDA.setTextColor(getResources().getColor(R.color.line_color));
                tv_CommentDetail_LRDA.setTextColor(getResources().getColor(R.color.text_color_black));
                break;
            case R.id.ll_Back_LifeRewardDetail:
                this.finish();
                break;
            case R.id.cimg_Avatar_LRDA:
                startActivity(LookOthersMassageActivity.class);
                break;
            case R.id.tv_Complaint_Detail:
                startActivity(ComplaintActivity.class);
                break;
            case R.id.tv_QiangDan_LifeRewardDetail:
                showPopupWindow(1);
                break;
            //底部点击事件
            case R.id.ll_Chat_LRDA:
//                ToastUtil.shortshow(this, "点击了私信");
                break;
            case R.id.ll_Share_LRDA:
                showPopupWindow(2);
                break;
            case R.id.ll_Comment_LRDA:
                showPopupWindow(3);
                break;
            //抢单弹框点击事件
            case R.id.tv_Cancel_LRDA_QiangDanPOP:
                popupWindoew.dismiss();
                break;
            case R.id.tv_Yes_LRDA_QiangDanPOP:
                popupWindoew.dismiss();
                ToastUtil.shortshow(this, "抢单成功，请等待");
                tv_QiangDan_LifeRewardDetail.setBackgroundResource(R.drawable.ic_qiandanedbg);
                tv_QiangDan_LifeRewardDetail.setText("已抢单");
                break;
            //分享弹窗点击事件
            case R.id.tv_Cancle_SharePop_LRDA:
                popupWindoew.dismiss();
                break;
            case R.id.img_WeiXin_SharePop_LRDA:

                break;
            case R.id.img_QQ_SharePop_LRDA:
                getmTencent().shareToQQ(this, getBundle(), shareListener);
                break;
            case R.id.img_XinLang_SharePop_LRDA:
//                Bitmap bitmap1 = BitmapFactory.decodeResource(getResources(),R.mipmap.logo_djx);
////                SendSharePost(shareInfo.id, 3);
//                sendMultiMessage(true, description, true, R.mipmap.logo_djx, true,
//                        false, false, false);
                break;
            //评论弹窗点击事件
            case R.id.tv_SendComment_Pop_LRDA:
//                String comment = edt_AddComment_Pop_LRDA.getText().toString().trim();
//                if (comment.equals("") || comment == null) {
//                    ToastUtil.shortshow(this, "评论内容不能为空");
//                } else {
//                    String newComment = edt_AddComment_Pop_LRDA.getText().toString();
//                    CommentInfo info = new CommentInfo(lifeRewarInfo.getAvatarID(), lifeRewarInfo.getUserName(), DateUtils.getCurrentDate(), newComment);
//                    commentAdapter.clear();
//                    commentAdapter.addData(info);
//                    initListViewCommentData();
//                    edt_AddComment_Pop_LRDA.clearFocus();
//                    InputMethodManager imm0 = (InputMethodManager) getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
//                    imm0.hideSoftInputFromWindow(edt_AddComment_Pop_LRDA.getWindowToken(), 0);
//                    popupWindoew.dismiss();
//                }
                break;
            //图片点击事件
            case R.id.img_Imge1_LifeRewardDetail:
                showPopupWindow(4);

                initPopImageView(1);

                break;
            case R.id.img_Imge2_1_LifeRewardDetail:
                showPopupWindow(4);

                initPopImageView(1);

                break;
            case R.id.img_Imge2_2_LifeRewardDetail:
                showPopupWindow(4);

                initPopImageView(2);

                break;
            case R.id.img_Imge3_1_LifeRewardDetail:
                initPopImageView(1);
                showPopupWindow(4);

                break;
            case R.id.img_Imge3_2_LifeRewardDetail:
                showPopupWindow(4);

                initPopImageView(2);

                break;
            case R.id.img_Imge3_3_LifeRewardDetail:
                showPopupWindow(4);

                initPopImageView(3);

                break;
            case R.id.img_Imge4_1_LifeRewardDetail:
                showPopupWindow(4);

                initPopImageView(1);

                break;
            case R.id.img_Imge4_2_LifeRewardDetail:
                showPopupWindow(4);

                initPopImageView(2);

                break;
            case R.id.img_Imge4_3_LifeRewardDetail:
                showPopupWindow(4);
                initPopImageView(3);


                break;
            case R.id.img_Imge4_4_LifeRewardDetail:
                showPopupWindow(4);
                initPopImageView(4);


                break;
            //浏览图片弹框的点击事件
            case R.id.ll_ShowImgeView_Pop_LRDA:
                popupWindoew.dismiss();
                break;
        }
    }

    private PopupWindow popupWindoew;
    private View popView;

    public void showPopupWindow(int i) {
        popupWindoew = new PopupWindow();
        popupWindoew.setFocusable(true);
        popupWindoew.setOutsideTouchable(true);
        popupWindoew.setTouchable(true);
        parentCover(1);
        //根据i的值弹出不同的popupwindow,1抢单；2分享；3评论。
        switch (i) {
            case 1:
                popupWindoew.setWidth((ScreenUtils.getScreenWidth(this) - ScreenUtils.getScreenWidth(this) / 9));
                popupWindoew.setHeight(ScreenUtils.getScreenHeight(this) / 4);
                popView = LayoutInflater.from(this).inflate(R.layout.popupwindow_lrda_qiangdan, null);
                popupWindoew.setBackgroundDrawable(getResources().getDrawable(R.color.transparent));
                //初始化抢单弹窗控件和事件
                initQiangDanPopView();
                initQiangDanPopEvent();
                break;
            case 2:
                popupWindoew.setWidth(ScreenUtils.getScreenWidth(this));
                popupWindoew.setHeight(ScreenUtils.getScreenHeight(this) / 4);
                popView = LayoutInflater.from(this).inflate(R.layout.popupwindow_lrda_share, null);
                popupWindoew.setBackgroundDrawable(getResources().getDrawable(R.color.transparent));
                //初始化分享弹窗控件和事件
                initSharePopView();
                initSharePopEvent();
                break;
            case 3:
                //防止PopupWindow被软件盘挡住
                popupWindoew.setSoftInputMode(PopupWindow.INPUT_METHOD_NEEDED);
                popupWindoew.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
                popupWindoew.setWidth(ScreenUtils.getScreenWidth(this));
                popupWindoew.setHeight(LinearLayout.LayoutParams.WRAP_CONTENT);
                popView = LayoutInflater.from(this).inflate(R.layout.popupwindow_lrda_comment, null);
                popupWindoew.setBackgroundDrawable(getResources().getDrawable(R.color.transparent));
                //初始化评论弹窗控件和事件
                initCommentPopView();
                initCommentPopEvent();
                break;
            case 4:
                popupWindoew.setWidth(ScreenUtils.getScreenWidth(this));
                popupWindoew.setHeight(ScreenUtils.getScreenHeight(this));
                popView = LayoutInflater.from(this).inflate(R.layout.popupwindow_lrda_showimageview, null);
                popupWindoew.setBackgroundDrawable(getResources().getDrawable(R.color.text_color_black));
                //初始化图片浏览弹窗控件和事件
                initShowImagePopView();
                initShowImageViewPopEvent();
                break;
        }
        popupWindoew.setContentView(popView);
        //不同popupwindow的位置
        switch (i) {
            case 1:
                popupWindoew.showAtLocation(tv_QiangDan_LifeRewardDetail, Gravity.CENTER, 0, 0);
                break;
            case 2:
                popupWindoew.showAtLocation(tv_QiangDan_LifeRewardDetail, Gravity.BOTTOM, 0, 0);
                break;
            case 3:
                popupWindoew.showAtLocation(tv_QiangDan_LifeRewardDetail, Gravity.BOTTOM, 0, 0);
                break;
            case 4:
                popupWindoew.showAtLocation(tv_QiangDan_LifeRewardDetail, Gravity.CENTER, 0, 0);
                break;
        }
        popupWindoew.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                parentCover(2);
            }
        });
    }

    private void initShowImageViewPopEvent() {
        ll_ShowImgeView_Pop_LRDA.setOnClickListener(this);
        vp_LRDA_Pop.addOnPageChangeListener(this);
    }

    /**
     * 浏览图片的弹窗框的控件和事件
     */
    private ViewPager vp_LRDA_Pop;
    private LinearLayout ll_ShowImgeView_Pop_LRDA;
    private TextView tv_ImageViewPosition_POP_LRDA, tv_ImageViewCount_POP_LRDA;
    private ShowImagePagerAdapter vp_Adapter;

    private void initShowImagePopView() {
        vp_LRDA_Pop = (ViewPager) popView.findViewById(R.id.vp_LRDA_Pop);
        ll_ShowImgeView_Pop_LRDA = (LinearLayout) popView.findViewById(R.id.ll_ShowImgeView_Pop_LRDA);
        tv_ImageViewPosition_POP_LRDA = (TextView) popView.findViewById(R.id.tv_ImageViewPosition_POP_LRDA);
        tv_ImageViewCount_POP_LRDA = (TextView) popView.findViewById(R.id.tv_ImageViewCount_POP_LRDA);
        vp_Adapter = new ShowImagePagerAdapter(this);
        initImageData();
        vp_LRDA_Pop.setAdapter(vp_Adapter);
    }

    /**
     * 加载图片
     */
    private void initImageData() {
        switch (i) {
            case 1:
                vp_Adapter.clear();
                PhotoView pv1 = new PhotoView(getApplicationContext());
                vp_Adapter.add(pv1);
                break;
            case 2:
                vp_Adapter.clear();
                PhotoView pv2 = new PhotoView(getApplicationContext());
                PhotoView pv3 = new PhotoView(getApplicationContext());
                vp_Adapter.add(pv2);
                vp_Adapter.add(pv3);
                break;
            case 3:
                vp_Adapter.clear();
                PhotoView pv4 = new PhotoView(getApplicationContext());
                PhotoView pv5 = new PhotoView(getApplicationContext());
                PhotoView pv6 = new PhotoView(getApplicationContext());
                vp_Adapter.add(pv4);
                vp_Adapter.add(pv5);
                vp_Adapter.add(pv6);
                break;
            case 4:
                vp_Adapter.clear();
                PhotoView pv7 = new PhotoView(getApplicationContext());
                PhotoView pv8 = new PhotoView(getApplicationContext());
                PhotoView pv9 = new PhotoView(getApplicationContext());
                PhotoView pv10 = new PhotoView(getApplicationContext());
                pv10.setImageResource(R.mipmap.vp1);
                vp_Adapter.add(pv7);
                vp_Adapter.add(pv8);
                vp_Adapter.add(pv9);
                vp_Adapter.add(pv10);
                break;
        }

    }

    /**
     * 根据点击的图片来初始化弹窗显示的当前图片
     */
    private void initPopImageView(int currentItem) {
        switch (currentItem) {
            case 1:
                vp_LRDA_Pop.setCurrentItem(0);
                break;
            case 2:
                vp_LRDA_Pop.setCurrentItem(1);
                break;
            case 3:
                vp_LRDA_Pop.setCurrentItem(2);
                break;
            case 4:
                vp_LRDA_Pop.setCurrentItem(3);
                break;
        }
        tv_ImageViewPosition_POP_LRDA.setText(vp_LRDA_Pop.getCurrentItem() + 1 + "");
        tv_ImageViewCount_POP_LRDA.setText(vp_Adapter.getCount() + "");
    }


    private void initCommentPopEvent() {
        tv_SendComment_Pop_LRDA.setOnClickListener(this);
    }

    private EditText edt_AddComment_Pop_LRDA;
    private TextView tv_SendComment_Pop_LRDA;

    /**
     * 评论的弹窗控件和事件
     */
    private void initCommentPopView() {
        edt_AddComment_Pop_LRDA = (EditText) popView.findViewById(R.id.edt_AddComment_Pop_LRDA);
        tv_SendComment_Pop_LRDA = (TextView) popView.findViewById(R.id.tv_SendComment_Pop_LRDA);
        edt_AddComment_Pop_LRDA.addTextChangedListener(this);
        edt_AddComment_Pop_LRDA.setFocusable(true);
        edt_AddComment_Pop_LRDA.setFocusableInTouchMode(true);
        edt_AddComment_Pop_LRDA.requestFocus();
        InputMethodManager imm = (InputMethodManager) getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(1000, InputMethodManager.HIDE_NOT_ALWAYS);

    }

    private void initSharePopEvent() {
        tv_Cancle_SharePop_LRDA.setOnClickListener(this);
        img_WeiXin_SharePop_LRDA.setOnClickListener(this);
        img_QQ_SharePop_LRDA.setOnClickListener(this);
        img_XinLang_SharePop_LRDA.setOnClickListener(this);
    }

    private TextView tv_Cancle_SharePop_LRDA;
    private ImageView img_WeiXin_SharePop_LRDA, img_QQ_SharePop_LRDA, img_XinLang_SharePop_LRDA;

    /**
     * 分享的弹窗控件和事件
     */
    private void initSharePopView() {
        tv_Cancle_SharePop_LRDA = (TextView) popView.findViewById(R.id.tv_Cancle_SharePop_LRDA);
        img_WeiXin_SharePop_LRDA = (ImageView) popView.findViewById(R.id.img_WeiXin_SharePop_LRDA);
        img_QQ_SharePop_LRDA = (ImageView) popView.findViewById(R.id.img_QQ_SharePop_LRDA);
        img_XinLang_SharePop_LRDA = (ImageView) popView.findViewById(R.id.img_XinLang_SharePop_LRDA);

    }

    private void initQiangDanPopEvent() {
        tv_Cancel_LRDA_QiangDanPOP.setOnClickListener(this);
        tv_Yes_LRDA_QiangDanPOP.setOnClickListener(this);
    }

    private TextView tv_Cancel_LRDA_QiangDanPOP, tv_Yes_LRDA_QiangDanPOP;

    private void initQiangDanPopView() {
        tv_Cancel_LRDA_QiangDanPOP = (TextView) popView.findViewById(R.id.tv_Cancel_LRDA_QiangDanPOP);
        tv_Yes_LRDA_QiangDanPOP = (TextView) popView.findViewById(R.id.tv_Yes_LRDA_QiangDanPOP);
    }

    public void parentCover(int i) {
        switch (i) {
            case 1:
                v_ParentCover_LRDA.setVisibility(View.VISIBLE);
                v_ParentCover_LRDA.setAlpha(0.5f);
                break;
            case 2:
                v_ParentCover_LRDA.setVisibility(View.GONE);
                break;
        }
    }

    public Bundle getBundle() {
        Bundle params = new Bundle();
        params.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, QQShare.SHARE_TO_QQ_TYPE_DEFAULT);
        params.putString(QQShare.SHARE_TO_QQ_TITLE, "Android四大组件简介");
        params.putString(QQShare.SHARE_TO_QQ_SUMMARY, "Android四大组件分别为activity、service、content provider、broadcast receiver...");
        params.putString(QQShare.SHARE_TO_QQ_TARGET_URL, "http://blog.csdn.net/android_hl/article/details/50183401");
        params.putString(QQShare.SHARE_TO_QQ_IMAGE_URL, "http://e.hiphotos.baidu.com/image/pic/item/d788d43f8794a4c2d17ff3ed0af41bd5ac6e3993.jpg");
        params.putString(QQShare.SHARE_TO_QQ_APP_NAME, "早起斗地主");
        return params;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        String comment = edt_AddComment_Pop_LRDA.getText().toString().trim();
        if (comment.equals("") || comment.length() == 0) {
            tv_SendComment_Pop_LRDA.setTextColor(getResources().getColor(R.color.text_color_hint));
            tv_SendComment_Pop_LRDA.setBackgroundResource(R.drawable.ic_qiandanedbg);
        } else {
            tv_SendComment_Pop_LRDA.setTextColor(getResources().getColor(R.color.white));
            tv_SendComment_Pop_LRDA.setBackgroundResource(R.drawable.ic_qiandanbg);
        }
    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        tv_ImageViewPosition_POP_LRDA.setText(position + 1 + "");
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    public class BaseUIlisener implements IUiListener {

        @Override
        public void onComplete(Object o) {
            ToastUtil.shortshow(getApplicationContext(), o.toString());
            Log.e("o", o.toString());
        }

        @Override
        public void onError(UiError uiError) {

        }

        @Override
        public void onCancel() {

        }
    }

    //特别注意：一定要添加以下代码，才可以从回调listener中获取到消息。
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constants.REQUEST_QQ_SHARE) {
            Tencent.onActivityResultData(requestCode, resultCode, data, shareListener);
        }
    }
}
