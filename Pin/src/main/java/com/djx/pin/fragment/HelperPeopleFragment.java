package com.djx.pin.fragment;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.djx.pin.R;
import com.djx.pin.activity.LookOthersMassageActivity;
import com.djx.pin.activity.SOSDetailActivity;
import com.djx.pin.activity.SOSNavigationActivity;
import com.djx.pin.adapter.HelperPeopleAdapter;
import com.djx.pin.application.PinApplication;
import com.djx.pin.improve.detail.OrderDetailActivity;
import com.djx.pin.beans.EventBeans;
import com.djx.pin.beans.HelperPeopleInfo;
import com.djx.pin.beans.SOSHelpInfo;
import com.djx.pin.beans.SOSInfo;
import com.djx.pin.beans.StaticBean;
import com.djx.pin.beans.UserInfo;
import com.djx.pin.business.AppConstants;
import com.djx.pin.serverapiconfig.ServerAPIConfig;
import com.djx.pin.utils.AndroidAsyncHttp;
import com.djx.pin.utils.LogUtil;
import com.djx.pin.utils.ToastUtil;
import com.djx.pin.utils.myutils.ConstantUtils;
import com.djx.pin.utils.myutils.LogicUtils;
import com.google.gson.Gson;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;
import de.greenrobot.event.EventBus;
import de.greenrobot.event.Subscribe;
import de.greenrobot.event.ThreadMode;

/**
 * Created by 陈刘磊 (代号：姜饼（GingerBread）) on 2016/5/10.
 */
public class HelperPeopleFragment extends BaseFragment implements View.OnClickListener, AdapterView.OnItemClickListener, HelperPeopleAdapter.AvatarLisenner, RadioGroup.OnCheckedChangeListener {
    protected final static String TAG = HelperPeopleFragment.class.getSimpleName();

    //view和控件
    private View view;
    private LinearLayout ll_ordertype, ll_order_intelligent, ll_order_distance, ll_order_all, ll_order_price;
    private TextView tv_order_intelligent, tv_order_distance, tv_order_all, tv_order_price;
    private ImageView iv_order_intelligent, iv_order_distance, iv_order_price,ll_User_HelperPeopleFragment,ll_Search_HelperPeopleFragment, ll_AddHelper_HelperPeopleFragment;
    private PullToRefreshListView lv_HelperPeople;
    private HelperPeopleAdapter adapter;

    private Dialog dialog_OrderIntelligent, dialog_OrderPrice, dialog_OrderDistance, dialog_SOSReceive;
    private View dialogView_OrderIntelligent, dialogView_OrderPrice, dialogView_OrderDistance, dilogView_SOSReceive;
    private LayoutInflater inflater;
    private RadioGroup rg_intelligent, rg_distance;
    private RadioButton rb_intelligent, rb_realtime_hottest, rb_realtime_newest, rb_near, rb_3km, rb_5km, rb_10km, rb_50km;

