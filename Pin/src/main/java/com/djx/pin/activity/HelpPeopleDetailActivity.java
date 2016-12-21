/*
package com.djx.pin.improve;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.djx.pin.R;
import com.djx.pin.activity.LookOthersMassageActivity;
import com.djx.pin.adapter.HelpPeopleDetailCommentAdapter;
import com.djx.pin.adapter.HelpPeopleDetailShareAdapter;
import com.djx.pin.adapter.ShowImagePagerAdapter;
import com.djx.pin.base.BaseActivity;
import com.djx.pin.beans.COMMENT;
import com.djx.pin.beans.HelpPeopleDetailCommentInfo;
import com.djx.pin.beans.HelpPeopleDetailShareInfo;
import com.djx.pin.beans.HelperPeopleInfo;
import com.djx.pin.beans.SHARE;
import com.djx.pin.beans.StaticBean;
import com.djx.pin.myview.CircleImageView;
import com.djx.pin.serverapiconfig.ServerAPIConfig;
import com.djx.pin.sina.SinaConstants;
import com.djx.pin.utils.AndroidAsyncHttp;
import com.djx.pin.utils.BitmapUtil2;
import com.djx.pin.utils.DateUtils;
import com.djx.pin.utils.LogUtil;
import com.djx.pin.utils.ScreenUtils;
import com.djx.pin.utils.ToastUtil;
import com.djx.pin.utils.Util;
import com.djx.pin.weixin.WXConstants;
import com.google.gson.Gson;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.sina.weibo.sdk.api.ImageObject;
import com.sina.weibo.sdk.api.TextObject;
import com.sina.weibo.sdk.api.WebpageObject;
import com.sina.weibo.sdk.api.WeiboMultiMessage;
import com.sina.weibo.sdk.api.share.IWeiboShareAPI;
import com.sina.weibo.sdk.api.share.SendMultiMessageToWeiboRequest;
import com.sina.weibo.sdk.api.share.WeiboShareSDK;
import com.sina.weibo.sdk.utils.Utility;
import com.tencent.connect.common.Constants;
import com.tencent.connect.share.QQShare;
import com.tencent.mm.sdk.modelmsg.SendMessageToWX;
import com.tencent.mm.sdk.modelmsg.WXMediaMessage;
import com.tencent.mm.sdk.modelmsg.WXTextObject;
import com.tencent.mm.sdk.modelmsg.WXWebpageObject;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import io.rong.imkit.RongIM;
import uk.co.senab.photoview.PhotoView;

package com.djx.pin.activity;

*/
/**
 * Created by Administrator on 2016/6/24.
 *//*

public class HelpPeopleDetailActivity extends BaseActivity implements View.OnClickListener, AdapterView.OnItemClickListener, ViewPager.OnPageChangeListener, TextWatcher, AbsListView.OnScrollListener, HelpPeopleDetailCommentAdapter.OnCommentAvatarListener, HelpPeopleDetailShareAdapter.OnShareAvatarListener {

    private HelpPeopleDetailShareAdapter shareAdapter;
    private HelpPeopleDetailCommentAdapter commentAdapter;
    private PullToRefreshListView lv;

    private LinearLayout ll_ViewGroup1_LRDA, ll_Top_LifeRewardDetail, ll_ViewGroup2_LRDA, ll_ShowAllContent_LRDA,
            ll_Location1_LifeRewardDetail, ll_Chat_LRDA, ll_Share_LRDA, ll_Comment_LRDA, ll_Back_LifeRewardDetail;
    private TextView tv_ShareDetail_LRDA, tv_CommentDetail_LRDA, tv_QiangDan_LifeRewardDetail, tv_spread;
    private int viewGroupTop;
    private View v_ShareLine_LRDA, v_CommentLine_LRDA, v_ParentCover_LRDA;
    private CircleImageView cimg_Avatar_HPDA;
    private String id,//求助id
            prev_user_id;//上一节点用户id,即PIN的用户user_id.
    private Context CONTEXT = HelpPeopleDetailActivity.this;
    private int share_index = 0, share_size = 10, comment_index = 0, comment_size = 10;//分享,评论的index,size;
    private LayoutInflater inflater;
    private TextView tv_UserName1_LifeReward, tv_HelperPrice_LifeRewardDetail, tv_Time1_LifeReward,
            tv_HelperPeopleNum_LifeRewardDetail, tv_Content_LDA, tv_Location_LifeRewardDetail, tv_HelperPeriod_LifeRewardDetail;
    private SharedPreferences sp;
    private ImageView iv_Avatar_1, iv_Avatar_2, iv_Avatar_3, iv_Avatar_4,//加载不同的headview对应的图片
            iv_spread;

    private ImageView[] imageViews_Pin = new ImageView[3];
    private List<SHARE> shareList;
    private List<COMMENT> commentList;
    private IWeiboShareAPI mWeiboShareAPI;//新浪分享
    private boolean isSpread = false;//控制展开全文,默认false,即不展开
    private Bundle bundle;

    */
/**
     * 上拉加载更多使用的索引
     *//*

    private int index_share = 0, size_share = 10;
    private int index_comment = 0, size_comment = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_helperpeopledetail);
        shareAdapter = new HelpPeopleDetailShareAdapter(this, this);
        commentAdapter = new HelpPeopleDetailCommentAdapter(this, this);
        initView();
        initData();
        initEvent();
    }

    private HelperPeopleInfo helperPeopleInfo;
    private int imageNumber = 0;

    */
