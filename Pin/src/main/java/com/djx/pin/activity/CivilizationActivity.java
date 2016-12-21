/*
package com.djx.pin.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.text.Editable;
import android.text.TextWatcher;
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
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.djx.pin.R;
import com.djx.pin.adapter.CivilizationListViewAdapter;
import com.djx.pin.adapter.LifeRewardListViewAdapter;
import com.djx.pin.adapter.ShowImagePagerAdapter;
import com.djx.pin.base.OldBaseActivity;
import com.djx.pin.beans.CivilizationInfo;
import com.djx.pin.beans.StaticBean;
import com.djx.pin.personal.ui.PersonalDataActivity;
import com.djx.pin.serverapiconfig.ServerAPIConfig;
import com.djx.pin.utils.AndroidAsyncHttp;
import com.djx.pin.utils.ScreenUtils;
import com.djx.pin.utils.ToastUtil;
import com.google.gson.Gson;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.tencent.connect.common.Constants;
import com.tencent.connect.share.QQShare;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;
import uk.co.senab.photoview.PhotoView;

*/
/**
 * Created by Administrator on 2016/6/14.
 *//*


public class CivilizationActivity extends OldBaseActivity implements View.OnClickListener, AdapterView.OnItemClickListener, LifeRewardListViewAdapter.ShareLisenner, TextWatcher, CivilizationListViewAdapter.ChatLisenner, CivilizationListViewAdapter.CommentLisenner, CivilizationListViewAdapter.ImageViewLisenner, CivilizationListViewAdapter.ShareLisenner, ViewPager.OnPageChangeListener, CivilizationListViewAdapter.AvatarLisenner {

    private LinearLayout ll_Back_LifeReward;
    private PullToRefreshListView lv_Reward_LifeReward;
    private CivilizationListViewAdapter adapter;
    private View v_ParentCover_LRDA;
    private TextView tv_CDA_IWantSay;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_civilization);
        initView();
        initEvent();
        //参数，上下文；分享接口，评论接口，私信接口，头像点击接口。
        adapter = new CivilizationListViewAdapter(this, this, this, this, this, this);

        lv_Reward_LifeReward.setAdapter(adapter);


        //获得经纬度
        getLatitudeLongitude();
        //获取服务器数据
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1000);
                    initData();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    */
