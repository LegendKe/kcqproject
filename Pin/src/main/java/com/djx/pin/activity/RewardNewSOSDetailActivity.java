package com.djx.pin.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.djx.pin.R;
import com.djx.pin.application.PinApplication;
import com.djx.pin.base.OldBaseActivity;
import com.djx.pin.beans.MyHelperSOSDetailInfo;
import com.djx.pin.beans.UserInfo;
import com.djx.pin.myview.CircleImageView;
import com.djx.pin.serverapiconfig.ServerAPIConfig;
import com.djx.pin.utils.AndroidAsyncHttp;
import com.djx.pin.utils.DateUtils;
import com.djx.pin.utils.LogUtil;
import com.djx.pin.utils.MyViewUtils;
import com.djx.pin.utils.QiniuUtils;
import com.djx.pin.utils.ToastUtil;
import com.djx.pin.utils.myutils.LogicUtils;
import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.util.TextUtils;

/**
 * Created by Administrator on 2016/6/24.
 */
public class RewardNewSOSDetailActivity extends OldBaseActivity implements View.OnClickListener  {
    protected final static String TAG = RewardNewSOSDetailActivity.class.getSimpleName();

    private LinearLayout ll_spread, ll_Back;
    private TextView tv_btn_order, tv_price, tv_need_people_count, tv_valid_duration, tv_detail_content, tv_spread, tv_location, tv_TaskGrab_MHDA, tv_TaskDo_MHDA, tv_TaskComplete_MHDA, tv_TaskRewardMoney,tv_username, tv_time;
    private CircleImageView cimg_avatar;