/**
     * 初始化帮人详情
     * 加载帮人详情基本信息
     *//*

    HelpPeopleDetailShareInfo shareInfo;

    private void initData() {
        bundle=getIntent().getExtras();
        id = bundle.getString("id");
        prev_user_id=bundle.getString("share_user_id");
        if (null == id || id.length() == 0) {
            LogUtil.e(this, "id异常");
        }
        AsyncHttpResponseHandler res = new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                String str_json = new String(bytes);
                try {
                    JSONObject obj = new JSONObject(str_json);
                    if (0 != obj.getInt("code")) {
                        LogUtil.e("0!=code");
                    }
                    Gson gson = new Gson();
                    shareInfo = new HelpPeopleDetailShareInfo();
                    shareInfo = gson.fromJson(obj.getJSONObject("result").toString(), HelpPeopleDetailShareInfo.class);
                    imageNumber = shareInfo.media.size();
                    addHeadView();
                } catch (JSONException e) {
                    LogUtil.e(CONTEXT, "enter catch=" + e.toString());
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                LogUtil.e(CONTEXT, "网络连接异常");
                ToastUtil.shortshow(CONTEXT, R.string.toast_error_net);
            }
        };
        String url = ServerAPIConfig.Get_HelpDetail + "&id=" + id + "&index=" + 0 + "&size=" + 10 + "&type=" + 1;
        AndroidAsyncHttp.get(url, res);

    }

    public void getShareData(int index) {
        if (null == id || id.length() == 0) {
            LogUtil.e(this, "id异常");
        }
        AsyncHttpResponseHandler res = new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                String str_json = new String(bytes);
                try {
                    JSONObject obj = new JSONObject(str_json);
                    if (0 != obj.getInt("code")) {
                        LogUtil.e("0!=code");
                    }
                    Gson gson = new Gson();
                    shareInfo = new HelpPeopleDetailShareInfo();
                    shareInfo = gson.fromJson(obj.getJSONObject("result").toString(), HelpPeopleDetailShareInfo.class);
                    tv_ShareDetail_LRDA.setText("分享 " + shareInfo.share_num);
                    for (int j = 0; j < shareInfo.share.size(); j++) {
                        shareAdapter.addData(shareInfo.share.get(j));
                    }
                    shareAdapter.notifyDataSetChanged();
                    if (lv.isRefreshing()) {
                        lv.onRefreshComplete();
                    }

                } catch (JSONException e) {
                    LogUtil.e(CONTEXT, "enter catch=" + e.toString());
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                LogUtil.e(CONTEXT, "网络连接异常");
                ToastUtil.shortshow(CONTEXT, R.string.toast_error_net);
            }
        };
        String url = ServerAPIConfig.Get_HelpDetail + "&id=" + id + "&index=" + index + "&size=" + size_share + "&type=" + 1;
        AndroidAsyncHttp.get(url, res);
    }

    HelpPeopleDetailCommentInfo commentInfo;

    public void getCommentData(int index) {
        if (null == id || id.length() == 0) {
            LogUtil.e(this, "id异常");
        }
        AsyncHttpResponseHandler res = new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                String str_json = new String(bytes);
                try {
                    JSONObject obj = new JSONObject(str_json);
                    if (0 != obj.getInt("code")) {
                        LogUtil.e("0!=code");
                    }
                    Gson gson = new Gson();
                    commentInfo = new HelpPeopleDetailCommentInfo();
                    commentInfo = gson.fromJson(obj.getJSONObject("result").toString(), HelpPeopleDetailCommentInfo.class);
                    tv_CommentDetail_LRDA.setText("评论 " + commentInfo.comment_num);
                    for (int j = 0; j < commentInfo.comment.size(); j++) {
                        commentAdapter.addData(commentInfo.comment.get(j));
                    }
                    commentAdapter.notifyDataSetChanged();
                    if (lv.isRefreshing()) {
                        lv.onRefreshComplete();
                    }
                } catch (JSONException e) {
                    LogUtil.e(CONTEXT, "enter catch=" + e.toString());
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                LogUtil.e(CONTEXT, "网络连接异常");
                ToastUtil.shortshow(CONTEXT, R.string.toast_error_net);
            }
        };
        String url = ServerAPIConfig.Get_HelpDetail + "&id=" + id + "&index=" + index + "&size=" + size_comment + "&type=" + 2;
        AndroidAsyncHttp.get(url, res);

    }


    */
/**
     * 添加headView
     *//*

    View headView;

    private void addHeadView() {
        lv.setAdapter(shareAdapter);
        AbsListView.LayoutParams layoutParams = new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        switch (imageNumber) {
            case 0:
                headView = LayoutInflater.from(this).inflate(R.layout.headview_helperpeopledetail, lv, false);
                break;
            case 1:
                headView = LayoutInflater.from(this).inflate(R.layout.headview_helperpeopledetail_1, lv, false);
                break;
            case 2:
                headView = LayoutInflater.from(this).inflate(R.layout.headview_helperpeopledetail_2, lv, false);
                break;
            case 3:
                headView = LayoutInflater.from(this).inflate(R.layout.headview_helperpeopledetail_3, lv, false);
                break;
            case 4:
                headView = LayoutInflater.from(this).inflate(R.layout.headview_helperpeopledetail_4, lv, false);
                break;
        }
        headView.setLayoutParams(layoutParams);
        initHeaderView();
        ListView listView = lv.getRefreshableView();
        listView.addHeaderView(headView);
    }

    */
/**
     * 初始化headView
     *//*

    private void initHeaderView() {

        */
