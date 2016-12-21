package com.djx.pin.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.djx.pin.R;
import com.djx.pin.base.OldBaseActivity;
import com.djx.pin.beans.LostChildDetailInfo;
import com.djx.pin.beans.StaticBean;
import com.djx.pin.improve.common.PhotoShowActivity;
import com.djx.pin.myview.CircleImageView;
import com.djx.pin.serverapiconfig.ServerAPIConfig;
import com.djx.pin.sina.SinaConstants;
import com.djx.pin.ui.CommonDialog;
import com.djx.pin.utils.AndroidAsyncHttp;
import com.djx.pin.utils.BitmapUtil;
import com.djx.pin.utils.LogUtil;
import com.djx.pin.utils.QiniuUtils;
import com.djx.pin.utils.ScreenUtils;
import com.djx.pin.utils.ToastUtil;
import com.djx.pin.utils.TurnIntoTime;
import com.djx.pin.utils.Util;
import com.djx.pin.utils.myutils.ScreenTools;
import com.djx.pin.weixin.WXConstants;
import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.sina.weibo.sdk.api.share.IWeiboShareAPI;
import com.sina.weibo.sdk.api.share.WeiboShareSDK;
import com.tencent.connect.share.QQShare;
import com.tencent.mm.sdk.modelmsg.SendMessageToWX;
import com.tencent.mm.sdk.modelmsg.WXMediaMessage;
import com.tencent.mm.sdk.modelmsg.WXWebpageObject;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.UiError;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.util.TextUtils;

/**
 * Created by Administrator on 2016/8/14 0014.
 */
public class LostChildDetailActivity extends OldBaseActivity implements View.OnClickListener {

