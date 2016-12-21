package com.djx.pin.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.djx.pin.R;
import com.djx.pin.adapter.LifeRewardOnLineCommentAdapter;
import com.djx.pin.adapter.LifeRewardOnLineShareAdapter;
import com.djx.pin.adapter.LifeRewardOnLineUpload_MediaAdapter;
import com.djx.pin.adapter.LifeRewardOnLineUpload_OtherAdapter;
import com.djx.pin.base.OldBaseActivity;
import com.djx.pin.beans.IDTokenInfo;
import com.djx.pin.beans.LifeRewardOnlineDetailInfo;
import com.djx.pin.beans.PhotoBrowseEntity;
import com.djx.pin.beans.StaticBean;
import com.djx.pin.beans.UserInfo;
import com.djx.pin.improve.common.PhotoShowActivity;
import com.djx.pin.improve.detail.OrderDetailActivity;
import com.djx.pin.myview.CircleImageView;
import com.djx.pin.personal.ui.PersonalDataActivity;
import com.djx.pin.serverapiconfig.ServerAPIConfig;
import com.djx.pin.sina.SinaConstants;
import com.djx.pin.utils.AndroidAsyncHttp;
import com.djx.pin.utils.BitmapUtil;
import com.djx.pin.utils.GridItemDecoration;
import com.djx.pin.utils.LogUtil;
import com.djx.pin.utils.MyItemDecoration;
import com.djx.pin.utils.QiniuUtils;
import com.djx.pin.utils.ScreenUtils;
import com.djx.pin.utils.ToastUtil;
import com.djx.pin.utils.TurnIntoTime;
import com.djx.pin.utils.Util;
import com.djx.pin.utils.myutils.ConstantUtils;
import com.djx.pin.weixin.WXConstants;
import com.djx.pin.widget.CustomImageView;
import com.djx.pin.widget.NineGridLayout;
import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.qiniu.android.http.ResponseInfo;
import com.qiniu.android.storage.UpCompletionHandler;
import com.qiniu.android.storage.UploadManager;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;
import io.rong.imkit.RongIM;

/**
 * Created by Administrator on 2016/7/27 0027.
 */