/**找到对应的控件*//*

        cimg_Avatar_HPDA = (CircleImageView) headView.findViewById(R.id.cimg_Avatar_HPDA);
        tv_UserName1_LifeReward = (TextView) headView.findViewById(R.id.tv_UserName1_LifeReward);
        tv_Time1_LifeReward = (TextView) headView.findViewById(R.id.tv_Time1_LifeReward);
        tv_QiangDan_LifeRewardDetail = (TextView) headView.findViewById(R.id.tv_QiangDan_LifeRewardDetail);
        tv_HelperPrice_LifeRewardDetail = (TextView) headView.findViewById(R.id.tv_HelperPrice_LifeRewardDetail);
        tv_HelperPeopleNum_LifeRewardDetail = (TextView) headView.findViewById(R.id.tv_HelperPeopleNum_LifeRewardDetail);
        tv_Content_LDA = (TextView) headView.findViewById(R.id.tv_Content_LDA);
        tv_Location_LifeRewardDetail = (TextView) headView.findViewById(R.id.tv_Location_LifeRewardDetail);
        tv_HelperPeriod_LifeRewardDetail = (TextView) headView.findViewById(R.id.tv_HelperPeriod_LifeRewardDetail);
        ll_ShowAllContent_LRDA = (LinearLayout) headView.findViewById(R.id.ll_ShowAllContent_LRDA);
        ll_ViewGroup1_LRDA = (LinearLayout) headView.findViewById(R.id.ll_ViewGroup1_LRDA);
        ll_Top_LifeRewardDetail = (LinearLayout) headView.findViewById(R.id.ll_Top_LifeRewardDetail);
        tv_ShareDetail_LRDA = (TextView) headView.findViewById(R.id.tv_ShareDetail_LRDA);
        tv_CommentDetail_LRDA = (TextView) headView.findViewById(R.id.tv_CommentDetail_LRDA);
        v_ShareLine_LRDA = headView.findViewById(R.id.v_ShareLine_LRDA);
        v_CommentLine_LRDA = headView.findViewById(R.id.v_CommentLine_LRDA);
        tv_spread = (TextView) headView.findViewById(R.id.tv_spread);
        iv_spread = (ImageView) headView.findViewById(R.id.iv_spread);
        imageViews_Pin[0] = (ImageView) headView.findViewById(R.id.iv_headview0);
        imageViews_Pin[1] = (ImageView) headView.findViewById(R.id.iv_headview1);
        imageViews_Pin[2] = (ImageView) headView.findViewById(R.id.iv_headview2);

        */
/**给对应的控件设置监听*//*

        tv_ShareDetail_LRDA.setOnClickListener(this);
        tv_CommentDetail_LRDA.setOnClickListener(this);
        tv_QiangDan_LifeRewardDetail.setOnClickListener(this);
        cimg_Avatar_HPDA.setOnClickListener(this);
        ll_ShowAllContent_LRDA.setOnClickListener(this);
        */
/**加载相应的信息*//*

        try {
            getOneImageViewUrl(cimg_Avatar_HPDA, shareInfo.portrait, 1);
        } catch (UnsupportedEncodingException e) {
            LogUtil.e(CONTEXT, "enter catch 图片加载失败");
            e.printStackTrace();
        }
        */
/**
         * 加载pin头像
         *//*

        for (int j = 0; j < shareInfo.pin.size(); j++) {
            try {
                getOneImageViewUrl(imageViews_Pin[j], shareInfo.pin.get(j).portrait, 1);
                imageViews_Pin[j].setVisibility(View.VISIBLE);
            } catch (UnsupportedEncodingException e) {
                LogUtil.e(CONTEXT, "enter catch 图片加载失败");
                e.printStackTrace();
            }
        }
        tv_UserName1_LifeReward.setText(shareInfo.nickname + "");
        tv_Time1_LifeReward.setText(DateUtils.formatDate(new Date(shareInfo.start_time), DateUtils.yyyyMMDD));
        tv_HelperPrice_LifeRewardDetail.setText(shareInfo.price + "");
        tv_HelperPeopleNum_LifeRewardDetail.setText(shareInfo.receiver_limit + "");
        tv_HelperPeriod_LifeRewardDetail.setText(DateUtils.formatDate(new Date(shareInfo.start_time), DateUtils.yyyyMMDD) + " 至 " + DateUtils.formatDate(new Date(shareInfo.end_time), DateUtils.yyyyMMDD));
        tv_Content_LDA.setText(shareInfo.description);
        tv_Location_LifeRewardDetail.setText(shareInfo.address);
        tv_ShareDetail_LRDA.setText("分享 " + shareInfo.share_num);
        tv_CommentDetail_LRDA.setText("评论 " + shareInfo.comment_num);

        */
