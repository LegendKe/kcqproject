package com.djx.pin.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.djx.pin.R;
import com.djx.pin.base.OldBaseActivity;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Administrator on 2016/8/12 0012.
 */
public class PayVerifySuccessActivity extends OldBaseActivity implements View.OnClickListener {

    Button bt_back;
    TextView tv_SuccessDate;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payverifysuccess);
        initView();
        initEvent();
        Date date=new Date(System.currentTimeMillis());
        SimpleDateFormat format=new SimpleDateFormat("yyyy年MM月dd日");
        tv_SuccessDate.setText("您于"+format.format(date)+"发布的悬赏已经提交成功");
        Log.e("日期===",format.format(date));
    }

    private void initEvent() {
        bt_back.setOnClickListener(this);
    }

    private void initView() {
        bt_back= (Button) findViewById(R.id.bt_back);
        tv_SuccessDate= (TextView) findViewById(R.id.tv_SuccessDate);
    }

    /**
     * Called when a view has been clicked.
     *
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.bt_back:
                startActivity(MainActivity.class);
//                PayVerifyActivity.instance.finish();
//                PayTypeActivity.instance.finish();
                this.finish();
                break;
        }
    }
}
