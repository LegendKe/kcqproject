package com.djx.pin.activity;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.djx.pin.R;
import com.djx.pin.base.OldBaseActivity;

/**
 * Created by Administrator on 2016/6/28.
 */
public class NewsVersionActivity extends OldBaseActivity implements View.OnClickListener {
    private LinearLayout ll_Back_NVA;
    private TextView tv_version;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_newsversion);
        initView();
        initEvent();
        setViewContent();
    }

    private void initEvent() {
        ll_Back_NVA.setOnClickListener(this);
    }

    private void initView() {
        ll_Back_NVA= (LinearLayout) findViewById(R.id.ll_Back_NVA);
        tv_version = (TextView) findViewById(R.id.tv_version);
    }

    private void setViewContent() {
        tv_version.setText(getAppVersionName(this));
    }

    @Override
    public void onClick(View v) {
        this.finish();
    }

    public static String getAppVersionName(Context context) {
        String versionName = "";
        try {
            // ---get the package info---
            PackageManager pm = context.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);
            versionName = pi.versionName;
            if (versionName == null || versionName.length() <= 0) {
                return "";
            }
        } catch (Exception e) {
            Log.e("VersionInfo", "Exception", e);
        }
        return versionName;
    }
}