/**找到对应的ImageView,并加载相应的图片*//*

        switch (imageNumber) {
            case 1:
                iv_Avatar_1 = (ImageView) headView.findViewById(R.id.iv_Avatar_1);
                iv_Avatar_1.setOnClickListener(this);
                try {
                    getOneImageViewUrl(iv_Avatar_1, shareInfo.media.get(0).media_id, 1);
                } catch (UnsupportedEncodingException e) {
                    LogUtil.e(CONTEXT, "enter catch");
                    e.printStackTrace();
                }
                break;
            case 2:
                iv_Avatar_1 = (ImageView) headView.findViewById(R.id.iv_Avatar_1);
                iv_Avatar_1.setOnClickListener(this);
                iv_Avatar_2 = (ImageView) headView.findViewById(R.id.iv_Avatar_2);
                iv_Avatar_2.setOnClickListener(this);
                try {
                    getOneImageViewUrl(iv_Avatar_1, shareInfo.media.get(0).media_id, 1);
                    getOneImageViewUrl(iv_Avatar_2, shareInfo.media.get(1).media_id, 1);
                } catch (UnsupportedEncodingException e) {
                    LogUtil.e(CONTEXT, "enter catch");
                    e.printStackTrace();
                }
                break;
            case 3:
                iv_Avatar_1 = (ImageView) headView.findViewById(R.id.iv_Avatar_1);
                iv_Avatar_1.setOnClickListener(this);
                iv_Avatar_2 = (ImageView) headView.findViewById(R.id.iv_Avatar_2);
                iv_Avatar_2.setOnClickListener(this);
                iv_Avatar_3 = (ImageView) headView.findViewById(R.id.iv_Avatar_3);
                iv_Avatar_3.setOnClickListener(this);
                try {
                    getOneImageViewUrl(iv_Avatar_1, shareInfo.media.get(0).media_id, 1);
                    getOneImageViewUrl(iv_Avatar_2, shareInfo.media.get(1).media_id, 1);
                    getOneImageViewUrl(iv_Avatar_3, shareInfo.media.get(2).media_id, 1);
                } catch (UnsupportedEncodingException e) {
                    LogUtil.e(CONTEXT, "enter catch=" + e.toString());
                    e.printStackTrace();
                }
                break;
            case 4:
                iv_Avatar_1 = (ImageView) headView.findViewById(R.id.iv_Avatar_1);
                iv_Avatar_1.setOnClickListener(this);
                iv_Avatar_2 = (ImageView) headView.findViewById(R.id.iv_Avatar_2);
                iv_Avatar_2.setOnClickListener(this);
                iv_Avatar_3 = (ImageView) headView.findViewById(R.id.iv_Avatar_3);
                iv_Avatar_3.setOnClickListener(this);
                iv_Avatar_4 = (ImageView) headView.findViewById(R.id.iv_Avatar_4);
                iv_Avatar_4.setOnClickListener(this);

                try {
                    getOneImageViewUrl(iv_Avatar_1, shareInfo.media.get(0).media_id, 1);
                    getOneImageViewUrl(iv_Avatar_2, shareInfo.media.get(1).media_id, 1);
                    getOneImageViewUrl(iv_Avatar_3, shareInfo.media.get(2).media_id, 1);
                    getOneImageViewUrl(iv_Avatar_4, shareInfo.media.get(3).media_id, 1);
                } catch (UnsupportedEncodingException e) {
                    LogUtil.e(CONTEXT, "enter catch");
                    e.printStackTrace();
                }

                break;
        }
        getCommentData(0);
        getShareData(0);
    }

    */
/**
     * 公用控件和事件
     *//*

    private void initEvent() {
        ll_Chat_LRDA.setOnClickListener(this);
        ll_Comment_LRDA.setOnClickListener(this);
        ll_Share_LRDA.setOnClickListener(this);
        ll_Back_LifeRewardDetail.setOnClickListener(this);
    }


    // 字段flag是用来判断分享类型的,true表示分享至朋友圈,false表示分享至好友.
    Boolean wxflag = true;

    // 微信分享文字,
    public void WXShareText(String text) {
        // 初始化一个wxTextOject对象,填写分享的文本内容
        WXTextObject textObj = new WXTextObject();
        textObj.text = text;

        // 用WXTextObject对象初始化一个WXMediaMessage对象
        WXMediaMessage msg = new WXMediaMessage();
        msg.mediaObject = textObj;
        msg.description = text;

        // 够着一个Req
        SendMessageToWX.Req req = new SendMessageToWX.Req();
        // transaction用于唯一标识一个请求
        req.transaction = "文字" + String.valueOf(System.currentTimeMillis());

        req.message = msg;

        // 判断flag的值
        req.scene = wxflag ? SendMessageToWX.Req.WXSceneTimeline
                : SendMessageToWX.Req.WXSceneSession;
        // 只有一种情况不能发送请求,即用户选择发送至朋友圈,但微信版本不支持.所以对该情况进行判断.其他情况都需要发送请求
        if (wxflag == true && isWXAppSupportAPI == false) {
            LogUtil.e(CONTEXT, "当前微信版本较低,暂不支持分享至朋友圈");
            ToastUtil.shortshow(CONTEXT, "当前微信版本较低,暂不支持分享至朋友圈");
            return;
        }
        wxapi.sendReq(req);

    }


    */
/**
     * 微信分享网页
     *
     * @param webpageUrl  分享网页的url
     * @param title       网页标题
     * @param description 网页描述
     * @param bmp         网页缩略图
     * @param dstWidth    缩略图的宽度
     * @param dstHeight   缩略图的高度
     *//*

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
        req.scene = wxflag ? SendMessageToWX.Req.WXSceneTimeline
                : SendMessageToWX.Req.WXSceneSession;
        // 只有一种情况不能发送请求,即用户选择发送至朋友圈,但微信版本不支持.所以对该情况进行判断.其他情况都需要发送请求
        if (wxflag == true && isWXAppSupportAPI == false) {
            LogUtil.e(CONTEXT, "当前微信版本较低,暂不支持分享至朋友圈");
            ToastUtil.shortshow(CONTEXT, "当前微信版本较低,暂不支持分享至朋友圈");
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
            LogUtil.e(CONTEXT, "未安装微信");
            ToastUtil.shortshow(CONTEXT, R.string.toast_weixin_share_uninstalled);
            isWXAppInstalled = false;
        }
        // 检查微信是否支持分享到朋友圈
        if (wxapi.getWXAppSupportAPI() >= 0x21020001) {
            isWXAppSupportAPI = true;
        } else {
            isWXAppSupportAPI = false;
        }

    }

    private RelativeLayout r_Parent;

    private void initView() {

        sp = getSharedPreferences(StaticBean.USER_INFO, MODE_PRIVATE);
        inflater = getLayoutInflater();
        lv = (PullToRefreshListView) findViewById(R.id.lv);
        lv.setMode(PullToRefreshBase.Mode.BOTH);
        lv.setOnScrollListener(this);
        lv.setOnRefreshListener(onRefreshListener);
        ll_Back_LifeRewardDetail = (LinearLayout) findViewById(R.id.ll_Back_LifeRewardDetail);
        ll_Chat_LRDA = (LinearLayout) findViewById(R.id.ll_Chat_LRDA);
        ll_Comment_LRDA = (LinearLayout) findViewById(R.id.ll_Comment_LRDA);
        ll_Share_LRDA = (LinearLayout) findViewById(R.id.ll_Share_LRDA);
        ll_ViewGroup2_LRDA = (LinearLayout) findViewById(R.id.ll_ViewGroup2_LRDA);
        ll_ViewGroup2_LRDA.setVisibility(ll_ViewGroup2_LRDA.GONE);
        v_ParentCover_LRDA = findViewById(R.id.v_ParentCover_LRDA);
        shareList = new ArrayList<>();
        commentList = new ArrayList<>();

    }

    */