/**
     * 添加listView中的数据
     *//*

    //经纬度
    double latitude;
    double longitude;
    LocationClient client;
    String session_id;
    int size;
    int index;
    int type;


    private void initData() {
///v1/culture_wall/list?session_id=4085c9d4f420864c407e&index=0&size=10&type=1&latitude=0&longitude=0
//        http://121.40.110.60:8001/v1/culture_wall/list?session_id=c4f9efa3b37e758ffc9f&index=0&type=2&latitude=0.0&longitude=0.0
        session_id = getSharedPreferences(StaticBean.USER_INFO, Context.MODE_PRIVATE).getString("session_id", null);

        if (type == 1 && (session_id == null || session_id.equals(""))) {
            ToastUtil.shortshow(getApplicationContext(), "请先登录应用");
            return;
        } else {
            index = 0;
            size = 10;
            type = 2;


            String url = null;
            if (session_id == null) {
                url = ServerAPIConfig.LookCivilization + "&index=" + index + "&size=" + size + "&type=" + type + "&latitude=" + latitude + "&longitude=" + longitude + "";

            }else {
                url = ServerAPIConfig.LookCivilization + "session_id=" + session_id + "&index=" + index + "&size=" + size + "&type=" + type + "&latitude=" + latitude + "&longitude=" + longitude + "";
            }

            Response.Listener<String> listenner = new Response.Listener<String>() {
                @Override
                public void onResponse(String s) {
                    Gson gson = new Gson();
                    CivilizationInfo info = gson.fromJson(s, CivilizationInfo.class);

                    if (info.code == 0) {
                        if (info.result.size > 0) {
                            List<CivilizationInfo.Result.CultureWallInfo> list = info.result.list;
                            for (int i = 0; i < list.size(); i++) {
                                CivilizationInfo.Result.CultureWallInfo lists = list.get(i);
                                adapter.addData(lists);
                                adapter.notifyDataSetChanged();
                                lv_Reward_LifeReward.onRefreshComplete();
                            }
                        } else {
                            ToastUtil.shortshow(getApplicationContext(), "没有新内容");
                            lv_Reward_LifeReward.onRefreshComplete();
                        }
                    } else {
                        errorCode(info.code);
                    }

                }
            };
            Response.ErrorListener errorlistener = new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError volleyError) {

                }
            };
            StringRequest request = new StringRequest(Request.Method.GET, url, listenner, errorlistener);

            getQueue().add(request);


        }
    }

    private void getLatitudeLongitude() {
        LocationClientOption option = new LocationClientOption();
        option.setCoorType("bd09ll");
        int span = 0;
        option.setScanSpan(span);
        client = new LocationClient(this);

        BDLocationListener locatonListener = new BDLocationListener() {
            @Override
            public void onReceiveLocation(BDLocation bdLocation) {
                latitude = bdLocation.getLatitude();
                longitude = bdLocation.getLongitude();
            }
        };
        client.registerLocationListener(locatonListener);
        client.setLocOption(option);
        client.start();

    }

    @Override
    protected void onStop() {
        super.onStop();
        client.stop();
    }

    private void initEvent() {
        ll_Back_LifeReward.setOnClickListener(this);

        lv_Reward_LifeReward.setOnItemClickListener(this);

        tv_CDA_IWantSay.setOnClickListener(this);

        //可以上下拉刷新
        lv_Reward_LifeReward.setMode(PullToRefreshBase.Mode.BOTH);
        //设置刷新监听
        lv_Reward_LifeReward.setOnRefreshListener(refreshListener);
    }

    PullToRefreshBase.OnRefreshListener2<ListView> refreshListener = new PullToRefreshBase.OnRefreshListener2<ListView>() {
        @Override
        public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
            adapter.clear();
            initData();
        }

        @Override
        public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
            index++;
            String url = ServerAPIConfig.LookCivilization + "session_id=" + session_id + "&index=" + index + "&size=" + size + "&type=" + type + "&latitude=" + latitude + "&longitude=" + longitude + "";

            Response.Listener<String> listenner = new Response.Listener<String>() {
                @Override
                public void onResponse(String s) {
                    Gson gson = new Gson();
                    CivilizationInfo info = gson.fromJson(s, CivilizationInfo.class);
                    if (info.code == 0) {
                        if (info.result.size > 0) {
                            List<CivilizationInfo.Result.CultureWallInfo> list = info.result.list;
                            for (int i = 0; i < list.size(); i++) {
                                CivilizationInfo.Result.CultureWallInfo lists = list.get(i);
                                adapter.addData(lists);
                                adapter.notifyDataSetChanged();
                                lv_Reward_LifeReward.onRefreshComplete();
                            }
                        } else {
                            ToastUtil.shortshow(getApplicationContext(), "暂无内容");
                            lv_Reward_LifeReward.onRefreshComplete();
                        }
                    } else {
                        errorCode(info.code);
                    }
                }
            };
            Response.ErrorListener errorlistener = new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError volleyError) {
                    lv_Reward_LifeReward.onRefreshComplete();
                }
            };
            StringRequest request = new StringRequest(Request.Method.GET, url, listenner, errorlistener);

            getQueue().add(request);
        }
    };

    private void initView() {
        ll_Back_LifeReward = (LinearLayout) findViewById(R.id.ll_Back_LifeReward);
        //ListView点击事件
        lv_Reward_LifeReward = (PullToRefreshListView) findViewById(R.id.lv_Reward_LifeReward);
        v_ParentCover_LRDA = findViewById(R.id.v_ParentCover_LRDA);
        tv_CDA_IWantSay = (TextView) findViewById(R.id.tv_CDA_IWantSay);
    }

    BaseUIlisener shareListener = new BaseUIlisener();

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_Back_LifeReward:
                this.finish();
                break;
            case R.id.tv_CDA_IWantSay:
                startActivity(CivilizationUpdataInfoActivity.class);
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
                break;
            //评论弹窗点击事件
            case R.id.tv_SendComment_Pop_LRDA:
                String comment = edt_AddComment_Pop_LRDA.getText().toString().trim();
                if (comment.equals("") || comment == null) {
                    ToastUtil.shortshow(this, "评论内容不能为空");
                } else {
                    edt_AddComment_Pop_LRDA.clearFocus();
                    InputMethodManager imm0 = (InputMethodManager) getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm0.hideSoftInputFromWindow(edt_AddComment_Pop_LRDA.getWindowToken(), 0);
                    popupWindoew.dismiss();
                }
                break;

            //浏览图片弹框的点击事件
            case R.id.ll_ShowImgeView_Pop_LRDA:
                popupWindoew.dismiss();
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        CivilizationInfo.Result.CultureWallInfo lifeRewardInfo = adapter.getItem(position - 1);
        int imageNumber = lifeRewardInfo.media.size();
        Bundle bundle = new Bundle();
        bundle.putString("id", lifeRewardInfo.id);
        bundle.putInt("imageNumber", imageNumber);
        bundle.putBoolean("showCommentPop", false);
        startActivity(CivilizationDetailActivity.class, bundle);

    }

    */
