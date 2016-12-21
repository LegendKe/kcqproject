package com.djx.pin.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.djx.pin.R;
import com.djx.pin.base.OldBaseActivity;
import com.djx.pin.serverapiconfig.ServerAPIConfig;
import com.djx.pin.utils.AndroidAsyncHttp;
import com.djx.pin.utils.MassageCode;
import com.djx.pin.utils.ToastUtil;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

/**
 * Created by Administrator on 2016/6/30.
 */
public class ForgotPassWordActivity extends OldBaseActivity implements View.OnClickListener {

    private LinearLayout ll_Back_CPWA;
    private EditText edt_PutPhone_CPWA, edt_Code_CPWA, edt_NewPassWord_CPWA, edt_NewPassWordAgain_CPWA;
    private TextView tv_SendCode_CPWA;
    private Button bt_Complete_CPWA;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgotpassword);

        initView();
        initEvent();
    }

    private void initEvent() {
        ll_Back_CPWA.setOnClickListener(this);
        tv_SendCode_CPWA.setOnClickListener(this);
        bt_Complete_CPWA.setOnClickListener(this);

    }

    private void initView() {
        ll_Back_CPWA = (LinearLayout) findViewById(R.id.ll_Back_CPWA);
        edt_PutPhone_CPWA = (EditText) findViewById(R.id.edt_PutPhone_CPWA);
        edt_Code_CPWA = (EditText) findViewById(R.id.edt_Code_CPWA);
        edt_NewPassWord_CPWA = (EditText) findViewById(R.id.edt_NewPassWord_CPWA);
        edt_NewPassWordAgain_CPWA = (EditText) findViewById(R.id.edt_NewPassWordAgain_CPWA);
        tv_SendCode_CPWA = (TextView) findViewById(R.id.tv_SendCode_CPWA);
        bt_Complete_CPWA = (Button) findViewById(R.id.bt_Complete_CPWA);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_Back_CPWA:
                this.finish();
                break;
            case R.id.tv_SendCode_CPWA:

                MassageCode.RequestMassageCode(edt_PutPhone_CPWA, tv_SendCode_CPWA, this);

                break;
            case R.id.bt_Complete_CPWA:

                String sms_code = edt_Code_CPWA.getText().toString().trim();
                String country_code = "0086";
                String mobile = edt_PutPhone_CPWA.getText().toString().trim();
                String password = edt_NewPassWord_CPWA.getText().toString().trim();
                boolean matches = password.matches("^[A-Za-z0-9]+$");
                if(!matches){
                    ToastUtil.shortshow(this,"密码只能是数字或字母");
                    return;
                }

                if (getEidtTextLength(edt_PutPhone_CPWA) > getedtLengthAfterTrim(edt_PutPhone_CPWA) || getEidtTextLength(edt_Code_CPWA) > getedtLengthAfterTrim(edt_Code_CPWA) || getEidtTextLength(edt_NewPassWord_CPWA) > getedtLengthAfterTrim(edt_NewPassWord_CPWA)) {
                    ToastUtil.shortshow(this, "输入不能有空格");

                    Log.e("length===", getEidtTextLength(edt_PutPhone_CPWA) + "");
                    Log.e("lengthaftertrim===", getedtLengthAfterTrim(edt_PutPhone_CPWA) + "");
                } else if (getedtLengthAfterTrim(edt_PutPhone_CPWA) > 0 && getedtLengthAfterTrim(edt_Code_CPWA) > 0 && getedtLengthAfterTrim(edt_NewPassWord_CPWA) > 0 && getedtLengthAfterTrim(edt_NewPassWordAgain_CPWA) > 0) {

                    if (getedtLengthAfterTrim(edt_NewPassWord_CPWA) > 5 && getedtLengthAfterTrim(edt_NewPassWordAgain_CPWA) > 5 &&
                            getedtStringAfterTrim(edt_NewPassWord_CPWA).equals(getedtStringAfterTrim(edt_NewPassWordAgain_CPWA))) {
                        PasswordReset(sms_code, country_code, mobile, password);
                    } else {
                        ToastUtil.shortshow(this, "密码大于五位,两次输入要一致");
                    }
                } else if (getedtLengthAfterTrim(edt_PutPhone_CPWA) == 0 || getedtLengthAfterTrim(edt_Code_CPWA) == 0 || getedtLengthAfterTrim(edt_NewPassWord_CPWA) == 0) {
                    ToastUtil.shortshow(this, "内容不能为空");
                }

                break;
        }
    }

    /**
     * {
     * "country_code": "0086",
     * "mobile": "13112345678",
     * "sms_code": "123112",
     * "password": "password"
     * }忘记密码
     */
    public void PasswordReset(String sms_code, String country_code, String mobile, String password) {
        String url = ServerAPIConfig.PassWordReset;
        RequestParams params = new RequestParams();
        params.put("sms_code", sms_code);
        params.put("country_code", country_code);
        params.put("mobile", mobile);
        params.put("password", password);
        Log.e("url====", url + params.toString());
        AsyncHttpResponseHandler upHandler = new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                String str = new String(bytes);
                Log.e("str==", str);
                try {
                    JSONObject object = new JSONObject(str);
                    int code = object.getInt("code");
                    if (code == 0) {
                        ToastUtil.shortshow(getApplicationContext(), "修改密码成功");

                        ForgotPassWordActivity.this.finish();
                    } else {
                        errorCode(code);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                Log.e("密码修改失败", "密码修改失败");
            }
        };
        AndroidAsyncHttp.post(url, params, upHandler);
    }
}