/**
     * 上拉加载更多监听
     *//*

    boolean isCommentAdapter = false;//是否是commentAdapter
    PullToRefreshBase.OnRefreshListener2<ListView> onRefreshListener = new PullToRefreshBase.OnRefreshListener2() {
        @Override
        public void onPullDownToRefresh(PullToRefreshBase refreshView) {
            getShareData(0);
            getCommentData(0);
        }

        @Override
        public void onPullUpToRefresh(PullToRefreshBase refreshView) {
            if (isCommentAdapter == false) {
                index_share = index_share + 1;
                //判断当前页码是否大于最后页码,大于则改为最后一个页码
                if (shareInfo.share_num / share_size < index_share) {
                    index_share = shareAdapter.getCount() / share_size;
                }
                getShareData(index_share);
            }
            if (isCommentAdapter == true) {
                index_comment = index_comment + 1;
                //判断当前页码是否大于最后页码,大于则改为最后一个页码
                if (commentInfo.comment_num / comment_size < index_comment) {
                    index_comment = commentAdapter.getCount() / comment_size;
                }
                getCommentData(index_comment);
            }
        }
    };


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        ToastUtil.shortshow(this, "点击了第" + position + "个Item");
    }

    BaseUIlisener shareListener = new BaseUIlisener();

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            */
/**用户点击pin按钮*//*

            case R.id.img_Pin_SharePop_LRDA:
                updataPinInfo();
                popupWindoew.dismiss();
                break;
            */
/**点击展开全文*//*

            case R.id.ll_ShowAllContent_LRDA:
                if (isSpread) {
                    tv_Content_LDA.setMaxLines(2);
                    tv_spread.setText(R.string.tv_spread);
                    iv_spread.setImageResource(R.mipmap.ic_downopen);
                    isSpread = false;
                } else {
                    tv_Content_LDA.setMaxLines(100);
                    tv_spread.setText(R.string.tv_inspread);
                    iv_spread.setImageResource(R.mipmap.ic_upclose);
                    isSpread = true;
                }
                break;
            */
/**点击分享按钮 查看分享列表*//*

            case R.id.tv_ShareDetail_LRDA:
                isCommentAdapter = false;
                v_ShareLine_LRDA.setVisibility(View.VISIBLE);
                v_CommentLine_LRDA.setVisibility(View.INVISIBLE);
                tv_ShareDetail_LRDA.setTextColor(getResources().getColor(R.color.text_color_black));
                tv_CommentDetail_LRDA.setTextColor(getResources().getColor(R.color.line_color));
                lv.setAdapter(shareAdapter);
                break;
            */
