package com.djx.pin.activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.djx.pin.R;
import com.djx.pin.base.OldBaseActivity;
import com.djx.pin.beans.StaticBean;
import com.djx.pin.myview.CircleImageView;
import com.djx.pin.serverapiconfig.ServerAPIConfig;
import com.djx.pin.utils.AndroidAsyncHttp;
import com.djx.pin.utils.DateUtils;
import com.djx.pin.utils.LogUtil;
import com.djx.pin.utils.MassageCode;
import com.djx.pin.utils.QiniuUtils;
import com.djx.pin.utils.ToastUtil;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

import cz.msebera.android.httpclient.Header;

public class PayConfirmActivity extends OldBaseActivity implements View.OnClickListener {

    CircleImageView cimg_Avatar;
    TextView tv_NickName,
            tv_Position,
            tv_Time,
            tv_price,
            tv_phoneNumber,
            tv_SendCode;
    Button bt_confirm;
    SharedPreferences sp;
    Context CONTEXT = PayConfirmActivity.this;
    Bundle bundle;//接收上个界面传来的信息
    EditText edt_Code;
    private LinearLayout ll_back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay_confirm);
        initView();
        initEvent();
        initData();


    }

    /**
     * 加载用户信息
     */
    private void initData() {
        QiniuUtils.setAvatarByIdFrom7Niu(this,cimg_Avatar,bundle.getString("portrait"));
        tv_NickName.setText(bundle.getString("nickname", ""));
        tv_Position.setText(bundle.getString("province", "") + bundle.getString("city", "") + bundle.getString("district", ""));
        tv_Time.setText(DateUtils.formatDate(new Date(bundle.getLong("complete_time", 0)), DateUtils.yyyyMMDD));
        tv_price.setText(bundle.getDouble("price", 0) + "");
        tv_phoneNumber.setText(sp.getString("mobile",null));
    }

    private void initView() {
        sp = getSharedPreferences(StaticBean.USER_INFO, MODE_PRIVATE);
        bundle = getIntent().getExtras();
        cimg_Avatar = (CircleImageView) findViewById(R.id.cimg_Avatar);
        tv_NickName = (TextView) findViewById(R.id.tv_NickName);
        tv_Position = (TextView) findViewById(R.id.tv_Position);
        tv_Time = (TextView) findViewById(R.id.tv_Time);
        tv_price = (TextView) findViewById(R.id.tv_price);
        tv_SendCode = (TextView) findViewById(R.id.tv_SendCode);
        tv_NickName = (TextView) findViewById(R.id.tv_NickName);
        tv_phoneNumber = (TextView) findViewById(R.id.tv_phoneNumber);
        bt_confirm = (Button) findViewById(R.id.bt_confirm);
        edt_Code = (EditText) findViewById(R.id.edt_Code);
        ll_back = ((LinearLayout) findViewById(R.id.ll_Back_MHDA));
    }

    private void initEvent() {
        tv_SendCode.setOnClickListener(this);
        bt_confirm.setOnClickListener(this);
        ll_back.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            /**发送短信验证码*/
            case R.id.tv_SendCode:
                if (null == tv_SendCode.getText() || tv_SendCode.getText().toString().length() == 0) {
                    LogUtil.e(CONTEXT, "手机号为空");
                    ToastUtil.shortshow(CONTEXT, R.string.toast_non_phone);
                    return;
                }
                MassageCode.RequestMassageCode(sp.getString("mobile", null), tv_SendCode, this);
                break;
            /**确定按钮*/
            case R.id.bt_confirm:
                if (null == edt_Code.getText() || edt_Code.getText().toString().length() != 6) {
                    ToastUtil.shortshow(CONTEXT, R.string.toast_sms_length_error);
                    return;
                }
                verifySmsCode(edt_Code.getText().toString());
                break;
            case R.id.ll_Back_MHDA:
                finish();
                break;
        }

    }

    /**
     * 发单者确认完成
     *
     * @param receiver_id 接单者id
     */
    private void updataConfirmOrder(String receiver_id) {
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
                    ToastUtil.shortshow(CONTEXT, R.string.toast_confirmorder_success);
                    startActivity(CommentActivity.class, bundle);
                } catch (JSONException e) {
                    LogUtil.e(CONTEXT, "updataSignOrder enter catch");
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
        params.put("id", bundle.getString("id", null));
        params.put("receiver_id", receiver_id);

        LogUtil.e("params detail="+params.toString());

        AndroidAsyncHttp.post(ServerAPIConfig.Do_ConfirmOrder, params, res);
    }

    /**
     * 验证验证码是否正确
     */
    private void verifySmsCode(String smscode) {
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
                    updataConfirmOrder(bundle.getString("receiver_id"));
                } catch (JSONException e) {
                    LogUtil.e(CONTEXT, "verifySmsCode enter catch");
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
        params.put("country_code", "0086");
        params.put("mobile", sp.getString("mobile", null));
        params.put("sms_code", smscode);

        AndroidAsyncHttp.post(ServerAPIConfig.VerifyMassageCode, params, res);


    }
}
