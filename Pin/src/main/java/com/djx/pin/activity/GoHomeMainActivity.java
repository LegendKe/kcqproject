package com.djx.pin.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.djx.pin.R;
import com.djx.pin.base.OldBaseActivity;
import com.djx.pin.beans.StaticBean;
import com.djx.pin.utils.myutils.LogicUtils;
import com.djx.pin.utils.ToastUtil;

public class GoHomeMainActivity extends OldBaseActivity implements View.OnClickListener {


    RelativeLayout rl_report_now,//我要举报
            rl_instruction,//使用说明
            rl_report_history;//历史举报
    LinearLayout ll_back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gohomemain);
        initView();
        initEvent();
    }

    private void initView() {
        ll_back = (LinearLayout) findViewById(R.id.ll_back);

        rl_report_now = (RelativeLayout) findViewById(R.id.rl_report_now);
        rl_instruction = (RelativeLayout) findViewById(R.id.rl_instruction);
        rl_report_history = (RelativeLayout) findViewById(R.id.rl_report_history);
    }
    private void initEvent() {
        rl_report_now.setOnClickListener(this);
        rl_instruction.setOnClickListener(this);
        rl_report_history.setOnClickListener(this);
        ll_back.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            //点击返回按钮
            case R.id.ll_back:
                finish();
                break;
            //点击我要举报
            case R.id.rl_report_now:
                //检查用户是否登录,未登录则return;已经实名认证的用户才可以举报
                if(getSharedPreferences(StaticBean.USER_INFO,MODE_PRIVATE).getBoolean("isLogined",false)==false){
                    ToastUtil.shortshow(this,R.string.toast_non_login);
                    return;
                }
                LogicUtils.realNameVerify(this, new LogicUtils.AfterPassedListener() {
                    @Override
                    public void realNameVerifyPassed() {
                        startActivity(new Intent(GoHomeMainActivity.this, GoHomeUpdataInfoActivity.class));
                    }
                });
                //startActivity(new Intent(GoHomeMainActivity.this, GoHomeUpdataInfoActivity.class));
                break;
            //点击历史举报按钮
            case R.id.rl_report_history:
                //检查用户是否登录,未登录则return;
                if(getSharedPreferences(StaticBean.USER_INFO,MODE_PRIVATE).getBoolean("isLogined",false)==false){
                    ToastUtil.shortshow(this,R.string.toast_non_login);
                    return;
                }
                startActivity(new Intent(this, GoHomeHistoryActivity.class));
                break;
            //点击使用说明按钮
            case R.id.rl_instruction:
                startActivity(new Intent(this, GoHomeInstructionActivity.class));
                break;
        }

    }
}
