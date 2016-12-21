package com.djx.pin.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.djx.pin.R;
import com.djx.pin.base.OldBaseActivity;
import com.djx.pin.utils.ToastUtil;

import java.text.DecimalFormat;

import static android.R.attr.format;

/**
 * Created by Administrator on 2016/7/19 0019.
 */
public class PurseAccidentInsuranceActivity extends OldBaseActivity implements View.OnClickListener {


    private TextView tv_InsuranceMassage_PAIA, tv_MoneyPointFront_PAIA, tv_MoneyPointBehind_PAIA, tv_ReCharge_PAIA;
    private LinearLayout ll_Back_PAIA, ll_LookDetail_PA;
    private Float credit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_paccidentinsurance);

        Intent intent=getIntent();
        Bundle bundle=intent.getExtras();
        credit = bundle.getFloat("credit");

        initView();
        initEvent();
    }

    private void initEvent() {
        tv_InsuranceMassage_PAIA.setMovementMethod(ScrollingMovementMethod.getInstance());
        ll_Back_PAIA.setOnClickListener(this);
        tv_ReCharge_PAIA.setOnClickListener(this);
        ll_LookDetail_PA.setOnClickListener(this);
    }

    private void initView() {
        tv_InsuranceMassage_PAIA = (TextView) findViewById(R.id.tv_InsuranceMassage_PAIA);
        tv_MoneyPointFront_PAIA = (TextView) findViewById(R.id.tv_MoneyPointFront_PAIA);
        tv_MoneyPointBehind_PAIA = (TextView) findViewById(R.id.tv_MoneyPointBehind_PAIA);
        tv_ReCharge_PAIA = (TextView) findViewById(R.id.tv_ReCharge_PAIA);
        ll_Back_PAIA = (LinearLayout) findViewById(R.id.ll_Back_PAIA);
        DecimalFormat totalFormat=new DecimalFormat("0.00");
        String totalStr = totalFormat.format(credit);
        int idx = totalStr.lastIndexOf(".");//查找小数点的位置
        tv_MoneyPointFront_PAIA.setText(totalStr.substring(0, idx));
        tv_MoneyPointBehind_PAIA.setText(totalStr.substring(idx, totalStr.length()));
        ll_LookDetail_PA = (LinearLayout) findViewById(R.id.ll_LookDetail_PA);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_Back_PAIA:
                this.finish();
                break;
            case R.id.tv_ReCharge_PAIA:
                Bundle bundle=new Bundle();
                bundle.putInt("type",2);
                startActivity(PurseRechargeActivity.class,bundle);
                //ToastUtil.shortshow(this,"点击了充值");
                break;
            //查看明细
            case R.id.ll_LookDetail_PA:
                Intent intent = new Intent(this, LookPurseDetailActivity.class);
                intent.putExtra("content_type", 2);
                startActivity(intent);
                break;
        }
    }
}
