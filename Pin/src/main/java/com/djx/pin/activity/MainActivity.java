package com.djx.pin.activity;

import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.hardware.SensorManager;
import android.location.LocationManager;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.djx.pin.R;
import com.djx.pin.application.PinApplication;
import com.djx.pin.base.OldBaseActivity;
import com.djx.pin.beans.EventBeans;
import com.djx.pin.beans.StaticBean;
import com.djx.pin.beans.UserInfo;
import com.djx.pin.business.AppConstants;
import com.djx.pin.fragment.HelperMapFragment;
import com.djx.pin.fragment.MessageFragment;
import com.djx.pin.improve.helppeople.HelpPeopleFragment;
import com.djx.pin.improve.positiveenergy.view.PositiveEnergyFragment;
import com.djx.pin.serverapiconfig.ServerAPIConfig;
import com.djx.pin.ui.CommonDialog;
import com.djx.pin.ui.NavigationDrawerFragment;
import com.djx.pin.ui.NavigationDrawerLoginedFragment;
import com.djx.pin.utils.AndroidAsyncHttp;
import com.djx.pin.utils.LogUtil;
import com.djx.pin.utils.ScreenUtils;
import com.djx.pin.utils.ToastUtil;
import com.djx.pin.utils.myutils.ConstantUtils;
import com.djx.pin.widget.BadgeView;
import com.djx.pin.widget.adapters.MViewPager;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.pgyersdk.update.PgyUpdateManager;
import com.squareup.seismic.ShakeDetector;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;
import de.greenrobot.event.EventBus;
import de.greenrobot.event.Subscribe;
import de.greenrobot.event.ThreadMode;
import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;

import static com.djx.pin.beans.UserInfo.getRongPortrait;

public class MainActivity extends OldBaseActivity implements View.OnClickListener ,NavigationDrawerFragment.NavigationDrawerCallbacks,NavigationDrawerLoginedFragment.NavigationDrawerLoginedCallbacks{

    protected final static String TAG = MainActivity.class.getSimpleName();
    private boolean GPSNeverShow = true;