    LinearLayout ll_back, ll_share;
    //条目id
    String id;
    View v_ParentCover_CDA;
//条目内容
    String description;
    CircleImageView cimg_Avatar_MHDA;
    TextView tv_UserName_MHDA, tv_Time_MHDA, tv_Name_LCDA, tv_Sex_LCDA,tv_LostTime_LCDA,
            tv_LostPlace_LCDA, tv_Description_LCDA;
    ImageView iv_child_avatar;
    LinearLayout ll_child_images;
    private TextView tv_phone;
    private ArrayList<String> urlList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lostchilddetail);
        id = getIntent().getExtras().getString("id");
        initView();
        initBaseData(0);
        initEvent();
    }

    private void initEvent() {
        ll_back.setOnClickListener(this);
        ll_share.setOnClickListener(this);
        tv_phone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CommonDialog.show(LostChildDetailActivity.this, "确定", "取消", "拨打电话?", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(!TextUtils.isEmpty(tv_phone.getText().toString().trim())){
                            startCallPhoneByPermissions();
                        }
                    }
                });
            }
        });

    }

    @Override
    protected void actionCallPhone() {
        super.actionCallPhone();
        String phoneNumber = tv_phone.getText().toString().trim();
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_CALL);
        intent.setData(Uri.parse("tel:" + phoneNumber));
        //开启系统拨号器
        startActivity(intent);
    }

    private void initView() {
        ll_back = (LinearLayout) findViewById(R.id.ll_back);
        ll_share = (LinearLayout) findViewById(R.id.ll_share);
        v_ParentCover_CDA = findViewById(R.id.v_ParentCover_CDA);
        cimg_Avatar_MHDA = (CircleImageView)findViewById(R.id.cimg_Avatar_MHDA);
        tv_UserName_MHDA = (TextView)findViewById(R.id.tv_UserName_MHDA);
        tv_Time_MHDA = (TextView)findViewById(R.id.tv_Time_MHDA);
        tv_Name_LCDA = (TextView)findViewById(R.id.tv_Name_LCDA);
        tv_Sex_LCDA = (TextView)findViewById(R.id.tv_Sex_LCDA);
        tv_LostTime_LCDA = (TextView)findViewById(R.id.tv_LostTime_LCDA);
        tv_LostPlace_LCDA = (TextView)findViewById(R.id.tv_LostPlace_LCDA);
        tv_Description_LCDA = (TextView)findViewById(R.id.tv_Description_LCDA);
        iv_child_avatar = ((ImageView)findViewById(R.id.iv_child_avatar));
        ll_child_images = ((LinearLayout)findViewById(R.id.ll_child_images));
        tv_phone = ((TextView) findViewById(R.id.tv_LostPhone_LCDA));
    }


    private void initBaseData(final int index) {
        String session_id = getSharedPreferences(StaticBean.USER_INFO, Context.MODE_PRIVATE).getString("session_id", null);
        String url = ServerAPIConfig.LostChildDetail + "session_id=" + session_id + "&id=" + id + "&type=" + 1 + "&index=" + index + "&size=" + 10 + "";
        Log.e("url=======", url);
        AsyncHttpResponseHandler res = new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                String str = new String(bytes);
                Log.e("走失儿童详情==", str);
                Gson gson = new Gson();
                LostChildDetailInfo info = gson.fromJson(str, LostChildDetailInfo.class);
                if (info.code == 0) {
                    LostChildDetailInfo.Result result = info.result;
                    addDataBase(result);
                }
            }
            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
            }
        };
        AndroidAsyncHttp.get(url, res);
    }

    private void addDataBase(LostChildDetailInfo.Result result) {
        description=result.description;
        QiniuUtils.setAvatarByIdFrom7Niu(this,cimg_Avatar_MHDA, result.portrait);
        tv_UserName_MHDA.setText(result.nickname);
        tv_Time_MHDA.setText(TurnIntoTime.getCreateTime(result.create_time));
        tv_Name_LCDA.setText(result.name);
        switch (result.gender) {
            case 0:
                tv_Sex_LCDA.setText("未知");
                break;
            case 1:
                tv_Sex_LCDA.setText("男");
                break;
            case 2:
                tv_Sex_LCDA.setText("女");
                break;
        }

       /* Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat format = new SimpleDateFormat("yyyy年MM月dd日");
        Log.e("日期===", format.format(date));*/

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String asHoursTime = sdf.format(result.lost_time);

        tv_LostTime_LCDA.setText(asHoursTime);

        tv_LostPlace_LCDA.setText(result.lost_location);
        tv_Description_LCDA.setText("        "+result.description);
        tv_phone.setText(result.mobile);
        List<LostChildDetailInfo.Result.Media> medias = result.media;
        Log.i("test","---------------result.media"+medias);

        int width = ScreenTools.instance(this).dip2px(160);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(width, ViewGroup.LayoutParams.MATCH_PARENT);
        layoutParams.setMargins(10, 0, 10, 0);


        if(medias != null && medias.size() > 0){
            Log.i("test","---------------medias.size()"+medias.size()+"        medias.get(0).id  "+medias.get(0).media_id);
            //设置头像
            QiniuUtils.setAvatarByIdFrom7Niu(this,iv_child_avatar, medias.get(0).media_id);
            //设置详情图片
            ImageView imageView;



            final ArrayList<String> ids = new ArrayList<>();
            for (int i = 0; i < medias.size(); i++) {
                String media = medias.get(i).media_id;
                ids.add(media);
            }

            QiniuUtils.get7NiuIMGUrl(this, ids, 600, 600, 1, new QiniuUtils.GetUrlsCallBack() {
                @Override
                public void getUrlCallBack(final List<String> list) {
                    if (list != null && list.size() > 0) {
                        urlList = ((ArrayList<String>) list);
                    }
                }
            });

            for (int i = 0; i < medias.size(); i++) {
                final int j = i;
                imageView = new ImageView(this);
                imageView.setLayoutParams(layoutParams);
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                Log.i("test","---------------medias.size()"+medias.size()+"        medias.get(i).id  "+medias.get(i).media_id);
                QiniuUtils.setOneImageByIdFrom7Niu(this,imageView,medias.get(i).media_id);
                ll_child_images.addView(imageView);
                imageView.setTag(j);
                final ImageView finalImageView = imageView;
                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(urlList != null){
                            Intent intent = new Intent(LostChildDetailActivity.this, PhotoShowActivity.class);
                            Bundle bundle = new Bundle();
                            bundle.putInt("CURRENT_POS", (int) finalImageView.getTag());
                            intent.putStringArrayListExtra("URLS", urlList);
                            startActivity(intent);
                        }
                    }
                });
            }
        }
    }





    BaseUIlisener shareListener = new BaseUIlisener();

    /**
     * Called when a view has been clicked.
     *
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_back:
                this.finish();
                break;
            case R.id.ll_share:
                showPopupWindow();
                break;

            //分享弹窗点击事件
            case R.id.img_WeiXin_SharePop_LRDA:
                wxflag=false;
                regToWx();
                getWXInfo();
                SendSharePost(id,1);
                Bitmap bitmap1= BitmapFactory.decodeResource(getResources(),R.mipmap.logo_djx);
                WXShareWeb(ServerAPIConfig.LostChildShare+id,"众觅互助",description,bitmap1,50,50);
                break;
            case R.id.img_QQ_SharePop_LRDA:
                getmTencent().shareToQQ(this, getBundle(), shareListener);
                break;
            case R.id.img_WeiXinF_SharePop_LRDA:
                wxflag=true;
                regToWx();
                getWXInfo();
                SendSharePost(id,1);

                Bitmap bitmap= BitmapFactory.decodeResource(getResources(),R.mipmap.logo_djx);
                WXShareWeb(ServerAPIConfig.LostChildShare+id,"众觅互助",description,bitmap,50,50);
                break;
            case R.id.img_XinLang_SharePop_LRDA:

                Bitmap bitmap2 = BitmapFactory.decodeResource(getResources(),R.mipmap.logo_djx);
                SendSharePost(id, 3);
                sendMultiMessage(true, description, true, BitmapUtil.Bitmap2Bytes(bitmap2), true,
                        false, false, false);
                break;
            case R.id.tv_Cancle_SharePop_LRDA:
                popupWindoew.dismiss();
                break;
        }
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
     */
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
            shareAction.withTargetUrl(ServerAPIConfig.LostChildShare+id);
        }
        shareAction.withTitle("众觅互助");

        shareAction.setPlatform(SHARE_MEDIA.SINA).setCallback(umShareListener).share();

    }

    private UMShareListener umShareListener = new UMShareListener() {
        @Override
        public void onResult(SHARE_MEDIA platform) {
            com.umeng.socialize.utils.Log.d("plat","platform"+platform);
            if(platform.name().equals("WEIXIN_FAVORITE")){
                Toast.makeText(LostChildDetailActivity.this,platform + " 收藏成功啦",Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(LostChildDetailActivity.this, platform + " 分享成功啦", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void onError(SHARE_MEDIA platform, Throwable t) {
            Toast.makeText(LostChildDetailActivity.this,platform + " 分享失败啦", Toast.LENGTH_SHORT).show();
            if(t!=null){
                com.umeng.socialize.utils.Log.d("throw","throw:"+t.getMessage());
            }
        }

        @Override
        public void onCancel(SHARE_MEDIA platform) {
            Toast.makeText(LostChildDetailActivity.this,platform + " 分享取消了", Toast.LENGTH_SHORT).show();
        }
    };
    private PopupWindow popupWindoew;
    private View popView;

    public void showPopupWindow() {
        popupWindoew = new PopupWindow();
        popupWindoew.setFocusable(true);
        popupWindoew.setOutsideTouchable(true);
        popupWindoew.setTouchable(true);
        parentCover(1);
        popupWindoew.setWidth((ScreenUtils.getScreenWidth(this)));
        popupWindoew.setHeight(ScreenUtils.getScreenHeight(this) / 4);
        popView = LayoutInflater.from(this).inflate(R.layout.popupwindow_lrda_share, null);
        popupWindoew.setBackgroundDrawable(getResources().getDrawable(R.color.transparent));
        initShareView();
        initShareEvent();

        //初始化抢单弹窗控件和事件
        popupWindoew.setContentView(popView);
        popupWindoew.showAtLocation(v_ParentCover_CDA, Gravity.BOTTOM, 0, 0);
        popupWindoew.setOnDismissListener(new PopupWindow.OnDismissListener() {
                                              @Override
                                              public void onDismiss() {
                                                  parentCover(2);

                                              }
                                          }
        );
    }

    private void initShareEvent() {
        img_WeiXin_SharePop_LRDA.setOnClickListener(this);
        img_QQ_SharePop_LRDA.setOnClickListener(this);
        img_XinLang_SharePop_LRDA.setOnClickListener(this);
        tv_Cancle_SharePop_LRDA.setOnClickListener(this);
        img_WeiXinF_SharePop_LRDA.setOnClickListener(this);
    }

    ImageView img_Pin_SharePop_LRDA, img_WeiXin_SharePop_LRDA, img_QQ_SharePop_LRDA, img_XinLang_SharePop_LRDA,img_WeiXinF_SharePop_LRDA;
    TextView tv_Cancle_SharePop_LRDA;

    //分享弹窗的view
    private void initShareView() {

        img_WeiXin_SharePop_LRDA = (ImageView) popView.findViewById(R.id.img_WeiXin_SharePop_LRDA);
        img_QQ_SharePop_LRDA = (ImageView) popView.findViewById(R.id.img_QQ_SharePop_LRDA);
        img_XinLang_SharePop_LRDA = (ImageView) popView.findViewById(R.id.img_XinLang_SharePop_LRDA);
        tv_Cancle_SharePop_LRDA = (TextView) popView.findViewById(R.id.tv_Cancle_SharePop_LRDA);
        img_WeiXinF_SharePop_LRDA = (ImageView) popView.findViewById(R.id.img_WeiXinF_SharePop_LRDA);
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
        params.putString(QQShare.SHARE_TO_QQ_TARGET_URL, ServerAPIConfig.LostChildShare+id);
        params.putString(QQShare.SHARE_TO_QQ_IMAGE_URL, ServerAPIConfig.Logo);
        params.putString(QQShare.SHARE_TO_QQ_APP_NAME, "早起斗地主");
        return params;
    }

    public class BaseUIlisener implements IUiListener {

        @Override
        public void onComplete(Object o) {
            try {
                JSONObject obj = new JSONObject(o.toString());
                if (obj.getInt("ret") == 0) {
                    popupWindoew.dismiss();
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

    private void SendSharePost(String target_id, final int type) {

        String url = ServerAPIConfig.SendSharePost;

        RequestParams params = new RequestParams();

        params.put("session_id", getSession_id());

        params.put("target_id", target_id);

        //被分享内容类别，1-求助，2-文明墙，7-网络悬赏
        params.put("target_type", 10);
        //分享类型，1-分享微信，2-分享QQ，3-分享微博
        params.put("type", type);
        Log.e("params===", url + params.toString());
        AsyncHttpResponseHandler res = new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                String str = new String(bytes);
                Log.e("str=====", str);
                try {
                    JSONObject object = new JSONObject(str);
                    if (object.getInt("code") == 0) {
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

