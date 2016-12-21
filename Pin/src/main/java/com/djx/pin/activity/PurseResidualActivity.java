package com.djx.pin.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.djx.pin.R;
import com.djx.pin.base.OldBaseActivity;
import com.djx.pin.beans.UserInfo;
import com.djx.pin.utils.myutils.LogicUtils;

import java.text.DecimalFormat;

import de.greenrobot.event.EventBus;
import de.greenrobot.event.Subscribe;
import de.greenrobot.event.ThreadMode;

/**
 * Created by Administrator on 2016/7/19 0019.
 */
public class PurseResidualActivity extends OldBaseActivity implements View.OnClickListener {
    protected final static String TAG = PurseResidualActivity.class.getSimpleName();

    LinearLayout ll_Back_PRA;
    TextView tv_MoneyPointFront_PRA, tv_MoneyPointBehind_PRA, tv_Withdrawal_PRA, tv_ReCharge_PRA;
    private float balance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_purseresidual);

        Intent intent=getIntent();
        Bundle bundle=intent.getExtras();
        balance = bundle.getFloat("balance");

        initView();
        initEvent();

        setViewContent();
        EventBus.getDefault().register(this);
    }

    private void initEvent() {
        ll_Back_PRA.setOnClickListener(this);
        tv_Withdrawal_PRA.setOnClickListener(this);
        tv_ReCharge_PRA.setOnClickListener(this);
    }

    private void initView() {
        ll_Back_PRA = (LinearLayout) findViewById(R.id.ll_Back_PRA);
        tv_MoneyPointFront_PRA = (TextView) findViewById(R.id.tv_MoneyPointFront_PRA);
        tv_MoneyPointBehind_PRA = (TextView) findViewById(R.id.tv_MoneyPointBehind_PRA);
        tv_Withdrawal_PRA = (TextView) findViewById(R.id.tv_Withdrawal_PRA);
        tv_ReCharge_PRA = (TextView) findViewById(R.id.tv_ReCharge_PRA);
    }

    private void setViewContent() {
        DecimalFormat totalFormat=new DecimalFormat("0.00");
        String totalStr = totalFormat.format(balance);
        int idx = totalStr.lastIndexOf(".");//查找小数点的位置
        tv_MoneyPointFront_PRA.setText(totalStr.substring(0, idx));
        tv_MoneyPointBehind_PRA.setText(totalStr.substring(idx, totalStr.length()));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_Back_PRA:
                this.finish();
                break;
            case R.id.tv_Withdrawal_PRA:
                //先检查有没有实名认证

                LogicUtils.realNameVerify(this, new LogicUtils.AfterPassedListener() {
                    @Override
                    public void realNameVerifyPassed() {
                        Intent intent = new Intent();
                        Bundle bundle = new Bundle();
                        bundle.putFloat("balance", balance);
                        intent.putExtras(bundle);
                        intent.setClass(PurseResidualActivity.this, WithdrawActivity.class);
                        startActivity(intent);
                    }
                });

                break;
            case R.id.tv_ReCharge_PRA:
                Bundle bundle1=new Bundle();
                bundle1.putInt("type",1);
                startActivity(PurseRechargeActivity.class,bundle1);
                break;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MainThread)
    public void onEvent(String state) {
        if (state.equals("update_balance")) {
            Log.i(TAG, "update balance");
            UserInfo.getUserInfo(PurseResidualActivity.this, new UserInfo.GetUserInfoCallBack() {
                @Override
                public void callback(boolean state) {
                    balance = UserInfo.getBalance(PurseResidualActivity.this);
                    setViewContent();
                }
            });
        }
    }
}