/**
     * 分享点击
     *//*

    CivilizationInfo.Result.CultureWallInfo info;
    //被分享内容的id
    String target_id;
    TextView tv_share_count;

    @Override
    public void setOnShareLisenner(int tag, View v) {
        tv_share_count = (TextView) v.findViewById(R.id.tv_ShareNumber_Civilization);
        info = adapter.getItem((Integer) v.getTag());
        target_id = info.id;
        switch (tag) {
            case 1:
                showPopupWindow(2);

                break;
        }
    }

    */
/**
     * 评论点击
     *//*

    @Override
    public void setOnCommentLisenner(int tag, View v) {
        CivilizationInfo.Result.CultureWallInfo info = adapter.getItem((Integer) v.getTag());
        switch (tag) {
            case 2:
                Bundle bundle = new Bundle();
                int imageNumber = info.media.size();
                bundle.putString("id", info.id);
                bundle.putInt("imageNumber", imageNumber);
                bundle.putBoolean("showCommentPop", true);
                startActivity(CivilizationDetailActivity.class, bundle);

                break;
        }
    }

    */
/**
     * 聊天点击
     *//*

    @Override
    public void setOnChatLisenner(int tag, View v) {
        CivilizationInfo.Result.CultureWallInfo info = adapter.getItem((Integer) v.getTag());
        switch (tag) {
            case 3:

                break;
        }
    }

    */
/**
     * 头像点击
     *//*

    @Override
    public void setOnAvatarLisenner(int tag, View v) {
        CivilizationInfo.Result.CultureWallInfo info = adapter.getItem((Integer) v.getTag());
        String user_id = info.user_id;
        Bundle bundle = new Bundle();
        bundle.putString("user_id", user_id);
        switch (tag) {
            case 4:
                if (user_id.equals(getUser_id())) {
                    startActivity(PersonalDataActivity.class);
                } else {
                    startActivity(LookOthersMassageActivity.class, bundle);
                }
                break;
        }
    }


    //图片数量
    int i;
    List<String> idList = new ArrayList<String>();

    @Override
    public void setOnImageViewLisenner(int tag, View v) {
        CivilizationInfo.Result.CultureWallInfo info = adapter.getItem((Integer) v.getTag());
        idList.clear();
        i = info.media.size();


        for (int j = 0; j < i; j++) {
            idList.add(info.media.get(j).media_id);
        }

        showPopupWindow(4);
        initPopImageView(tag + 1);

    }

    private PopupWindow popupWindoew;
    private View popView;

    public void showPopupWindow(int i) {
        popupWindoew = new PopupWindow();
        popupWindoew.setFocusable(true);
        popupWindoew.setOutsideTouchable(true);
        popupWindoew.setTouchable(true);
        parentCover(1);
        //根据i的值弹出不同的popupwindow,2分享；3评论;4浏览图片。
        switch (i) {
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
            case 2:
                popupWindoew.showAtLocation(ll_Back_LifeReward, Gravity.BOTTOM, 0, 0);
                break;
            case 3:
                popupWindoew.showAtLocation(ll_Back_LifeReward, Gravity.BOTTOM, 0, 0);
                break;
            case 4:
                popupWindoew.showAtLocation(ll_Back_LifeReward, Gravity.CENTER, 0, 0);
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
        //设置滑动监听
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
        initImageData();
        vp_LRDA_Pop.setAdapter(vp_Adapter);
    }

    */
