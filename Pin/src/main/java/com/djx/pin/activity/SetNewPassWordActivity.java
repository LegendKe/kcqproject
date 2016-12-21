package com.djx.pin.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.djx.pin.R;
import com.djx.pin.base.OldBaseActivity;

/**
 * Created by Administrator on 2016/6/7.
 */
public class SetNewPassWordActivity extends OldBaseActivity implements View.OnClickListener {

    private LinearLayout ll_SNPA_Back;
    private EditText et_SNPA_NewPassword, et_SNPA_NewPasswordAgain;
    private Button bt_SNPA_Finish;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setnewpassword);
        initView();
        initEvent();
    }

    private void initView() {
        ll_SNPA_Back = (LinearLayout) findViewById(R.id.ll_SNPA_Back);
        et_SNPA_NewPassword = (EditText) findViewById(R.id.et_SNPA_NewPassword);
        et_SNPA_NewPasswordAgain = (EditText) findViewById(R.id.et_SNPA_NewPasswordAgain);
        bt_SNPA_Finish = (Button) findViewById(R.id.bt_SNPA_Finish);
    }

    private void initEvent() {
        ll_SNPA_Back.setOnClickListener(this);

        bt_SNPA_Finish.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_SNPA_Back:
                startActivity(ForgotPassWordActivity.class);
                this.finish();
                break;
            case R.id.bt_SNPA_Finish:
                startActivity(LoginActivity.class);
                finish();
                break;
        }
    }
}
