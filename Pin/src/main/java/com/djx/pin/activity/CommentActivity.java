package com.djx.pin.activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.djx.pin.R;
import com.djx.pin.base.OldBaseActivity;
import com.djx.pin.beans.StaticBean;
import com.djx.pin.myview.CircleImageView;
import com.djx.pin.serverapiconfig.ServerAPIConfig;
import com.djx.pin.utils.AndroidAsyncHttp;
import com.djx.pin.utils.DateUtils;
import com.djx.pin.utils.QiniuUtils;
import com.djx.pin.utils.ToastUtil;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.Date;

import cz.msebera.android.httpclient.Header;

public class CommentActivity extends OldBaseActivity implements View.OnClickListener, RadioGroup.OnCheckedChangeListener {


    CircleImageView cimg_Avatar;
    TextView tv_NickName,
            tv_Position,
            tv_Time,
            tv_price;
    Button bt_confirm;
    SharedPreferences sp;
    Context CONTEXT = CommentActivity.this;
    Bundle bundle;//接收上个界面传来的信息
    RadioGroup rg;
    RadioButton rb_0, rb_1, rb_2;
    int comment_grade = 1;//评论等级,默认等于1
    private LinearLayout ll_back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);
        initView();
        initEvent();
        initData();
    }
    /**
     * 初始化
     * 加载用户基本信息
     */
    private void initData() {
        QiniuUtils.setAvatarByIdFrom7Niu(this,cimg_Avatar,bundle.getString("portrait"));
        tv_NickName.setText(bundle.getString("nickname", ""));
        tv_Position.setText(bundle.getString("province", "") + bundle.getString("city", "") + bundle.getString("district", ""));
        tv_Time.setText(DateUtils.formatDate(new Date(bundle.getLong("complete_time", 0)), DateUtils.yyyyMMDD));
        tv_price.setText(bundle.getDouble("price", 0) + "");
    }

    private void initEvent() {
        bt_confirm.setOnClickListener(this);
        rg.setOnCheckedChangeListener(this);
        ll_back.setOnClickListener(this);
    }

    private void initView() {
        sp = getSharedPreferences(StaticBean.USER_INFO, MODE_PRIVATE);
        bundle = getIntent().getExtras();
        cimg_Avatar = (CircleImageView) findViewById(R.id.cimg_Avatar);
        tv_NickName = (TextView) findViewById(R.id.tv_NickName);
        tv_Position = (TextView) findViewById(R.id.tv_Position);
        tv_Time = (TextView) findViewById(R.id.tv_Time);
        tv_price = (TextView) findViewById(R.id.tv_price);
        tv_NickName = (TextView) findViewById(R.id.tv_NickName);
        bt_confirm = (Button) findViewById(R.id.bt_confirm);
        ll_back = ((LinearLayout) findViewById(R.id.ll_Back_MHDA));

        rg = (RadioGroup) findViewById(R.id.rg);
        rb_0 = (RadioButton) findViewById(R.id.rb_0);
        rb_1 = (RadioButton) findViewById(R.id.rb_1);
        rb_2 = (RadioButton) findViewById(R.id.rb_2);
        rb_0.setChecked(true);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_confirm:
                updataCommentInfo();
                break;
            case R.id.ll_Back_MHDA:
                finish();
                break;
        }

    }

    /**
     * 上传评论信息
     */
    public void updataCommentInfo() {

        AsyncHttpResponseHandler res = new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                String str_json = new String(bytes);
                try {
                    JSONObject obj = new JSONObject(str_json);
                    if (0 != obj.getInt("code")) {
                        errorCode(obj.getInt("code"));

                        return;
                    }
                    ToastUtil.shortshow(CONTEXT, R.string.toast_distributecomment_success);
                    startActivity(MainActivity.class);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                ToastUtil.shortshow(CONTEXT, R.string.toast_error_net);
            }
        };
        RequestParams params = new RequestParams();
        params.put("session_id", sp.getString("session_id", null));
        params.put("id", bundle.getString("id"));
        params.put("receiver_id", bundle.getString("receiver_id"));
        params.put("grade", comment_grade);
        AndroidAsyncHttp.post(ServerAPIConfig.Do_DistributeComment, params, res);
    }
    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (checkedId) {
            case R.id.rb_0:
                comment_grade = 1;
                break;
            case R.id.rb_1:
                comment_grade = 2;
                break;
            case R.id.rb_2:
                comment_grade = 3;
                break;
        }
    }
}