    private NavigationDrawerFragment mNavigationDrawerFragment;
    private NavigationDrawerLoginedFragment mNavigationDrawerLoginedFragment;
    boolean isLogined;
    SharedPreferences sp;
    private BadgeView mBvNotice;
    private static AsyncHttpResponseHandler res4Portrait;//异步获取用户头像ID
    private MViewPager viewPager;
    private RadioGroup radioGroup;
    private ArrayList<Fragment> fragments;

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            sp = getSharedPreferences(StaticBean.USER_INFO, MODE_PRIVATE);
            isLogined = sp.getBoolean("isLogined", false);
            if (intent.getAction().equals(AppConstants.INTENT_ACTION_LOGIN)) {
                sp.edit().putBoolean("isLogined",true);
                String sos_id = UserInfo.getSOSId(MainActivity.this);
                if (sos_id != null && sos_id.length() != 0) {
                    Button.OnClickListener positiveListener;
                    switch (UserInfo.getSOSStatus(getApplicationContext())) {
                        //发布状态
                        case 0:
                            positiveListener = new Button.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    startActivity(SOSSafeActivity.class);
                                }
                            };
                            CommonDialog.show(MainActivity.this, "确定", "取消", getString(R.string.dialog_sos_ongoing), positiveListener);
                            break;
                        //我安全了状态
                        case 1:
                            positiveListener = new Button.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Bundle bundle_SOS = new Bundle();
                                    bundle_SOS.putString("id", UserInfo.getSOSId(getApplicationContext()));
                                    startActivity(MyHelperSOSDetailActivity.class, bundle_SOS);
                                }
                            };
                            CommonDialog.show(MainActivity.this, "确定", "取消", getString(R.string.dialog_already_safe_sos_ongoing), positiveListener);
                            break;
                        default:
                            break;
                    }
                }

                drawerLayoutHideFragment(mNavigationDrawerFragment);
                initUnreadCountListener();
                Intent sendIntent = new Intent(AppConstants.INTENT_ACTION_LOGIN_TOMAP);
                sendBroadcast(sendIntent);
            } else if (intent.getAction().equals(AppConstants.INTENT_ACTION_OPENDRAWER)) {
                //mDrawerLayout.openDrawer(mFragmentContainerView);
                if(isLogined){//如果已经登录
                    mDrawerLayout.openDrawer(mFragmentContainerView);
                    drawerLayoutHideFragment(mNavigationDrawerFragment);//去掉第三方登录
                }else {//没有登录
                    startActivity(LoginActivity.class);
                }
            } else if (intent.getAction().equals(AppConstants.INTENT_ACTION_UNREAD_MSGCOUNT)) {
                mBvNotice.setText("1");
                mBvNotice.setVisibility(View.VISIBLE);
            }

        }
    };
    private RadioButton rb_01;
    private RadioButton rb_00;
    private RadioButton rb_02;
    private RadioButton rb_03;


    private void initUnreadCountListener() {
        RongIM.getInstance().setOnReceiveUnreadCountChangedListener(mCountListener, Conversation.ConversationType.PRIVATE);
    }

    public RongIM.OnReceiveUnreadCountChangedListener mCountListener = new RongIM.OnReceiveUnreadCountChangedListener() {
        @Override
        public void onMessageIncreased(int count) {
            if (count == 0) {
                mBvNotice.setText("" + count);
                mBvNotice.setVisibility(View.GONE);
            } else if (count > 0 && count < 100) {
                mBvNotice.setText("" + count);
                mBvNotice.setVisibility(View.VISIBLE);
            } else {
                mBvNotice.setText("99+");
                mBvNotice.setVisibility(View.VISIBLE);
            }
        }
    };

    private ShakeDetector shakeDetector;
    private SensorManager sensorManager;

    @Subscribe(threadMode = ThreadMode.MainThread)
    public void onEvent(EventBeans eventBeans){
        if(eventBeans.style == ConstantUtils.OPEN_DRAW_LAYOUT){
            mDrawerLayout.closeDrawer(Gravity.LEFT);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LogUtil.e("-------------mianactivity---------onCreate()");
        setContentView(R.layout.activity_main_new);
        EventBus.getDefault().register(this);
        PgyUpdateManager.register(this);//蒲公英自动更新
        sp = getSharedPreferences(StaticBean.USER_INFO, MODE_PRIVATE);
        isLogined = sp.getBoolean("isLogined", false);
        initView();
        initEvent();
        ((AudioManager)getSystemService(AUDIO_SERVICE)).registerMediaButtonEventReceiver(new ComponentName(this,MusicIntentReceiver.class));
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        shakeDetector = new ShakeDetector(new ShakeDetector.Listener() {
            @Override
            public void hearShake() {
                shakeDetector.stop();
                Intent sendIntent = new Intent(AppConstants.INTENT_ACTION_SOS);
                sendIntent.putExtra("sossource", 3);
                sendBroadcast(sendIntent);
            }
        });

        GPSNeverShow = sp.getBoolean("GPSNeverShow",false);

    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        handleIntent(intent);
    }

    /**
     * 处理传进来的intent
     */
    private void handleIntent(Intent intent) {
        if (intent == null)
            return;
    }

    private View mFragmentContainerView;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;

    public void initEvent() {
        IntentFilter filter = new IntentFilter(AppConstants.INTENT_ACTION_LOGIN);
        filter.addAction(AppConstants.INTENT_ACTION_LOGOUT);
        filter.addAction(AppConstants.INTENT_ACTION_OPENDRAWER);
        filter.addAction(AppConstants.INTENT_ACTION_MODIFY_USERINFO);
        registerReceiver(mReceiver, filter);
    }
    public void initView() {
        mFragmentContainerView = findViewById(R.id.navigation_drawer_layout);
        ViewGroup.LayoutParams params = mFragmentContainerView.getLayoutParams();
        params.width= ScreenUtils.getScreenWidth(this)*2/3;
        mFragmentContainerView.setLayoutParams(params);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow,
                GravityCompat.START);
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
                null, R.string.navigation_drawer_open,
                R.string.navigation_drawer_close) {

            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                invalidateOptionsMenu();
            }

            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                invalidateOptionsMenu();
            }
        };

        mDrawerLayout.post(new Runnable() {
            @Override
            public void run() {
                mDrawerToggle.syncState();
            }
        });

        mDrawerLayout.addDrawerListener(mDrawerToggle);

        mNavigationDrawerLoginedFragment = (NavigationDrawerLoginedFragment) getSupportFragmentManager()
                .findFragmentById(R.id.navigation_drawer_logined);
        mNavigationDrawerFragment = (NavigationDrawerFragment) getSupportFragmentManager()
                .findFragmentById(R.id.navigation_drawer);
        drawerLayoutHideFragment(mNavigationDrawerFragment);//去掉第三方登录

        mBvNotice = ((BadgeView) findViewById(R.id.badgeView));
        mBvNotice.setVisibility(View.GONE);
        mBvNotice.setBadgePosition(BadgeView.POSITION_TOP_RIGHT);
        viewPager = ((MViewPager) findViewById(R.id.main_pagerId));
        radioGroup = ((RadioGroup) findViewById(R.id.rg));
        rb_00 = ((RadioButton) findViewById(R.id.rb_0));
        rb_01 = ((RadioButton) findViewById(R.id.rb_1));
        rb_01.setTextColor(getResources().getColor(R.color.blue));
        rb_02 = ((RadioButton) findViewById(R.id.rb_2));
        rb_03 = ((RadioButton) findViewById(R.id.rb_3));

        viewPager.setOffscreenPageLimit(4);
        fragments = new ArrayList<>();
        fragments.add(new PositiveEnergyFragment());
        fragments.add(new HelperMapFragment());
        fragments.add(new HelpPeopleFragment());
        fragments.add(new MessageFragment());

        viewPager.setAdapter(new FragmentPagerAdapter(this.getSupportFragmentManager()) {

            @Override
            public int getCount() {
                return fragments.size();
            }
            @Override
            public Fragment getItem(int arg0) {
                return fragments.get(arg0);
            }
        });
        viewPager.setCurrentItem(1,false);

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                int current=0;
                switch(checkedId)
                {
                    case R.id.rb_0:
                        current = 0 ;
                        rb_00.setTextColor(getResources().getColor(R.color.blue));
                        rb_01.setTextColor(getResources().getColor(R.color.text_color_light_black));
                        rb_02.setTextColor(getResources().getColor(R.color.text_color_light_black));
                        rb_03.setTextColor(getResources().getColor(R.color.text_color_light_black));
                        break ;
                    case R.id.rb_1:
                        current = 1 ;
                        rb_00.setTextColor(getResources().getColor(R.color.text_color_light_black));
                        rb_01.setTextColor(getResources().getColor(R.color.blue));
                        rb_02.setTextColor(getResources().getColor(R.color.text_color_light_black));
                        rb_03.setTextColor(getResources().getColor(R.color.text_color_light_black));
                        break;
                    case R.id.rb_2:
                        current = 2 ;
                        rb_00.setTextColor(getResources().getColor(R.color.text_color_light_black));
                        rb_01.setTextColor(getResources().getColor(R.color.text_color_light_black));
                        rb_02.setTextColor(getResources().getColor(R.color.blue));
                        rb_03.setTextColor(getResources().getColor(R.color.text_color_light_black));
                        break;
                    case R.id.rb_3:
                        current = 3 ;
                        rb_00.setTextColor(getResources().getColor(R.color.text_color_light_black));
                        rb_01.setTextColor(getResources().getColor(R.color.text_color_light_black));
                        rb_02.setTextColor(getResources().getColor(R.color.text_color_light_black));
                        rb_03.setTextColor(getResources().getColor(R.color.blue));
                        break ;
                }
                if(viewPager.getCurrentItem() != current)
                {
                    viewPager.setCurrentItem(current,false);
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LogUtil.e("-------------mianactivity---------onDestroy()");
        unregisterReceiver(mReceiver);
        PgyUpdateManager.unregister();
        PinApplication.getMyApp().stopSelfUpLoadService();
        PinApplication.getMyApp().stopSOSUpLoadService();
        EventBus.getDefault().unregister(this);
        //删除缓存文件
        PinApplication.getMyApp().getMySQLLiteOpenHelper().deleteAll();
        //deleteFile(getCacheDir());
    }
    public void deleteFile(File file) {
        //File file = new File(path);
        if (file.exists()) { // 判断文件是否存在
            if (file.isFile()) { // 判断是否是文件
                file.delete(); // delete()方法 你应该知道 是删除的意思;
            } else if (file.isDirectory()) { // 否则如果它是一个目录
                File files[] = file.listFiles(); // 声明目录下所有的文件 files[];
                for (int i = 0; i < files.length; i++) { // 遍历目录下所有的文件
                    this.deleteFile(files[i]); // 把每个文件 用这个方法进行迭代
                }
            }
            file.delete();
        } else {
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        LogUtil.e("-------------mianactivity---------onStart()");
        int is_show_location = sp.getInt("is_show_location", 0);
        if(isLogined && is_show_location == 1){
            PinApplication.getMyApp().startSelfUpLoadService();
        }else {
            PinApplication.getMyApp().stopSelfUpLoadService();
        }
        if (isLogined) {
            String session_id = sp.getString("session_id", "");
            //rongLogin(session_id);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            default:
                break;
        }
    }

    /**
     * 监听返回--是否退出程序
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
            if (event.getRepeatCount() == 0) {
                shakeDetector.start(sensorManager);
            }
            return true;
        } else {
            return super.onKeyDown(keyCode, event);
        }
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
            shakeDetector.stop();
            return true;
        } else {
            return super.onKeyUp(keyCode, event);
        }
    }

    public void drawerLayoutHideFragment(Fragment hideFragment) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        if (hideFragment == mNavigationDrawerFragment) {
            ft.hide(hideFragment);
            ft.show(mNavigationDrawerLoginedFragment);
        } else {
            ft.hide(hideFragment);
        }
        ft.commitAllowingStateLoss();
    }

    @Override
    protected void onStop() {
        super.onStop();
        LogUtil.e("-------------mianactivity---------onStop()");
    }

    @Override
    protected void onResume() {
        super.onResume();
        LogUtil.e("-------------mianactivity---------onResume()");
    }

    @Override
    protected void onPause() {
        super.onPause();
        LogUtil.e("-------------mianactivity---------onPause()");
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        LogUtil.e("-------------mianactivity---------onPostCreat()");

        //检查GPS是否打开
        LocationManager locationManager =
            ((LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE));
        boolean gpsisopen = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (!gpsisopen && GPSNeverShow == false) {
            Button.OnClickListener positiveListener = new Button.OnClickListener() {
                @Override
                public void onClick(View view) {

                    sp.edit().putBoolean("GPSNeverShow",GPSNeverShow).commit();
                    Intent intent = new Intent();
                    intent.setAction(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                     try {
                         getApplicationContext().startActivity(intent);
                     } catch(ActivityNotFoundException ex) {
                         intent.setAction(Settings.ACTION_SETTINGS);
                         try {
                             getApplicationContext().startActivity(intent);
                         } catch (Exception e) {
                             e.printStackTrace();
                         }
                     }
                }
            };
            CommonDialog.show2(MainActivity.this, "确定", "取消", getString(R.string.dialog_opengps_hint), positiveListener, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    sp.edit().putBoolean("GPSNeverShow",GPSNeverShow).commit();
                    CommonDialog.dismiss(MainActivity.this);
                }
            },new CommonDialog.CheckBoxCheckedListener() {

                @Override
                public void OnCheckChanged(boolean isChecked) {
                    if(isChecked){//已选
                        GPSNeverShow = true;
                    }else{
                        GPSNeverShow = false;
                    }
                }
            });
        }

    }

    //FIXME:重复代码  待重构
    /**
     * 登陆融云,默认先刷新融云Token,然后在登陆
     * @param session_id
     */
    public void rongLogin(String session_id) {
        Log.e("message","----mainactivity-----rongLogin()----------");
        RongIM.getInstance().getRongIMClient().logout();
        AsyncHttpResponseHandler res = new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                String strJson = new String(bytes);
                try {
                    JSONObject obj = new JSONObject(strJson);
                    if (0 == obj.getInt("code")) {
                        obj=obj.getJSONObject("result");
                        sp.edit().putString("rongyun_token",obj.getString("rongyun_token")).commit();
                        connect(obj.getString("rongyun_token"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
            }
        };

        RequestParams params = new RequestParams();
        params.put("session_id", session_id);
        AndroidAsyncHttp.post(ServerAPIConfig.RefreshRongToken, params, res);
    }
    /**
     * 建立与融云服务器的连接
     *
     * @param token
     */
    private void connect(String token) {
        Log.e("message","----mainactivity-----connect()----------");
        if (getApplicationInfo().packageName.equals(PinApplication.getCurProcessName(getApplicationContext()))) {

            /**
             * IMKit SDK调用第二步,建立与服务器的连接
             */
        LogUtil.e("--------------------MainActivity----------RongIM.connect()-------------------------------------------------");
            RongIM.connect(token, new RongIMClient.ConnectCallback() {
                /**
                 * Token 错误，在线上环境下主要是因为 Token 已经过期，您需要向 App Server 重新请求一个新的 Token
                 */
                @Override
                public void onTokenIncorrect() {
                    LogUtil.e("--融云登陆失败:Token 已经过期");
                }

                /**
                 * 连接融云成功
                 * @param userid 当前 token
                 */
                @Override
                public void onSuccess(String userid) {
                    LogUtil.e("--融云登陆成功");
                    setRongyunUserProvider();
                    Intent intent = new Intent(AppConstants.INTENT_ACTION_LOGIN);
                    sendBroadcast(intent);
                }
                @Override
                public void onError(RongIMClient.ErrorCode errorCode) {
                    LogUtil.e("--融云登陆错误");
                }
            });
        }
    }

    private void setRongyunUserProvider() {
        Log.e("message","----mainactivity-----setRongyunUserProvider()----------");
        //设置融云信息提供者
        RongIM.setUserInfoProvider(new RongIM.UserInfoProvider() {
            @Override
            public io.rong.imlib.model.UserInfo getUserInfo(String s) {
                RequestParams params=new RequestParams();
                params.put("user_id",s);
                AndroidAsyncHttp.get(ServerAPIConfig.Get_UserInfo,params,res4Portrait);
                return null;
            }
        }, true);

        //获取用户头像ID
        res4Portrait = new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                String strJson = new String(bytes);
                try {
                    JSONObject obj=new JSONObject(strJson);
                    if(0!=obj.getInt("code")){
                        errorCode(obj.getInt("code"));
                        return;
                    }
                    Log.e("message","----mainactivity-----获取用户头像ID----------");
                    obj=obj.getJSONObject("result");
                    String portrait_id=obj.getString("portrait");
                    //如果用户头像字段异常表明用户没有设置头像,直接return;
                    if(null==portrait_id||portrait_id.length()==0){
                        RongIM.getInstance().refreshUserInfoCache(new io.rong.imlib.model.UserInfo(obj.getString("user_id"), obj.getString("nickname"), Uri.parse("")));
                        //知messagectivity刷新
                        LogUtil.e("================知messagectivity刷新=========================================");
                        Log.e("message","----mainactivity-----知messagectivity刷新----------");
                        //EventBus.getDefault().post(new EventBeans(ConstantUtils.RE_LOGIN));
                        return;
                    }
                    try {
                        getRongPortrait(getApplicationContext(), obj.getString("user_id"),obj.getString("nickname"),portrait_id,1);
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                ToastUtil.shortshow(getApplicationContext(),R.string.toast_error_net);
            }
        };
    }


    @Override
    public void onNavigationDrawerItemSelected(int position) {

    }

    @Override
    public void onNavigationDrawerLoginedItemSelected(int position) {

    }
}