    private Bundle bundle;
    private MyHelperSOSDetailInfo info;
    private boolean isSpread = false;//控制展开全文,默认false,即不展开
    private ImageView iv_spread, cimg_Avatar;
    private ImageView img_TaskState_MHDA, img_ShowAllContent_MHDA;
    private int color_blue = 0XFF1295F7, color_gray = 0XFF8A8A8A;
    private BDLocation mLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rewardsosdetail);
        bundle = getIntent().getExtras();
        mLocation = PinApplication.getMyApp().getBdLocation();
        initView();
        initData();
        initEvent();
    }

    private void initView() {
        //init headview
        tv_btn_order = (TextView) findViewById(R.id.tv_btn_order);
        tv_price = (TextView) findViewById(R.id.tv_price);
        tv_need_people_count = (TextView) findViewById(R.id.tv_need_people_count);
        tv_valid_duration = (TextView) findViewById(R.id.tv_valid_duration);
        tv_detail_content = (TextView) findViewById(R.id.tv_detail_content);
        tv_spread = (TextView) findViewById(R.id.tv_spread);
        ll_spread = (LinearLayout)findViewById(R.id.ll_spread);
        ll_Back = (LinearLayout)findViewById(R.id.ll_Back);

        cimg_avatar = (CircleImageView)findViewById(R.id.cimg_avatar);
        iv_spread = (ImageView) findViewById(R.id.iv_spread);
        tv_location = (TextView)findViewById(R.id.tv_location);

        cimg_Avatar = (CircleImageView) findViewById(R.id.cimg_Avatar);
        img_TaskState_MHDA = (ImageView) findViewById(R.id.img_TaskState_MHDA);
        tv_TaskDo_MHDA = (TextView) findViewById(R.id.tv_TaskDo_MHDA);
        tv_TaskGrab_MHDA = (TextView) findViewById(R.id.tv_TaskGrab_MHDA);
        tv_TaskComplete_MHDA = (TextView) findViewById(R.id.tv_TaskComplete_MHDA);
        tv_TaskRewardMoney = (TextView) findViewById(R.id.tv_TaskRewardMoney);
        tv_username = (TextView) findViewById(R.id.tv_username);
        tv_time = (TextView) findViewById(R.id.tv_time);


        //根据内容行数决定显示“展开”的显示与隐藏
        MyViewUtils.viewShowAccordingLines(ll_spread,tv_detail_content);
    }

    private void initData() {
        String url = ServerAPIConfig.Do_SOSDetail + "session_id=" + UserInfo.getSessionID(this) + "&id=" + bundle.getString("id", null) + "&index=" + 0 + "&size=" + 10;
        String session_id = UserInfo.getSessionID(this);
        String id = bundle.getString("id");
        int type = bundle.getInt("type");

        AsyncHttpResponseHandler res = new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                String str_json = new String(bytes);
                Log.i(TAG, str_json);
                try {
                    JSONObject obj = new JSONObject(str_json);
                    if (0 != obj.getInt("code")) {
                        Log.i(TAG, "initData 数据解析错误:code=" + obj.toString());
                        errorCode(obj.getInt("code"));
                        return;
                    }
                    Gson gson = new Gson();
                    info = gson.fromJson(obj.getJSONObject("result").toString(), MyHelperSOSDetailInfo.class);
                    initContentView();
                } catch (JSONException e) {
                    Log.i(TAG, "enter catch=" + e.toString());
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                Log.i(TAG, "网络连接异常");
                ToastUtil.shortshow(RewardNewSOSDetailActivity.this, R.string.toast_error_net);
            }
        };

        AndroidAsyncHttp.get(url, res);
    }

    private void initContentView() {
        if (info == null) {
            return;
        }

        QiniuUtils.setAvatarByIdFrom7Niu(this,cimg_avatar,info.portrait);

        tv_price.setText(info.price + "");
        tv_need_people_count.setText("不限");
        tv_valid_duration.setText(DateUtils.formatDate(new Date(info.start_time), DateUtils.yyyyMMDD) + " 至 " + DateUtils.formatDate(new Date(info.end_time), DateUtils.yyyyMMDD));
        tv_detail_content.setText(info.nickname + getString(R.string.sos_desc));
        tv_location.setText(info.location);
        tv_username.setText(info.nickname);
        tv_time.setText(DateUtils.formatDate(new Date(info.start_time), DateUtils.yyyyMMDD));

        //-1-未抢单，0-参与，1-完成等待确认，2-获得赏金，3-完成未获得赏金，4-申诉判定完成，5-中途放弃
        Log.i(TAG, "info.process_status is " + info.process_status);
        Log.i(TAG, "info.status is " + info.status);
        switch (info.process_status) {
            case -1:
                tv_btn_order.setText("抢单");
            /**订单状态在 0-参与*/
            case 0:

                /*此时发单者已经安全*/
                if (info.status > 0) {
                    img_TaskState_MHDA.setImageResource(R.mipmap.task_progress_2dot_02);
                    tv_TaskGrab_MHDA.setVisibility(View.GONE);
                    tv_TaskDo_MHDA.setTextColor(color_blue);
                    tv_TaskComplete_MHDA.setVisibility(View.GONE);
                    tv_TaskRewardMoney.setText("已结束");
                    tv_TaskRewardMoney.setTextColor(color_blue);
                    tv_btn_order.setText("已结束");
                    //tv_btn_order.setBackground(getResources().getDrawable(R.mipmap.ic_qiandanedbg));
                } else {
                    img_TaskState_MHDA.setImageResource(R.mipmap.task_progress_4dot_02);
                    tv_TaskDo_MHDA.setTextColor(color_blue);
                    tv_TaskComplete_MHDA.setTextColor(color_gray);
                    tv_TaskRewardMoney.setTextColor(color_gray);

                    tv_btn_order.setText("导航");
                }
                break;

            /**订单状态在 1-完成等待确认*/
            case 1:
                img_TaskState_MHDA.setImageResource(R.mipmap.task_progress_4dot_03);
                tv_TaskDo_MHDA.setTextColor(color_blue);
                tv_TaskComplete_MHDA.setTextColor(color_blue);
                tv_TaskRewardMoney.setTextColor(color_gray);
                tv_btn_order.setText("已完成");
                break;
            /**订单状态在 2-获得赏金*/
            case 2:
                img_TaskState_MHDA.setImageResource(R.mipmap.task_progress_4dot_04);
                tv_TaskDo_MHDA.setTextColor(color_blue);
                tv_TaskComplete_MHDA.setTextColor(color_blue);
                tv_TaskRewardMoney.setTextColor(color_blue);
                tv_btn_order.setText("已结束");
                break;
            /**订单状态在 3-完成未获得赏金*/
            case 3:
                img_TaskState_MHDA.setImageResource(R.mipmap.task_progress_4dot_03);
                tv_TaskDo_MHDA.setTextColor(color_blue);
                tv_TaskComplete_MHDA.setTextColor(color_blue);
                tv_TaskRewardMoney.setTextColor(color_gray);
                tv_btn_order.setText("已完成");
                break;
            /**申诉判定完成*/
            case 4:
                break;
            /**中途放弃*/
            case 5:
                break;
            default:
                break;
        }

    }

    private void initEvent() {
        tv_btn_order.setOnClickListener(this);

        cimg_avatar.setOnClickListener(this);
        tv_location.setOnClickListener(this);
        tv_btn_order.setOnClickListener(this);
        ll_spread.setOnClickListener(this);
        ll_Back.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        Log.i(TAG, "onClick");
        switch (view.getId()) {
            case R.id.ll_Back:
                this.finish();
                break;
            /**点击展开全文*/
            case R.id.ll_spread:
                if (isSpread) {
                    tv_detail_content.setMaxLines(6);
                    tv_spread.setText(R.string.tv_spread);
                    iv_spread.setImageResource(R.mipmap.ic_downopen);
                    isSpread = false;
                } else {
                    tv_detail_content.setMaxLines(100);
                    tv_spread.setText(R.string.tv_inspread);
                    iv_spread.setImageResource(R.mipmap.ic_upclose);
                    isSpread = true;
                }
                break;
            case R.id.ll_Back_LifeRewardDetail:
                this.finish();
                break;
            case R.id.cimg_Avatar_HPDA:
                String user_id = info.user_id;
                String nickName = info.nickname;
                Bundle bundle = new Bundle();
                bundle.putString("user_id", user_id);
                bundle.putString("nickName", nickName);
                startActivity(LookOthersMassageActivity.class, bundle);
                break;
            case R.id.tv_location:
            case R.id.tv_btn_order:
                if (checkSOSReceiveUserInfo()) {
                    LogicUtils.realNameVerify(this, new LogicUtils.AfterPassedListener() {
                        @Override
                        public void realNameVerifyPassed() {
                            Bundle outBundle = new Bundle();
                            if (tv_btn_order.getText().toString().equals("导航")) {
                                receiveSOSOrder();
                            } else if (tv_btn_order.getText().toString().equals("已结束") ||
                                    tv_btn_order.getText().toString().equals("已完成") ){
                                outBundle.putBoolean("isfinished", true);
                            }
                            outBundle.putString("id", info.id);
                            outBundle.putInt("type",1);
                            startActivity(SOSNavigationActivity.class, outBundle);
                        }
                    });

                }
        }
    }

    /**
     * SOS-接单者接单
     */
    public void receiveSOSOrder() {

        if(TextUtils.isEmpty(UserInfo.getSessionID(this))){
            ToastUtil.shortshow(this,"请登录");
            return;
        }
        AsyncHttpResponseHandler res = new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                String str_json = new String(bytes);

                try {
                    JSONObject obj = new JSONObject(str_json);
                    if (0 != obj.getInt("code")) {
                        if (3101 == obj.getInt("code")) {
                            ToastUtil.shortshow(getApplicationContext(), R.string.toast_err_receive_sos_order_repeat);
//                            isStop = true;
//                            SOSNavigationActivity.this.finish();
                            return;
                        }
                        if (2022 == obj.getInt("code")) {
                            ToastUtil.shortshow(getApplicationContext(), R.string.toast_err_receive_sos_order_repeat1);
//                            isStop = true;
//                            SOSNavigationActivity.this.finish();
                            return;
                        }
                        errorCode(obj.getInt("code"));
                        Log.i(TAG, "receiveSOSOrder 数据解析错误:code=" + obj.toString());
                        ToastUtil.shortshow(getApplicationContext(), R.string.toast_receivesos_failer);
                        return;
                    }
                    ToastUtil.shortshow(getApplicationContext(), R.string.toast_receivesos_success);
                } catch (JSONException e) {
                    LogUtil.e(getApplicationContext(), "enter catch=" + e.toString());
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                LogUtil.e(getApplicationContext(), "网络连接异常");
                ToastUtil.shortshow(getApplicationContext(), R.string.toast_error_net);
            }
        };
        RequestParams params = new RequestParams();
        params.put("session_id", UserInfo.getSessionID(this));
        params.put("id", bundle.getString("id"));
        params.put("latitude", mLocation.getLatitude());
        params.put("longitude", mLocation.getLongitude());
        params.put("location", mLocation.getAddrStr());
        AndroidAsyncHttp.post(ServerAPIConfig.Do_SOSReceive, params, res);
    }


    /**
     * 接单者接SOS单的时候检查其是否具有接单的条件,具有返回true,反之false
     * @return
     */
    private boolean checkSOSReceiveUserInfo() {
        //检查用户是否登录
        if (!UserInfo.getIsLogin(this)) {
            ToastUtil.shortshow(getApplicationContext(), R.string.toast_non_login);
            return false;
        }
        //检查是否绑定手机号
        if (null == UserInfo.getMobile(this) || 11 != UserInfo.getMobile(this).toString().length()) {
            ToastUtil.shortshow(getApplicationContext(), R.string.tv_tip_phone);
            return false;
        }
        return true;
    }

}
