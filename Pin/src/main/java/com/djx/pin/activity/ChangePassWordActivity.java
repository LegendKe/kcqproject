package com.djx.pin.activity;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.djx.pin.R;
import com.djx.pin.base.OldBaseActivity;
import com.djx.pin.beans.StaticBean;
import com.djx.pin.utils.BundleOrChangePhone;
import com.djx.pin.utils.MassageCode;
import com.djx.pin.utils.ToastUtil;

/**
 * Created by Administrator on 2016/6/30.
 */
public class ChangePassWordActivity extends OldBaseActivity implements View.OnClickListener {

    private LinearLayout ll_Back_CPWA;
    private EditText edt_PutPhone_CPWA,edt_Code_CPWA,edt_NewPassWord_CPWA,edt_NewPassWordAgain_CPWA;
    private TextView tv_SendCode_CPWA;
    private Button bt_Complete_CPWA;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_changepassword);

        initView();
        initEvent();
    }

    private void initEvent() {
        ll_Back_CPWA.setOnClickListener(this);
        tv_SendCode_CPWA.setOnClickListener(this);
        bt_Complete_CPWA.setOnClickListener(this);

    }

    private void initView() {
        ll_Back_CPWA= (LinearLayout) findViewById(R.id.ll_Back_CPWA);
        edt_PutPhone_CPWA= (EditText) findViewById(R.id.edt_PutPhone_CPWA);
        edt_Code_CPWA= (EditText) findViewById(R.id.edt_Code_CPWA);
        edt_NewPassWord_CPWA= (EditText) findViewById(R.id.edt_NewPassWord_CPWA);
        edt_NewPassWordAgain_CPWA= (EditText) findViewById(R.id.edt_NewPassWordAgain_CPWA);
        tv_SendCode_CPWA= (TextView) findViewById(R.id.tv_SendCode_CPWA);
        bt_Complete_CPWA= (Button) findViewById(R.id.bt_Complete_CPWA);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.ll_Back_CPWA:
                this.finish();
                break;
            case R.id.tv_SendCode_CPWA:

                MassageCode.RequestMassageCode(edt_PutPhone_CPWA,tv_SendCode_CPWA,this);

                break;
            case R.id.bt_Complete_CPWA:
                /**
                 * {
                 "session_id": "8527a2b6a58b4fa963fd",
                 "country_code": "0086",
                 "mobile": "13112345678",
                 "sms_code": "123112",
                 "password": "password"
                 }
                 * */
                String session_id=getSharedPreferences(StaticBean.USER_INFO, Context.MODE_PRIVATE).getString("session_id",null);
                if (session_id == null || session_id.equals("")) {
                    ToastUtil.shortshow(getApplicationContext(), "请先登录应用");
                    return;
                } else {
                String sms_code = edt_Code_CPWA.getText().toString().trim();
                String country_code = "0086";
                String mobile = edt_PutPhone_CPWA.getText().toString().trim();
                String password = edt_NewPassWord_CPWA.getText().toString().trim();
                if (getEidtTextLength(edt_PutPhone_CPWA) > getedtLengthAfterTrim(edt_PutPhone_CPWA) || getEidtTextLength(edt_Code_CPWA) > getedtLengthAfterTrim(edt_Code_CPWA) || getEidtTextLength(edt_NewPassWord_CPWA) > getedtLengthAfterTrim(edt_NewPassWord_CPWA)) {
                    ToastUtil.shortshow(this, "输入不能有空格");

                    Log.e("length===", getEidtTextLength(edt_PutPhone_CPWA) + "");
                    Log.e("lengthaftertrim===", getedtLengthAfterTrim(edt_PutPhone_CPWA) + "");
                } else if (getedtLengthAfterTrim(edt_PutPhone_CPWA) > 0 && getedtLengthAfterTrim(edt_Code_CPWA) > 0 && getedtLengthAfterTrim(edt_NewPassWord_CPWA) > 0 && getedtLengthAfterTrim(edt_NewPassWordAgain_CPWA) > 0) {

                    if (getedtLengthAfterTrim(edt_NewPassWord_CPWA) > 5 && getedtLengthAfterTrim(edt_NewPassWordAgain_CPWA) > 5 &&
                            getedtStringAfterTrim(edt_NewPassWord_CPWA).equals(getedtStringAfterTrim(edt_NewPassWordAgain_CPWA))) {
                        BundleOrChangePhone.ChangePassword(session_id, sms_code, country_code, mobile, password,this);

                    } else {
                        ToastUtil.shortshow(this, "密码大于五位,两次输入要一致");
                    }
                } else if (getedtLengthAfterTrim(edt_PutPhone_CPWA) == 0 || getedtLengthAfterTrim(edt_Code_CPWA) == 0 || getedtLengthAfterTrim(edt_NewPassWord_CPWA) == 0) {
                    ToastUtil.shortshow(this, "内容不能为空");
                }

                break;
        }}
    }
}