/**
     * 加载图片
     *//*


    List<PhotoView> photoViewList = new ArrayList<PhotoView>();

    private void initImageData() {
        photoViewList.clear();
        switch (i) {
            case 1:
                vp_Adapter.clear();
                PhotoView pv1 = new PhotoView(getApplicationContext());
                photoViewList.add(pv1);
                try {
                    getPhotoViewUrl(photoViewList, i, idList, 1);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                vp_Adapter.add(pv1);
                break;
            case 2:
                vp_Adapter.clear();
                PhotoView pv2 = new PhotoView(getApplicationContext());
                PhotoView pv3 = new PhotoView(getApplicationContext());
                photoViewList.add(pv2);
                photoViewList.add(pv3);

                try {
                    getPhotoViewUrl(photoViewList, i, idList, 1);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
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

                try {
                    getPhotoViewUrl(photoViewList, i, idList, 1);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
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
                try {
                    getPhotoViewUrl(photoViewList, i, idList, 1);

                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                vp_Adapter.add(pv7);
                vp_Adapter.add(pv8);
                vp_Adapter.add(pv9);
                vp_Adapter.add(pv10);
                break;
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
        img_XinLang_SharePop_LRDA.setOnClickListener(this);
    }

    private TextView tv_Cancle_SharePop_LRDA;
    private ImageView img_WeiXin_SharePop_LRDA, img_QQ_SharePop_LRDA, img_XinLang_SharePop_LRDA;

    */
/**
     * 分享的弹窗控件和事件
     *//*

    private void initSharePopView() {
        tv_Cancle_SharePop_LRDA = (TextView) popView.findViewById(R.id.tv_Cancle_SharePop_LRDA);
        img_WeiXin_SharePop_LRDA = (ImageView) popView.findViewById(R.id.img_WeiXin_SharePop_LRDA);
        img_QQ_SharePop_LRDA = (ImageView) popView.findViewById(R.id.img_QQ_SharePop_LRDA);
        img_XinLang_SharePop_LRDA = (ImageView) popView.findViewById(R.id.img_XinLang_SharePop_LRDA);

    }

    public Bundle getBundle() {
        Bundle params = new Bundle();
        params.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, QQShare.SHARE_TO_QQ_TYPE_DEFAULT);
        params.putString(QQShare.SHARE_TO_QQ_TITLE, "");
        params.putString(QQShare.SHARE_TO_QQ_SUMMARY, "");
        params.putString(QQShare.SHARE_TO_QQ_TARGET_URL, "");
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

            try {
                JSONObject obj = new JSONObject(o.toString());
                int ret = obj.getInt("ret");
                if (ret == 0) {
                    SendSharePost(target_id, 2);

                } else {
                    ToastUtil.shortshow(getApplicationContext(), "分享失败");

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

    //特别注意：一定要添加以下代码，才可以从回调listener中获取到消息。
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constants.REQUEST_QQ_SHARE) {
            Tencent.onActivityResultData(requestCode, resultCode, data, shareListener);
        }
    }
}
*/
