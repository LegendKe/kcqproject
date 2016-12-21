package com.djx.pin.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.djx.pin.R;
import com.djx.pin.utils.DateUtils;

public class PaySuccessActivity extends AppCompatActivity implements View.OnClickListener {
    TextView tv_success;
    Button bt_success;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_windowthrowupdatasuccess);
        initView();
        initEvent();
    }

    private void initEvent() {
        bt_success.setOnClickListener(this);

    }

    private void initView() {
        tv_success= (TextView) findViewById(R.id.tv_success);
        tv_success.setText("您于"+ DateUtils.getCurrentDate()+"发布的悬赏\n已提交成功");
        bt_success= (Button) findViewById(R.id.bt_success);

    }

    @Override
    public void onClick(View v) {
        startActivity(new Intent(this,WindowThrowMainActivity.class));
        this.finish();
    }
}
