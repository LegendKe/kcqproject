package com.djx.pin.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.djx.pin.R;
import com.djx.pin.adapter.MyPageAdapter;
import com.djx.pin.base.OldBaseActivity;
import com.djx.pin.beans.StaticBean;
import com.djx.pin.improve.login.GuideFragment01;
import com.djx.pin.improve.login.GuideFragment02;
import com.djx.pin.improve.login.GuideFragment03;
import com.djx.pin.improve.login.GuideFragment04;
import com.djx.pin.ui.CommonDialog;
import com.lidroid.xutils.util.LogUtils;
import com.tencent.mm.sdk.constants.Build;

import java.util.ArrayList;

/**
 * Created by 陈刘磊 (代号：姜饼（GingerBread）) on 2016/5/21.
 */
public class LeadActivity extends OldBaseActivity{

    private ViewPager viewPager;
    private MyPageAdapter adapter;
    //private ImageView iv_immediatelyexperience;
    private int denyNum = 0;//权限提醒被拒绝次数
    private ArrayList<Fragment> fragments;
    private ArrayList<ImageView> iv_dots ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lead);

        // 暂时没有设计引导界面.所以先隐藏起来.

        LogUtils.e("-----------" + Build.SDK_INT);
        if (android.os.Build.VERSION.SDK_INT < 23) {
            boolean isFirstRun = getSharedPreferences("Setting", Context.MODE_PRIVATE).getBoolean("isFirstRun", true);
            if (isFirstRun) {
                initView();
                getSharedPreferences(StaticBean.USER_INFO, Context.MODE_PRIVATE).edit().putBoolean("isLogined", false).commit();
                getSharedPreferences("Setting", Context.MODE_PRIVATE).edit().putBoolean("isFirstRun", false).commit();
            } else {
                startActivity(LogoActivity.class);
                this.finish();
            }
        }
    }

    @Override
    public void appPermissionOK() {
        boolean isFirstRun = getSharedPreferences("Setting", Context.MODE_PRIVATE).getBoolean("isFirstRun", true);
        if (isFirstRun) {
            initView();
            getSharedPreferences(StaticBean.USER_INFO, Context.MODE_PRIVATE).edit().putBoolean("isLogined", false).commit();
            getSharedPreferences("Setting", Context.MODE_PRIVATE).edit().putBoolean("isFirstRun", false).commit();
        } else {
            startActivity(LogoActivity.class);
            this.finish();
        }
    }


    @Override
    public void appPermissionDeny() {

        CommonDialog.show(this, "确定", "取消", "为确保正常使用,请务必打开外部存储及位置信息权限", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                denyNum++;
                if(denyNum>5){
                    startActivity(new Intent(Settings.ACTION_APPLICATION_SETTINGS));
                }else {
                    startInitBaiduSDKByPermissions();
                }
            }
        }, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LeadActivity.this.finish();
            }
        });

    }


    //初始化数据
    public void initView() {
        viewPager = (ViewPager) findViewById(R.id.viewPager);
        ImageView iv_01 = ((ImageView) findViewById(R.id.iv_01));
        ImageView iv_02 = ((ImageView) findViewById(R.id.iv_02));
        ImageView iv_03 = ((ImageView) findViewById(R.id.iv_03));
        ImageView iv_04 = ((ImageView) findViewById(R.id.iv_04));
        iv_dots = new ArrayList<>();
        iv_dots.add(iv_01);
        iv_dots.add(iv_02);
        iv_dots.add(iv_03);
        iv_dots.add(iv_04);
        fragments = new ArrayList<>();
        fragments.add(new GuideFragment01());
        fragments.add(new GuideFragment02());
        fragments.add(new GuideFragment03());
        fragments.add(new GuideFragment04());
        viewPager.setAdapter(new MyPagerAdapter(getSupportFragmentManager()));
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }
            @Override
            public void onPageSelected(int position) {
                Log.e("pos","-----position:-----"+position);
                for (int i = 0; i < fragments.size(); i++) {
                    if(i != position){
                        iv_dots.get(i).setImageResource(R.mipmap.dot_normal);
                    }else {
                        iv_dots.get(position).setImageResource(R.mipmap.dot_select);
                    }
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    class MyPagerAdapter extends FragmentPagerAdapter{

        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }
    }
}
