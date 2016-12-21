package com.djx.pin.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.djx.pin.R;
import com.djx.pin.activity.LoginActivity;
import com.djx.pin.activity.MainActivity;
import com.djx.pin.base.OldBaseActivity;
import com.djx.pin.base.NewBaseFragment;
import com.djx.pin.beans.StaticBean;
import com.djx.pin.utils.ToastUtil;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 侧滑菜单界面
 *
 * @author FireAnt（http://my.oschina.net/LittleDY）
 * @created 2014年9月25日 下午6:00:05
 */
public class NavigationDrawerFragment extends NewBaseFragment implements
        OnClickListener {
    protected final static String TAG = NavigationDrawerFragment.class.getSimpleName();

    /**
     * Remember the position of the selected item.
     */
    private static final String STATE_SELECTED_POSITION = "selected_navigation_drawer_position";

    /**
     * A pointer to the current callbacks instance (the Activity).
     */
    private NavigationDrawerCallbacks mCallbacks;

    /**
     * Helper component that ties the action bar to the navigation drawer.
     */
    private ActionBarDrawerToggle mDrawerToggle;

    private DrawerLayout mDrawerLayout;
    private View mDrawerListView;
    private View mFragmentContainerView;

    private int mCurrentSelectedPosition = 0;
    private boolean mFromSavedInstanceState;

    @Bind(R.id.ll_UnLogined)
    LinearLayout ll_UnLogined;

    @Bind(R.id.im_WeiXin_Slidmenu)
    ImageView im_WeiXin_Slidmenu;

    @Bind(R.id.im_QQ_Slidmenu)
    ImageView im_QQ_Slidmenu;

    @Bind(R.id.im_XinLang_Slidmenu)
    ImageView im_XinLang_Slidmenu;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null) {
            mCurrentSelectedPosition = savedInstanceState
                    .getInt(STATE_SELECTED_POSITION);
            mFromSavedInstanceState = true;
        }

        selectItem(mCurrentSelectedPosition);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
            mDrawerListView = inflater.inflate(R.layout.slidmenu_unlogin,
                    container, false);
        mDrawerListView.setOnClickListener(this);
        ButterKnife.bind(this, mDrawerListView);
        initView(mDrawerListView);
//        initData();
        return mDrawerListView;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.ll_UnLogined:
                getActivity().startActivity(new Intent(getActivity(), LoginActivity.class));
                break;
            case R.id.im_WeiXin_Slidmenu:
                getActivity().startActivity(new Intent(getActivity(), LoginActivity.class));

                break;
            case R.id.im_QQ_Slidmenu:
                getActivity().startActivity(new Intent(getActivity(), LoginActivity.class));
                //qqLogin();
                break;
            case R.id.im_XinLang_Slidmenu:
                getActivity().startActivity(new Intent(getActivity(), LoginActivity.class));

                break;
            default:
                break;

        }
//        mDrawerLayout.postDelayed(new Runnable() {
//
//            @Override
//            public void run() {
//                mDrawerLayout.closeDrawers();
//            }
//        }, 800);
    }

    private void switchTheme() {
//        if (AppContext.getNightModeSwitch()) {
//            AppContext.setNightModeSwitch(false);
//        } else {
//            AppContext.setNightModeSwitch(true);
//        }
//
//        if (AppContext.getNightModeSwitch()) {
//            getActivity().setTheme(R.style.AppBaseTheme_Night);
//        } else {
//            getActivity().setTheme(R.style.AppBaseTheme_Light);
//        }
//
//        getActivity().recreate();
    }

    @Override
    public void initView(View view) {

        TextView night = (TextView) view.findViewById(R.id.tv_night);
        ll_UnLogined.setOnClickListener(this);
        im_WeiXin_Slidmenu.setOnClickListener(this);
        im_QQ_Slidmenu.setOnClickListener(this);
        im_XinLang_Slidmenu.setOnClickListener(this);
    }

//    @Override
    public void initData() {
    }

    public boolean isDrawerOpen() {
        return mDrawerLayout != null
                && mDrawerLayout.isDrawerOpen(mFragmentContainerView);
    }

    /**
     * Users of this fragment must call this method to set up the navigation
     * drawer interactions.
     *
     * @param fragmentId   The android:id of this fragment in its activity's layout.
     * @param drawerLayout The DrawerLayout containing this fragment's UI.
     */
    public void setUp(int fragmentId, DrawerLayout drawerLayout) {
        mFragmentContainerView = getActivity().findViewById(fragmentId);
        mDrawerLayout = drawerLayout;

        // set a custom shadow that overlays the main content when the drawer
        // opens
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow,
                GravityCompat.START);
        // set up the drawer's list view with items and click listener

//        ActionBar actionBar = getActionBar();
//        actionBar.setDisplayHomeAsUpEnabled(true);
//        actionBar.setHomeButtonEnabled(true);

        mDrawerToggle = new ActionBarDrawerToggle(getActivity(), mDrawerLayout,
                null, R.string.navigation_drawer_open,
                R.string.navigation_drawer_close) {

            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                getActivity().invalidateOptionsMenu();
            }

            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                getActivity().invalidateOptionsMenu();
            }
        };

        mDrawerLayout.post(new Runnable() {
            @Override
            public void run() {
                mDrawerToggle.syncState();
            }
        });

        mDrawerLayout.setDrawerListener(mDrawerToggle);
    }

    public void openDrawerMenu() {
        mDrawerLayout.openDrawer(mFragmentContainerView);
    }

    private void selectItem(int position) {
        mCurrentSelectedPosition = position;
        if (mDrawerLayout != null) {
            mDrawerLayout.closeDrawer(mFragmentContainerView);
        }
        if (mCallbacks != null) {
            mCallbacks.onNavigationDrawerItemSelected(position);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mCallbacks = (NavigationDrawerCallbacks) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(
                    "Activity must implement NavigationDrawerCallbacks.");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(STATE_SELECTED_POSITION, mCurrentSelectedPosition);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private ActionBar getActionBar() {
        return ((AppCompatActivity) getActivity()).getSupportActionBar();
    }

    public interface NavigationDrawerCallbacks {
        void onNavigationDrawerItemSelected(int position);
    }


    /**
     * qq登录
     */
    public void qqLogin() {
//        loginLisener = new BaseActivity.BaseUIlisener() {
//            @Override
//            public void onComplete(Object o) {
//                Log.d("o", o.toString());
//                JSONObject obj = (JSONObject) o;
//                try {
//                    access_token = obj.getString("access_token");
//                    getSharedPreferences(StaticBean.USER_INFO, Context.MODE_PRIVATE).edit().putString("access_token", access_token).commit();
//
//                    openid = obj.getString("openid");
//                    getSharedPreferences(StaticBean.USER_INFO, Context.MODE_PRIVATE).edit().putString("openid", openid).commit();
//
//                    expires_in = obj.getString("expires_in");
//                    //第一次登录后有效期
//                    long l = System.currentTimeMillis() + Long.parseLong(expires_in) * 1000;
//                    //有效期还剩多少
//                    long g = (l - System.currentTimeMillis()) / 1000;
//                    expires_in = g + "";
//                    getSharedPreferences(StaticBean.USER_INFO, Context.MODE_PRIVATE).edit().putString("expires_in", expires_in).commit();
//
//                    ToastUtil.shortshow(MainActivity.this, "登录成功进入主页面");
//                    getSharedPreferences(StaticBean.USER_INFO, Context.MODE_PRIVATE).edit().putBoolean("isLogined", true).commit();
//                    getSession_idByQQ();
//
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//            }
//        };
//        getmTencent().login(this, "all", loginLisener);
    }
}
