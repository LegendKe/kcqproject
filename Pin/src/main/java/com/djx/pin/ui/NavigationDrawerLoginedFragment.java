package com.djx.pin.ui;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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
import android.widget.LinearLayout;
import android.widget.TextView;

import com.djx.pin.R;
import com.djx.pin.activity.LoginActivity;
import com.djx.pin.activity.MyHelperActivity;
import com.djx.pin.activity.PurseActivity;
import com.djx.pin.activity.RewardActivity;
import com.djx.pin.activity.SettingActivity;
import com.djx.pin.activity.TeasingActivity;
import com.djx.pin.base.NewBaseFragment;
import com.djx.pin.beans.EventBeans;
import com.djx.pin.beans.StaticBean;
import com.djx.pin.beans.UserInfo;
import com.djx.pin.business.AppConstants;
import com.djx.pin.myview.CircleImageView;
import com.djx.pin.personal.ui.PersonalDataActivity;
import com.djx.pin.utils.LogUtil;
import com.djx.pin.utils.QiniuUtils;
import com.djx.pin.utils.myutils.ConstantUtils;

import butterknife.Bind;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.util.TextUtils;
import de.greenrobot.event.EventBus;

import static android.content.Context.MODE_PRIVATE;

/**
 * 侧滑菜单界面
 *
 * @author FireAnt（http://my.oschina.net/LittleDY）
 * @created 2014年9月25日 下午6:00:05
 */
