package com.djx.pin.activity;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
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
import com.djx.pin.utils.LogUtil;
import com.djx.pin.utils.ToastUtil;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.text.DecimalFormat;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;
import de.greenrobot.event.EventBus;

/**
 * Created by Administrator on 2016/8/13 0013.
 */
public class PurseRechargeActivity extends OldBaseActivity implements View.OnClickListener {
    protected final static String TAG = PurseRechargeActivity.class.getSimpleName();

    LinearLayout ll_Back_MHDA;
    EditText et_RechargeNumber, edt_Code;
    TextView tv_SendCode, tv_phoneNumber, tv_Title;
    Button bt_confirm;
    String mobile;
    //充值类型type,1:余额充值,2:意外保险信用金充值
    int rechargeType;
    private static final int DECIMAL_DIGITS = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_purserecharge);
        mobile = getSharedPreferences(StaticBean.USER_INFO, Context.MODE_PRIVATE).getString("mobile", null);
        initView();
        initEvent();
        rechargeType = getIntent().getExtras().getInt("type");
        switch (rechargeType) {
            case 1:
                tv_Title.setText("余额充值");
                break;
            case 2:
                tv_Title.setText("意外保险信用金充值");
                break;
        }

    }

    private void initEvent() {
        ll_Back_MHDA.setOnClickListener(this);
        bt_confirm.setOnClickListener(this);

        et_RechargeNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().contains(".")) {
                    if (s.length() - 1 - s.toString().indexOf(".") > DECIMAL_DIGITS) {
                        s = s.toString().subSequence(0,
                                s.toString().indexOf(".") + DECIMAL_DIGITS + 1);
                        et_RechargeNumber.setText(s);
                        et_RechargeNumber.setSelection(s.length());
                    }
                }
                if (s.toString().trim().substring(0).equals(".")) {
                    s = "0" + s;
                    et_RechargeNumber.setText(s);
                    et_RechargeNumber.setSelection(2);
                }
                if (s.toString().startsWith("0")
                        && s.toString().trim().length() > 1) {
                    if (!s.toString().substring(1, 2).equals(".")) {
                        et_RechargeNumber.setText(s.subSequence(0, 1));
                        et_RechargeNumber.setSelection(1);
                        return;
                    }
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    private void initView() {
        ll_Back_MHDA = (LinearLayout) findViewById(R.id.ll_Back_MHDA);
        et_RechargeNumber = (EditText) findViewById(R.id.et_RechargeNumber);
        tv_Title = (TextView) findViewById(R.id.tv_Title);
        bt_confirm = (Button) findViewById(R.id.bt_confirm);
    }

    /**
     * Called when a view has been clicked.
     *
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_Back_MHDA:
                this.finish();
                break;
            case R.id.bt_confirm://充值
                if (et_RechargeNumber.getText().toString() == null || et_RechargeNumber.getText().toString().equals("")) {
                    ToastUtil.shortshow(getApplicationContext(), "请输入充值金额");
                }
                else {
                    payBalance(rechargeType);
                }
                break;
        }
    }



    /**
     * 支付宝支付接口
     * {
     * "session_id": "4085c9d4f420864c407e",
     * "id": "adas8d09as8d0adad",
     * "type": 1,
     *
     * "country_code": "0086",
     * "mobile": "13112345678",
     * "sms_code": "231512"
     * }
     *
     *
     */
 /*   "session_id": "8527a2b6a58b4fa963fd",
            "amount": 100,
            "type": 1,
*/
    String order_id;
    double amount;
    String seller_id;

    private void payBalance(int rechargeType) {
        String url = ServerAPIConfig.PurseRecharge;
        amount = Double.parseDouble(et_RechargeNumber.getText().toString());
        Log.i(TAG, "amount is " + amount);
        if (amount < 0) {
            et_RechargeNumber.setError("输入有误");
        }
        RequestParams params = new RequestParams();
        params.put("session_id", getSession_id());
        params.put("amount", amount);
        params.put("type", rechargeType);

        Log.e("1url===", url + params.toString());
        AsyncHttpResponseHandler res = new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                String str = new String(bytes);
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
     */
    private void ZhiFuBaoPay(final String orderid, final double amount, final String sellerid) {
        String partner = "2088421507381223";
        String service = "mobile.securitypay.pay";
        String _input_charset = "utf-8";
        String notify_url = ServerAPIConfig.ZhiFuBaoHuiDiao;
        String out_trade_no = orderid;

        String subject = toURLEncoded("充值") + orderid;

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
            content.put("total_fee", total_fee);//金额

            obj.put("content", content);
            StringEntity entity = new StringEntity(obj.toString(), "utf-8");
            Log.e("2url===", url + obj.toString());

            AsyncHttpResponseHandler res = new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int i, Header[] headers, byte[] bytes) {
                    String str = new String(bytes);
                    try {
                        JSONObject object = new JSONObject(str);
                        if (object.getInt("code") == 0) {
                            object = object.getJSONObject("result");
                            String sign = object.getString("sign");
                            Log.i(TAG, "get alipay sign");
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
     */
    private void RequestZhiFubao(String sign, String orderid, double amount, String sellerid) {
        final PayTask payTask = new PayTask(PurseRechargeActivity.this);

        String partner = "\"2088421507381223\"";
        String service = "\"mobile.securitypay.pay\"";
        String _input_charset = "\"utf-8\"";
        String sign_type = "\"RSA\"";
        String notify_url = "\"" + ServerAPIConfig.ZhiFuBaoHuiDiao + "\"";
        String out_trade_no = "\"" + orderid + "\"";
        String subject = "\"" + toURLEncoded("充值") + orderid + "\"";
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
        Log.i(TAG, "orderInfo==" + orderInfo);

        payTask.getVersion();
        Log.i(TAG, "version===" + payTask.getVersion());
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
                        Log.i(TAG, "支付成功");
                        EventBus.getDefault().post("update_balance");
                        ToastUtil.longshow(PurseRechargeActivity.this, "支付成功");
                        finish();

                        Log.i(TAG, "result===" + msg.obj.toString());
                    } else {
                        // 判断resultStatus 为非“9000”则代表可能支付失败
                        // “8000”代表支付结果因为支付渠道原因或者系统原因还在等待支付结果确认，最终交易是否成功以服务端异步通知为准（小概率状态）
                        if (TextUtils.equals(resultStatus, "8000")) {
                            Log.i(TAG, "支付结果确认中");
                            Toast.makeText(PurseRechargeActivity.this, "支付结果确认中",
                                    Toast.LENGTH_SHORT).show();

                        } else {
                            // 其他值就可以判断为支付失败，包括用户主动取消支付，或者系统返回的错误
                            Log.i(TAG, "支付失败");
                            Toast.makeText(PurseRechargeActivity.this, "支付失败",
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
