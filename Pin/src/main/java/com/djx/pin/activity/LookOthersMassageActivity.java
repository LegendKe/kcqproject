package com.djx.pin.activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.djx.pin.R;
import com.djx.pin.base.OldBaseActivity;
import com.djx.pin.beans.OtherPersionDataInfo;
import com.djx.pin.beans.ServiceEntity;
import com.djx.pin.beans.StaticBean;
import com.djx.pin.myview.CircleImageView;
import com.djx.pin.personal.adapter.ServiceAdapter;
import com.djx.pin.serverapiconfig.ServerAPIConfig;
import com.djx.pin.utils.AndroidAsyncHttp;
import com.djx.pin.utils.QiniuUtils;
import com.djx.pin.utils.ToastUtil;
import com.djx.pin.utils.myutils.MapUtils;
import com.djx.pin.utils.myutils.ScreenTools;
import com.djx.pin.widget.adapters.MyRecyclerView;
import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;

import cz.msebera.android.httpclient.Header;
import io.rong.imkit.RongIM;

/**
 * Created by Administrator on 2016/7/1 0001.
 */
public class LookOthersMassageActivity extends OldBaseActivity implements View.OnClickListener {

    private LinearLayout ll_Back_LOMA, ll_Chat_LOMA;
    //    Credibility 可信度
    private TextView tv_UserName_LOMA, tv_identify, tv_distance, tv_Sex_LOMA, tv_Birthday_LOMA, tv_NativePlace_LOMA;
    private ImageView img_Sex_LOMA;
    private CircleImageView cimg_Avatar_LOMA;
    String user_id;
    private String nickname;
    private MyRecyclerView recyclerView;
    private String session_id;
    private ServiceAdapter adapter;
    private TextView tv_zhima_value;
    private Bundle bundle;
    private TextView tv_wish;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lookothersmassage);

        initView();

        bundle = getIntent().getExtras();
        if (bundle != null) {
            user_id = bundle.getString("user_id");
            initData(user_id);
            userBaseData(user_id);
        }
        initEvent();
    }

    //初始化适配器数据
    private void initData(String user_id) {
        session_id = getSharedPreferences(StaticBean.USER_INFO, Context.MODE_PRIVATE).getString("session_id", null);
        String url = ServerAPIConfig.SKILL_LIST;
        RequestParams params = new RequestParams();
        params.put("session_id",session_id);
        params.put("user_id",user_id);

        AsyncHttpResponseHandler res = new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                String str = new String(bytes);
                try {
                    JSONObject obj = new JSONObject(str);
                    if (obj.getInt("code") == 0) {
                        Gson gson = new Gson();
                        ServiceEntity serviceEntity = gson.fromJson(obj.getJSONObject("result").toString(), ServiceEntity.class);
                        adapter.addAll(serviceEntity.list);
                    } else {
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                ToastUtil.shortshow(LookOthersMassageActivity.this, R.string.toast_error_net);
            }
        };
        AndroidAsyncHttp.post(url, params, res);

    }


    private void userBaseData(String user_id) {
        /**
         * 获取用户信息
         *
         * @param session_id 用户的session_id
         * @param user_id    用户的user_id
         */
        String url = ServerAPIConfig.Get_UserInfo + "&user_id=" + user_id;
        AsyncHttpResponseHandler res = new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                String str = new String(bytes);

                Gson gson = new Gson();
                OtherPersionDataInfo info = gson.fromJson(str, OtherPersionDataInfo.class);
                OtherPersionDataInfo.Result result = info.result;
                int code = info.code;
                Log.e("obj===", info.toString());
                if (code == 0) {
                    nickname = result.nickname;
                    tv_UserName_LOMA.setText(nickname);
                    tv_Birthday_LOMA.setText(result.birthday);
                    tv_NativePlace_LOMA.setText(result.province+result.city+result.district);
                    if(result.wish != null){
                        tv_wish.setText(result.wish);
                    }
                    if (result.gender==0){
                        tv_Sex_LOMA.setText("未知");
                        img_Sex_LOMA.setVisibility(View.INVISIBLE);
                    }else if (result.gender==1){
                        tv_Sex_LOMA.setText("男");
                        img_Sex_LOMA.setImageResource(R.mipmap.ic_sexman);
                    }else if (result.gender==2){
                        tv_Sex_LOMA.setText("女");
                        img_Sex_LOMA.setImageResource(R.mipmap.ic_sexwomen);
                    }
                    int screenWidth = ScreenTools.instance(getApplicationContext()).getScreenWidth();
                    double per = screenWidth/1080.0;
                    if(result.is_auth == 1){//已认证
                        tv_identify.setText("已认证");
                        tv_identify.setTextColor(getResources().getColor(R.color.blue));
                        tv_identify.setBackground(getResources().getDrawable(R.drawable.shap_solid_white));
                        Drawable drawable1 = getResources().getDrawable(R.mipmap.ic_identify_blue);
                        drawable1.setBounds(0, 0, (int)(28*per), (int)(28*per));//第一0是距左边距离，第二0是距上边距离，28   28分别是长宽
                        tv_identify.setCompoundDrawables(drawable1, null, null, null);//只放左边
                    }
                    if(!TextUtils.isEmpty(result.zhima_open_id) || result.credit > 0){
                        tv_zhima_value.setText(result.credit+"");
                        tv_zhima_value.setTextColor(getResources().getColor(R.color.blue));
                        tv_zhima_value.setBackground(getResources().getDrawable(R.drawable.shap_solid_white));
                        Drawable drawable1 = getResources().getDrawable(R.mipmap.ic_zhima_blue);
                        drawable1.setBounds(0, 0, (int)(28*per), (int)(35*per));//第一0是距左边距离，第二0是距上边距离，28   35分别是长宽
                        tv_zhima_value.setCompoundDrawables(drawable1, null, null, null);//只放左边
                    }
                    double longitude = result.longitude;
                    double latitude = result.latitude;
                    SharedPreferences sharedPreferences = getSharedPreferences(StaticBean.USER_INFO, Context.MODE_PRIVATE);
                    String latitude1 = sharedPreferences.getString("latitude", null);
                    String longitude1 = sharedPreferences.getString("longitude", null);
                    if(latitude == 0 || longitude == 0 || TextUtils.isEmpty(latitude1)  || TextUtils.isEmpty(longitude1) || latitude1.length() ==1 || longitude1.length() ==1){
                        tv_distance.setText("隐 藏");
                        tv_distance.setBackground(getResources().getDrawable(R.drawable.shap_hollow));
                    }else {
                        Double latitude2 = Double.valueOf(sharedPreferences.getString("latitude", null));
                        Double longitude2 = Double.valueOf(sharedPreferences.getString("longitude", null));

                        double distance = MapUtils.getDistance(latitude, longitude, latitude2, longitude2);

                        DecimalFormat df2 ;
                        double dic = distance / 1000;
                        if(dic < 1){
                            df2 = new DecimalFormat("0.0");
                        }else if(dic>10){
                            df2 = new DecimalFormat("##");
                        }
                        else {
                            df2 = new DecimalFormat("##.0");
                        }
                        String format = df2.format(dic);
                        tv_distance.setText(format +"km");
                        tv_distance.setTextColor(getResources().getColor(R.color.blue));
                        tv_distance.setBackground(getResources().getDrawable(R.drawable.shap_solid_white));
                        Drawable drawable1 = getResources().getDrawable(R.mipmap.ic_distance_blue);
                        drawable1.setBounds(0, 0, (int)(20*per), (int)(30*per));//第一0是距左边距离，第二0是距上边距离，40分别是长宽
                        tv_distance.setCompoundDrawables(drawable1, null, null, null);//只放左边
                    }
                    QiniuUtils.setAvatarByIdFrom7Niu(LookOthersMassageActivity.this,cimg_Avatar_LOMA,result.portrait);
                } else {
                    errorCode(code);
                }
            }
            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {

            }
        };
        AndroidAsyncHttp.get(url, res);


    }

    private void initEvent() {
        ll_Back_LOMA.setOnClickListener(this);
        ll_Chat_LOMA.setOnClickListener(this);
    }

    private void initView() {
        ll_Back_LOMA = (LinearLayout) findViewById(R.id.ll_Back_LOMA);
        ll_Chat_LOMA = (LinearLayout) findViewById(R.id.ll_Chat_LOMA);
        recyclerView = ((MyRecyclerView) findViewById(R.id.recyclerView));
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ServiceAdapter(this);
        adapter.setWriteHide(true);
        recyclerView.setAdapter(adapter);
        View headview = View.inflate(this, R.layout.headview_userinfo,null);
        RecyclerView.LayoutParams layoutParams = new MyRecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        headview.setLayoutParams(layoutParams);

        tv_UserName_LOMA = (TextView) headview.findViewById(R.id.tv_UserName_LOMA);
        tv_distance = (TextView) headview.findViewById(R.id.tv_distance);
        tv_identify = (TextView) headview.findViewById(R.id.tv_identify);
        tv_zhima_value = (TextView) headview.findViewById(R.id.tv_zhima_value);

        tv_Sex_LOMA = (TextView) headview.findViewById(R.id.tv_Sex_LOMA);
        tv_Birthday_LOMA = (TextView) headview.findViewById(R.id.tv_Birthday_LOMA);
        tv_NativePlace_LOMA = (TextView) headview.findViewById(R.id.tv_NativePlace_LOMA);
        img_Sex_LOMA = (ImageView) headview.findViewById(R.id.img_Sex_LOMA);
        cimg_Avatar_LOMA = (CircleImageView) headview.findViewById(R.id.cimg_Avatar_LOMA);
        tv_wish = ((TextView) headview.findViewById(R.id.tv_wish));

        recyclerView.addHeaderView(headview);

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_Back_LOMA:
                this.finish();
                break;
            case R.id.ll_Chat_LOMA:
                //检查用户是否登录,未登录则return;已经实名认证的用户才可以举报
                if (getSharedPreferences(StaticBean.USER_INFO, MODE_PRIVATE).getBoolean("isLogined", false) == false) {
                    ToastUtil.shortshow(this, R.string.toast_non_login);
                    return;
                }
                //检查是否是同一用户
                if (user_id.equals(getUser_id())) {
                    ToastUtil.shortshow(this, R.string.toast_error_talk);
                    return;
                }
                //启动会话界面
                if (RongIM.getInstance() != null)
                    startRecordByPermissions();
                break;
        }
    }

    @Override
    public void excuteActionContainRecordPermision() {
        if(user_id != null){
            RongIM.getInstance().startPrivateChat(this, user_id, nickname);
        }
    }
}
