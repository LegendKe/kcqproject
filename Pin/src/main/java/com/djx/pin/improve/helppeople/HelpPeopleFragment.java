package com.djx.pin.improve.helppeople;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.TextView;

import com.djx.pin.R;
import com.djx.pin.business.AppConstants;
import com.djx.pin.fragment.OnlineFragment;
import com.djx.pin.improve.base.fragment.BaseFragment;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * Created by 柯传奇 on 2016/12/2 0002.
 */
public class HelpPeopleFragment extends BaseFragment {

    @Bind(R.id.tv_title_action_bar)
    TextView tv_title_online;
    @Bind(R.id.tv_title2_action_bar)
    TextView tv_title_offline;
    private Animator anim_in,anim_out,anim_out0;
    @Bind(R.id.viewPager)
    ViewPager viewPager;
    private float mDensity;

    @Override
    protected int setLayoutId() {
        return R.layout.fragment_help_people;
    }

    @Override
    protected void initView() {
        anim_in = AnimatorInflater.loadAnimator(context, R.animator.zoom_in);
        anim_out = AnimatorInflater.loadAnimator(context, R.animator.zoom_out);
        anim_out0 = AnimatorInflater.loadAnimator(context, R.animator.zoom_out_0);


        mDensity = getResources().getDisplayMetrics().density;
        final ArrayList<Fragment> fragmentList = new ArrayList<>();
        fragmentList.add(new OnlineFragment());
        fragmentList.add(new HelpPeopleOfflineMainFragment());
        viewPager.setAdapter(new FragmentPagerAdapter(getChildFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                return fragmentList.get(position);
            }
            @Override
            public int getCount() {
                return 2;
            }
        });
        viewPager.setPageTransformer(true,new FlipPagerTransformer());
        viewPager.setOffscreenPageLimit(0);
        viewPager.setCurrentItem(1);
        viewHelpInit();
    }

    @Override
    protected void initEvent() {
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}
            @Override
            public void onPageSelected(int position) {
                if(position == 0){
                    onlineHelpZoomOut();
                }else if(position == 1){
                    offlineHelpZoomOut();
                }
            }
            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    @OnClick(R.id.iv_user_action_bar)
    public void openDrawLayout(View v){
        Intent intent = new Intent(AppConstants.INTENT_ACTION_OPENDRAWER);
        getActivity().sendBroadcast(intent);
    }
    @OnClick(R.id.tv_title_action_bar)
    public void selectOnlineHelp(View v){
        onlineHelpZoomOut();
    }
    @OnClick(R.id.tv_title2_action_bar)
    public void selectOfflineHelp(View v){
        offlineHelpZoomOut();
    }

    private void onlineHelpZoomOut(){
        tv_title_online.setClickable(false);
        tv_title_offline.setClickable(true);
        anim_in.setTarget(tv_title_offline);
        anim_out.setTarget(tv_title_online);
        anim_in.start();
        anim_out.start();
        viewPager.setCurrentItem(0);
    }
    private void viewHelpInit(){
        tv_title_online.setClickable(true);
        tv_title_offline.setClickable(false);
        anim_in.setTarget(tv_title_online);
        anim_out0.setTarget(tv_title_offline);
        anim_in.start();
        anim_out0.start();
    }

    private void offlineHelpZoomOut(){
        tv_title_online.setClickable(true);
        tv_title_offline.setClickable(false);
        anim_in.setTarget(tv_title_online);
        anim_out.setTarget(tv_title_offline);
        anim_in.start();
        anim_out.start();
        viewPager.setCurrentItem(1);
    }
    private class FlipPagerTransformer implements ViewPager.PageTransformer{

        @Override
        public void transformPage(View page, float position) {
            if(position <= 0 && position >= -1){
                page.setPivotX(page.getMeasuredWidth());
            }else if(position <= 1 && position >= -1){
                page.setPivotX(0);
            }
            page.setPivotY(page.getMeasuredHeight() * 0.5f);
            if(mDensity <= 1.5f){
                page.setRotationY(position * 90f);

            }else if(1.5f < mDensity && mDensity <= 2.0f){
                page.setRotationY(position * 75f);
            }else if(2.0f < mDensity){
                page.setRotationY(position * 60f);
            }
        }

    }


}
