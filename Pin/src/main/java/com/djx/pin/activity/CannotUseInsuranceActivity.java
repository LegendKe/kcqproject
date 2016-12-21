package com.djx.pin.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.djx.pin.R;
import com.djx.pin.base.OldBaseActivity;

/**
 * Created by Administrator on 2016/6/28.
 */
public class CannotUseInsuranceActivity extends OldBaseActivity implements View.OnClickListener {
    private LinearLayout ll_Back_NVA;
    private Button bt_goback;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nouse_insurance);
        initView();
        initEvent();
    }

    private void initEvent() {
        ll_Back_NVA.setOnClickListener(this);
        bt_goback.setOnClickListener(this);
    }

    private void initView() {
        ll_Back_NVA= (LinearLayout) findViewById(R.id.ll_Back_NVA);
        bt_goback= (Button) findViewById(R.id.bt_goback);
    }

    @Override
    public void onClick(View v) {
        this.finish();
    }

}
