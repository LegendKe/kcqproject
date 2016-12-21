package com.djx.pin.personal;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.djx.pin.R;
import com.djx.pin.base.baseui.BaseActivity;
import com.djx.pin.beans.StaticBean;

import cz.msebera.android.httpclient.util.TextUtils;

/**
 * Created by 柯传奇 on 2016/11/8 0008.
 */
public class RealNameVerified extends BaseActivity{

    private TextView tv_name;
    private TextView tv_id;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_realname_verified);
        initView();
        initData();
    }


    protected void initView() {
        tv_name = ((TextView) findViewById(R.id.tv_name));
        tv_id = ((TextView) findViewById(R.id.tv_id_number));
        ((ImageView) findViewById(R.id.ic_back)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    protected void initData() {
        String name = getSharedPreferences(StaticBean.USER_INFO, Context.MODE_PRIVATE).getString("real_name", null);
        String certNo = getSharedPreferences(StaticBean.USER_INFO, Context.MODE_PRIVATE).getString("id_card", null);

        if(!TextUtils.isEmpty(name) && !TextUtils.isEmpty(certNo)){

            String newName = certNo.replace(certNo.substring(1, certNo.length() - 1), "*************");
            tv_id.setText(newName);

            if(name.length() == 1){
                tv_name.setText(name);
            }else if(name.length() > 1) {
                StringBuffer sb = new StringBuffer();
                for (int i = 0; i < name.length()-1; i++) {
                    sb.append("*");
                }
                String str = sb.append(name.charAt(name.length()-1)).toString();
                tv_name.setText(str);
            }


        }

    }
}
