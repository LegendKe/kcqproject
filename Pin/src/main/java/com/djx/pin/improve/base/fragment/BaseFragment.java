package com.djx.pin.improve.base.fragment;

/**
 * Created by 柯传奇 on 2016/11/28 0028.
 */

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.ButterKnife;

public abstract class BaseFragment extends Fragment {

    private Bundle mBundle;
    protected Activity context;

    public static final int STATE_NONE = 0;
    public static final int STATE_REFRESH = 1;
    public static final int STATE_LOADMORE = 2;
    public static final int STATE_NOMORE = 3;
    public static final int STATE_PRESSNONE = 4;// 正在下拉但还没有到刷新的状态
    public static int mState = STATE_NONE;

    private Fragment mFragment;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(setLayoutId(),null);
        context = getActivity();
        ButterKnife.bind(this,view);
        initView();
        initView(view);
        initData();
        initEvent();
        return view;
    }

    protected void initView(View view) {
    }

    protected void initEvent() {
    }

    protected void initData() {
    }

    protected void initView() {
    }

    protected abstract int setLayoutId();

    protected void initBundle(Bundle bundle) {}

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBundle = getArguments();
        initBundle(mBundle);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
        mBundle = null;
    }

    protected void addFragment(int frameLayoutId, Fragment fragment) {
        if (fragment != null) {
            FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
            if (fragment.isAdded()) {
                if (mFragment != null) {
                    transaction.hide(mFragment).show(fragment);
                } else {
                    transaction.show(fragment);
                }
            } else {
                if (mFragment != null) {
                    transaction.hide(mFragment).add(frameLayoutId, fragment);
                } else {
                    transaction.add(frameLayoutId, fragment);
                }
            }
            mFragment = fragment;
            transaction.commit();
        }
    }
   protected void replaceFragment(int frameLayoutId, Fragment fragment) {
       if (fragment != null) {
           FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
           transaction.replace(frameLayoutId, fragment);
           transaction.commit();
       }
   }

    /***
     * 实现Activity之间不传值跳转的方法
     */
    public void startActivity(Class<?> Class) {
        Intent intent = new Intent(getContext(), Class);
        this.startActivity(intent);
    }
    /***
     * 实现Activity之间传值跳转
     */
    public void startActivity(Class<?> Class, Bundle bundle) {
        Intent intent = new Intent(getContext(), Class);
        intent.putExtras(bundle);
        this.startActivity(intent);
    }
}
