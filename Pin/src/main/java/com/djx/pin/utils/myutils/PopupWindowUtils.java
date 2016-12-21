package com.djx.pin.utils.myutils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.djx.pin.R;
import com.djx.pin.application.PinApplication;
import com.djx.pin.beans.StaticBean;
import com.djx.pin.serverapiconfig.ServerAPIConfig;
import com.djx.pin.utils.AndroidAsyncHttp;
import com.djx.pin.utils.BitmapUtil;
import com.djx.pin.utils.LogUtil;
import com.djx.pin.utils.ScreenUtils;
import com.djx.pin.utils.ToastUtil;
import com.djx.pin.utils.Util;
import com.djx.pin.weixin.WXConstants;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
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

import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

/**
 * Created by 柯传奇 on 2016/10/19 0019.
 */
public class PopupWindowUtils {

    //qqAPPID
    public static final String APPID = "1105544348";
    private static WindowManager.LayoutParams lp;
    private static PopupWindow popWindow = null;
    private static PinSuccedCallBack pinSucceedCallBack;
    private static String prev_user_id;

    public static void setPinSucceedCallBack(PinSuccedCallBack pinSucceedCallBack,String prev_user_id) {
        PopupWindowUtils.pinSucceedCallBack = pinSucceedCallBack;
        PopupWindowUtils.prev_user_id = prev_user_id;
    }

