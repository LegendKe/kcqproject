package com.djx.pin.activity;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alipay.sdk.app.PayTask;
import com.djx.pin.R;
import com.djx.pin.alipay.PayResult;
import com.djx.pin.base.OldBaseActivity;
import com.djx.pin.beans.StaticBean;
import com.djx.pin.beans.UserInfo;
import com.djx.pin.serverapiconfig.ServerAPIConfig;
import com.djx.pin.utils.AndroidAsyncHttp;
import com.djx.pin.utils.LogUtil;
import com.djx.pin.utils.ToastUtil;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;

public class PayTypeActivity extends OldBaseActivity implements View.OnClickListener {

    SharedPreferences sp;
    RelativeLayout rl_balancepay, rl_alipay;
    CheckBox cb_balance, cb_alipay;
    Button bt_confirm_pay;
    TextView tv_balance,tv_price;
    Context CONTEXT = PayTypeActivity.this;
    LinearLayout ll_back;
    Bundle bundle;


    static Activity instance;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay);
        initView();
        initEvent();
        getData();
        instance=this;

    }

    private void initView() {
        sp = getSharedPreferences(StaticBean.USER_INFO, MODE_PRIVATE);
        bundle = getIntent().getExtras();
        rl_balancepay = (RelativeLayout) findViewById(R.id.rl_balancepay);
        rl_alipay = (RelativeLayout) findViewById(R.id.rl_alipay);
        cb_balance = (CheckBox) findViewById(R.id.cb_balance);
        cb_alipay = (CheckBox) findViewById(R.id.cb_alipay);
        bt_confirm_pay = (Button) findViewById(R.id.bt_confirm_pay);
        tv_balance = (TextView) findViewById(R.id.tv_balance);
        ll_back = (LinearLayout) findViewById(R.id.ll_back);
        tv_price= (TextView) findViewById(R.id.tv_price);
        tv_price.setText(bundle.getFloat("amount")+"");


    }

    private void initEvent() {
        cb_balance.setChecked(true);
        cb_alipay.setClickable(false);
        cb_balance.setClickable(false);
        bt_confirm_pay.setOnClickListener(this);
        rl_balancepay.setOnClickListener(this);
        rl_alipay.setOnClickListener(this);
        ll_back.setOnClickListener(this);

    }

    boolean is_balance = true;
    boolean is_alipay = false;

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            //点击余额支付
            case R.id.rl_balancepay:
                is_alipay = false;
                if (is_balance) {
                    is_balance = false;
                    cb_balance.setChecked(false);
                    cb_alipay.setChecked(false);
                } else {
                    is_balance = true;
                    cb_balance.setChecked(true);
                    cb_alipay.setChecked(false);
                }

                break;
            //点击支付宝支付
            case R.id.rl_alipay:
                is_balance = false;
                if (is_alipay) {
                    is_alipay = false;
                    cb_alipay.setChecked(false);
                    cb_balance.setChecked(false);
                } else {
                    is_alipay = true;
                    cb_balance.setChecked(false);
                    cb_alipay.setChecked(true);
                }
                break;
            //点击确认支付
            case R.id.bt_confirm_pay:
                onPay();
                break;
            case R.id.ll_back:
                this.finish();
                break;
        }

    }

    /**
     * 开始支付
     * 如果是余额支付,调用余额支付
     * 如果是支付宝支付,调用支付宝支付
     */
    public void onPay() {
        //检查用户是否登录,未登录则return;已经实名认证的用户才可以举报
        if (sp.getBoolean("isLogined", false) == false) {
            ToastUtil.shortshow(this, R.string.toast_non_login);
            return;
        }
        //余额支付
        if (cb_balance.isChecked()) {
            startActivity(PayVerifyActivity.class, bundle);
            finish();
        }
        //支付宝支付
        if (cb_alipay.isChecked()) {
            payBalance();
        }
    }



    /**
     * 获取用户余额
     */
    public void getData() {
        UserInfo.getUserInfo(getApplicationContext(), sp.getString("session_id", null), sp.getString("user_id", null), new UserInfo.GetUserInfoCallBack() {
            @Override
            public void callback(boolean state) {
                if (state) {
                    tv_balance.setText("目前余额" + UserInfo.getBalance(getApplicationContext()) + "元");
                } else {
                    ToastUtil.shortshow(CONTEXT, R.string.toast_failer_balance);
                }
            }
        });
    }





    /**
     * 支付宝支付接口
     * {
     * "session_id": "4085c9d4f420864c407e",
     * "id": "adas8d09as8d0adad",
     * "type": 1,
     *
     * }
     */

    String order_id;
    Double amount;
    String seller_id;
    //悬赏类型
    int type;

    private void payBalance() {
        String id = bundle.getString("id");
        type = bundle.getInt("type");
        String url = ServerAPIConfig.ZhiFuBaoPay;

        RequestParams params = new RequestParams();

        params.put("session_id", getSession_id());
        params.put("id", id);
        params.put("type", type);

        AsyncHttpResponseHandler res = new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                String str = new String(bytes);
                Log.e("str===", str);
                try {
                    JSONObject obj = new JSONObject(str);
                    if (obj.getInt("code") == 0) {

                        obj = obj.getJSONObject("result");
                        order_id = obj.getString("order_id");
                        amount = obj.getDouble("amount");
                        seller_id = obj.getString("seller_id");
                        ZhiFuBaoPay(order_id, amount, seller_id);
                    } else {
                        errorCode(obj.getInt("code"));
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
     * 支付信息
     * 订单;金额;收款
     * */
    private void ZhiFuBaoPay(final String orderid, final Double amount, final String sellerid) {


        String partner = "2088421507381223";
        String service = "mobile.securitypay.pay";
        String _input_charset = "utf-8";
        String notify_url = ServerAPIConfig.ZhiFuBaoHuiDiao;
        String out_trade_no = orderid;
        String subject = null;
        switch (type) {
            case 1:
                subject = toURLEncoded("求助") + orderid;
                break;
            case 7:
                subject = toURLEncoded("网络悬赏") + orderid;
                break;
        }
        String payment_type = "1";
        String seller_id = sellerid;
        String body = "texttexttext";
        double total_fee = amount;


        String url = ServerAPIConfig.ZhiFuBaoQianMing;
        JSONObject obj = new JSONObject();
        JSONObject content = new JSONObject();
        try {
            obj.put("session_id", getSession_id());
            obj.put("sort", 1);
            content.put("partner", partner);
            content.put("service", service);
            content.put("_input_charset", _input_charset);
            content.put("notify_url", notify_url);
            content.put("out_trade_no", out_trade_no);
            content.put("subject", subject);
            content.put("payment_type", payment_type);
            content.put("seller_id", seller_id);
            content.put("body", body);
            content.put("total_fee", total_fee);

            obj.put("content", content);
            StringEntity entity = new StringEntity(obj.toString(), "utf-8");


            AsyncHttpResponseHandler res = new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int i, Header[] headers, byte[] bytes) {
                    String str = new String(bytes);
                    try {
                        JSONObject object = new JSONObject(str);
                        if (object.getInt("code") == 0) {
                            object = object.getJSONObject("result");
                            String sign = object.getString("sign");
                            RequestZhiFubao(sign, orderid, amount, sellerid);
                        } else {
                            errorCode(object.getInt("code"));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    Log.e("str===", str);
                }

                @Override
                public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {

                }
            };
            AndroidAsyncHttp.post(this, url, entity, "application/json;charset=UTF-8", res);
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * 获取签名
     * */
    private void RequestZhiFubao(String sign, String orderid, Double amount, String sellerid) {
        final PayTask payTask = new PayTask(PayTypeActivity.this);

        String partner = "\"2088421507381223\"";
        String service = "\"mobile.securitypay.pay\"";
        String _input_charset = "\"utf-8\"";
        String sign_type = "\"RSA\"";
        String notify_url = "\"" + ServerAPIConfig.ZhiFuBaoHuiDiao + "\"";
        String out_trade_no = "\"" + orderid + "\"";

        String subject = null;
        switch (type) {
            case 1:
                subject = "\""+toURLEncoded("求助") + orderid+"\"";
                break;
            case 7:
                subject = "\""+ toURLEncoded("网络悬赏")+ orderid+"\"";
                break;
        }
        String payment_type = "\"1\"";
        String seller_id = "\"" + sellerid + "\"";
        String body = "\"texttexttext\"";
        DecimalFormat format = new DecimalFormat("0.##");
        String total_fee = "\"" + format.format(amount )+ "\"";

        final String orderInfo =
                "_input_charset=" + _input_charset +
                        "&body=" + body +
                        "&notify_url=" + notify_url +
                        "&out_trade_no=" + out_trade_no +
                        "&partner=" + partner +
                        "&payment_type=" + payment_type +
                        "&seller_id=" + seller_id +
                        "&service=" + service +
                        "&subject=" + subject +
                        "&total_fee=" + total_fee +
                        "&sign=\"" + sign + "\"" +
                        "&sign_type=" + sign_type;
        Log.e("orderInfo==", orderInfo);

        payTask.getVersion();
        Log.e("version===", payTask.getVersion());
        final Handler mHandler = new Handler() {
            /**
             * Subclasses must implement this to receive messages.
             *
             * @param msg
             */
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if (msg.what == 1) {
                    PayResult payResult = new PayResult((String) msg.obj);
                    Log.e("result===", msg.obj.toString());
                    // 支付宝返回此次支付结果及加签，建议对支付宝签名信息拿签约时支付宝提供的公钥做验签
                    String resultInfo = payResult.getResult();

                    String resultStatus = payResult.getResultStatus();

                    // 判断resultStatus 为“9000”则代表支付成功，具体状态码代表含义可参考接口文档
                    if (TextUtils.equals(resultStatus, "9000")) {

                        // 支付成功后想服务器返回支付成功状态码
                        LogUtil.e("支付成功");
                        Log.e("result===", msg.obj.toString());
                        startActivity(PayVerifySuccessActivity.class);
                        ToastUtil.shortshow(getApplicationContext(),"支付成功");
                        finish();
                    } else {
                        // 判断resultStatus 为非“9000”则代表可能支付失败
                        // “8000”代表支付结果因为支付渠道原因或者系统原因还在等待支付结果确认，最终交易是否成功以服务端异步通知为准（小概率状态）
                        if (TextUtils.equals(resultStatus, "8000")) {
                            LogUtil.e("支付结果确认中");
                            Toast.makeText(PayTypeActivity.this, "支付结果确认中",
                                    Toast.LENGTH_SHORT).show();

                        } else {
                            // 其他值就可以判断为支付失败，包括用户主动取消支付，或者系统返回的错误
                            LogUtil.e("支付失败");
                            Toast.makeText(PayTypeActivity.this, "支付失败",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }
        };
        new Thread(new Runnable() {
            @Override
            public void run() {
                String result = payTask.pay(orderInfo);
                Message message = new Message();
                message.what = 1;
                message.obj = result;
                mHandler.sendMessage(message);
            }
        }).start();
    }







}
