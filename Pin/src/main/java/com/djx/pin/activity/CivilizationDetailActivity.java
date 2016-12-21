package com.djx.pin.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.djx.pin.R;
import com.djx.pin.adapter.CivilizationDetailCommentAdapter;
import com.djx.pin.adapter.CivilizationDetailShareAdapter;
import com.djx.pin.adapter.ShowImagePagerAdapter;
import com.djx.pin.base.OldBaseActivity;
import com.djx.pin.beans.CivilizationDetailCommentInfo;
import com.djx.pin.beans.CivilizationDetailShareInfo;
import com.djx.pin.beans.StaticBean;
import com.djx.pin.myview.CircleImageView;
import com.djx.pin.personal.ui.PersonalDataActivity;
import com.djx.pin.serverapiconfig.ServerAPIConfig;
import com.djx.pin.sina.SinaConstants;
import com.djx.pin.utils.AndroidAsyncHttp;
import com.djx.pin.utils.BitmapUtil;
import com.djx.pin.utils.LogUtil;
import com.djx.pin.utils.MyViewUtils;
import com.djx.pin.utils.QiniuUtils;
import com.djx.pin.utils.ScreenUtils;
import com.djx.pin.utils.ToastUtil;
import com.djx.pin.utils.TurnIntoTime;
import com.djx.pin.utils.Util;
import com.djx.pin.weixin.WXConstants;
import com.djx.pin.widget.NineGridLayout;
import com.google.gson.Gson;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.sina.weibo.sdk.api.share.IWeiboShareAPI;
import com.sina.weibo.sdk.api.share.WeiboShareSDK;
import com.tencent.connect.common.Constants;
import com.tencent.connect.share.QQShare;
import com.tencent.mm.sdk.modelmsg.SendMessageToWX;
import com.tencent.mm.sdk.modelmsg.WXMediaMessage;
import com.tencent.mm.sdk.modelmsg.WXWebpageObject;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.utils.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;
import io.rong.imkit.RongIM;
import uk.co.senab.photoview.PhotoView;

/**
 * Created by Administrator on 2016/6/14.
 */
public class CivilizationDetailActivity extends OldBaseActivity implements AdapterView.OnItemClickListener, View.OnClickListener, TextWatcher, ViewPager.OnPageChangeListener, AbsListView.OnScrollListener, CivilizationDetailShareAdapter.OnShareAvatarListener, CivilizationDetailCommentAdapter.OnCommentAvatarListener {
    protected final static String TAG = CivilizationDetailActivity.class.getSimpleName();

    private PullToRefreshListView lv_CivilizationDetail;
    private CivilizationDetailShareAdapter shareAdapter;

    private CivilizationDetailCommentAdapter commentAdapter;


    private LinearLayout ll_ViewGroup1_CDA, ll_Top_CivilizationDetail, ll_ViewGroup2_CDA,
            ll_Location_CDA, ll_Chat_CDA, ll_Share_CDA, ll_Comment_CDA, ll_Back_CivilizationDetail, ll_ShowAllContent_MHDA;

    private TextView tv_ShareDetail_CDA, tv_CommentDetail_CDA, tv_UserName_CDA, tv_Time_CDA, tv_HelperAbout_CDA, tv_Location_CDA, tv_ShowAllContent_MHDA;
    private int viewGroupTop;
    private View v_ShareLine_CDA, v_CommentLine_CDA, v_ParentCover_CDA;

    private CircleImageView cimg_Avatar_CDA;
    private ImageView img_ShowAllContent_MHDA;
    //分享评论请求的页数
    int index_comment = 0;
    int index_share = 0;

