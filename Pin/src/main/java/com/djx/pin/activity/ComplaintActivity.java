package com.djx.pin.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import com.djx.pin.R;
import com.djx.pin.base.OldBaseActivity;

/**
 * Created by Administrator on 2016/7/1.
 */
public class ComplaintActivity extends OldBaseActivity implements View.OnClickListener {

    private LinearLayout ll_Back_CA;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complaint);

        initView();
        initEvent();
    }

    private void initEvent() {
        ll_Back_CA.setOnClickListener(this);
    }

    private void initView() {
        ll_Back_CA = (LinearLayout) findViewById(R.id.ll_Back_CA);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_Back_CA:
                this.finish();
                break;
        }
    }
}