public class LifeRewardOnLineDetailActivity extends OldBaseActivity implements View.OnClickListener, TextWatcher,
        LifeRewardOnLineShareAdapter.OnShareAvatarListener, LifeRewardOnLineCommentAdapter.OnCommentAvatarListener, LifeRewardOnLineUpload_MediaAdapter.OnLookImageListener {
    protected final static String TAG = LifeRewardOnLineDetailActivity.class.getSimpleName();

    LinearLayout ll_Back_LRDA, ll_ViewGroup2_CDA, ll_Chat_CDA, ll_Share_CDA, ll_Comment_CDA;

    LifeRewardOnLineUpload_MediaAdapter mediaAdapter;
    LifeRewardOnLineUpload_OtherAdapter otherAdapter;

    LifeRewardOnLineShareAdapter shareAdapter;
    LifeRewardOnLineCommentAdapter commentAdapter;

    RecyclerView rv_recyclerView;

    LinearLayoutManager linearLayoutManager;
    GridLayoutManager gridLayoutManager;

    View v_ParentCover_CDA;
    LifeRewardOnlineDetailInfo.Result.Receiver jsonData;
    List<LifeRewardOnlineDetailInfo.Result.Receiver> receiverList;

    //悬赏内容Id
    String id;
    //悬赏内容
    String description;
    //网络悬赏类型
    int tsakType;
    //图片数量
    int imageNumber;
    private List<LifeRewardOnlineDetailInfo.Result.Partake> partakeList;//分享人的资料
    private int total_status;//总的订单状态
    private int process_status;
    private boolean otherFlag;
    private NineGridLayout nineGridLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        id = getIntent().getStringExtra("id");
        tsakType = getIntent().getIntExtra("type", 1);
        imageNumber = getIntent().getIntExtra("imageNumber", 0);
        total_status = getIntent().getIntExtra("total_status", -1);//总的订单状态


        setContentView(R.layout.activity_liferewarddetailonline);
        initView();
        initEvent();




    }

    private void initEvent() {
        ll_Back_LRDA.setOnClickListener(this);
        ll_Chat_CDA.setOnClickListener(this);
        ll_Comment_CDA.setOnClickListener(this);
        ll_Share_CDA.setOnClickListener(this);
    }


    //当前正在显示的界面,1:分享;2,评论;3,已上传
    int showTab = 3;

    //请求的页数
    int index_upload = 0;

    /**
     * 头部点击事件
     */
    private void initHeadEnvent() {
        cimg_Avatar_HPDA.setOnClickListener(this);
        tv_Upload_CDA.setOnClickListener(this);
        tv_CommentDetail_CDA.setOnClickListener(this);
        tv_QiangDan_LifeRewardDetail.setOnClickListener(this);
    }


    private void initView() {


        rv_recyclerView = (RecyclerView) findViewById(R.id.rv_recyclerView);
        ll_Back_LRDA = (LinearLayout) findViewById(R.id.ll_Back_LRDA);
        ll_ViewGroup2_CDA = (LinearLayout) findViewById(R.id.ll_ViewGroup2_CDA);
        ll_Chat_CDA = (LinearLayout) findViewById(R.id.ll_Chat_CDA);
        ll_Comment_CDA = (LinearLayout) findViewById(R.id.ll_Comment_CDA);
        ll_Share_CDA = (LinearLayout) findViewById(R.id.ll_Share_CDA);

        v_ParentCover_CDA = findViewById(R.id.v_ParentCover_CDA);

        gridLayoutManager = new GridLayoutManager(this, 3);
        linearLayoutManager = new LinearLayoutManager(this);
        rv_recyclerView.setLayoutManager(gridLayoutManager);

        mediaAdapter = new LifeRewardOnLineUpload_MediaAdapter(this,this);
        mediaAdapter.setMediaType(tsakType);
        mediaAdapter.setTaskStatus(total_status);


        otherAdapter = new LifeRewardOnLineUpload_OtherAdapter(this);
        otherAdapter.setTaskStatus(total_status);

        shareAdapter = new LifeRewardOnLineShareAdapter(this, this);
        commentAdapter = new LifeRewardOnLineCommentAdapter(this, this);

        addHeadView();



    }

    View headView;

    CircleImageView cimg_Avatar_HPDA;
    TextView tv_UserName1_LifeReward, tv_Time1_LifeReward, tv_QiangDan_LifeRewardDetail, tv_HelperPrice_LifeRewardDetail, tv_HelperPeopleNum_LifeRewardDetail, tv_Content_LDA,
            tv_LookTimes, tv_JoinNumber, tv_Upload_CDA, tv_HelperPeriod_LifeRewardDetail, tv_ShareDetail_CDA, tv_CommentDetail_CDA, tv_content_type;
    LinearLayout  ll_ViewGroup1_CDA, ll_Top_CivilizationDetail;
    View v_UploadLine_CDA, v_ShareLine_CDA, v_CommentLine_CDA;

    View footerView;

    //悬赏的类型，1:照片，2:视频，3:其他


    /**
     * 添加头布局
     */
    private void addHeadView() {
        /**
         * 给头部设置大小，必须要有
         * */
        AbsListView.LayoutParams layoutParams = new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        headView = LayoutInflater.from(this).inflate(R.layout.headview_liferewarddetailonline, rv_recyclerView, false);
        headView.setLayoutParams(layoutParams);

        footerView = LayoutInflater.from(this).inflate(R.layout.footer_loadmore, rv_recyclerView, false);
        footerView.setLayoutParams(layoutParams);
        shareAdapter.addHeader(headView);
        commentAdapter.addHeader(headView);


        shareAdapter.addFooter(footerView);
        commentAdapter.addFooter(footerView);
        //头部view和底部view
        initHeaderView();
        initHeadEnvent();
        initFooterView();

        //基本资料信息
        requestBaseData();

        switch (tsakType) {
            case 1://图片
                rv_recyclerView.setLayoutManager(gridLayoutManager);
                mediaAdapter.addHeader(headView);
                mediaAdapter.addFooter(footerView);
                mediaAdapter.setTaskStatus(total_status);
                rv_recyclerView.setAdapter(mediaAdapter);
                requestUploadMedia(0);
                break;
            case 2://视频
                requestUploadVideo(0);
                rv_recyclerView.setLayoutManager(gridLayoutManager);
                mediaAdapter.addHeader(headView);
                mediaAdapter.addFooter(footerView);
                mediaAdapter.setTaskStatus(total_status);
                rv_recyclerView.setAdapter(mediaAdapter);

                break;
            case 3://其它
                requestUploadOther(0);
                rv_recyclerView.setLayoutManager(linearLayoutManager);
                otherAdapter.addHeader(headView);
                otherAdapter.addFooter(footerView);
                otherAdapter.setTaskStatus(total_status);
                rv_recyclerView.setAdapter(otherAdapter);
                break;
        }
        requestShareData(0);
        requestComment(0);
        decor = new MyItemDecoration(this);
        gridItemDecoration = new GridItemDecoration(this, true);
        rv_recyclerView.addItemDecoration(decor);
    }


    //底部加载更多控件
    TextView tv_LoadMore;

    private void initFooterView() {
        tv_LoadMore = (TextView) footerView.findViewById(R.id.tv_LoadMore);
        tv_LoadMore.setVisibility(View.GONE);
    }

    MyItemDecoration decor;
    GridItemDecoration gridItemDecoration;

    private void initHeaderView() {
        //头部控件初始化
        cimg_Avatar_HPDA = (CircleImageView) headView.findViewById(R.id.cimg_Avatar_HPDA);
        tv_UserName1_LifeReward = (TextView) headView.findViewById(R.id.tv_UserName1_LifeReward);
        tv_Time1_LifeReward = (TextView) headView.findViewById(R.id.tv_Time1_LifeReward);
        tv_HelperPrice_LifeRewardDetail = (TextView) headView.findViewById(R.id.tv_HelperPrice_LifeRewardDetail);
        tv_QiangDan_LifeRewardDetail = (TextView) headView.findViewById(R.id.tv_QiangDan_LifeRewardDetail);
        tv_HelperPeopleNum_LifeRewardDetail = (TextView) headView.findViewById(R.id.tv_HelperPeopleNum_LifeRewardDetail);
        tv_Content_LDA = (TextView) headView.findViewById(R.id.tv_Content_LDA);
        tv_content_type = (TextView) headView.findViewById(R.id.tv_content_type);
        tv_LookTimes = (TextView) headView.findViewById(R.id.tv_LookTimes);
        tv_JoinNumber = (TextView) headView.findViewById(R.id.tv_JoinNumber);
        tv_Upload_CDA = (TextView) headView.findViewById(R.id.tv_Upload_CDA);
        tv_HelperPeriod_LifeRewardDetail = (TextView) headView.findViewById(R.id.tv_HelperPeriod_LifeRewardDetail);
        tv_ShareDetail_CDA = (TextView) headView.findViewById(R.id.tv_ShareDetail_CDA);
        tv_CommentDetail_CDA = (TextView) headView.findViewById(R.id.tv_CommentDetail_CDA);
        ll_ViewGroup1_CDA = (LinearLayout) headView.findViewById(R.id.ll_ViewGroup1_CDA);
        ll_Top_CivilizationDetail = (LinearLayout) headView.findViewById(R.id.ll_Top_CivilizationDetail);
        v_UploadLine_CDA = headView.findViewById(R.id.v_UploadLine_CDA);
        v_ShareLine_CDA = headView.findViewById(R.id.v_ShareLine_CDA);
        v_CommentLine_CDA = headView.findViewById(R.id.v_CommentLine_CDA);
        nineGridLayout = ((NineGridLayout) headView.findViewById(R.id.imgs_9grid_layout));

    }


    /**
     * 添加媒体信息
     * <p/>
     * 后面有接口的话要分为图片和视频
     * session_id 	String
     * <p/>
     * 用户会话id，可选
     * id 	String
     * <p/>
     * 悬赏id
     * index 	Number
     * <p/>
     * 分页页码，从0开始取
     * size 	Number
     * <p/>
     * 每页的数量
     * type 	Number
     * <p/>
     * 类型，1-分享列表，2-评论列表，3-回答列表
     */
    private void requestShareData(final int index_share) {

        String session_id = getSharedPreferences(StaticBean.USER_INFO, Context.MODE_PRIVATE).getString("session_id", null);

        final int index = index_share;
        int size = 10;
        int type = 1;

        String url = ServerAPIConfig.OnlineReward + "session_id=" + session_id + "&id=" + id + "&index=" + index + "&size=" + size + "&type=" + type + "";
        Log.e("info==", url);
        final AsyncHttpResponseHandler res = new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                String str = new String(bytes);
                //Log.e("info==", str);
                Gson gson = new Gson();
                LifeRewardOnlineDetailInfo info = gson.fromJson(str, LifeRewardOnlineDetailInfo.class);
                if (info.code == 0) {

                    LifeRewardOnlineDetailInfo.Result result = info.result;
                    tv_ShareDetail_CDA.setText("分享" + result.share_num);
                    List<LifeRewardOnlineDetailInfo.Result.Share> share = result.share;
                    partakeList = result.partake;
                    share_num = result.share_num;
                    tv_ShareDetail_CDA.setText("分享" + result.share_num + "");
                    initShareData(share);
                    Log.e("info==", info.toString());

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
     * 添加评论数据
     */
    public void requestComment(final int index_comment) {
        String session_id = getSharedPreferences(StaticBean.USER_INFO, Context.MODE_PRIVATE).getString("session_id", null);

        final int index = index_comment;
        int size = 10;
        int type = 2;

        String url = ServerAPIConfig.OnlineReward + "session_id=" + session_id + "&id=" + id + "&index=" + index + "&size=" + size + "&type=" + type + "";

        final AsyncHttpResponseHandler res = new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                String str = new String(bytes);
                Log.e("str=========", str);
                Gson gson = new Gson();
                LifeRewardOnlineDetailInfo info = gson.fromJson(str, LifeRewardOnlineDetailInfo.class);
                if (info.code == 0) {

                    LifeRewardOnlineDetailInfo.Result result = info.result;

                    comment_num = result.comment_num;

                    tv_CommentDetail_CDA.setText("评论" + result.comment_num + "");


                    List<LifeRewardOnlineDetailInfo.Result.Comment> comment = result.comment;
                    initCommentData(comment);

                    Log.e("info===", info.toString());
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
     * 添加已上传数据
     */
    public void requestUploadOther(int index_upload) {
        String session_id = getSharedPreferences(StaticBean.USER_INFO, Context.MODE_PRIVATE).getString("session_id", null);
        final int index = index_upload;
        int size = 9;
        int type = 3;
        String url = ServerAPIConfig.OnlineReward + "session_id=" + session_id + "&id=" + id + "&index=" + index + "&size=" + size + "&type=" + type + "";

        final AsyncHttpResponseHandler res = new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                String str = new String(bytes);
                Log.e("str=========", str);
                Gson gson = new Gson();
                LifeRewardOnlineDetailInfo info = gson.fromJson(str, LifeRewardOnlineDetailInfo.class);
                if (info.code == 0) {
                    LifeRewardOnlineDetailInfo.Result result = info.result;

                    int receiver_limit = result.receiver_limit;//接单者上限
                    int confirm_num = result.confirm_num;//得赏人数
                    otherAdapter.setConfirmed2LimitNum(confirm_num,receiver_limit);//设置已打赏人数


                    receiver_num = result.receiver_num;
                    tv_Upload_CDA.setText("已上传" + result.receiver_num + "");

                    List<LifeRewardOnlineDetailInfo.Result.Receiver> receiver = result.receiver;
                    initUploadOtherData(receiver);
                    Log.e("info===", info.toString());
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
     * 添加已上传视频
     */
    public void requestUploadVideo(int index_upload) {
        String session_id = getSharedPreferences(StaticBean.USER_INFO, Context.MODE_PRIVATE).getString("session_id", null);
        final int index = index_upload;
        int size = 9;
        int type = 3;
        String url = ServerAPIConfig.OnlineReward + "session_id=" + session_id + "&id=" + id + "&index=" + index + "&size=" + size + "&type=" + type + "";

        final AsyncHttpResponseHandler res = new AsyncHttpResponseHandler() {
            private int order_status;

            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                String str = new String(bytes);
                Log.e("str=========", str);
                Gson gson = new Gson();
                LifeRewardOnlineDetailInfo info = gson.fromJson(str, LifeRewardOnlineDetailInfo.class);
                if (info.code == 0) {
                    LifeRewardOnlineDetailInfo.Result result = info.result;
                    receiver_num = result.receiver_num;

                    int receiver_limit = result.receiver_limit;//接单者上限
                    int confirm_num = result.confirm_num;//得赏人数
                    mediaAdapter.setConfirmed2LimitNum(confirm_num,receiver_limit);//设置已打赏人数

                    order_status = result.status;
                    tv_Upload_CDA.setText("已上传" + result.receiver_num + "");
                    List<LifeRewardOnlineDetailInfo.Result.Receiver> receiver = result.receiver;

                    for (int j = 0; j < receiver.size(); j++) {
                        LifeRewardOnlineDetailInfo.Result.Receiver receiver0 = receiver.get(j);
                        mediaAdapter.addData(receiver0);
                    }
                    mediaAdapter.notifyDataSetChanged();


                    Log.e("info===", info.toString());
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
     * 添加已上传数据
     */
    public void requestUploadMedia(int index_upload) {
        String session_id = getSharedPreferences(StaticBean.USER_INFO, Context.MODE_PRIVATE).getString("session_id", null);
        final int index = index_upload;
        int size = 9;
        int type = 3;
        String url = ServerAPIConfig.OnlineReward + "session_id=" + session_id + "&id=" + id + "&index=" + index + "&size=" + size + "&type=" + type + "";
        Log.e("kcq",url);

        final AsyncHttpResponseHandler res = new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                String str = new String(bytes);
                Gson gson = new Gson();
                LifeRewardOnlineDetailInfo info = gson.fromJson(str, LifeRewardOnlineDetailInfo.class);
                if (info.code == 0) {
                    LifeRewardOnlineDetailInfo.Result result = info.result;

                    receiver_num = result.receiver_num;

                    int receiver_limit = result.receiver_limit;//接单者上限
                    int confirm_num = result.confirm_num;//得赏人数
                    mediaAdapter.setConfirmed2LimitNum(confirm_num,receiver_limit);//设置已打赏人数


                    tv_Upload_CDA.setText("已上传" + result.receiver_num + "");
                    List<LifeRewardOnlineDetailInfo.Result.Receiver> receiver = result.receiver;
                    receiverList = receiver;

                    for (int j = 0; j < receiver.size(); j++) {
                        LifeRewardOnlineDetailInfo.Result.Receiver receiver0 = receiver.get(j);
                        mediaAdapter.addData(receiver0);
                    }
                    mediaAdapter.notifyDataSetChanged();

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
     * 添加已上传数据
     */
    String shareUrl;

    public void requestBaseData() {
        String session_id = getSharedPreferences(StaticBean.USER_INFO, Context.MODE_PRIVATE).getString("session_id", null);

        final int index = index_upload;
        int size = 9;
        int type = 3;

        String url = ServerAPIConfig.OnlineReward + "session_id=" + session_id + "&id=" + id + "&index=" + index + "&size=" + size + "&type=" + type + "";
        shareUrl = url;
        final AsyncHttpResponseHandler res = new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                String str = new String(bytes);
                Log.e("str=========", str);
                Gson gson = new Gson();
                LifeRewardOnlineDetailInfo info = gson.fromJson(str, LifeRewardOnlineDetailInfo.class);
                if (info.code == 0) {
                    LifeRewardOnlineDetailInfo.Result result = info.result;
                    //添加基本信息
                    initBaseDate(result);
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
     * 添加公共部分基本控件
     */
    //已上传人数,分享人数,评论人数
    int receiver_num;
    int share_num;
    int comment_num;
    //被分享内容id
    String target_id;

    //订单状态
    int taskStutes;
    //发单者user_id
    String task_user_id;
    //发单者昵称
    String task_nickName;

    private void initBaseDate(LifeRewardOnlineDetailInfo.Result result) {
        target_id = result.id;
        description=result.description;
        taskStutes = result.status;
        task_user_id = result.user_id;
        process_status = result.process_status;
        QiniuUtils.setAvatarByIdFrom7Niu(LifeRewardOnLineDetailActivity.this,cimg_Avatar_HPDA, result.portrait);
        task_nickName = result.nickname;
        tv_UserName1_LifeReward.setText(result.nickname);

        if (task_user_id.equals(getUser_id())) {//自己
            tv_QiangDan_LifeRewardDetail.setText("查看");
        } else if (taskStutes != 2) {
            if (result.process_status == -1) {//只有在非发单人查看时才有，订单处理状态，-1-未抢单，0-已抢单（已回答），1-已打赏，2-拒绝打赏
                tv_QiangDan_LifeRewardDetail.setText("抢单");
            }else {
                tv_QiangDan_LifeRewardDetail.setText("查看");
                otherFlag = true;
            }
        } else {//订单已关闭
            if(result.process_status != -1){//已过抢单
                tv_QiangDan_LifeRewardDetail.setText("查看");
                otherFlag = true;
            }else {
                tv_QiangDan_LifeRewardDetail.setText(R.string.order_closed);
                tv_QiangDan_LifeRewardDetail.setBackgroundResource(R.drawable.ic_qiandanedbg);
            }
        }

        tv_HelperPrice_LifeRewardDetail.setText(result.price + "元");
        tv_HelperPeopleNum_LifeRewardDetail.setText(result.receiver_limit + "");
        String create_time = TurnIntoTime.getLifeTime(result.create_time);
        String end_time = TurnIntoTime.getLifeTime(result.end_time);
        tv_HelperPeriod_LifeRewardDetail.setText(create_time + "至" + end_time);
        tv_Time1_LifeReward.setText(TurnIntoTime.getCreateTime(result.create_time));
        //1-照片悬赏，2-视频悬赏，3-文字悬赏（其他悬赏）
        switch(result.type) {
            case 1:
                tv_content_type.setText("图片悬赏：");
                break;
            case 2:
                tv_content_type.setText("视频悬赏：");
                break;
            case 3:
                tv_content_type.setText("其他悬赏：");
                break;
            default:
                break;
        }
        tv_Content_LDA.setText(result.description);
        tv_LookTimes.setText(result.view_num + "次浏览");
        if (result.partake.size() == 0) {
            tv_JoinNumber.setText("暂时无人参与");
        } else {
            tv_JoinNumber.setText(result.partake.get(0).nickname + "等" + result.partake.size() + "人参与");
        }

        final ArrayList<String> ids = new ArrayList<>();
        if (result.media != null && result.media.size() > 0) {
            for (int i = 0; i < result.media.size(); i++) {
                ids.add(result.media.get(i).media_id);
            }
            QiniuUtils.set9GridByIdsFrom7Niu(this, ids, result.id, nineGridLayout);
            nineGridLayout.setImageShowListener(new NineGridLayout.ImageShowListener() {
                @Override
                public void imageShow(int imgPos,ArrayList<CustomImageView> imageViews) {
                    if (ids != null) {
                        Intent intent = new Intent(LifeRewardOnLineDetailActivity.this, PhotoShowActivity.class);
                        intent.putExtra("CURRENT_POS", imgPos);
                        intent.putStringArrayListExtra("IDS", ids);
                        startActivity(intent);
                    }
                }
            });
        }
    }

    /**
     * 添加其它数据
     */
    private void initUploadOtherData(List<LifeRewardOnlineDetailInfo.Result.Receiver> receiver) {
        for (int i = 0; i < receiver.size(); i++) {
            LifeRewardOnlineDetailInfo.Result.Receiver info = receiver.get(i);
            otherAdapter.addData(info);
        }
        otherAdapter.notifyDataSetChanged();

    }

    /**
     * 添加分享数据
     *
     * @param share
     */
    private void initShareData(List<LifeRewardOnlineDetailInfo.Result.Share> share) {

//        MyHelperOnLineShareInfo info = new MyHelperOnLineShareInfo("分享");
        for (int i = 0; i < share.size(); i++) {
            LifeRewardOnlineDetailInfo.Result.Share shareInfo = share.get(i);
            shareAdapter.addData(shareInfo);
        }
        shareAdapter.notifyDataSetChanged();

    }

    /**
     * 添加评论数据
     *
     * @param comment
     */
    private void initCommentData(List<LifeRewardOnlineDetailInfo.Result.Comment> comment) {
//        MyHelperOnLineShareInfo info = new MyHelperOnLineShareInfo("评论");
        for (int i = 0; i < comment.size(); i++) {
            LifeRewardOnlineDetailInfo.Result.Comment commentInfo = comment.get(i);
            commentAdapter.addData(commentInfo);
        }
        commentAdapter.notifyDataSetChanged();

    }
    private boolean checkUserAuth() {
        if (UserInfo.getMobile(this) == null || UserInfo.getMobile(this).length() == 0) {
            ToastUtil.shortshow(this, "请您先绑定手机号");
            return false;
        }
        return true;
    }

    /**
     * Called when a view has been clicked.
     *
     * @param v The view that was clicked.
     */

    //是否全文显示
    boolean isShowAll = true;
    //qq分享回调监听
    BaseUIlisener shareListener = new BaseUIlisener();
    Intent intent;
    private File tempFile = new File(Environment.getExternalStorageDirectory(), getPhotoFileName());
    //bundle用来传递播放视频的id
    Bundle bundle;
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            //返回
            case R.id.ll_Back_LRDA:
                this.finish();
                break;

            //点击头像
            case R.id.cimg_Avatar_HPDA:
                String user_id = getUser_id();
                if (task_user_id.equals(user_id)) {
                    startActivity(PersonalDataActivity.class);
                } else {
                    Bundle bundle = new Bundle();
                    bundle.putString("user_id", task_user_id);
                    bundle.putString("nickName", task_nickName);
                    startActivity(LookOthersMassageActivity.class, bundle);
                }
                break;
            //点击回复
            case R.id.tv_QiangDan_LifeRewardDetail:
                if (!checkUserAuth()) {
                    return;
                }
                if(tv_QiangDan_LifeRewardDetail.getText().toString().trim().equals("查看")){//自己单
                    if(otherFlag){//抢单者查看别人单
                        Bundle bundle = new Bundle();
                        bundle.putString("id",target_id);
                        bundle.putInt("status",taskStutes);//0-发布未支付，1-发单成功，2-订单结束（停止招募）
                        bundle.putInt("process_status",process_status);
                        startActivity(RewardOnLineDetailActivity.class,bundle);
                    }else {
                        Bundle bundle_Online = new Bundle();
                        bundle_Online.putString("id", target_id);
                        bundle_Online.putInt("status",taskStutes);
                        bundle_Online.putInt("UI", ConstantUtils.MY_HELP);
                        startActivity(OrderDetailActivity.class, bundle_Online);
                    }
                }else if (taskStutes != 2) {
                    switch (tsakType) {
                        case 1:
                            showPopupWindow(1);//照片
                            break;
                        case 2:
                            showPopupWindow(6);//视频
                            break;
                        case 3:
                            ToastUtil.shortshow(this, "文本上传");
                            showPopupWindow(5);
                            break;
                    }
                } else {
                    ToastUtil.shortshow(getApplicationContext(), "悬赏已结束");
                }

                break;

            //查看上传
            case R.id.tv_Upload_CDA:
                showTab = 3;
                v_UploadLine_CDA.setVisibility(View.VISIBLE);
                tv_Upload_CDA.setTextColor(getResources().getColor(R.color.text_color_black));
                tv_ShareDetail_CDA.setTextColor(getResources().getColor(R.color.text_color_normal));
                tv_CommentDetail_CDA.setTextColor(getResources().getColor(R.color.text_color_normal));
                v_UploadLine_CDA.setBackgroundColor(getResources().getColor(R.color.text_color_focused));
                v_CommentLine_CDA.setVisibility(View.INVISIBLE);
                v_ShareLine_CDA.setVisibility(View.INVISIBLE);

                //如果悬浮则不显示第一个item
                //如果不是悬浮则直接返回

                switch (tsakType) {
                    case 1:
                        rv_recyclerView.setLayoutManager(gridLayoutManager);
                       /* mediaAdapter.setStatus(taskStutes);//设置订单总状态*/
                        rv_recyclerView.setAdapter(mediaAdapter);
                        break;
                    case 2:
                        rv_recyclerView.setLayoutManager(linearLayoutManager);
                      /*  otherAdapter.setTaskStatus(taskStutes);*/
                       // rv_recyclerView.setAdapter(otherAdapter);
                        rv_recyclerView.setAdapter(mediaAdapter);
                        break;
                    case 3:
                        rv_recyclerView.setLayoutManager(linearLayoutManager);
                       /* otherAdapter.setTaskStatus(taskStutes);*/
                        rv_recyclerView.setAdapter(otherAdapter);
                        break;
                }
                break;

            //查看分享
            case R.id.tv_ShareDetail_CDA:

                showTab = 1;
                tv_Upload_CDA.setTextColor(getResources().getColor(R.color.text_color_normal));
                tv_ShareDetail_CDA.setTextColor(getResources().getColor(R.color.text_color_black));
                tv_CommentDetail_CDA.setTextColor(getResources().getColor(R.color.text_color_normal));
                v_UploadLine_CDA.setVisibility(View.INVISIBLE);
                v_CommentLine_CDA.setVisibility(View.INVISIBLE);
                v_ShareLine_CDA.setVisibility(View.VISIBLE);
                v_ShareLine_CDA.setBackgroundColor(getResources().getColor(R.color.text_color_focused));

                if (ll_Top_CivilizationDetail.getParent() == ll_ViewGroup2_CDA) {
                    rv_recyclerView.setLayoutManager(linearLayoutManager);
                    rv_recyclerView.setAdapter(shareAdapter);
                } else {
                    rv_recyclerView.setLayoutManager(linearLayoutManager);
                    rv_recyclerView.setAdapter(shareAdapter);
                }

                break;

            //查看评论
            case R.id.tv_CommentDetail_CDA:
                showTab = 2;
                tv_Upload_CDA.setTextColor(getResources().getColor(R.color.text_color_normal));
                tv_ShareDetail_CDA.setTextColor(getResources().getColor(R.color.text_color_normal));
                tv_CommentDetail_CDA.setTextColor(getResources().getColor(R.color.text_color_black));

                v_UploadLine_CDA.setVisibility(View.INVISIBLE);
                v_CommentLine_CDA.setVisibility(View.VISIBLE);
                v_CommentLine_CDA.setBackgroundColor(getResources().getColor(R.color.text_color_focused));
                v_ShareLine_CDA.setVisibility(View.INVISIBLE);

                if (ll_Top_CivilizationDetail.getParent() == ll_ViewGroup2_CDA) {

                    rv_recyclerView.setLayoutManager(linearLayoutManager);

                    rv_recyclerView.setAdapter(commentAdapter);

                } else {

                    rv_recyclerView.setLayoutManager(linearLayoutManager);

                    rv_recyclerView.setAdapter(commentAdapter);
                }
                break;

            //底部点击事件，点击底部事件时让listView失去焦点
            case R.id.ll_Chat_CDA:

                //检查用户是否登录,未登录则return;已经实名认证的用户才可以举报
                if (getSharedPreferences(StaticBean.USER_INFO, MODE_PRIVATE).getBoolean("isLogined", false) == false) {
                    ToastUtil.shortshow(this, R.string.toast_non_login);
                    return;
                }
                //检查是否是同一用户
                if (task_user_id.equals(getUser_id())) {
                    ToastUtil.shortshow(getApplicationContext(), R.string.toast_error_talk);
                    return;
                }
                //启动会话界面
                if (RongIM.getInstance() != null)
                    startRecordByPermissions();//请求录音权限操作  ---> 获取成功后执行 excuteActionContainRecordPermision()

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
            case R.id.img_WeiXin_SharePop_LRDA:
                wxflag=false;
                regToWx();
                getWXInfo();
                //检查是否安装微信
                if (!isWXAppInstalled)
                    return;
                //检查用户是否登录,未登录则return;
                if (getSharedPreferences(StaticBean.USER_INFO, MODE_PRIVATE).getBoolean("isLogined", false) == false) {
                    ToastUtil.shortshow(this, R.string.toast_non_login);
                    return;
                }

                SendSharePost(id, 1);
//                WXShareText("测试分享到微信");
                Bitmap bitmap= BitmapFactory.decodeResource(getResources(),R.mipmap.logo_djx);
                WXShareWeb(ServerAPIConfig.OnLineShare+id,"众觅互助",description,bitmap,50,50);
                break;
            case R.id.img_QQ_SharePop_LRDA:
                getmTencent().shareToQQ(this, getBundle(), shareListener);
                break;
            case R.id.img_XinLang_SharePop_LRDA:
                Bitmap bitmap2 = BitmapFactory.decodeResource(getResources(),R.mipmap.logo_djx);
                SendSharePost(id, 3);
                sendMultiMessage(true, description, true, BitmapUtil.Bitmap2Bytes(bitmap2), true,
                        false, false, false);
                break;
            case R.id.img_WeiXinF_SharePop_LRDA:
                wxflag=true;
                regToWx();
                getWXInfo();
                //检查是否安装微信
                if (!isWXAppInstalled)
                    return;
                //检查用户是否登录,未登录则return;
                if (getSharedPreferences(StaticBean.USER_INFO, MODE_PRIVATE).getBoolean("isLogined", false) == false) {
                    ToastUtil.shortshow(this, R.string.toast_non_login);
                    return;
                }

                SendSharePost(id, 1);
                Bitmap bitmap1= BitmapFactory.decodeResource(getResources(),R.mipmap.logo_djx);
                WXShareWeb(ServerAPIConfig.OffLineShare+id,"众觅互助",description,bitmap1,50,50);
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
            //浏览图片弹框的点击事件
            case R.id.ll_ShowImgeView_Pop_LRDA:
                popupWindoew.dismiss();
                break;
            //回复图片弹框的点击事件
            case R.id.tv_TakePhotots_Popwin://拍照
                startTakePhotoByPermissions();
                break;
            case R.id.tv_Piktures_Popwin:
                startReadSDCardByPermissions();//相册
                break;
            case R.id.tv_Cancel_Popwin:
                popupWindoew.dismiss();
                break;

            //文本回复弹窗点击事件
            case R.id.tv_SendText:
                if (edt_TextUpload.getText().length() == 0) {
                    ToastUtil.shortshow(LifeRewardOnLineDetailActivity.this, "输入内容不能为空");
                } else {
                    SendMassage(3, 1, edt_TextUpload.getText().toString());
                }
                break;
            case R.id.tv_CancelText:
                popupWindoew.dismiss();
                break;
            //视频回复弹窗点击事件
            case R.id.tv_SendVideo:
                if (edt_VideoUpload.getText().length() == 0) {
                    ToastUtil.shortshow(LifeRewardOnLineDetailActivity.this, "输入内容不能为空");
                } else {
                    SendMassage(2, 1, edt_VideoUpload.getText().toString());
                }
                break;
            case R.id.tv_CancelVideo:

                popupWindoew.dismiss();

                break;
        }

    }

    @Override
    public void startTakePhoto() {//拍照
        intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // 指定调用相机拍照后照片的储存路径
        intent.putExtra(MediaStore.EXTRA_OUTPUT,
                Uri.fromFile(tempFile));
        startActivityForResult(intent, 1);
        popupWindoew.dismiss();
    }

    @Override
    public void startReadSDCard() {
        intent = new Intent();
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, 0);
        popupWindoew.dismiss();
    }

    @Override
    public void excuteActionContainRecordPermision() {
        RongIM.getInstance().startPrivateChat(this, task_user_id, task_nickName);
    }
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
            shareAction.withTargetUrl(ServerAPIConfig.OffLineShare+id);
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
            shareAction.withTargetUrl(ServerAPIConfig.OffLineShare+id);
        }
        shareAction.withTitle("众觅互助");

        shareAction.setPlatform(SHARE_MEDIA.SINA).setCallback(umShareListener).share();
    }

    private UMShareListener umShareListener = new UMShareListener() {
        @Override
        public void onResult(SHARE_MEDIA platform) {
            com.umeng.socialize.utils.Log.d("plat","platform"+platform);
            if(platform.name().equals("WEIXIN_FAVORITE")){
                Toast.makeText(LifeRewardOnLineDetailActivity.this,platform + " 收藏成功啦",Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(LifeRewardOnLineDetailActivity.this, platform + " 分享成功啦", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void onError(SHARE_MEDIA platform, Throwable t) {
            Toast.makeText(LifeRewardOnLineDetailActivity.this,platform + " 分享失败啦", Toast.LENGTH_SHORT).show();
            if(t!=null){
                com.umeng.socialize.utils.Log.d("throw","throw:"+t.getMessage());
            }
        }

        @Override
        public void onCancel(SHARE_MEDIA platform) {
            Toast.makeText(LifeRewardOnLineDetailActivity.this,platform + " 分享取消了", Toast.LENGTH_SHORT).show();
        }
    };

    /**
     * 创建微博分享接口实例,并进行注册.
     */
    private IWeiboShareAPI mWeiboShareAPI;//新浪分享

    public void registerApp() {
        mWeiboShareAPI = WeiboShareSDK.createWeiboAPI(this, SinaConstants.APP_KEY);
        // 将营养注册到微博客户端
        mWeiboShareAPI.registerApp();
    }





    // 使用系统当前日期加以调整作为照片的名称
    private String getPhotoFileName() {
        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "'IMG'_yyyyMMdd_HHmmss");
        return dateFormat.format(date) + ".jpg";
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
        params.put("target_type", 7);
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
                        requestComment(0);
                        popupWindoew.dismiss();
                    }else if(2113 == object.getInt("code")){
                        ToastUtil.longshow(getApplicationContext(),R.string.sensitive_word);
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

    private PopupWindow popupWindoew;
    private View popView;

    public void showPopupWindow(int i) {
        popupWindoew = new PopupWindow();
        popupWindoew.setFocusable(true);
        popupWindoew.setOutsideTouchable(true);
        popupWindoew.setTouchable(true);
        parentCover(1);
        //根据i的值弹出不同的popupwindow,1回复图片选择；2分享；3评论;4浏览图片;5文本回复;6:视频回复。
        switch (i) {
            case 1:
                popupWindoew.setWidth((ScreenUtils.getScreenWidth(this)));
                popupWindoew.setHeight(ScreenUtils.getScreenHeight(this) / 4);
                popView = LayoutInflater.from(this).inflate(R.layout.popwindow_photochose_ipda, null);
                popupWindoew.setBackgroundDrawable(getResources().getDrawable(R.color.transparent));
                //初始化抢单弹窗控件和事件
                initPhotoUploadPopView();
                initPhotoUploadPopEvent();
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
            case 5:
                popupWindoew.setWidth(ScreenUtils.getScreenWidth(this));
                popupWindoew.setHeight(ScreenUtils.getScreenHeight(this) / 2);
                popView = LayoutInflater.from(this).inflate(R.layout.popwindow_liferewardonline_text, null);
                popupWindoew.setBackgroundDrawable(getResources().getDrawable(R.color.transparent));
                initTextUploadPopView();
                initTextoUploadPopEvent();
                break;
            case 6:
                popupWindoew.setWidth(ScreenUtils.getScreenWidth(this));
                popupWindoew.setHeight(ScreenUtils.getScreenHeight(this) / 2);
                popView = LayoutInflater.from(this).inflate(R.layout.popwindow_liferewardonline_video, null);
                popupWindoew.setBackgroundDrawable(getResources().getDrawable(R.color.transparent));
                initVideoUploadPopView();
                initVideoUploadPopEvent();
                break;
        }
        popupWindoew.setContentView(popView);
        //不同popupwindow的位置
        switch (i) {
            case 1:
                popupWindoew.showAtLocation(v_ParentCover_CDA, Gravity.BOTTOM, 0, 0);
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
            case 5:
                popupWindoew.showAtLocation(v_ParentCover_CDA, Gravity.CENTER, 0, 0);
                break;
            case 6:
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

    private void initVideoUploadPopEvent() {
        tv_SendVideo.setOnClickListener(this);
        tv_CancelVideo.setOnClickListener(this);
    }

    EditText edt_VideoUpload;
    TextView tv_SendVideo, tv_CancelVideo;

    //上传视频连接
    private void initVideoUploadPopView() {
        tv_SendVideo = (TextView) popView.findViewById(R.id.tv_SendVideo);
        tv_CancelVideo = (TextView) popView.findViewById(R.id.tv_CancelVideo);
        edt_VideoUpload = (EditText) popView.findViewById(R.id.edt_VideoUpload);
    }

    private void initTextoUploadPopEvent() {
        tv_SendText.setOnClickListener(this);
        tv_CancelText.setOnClickListener(this);
    }

    //上传文本
    EditText edt_TextUpload;
    TextView tv_SendText, tv_CancelText;

    private void initTextUploadPopView() {
        edt_TextUpload = (EditText) popView.findViewById(R.id.edt_TextUpload);
        tv_SendText = (TextView) popView.findViewById(R.id.tv_SendText);
        tv_CancelText = (TextView) popView.findViewById(R.id.tv_CancelText);
    }



    /**
     * 加载图片
     */

    //图片id集合
    List<String> idList = new ArrayList<String>();





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

    private void initPhotoUploadPopEvent() {
        tv_TakePhotots_Popwin.setOnClickListener(this);
        tv_Piktures_Popwin.setOnClickListener(this);
        tv_Cancel_Popwin.setOnClickListener(this);
    }

    private TextView tv_TakePhotots_Popwin, tv_Piktures_Popwin, tv_Cancel_Popwin;

    private void initPhotoUploadPopView() {
        tv_TakePhotots_Popwin = (TextView) popView.findViewById(R.id.tv_TakePhotots_Popwin);
        tv_Piktures_Popwin = (TextView) popView.findViewById(R.id.tv_Piktures_Popwin);
        tv_Cancel_Popwin = (TextView) popView.findViewById(R.id.tv_Cancel_Popwin);
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
        params.putString(QQShare.SHARE_TO_QQ_SUMMARY, description);
        params.putString(QQShare.SHARE_TO_QQ_TARGET_URL, ServerAPIConfig.OnLineShare+target_id);
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

    //分享列表点击头像查看用户资料
    @Override
    public void setOnShareAvatarListener(View view, int type) {
        LifeRewardOnlineDetailInfo.Result.Share info = (LifeRewardOnlineDetailInfo.Result.Share) shareAdapter.getList().get((Integer) view.getTag() - 1);
        String user_id = info.user_id;
        String nickName = info.nickname;
        Bundle bundle = new Bundle();
        bundle.putString("user_id", user_id);
        bundle.putString("nickName", nickName);
        startActivity(LookOthersMassageActivity.class, bundle);
    }

    //评论列表点击头像查看用户资料
    @Override
    public void setOnCommentAvatarListener(View view, int type) {
        LifeRewardOnlineDetailInfo.Result.Comment info = (LifeRewardOnlineDetailInfo.Result.Comment) commentAdapter.getList().get((Integer) view.getTag() - 1);
        String user_id = info.user_id;
        String nickName = info.nickname;
        Bundle bundle = new Bundle();
        bundle.putString("user_id", user_id);
        bundle.putString("nickName", nickName);
        startActivity(LookOthersMassageActivity.class, bundle);
    }

    /**
     * 点击已上传的图片,跳转到浏览图片activity
     * @param v
     * @param tag
     * @param receiverList
     */
    @Override
    public void setOnLookImageListener(View v, int tag ,List<LifeRewardOnlineDetailInfo.Result.Receiver> receiverList,int pos) {//the photo callback
        Intent intent = null;
        Bundle bundle = new Bundle();
        PhotoBrowseEntity photoBrowseEntity = new PhotoBrowseEntity(pos,receiverList);
        bundle.putSerializable("photoBrowseEntity",photoBrowseEntity);
        if(tsakType == 2){//视频
            intent = new Intent(this, VideoPlayActivity.class);
            intent.putExtra("VIDEO_URL",receiverList.get(pos).content);
            startActivity(intent);
        }else {//图片

            intent = new Intent(this, PhotoShowActivity.class);
            Bundle bundle2 = new Bundle();
            bundle2.putInt("CURRENT_POS", pos);
            ArrayList<String> ids = new ArrayList<>();
            for (int i = 0; i < receiverList.size(); i++) {
                LifeRewardOnlineDetailInfo.Result.Receiver receiver = receiverList.get(i);
                if(receiver.media != null && receiver.media.get(0)!= null &&  receiver.media.get(0).media_id != null){
                    ids.add(receiver.media.get(0).media_id);
                }
            }
            intent.putStringArrayListExtra("IDS", ids);
            intent.putExtras(bundle2);
            startActivity(intent);


            /*intent = new Intent(this, PhotoBrowseActivity.class);
            intent.putExtras(bundle);
            startActivity(intent);*/
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
        params.put("target_type", 7);
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
                        requestShareData(0);
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

    String picPath;

    //特别注意：一定要添加以下代码，才可以从回调listener中获取到消息。
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constants.REQUEST_QQ_SHARE) {

            Tencent.onActivityResultData(requestCode, resultCode, data, shareListener);
        }
        picPath = null;
        if (resultCode == RESULT_OK) {
            if (requestCode == 0) {//选照片
                picPath = getPath(getApplicationContext(), data.getData());
                //getQiniu();
                ArrayList<String> img_paths = new ArrayList<>();
                img_paths.add(picPath);
                QiniuUtils.postRequestWithIMGS(this, img_paths, UserInfo.getSessionID(this), 1, new QiniuUtils.PostRequestWithPics() {
                    @Override
                    public void postRequest(IDTokenInfo idTokenInfo_Pic) {
                        postRequest2Server(idTokenInfo_Pic);
                        //SendMassage(1, 1, "");
                    }
                });

            } else if (requestCode == 1) {//拍照
                Uri uri = Uri.fromFile(tempFile);
                picPath = getPath(getApplicationContext(), uri);
                getQiniu();
            }
        }
    }

    private void postRequest2Server(IDTokenInfo idTokenInfo_pic) {
        String media_id = idTokenInfo_pic.list.get(0).id;
        String url = ServerAPIConfig.RewardBook;
        JSONObject obj = new JSONObject();
        JSONObject media_obj = new JSONObject();
        JSONArray media = new JSONArray();
        try {
            Log.e("onlineDetail", "向服务器发送数据-------SendMassage:--id:" + id);
            Log.e("onlineDetail", "向服务器发送数据-------SendMassage:--target_id:" + target_id);

            media_obj.put("id", media_id);
            media_obj.put("media_type", media_type);
            media.put(media_obj);

            obj.put("session_id", getSession_id());
            obj.put("id", target_id);
            obj.put("content", "");
            obj.put("media", media);

            StringEntity entity = new StringEntity(obj.toString(), "utf-8");

            AsyncHttpResponseHandler res = new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int i, Header[] headers, byte[] bytes) {
                    String str = new String(bytes);
                    Log.e("online","onSuccess()--str:"+str);
                    try {
                        JSONObject object = new JSONObject(str);
                        if (object.getInt("code") == 0) {
                            //上传成功后,按钮变灰:
                            tv_QiangDan_LifeRewardDetail.setBackground(getResources().getDrawable(R.drawable.ic_qiandanedbg));
                            tv_QiangDan_LifeRewardDetail.setText("查看");
                            otherFlag = true;
                            mediaAdapter.clear();
                            requestUploadMedia(0);
                            popupWindoew.dismiss();
                            ToastUtil.shortshow(getApplicationContext(), "上传成功,请等待审核通过");
                        } else {
                            errorCode(object.getInt("code"));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {

                }
            };
            AndroidAsyncHttp.post(this, url, entity, "application/json;charset=UTF-8", res);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    /**
     * 获取七牛上传id/token
     * 参数：session_id String 用户会话id
     * size Number 需要id/token的数量
     * media_type Number 资源类型，1-图片，2-视频
     */
    //图片数量
    int size = 1;
    String session_id;
    int media_type = 1;
    IDTokenInfo idTokenInfo;


    public void getQiniu() {
        session_id = getSharedPreferences(StaticBean.USER_INFO, Context.MODE_PRIVATE).getString("session_id", null);
        if (session_id == null || session_id.equals("")) {
            ToastUtil.shortshow(getApplicationContext(), "请先登录应用");
            return;
        } else {
            RequestParams params = new RequestParams();
            params.put("session_id", session_id);
            params.put("size", size);
            params.put("media_type", media_type);

            AsyncHttpResponseHandler upHandler = new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int i, Header[] headers, byte[] bytes) {
                    String str_json = new String(bytes);
                    try {
                        JSONObject obj = new JSONObject(str_json);
                        if (obj.getInt("code") == 0) {
                            Gson gson = new Gson();
                            idTokenInfo = gson.fromJson(obj.getString("result").toString(), IDTokenInfo.class);
                            for (int j = 0; j < size; j++) {
                            }
                            upIamgeView();
                        } else {
                            ToastUtil.shortshow(getApplicationContext(), "服务器返回结果异常");
                            errorCode(obj.getInt("code"));
                            return;
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                    ToastUtil.shortshow(getApplicationContext(), "失败");
                }
            };
            AndroidAsyncHttp.post(ServerAPIConfig.GetIDToken, params, upHandler);
            //ToastUtil.shortshow(getApplicationContext(), size + "");
        }
    }

    /**
     * 七牛上传图片
     */
    public void upIamgeView() {

        final UploadManager upLoadManager = new UploadManager();
        for (int i = 0; i < size; i++) {

            UpCompletionHandler upComplete = new UpCompletionHandler() {
                @Override
                public void complete(String key, ResponseInfo responseInfo, JSONObject jsonObject) {
                    for (int j = 0; j < size; j++) {
                        if (key.equals(idTokenInfo.list.get(j).id)) {
                            if (responseInfo.isOK()) {
                                SendMassage(1, 1, "");
                            } else {
                                //如果上传失败直接结束图片上传
                                ToastUtil.shortshow(getApplicationContext(), "上传失败");
                                return;
                            }
                        }
                    }


                }
            };
            String token = idTokenInfo.list.get(i).token;
            String key = idTokenInfo.list.get(i).id;
            upLoadManager.put(picPath, key, token, upComplete, null);
        }

    }

    /**
     * {
     * "session_id": "8527a2b6a58b4fa963fd",
     * "real_name": "",
     * "id_card": "21001199101010000",
     * "id_card_pic": "13k12j3k12kl31sal2"
     * }
     * 向服务器发送数据
     */
    public void SendMassage(final int upload_type, int media_type, String content) {

        String media_id = null;

        switch (upload_type) {
            case 1:
                media_id = idTokenInfo.list.get(0).id;
                break;
            case 2:
                media_id = null;
                break;
            case 3:
                media_id = null;
                break;

        }
        String url = ServerAPIConfig.RewardBook;
        JSONObject obj = new JSONObject();
        JSONObject media_obj = new JSONObject();
        JSONArray media = new JSONArray();
        try {
            obj.put("session_id", getSession_id());
            obj.put("id", target_id);//1e07dfa865679309043e
                              //1e07dfa865679309043e
            Log.e("onlineDetail","向服务器发送数据-------SendMassage:--"+id);
            obj.put("content", content);

            switch (upload_type) {
                case 1:
                    media_obj.put("id", media_id);
                    media_obj.put("media_type", media_type);
                    media.put(media_obj);
                    break;
                case 2:

                    break;
                case 3:

                    break;
            }
            obj.put("media", media);
            StringEntity entity = new StringEntity(obj.toString(), "utf-8");

            AsyncHttpResponseHandler res = new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int i, Header[] headers, byte[] bytes) {
                    String str = new String(bytes);
                    try {
                        JSONObject object = new JSONObject(str);
                        if (object.getInt("code") == 0) {
                            //上传成功后,按钮变灰:
                            tv_QiangDan_LifeRewardDetail.setBackground(getResources().getDrawable(R.drawable.ic_qiandanedbg));
                            /*tv_QiangDan_LifeRewardDetail.setText(R.string.order_grabed);*/
                            tv_QiangDan_LifeRewardDetail.setText("查看");
                            otherFlag = true;
                            switch (upload_type) {
                                case 1:
                                    mediaAdapter.clear();
                                    requestUploadMedia(0);
                                    break;
                                case 3:
                                    otherAdapter.clear();
                                    requestUploadOther(0);
                                    break;
                            }
                            popupWindoew.dismiss();
                            ToastUtil.shortshow(getApplicationContext(), "上传成功,请等待审核通过");

                        } else {
                            errorCode(object.getInt("code"));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {

                }
            };
            AndroidAsyncHttp.post(this, url, entity, "application/json;charset=UTF-8", res);
        } catch (JSONException e) {
            e.printStackTrace();
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

}