public class NavigationDrawerLoginedFragment extends NewBaseFragment implements
        OnClickListener {
    protected final static String TAG = NavigationDrawerLoginedFragment.class.getSimpleName();

    /**
     * Remember the position of the selected item.
     */
    private static final String STATE_SELECTED_POSITION = "selected_navigation_drawer_position";
    /**
     * A pointer to the current callbacks instance (the Activity).
     */
    private NavigationDrawerLoginedCallbacks mCallbacks;

    /**
     * Helper component that ties the action bar to the navigation drawer.
     */
    private ActionBarDrawerToggle mDrawerToggle;

    private DrawerLayout mDrawerLayout;
    private View mDrawerListView;
    private View mFragmentContainerView;

    private int mCurrentSelectedPosition = 0;
    private boolean mFromSavedInstanceState;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            Log.i(TAG, "mHandler");
            switch (msg.what) {
                case 1:
                    updateData();
                    break;
            }
        }
    };


    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(AppConstants.INTENT_ACTION_LOGIN)) {
                Log.i(TAG, "action login");
//                drawerLayoutHideFragment(mNavigationDrawerFragment);
            } else if (intent.getAction().equals(AppConstants.INTENT_ACTION_MODIFY_USERINFO)) {
                Log.i(TAG, "modify userinfo");
                UserInfo.getUserInfo(getActivity(), mHandler);
            }
        }
    };

    @Bind(R.id.ll_Slidmenu_Myhelper)
    LinearLayout ll_Slidmenu_Myhelper;

    @Bind(R.id.ll_Slidmenu_Reward)
    LinearLayout ll_Slidmenu_Reward;

    @Bind(R.id.ll_Slidmenu_Purse)
    LinearLayout ll_Slidmenu_Purse;

    @Bind(R.id.ll_Slidmenu_Teasing)
    LinearLayout ll_Slidmenu_Teasing;

    @Bind(R.id.tv_Slidmenu_Set)
    TextView tv_Slidmenu_Set;

    @Bind(R.id.ll_slidmenu_Userinfo)
    LinearLayout ll_slidmenu_Userinfo;

    @Bind(R.id.cmg_Slid_MA)
    CircleImageView cmg_Slid_MA;

    @Bind(R.id.tv_NickName_MA)
    TextView tv_NickName_MA;

    @Bind(R.id.tv_authentication)
    TextView tv_authentication;
    @Bind(R.id.tv_credit)
    TextView tv_credit;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        LogUtil.e("-----------------onCreateView()-------------");
            mDrawerListView = inflater.inflate(R.layout.slidmenu_logined,
                    container, false);
        initView(mDrawerListView);

        initEvent();
        return mDrawerListView;
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            //登录成功后菜单监听
            case R.id.ll_slidmenu_Userinfo:
                intent = new Intent(getActivity(), PersonalDataActivity.class);
                getActivity().startActivity(intent);
                break;
            case R.id.ll_Slidmenu_Myhelper:
                intent = new Intent(getActivity(), MyHelperActivity.class);
                getActivity().startActivity(intent);
                break;
            case R.id.ll_Slidmenu_Reward:
                intent = new Intent(getActivity(), RewardActivity.class);
                getActivity().startActivity(intent);
                break;
            case R.id.ll_Slidmenu_Purse:
                intent = new Intent(getActivity(), PurseActivity.class);
                getActivity().startActivity(intent);
                break;
            case R.id.ll_Slidmenu_Teasing:
                intent = new Intent(getActivity(), TeasingActivity.class);
                getActivity().startActivity(intent);
                break;
            case R.id.tv_Slidmenu_Set:
                intent = new Intent(getActivity(), SettingActivity.class);
                getActivity().startActivity(intent);
                EventBus.getDefault().post(new EventBeans(ConstantUtils.OPEN_DRAW_LAYOUT));//关闭抽屉
                break;
            case R.id.ll_UnLogined:
                intent = new Intent(getActivity(), LoginActivity.class);
                getActivity().startActivity(intent);
                break;
        }
    }
    @Override
    public void initView(View view) {
        Log.i(TAG, "initView");
        LogUtil.e("-----------------initView()-------------");
        ButterKnife.bind(this, view);

        updateData();
    }

    public void initEvent() {
        IntentFilter filter = new IntentFilter(AppConstants.INTENT_ACTION_LOGIN);
        filter.addAction(AppConstants.INTENT_ACTION_MODIFY_USERINFO);
        getActivity().registerReceiver(mReceiver, filter);

        //登录成功后控件监听事件
        ll_Slidmenu_Myhelper.setOnClickListener(this);
        ll_Slidmenu_Reward.setOnClickListener(this);

        ll_Slidmenu_Purse.setOnClickListener(this);
        ll_Slidmenu_Teasing.setOnClickListener(this);
        tv_Slidmenu_Set.setOnClickListener(this);
        ll_slidmenu_Userinfo.setOnClickListener(this);
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        LogUtil.e("-----------------onHiddenChanged()-------------");
        if (hidden) {
            return;
        }
        updateData();
    }

    @Override
    public void onStart() {
        super.onStart();
        LogUtil.e("-----------------onStart()-------------");
        updateData();
    }

    /**
     * 登陆后初始化菜单内容
     */
    public void updateData() {
        SharedPreferences sharedPreferences1 = getActivity().getSharedPreferences(StaticBean.USER_INFO, Context.MODE_PRIVATE);
        boolean isLogined = getActivity().getSharedPreferences(StaticBean.USER_INFO, Context.MODE_PRIVATE).getBoolean("isLogined", false);
        if (!isLogined) {
            return;
        }
        String nickname = UserInfo.getNickname(getActivity());
        String portrait = sharedPreferences1.getString("portrait",null);
        LogUtil.e("==========navigation======portrait===="+portrait);
        if (!TextUtils.isEmpty(portrait)) {
            QiniuUtils.setAvatarByIdFrom7Niu(getContext(),cmg_Slid_MA,portrait);
        } else if(TextUtils.isEmpty(portrait)){
            LogUtil.e("==========navigation======cmg_Slid_MA.setImageResource=");
            cmg_Slid_MA.setImageResource(R.mipmap.ic_defualtavater);
        }

        int is_auth = getActivity().getSharedPreferences(StaticBean.USER_INFO, Context.MODE_PRIVATE).getInt("is_auth", 0);
        String real_name = getActivity().getSharedPreferences(StaticBean.USER_INFO, Context.MODE_PRIVATE).getString("real_name", null);
        String id_card = getActivity().getSharedPreferences(StaticBean.USER_INFO, Context.MODE_PRIVATE).getString("id_card", null);

        String zhiMaOpenID = UserInfo.getZhiMaOpenID(getActivity());
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(StaticBean.USER_INFO, MODE_PRIVATE);
        int credit = sharedPreferences.getInt("credit", -1);

        if (is_auth == 0) {
            if (!TextUtils.isEmpty(real_name) && !TextUtils.isEmpty(id_card)) {
                tv_authentication.setText("认证  审核中");
            } else{
                tv_authentication.setText("认证  未实名");
            }
        } else if (is_auth == 2) {
            tv_authentication.setText("认证  未通过");
        } else if (is_auth == 1){
            tv_authentication.setText("认证  已实名");
        }
        if (!TextUtils.isEmpty(zhiMaOpenID)) {
            if(credit == -1){
                tv_credit.setText("信用  "+"未设置");
            }else {
                tv_credit.setText("信用  "+credit);
            }
        }else {
            tv_credit.setText("信用  "+"未设置");
        }
        if (nickname == null || nickname.equals("")) {
            tv_NickName_MA.setText("未知");
        } else {
            tv_NickName_MA.setText(nickname);
        }
        getActivity().getSharedPreferences(StaticBean.KEY, Context.MODE_PRIVATE).edit().putBoolean("is_DataChanged", false).commit();
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

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mCallbacks = (NavigationDrawerLoginedCallbacks) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(
                    "Activity must implement NavigationDrawerLoginedCallbacks.");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;
        getActivity().unregisterReceiver(mReceiver);
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

    public interface NavigationDrawerLoginedCallbacks {
        void onNavigationDrawerLoginedItemSelected(int position);
    }
}