    //分享评论总数
    int share_num;
    int comment_num;
    //被分享内容id
    String target_id;
    //被分享内容
    String target_content;
    private NineGridLayout nineGridLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_civilizationdetail);

        //每个布局中公用的控件的初始化和事件
        initView();
        initEvent();

        shareAdapter = new CivilizationDetailShareAdapter(this, this);
        commentAdapter = new CivilizationDetailCommentAdapter(this, this);
        lv_CivilizationDetail.setAdapter(commentAdapter);
        requsetBaseData();
        requsetComment(0);
        requestShare(0);
        lv_CivilizationDetail.setAdapter(commentAdapter);
    }


    //图片数量
    int j;
    Bundle bundle;

    View headView = null;

    private void initCivilization() {
        bundle = getIntent().getExtras();
       // j = bundle.getInt("imageNumber");

     /*
        if (j == 0) {
            //无图片的布局
            headView = LayoutInflater.from(this).inflate(R.layout.headview_civilizationdetail, lv_CivilizationDetail, false);
            cimg_Avatar_CDA = (CircleImageView) headView.findViewById(R.id.cimg_Avatar_CDA);
        } else if (j == 1) {
            //一张图片的布局
            headView = LayoutInflater.from(this).inflate(R.layout.headview_civilizationdetail_1, lv_CivilizationDetail, false);
            initImageView1();
            initImageViewEvent1();
        } else if (j == 2) {
            //两张图片的布局
            headView = LayoutInflater.from(this).inflate(R.layout.headview_civilizationdetail_2, lv_CivilizationDetail, false);
            initImageView2();
            initImageViewEvent2();
        } else if (j == 3) {
            //三张图片的布局
            headView = LayoutInflater.from(this).inflate(R.layout.headview_civilizationdetail_3, lv_CivilizationDetail, false);
            initImageView3();
            initImageViewEvent3();
        } else if (j == 4) {
            //四张图片的布局
            headView = LayoutInflater.from(this).inflate(R.layout.headview_civilizationdetail_4, lv_CivilizationDetail, false);
            initImageView4();
            initImageViewEvent4();
        }*/


        headView = LayoutInflater.from(this).inflate(R.layout.headview_civilizationdetail_1, lv_CivilizationDetail, false);

        AbsListView.LayoutParams layoutParams = new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT, AbsListView.LayoutParams.WRAP_CONTENT);
        headView.setLayoutParams(layoutParams);
        ListView listView = lv_CivilizationDetail.getRefreshableView();
        listView.addHeaderView(headView);

        //头布局部分控件
        ll_ViewGroup1_CDA = (LinearLayout) headView.findViewById(R.id.ll_ViewGroup1_CDA);
        ll_Top_CivilizationDetail = (LinearLayout) headView.findViewById(R.id.ll_Top_CivilizationDetail);

        ll_Location_CDA = (LinearLayout) headView.findViewById(R.id.ll_Location_CDA);

        ll_ShowAllContent_MHDA = (LinearLayout) headView.findViewById(R.id.ll_ShowAllContent_MHDA);
        //评论和分享的两个TextView
        tv_ShareDetail_CDA = (TextView) headView.findViewById(R.id.tv_ShareDetail_CDA);
        tv_CommentDetail_CDA = (TextView) headView.findViewById(R.id.tv_CommentDetail_CDA);
        tv_ShowAllContent_MHDA = (TextView) headView.findViewById(R.id.tv_ShowAllContent_MHDA);

        //顶部控件
        tv_UserName_CDA = (TextView) headView.findViewById(R.id.tv_UserName_CDA);
        tv_Time_CDA = (TextView) headView.findViewById(R.id.tv_Time_CDA);
        tv_HelperAbout_CDA = (TextView) headView.findViewById(R.id.tv_HelperAbout_CDA);
        tv_Location_CDA = (TextView) headView.findViewById(R.id.tv_Location_CDA);

        img_ShowAllContent_MHDA = (ImageView) headView.findViewById(R.id.img_ShowAllContent_MHDA);

        v_ShareLine_CDA = headView.findViewById(R.id.v_ShareLine_CDA);
        v_CommentLine_CDA = headView.findViewById(R.id.v_CommentLine_CDA);


        nineGridLayout = ((NineGridLayout) headView.findViewById(R.id.imgs_9grid_layout));


        MyViewUtils.viewShowAccordingLines(ll_ShowAllContent_MHDA,tv_HelperAbout_CDA);//根据行数控制隐藏显示
    }


    //图片id集合
    List<String> idList = new ArrayList<String>();

    CivilizationDetailCommentInfo info;

    public void requsetComment(int index_comment) {
        String session_id = getSharedPreferences(StaticBean.USER_INFO, Context.MODE_PRIVATE).getString("session_id", null);
        final String id = bundle.getString("id");
        // GET /v1/culture_wall/details?session_id=4085c9d4f420864c407e&id=312312jk1j3lk1l&type=1&index=0&size=10
        int index = index_comment;
        int size = 10;
        String url = ServerAPIConfig.LookCivilizationDetail + "session_id=" + session_id + "&id=" + id + "&type=" + 2 + "&index=" + index + "&size=" + size;

        AsyncHttpResponseHandler res = new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                String str = new String(bytes);
                Gson gson = new Gson();
                info = gson.fromJson(str, CivilizationDetailCommentInfo.class);
                if (info.code == 0) {
                    CivilizationDetailCommentInfo.Result result = info.result;
                    comment_num = result.comment_num;
                    tv_CommentDetail_CDA.setText("评论  " + result.comment_num + "");
                    initListViewCommentData(result);
                } else {
                    errorCode(info.code);
                }

            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
            }
        };
        AndroidAsyncHttp.get(url, res);
    }

    public void requsetBaseData() {
        String session_id = getSharedPreferences(StaticBean.USER_INFO, Context.MODE_PRIVATE).getString("session_id", null);
        final String id = bundle.getString("id");
        // GET /v1/culture_wall/details?session_id=4085c9d4f420864c407e&id=312312jk1j3lk1l&type=1&index=0&size=10
        int index = 0;
        int size = 10;
        String url = ServerAPIConfig.LookCivilizationDetail + "session_id=" + session_id + "&id=" + id + "&type=" + 2 + "&index=" + index + "&size=" + size;

        AsyncHttpResponseHandler res = new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                String str = new String(bytes);
                Log.i(TAG, "json is " + str);
                Gson gson = new Gson();
                info = gson.fromJson(str, CivilizationDetailCommentInfo.class);
                if (info.code == 0) {
                    initDetailData(info);
                } else {
                    errorCode(info.code);
                }
            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
            }
        };
        AndroidAsyncHttp.get(url, res);
    }


    public void initDetailData(CivilizationDetailCommentInfo info) {
        target_id = info.result.id;
        target_content = info.result.description;
        String nickname = info.result.nickname;
        long time = info.result.create_time;
        String helperAbout = info.result.description;
        String location = info.result.location;
        String create_time = TurnIntoTime.getCreateTime(time);
        tv_UserName_CDA.setText(nickname);
        tv_Time_CDA.setText(create_time);
        Log.i(TAG, "detail is " + helperAbout);
        tv_HelperAbout_CDA.setText(helperAbout);
        tv_Location_CDA.setText(location);

        int imageNumber = info.result.media.size();
        //图片id集合添加id


        for (int k = 0; k < imageNumber; k++) {
            CivilizationDetailCommentInfo.Result.Media media = info.result.media.get(k);
            idList.add(k, media.media_id);
        }
        QiniuUtils.setAvatarByIdFrom7Niu(this,cimg_Avatar_CDA,info.result.portrait);
        switch (imageNumber) {
            case 1:
                QiniuUtils.setOneImageByIdFrom7Niu(this,img_Imge1_CivilizationDetail,idList.get(0));
                break;
            case 2:
                QiniuUtils.setOneImageByIdFrom7Niu(this,img_Imge2_1_CivilizationDetail,idList.get(0));
                QiniuUtils.setOneImageByIdFrom7Niu(this,img_Imge2_2_CivilizationDetail,idList.get(1));
                break;
            case 3:
                QiniuUtils.setOneImageByIdFrom7Niu(this,img_Imge3_1_CivilizationDetail,idList.get(0));
                QiniuUtils.setOneImageByIdFrom7Niu(this,img_Imge3_2_CivilizationDetail,idList.get(1));
                QiniuUtils.setOneImageByIdFrom7Niu(this,img_Imge3_3_CivilizationDetail,idList.get(2));
                break;
            case 4:
                QiniuUtils.setOneImageByIdFrom7Niu(this,img_Imge4_1_CivilizationDetail,idList.get(0));
                QiniuUtils.setOneImageByIdFrom7Niu(this,img_Imge4_2_CivilizationDetail,idList.get(1));
                QiniuUtils.setOneImageByIdFrom7Niu(this,img_Imge4_3_CivilizationDetail,idList.get(2));
                QiniuUtils.setOneImageByIdFrom7Niu(this,img_Imge4_4_CivilizationDetail,idList.get(3));
                break;

        }


    }

    @Override
    protected void onStart() {
        super.onStart();
        boolean showCommentPop = bundle.getBoolean("showCommentPop");
        if (showCommentPop) {
            v_ShareLine_CDA.setVisibility(View.INVISIBLE);
            v_CommentLine_CDA.setVisibility(View.VISIBLE);
            tv_CommentDetail_CDA.setTextColor(getResources().getColor(R.color.text_color_black));
        }
    }

    private void initImageViewEvent4() {
        img_Imge4_1_CivilizationDetail.setOnClickListener(this);
        img_Imge4_2_CivilizationDetail.setOnClickListener(this);
        img_Imge4_3_CivilizationDetail.setOnClickListener(this);
        img_Imge4_4_CivilizationDetail.setOnClickListener(this);
    }

    private ImageView img_Imge4_1_CivilizationDetail, img_Imge4_2_CivilizationDetail, img_Imge4_3_CivilizationDetail, img_Imge4_4_CivilizationDetail;

    private void initImageView4() {
        cimg_Avatar_CDA = (CircleImageView) headView.findViewById(R.id.cimg_Avatar_CDA);


        img_Imge4_1_CivilizationDetail = (ImageView) headView.findViewById(R.id.img_Imge4_1_CivilizationDetail);
        img_Imge4_2_CivilizationDetail = (ImageView) headView.findViewById(R.id.img_Imge4_2_CivilizationDetail);
        img_Imge4_3_CivilizationDetail = (ImageView) headView.findViewById(R.id.img_Imge4_3_CivilizationDetail);
        img_Imge4_4_CivilizationDetail = (ImageView) headView.findViewById(R.id.img_Imge4_4_CivilizationDetail);

    }

    private void initImageViewEvent3() {
        img_Imge3_1_CivilizationDetail.setOnClickListener(this);
        img_Imge3_2_CivilizationDetail.setOnClickListener(this);
        img_Imge3_3_CivilizationDetail.setOnClickListener(this);
    }

    private ImageView img_Imge3_1_CivilizationDetail, img_Imge3_2_CivilizationDetail, img_Imge3_3_CivilizationDetail;

    private void initImageView3() {
        cimg_Avatar_CDA = (CircleImageView) headView.findViewById(R.id.cimg_Avatar_CDA);

        img_Imge3_1_CivilizationDetail = (ImageView) headView.findViewById(R.id.img_Imge3_1_CivilizationDetail);
        img_Imge3_2_CivilizationDetail = (ImageView) headView.findViewById(R.id.img_Imge3_2_CivilizationDetail);
        img_Imge3_3_CivilizationDetail = (ImageView) headView.findViewById(R.id.img_Imge3_3_CivilizationDetail);
    }

    private void initImageViewEvent2() {
        img_Imge2_1_CivilizationDetail.setOnClickListener(this);
        img_Imge2_2_CivilizationDetail.setOnClickListener(this);
    }

    private ImageView img_Imge2_1_CivilizationDetail, img_Imge2_2_CivilizationDetail;

    private void initImageView2() {
        cimg_Avatar_CDA = (CircleImageView) headView.findViewById(R.id.cimg_Avatar_CDA);
        img_Imge2_1_CivilizationDetail = (ImageView) headView.findViewById(R.id.img_Imge2_1_CivilizationDetail);
        img_Imge2_2_CivilizationDetail = (ImageView) headView.findViewById(R.id.img_Imge2_2_CivilizationDetail);

    }


    /**
     * 只有一个图片
     */
    private void initImageViewEvent1() {
        img_Imge1_CivilizationDetail.setOnClickListener(this);
    }

    private ImageView img_Imge1_CivilizationDetail;

    private void initImageView1() {
        cimg_Avatar_CDA = (CircleImageView) headView.findViewById(R.id.cimg_Avatar_CDA);
        img_Imge1_CivilizationDetail = (ImageView) headView.findViewById(R.id.img_Imge1_CivilizationDetail);

    }

    /**
     * 添加评论数据
     */
    private void initListViewCommentData(CivilizationDetailCommentInfo.Result result) {
        List<CivilizationDetailCommentInfo.Result.Comment> commentList = result.comment;


        if (index_comment > 0 && result.comment_num == commentAdapter.getList().size()) {
            ToastUtil.shortshow(getApplicationContext(), "没有更多内容");
            lv_CivilizationDetail.onRefreshComplete();
        } else {
            if (commentList.size() != 0) {
                for (int i = 0; i < commentList.size(); i++) {
                    commentAdapter.addData(commentList.get(i));
                }
                commentAdapter.notifyDataSetChanged();
                lv_CivilizationDetail.onRefreshComplete();
            }
        }


    }

    public void requestShare(int index_share) {
        String session_id = getSharedPreferences(StaticBean.USER_INFO, Context.MODE_PRIVATE).getString("session_id", null);

        final String id = bundle.getString("id");
        // GET /v1/culture_wall/details?session_id=4085c9d4f420864c407e&id=312312jk1j3lk1l&type=1&index=0&size=10
        int index = index_share;
        int size = 10;
        String url = ServerAPIConfig.LookCivilizationDetail + "session_id=" + session_id + "&id=" + id + "&type=" + 1 + "&index=" + index + "&size=" + size;

        AsyncHttpResponseHandler res = new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                String str = new String(bytes);
                Gson gson = new Gson();
                CivilizationDetailShareInfo info = gson.fromJson(str, CivilizationDetailShareInfo.class);
                if (info.code == 0) {
                    CivilizationDetailShareInfo.Result result = info.result;
                    share_num = result.share_num;
                    tv_ShareDetail_CDA.setText("分享  " + result.share_num + "");
                    initListViewShareData(result);
                } else {
                    errorCode(info.code);
                }

            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
            }
        };
        AndroidAsyncHttp.get(url, res);

    }

    /**
     * 分享数据
     *
     * @param result
     */
    private void initListViewShareData(CivilizationDetailShareInfo.Result result) {


        if (index_share > 0 && result.share_num == shareAdapter.getList().size()) {
            ToastUtil.shortshow(getApplicationContext(), "没有更多内容");
            lv_CivilizationDetail.onRefreshComplete();
        } else {
            for (int i = 0; i < result.share.size(); i++) {
                CivilizationDetailShareInfo.Result.Share share = result.share.get(i);
                shareAdapter.addData(share);
            }
            shareAdapter.notifyDataSetChanged();
            lv_CivilizationDetail.onRefreshComplete();
        }
    }

    /**
     * 公用控件和事件
     */
    private void initEvent() {
        //tv_ShareDetail_CDA.setOnClickListener(this);
        tv_CommentDetail_CDA.setOnClickListener(this);
        ll_Chat_CDA.setOnClickListener(this);
        ll_Comment_CDA.setOnClickListener(this);
        ll_Share_CDA.setOnClickListener(this);
        ll_Back_CivilizationDetail.setOnClickListener(this);
//        tv_QiangDan_CivilizationDetail.setOnClickListener(this);

        cimg_Avatar_CDA.setOnClickListener(this);


        lv_CivilizationDetail.setMode(PullToRefreshBase.Mode.PULL_FROM_END);

        lv_CivilizationDetail.setOnRefreshListener(refreshListener);

        lv_CivilizationDetail.setOnScrollListener(this);

        ll_ShowAllContent_MHDA.setOnClickListener(this);
    }

    PullToRefreshBase.OnRefreshListener<ListView> refreshListener = new PullToRefreshBase.OnRefreshListener<ListView>() {
        @Override
        public void onRefresh(PullToRefreshBase<ListView> refreshView) {
            switch (context_type) {
                case 1:
                    index_share++;
                    requestShare(index_share);
                    break;
                case 2:
                    index_comment++;
                    requsetComment(index_comment);
                    break;
            }
        }
    };

    private void initView() {
        //civilization布局部分控件
        ll_Back_CivilizationDetail = (LinearLayout) findViewById(R.id.ll_Back_CivilizationDetail);
        lv_CivilizationDetail = (PullToRefreshListView) findViewById(R.id.lv_CivilizationDetail);
        ll_Chat_CDA = (LinearLayout) findViewById(R.id.ll_Chat_CDA);
        ll_Comment_CDA = (LinearLayout) findViewById(R.id.ll_Comment_CDA);
        ll_Share_CDA = (LinearLayout) findViewById(R.id.ll_Share_CDA);
        ll_ViewGroup2_CDA = (LinearLayout) findViewById(R.id.ll_ViewGroup2_CDA);
        v_ParentCover_CDA = findViewById(R.id.v_ParentCover_CDA);
        //加入头布局
        initCivilization();

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
    }

    BaseUIlisener shareListener = new BaseUIlisener();

    //正在显示的内容
    int context_type = 2;
    boolean isShowAll = true;

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
           /* case R.id.tv_ShareDetail_CDA:
                v_ShareLine_CDA.setVisibility(View.VISIBLE);
                v_CommentLine_CDA.setVisibility(View.INVISIBLE);
                tv_CommentDetail_CDA.setTextColor(getResources().getColor(R.color.line_color));

                lv_CivilizationDetail.setAdapter(shareAdapter);
                context_type = 1;
                break;*/
            case R.id.tv_CommentDetail_CDA:
                v_ShareLine_CDA.setVisibility(View.INVISIBLE);
                v_CommentLine_CDA.setVisibility(View.VISIBLE);
                tv_CommentDetail_CDA.setTextColor(getResources().getColor(R.color.text_color_black));

                lv_CivilizationDetail.setAdapter(commentAdapter);
                context_type = 2;
                break;

            case R.id.ll_Back_CivilizationDetail:
                this.finish();
                break;
            case R.id.ll_ShowAllContent_MHDA:
                if (isShowAll) {
                    tv_ShowAllContent_MHDA.setText(R.string.content_pack_up);
                    tv_HelperAbout_CDA.setMaxLines(100);
                    img_ShowAllContent_MHDA.setImageResource(R.mipmap.ic_upclose);
                    isShowAll = false;
                } else {
                    tv_HelperAbout_CDA.setMaxLines(6);
                    tv_ShowAllContent_MHDA.setText("展开全文");
                    img_ShowAllContent_MHDA.setImageResource(R.mipmap.ic_downopen);
                    isShowAll = true;
                }
                break;
            case R.id.cimg_Avatar_CDA:
                String user_id = info.result.user_id;
                String nickName = info.result.nickname;
                if (user_id.equals(getUser_id())) {
                    startActivity(PersonalDataActivity.class);
                } else {
                    Bundle bundle = new Bundle();
                    bundle.putString("user_id", user_id);
                    bundle.putString("nickName", nickName);
                    startActivity(LookOthersMassageActivity.class, bundle);
                }
                break;
            case R.id.tv_QiangDan_CivilizationDetail:
                showPopupWindow(1);
                break;
            //底部点击事件，点击底部事件时让listView失去焦点
            case R.id.ll_Chat_CDA:

                //检查用户是否登录,未登录则return;已经实名认证的用户才可以举报
                if (getSharedPreferences(StaticBean.USER_INFO, MODE_PRIVATE).getBoolean("isLogined", false) == false) {
                    ToastUtil.shortshow(this, R.string.toast_non_login);
                    return;
                }
                //检查是否是同一用户
                if (info.result.user_id.equals(getUser_id())) {
                    ToastUtil.shortshow(getApplicationContext(), R.string.toast_error_talk);
                    return;
                }

                //启动会话界面
                if (RongIM.getInstance() != null)
                    startRecordByPermissions();//请求录音权限操作  ---> 获取成功后执行 excuteActionContainRecordPermision()
                    //RongIM.getInstance().startPrivateChat(this, info.result.user_id, info.result.nickname);

                break;
            case R.id.ll_Share_CDA:
                showPopupWindow(2);
                break;
            case R.id.ll_Comment_CDA:
                showPopupWindow(3);
                break;

            //分享弹窗点击事件
            case R.id.tv_Cancle_SharePop_LRDA:
                popupWindoew.dismiss();
                break;
            case R.id.img_WeiXinF_SharePop_LRDA:
                wxflag=true;
                regToWx();
                getWXInfo();
                SendSharePost(target_id,1);

                Bitmap bitmap= BitmapFactory.decodeResource(getResources(),R.mipmap.logo_djx);
                WXShareWeb(ServerAPIConfig.CivilizationShare+target_id,"众觅互助",target_content,bitmap,50,50);
                break;
            case R.id.img_WeiXin_SharePop_LRDA:
                wxflag=false;
                regToWx();
                getWXInfo();
                SendSharePost(target_id,1);
                Bitmap bitmap1= BitmapFactory.decodeResource(getResources(),R.mipmap.logo_djx);
                WXShareWeb(ServerAPIConfig.CivilizationShare+target_id,"众觅互助",target_content,bitmap1,50,50);
                break;
            case R.id.img_QQ_SharePop_LRDA:
                getmTencent().shareToQQ(this, getBundle(), shareListener);
                break;
            case R.id.img_XinLang_SharePop_LRDA:
