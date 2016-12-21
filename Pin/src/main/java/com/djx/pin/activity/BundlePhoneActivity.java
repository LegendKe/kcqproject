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
 * Created by Administrator on 2016/6/29.
 */
public class BundlePhoneActivity extends OldBaseActivity implements View.OnClickListener {

    private LinearLayout ll_Back_BPA;
    private EditText edt_PutPhone_BPA, edt_Code_BPA, edt_Password_BPA, edt_PasswordAgain_BPA;
    private Button bt_Complete_BPA;
    private TextView tv_SendCode_BPA;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bundlephone);

        initView();
        initEvent();
    }

    private void initEvent() {
        ll_Back_BPA.setOnClickListener(this);
        tv_SendCode_BPA.setOnClickListener(this);
        bt_Complete_BPA.setOnClickListener(this);
    }

    private void initView() {
        ll_Back_BPA = (LinearLayout) findViewById(R.id.ll_Back_BPA);
        edt_PutPhone_BPA = (EditText) findViewById(R.id.edt_PutPhone_BPA);
        edt_Code_BPA = (EditText) findViewById(R.id.edt_Code_BPA);
        edt_Password_BPA = (EditText) findViewById(R.id.edt_Password_BPA);
        edt_PasswordAgain_BPA = (EditText) findViewById(R.id.edt_PasswordAgain_BPA);
        bt_Complete_BPA = (Button) findViewById(R.id.bt_Complete_BPA);
        tv_SendCode_BPA = (TextView) findViewById(R.id.tv_SendCode_BPA);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_Back_BPA:
                this.finish();
                break;
            case R.id.tv_SendCode_BPA:
                MassageCode.RequestMassageCode(edt_PutPhone_BPA, tv_SendCode_BPA, this);

                break;

            case R.id.bt_Complete_BPA:
                String session_id = getSharedPreferences(StaticBean.USER_INFO, Context.MODE_PRIVATE).getString("session_id", null);
                if (session_id == null || session_id.equals("")) {
                    ToastUtil.shortshow(getApplicationContext(), "请先登录应用");
                    return;
                } else {
                String sms_code = edt_Code_BPA.getText().toString().trim();
                String country_code = "0086";
                String mobile = edt_PutPhone_BPA.getText().toString().trim();
                String password = edt_Password_BPA.getText().toString().trim();

                if (getEidtTextLength(edt_PutPhone_BPA) > getedtLengthAfterTrim(edt_PutPhone_BPA) || getEidtTextLength(edt_Code_BPA) > getedtLengthAfterTrim(edt_Code_BPA) || getEidtTextLength(edt_Password_BPA) > getedtLengthAfterTrim(edt_Password_BPA)) {
                    ToastUtil.shortshow(this, "输入不能有空格");

                    Log.e("length===", getEidtTextLength(edt_PutPhone_BPA) + "");
                    Log.e("lengthaftertrim===", getedtLengthAfterTrim(edt_PutPhone_BPA) + "");
                } else if (getedtLengthAfterTrim(edt_PutPhone_BPA) > 0 && getedtLengthAfterTrim(edt_Code_BPA) > 0 && getedtLengthAfterTrim(edt_Password_BPA) > 0 && getedtLengthAfterTrim(edt_PasswordAgain_BPA) > 0) {

                    if (getedtLengthAfterTrim(edt_Password_BPA) > 5 && getedtLengthAfterTrim(edt_PasswordAgain_BPA) > 5 &&
                            getedtStringAfterTrim(edt_Password_BPA).equals(getedtStringAfterTrim(edt_PasswordAgain_BPA))) {
                        BundleOrChangePhone.RequsetBundlePhone(session_id, sms_code, country_code, mobile, password,this);

                    } else {
                        ToastUtil.shortshow(this, "密码大于五位,两次输入要一致");
                    }
                } else if (getedtLengthAfterTrim(edt_PutPhone_BPA) == 0 || getedtLengthAfterTrim(edt_Code_BPA) == 0 || getedtLengthAfterTrim(edt_Password_BPA) == 0) {
                    ToastUtil.shortshow(this, "内容不能为空");
                }
                break;
        }}
    }


}