    private EditText et_min_price, et_max_price;
    Button bt_confirm_dialog_order_price, bt_confirm_dialog_sos_receive, bt_cancle_dialog_sos_receive;
    int filter_price_min = 1000,//筛选条件:最低价格
            filter_price_max = 0,//筛选条件:最高价格
            filter_distance = 0,//筛选条件:距离
            filter_order = 0;//筛选条件:排序规则
    int page_index = 0,//分页:页码
            page_index_sos = 0,//sos分页页码
            page_size = 10;//分页:每页的数量
    int color_blue = 0XFF1295F7,//蓝色
            color_black = 0XFF3D3D3D;//黑色
    boolean isRefreshing = false;//是否正在刷新,默认不在刷新
    SharedPreferences sp;
    private RadioButton rb_realtime_descend;
    private RadioButton rb_realtime_ascend;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private PinApplication myApp;
    private boolean isFirstLocation = true;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.helperpeoplefragment, null);
        }
        initView();
        myApp = PinApplication.getMyApp();
        EventBus.getDefault().register(this);
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MainThread)
    public void onEvent(EventBeans eventBeans){
        if(eventBeans.style == 1){//表示已更新一次定位
            if(isFirstLocation){
                isFirstLocation = false;
                page_index = 0;
                adapter.clear();
                getSOSData(0);
                getHelpData();
                initEvent();
            }
        }
    }

    /**
     * 检查用户输入的信息是否正确
     *
     * @return 正确返回ture, 反之false
     */
    public boolean checkUserInfo() {

        /**检查最小金额是否为空*/
        if (null == et_min_price.getText() || et_min_price.getText().toString().length() == 0) {
            ToastUtil.shortshow(getContext(), R.string.toast_non_min_price);
            LogUtil.e(getContext(), "最小金额为空");
            return false;
        }

        /**检查最大金额是否为空*/
        if (null == et_max_price.getText() || et_max_price.getText().toString().length() == 0) {
            ToastUtil.shortshow(getContext(), R.string.toast_non_max_price);
            LogUtil.e(getContext(), "最大金额为空");
            return false;
        }

        /**检查最小金额是否大于1000*/
        if (1000 >= Integer.parseInt(et_min_price.getText().toString())) {
            ToastUtil.shortshow(getContext(), R.string.toast_error_min_price);
            LogUtil.e(getContext(), "最小金额小于1000");
            return false;
        }

        /**检查最大金额是否大于最小金额*/
        if (Integer.parseInt(et_min_price.getText().toString()) > Integer.parseInt(et_max_price.getText().toString())) {
            ToastUtil.shortshow(getContext(), R.string.toast_error_max_price);
            LogUtil.e(getContext(), "最大金额不能小于最小金额");
            return false;
        }
        return true;
    }

    public void initDialog() {
        /**智能排序对话框*/
        if(dialog_OrderIntelligent == null){
            dialog_OrderIntelligent = new Dialog(getContext(), R.style.dialog_non_transparent);
        }
        dialog_OrderIntelligent.setContentView(dialogView_OrderIntelligent);
        WindowManager.LayoutParams layoutParams_OrderIntelligent = dialog_OrderIntelligent.getWindow().getAttributes();
        layoutParams_OrderIntelligent.gravity = Gravity.TOP;
        layoutParams_OrderIntelligent.width = WindowManager.LayoutParams.MATCH_PARENT;
        layoutParams_OrderIntelligent.y = ll_ordertype.getBottom();
        LogUtil.e("HelpPeopleFragment=" + ll_ordertype.getBottom());

        dialog_OrderIntelligent.getWindow().setAttributes(layoutParams_OrderIntelligent);

        /**金额选择对话框*/
        dialog_OrderPrice = new Dialog(getContext(), R.style.dialog_non_transparent);
        dialog_OrderPrice.setContentView(dialogView_OrderPrice);
        WindowManager.LayoutParams layoutParams_OrderPrice = dialog_OrderPrice.getWindow().getAttributes();
        layoutParams_OrderPrice.gravity = Gravity.TOP;
        layoutParams_OrderPrice.width = WindowManager.LayoutParams.MATCH_PARENT;
        layoutParams_OrderPrice.y = ll_ordertype.getBottom();
        dialog_OrderPrice.getWindow().setAttributes(layoutParams_OrderPrice);

        //距离选择对话框
        dialog_OrderDistance = new Dialog(getContext(), R.style.dialog_non_transparent);
        dialog_OrderDistance.setContentView(dialogView_OrderDistance);
        WindowManager.LayoutParams layoutParams_OrderDistance = dialog_OrderDistance.getWindow().getAttributes();
        layoutParams_OrderDistance.gravity = Gravity.TOP;
        layoutParams_OrderDistance.width = WindowManager.LayoutParams.MATCH_PARENT;
        layoutParams_OrderDistance.y = ll_ordertype.getBottom();
        dialog_OrderDistance.getWindow().setAttributes(layoutParams_OrderDistance);


        /**SOS接单警告对话框*/
        dialog_SOSReceive = new Dialog(getContext(), R.style.dialog_non_transparent);
        //dialog_SOSReceive.setContentView(dilogView_SOSReceive);
    }

    private void initEvent() {
       /* ll_User_HelperPeopleFragment.setOnClickListener(this);
        ll_Search_HelperPeopleFragment.setOnClickListener(this);*/
        ll_AddHelper_HelperPeopleFragment.setOnClickListener(this);
        lv_HelperPeople.setOnItemClickListener(this);
        lv_HelperPeople.setOnRefreshListener(refreshListener);
        ll_order_intelligent.setOnClickListener(this);
        ll_order_all.setOnClickListener(this);
        ll_order_distance.setOnClickListener(this);
        ll_order_price.setOnClickListener(this);
        rb_realtime_descend.setOnClickListener(this);
        rb_realtime_ascend.setOnClickListener(this);

        rg_intelligent.setOnCheckedChangeListener(this);
        rg_distance.setOnCheckedChangeListener(this);
        bt_confirm_dialog_order_price.setOnClickListener(this);
        bt_confirm_dialog_sos_receive.setOnClickListener(this);
        bt_cancle_dialog_sos_receive.setOnClickListener(this);


        rb_intelligent.setOnClickListener(this);
        rb_realtime_hottest.setOnClickListener(this);
        rb_realtime_newest.setOnClickListener(this);
        rb_near.setOnClickListener(this);
        rb_3km.setOnClickListener(this);
        rb_5km.setOnClickListener(this);
        rb_10km.setOnClickListener(this);
        rb_50km.setOnClickListener(this);
    }

    private void initView() {
        sp = getContext().getSharedPreferences(StaticBean.USER_INFO, Context.MODE_PRIVATE);
        inflater = LayoutInflater.from(getContext());
        ll_ordertype = (LinearLayout) view.findViewById(R.id.ll_ordertype);

        ll_ordertype.post(new Runnable() {
            @Override
            public void run() {
                initDialog();
            }
        });
        ll_User_HelperPeopleFragment = (ImageView) view.findViewById(R.id.iv_user_action_bar);
        ll_order_intelligent = (LinearLayout) view.findViewById(R.id.ll_order_intelligent);
        ll_order_price = (LinearLayout) view.findViewById(R.id.ll_order_price);
        ll_order_distance = (LinearLayout) view.findViewById(R.id.ll_order_distance);
        ll_order_all = (LinearLayout) view.findViewById(R.id.ll_order_all);

        tv_order_intelligent = (TextView) view.findViewById(R.id.tv_order_intelligent);
        tv_order_distance = (TextView) view.findViewById(R.id.tv_order_distance);
        tv_order_all = (TextView) view.findViewById(R.id.tv_order_all);
        tv_order_price = (TextView) view.findViewById(R.id.tv_order_price);
        iv_order_intelligent = (ImageView) view.findViewById(R.id.iv_order_intelligent);
        iv_order_distance = (ImageView) view.findViewById(R.id.iv_order_distance);
        iv_order_price = (ImageView) view.findViewById(R.id.iv_order_price);

        /**智能排序对话框*/
        dialogView_OrderIntelligent = inflater.inflate(R.layout.layout_dialog_order_intelligent, null,false);
        rg_intelligent = (RadioGroup) dialogView_OrderIntelligent.findViewById(R.id.rg_intelligent);
        rb_intelligent = (RadioButton) dialogView_OrderIntelligent.findViewById(R.id.rb_intelligent);
        rb_realtime_hottest = (RadioButton) dialogView_OrderIntelligent.findViewById(R.id.rb_realtime_hottest);
        rb_realtime_newest = (RadioButton) dialogView_OrderIntelligent.findViewById(R.id.rb_realtime_newest);
        rb_realtime_ascend = (RadioButton) dialogView_OrderIntelligent.findViewById(R.id.rb_realtime_ascend);
        rb_realtime_descend = (RadioButton) dialogView_OrderIntelligent.findViewById(R.id.rb_realtime_descend);

        /**金额选择对话框*/
        dialogView_OrderPrice = inflater.inflate(R.layout.layout_dialog_order_price, null,false);
        et_min_price = (EditText) dialogView_OrderPrice.findViewById(R.id.et_min_price);
        et_max_price = (EditText) dialogView_OrderPrice.findViewById(R.id.et_max_price);
        bt_confirm_dialog_order_price = (Button) dialogView_OrderPrice.findViewById(R.id.bt_confirm_dialog_order_price);


        /**距离选择对话框*/
        dialogView_OrderDistance = inflater.inflate(R.layout.layout_dialog_order_distance, null,false);
        rg_distance = (RadioGroup) dialogView_OrderDistance.findViewById(R.id.rg_distance);
        rb_near = (RadioButton) dialogView_OrderDistance.findViewById(R.id.rb_near);
        rb_3km = (RadioButton) dialogView_OrderDistance.findViewById(R.id.rb_3km);
        rb_5km = (RadioButton) dialogView_OrderDistance.findViewById(R.id.rb_5km);
        rb_10km = (RadioButton) dialogView_OrderDistance.findViewById(R.id.rb_10km);
        rb_50km = (RadioButton) dialogView_OrderDistance.findViewById(R.id.rb_50km);


        /**SOS接单警告对话框*/
        dilogView_SOSReceive = inflater.inflate(R.layout.layout_dialog_sos_receive, null,false);
        bt_confirm_dialog_sos_receive = (Button) dilogView_SOSReceive.findViewById(R.id.bt_confirm_dialog_sos_receive);
        bt_cancle_dialog_sos_receive = (Button) dilogView_SOSReceive.findViewById(R.id.bt_cancle_dialog_sos_receive);


        lv_HelperPeople = (PullToRefreshListView) view.findViewById(R.id.lv_HelperPeople);
        lv_HelperPeople.setMode(PullToRefreshBase.Mode.BOTH);


        //参数：1，上下文；2，点击头像回调接口对象
        adapter = new HelperPeopleAdapter(getActivity(), this);
        lv_HelperPeople.setAdapter(adapter);
    }

    PullToRefreshBase.OnRefreshListener2<ListView> refreshListener = new PullToRefreshBase.OnRefreshListener2() {
        @Override
        public void onPullDownToRefresh(PullToRefreshBase refreshView) {
            page_index = 0;
            adapter.clear();
            getSOSData(0);
            getHelpData();
        }

        @Override
        public void onPullUpToRefresh(PullToRefreshBase refreshView) {
            page_index = page_index + 1;
            page_index_sos = page_index_sos + 1;
            getSOSData(page_index_sos);
            getHelpData();
        }
    };

    /**
     * 接单者接SOS单的时候检查其是否具有接单的条件,具有返回true,反之false
     * @return
     */
    private boolean checkSOSReceiveUserInfo() {
        //检查用户是否登录
        if (sp.getBoolean("isLogined", false) == false) {
            ToastUtil.shortshow(getContext(), R.string.toast_non_login);
            dialog_SOSReceive.dismiss();
            return false;
        }
        //检查是否绑定手机号
        if (null == sp.getString("mobile", null) || 11 != sp.getString("mobile", null).toString().length()) {
            ToastUtil.shortshow(getContext(), R.string.tv_tip_phone);
            dialog_SOSReceive.dismiss();
            return false;
        }

        return true;
    }


    //常量
    public final int USER_HPF = 1;
    public final int SEARCH_HPF = 4;
    public final int ADDHELPER_HPF = 2;

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            //SOS接单警告对话框 确认按钮
            case R.id.bt_confirm_dialog_sos_receive:
                if (checkSOSReceiveUserInfo()) {
                    LogicUtils.realNameVerify(getActivity(), new LogicUtils.AfterPassedListener() {
                        @Override
                        public void realNameVerifyPassed() {
                            dialog_SOSReceive.dismiss();
                            Bundle bundle = new Bundle();
                            bundle.putString("id", adapter.getItem(position_SOSReceive).id);
                            bundle.putInt("type",1);
                            startActivity(SOSNavigationActivity.class, bundle);
                        }
                    });
                }
                break;
            //SOS接单警告对话框 取消按钮
            case R.id.bt_cancle_dialog_sos_receive:
                dialog_SOSReceive.dismiss();
                break;

            //智能排序
            case R.id.rb_intelligent:
                filter_price_min = 1000;//筛选条件:最低价格
                filter_price_max = 0;//筛选条件:最高价格
                filter_distance = 0;//筛选条件:距离
                filter_order = 0;//筛选条件:排序规则
                page_index = 0;//分页:页码
                dialog_OrderPrice.dismiss();
                adapter.clear();
                getSOSData(0);
                getHelpData();

                dialog_OrderIntelligent.dismiss();
                break;
            //实时最热
            case R.id.rb_realtime_hottest:
                filter_price_min = 1000;//筛选条件:最低价格
                filter_price_max = 0;//筛选条件:最高价格
                filter_distance = 0;//筛选条件:距离
                filter_order = 1;//筛选条件:排序规则
                page_index = 0;//分页:页码
                dialog_OrderPrice.dismiss();
                adapter.clear();
                getSOSData(0);
                getHelpData();
                dialog_OrderIntelligent.dismiss();
                break;
            //实时最新
            case R.id.rb_realtime_newest:
                filter_price_min = 1000;//筛选条件:最低价格
                filter_price_max = 0;//筛选条件:最高价格
                filter_distance = 0;//筛选条件:距离
                filter_order = 2;//筛选条件:排序规则
                page_index = 0;//分页:页码
                dialog_OrderPrice.dismiss();
                adapter.clear();
                getSOSData(0);
                getHelpData();
                dialog_OrderIntelligent.dismiss();
                break;
            //升序
            case R.id.rb_realtime_ascend:
                filter_price_min = 0;//筛选条件:最低价格
                filter_price_max =1000;//筛选条件:最高价格
                filter_distance = 0;//筛选条件:距离
                filter_order = 3;//筛选条件:排序规则
                page_index = 0;//分页:页码
                dialog_OrderPrice.dismiss();
                adapter.clear();
                getHelpData();
                dialog_OrderIntelligent.dismiss();
                break;
            //倒序
            case R.id.rb_realtime_descend:
                filter_price_min = 0;//筛选条件:最低价格
                filter_price_max =1000;//筛选条件:最高价格
                filter_distance = 0;//筛选条件:距离
                filter_order = 4;//筛选条件:排序规则
                page_index = 0;//分页:页码
                dialog_OrderPrice.dismiss();
                adapter.clear();
                getHelpData();
                dialog_OrderIntelligent.dismiss();
                break;


            //距离选择:附近
            case R.id.rb_near:
                filter_price_min = 1000;//筛选条件:最低价格
                filter_price_max = 0;//筛选条件:最高价格
                filter_distance = 1;//筛选条件:距离
                filter_order = 0;//筛选条件:排序规则
                page_index = 0;//分页:页码
                dialog_OrderPrice.dismiss();
                adapter.clear();
                dialog_OrderDistance.dismiss();
                getSOSData(0);
                getHelpData();
                break;
            //距离选择:3km
            case R.id.rb_3km:
                filter_price_min = 1000;//筛选条件:最低价格
                filter_price_max = 0;//筛选条件:最高价格
                filter_distance = 3;//筛选条件:距离
                filter_order = 0;//筛选条件:排序规则
                page_index = 0;//分页:页码
                dialog_OrderPrice.dismiss();
                adapter.clear();
                getSOSData(0);
                getHelpData();
                dialog_OrderDistance.dismiss();
                break;
            //距离选择:5km
            case R.id.rb_5km:
                filter_price_min = 1000;//筛选条件:最低价格
                filter_price_max = 0;//筛选条件:最高价格
                filter_distance = 4;//筛选条件:距离
                filter_order = 0;//筛选条件:排序规则
                page_index = 0;//分页:页码
                dialog_OrderPrice.dismiss();
                adapter.clear();
                getSOSData(0);
                getHelpData();
                dialog_OrderDistance.dismiss();
                break;
            //距离选择:10km
            case R.id.rb_10km:
                filter_price_min = 1000;//筛选条件:最低价格
                filter_price_max = 0;//筛选条件:最高价格
                filter_distance = 5;//筛选条件:距离
                filter_order = 0;//筛选条件:排序规则
                page_index = 0;//分页:页码
                dialog_OrderPrice.dismiss();
                adapter.clear();
                getSOSData(0);
                getHelpData();
                dialog_OrderDistance.dismiss();
                break;
            //距离选择:50km
            case R.id.rb_50km:
                filter_price_min = 1000;//筛选条件:最低价格
                filter_price_max = 0;//筛选条件:最高价格
                filter_distance = 6;//筛选条件:距离
                filter_order = 0;//筛选条件:排序规则
                page_index = 0;//分页:页码
                dialog_OrderPrice.dismiss();
                adapter.clear();
                getSOSData(0);
                getHelpData();
                dialog_OrderDistance.dismiss();
                break;
            //点击全部分类按钮
            case R.id.ll_order_all:
                filter_price_min = 1000;//筛选条件:最低价格
                filter_price_max = 0;//筛选条件:最高价格
                filter_distance = 0;//筛选条件:距离
                filter_order = 0;//筛选条件:排序规则
                page_index = 0;//分页:页码

                //控制颜色变化
                tv_order_all.setTextColor(color_blue);
                tv_order_intelligent.setTextColor(color_black);
                tv_order_distance.setTextColor(color_black);
                tv_order_price.setTextColor(color_black);

                //控制图标变化
                iv_order_distance.setImageResource(R.mipmap.ic_downopen);
                iv_order_intelligent.setImageResource(R.mipmap.ic_downopen);
                iv_order_price.setImageResource(R.mipmap.ic_downopen);
                adapter.clear();//清空以前的数据
                getSOSData(0);
                getHelpData();
                break;
            //智能排序对话框
            case R.id.ll_order_intelligent:
                //控制颜色变化
                dialog_OrderIntelligent.setContentView(dialogView_OrderIntelligent);
                tv_order_all.setTextColor(color_black);
                tv_order_intelligent.setTextColor(color_blue);
                tv_order_distance.setTextColor(color_black);
                tv_order_price.setTextColor(color_black);

                //控制图标变化
                iv_order_distance.setImageResource(R.mipmap.ic_downopen);
                iv_order_intelligent.setImageResource(R.mipmap.ic_upclose);
                iv_order_price.setImageResource(R.mipmap.ic_downopen);

                dialog_OrderIntelligent.show();
                break;
            //金额选择对话框
            case R.id.ll_order_price:
                //控制颜色变化
                dialog_OrderPrice.setContentView(dialogView_OrderPrice);
                tv_order_all.setTextColor(color_black);
                tv_order_intelligent.setTextColor(color_black);
                tv_order_distance.setTextColor(color_black);
                tv_order_price.setTextColor(color_blue);

                //控制图标变化
                iv_order_distance.setImageResource(R.mipmap.ic_downopen);
                iv_order_intelligent.setImageResource(R.mipmap.ic_downopen);
                iv_order_price.setImageResource(R.mipmap.ic_upclose);

                dialog_OrderPrice.show();
                break;
            //距离选择对话框
            case R.id.ll_order_distance:
                dialog_OrderDistance.setContentView(dialogView_OrderDistance);
                //控制颜色变化
                tv_order_all.setTextColor(color_black);
                tv_order_intelligent.setTextColor(color_black);
                tv_order_distance.setTextColor(color_blue);
                tv_order_price.setTextColor(color_black);

                //控制图标变化
                iv_order_distance.setImageResource(R.mipmap.ic_upclose);
                iv_order_intelligent.setImageResource(R.mipmap.ic_downopen);
                iv_order_price.setImageResource(R.mipmap.ic_downopen);

                dialog_OrderDistance.show();
                break;
            //点击金额选择对话框中的确定按钮
            case R.id.bt_confirm_dialog_order_price:

                if (!checkUserInfo()) {
                    return;
                }
                filter_price_min = Integer.parseInt(et_min_price.getText().toString());//筛选条件:最低价格
                filter_price_max = Integer.parseInt(et_max_price.getText().toString());//筛选条件:最高价格
                filter_distance = 0;//筛选条件:距离
                filter_order = 0;//筛选条件:排序规则
                page_index = 0;//分页:页码
                dialog_OrderPrice.dismiss();
                dialog_OrderPrice.dismiss();
                adapter.clear();
                getHelpData();
                break;
            case R.id.iv_user_action_bar:
                Intent intent = new Intent(AppConstants.INTENT_ACTION_OPENDRAWER);
                getActivity().sendBroadcast(intent);
                break;
        }
    }

    public final int HPF_ITEM = 1;
    private int position_SOSReceive = 0;

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        /**
         *如果点击的是SOS信息,则调到导航界面
         *如果点击的帮人信息则调到帮人详情界面
         */
        if(!UserInfo.getIsLogin(getContext())){
            ToastUtil.shortshow(getContext(),"请登录");
            return;
        }
        position = position - 1;
        if (adapter.getItem(position).isSOS) {
            position_SOSReceive = position;
            dialog_SOSReceive.setContentView(dilogView_SOSReceive);
            if (checkSOSReceiveUserInfo()) {
                Bundle bundle = new Bundle();
                bundle.putString("id", adapter.getItem(position_SOSReceive).id);
                bundle.putInt("type",1);
                startActivity(SOSDetailActivity.class, bundle);
            }
        }
        if (!adapter.getItem(position).isSOS) {
            Bundle bundle=new Bundle();
            bundle.putString("id",adapter.getItem(position).id);
            bundle.putString("share_user_id",adapter.getItem(position).share_user_id);
            bundle.putInt("UI", ConstantUtils.HELP_PEOPLE);
            startActivity(OrderDetailActivity.class,bundle);
        }

    }

    //帮人界面点击头像查看别人的基本资料
    public final int LOMA_BYAvatar = 6;
    @Override
    public void setOnAvatarLisenner(int tag, View v) {
        SOSHelpInfo info = adapter.getItem((Integer) v.getTag());
        String user_id = info.user_id;
        String nickName = info.nickname;
        Bundle bundle = new Bundle();
        bundle.putString("user_id", user_id);
        bundle.putString("nickName", nickName);
        startActivity(LookOthersMassageActivity.class, bundle);
    }

    /**
     * RadioGroup中的RadioButton点击后改变字体颜色
     *
     * @param group
     * @param checkedId
     */
    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {

        //改变RadioButton的颜色
        for (int i = 0; i < group.getChildCount(); i++) {
            if (group.getChildAt(i).getId() == checkedId) {
                ((RadioButton) group.getChildAt(i)).setTextColor(color_blue);
            } else {
                ((RadioButton) group.getChildAt(i)).setTextColor(color_black);
            }
        }
    }

    /**
     * 获取SOS信息列表 默认一次性拉取所有数据
     */
    public void getSOSData(int index) {
        String url = null;
        LogUtil.e("getSOSData()----------bdLocation: "+myApp.getBdLocation());
        if (UserInfo.getIsLogin(getContext()) && myApp.getBdLocation()!= null) {
            url = ServerAPIConfig.Get_SOSList + "session_id=" + UserInfo.getSessionID(getContext()) + "&index=" + index + "&size=" + 100 + "&latitude=" + myApp.getBdLocation().getLatitude() + "&longitude=" + myApp.getBdLocation().getLongitude();
        } else if(myApp.getBdLocation() != null){
            url = ServerAPIConfig.Get_SOSList + "&index=" + index + "&size=" + 100 + "&latitude=" + myApp.getBdLocation().getLatitude() + "&longitude=" + myApp.getBdLocation().getLongitude();
        }
        if(url == null){
            return;
        }
        AsyncHttpResponseHandler res = new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                String str_json = new String(bytes);
                try {
                    JSONObject obj = new JSONObject(str_json);
                    if (0 != obj.getInt("code")) {
                        LogUtil.e(getContext(), "getSOSData 数据解析错误:code=" + obj.getInt("code"));
                        return;
                    }
                    Gson gson = new Gson();
                    SOSInfo sosInfo = gson.fromJson(obj.getJSONObject("result").toString(), SOSInfo.class);
                    List<SOSHelpInfo> list = SOSInfo2SOSHelpInfo(sosInfo.list);
                    for (int j = 0; j < list.size(); j++) {
                        adapter.addData(0, list.get(j));
                    }
                    adapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    LogUtil.e(getContext(), "enter catch=" + e.toString());
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                ToastUtil.shortshow(getContext(), R.string.toast_error_net);
                LogUtil.e(getContext(), "HelperPeopleFragment,enter onFailure");
            }
        };

        AndroidAsyncHttp.get(url, res);

    }

    /**
     * 获取帮人信息列表
     */
    public void getHelpData() {
        String url = null;
        if (UserInfo.getIsLogin(getContext()) && myApp.getBdLocation()!= null) {
            url = ServerAPIConfig.Get_HelpList + "session_id=" + UserInfo.getSessionID(getContext()) + "&index=" + page_index + "&size=" + page_size + "&latitude=" + myApp.getBdLocation().getLatitude() + "&longitude=" + myApp.getBdLocation().getLongitude() + "&price_min=" + filter_price_min + "&price_max=" + filter_price_max + "&distance=" + filter_distance + "&order=" + filter_order;
            LogUtil.e("帮人列表---------------------:"+url);
        } else if(myApp.getBdLocation() != null){
            url = ServerAPIConfig.Get_HelpList + "&index=" + page_index + "&size=" + page_size + "&latitude=" + myApp.getBdLocation().getLatitude() + "&longitude=" + myApp.getBdLocation().getLongitude() + "&price_min=" + filter_price_min + "&price_max=" + filter_price_max + "&distance=" + filter_distance + "&order=" + filter_order;
            LogUtil.e("帮人列表---------------------:"+url);
        }
        Log.i(TAG,"getHelpData is " + url);
        if(url == null){
            return;
        }
        AsyncHttpResponseHandler res = new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                String str_json = new String(bytes);
                try {
                    JSONObject obj = new JSONObject(str_json);
                    if (0 != obj.getInt("code")) {
                        return;
                    }
                    Gson gson = new Gson();
                    HelperPeopleInfo helpinfo = gson.fromJson(obj.getJSONObject("result").toString(), HelperPeopleInfo.class);
                    List<SOSHelpInfo> list = HelpPeopleInfo2SOSHelpInfo(helpinfo.list);
                    for (int j = 0; j < list.size(); j++) {
                        adapter.addData(list.get(j));
                    }
                    adapter.notifyDataSetChanged();
                    if (lv_HelperPeople.isRefreshing()) {
                        lv_HelperPeople.onRefreshComplete();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                if (lv_HelperPeople.isRefreshing()) {
                    lv_HelperPeople.onRefreshComplete();
                }
                ToastUtil.shortshow(getContext(), R.string.toast_error_net);
                Log.e(TAG, "HelperPeopleFragment,enter onFailure");
            }
        };
        AndroidAsyncHttp.get(url, res);
    }

    /**
     * 将HelperPeopleInfo信息转化为SOSHelpInfo
     *
     * @param list HelperPeopleInfo.LIST数据集
     * @return SOSHelpInfo数据集
     */
    public List<SOSHelpInfo> HelpPeopleInfo2SOSHelpInfo(List<HelperPeopleInfo.LIST> list) {
        List<SOSHelpInfo> list_HelpPeopleInfo2SOSHelpInfo = new ArrayList<SOSHelpInfo>();

        for (int j = 0; j < list.size(); j++) {
            SOSHelpInfo soshelpinfo = new SOSHelpInfo();
            soshelpinfo.user_id=list.get(j).user_id;
            soshelpinfo.nickname=list.get(j).nickname;
            soshelpinfo.isSOS = false;
            soshelpinfo.id = list.get(j).id;
            soshelpinfo.address = list.get(j).address;
            soshelpinfo.distance = list.get(j).distance;
            soshelpinfo.portrait = list.get(j).portrait;
            soshelpinfo.price = list.get(j).price;
            soshelpinfo.start_time = list.get(j).start_time;
            soshelpinfo.stop_time = list.get(j).stop_time;
            soshelpinfo.end_time = list.get(j).end_time;
            soshelpinfo.description = list.get(j).description;
            soshelpinfo.share_user_id = list.get(j).share_user_id;
            list_HelpPeopleInfo2SOSHelpInfo.add(soshelpinfo);
        }
        return list_HelpPeopleInfo2SOSHelpInfo;
    }

    /**
     * 将SOSInfo信息转化为SOSHelpInfo
     *
     * @param list SOSInfo.LIST数据集
     * @return SOSHelpInfo数据集
     */
    public List<SOSHelpInfo> SOSInfo2SOSHelpInfo(List<SOSInfo.LIST> list) {
        List<SOSHelpInfo> list_HelpPeopleInfo2SOSHelpInfo = new ArrayList<SOSHelpInfo>();
        for (int j = 0; j < list.size(); j++) {
            SOSHelpInfo soshelpinfo = new SOSHelpInfo();
            soshelpinfo.isSOS = true;

            soshelpinfo.user_id=list.get(j).user_id;
            soshelpinfo.nickname=list.get(j).nickname;
            soshelpinfo.id = list.get(j).id;
            soshelpinfo.portrait = list.get(j).portrait;
            soshelpinfo.price = list.get(j).price;
            soshelpinfo.start_time = list.get(j).start_time;
            soshelpinfo.stop_time = list.get(j).stop_time;
            soshelpinfo.end_time = list.get(j).end_time;
            soshelpinfo.distance = list.get(j).distance;


            soshelpinfo.address = list.get(j).location;


            soshelpinfo.description = soshelpinfo.nickname + getString(R.string.sos_desc);
            list_HelpPeopleInfo2SOSHelpInfo.add(soshelpinfo);
        }
        return list_HelpPeopleInfo2SOSHelpInfo;
    }
}