/**点击评论按钮 查看评论列表*//*

            case R.id.tv_CommentDetail_LRDA:
                v_ShareLine_LRDA.setVisibility(View.INVISIBLE);
                v_CommentLine_LRDA.setVisibility(View.VISIBLE);
                tv_ShareDetail_LRDA.setTextColor(getResources().getColor(R.color.line_color));
                tv_CommentDetail_LRDA.setTextColor(getResources().getColor(R.color.text_color_black));
                lv.refreshDrawableState();

                lv.setAdapter(commentAdapter);
                isCommentAdapter = true;
                break;
            case R.id.ll_Back_LifeRewardDetail:
                this.finish();
                break;
            case R.id.cimg_Avatar_HPDA:
                String user_id = shareInfo.user_id;
                String nickName = shareInfo.nickname;
                Bundle bundle = new Bundle();
                bundle.putString("user_id", user_id);
                bundle.putString("nickName", nickName);
                startActivity(LookOthersMassageActivity.class, bundle);
                break;
            case R.id.tv_QiangDan_LifeRewardDetail:
                //检查用户是否登录,未登录则return;已经实名认证的用户才可以举报
                if (getSharedPreferences(StaticBean.USER_INFO, MODE_PRIVATE).getBoolean("isLogined", false) == false) {
                    ToastUtil.shortshow(this, R.string.toast_non_login);
                    return;
                }
                //检查用户是否实名认证,未实名认证则return;已经实名认证的用户才可以举报
                if (0 == getSharedPreferences(StaticBean.USER_INFO, MODE_PRIVATE).getInt("is_auth", 0)) {
                    ToastUtil.shortshow(this, R.string.toast_error_is_auth);
                    return;
                }
                acceptOrder();
                break;
            //私信点击事件
            case R.id.ll_Chat_LRDA:
                ToastUtil.shortshow(this, "点击了私信");
                //检查用户是否登录,未登录则return;已经实名认证的用户才可以举报
                if (getSharedPreferences(StaticBean.USER_INFO, MODE_PRIVATE).getBoolean("isLogined", false) == false) {
                    ToastUtil.shortshow(this, R.string.toast_non_login);
                    return;
                }
                //检查是否是同一用户
                if (shareInfo.user_id.equals(sp.getString("user_id", null))) {
                    ToastUtil.shortshow(CONTEXT, R.string.toast_error_talk);
                    return;
                }

                //启动会话界面
                if (RongIM.getInstance() != null)
                    RongIM.getInstance().startPrivateChat(CONTEXT, shareInfo.user_id, shareInfo.nickname);
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
                ToastUtil.shortshow(this, "发起抢单成功，请等待");
                tv_QiangDan_LifeRewardDetail.setBackgroundResource(R.mipmap.ic_qiandanedbg);
                tv_QiangDan_LifeRewardDetail.setText("已抢单");
                break;
            //分享弹窗点击事件
            case R.id.tv_Cancle_SharePop_LRDA:
                popupWindoew.dismiss();
                break;
            //点击微信分享
            case R.id.img_WeiXin_SharePop_LRDA:
                regToWx();
                getWXInfo();
                //检查是否安装微信
                if (!isWXAppInstalled)
                    return;
                //检查用户是否登录,未登录则return;
                if (getSharedPreferences(StaticBean.USER_INFO, MODE_PRIVATE).getBoolean("isLogined", false) == false) {
                    ToastUtil.shortshow(CONTEXT, R.string.toast_non_login);
                    return;
                }

                SendSharePost(shareInfo.id, 1);
                WXShareText("测试分享到微信");
//                Bitmap bitmap=((BitmapDrawable)iv_Avatar_1.getDrawable()).getBitmap();
//                WXShareWeb("http://www.baidu.com/","","",bitmap,50,50);
                break;
            case R.id.img_QQ_SharePop_LRDA:

                getmTencent().shareToQQ(this, getBundle(), shareListener);
                break;
            //点击新浪分享
            case R.id.img_XinLang_SharePop_LRDA:
                registerApp();
                //检查是否安装新浪微博
                if (!mWeiboShareAPI.isWeiboAppInstalled()) {
                    ToastUtil.shortshow(CONTEXT, R.string.toast_sina_share_uninstalled);
                    return;
                }
                //检查用户是否登录,未登录则return;
                if (getSharedPreferences(StaticBean.USER_INFO, MODE_PRIVATE).getBoolean("isLogined", false) == false) {
                    ToastUtil.shortshow(CONTEXT, R.string.toast_non_login);
                    return;
                }

                Bitmap bitmap1 = ((BitmapDrawable) iv_Avatar_1.getDrawable()).getBitmap();
                SendSharePost(shareInfo.id, 3);
                sendMultiMessage(true, "分享到新浪微博==", true, BitmapUtil2.Bitmap2Bytes(bitmap1), true,
                        false, false, false);
                break;
            //评论弹窗点击事件
            case R.id.tv_SendComment_Pop_LRDA:
                if (checkCommentUserInfo()) {
                    updataCommentInfo();
                }
                popupWindoew.dismiss();
                break;
            //图片点击事件
            case R.id.iv_Avatar_1:
                showPopupWindow(4);
                initPopImageView(0);

                break;
            case R.id.iv_Avatar_2:
                showPopupWindow(4);
                initPopImageView(1);

                break;
            case R.id.iv_Avatar_3:
                showPopupWindow(4);
                initPopImageView(2);
                break;
            case R.id.iv_Avatar_4:
                showPopupWindow(4);
                initPopImageView(3);
                break;
            //浏览图片弹框的点击事件
            case R.id.ll_ShowImgeView_Pop_LRDA:
                popupWindoew.dismiss();
                break;
        }
    }


    */
/**
     * 创建微博分享接口实例,并进行注册.
     *//*

    public void registerApp() {
        mWeiboShareAPI = WeiboShareSDK.createWeiboAPI(this, SinaConstants.APP_KEY);
        // 将营养注册到微博客户端
        mWeiboShareAPI.registerApp();
    }

    */
/**
     * 考虑到分享的内容只需要文字+图片.所以只实现了这两个功能.其他的视频 音频暂未实现
     *
     * @param hasText    是否有文字
     * @param text       文字内容
     * @param hasImage   是否有图片
     * @param imgByte    图片(是byte[]类型的图片)
     * @param hasWebpage 是否有网页(暂未实现)
     * @param hasMusic   是否有音乐(暂未实现)
     * @param hasVideo   是否有视频(暂未实现)
     * @param hasVoice   是否有声音(暂未实现)
     *//*

    private void sendMultiMessage(Boolean hasText, String text,
                                  Boolean hasImage, byte[] imgByte, Boolean hasWebpage,
                                  Boolean hasMusic, Boolean hasVideo, Boolean hasVoice) {
        // 初始化微博的分享消息
        WeiboMultiMessage weiboMessage = new WeiboMultiMessage();


        if (hasText) {
            LogUtil.e("有文字");
            TextObject textObject = new TextObject();
            textObject.text = text;
            weiboMessage.textObject = textObject;
        }
        if (hasImage) {
            LogUtil.e("有图片");
            ImageObject imageObject = new ImageObject();
            //设置缩略图。 注意：最终压缩过的缩略图大小不得超过 32kb。
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_test);
            imageObject.setImageObject(bitmap);
            weiboMessage.imageObject = imageObject;
        }
        if (hasWebpage) {
            LogUtil.e("有网页");
            WebpageObject webpageObject = new WebpageObject();
            webpageObject.identify = Utility.generateGUID();
            webpageObject.title = "标题";
            webpageObject.description = "描述";
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_test);
            // 设置 Bitmap 类型的图片到视频对象里         设置缩略图。 注意：最终压缩过的缩略图大小不得超过 32kb。
            webpageObject.setThumbImage(bitmap);
            webpageObject.actionUrl = "http://www.baidu.com";
            webpageObject.defaultText = "Webpage 默认文案";
            weiboMessage.mediaObject = webpageObject;
        }

        SendMultiMessageToWeiboRequest request = new SendMultiMessageToWeiboRequest();
        request.transaction = String.valueOf(System.currentTimeMillis());
        request.multiMessage = weiboMessage;
        // 发送请求消息到微博,唤起微博分享界面
        mWeiboShareAPI.sendRequest(HelpPeopleDetailActivity.this, request);
    }


    */
