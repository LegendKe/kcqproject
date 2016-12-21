package com.djx.pin.activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alipay.sdk.app.PayTask;
import com.djx.pin.R;
import com.djx.pin.alipay.PayResult;
import com.djx.pin.base.OldBaseActivity;
import com.djx.pin.beans.StaticBean;
import com.djx.pin.serverapiconfig.ServerAPIConfig;
import com.djx.pin.utils.AndroidAsyncHttp;
import com.djx.pin.utils.MassageCode;
import com.djx.pin.utils.ToastUtil;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;

public class PayAlipayActivity extends OldBaseActivity implements View.OnClickListener {

    TextView tv_phoneNumber, tv_SendCode;
    EditText edt_Code;
    LinearLayout ll_back;
    SharedPreferences sp;
    Button bt_paybalance;

    Bundle bundle;
    String mobile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payverify);
        initView();
        initEvent();
        bundle = getIntent().getExtras();

    }

    private void initView() {
        sp = getSharedPreferences(StaticBean.USER_INFO, MODE_PRIVATE);
        tv_phoneNumber = (TextView) findViewById(R.id.tv_phoneNumber);
        tv_SendCode = (TextView) findViewById(R.id.tv_SendCode);
        edt_Code = (EditText) findViewById(R.id.edt_Code);
        ll_back = (LinearLayout) findViewById(R.id.ll_back);
        bt_paybalance = (Button) findViewById(R.id.bt_paybalance);


    }

    private void initEvent() {
        mobile = getSharedPreferences(StaticBean.USER_INFO, Context.MODE_PRIVATE).getString("mobile", null);
        tv_SendCode.setOnClickListener(this);
        bt_paybalance.setOnClickListener(this);
        ll_back.setOnClickListener(this);
        tv_phoneNumber.setText(mobile);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            //发送短信验证码
            case R.id.tv_SendCode:
                MassageCode.RequestMassageCode(mobile, tv_SendCode, this);
                break;
            //发起支付宝网络请求
            case R.id.bt_paybalance:
                payBalance();
                break;
            case R.id.ll_back:
                this.finish();
                break;
        }
    }


    /**
     * 支付宝支付接口
     * {
     * "session_id": "4085c9d4f420864c407e",
     * "id": "adas8d09as8d0adad",
     * "type": 1,
     * "country_code": "0086",
     * "mobile": "13112345678",
     * "sms_code": "231512"
     * }
     */

    String order_id;
    int amount;
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

       /* params.put("country_code", "0086");
        params.put("mobile", mobile);
        params.put("sms_code", edt_Code.getText().toString());*/

        AsyncHttpResponseHandler res = new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                String str = new String(bytes);
                try {
                    JSONObject obj = new JSONObject(str);
                    if (obj.getInt("code") == 0) {

                        obj = obj.getJSONObject("result");
                        order_id = obj.getString("order_id");
                        amount = obj.getInt("amount");
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
    private void ZhiFuBaoPay(final String orderid, final int amount, final String sellerid) {


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
    private void RequestZhiFubao(String sign, String orderid, int amount, String sellerid) {
        final PayTask payTask = new PayTask(PayAlipayActivity.this);

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
        String total_fee = "\"" + amount + "\"";

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
        payTask.getVersion();
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
                    // 支付宝返回此次支付结果及加签，建议对支付宝签名信息拿签约时支付宝提供的公钥做验签
                    String resultInfo = payResult.getResult();

                    String resultStatus = payResult.getResultStatus();

                    // 判断resultStatus 为“9000”则代表支付成功，具体状态码代表含义可参考接口文档
                    if (TextUtils.equals(resultStatus, "9000")) {

                        // 支付成功后想服务器返回支付成功状态码
                        ToastUtil.longshow(PayAlipayActivity.this, "支付成功");

                    } else {
                        // 判断resultStatus 为非“9000”则代表可能支付失败
                        // “8000”代表支付结果因为支付渠道原因或者系统原因还在等待支付结果确认，最终交易是否成功以服务端异步通知为准（小概率状态）
                        if (TextUtils.equals(resultStatus, "8000")) {
                            Toast.makeText(PayAlipayActivity.this, "支付结果确认中",
                                    Toast.LENGTH_SHORT).show();

                        } else {
                            // 其他值就可以判断为支付失败，包括用户主动取消支付，或者系统返回的错误
                            Toast.makeText(PayAlipayActivity.this, "支付失败",
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
