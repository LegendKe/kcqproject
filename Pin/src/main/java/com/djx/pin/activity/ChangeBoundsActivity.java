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
public class ChangeBoundsActivity extends OldBaseActivity implements View.OnClickListener {

    private LinearLayout ll_Back_CBA;
    private EditText edt_PutPhone_CBA,edt_Code_CBA,edt_LoginPassWord_CBA;
    private TextView tv_SendCode_CBA;
    private Button bt_Complete_CBA;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_changebounds);

        initView();
        initEvent();
    }

    private void initEvent() {
        ll_Back_CBA.setOnClickListener(this);
        tv_SendCode_CBA.setOnClickListener(this);
        bt_Complete_CBA.setOnClickListener(this);
    }

    private void initView() {
        ll_Back_CBA= (LinearLayout) findViewById(R.id.ll_Back_CBA);
        edt_PutPhone_CBA= (EditText) findViewById(R.id.edt_PutPhone_CBA);
        edt_Code_CBA= (EditText) findViewById(R.id.edt_Code_CBA);
        edt_LoginPassWord_CBA= (EditText) findViewById(R.id.edt_LoginPassWord_CBA);
        tv_SendCode_CBA= (TextView) findViewById(R.id.tv_SendCode_CBA);
        bt_Complete_CBA= (Button) findViewById(R.id.bt_Complete_CBA);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.ll_Back_CBA:
                this.finish();
                break;
            case R.id.tv_SendCode_CBA:
                MassageCode.RequestMassageCode(edt_PutPhone_CBA,tv_SendCode_CBA,this);
                break;
            case R.id.bt_Complete_CBA:


                String session_id = getSharedPreferences(StaticBean.USER_INFO, Context.MODE_PRIVATE).getString("session_id", null);
                if (session_id == null || session_id.equals("")) {
                    ToastUtil.shortshow(getApplicationContext(), "请先登录应用");
                    return;
                } else {
                String sms_code = edt_Code_CBA.getText().toString().trim();
                String country_code = "0086";
                String mobile = edt_PutPhone_CBA.getText().toString().trim();
                String password = edt_LoginPassWord_CBA.getText().toString().trim();

                if (getEidtTextLength(edt_PutPhone_CBA) > getedtLengthAfterTrim(edt_PutPhone_CBA) || getEidtTextLength(edt_Code_CBA) > getedtLengthAfterTrim(edt_Code_CBA) || getEidtTextLength(edt_LoginPassWord_CBA) > getedtLengthAfterTrim(edt_LoginPassWord_CBA)) {
                    ToastUtil.shortshow(this, "输入不能有空格");

                    Log.e("length===", getEidtTextLength(edt_PutPhone_CBA) + "");
                    Log.e("lengthaftertrim===", getedtLengthAfterTrim(edt_PutPhone_CBA) + "");
                } else if (getedtLengthAfterTrim(edt_PutPhone_CBA) > 0 && getedtLengthAfterTrim(edt_Code_CBA) > 0 && getedtLengthAfterTrim(edt_LoginPassWord_CBA) > 0 ) {

                    if (getedtLengthAfterTrim(edt_LoginPassWord_CBA) > 5 ) {
                        BundleOrChangePhone.RequsetChangeBundlePhone(session_id, sms_code, country_code, mobile, password,this);

                    } else {
                        ToastUtil.shortshow(this, "密码不对");
                    }
                } else if (getedtLengthAfterTrim(edt_PutPhone_CBA) == 0 || getedtLengthAfterTrim(edt_Code_CBA) == 0 || getedtLengthAfterTrim(edt_LoginPassWord_CBA) == 0) {
                    ToastUtil.shortshow(this, "内容不能为空");
                }
                break;

        }}
    }
}
