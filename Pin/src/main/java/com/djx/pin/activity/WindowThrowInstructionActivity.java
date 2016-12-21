package com.djx.pin.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import com.djx.pin.R;
public class WindowThrowInstructionActivity extends AppCompatActivity implements View.OnClickListener {

    LinearLayout ll_back;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_windowthrowinstruction);
        initView();
        initEvent();
    }

    private void initEvent() {
        ll_back.setOnClickListener(this);

    }

    private void initView() {
        ll_back= (LinearLayout) findViewById(R.id.ll_back);

    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.ll_back:
                finish();
                break;
        }
    }
}