/**
     * 发送pin至服务器
     *//*

    private void updataPinInfo(){
        AsyncHttpResponseHandler resPin = new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                String strJson = new String(bytes);
                LogUtil.e("strJson="+strJson);
                try {
                    JSONObject obj = new JSONObject(strJson);
                    if (0 != obj.getInt("code")) {
                        errorCode(obj.getInt("code"));
                        LogUtil.e("code=" + obj.getInt("code"));
                        return;
                    }
                    ToastUtil.shortshow(CONTEXT, R.string.toast_success_pin);
                } catch (JSONException e) {
                    LogUtil.e(CONTEXT, "enter catch");
                    e.printStackTrace();
                }
            }
            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                ToastUtil.shortshow(CONTEXT, R.string.toast_error_net);
                LogUtil.e(CONTEXT, "网络连接失败");
            }
        };
        RequestParams params = new RequestParams();
        params.put("session_id", sp.getString("session_id", null));
        params.put("id", id);
        params.put("prev_user_id", prev_user_id);
        params.put("province", mLocation.getProvince());
        params.put("city", mLocation.getCity());
        params.put("district", mLocation.getDistrict());
        params.put("address", mLocation.getAddrStr());
        params.put("latitude", mLatitude);
        params.put("longitude", mLongitude);
        AndroidAsyncHttp.post(ServerAPIConfig.HelperPin, params, resPin);



    }

    */
/**
     * 发布评论至服务器
     *//*

    private void updataCommentInfo() {
        AsyncHttpResponseHandler resComment = new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                String strJson = new String(bytes);
                try {
                    JSONObject obj = new JSONObject(strJson);
                    if (0 != obj.getInt("code")) {
                        LogUtil.e("code=" + obj.getInt("code"));
                        ToastUtil.shortshow(CONTEXT, R.string.toast_fail_comment);
                        return;
                    }
                    ToastUtil.shortshow(CONTEXT, R.string.toast_success_comment);
                    commentAdapter.clear();
                    index_comment = 0;
                    getCommentData(0);
                } catch (JSONException e) {
                    LogUtil.e(CONTEXT, "enter catch");
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                ToastUtil.shortshow(CONTEXT, R.string.toast_error_net);
                LogUtil.e(CONTEXT, "网络连接失败");

            }
        };
        RequestParams params = new RequestParams();
        params.put("session_id", sp.getString("session_id", null));
        params.put("target_id", shareInfo.id);
        params.put("target_type", 1);
        params.put("content", edt_AddComment_Pop_LRDA.getText().toString());
        AndroidAsyncHttp.post(ServerAPIConfig.UPdata_Comment, params, resComment);
    }

    */
/**
     * 检查和评论相关的信息是否正确
     *
     * @return 和评论相关的信息都正确则返回true, 反之false
     *//*

    private boolean checkCommentUserInfo() {
        //检查用户是否登录,未登录则return;
        if (getSharedPreferences(StaticBean.USER_INFO, MODE_PRIVATE).getBoolean("isLogined", false) == false) {
            ToastUtil.shortshow(CONTEXT, R.string.toast_non_login);
            return false;
        }
        //检查评论内容是否为空
        if (null == edt_AddComment_Pop_LRDA.getText() || edt_AddComment_Pop_LRDA.getText().toString().length() == 0) {
            ToastUtil.shortshow(CONTEXT, R.string.toast_non_comment);
            return false;
        }
        return true;
    }


    */
/**
     * 显示PopupWindow
     *//*

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
                popView = LayoutInflater.from(this).inflate(R.layout.popupwindow_lrda_pin_share, null);
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
                popupWindoew.setHeight(ScreenUtils.getScreenHeight(this) / 7);
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

    */
/**
     * 浏览图片的弹窗框的控件和事件
     *//*

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
        vp_LRDA_Pop.setAdapter(vp_Adapter);
        initImageData();
    }

    */
/**
     * 加载图片
     *//*

    private void initImageData() {
        vp_Adapter.clear();
        PhotoView[] pv = new PhotoView[imageNumber];
        for (int i = 0; i < shareInfo.media.size(); i++) {
            pv[i] = new PhotoView(CONTEXT);
            try {
                getOnePhotoViewUrl(pv[i], shareInfo.media.get(i).media_id, 1);
            } catch (UnsupportedEncodingException e) {
                LogUtil.e(CONTEXT, "enter catch");
                e.printStackTrace();
            }
            vp_Adapter.add(pv[i]);
            vp_Adapter.notifyDataSetChanged();
        }
    }

    */
/**
     * 根据点击的图片来初始化弹窗显示的当前图片
     *//*

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

    */
/**
     * 评论的弹窗控件和事件
     *//*

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
        img_Pin_SharePop_LRDA.setOnClickListener(this);
        img_XinLang_SharePop_LRDA.setOnClickListener(this);
    }

    private TextView tv_Cancle_SharePop_LRDA;
    private ImageView img_WeiXin_SharePop_LRDA, img_QQ_SharePop_LRDA, img_XinLang_SharePop_LRDA,img_Pin_SharePop_LRDA;

    */