//                //检查用户是否登录,未登录则return;
//                if (getSharedPreferences(StaticBean.USER_INFO, MODE_PRIVATE).getBoolean("isLogined", false) == false) {
//                    ToastUtil.shortshow(this, R.string.toast_non_login);
//                    return;
//                }
//                registerApp();
//                //检查是否安装新浪微博
//                if (!mWeiboShareAPI.isWeiboAppInstalled()) {
//                    ToastUtil.shortshow(this, R.string.toast_sina_share_uninstalled);
//                    return;
//                }
//                //检查用户是否登录,未登录则return;
//                if (getSharedPreferences(StaticBean.USER_INFO, MODE_PRIVATE).getBoolean("isLogined", false) == false) {
//                    ToastUtil.shortshow(this, R.string.toast_non_login);
//                    return;
//                }
//
                Bitmap bitmap2 = BitmapFactory.decodeResource(getResources(),R.mipmap.logo_djx);
                SendSharePost(target_id, 3);
                sendMultiMessage(true, target_content, true, BitmapUtil.Bitmap2Bytes(bitmap2), true,
                        false, false, false);
                break;
            //评论弹窗点击事件
            case R.id.tv_SendComment_Pop_LRDA:
                String content = edt_AddComment_Pop_LRDA.getText().toString();
                if (content.equals("") || content == null) {
                    ToastUtil.shortshow(this, "评论内容不能为空");
                } else {
                    SendCommnetPost(target_id, content);

                }
                break;
            //图片点击事件
            case R.id.img_Imge1_CivilizationDetail:
                showPopupWindow(4);
                initPopImageView(1);
                break;
            case R.id.img_Imge2_1_CivilizationDetail:

                showPopupWindow(4);
                initPopImageView(1);
                break;
            case R.id.img_Imge2_2_CivilizationDetail:

                showPopupWindow(4);
                initPopImageView(2);
                break;
            case R.id.img_Imge3_1_CivilizationDetail:

                showPopupWindow(4);
                initPopImageView(1);
                break;
            case R.id.img_Imge3_2_CivilizationDetail:

                showPopupWindow(4);
                initPopImageView(2);
                break;
            case R.id.img_Imge3_3_CivilizationDetail:

                showPopupWindow(4);
                initPopImageView(3);
                break;
            case R.id.img_Imge4_1_CivilizationDetail:

                showPopupWindow(4);
                initPopImageView(1);

                break;
            case R.id.img_Imge4_2_CivilizationDetail:

                showPopupWindow(4);
                initPopImageView(2);
                break;
            case R.id.img_Imge4_3_CivilizationDetail:

                showPopupWindow(4);
                initPopImageView(3);
                break;
            case R.id.img_Imge4_4_CivilizationDetail:

                showPopupWindow(4);
                initPopImageView(4);
                break;
            //浏览图片弹框的点击事件
            case R.id.ll_ShowImgeView_Pop_LRDA:
                popupWindoew.dismiss();
                break;
        }
    }

    /**
     * 获取权限后执行聊天
     */
    @Override
    public void excuteActionContainRecordPermision() {
        RongIM.getInstance().startPrivateChat(this, info.result.user_id, info.result.nickname);
    }

    /**
     * 创建微博分享接口实例,并进行注册.
     */
    private IWeiboShareAPI mWeiboShareAPI;//新浪分享

    public void registerApp() {
        mWeiboShareAPI = WeiboShareSDK.createWeiboAPI(this, SinaConstants.APP_KEY);
        // 将营养注册到微博客户端
        mWeiboShareAPI.registerApp();
    }

