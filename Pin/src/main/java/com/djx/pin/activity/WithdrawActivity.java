package com.djx.pin.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.djx.pin.R;
import com.djx.pin.base.OldBaseActivity;
import com.djx.pin.beans.StaticBean;
import com.djx.pin.serverapiconfig.ServerAPIConfig;
import com.djx.pin.ui.CommonDialog;
import com.djx.pin.utils.AndroidAsyncHttp;
import com.djx.pin.utils.MassageCode;
import com.djx.pin.utils.ToastUtil;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.TimerTask;

import cz.msebera.android.httpclient.Header;

/**
 * Created by Administrator on 2016/6/30.
 */
public class WithdrawActivity extends OldBaseActivity implements View.OnClickListener {
    protected final static String TAG = WithdrawActivity.class.getSimpleName();

    private LinearLayout ll_Back_WA;
    private EditText edt_WithdrawMoney_WA,edt_PutWithdrawAccount_WA,edt_Code_WA;
    private TextView tv_AllMoney_WA,tv_PhoneNumber_WA,tv_PutZhiFuBaoName_WA;
    private Button bt_Complete_WA;
    private Float maxBalance;
    private TextView tv_withdraw_SMSCode;
    private Handler handler;
    private TimerTask timerTask;
    private int Second = 60;
    private static final int DECIMAL_DIGITS = 1;
    private SharedPreferences sp;
    private String mobile;
    private String real_name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.anctivity_withdraw);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        maxBalance = bundle.getFloat("balance");

        sp = getSharedPreferences(StaticBean.USER_INFO, MODE_PRIVATE);
        mobile = sp.getString("mobile",null);//用户手机号
        real_name = sp.getString("real_name", null);//用户真是姓名

        initView();
        initEvent();
    }

    private void initEvent() {
        ll_Back_WA.setOnClickListener(this);
        bt_Complete_WA.setOnClickListener(this);
        tv_withdraw_SMSCode.setOnClickListener(this);
        edt_WithdrawMoney_WA.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().contains(".")) {
                    if (s.length() - 1 - s.toString().indexOf(".") > DECIMAL_DIGITS) {
                        s = s.toString().subSequence(0,
                                s.toString().indexOf(".") + DECIMAL_DIGITS + 1);
                        edt_WithdrawMoney_WA.setText(s);
                        edt_WithdrawMoney_WA.setSelection(s.length());
                    }
                }
                if (s.toString().trim().substring(0).equals(".")) {
                    s = "0" + s;
                    edt_WithdrawMoney_WA.setText(s);
                    edt_WithdrawMoney_WA.setSelection(2);
                }
                if (s.toString().startsWith("0")
                        && s.toString().trim().length() > 1) {
                    if (!s.toString().substring(1, 2).equals(".")) {
                        edt_WithdrawMoney_WA.setText(s.subSequence(0, 1));
                        edt_WithdrawMoney_WA.setSelection(1);
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

        ll_Back_WA= (LinearLayout) findViewById(R.id.ll_Back_WA);
        edt_WithdrawMoney_WA= (EditText) findViewById(R.id.edt_WithdrawMoney_WA);
        edt_PutWithdrawAccount_WA= (EditText) findViewById(R.id.edt_PutWithdrawAccount_WA);

        tv_PhoneNumber_WA= (TextView) findViewById(R.id.tv_PhoneNumber_WA);
        tv_PutZhiFuBaoName_WA= (TextView) findViewById(R.id.tv_PutZhiFuBaoName_WA);
        tv_PhoneNumber_WA.setText(mobile);
        tv_PutZhiFuBaoName_WA.setText(real_name);


        edt_Code_WA= (EditText) findViewById(R.id.edt_Code_WA);
        tv_AllMoney_WA= (TextView) findViewById(R.id.tv_AllMoney_WA);
        bt_Complete_WA= (Button) findViewById(R.id.bt_Complete_WA);
        DecimalFormat format = new DecimalFormat("0.00");
        tv_AllMoney_WA.setText(format.format(maxBalance));

        tv_withdraw_SMSCode = (TextView) findViewById(R.id.tv_withdraw_SMSCode);

        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                tv_withdraw_SMSCode.setText("重新发送(" + (Second--) + ")");
                if (0 == Second) {
                    tv_withdraw_SMSCode.setText("发送验证码");
                    tv_withdraw_SMSCode.setClickable(true);
                    tv_withdraw_SMSCode.setTextColor(0xFF1295f7);
                    timerTask.cancel();
                }
            }
        };
        timerTask = new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        handler.sendEmptyMessage(0);
                    }
                });
            }
        };
    }

    boolean checkUserInput() {
        if (edt_WithdrawMoney_WA.getText().length() == 0) {
            edt_WithdrawMoney_WA.setError("不能为空");
            edt_WithdrawMoney_WA.requestFocus();
            return false;
        }else if (edt_PutWithdrawAccount_WA.getText().length() == 0) {
            edt_PutWithdrawAccount_WA.setError("不能为空");
            edt_PutWithdrawAccount_WA.requestFocus();
            return false;
        } else if (edt_Code_WA.getText().length() == 0) {
            edt_Code_WA.setError("不能为空");
            edt_Code_WA.requestFocus();
            return false;
        }
        return true;
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.ll_Back_WA:
                this.finish();
                break;
            case R.id.bt_Complete_WA://提现
                //金额限制
                String money_withDraw = edt_WithdrawMoney_WA.getText().toString().trim();
                String money_all = tv_AllMoney_WA.getText().toString().trim();
                Double moneyWithDrawNum = Double.valueOf(money_withDraw);
                Double moneyAllNum = Double.valueOf(money_all);
                if(moneyAllNum < moneyWithDrawNum){
                    ToastUtil.shortshow(this,"金额不足");
                    return;
                }
                if(moneyWithDrawNum < 50){
                    ToastUtil.shortshow(this,"提现金额不能小于50元");
                    return;
                }
                performWithdraw();
                break;
            case R.id.tv_withdraw_SMSCode:
                MassageCode.RequestMassageCode(tv_PhoneNumber_WA,  tv_withdraw_SMSCode, this);
        }
    }

    private void performWithdraw() {
        if (!checkUserInput()) {
            return;
        }
        AsyncHttpResponseHandler res = new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {

                String str=new String(bytes);
                Log.i(TAG, "jstr is " + str);
                try {
                    JSONObject obj=new JSONObject(str);
                    if (obj.getInt("code")==0){
                        Button.OnClickListener positiveListener = new Button.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                finish();
                            }
                        };
                        CommonDialog.show(WithdrawActivity.this, "确定", "取消", "提现成功，将会在3个工作日到账\n请耐心等待", positiveListener);

                    }else {
                        errorCode(obj.getInt("code"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                ToastUtil.shortshow(WithdrawActivity.this, "提现返回失败");
            }
        };
        RequestParams params = new RequestParams();
        params.put("session_id", getSharedPreferences(StaticBean.USER_INFO, MODE_PRIVATE).getString("session_id", null));
        params.put("amount", Double.parseDouble(edt_WithdrawMoney_WA.getText().toString()));
        params.put("account", edt_PutWithdrawAccount_WA.getText().toString());
        params.put("name", tv_PutZhiFuBaoName_WA.getText().toString());
        params.put("country_code","0086");
        params.put("mobile", tv_PhoneNumber_WA.getText().toString());
        params.put("sms_code",edt_Code_WA.getText().toString() );
        AndroidAsyncHttp.post(ServerAPIConfig.PurseWithdraw, params, res);
    }
}