/**
     * 分享的弹窗控件和事件
     *//*

    private void initSharePopView() {
        tv_Cancle_SharePop_LRDA = (TextView) popView.findViewById(R.id.tv_Cancle_SharePop_LRDA);
        img_Pin_SharePop_LRDA = (ImageView) popView.findViewById(R.id.img_Pin_SharePop_LRDA);
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
            tv_SendComment_Pop_LRDA.setTextColor(getResources().getColor(R.color.line_color));
            tv_SendComment_Pop_LRDA.setBackgroundResource(R.mipmap.ic_qiandanedbg);
        } else {
            tv_SendComment_Pop_LRDA.setTextColor(getResources().getColor(R.color.white));
            tv_SendComment_Pop_LRDA.setBackgroundResource(R.mipmap.ic_qiandanbg);
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

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
    }

    */
/**
     * 控制 评论分享显示的位置
     *
     * @param view
     * @param firstVisibleItem
     * @param visibleItemCount
     * @param totalItemCount
     *//*

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        if (firstVisibleItem == 1 && ll_Top_LifeRewardDetail.getParent() == ll_ViewGroup2_LRDA) {
            ll_ViewGroup2_LRDA.setVisibility(View.GONE);
            ll_ViewGroup2_LRDA.removeView(ll_Top_LifeRewardDetail);
            ll_ViewGroup1_LRDA.addView(ll_Top_LifeRewardDetail);
        }
        if (firstVisibleItem > 1 && ll_Top_LifeRewardDetail.getParent() == ll_ViewGroup1_LRDA) {
            ll_ViewGroup2_LRDA.setVisibility(View.VISIBLE);
            ll_ViewGroup1_LRDA.removeView(ll_Top_LifeRewardDetail);
            ll_ViewGroup2_LRDA.addView(ll_Top_LifeRewardDetail);
        }
    }

    */
/**
     * 评论列表点击头像查看详情
     *
     * @param view
     * @param type
     *//*

    @Override
    public void setOnCommentAvatarListener(View view, int type) {
        HelpPeopleDetailCommentInfo.COMMENT info = commentAdapter.getItem((Integer) view.getTag());
        String user_id = info.user_id;
        String nickName = info.nickname;
        Bundle bundle = new Bundle();
        bundle.putString("user_id", user_id);
        bundle.putString("nickName", nickName);
        startActivity(LookOthersMassageActivity.class, bundle);

    }

    */
/**
     * 分享列表点击头像查看详情
     *
     * @param view
     * @param type
     *//*

    @Override
    public void setOnShareAvatarListener(View view, int type) {
        HelpPeopleDetailShareInfo.SHARE info = shareAdapter.getItem((Integer) view.getTag());
        String user_id = info.user_id;
        String nickName = info.nickname;
        Bundle bundle = new Bundle();
        bundle.putString("user_id", user_id);
        bundle.putString("nickName", nickName);
        startActivity(LookOthersMassageActivity.class, bundle);

    }

    public class BaseUIlisener implements IUiListener {

        @Override
        public void onComplete(Object o) {
            try {
                JSONObject obj = new JSONObject(o.toString());
                if (obj.getInt("ret") == 0) {
                    SendSharePost(shareInfo.id, 2);
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

    */
/**
     * 分享成功后发送至服务器
     * {
     * "session_id": "4085c9d4f420864c407e",
     * "target_id": 4085c91231j23kl407e,
     * "target_type": 1,
     * "type": 1,
     * }
     *//*

    private void SendSharePost(String target_id, final int type) {

        String url = ServerAPIConfig.SendSharePost;

        RequestParams params = new RequestParams();

        params.put("session_id", getSession_id());

        params.put("target_id", target_id);

        //被分享内容类别，1-求助，2-文明墙，7-网络悬赏
        params.put("target_type", 1);
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
                        getShareData(0);
                        popupWindoew.dismiss();
                        ToastUtil.shortshow(getApplicationContext(), "分享成功");
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
                LogUtil.e(CONTEXT, "网络连接失败");
                ToastUtil.shortshow(CONTEXT, R.string.toast_error_net);

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

    */
/**
     * 用户抢单,
     * 点击抢单按钮执行此函数
     *//*

    public void acceptOrder() {
        AsyncHttpResponseHandler res = new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                String str_json = new String(bytes);
                try {
                    JSONObject obj = new JSONObject(str_json);
                    if (0 != obj.getInt("code")) {
                        if (4102 == obj.getInt("code")) {
                            ToastUtil.shortshow(CONTEXT, R.string.toast_error_accept_order);
                            return;
                        }
                        if (4103 == obj.getInt("code")) {
                            ToastUtil.shortshow(CONTEXT, R.string.toast_error_accept_order1);
                            return;
                        }
                        if (4105 == obj.getInt("code")) {
                            ToastUtil.shortshow(CONTEXT, R.string.toast_error_accept_order2);
                            return;
                        }
                        if (2021 == obj.getInt("code")) {
                            ToastUtil.shortshow(CONTEXT, R.string.toast_error_accept_order3);
                            return;
                        }
                        LogUtil.e(CONTEXT, "请求参数错误");
                        return;
                    }
                    showPopupWindow(1);
                } catch (JSONException e) {
                    LogUtil.e(CONTEXT, "enter catch");
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {

                LogUtil.e(CONTEXT, "网络连接异常");
                ToastUtil.shortshow(CONTEXT, R.string.toast_error_net);
            }
        };

        RequestParams params = new RequestParams();
        params.put("session_id", sp.getString("session_id", null));
        params.put("id", id);
        params.put("prev_user_id", prev_user_id);
        AndroidAsyncHttp.post(ServerAPIConfig.Updata_AcceptOrder, params, res);


    }
}*/