    /**
     * 分享(qq,微信,朋友圈,新浪)
     * @param context
     * @param target_id 1-求助，2-文明墙，7-网络悬赏，10-走失儿童
     * @param session_id
     * @param parent
     * @param isPinShow 是否显示pin
     */
    public static void sharePopupWindow(final Activity context, final String target_id,final int target_type, final String session_id, final String description, final View parent,Boolean isPinShow) {
        View popView = LayoutInflater.from(context).inflate(R.layout.popupwindow_lrda_share, null);
        popWindow = new PopupWindow(popView,  ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        //设置layout在PopupWindow中显示的位置
        popWindow.showAtLocation(parent, Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL, 0, 0);
        TextView tv_Cance = (TextView) popView.findViewById(R.id.tv_Cancle_SharePop_LRDA);
        ImageView iv_weixin = (ImageView) popView.findViewById(R.id.img_WeiXin_SharePop_LRDA);
        ImageView iv_qq = (ImageView) popView.findViewById(R.id.img_QQ_SharePop_LRDA);
        ImageView iv_xinlang = (ImageView) popView.findViewById(R.id.img_XinLang_SharePop_LRDA);
        ImageView iv_weixinF = (ImageView) popView.findViewById(R.id.img_WeiXinF_SharePop_LRDA);
        ImageView iv_pin = (ImageView) popView.findViewById(R.id.iv_pin);
        if(isPinShow){
            iv_pin.setVisibility(View.VISIBLE);
            iv_pin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    updataPinInfo(context,target_id,prev_user_id,popWindow,pinSucceedCallBack);
                }
            });
        }
        tv_Cance.setOnClickListener(new View.OnClickListener() {//取消
            @Override
            public void onClick(View v) {
                lp.alpha = 1f;
                context.getWindow().setAttributes(lp);
                popWindow.dismiss();
            }
        });

        lp = context.getWindow().getAttributes();
        lp.alpha = 0.5f;// 设置背景颜色变暗
        context.getWindow().setAttributes(lp);
        String serverAPIConfig = null;
        switch (target_type){//1-求助，2-文明墙，7-网络悬赏，10-走失儿童
            case 1:
                serverAPIConfig = ServerAPIConfig.OffLineShare;
                break;
            case 2:
                serverAPIConfig = ServerAPIConfig.CivilizationShare;
                break;
            case 7:
                serverAPIConfig = ServerAPIConfig.OnLineShare;
                break;
            case 10:
                serverAPIConfig = ServerAPIConfig.LostChildShare;
                break;
        }
        final String finalServerAPIConfig = serverAPIConfig;
        iv_weixin.setOnClickListener(new View.OnClickListener() {//微信
            @Override
            public void onClick(View v) {
                IWXAPI wxapi = WXAPIFactory.createWXAPI(context, WXConstants.APP_ID, true);
                wxapi.registerApp(WXConstants.APP_ID);
                // 检查是否安装微信
                if (wxapi.isWXAppInstalled()) {//已安装
                    if (context.getSharedPreferences(StaticBean.USER_INFO, Context.MODE_PRIVATE).getBoolean("isLogined", false) == false) {//没登陆
                        ToastUtil.shortshow(context, R.string.toast_non_login);
                        return;
                    } else {
                        Bitmap bitmap1= BitmapFactory.decodeResource(context.getResources(),R.mipmap.logo_djx);
                        WXShareWeb(context,wxapi, finalServerAPIConfig +target_id,"众觅互助",description,bitmap1,50,50,false);
                        SendSharePost(target_id, 1,target_type, session_id,popWindow);
                    }
                } else {
                    ToastUtil.shortshow(context, R.string.toast_weixin_share_uninstalled);
                    return;
                }
            }
        });

        iv_qq.setOnClickListener(new View.OnClickListener() {//qq
            @Override
            public void onClick(View v) {
                getmTencent(context).shareToQQ(context, getBundle(finalServerAPIConfig,target_id, description), new IUiListener() {
                    @Override
                    public void onComplete(Object o) {
                        try {
                            JSONObject obj = new JSONObject(o.toString());
                            if (obj.getInt("ret") == 0) {
                                SendSharePost(target_id, 2,target_type,session_id,popWindow);
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
                });
            }
        });
        iv_xinlang.setOnClickListener(new View.OnClickListener() {//新浪
            @Override
            public void onClick(View v) {
                Bitmap bitmap2 = BitmapFactory.decodeResource(context.getResources(),R.mipmap.logo_djx);
                SendSharePost(target_id, 3,target_type,session_id,popWindow);
                sendMultiMessage(context,target_id,true, description, true, BitmapUtil.Bitmap2Bytes(bitmap2), true, false, false, false);
            }
        });

        iv_weixinF.setOnClickListener(new View.OnClickListener() {//微信朋友圈
            @Override
            public void onClick(View v) {
                IWXAPI wxapi = WXAPIFactory.createWXAPI(context, WXConstants.APP_ID, true);
                wxapi.registerApp(WXConstants.APP_ID);
                // 检查是否安装微信
                if (wxapi.isWXAppInstalled()) {//已安装
                    if (context.getSharedPreferences(StaticBean.USER_INFO, Context.MODE_PRIVATE).getBoolean("isLogined", false) == false) {//没登陆
                        ToastUtil.shortshow(context, R.string.toast_non_login);
                        return;
                    } else {
                        Bitmap bitmap1= BitmapFactory.decodeResource(context.getResources(),R.mipmap.logo_djx);
                        WXShareWeb(context,wxapi,finalServerAPIConfig+target_id,"众觅互助",description,bitmap1,50,50,true);
                        SendSharePost(target_id, 1,target_type, session_id,popWindow);
                    }
                } else {
                    ToastUtil.shortshow(context, R.string.toast_weixin_share_uninstalled);
                    return;
                }
            }
        });

        popWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                lp.alpha = 1f;
                context.getWindow().setAttributes(lp);
            }
        });
    }


    public interface CommentSuccedCallBack{
        void commentSucceed();
    }

    /**
     *
     * @param target_id
     * @param type //被评论内容类别，1-求助，2-文明墙，7-网络悬赏
     * @param session_id
     * @param context
     * @param parent
     * @param succedCallBack
     */
    public static void commentPopupWindow(final String target_id, final int type, final String session_id, final Activity context, View parent, final CommentSuccedCallBack succedCallBack){

        popWindow = new PopupWindow();
        popWindow.setFocusable(true);
        //防止PopupWindow被软件盘挡住
        popWindow.setSoftInputMode(PopupWindow.INPUT_METHOD_NEEDED);
        popWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        popWindow.setWidth(ScreenUtils.getScreenWidth(context));
        popWindow.setHeight(LinearLayout.LayoutParams.WRAP_CONTENT);
        View popView = LayoutInflater.from(context).inflate(R.layout.popupwindow_lrda_comment, null);
        popWindow.setBackgroundDrawable(context.getResources().getDrawable(R.color.transparent));
        final EditText edt_AddComment = (EditText) popView.findViewById(R.id.edt_AddComment_Pop_LRDA);
        final TextView tv_SendComment = (TextView) popView.findViewById(R.id.tv_SendComment_Pop_LRDA);
        edt_AddComment.setFocusable(true);
        edt_AddComment.setFocusableInTouchMode(true);
        edt_AddComment.requestFocus();
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(1000, InputMethodManager.HIDE_NOT_ALWAYS);
        //初始化评论弹窗控件和事件
        popView.setFocusable(true);
        popView.setFocusableInTouchMode(true);
        popWindow.setOutsideTouchable(true);
        popWindow.setTouchable(true);
        // 1)重写onKeyListener,点击返回键，取消pupwindow
        popView.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    popWindow.dismiss();
                    popWindow = null;
                    lp.alpha = 1f;// 返回重新变亮
                    context.getWindow().setAttributes(lp);
                    return true;
                }
                return false;
            }
        });
        lp = context.getWindow().getAttributes();
        lp.alpha = 0.5f;// 设置背景颜色变暗
        context.getWindow().setAttributes(lp);
        edt_AddComment.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String comment = edt_AddComment.getText().toString().trim();
                if (comment.equals("") || comment.length() == 0) {
                    tv_SendComment.setTextColor(context.getResources().getColor(R.color.text_color_hint));
                    tv_SendComment.setBackgroundResource(R.drawable.shape_sendbt_gray);
                } else {
                    tv_SendComment.setTextColor(context.getResources().getColor(R.color.white));
                    tv_SendComment.setBackgroundResource(R.drawable.shape_sendbt_blue);
                }
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });
        tv_SendComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String content = edt_AddComment.getText().toString();
                if (content.equals("") || content == null) {
                    ToastUtil.shortshow(context, "评论内容不能为空");
                } else {
                    SendCommnetPost(context,target_id,session_id, content,type,popWindow,succedCallBack);
                }
            }
        });
        popWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                lp.alpha = 1f;
                context.getWindow().setAttributes(lp);
            }
        });
        popWindow.setContentView(popView);
        popWindow.showAtLocation(parent, Gravity.BOTTOM, 0, 0);
    }

    private static void SendCommnetPost(final Context context, String target_id, String session_id, final String content, int type,final PopupWindow popWindow, final CommentSuccedCallBack succedCallBack) {
        String url = ServerAPIConfig.UPdata_Comment;
        RequestParams params = new RequestParams();
        params.put("session_id", session_id);
        params.put("target_id", target_id);
        //被评论内容类别，1-求助，2-文明墙，7-网络悬赏
        params.put("target_type", type);
        //评论内容
        params.put("content", content);
        AsyncHttpResponseHandler res = new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                String str = new String(bytes);
                try {
                    JSONObject object = new JSONObject(str);
                    if (object.getInt("code") == 0) {
                        ToastUtil.longshow(context,"评论成功");
                        succedCallBack.commentSucceed();
                        popWindow.dismiss();
                    }else if(2113 == object.getInt("code")){
                        ToastUtil.longshow(context,R.string.sensitive_word);
                    } else {
                        //errorCode(object.getInt("code"));
                        popWindow.dismiss();
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


    private static void sendMultiMessage(final Activity context, String target_id, Boolean hasText, String text,
                                         Boolean hasImage, byte[] imgByte, Boolean hasWebpage,
                                         Boolean hasMusic, Boolean hasVideo, Boolean hasVoice) {
        ShareAction shareAction = new ShareAction(context);
        if (hasText) {
            shareAction.withText(text);
        }
        if (hasImage) {
            LogUtil.e("有图片");
            UMImage image = new UMImage(context, imgByte);
            shareAction.withMedia(image);
        }

        if (hasWebpage) {
            LogUtil.e("有网页");
            shareAction.withTargetUrl(ServerAPIConfig.OffLineShare+target_id);
        }
        shareAction.withTitle("众觅互助");

        shareAction.setPlatform(SHARE_MEDIA.SINA).setCallback(new UMShareListener() {
            @Override
            public void onResult(SHARE_MEDIA share_media) {
                com.umeng.socialize.utils.Log.d("plat","platform"+share_media);
                if(share_media.name().equals("WEIXIN_FAVORITE")){
                    Toast.makeText(context, share_media + " 收藏成功啦",Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(context, share_media + " 分享成功啦", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(SHARE_MEDIA share_media, Throwable throwable) {

            }

            @Override
            public void onCancel(SHARE_MEDIA share_media) {
                Toast.makeText(context,share_media + " 分享取消了", Toast.LENGTH_SHORT).show();
            }
        }).share();
    }


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
    public static void WXShareWeb(Context context,IWXAPI wxapi,String webpageUrl, String title, String description,
                           Bitmap bmp, int dstWidth, int dstHeight,boolean wxflag) {

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
        req.transaction = String.valueOf(System.currentTimeMillis());
        req.message = msg;

        // 判断flag的值
        req.scene = wxflag ? SendMessageToWX.Req.WXSceneTimeline : SendMessageToWX.Req.WXSceneSession;

        if (wxflag == true && wxapi.getWXAppSupportAPI() < 0x21020001) {
            ToastUtil.shortshow(context, "当前微信版本较低,暂不支持分享至朋友圈");
            return;
        }
        // 调用api接口发送数据到微信
        wxapi.sendReq(req);
    }

    public static Bundle getBundle(String serverAPIConfig,String target_id,String description) {
        Bundle params = new Bundle();
        params.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, QQShare.SHARE_TO_QQ_TYPE_DEFAULT);
        params.putString(QQShare.SHARE_TO_QQ_TITLE, "众觅互助");
        params.putString(QQShare.SHARE_TO_QQ_SUMMARY, description);
        params.putString(QQShare.SHARE_TO_QQ_TARGET_URL, serverAPIConfig+target_id);
        params.putString(QQShare.SHARE_TO_QQ_IMAGE_URL, ServerAPIConfig.Logo);
        params.putString(QQShare.SHARE_TO_QQ_APP_NAME, "众觅");
        return params;
    }

    /**
     * 获得QQSDK控制对象
     */
    public static Tencent getmTencent(Context context) {

        Tencent mTencent = null;
        if (mTencent == null) {
            mTencent = Tencent.createInstance(APPID, context);
            return mTencent;
        }
        return mTencent;
    }

    /**
     *
     * @param target_id 被分享内容id
     * @param type  分享类型，1-分享微信，2-分享QQ，3-分享微博
     * @param target_type 被分享内容类别，1-求助，2-文明墙，7-网络悬赏，10-走失儿童
     * @param session_id
     * @param popWindow
     */
    protected static void SendSharePost(String target_id, final int type,final int target_type, String session_id, final PopupWindow popWindow) {

        String url = ServerAPIConfig.SendSharePost;
        RequestParams params = new RequestParams();
        params.put("session_id", session_id);
        params.put("target_id", target_id);
        //被分享内容类别，1-求助，2-文明墙，7-网络悬赏
        params.put("target_type", target_type);
        //分享类型，1-分享微信，2-分享QQ，3-分享微博
        params.put("type", type);
        AsyncHttpResponseHandler res = new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                String str = new String(bytes);
                Log.i("test","------------------------------str--------------"+str);
                try {
                    JSONObject object = new JSONObject(str);
                    if (object.getInt("code") == 0) {//分享成功
                        //ToastUtil.shortshow(getApplicationContext(), "分享成功");
                    } else {
                       // context.errorCode(object.getInt("code"));
                    }
                    popWindow.dismiss();

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                Log.i("test","------------------------------onFailure--------------");
            }
        };
        AndroidAsyncHttp.post(url, params, res);
    }

    public interface PinSuccedCallBack{
        void pinSucceedCallBack();
    }
    /**
     * 发送pin至服务器
     */
    private static void updataPinInfo(final Context context, String id, String prev_user_id, final PopupWindow popWindow, final PinSuccedCallBack pinSuccedCallBack) {
        SharedPreferences sp = context.getSharedPreferences(StaticBean.USER_INFO, Context.MODE_PRIVATE);
        if (sp.getBoolean("isLogined", false) == false) {//没登陆
            ToastUtil.shortshow(context, R.string.toast_non_login);
            return;
        }
        AsyncHttpResponseHandler resPin = new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                String strJson = new String(bytes);
                try {
                    JSONObject obj = new JSONObject(strJson);
                    if (obj.getInt("code") == 0) {
                        ToastUtil.shortshow(context, R.string.toast_success_pin);
                        popWindow.dismiss();
                        //刷新数据
                        pinSuccedCallBack.pinSucceedCallBack();
                    } else {
                        ToastUtil.errorCode(context,obj.getInt("code"));
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                ToastUtil.shortshow(context, R.string.toast_error_net);
            }
        };
        RequestParams params = new RequestParams();
        params.put("session_id", sp.getString("session_id", null));
        params.put("id", id);
        params.put("prev_user_id", prev_user_id);
        PinApplication myApp = PinApplication.getMyApp();
        params.put("province", myApp.getBdLocation().getProvince());
        params.put("city", myApp.getBdLocation().getCity());
        params.put("district", myApp.getBdLocation().getDistrict());
        params.put("address", myApp.getBdLocation().getAddrStr());
        params.put("latitude", myApp.getBdLocation().getLatitude());
        params.put("longitude", myApp.getBdLocation().getLongitude());
        AndroidAsyncHttp.post(ServerAPIConfig.HelperPin, params, resPin);


    }

}