//    /**
//     * 考虑到分享的内容只需要文字+图片.所以只实现了这两个功能.其他的视频 音频暂未实现
//     *
//     * @param hasText    是否有文字
//     * @param text       文字内容
//     * @param hasImage   是否有图片
//     * @param imgByte    图片(是byte[]类型的图片)
//     * @param hasWebpage 是否有网页(暂未实现)
//     * @param hasMusic   是否有音乐(暂未实现)
//     * @param hasVideo   是否有视频(暂未实现)
//     * @param hasVoice   是否有声音(暂未实现)
//     */
//    private void sendMultiMessage(Boolean hasText, String text,
//                                  Boolean hasImage, byte[] imgByte, Boolean hasWebpage,
//                                  Boolean hasMusic, Boolean hasVideo, Boolean hasVoice) {
//        // 初始化微博的分享消息
//        WeiboMultiMessage weiboMessage = new WeiboMultiMessage();
//
//
//        if (hasText) {
//            LogUtil.e("有文字");
//            TextObject textObject = new TextObject();
//            textObject.text = text;
//            weiboMessage.textObject = textObject;
//        }
//        if (hasImage) {
//            LogUtil.e("有图片");
//            ImageObject imageObject = new ImageObject();
//            //设置缩略图。 注意：最终压缩过的缩略图大小不得超过 32kb。
//            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.logo_djx);
//            imageObject.setImageObject(bitmap);
//            weiboMessage.imageObject = imageObject;
//        }
//        if (hasWebpage) {
//            LogUtil.e("有网页");
//            WebpageObject webpageObject = new WebpageObject();
//            webpageObject.identify = Utility.generateGUID();
//            webpageObject.title = "\n--众觅互助";
//            webpageObject.description = target_content;
//            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.logo_djx);
//            // 设置 Bitmap 类型的图片到视频对象里         设置缩略图。 注意：最终压缩过的缩略图大小不得超过 32kb。
//            webpageObject.setThumbImage(bitmap);
//            webpageObject.actionUrl = ServerAPIConfig.CivilizationShare+target_id;
//            webpageObject.defaultText = "众觅互助";
//            weiboMessage.mediaObject = webpageObject;
//        }
//
//        SendMultiMessageToWeiboRequest request = new SendMultiMessageToWeiboRequest();
//        request.transaction = String.valueOf(System.currentTimeMillis());
//        request.multiMessage = weiboMessage;
//        // 发送请求消息到微博,唤起微博分享界面
//        mWeiboShareAPI.sendRequest(CivilizationDetailActivity.this, request);
//    }

    private void sendMultiMessage(Boolean hasText, String text,
                                  Boolean hasImage, byte[] imgByte, Boolean hasWebpage,
                                  Boolean hasMusic, Boolean hasVideo, Boolean hasVoice) {
        ShareAction shareAction = new ShareAction(this);
        if (hasText) {
            shareAction.withText(text);
        }
        if (hasImage) {
            LogUtil.e("有图片");
            UMImage image = new UMImage(this, imgByte);
            shareAction.withMedia(image);
        }

        if (hasWebpage) {
            LogUtil.e("有网页");
            shareAction.withTargetUrl(ServerAPIConfig.CivilizationShare + target_id);
        }
        shareAction.withTitle("众觅互助");

        shareAction.setPlatform(SHARE_MEDIA.SINA).setCallback(umShareListener).share();
    }

    private void sendMultiMessage(Boolean hasText, String text,
                                  Boolean hasImage, int imgid, Boolean hasWebpage,
                                  Boolean hasMusic, Boolean hasVideo, Boolean hasVoice) {
        ShareAction shareAction = new ShareAction(this);
        if (hasText) {
            shareAction.withText(text);
        }
        if (hasImage) {
            LogUtil.e("有图片");
            UMImage image = new UMImage(this, imgid);
            shareAction.withMedia(image);
        }

        if (hasWebpage) {
            LogUtil.e("有网页");
            shareAction.withTargetUrl(ServerAPIConfig.CivilizationShare + target_id);
        }
        shareAction.withTitle("众觅互助");

        shareAction.setPlatform(SHARE_MEDIA.SINA).setCallback(umShareListener).share();
    }

    private UMShareListener umShareListener = new UMShareListener() {
        @Override
        public void onResult(SHARE_MEDIA platform) {
            com.umeng.socialize.utils.Log.d("plat","platform"+platform);
            if(platform.name().equals("WEIXIN_FAVORITE")){
                Toast.makeText(CivilizationDetailActivity.this,platform + " 收藏成功啦",Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(CivilizationDetailActivity.this, platform + " 分享成功啦", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void onError(SHARE_MEDIA platform, Throwable t) {
            Toast.makeText(CivilizationDetailActivity.this,platform + " 分享失败啦", Toast.LENGTH_SHORT).show();
            if(t!=null){
                com.umeng.socialize.utils.Log.d("throw","throw:"+t.getMessage());
            }
        }

        @Override
        public void onCancel(SHARE_MEDIA platform) {
            Toast.makeText(CivilizationDetailActivity.this,platform + " 分享取消了", Toast.LENGTH_SHORT).show();
        }
    };
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
                popupWindoew.showAtLocation(v_ParentCover_CDA, Gravity.CENTER, 0, 0);
                break;
            case 2:
                popupWindoew.showAtLocation(v_ParentCover_CDA, Gravity.BOTTOM, 0, 0);
                break;
            case 3:
                popupWindoew.showAtLocation(v_ParentCover_CDA, Gravity.BOTTOM, 0, 0);
                break;
            case 4:
                popupWindoew.showAtLocation(v_ParentCover_CDA, Gravity.CENTER, 0, 0);
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

    List<PhotoView> photoViewList = new ArrayList<PhotoView>();

    private void initImageData() {
        photoViewList.clear();
        switch (j) {
            case 1:
                vp_Adapter.clear();
                PhotoView pv1 = new PhotoView(getApplicationContext());
                photoViewList.add(pv1);
                vp_Adapter.add(pv1);
                break;
            case 2:
                vp_Adapter.clear();
                PhotoView pv2 = new PhotoView(getApplicationContext());
                PhotoView pv3 = new PhotoView(getApplicationContext());
                photoViewList.add(pv2);
                photoViewList.add(pv3);
                vp_Adapter.add(pv2);
                vp_Adapter.add(pv3);
                break;
            case 3:
                vp_Adapter.clear();
                PhotoView pv4 = new PhotoView(getApplicationContext());
                PhotoView pv5 = new PhotoView(getApplicationContext());
                PhotoView pv6 = new PhotoView(getApplicationContext());

                photoViewList.add(pv4);
                photoViewList.add(pv5);
                photoViewList.add(pv6);

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

                photoViewList.add(pv7);
                photoViewList.add(pv8);
                photoViewList.add(pv9);
                photoViewList.add(pv10);

                vp_Adapter.add(pv7);
                vp_Adapter.add(pv8);
                vp_Adapter.add(pv9);
                vp_Adapter.add(pv10);
                break;
        }

        try {
            getPhotoViewUrl(photoViewList, j, idList, 1);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
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
        img_WeiXinF_SharePop_LRDA.setOnClickListener(this);
    }

    private TextView tv_Cancle_SharePop_LRDA;
    private ImageView img_WeiXin_SharePop_LRDA, img_QQ_SharePop_LRDA, img_XinLang_SharePop_LRDA,img_WeiXinF_SharePop_LRDA;

    /**
     * 分享的弹窗控件和事件
     */
    private void initSharePopView() {
        tv_Cancle_SharePop_LRDA = (TextView) popView.findViewById(R.id.tv_Cancle_SharePop_LRDA);
        img_WeiXin_SharePop_LRDA = (ImageView) popView.findViewById(R.id.img_WeiXin_SharePop_LRDA);
        img_QQ_SharePop_LRDA = (ImageView) popView.findViewById(R.id.img_QQ_SharePop_LRDA);
        img_XinLang_SharePop_LRDA = (ImageView) popView.findViewById(R.id.img_XinLang_SharePop_LRDA);
        img_WeiXinF_SharePop_LRDA = (ImageView) popView.findViewById(R.id.img_WeiXinF_SharePop_LRDA);

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
                v_ParentCover_CDA.setVisibility(View.VISIBLE);
                v_ParentCover_CDA.setAlpha(0.5f);
                break;
            case 2:
                v_ParentCover_CDA.setVisibility(View.GONE);
                break;
        }
    }

    public Bundle getBundle() {
        Bundle params = new Bundle();
        params.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, QQShare.SHARE_TO_QQ_TYPE_DEFAULT);
        params.putString(QQShare.SHARE_TO_QQ_TITLE, "众觅互助");
        params.putString(QQShare.SHARE_TO_QQ_SUMMARY, target_content);
        params.putString(QQShare.SHARE_TO_QQ_TARGET_URL, ServerAPIConfig.CivilizationShare + target_id);
        params.putString(QQShare.SHARE_TO_QQ_IMAGE_URL, ServerAPIConfig.Logo);
        params.putString(QQShare.SHARE_TO_QQ_APP_NAME, "众觅");
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
            tv_SendComment_Pop_LRDA.setBackgroundResource(R.drawable.shape_sendbt_gray);
        } else {
            tv_SendComment_Pop_LRDA.setTextColor(getResources().getColor(R.color.white));
            tv_SendComment_Pop_LRDA.setBackgroundResource(R.drawable.shape_sendbt_blue);
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

    /**
     * Callback method to be invoked while the list view or grid view is being scrolled. If the
     * view is being scrolled, this method will be called before the next frame of the scroll is
     * rendered. In particular, it will be called before any calls to
     * {@link "Adapter#getView(int, View, ViewGroup)}.
     *
     * @param view        The view whose scroll state is being reported
     * @param scrollState The current scroll state. One of
     *                    {@link #SCROLL_STATE_TOUCH_SCROLL} or {@link #SCROLL_STATE_IDLE}.
     */
    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {

    }

    /**
     * Callback method to be invoked when the list or grid has been scrolled. This will be
     * called after the scroll has completed
     *
     * @param view             The view whose scroll state is being reported
     * @param firstVisibleItem the index of the first visible cell (ignore if
     *                         visibleItemCount == 0)
     * @param visibleItemCount the number of visible cells
     * @param totalItemCount   the number of items in the list adaptor
     */
    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        if (firstVisibleItem >= 2) {
            if (ll_Top_CivilizationDetail.getParent() != ll_ViewGroup2_CDA) {
                ll_ViewGroup2_CDA.setVisibility(View.VISIBLE);
                ll_ViewGroup1_CDA.removeView(ll_Top_CivilizationDetail);
                ll_ViewGroup2_CDA.addView(ll_Top_CivilizationDetail);
            }
        } else {
            if (ll_Top_CivilizationDetail.getParent() != ll_ViewGroup1_CDA) {
                ll_ViewGroup2_CDA.setVisibility(View.GONE);
                ll_ViewGroup2_CDA.removeView(ll_Top_CivilizationDetail);
                ll_ViewGroup1_CDA.addView(ll_Top_CivilizationDetail);
            }
        }


    }



    // 字段flag是用来判断分享类型的,true表示分享至朋友圈,false表示分享至好友.
    Boolean wxflag = false;
    /**
     * 微信分享网页
     *
     * @param webpageUrl  分享网页的url
     * @param title       网页标题
     * @param description 网页描述
     * @param bmp         网页缩略图
     * @param dstWidth    缩略图的宽度
     * @param dstHeight   缩略图的高度
     */
    public void WXShareWeb(String webpageUrl, String title, String description,
                           Bitmap bmp, int dstWidth, int dstHeight) {

        // 初始化一个WXWebpageObject对象,填写url;
        WXWebpageObject webpage = new WXWebpageObject();
        webpage.webpageUrl = webpageUrl;
        //
        WXMediaMessage msg = new WXMediaMessage(webpage);
        // 网页标题
        msg.title = title;
        // 网页描述
        msg.description = description;
        // 网页缩略图
        Bitmap thumbBmp = Bitmap.createScaledBitmap(bmp, dstWidth, dstHeight,
                true);
        bmp.recycle();
        msg.thumbData = Util.bmpToByteArray(thumbBmp, true);

        // 构造一个req
        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = "视屏" + String.valueOf(System.currentTimeMillis());
        req.message = msg;

        // 判断flag的值
        req.scene = wxflag ? SendMessageToWX.Req.WXSceneTimeline : SendMessageToWX.Req.WXSceneSession;
        // 只有一种情况不能发送请求,即用户选择发送至朋友圈,但微信版本不支持.所以对该情况进行判断.其他情况都需要发送请求
        if (wxflag == true && isWXAppSupportAPI == false) {

            ToastUtil.shortshow(this, "当前微信版本较低,暂不支持分享至朋友圈");
            return;
        }
        // 调用api接口发送数据到微信
        wxapi.sendReq(req);
    }
    // IWXAPI是第三方app和微信通信的openapi接口
    private IWXAPI wxapi;

    // 将应用的appid注册到微信
    private void regToWx() {
        // 通过WXAPIFactory工厂,获取IWXAPI的实例
        wxapi = WXAPIFactory.createWXAPI(this, WXConstants.APP_ID, true);
        // 将应用的appid注册到微信
        wxapi.registerApp(WXConstants.APP_ID);

    }

    Boolean isWXAppInstalled = false;// 用来判断是否安装了微信
    Boolean isWXAppSupportAPI = false;// 判断是否支持分享到朋友圈

    // 获取用户是否安装微信 以及安装的微信版本是否支持分享到朋友圈
    public void getWXInfo() {
        // 检查是否安装微信
        if (wxapi.isWXAppInstalled()) {
            isWXAppInstalled = true;
        } else {
            ToastUtil.shortshow(this, R.string.toast_weixin_share_uninstalled);
            isWXAppInstalled = false;
        }
        // 检查微信是否支持分享到朋友圈
        if (wxapi.getWXAppSupportAPI() >= 0x21020001) {
            isWXAppSupportAPI = true;
        } else {
            isWXAppSupportAPI = false;
        }

    }
    /**
     * 评论头像点击查看资料
     * type=1,点击头像
     */


    @Override
    public void setOnShareAvatarListener(View view, int type) {
        CivilizationDetailShareInfo.Result.Share info = shareAdapter.getItem((Integer) view.getTag());
        switch (type) {
            case 1:
                String user_id = info.user_id;
                String nickName = info.nickname;
                Bundle bundle = new Bundle();
                bundle.putString("user_id", user_id);
                bundle.putString("nickName", nickName);
                startActivity(LookOthersMassageActivity.class, bundle);
                break;

        }
    }

    @Override
    public void setOnCommentAvatarListener(View view, int type) {
        CivilizationDetailCommentInfo.Result.Comment info = commentAdapter.getItem((Integer) view.getTag());
        switch (type) {
            case 1:
                String user_id = info.user_id;
                String nickName = info.nickname;
                Bundle bundle = new Bundle();
                bundle.putString("user_id", user_id);
                bundle.putString("nickName", nickName);
                startActivity(LookOthersMassageActivity.class, bundle);
                break;
        }
    }

    public class BaseUIlisener implements IUiListener {

        @Override
        public void onComplete(Object o) {
            try {
                JSONObject obj = new JSONObject(o.toString());
                if (obj.getInt("ret") == 0) {
                    SendSharePost(target_id, 2);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onError(UiError uiError) {

        }

        @Override
        public void onCancel() {

        }
    }

    /**
     * {
     * "session_id": "4085c9d4f420864c407e",
     * "target_id": 4085c91231j23kl407e,
     * "target_type": 1,
     * "type": 1,
     * }
     */

    private void SendSharePost(String target_id, final int type) {

        String url = ServerAPIConfig.SendSharePost;

        RequestParams params = new RequestParams();

        params.put("session_id", getSession_id());

        params.put("target_id", target_id);

        //被分享内容类别，1-求助，2-文明墙，7-网络悬赏
        params.put("target_type", 2);
        //分享类型，1-分享微信，2-分享QQ，3-分享微博
        params.put("type", type);
        AsyncHttpResponseHandler res = new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                String str = new String(bytes);
                try {
                    JSONObject object = new JSONObject(str);
                    if (object.getInt("code") == 0) {
                        shareAdapter.clear();
                        requestShare(0);
                        popupWindoew.dismiss();
                    } else {
                        errorCode(object.getInt("code"));
                        popupWindoew.dismiss();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {

            }
        };
        AndroidAsyncHttp.post(url, params, res);
    }

    /**
     * {
     * "session_id": "4085c9d4f420864c407e",       用户会话id
     * "target_id": 4085c91231j23kl407e,           被评论内容id
     * "target_type": 2,                           被评论内容类别，1-求助，2-文明墙，7-网络悬赏
     * "content": "评论内容",                      评论内容，不能为空
     * }
     */
    private void SendCommnetPost(String target_id, String content) {
        String url = ServerAPIConfig.UPdata_Comment;

        RequestParams params = new RequestParams();

        params.put("session_id", getSession_id());

        params.put("target_id", target_id);

        //被评论内容类别，1-求助，2-文明墙，7-网络悬赏
        params.put("target_type", 2);
        //评论内容
        params.put("content", content);
        AsyncHttpResponseHandler res = new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                String str = new String(bytes);
                try {
                    JSONObject object = new JSONObject(str);
                    if (object.getInt("code") == 0) {
                        commentAdapter.clear();
                        requsetComment(0);
                        popupWindoew.dismiss();
                    } else if(2113 == object.getInt("code")){
                        ToastUtil.longshow(getApplicationContext(),getString(R.string.sensitive_word));
                    }else {
                        errorCode(object.getInt("code"));
                        popupWindoew.dismiss();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {

            }
        };
        AndroidAsyncHttp.post(url, params, res);

    }

    //特别注意：一定要添加以下代码，才可以从回调listener中获取到消息。
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constants.REQUEST_QQ_SHARE) {

            Tencent.onActivityResultData(requestCode, resultCode, data, shareListener);
        }
    }
}
