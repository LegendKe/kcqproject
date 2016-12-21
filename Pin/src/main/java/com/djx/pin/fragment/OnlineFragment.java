package com.djx.pin.fragment;

import android.Manifest;
import android.app.Dialog;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.bumptech.glide.Glide;
import com.djx.pin.R;
import com.djx.pin.activity.LifeRewardMainActivity;
import com.djx.pin.activity.LoginActivity;
import com.djx.pin.activity.LookOthersMassageActivity;
import com.djx.pin.beans.LifeRewardOnlineEntity;
import com.djx.pin.beans.UserInfo;
import com.djx.pin.improve.detail.OrderDetailActivity;
import com.djx.pin.adapter.RewardRecyclerAdapter;
import com.djx.pin.serverapiconfig.ServerAPIConfig;
import com.djx.pin.utils.AndroidAsyncHttp;
import com.djx.pin.utils.LogUtil;
import com.djx.pin.utils.ToastUtil;
import com.djx.pin.utils.myutils.ConstantUtils;
import com.djx.pin.utils.myutils.ScreenTools;
import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.Bind;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;
import io.rong.imkit.RongIM;

/**
 * Created by 柯传奇 on 2016/10/12 0013.
 */
public class OnlineFragment extends BaseFragment implements View.OnClickListener,RadioGroup.OnCheckedChangeListener {
    protected final static String TAG = OnlineFragment.class.getSimpleName();

    //view和控件
    private View view;
    private LinearLayout ll_ordertype, ll_order_intelligent, ll_order_all, ll_order_price;
    private TextView tv_order_intelligent, tv_order_all, tv_order_price;
    private ImageView iv_order_intelligent, iv_order_price;
    private BDLocation bdLocation;
    private Dialog dialog_OrderIntelligent, dialog_OrderPrice;
    private View dialogView_OrderIntelligent, dialogView_OrderPrice;
    private LayoutInflater inflater;
    private RadioGroup rg_intelligent;
    private RadioButton rb_intelligent, rb_realtime_hottest, rb_realtime_newest;
    private EditText et_min_price, et_max_price;
    Button bt_confirm_dialog_order_price;
    int filter_price_min = 0, filter_price_max = 0, filter_order = 0;
    int color_blue = 0XFF1295F7, color_black = 0XFF3D3D3D;//黑色
    private RadioButton rb_realtime_descend;
    private RadioButton rb_realtime_ascend;
    private RewardRecyclerAdapter recyclerAdapter;
    private LinearLayoutManager layoutManager;
    private int lastVisibleItem;
    private int currentPage,page_size = 100;//当前页,每页的数量
    @Bind(R.id.rv_recyclerView)
    RecyclerView recyclerView;
    @Bind(R.id.swiperefreshlayout)
    SwipeRefreshLayout swipeRefreshLayout;
    @Bind(R.id.parent_view)
    RelativeLayout parent_view;
    private boolean isBottom;
    private boolean isRefresh;
    private int pxNum;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_online, null);
        ButterKnife.bind(this,view);
        initView();
        initData();
        initEvent();
        return view;
    }

    private void initData() {
        recyclerAdapter = new RewardRecyclerAdapter(getActivity());
        recyclerAdapter.setParentView(parent_view);//供popupwindow试用
        recyclerView.setAdapter(recyclerAdapter);
        getOnlineRewardData(0,page_size);

    }


    /**
     * 检查用户输入的信息是否正确
     * @return 正确返回ture, 反之false
     */
    public boolean checkUserInfo() {
        /**检查最小金额是否为空*/
        if (null == et_min_price.getText() || et_min_price.getText().toString().length() == 0) {
            ToastUtil.shortshow(getContext(), R.string.toast_non_min_price);
            return false;
        }
        /**检查最大金额是否为空*/
        if (null == et_max_price.getText() || et_max_price.getText().toString().length() == 0) {
            ToastUtil.shortshow(getContext(), R.string.toast_non_max_price);
            return false;
        }
        /**检查最大金额是否大于最小金额*/
        if (Integer.parseInt(et_min_price.getText().toString()) > Integer.parseInt(et_max_price.getText().toString())) {
            ToastUtil.shortshow(getContext(), R.string.toast_error_max_price);
            return false;
        }
        return true;
    }

    public void initDialog(int height) {
        /**智能排序对话框*/
        dialog_OrderIntelligent = new Dialog(getContext(), R.style.dialog_non_transparent);
        dialog_OrderIntelligent.setContentView(dialogView_OrderIntelligent);
        WindowManager.LayoutParams layoutParams_OrderIntelligent = dialog_OrderIntelligent.getWindow().getAttributes();
        layoutParams_OrderIntelligent.gravity = Gravity.TOP;
        layoutParams_OrderIntelligent.width = WindowManager.LayoutParams.MATCH_PARENT;
        int heigth2 = ScreenTools.instance(getContext()).dip2px(50);
        //layoutParams_OrderIntelligent.y = ll_ordertype.getBottom() + height;
        layoutParams_OrderIntelligent.y = heigth2;
        dialog_OrderIntelligent.getWindow().setAttributes(layoutParams_OrderIntelligent);

        /**金额选择对话框*/
        dialog_OrderPrice = new Dialog(getContext(), R.style.dialog_non_transparent);
        dialog_OrderPrice.setContentView(dialogView_OrderPrice);
        WindowManager.LayoutParams layoutParams_OrderPrice = dialog_OrderPrice.getWindow().getAttributes();
        layoutParams_OrderPrice.gravity = Gravity.TOP;
        layoutParams_OrderPrice.width = WindowManager.LayoutParams.MATCH_PARENT;
        //layoutParams_OrderPrice.y = ll_ordertype.getBottom() + height;
        layoutParams_OrderPrice.y = heigth2;
        dialog_OrderPrice.getWindow().setAttributes(layoutParams_OrderPrice);

    }

    private void initEvent() {
        ll_order_intelligent.setOnClickListener(this);
        ll_order_all.setOnClickListener(this);
        ll_order_price.setOnClickListener(this);
        rg_intelligent.setOnCheckedChangeListener(this);
        bt_confirm_dialog_order_price.setOnClickListener(this);
        rb_intelligent.setOnClickListener(this);
        rb_realtime_hottest.setOnClickListener(this);
        rb_realtime_newest.setOnClickListener(this);

        //下拉刷新
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if(isRefresh == false){
                    isRefresh = true;
                    getOnlineRewardData(0,page_size);
                    recyclerAdapter.setState(1000);
                    currentPage = 0;
                    isBottom = false;
                }
            }
        });
        //上拉加载更多
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                lastVisibleItem =layoutManager.findLastVisibleItemPosition();
            }
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState ==RecyclerView.SCROLL_STATE_IDLE && lastVisibleItem + 1 ==recyclerAdapter.getItemCount() && isBottom == false) {
                    currentPage++;
                    recyclerAdapter.setState(1000);
                    getOnlineRewardData(currentPage,page_size);
                }
                switch (newState){
                    case RecyclerView.SCROLL_STATE_SETTLING:
                        Log.i("Online","用户在手指离开屏幕之前，由于滑了一下，视图仍然依靠惯性继续滑动");
                        Glide.with(getContext()).pauseRequests();
                        //刷新
                        break;
                    case RecyclerView.SCROLL_STATE_IDLE:
                        Log.i("Online", "视图已经停止滑动");
                        Glide.with(getContext()).resumeRequests();
                        break;
                    case RecyclerView.SCROLL_STATE_DRAGGING:
                        Log.i("Online","手指没有离开屏幕，视图正在滑动");
                        Glide.with(getContext()).resumeRequests();
                        break;
                }





            }
        });



        /**
         * recyclerAdapter的item点击事件
         */
        recyclerAdapter.addOnItemClickListener(new RewardRecyclerAdapter.ItemClickListener() {
            @Override
            public void onItemClick(int position, View itemView) {
                if(!UserInfo.getIsLogin(getContext())){
                    startActivity(LoginActivity.class);
                    return;
                }
                LifeRewardOnlineEntity.OnlineBean onlineBean = recyclerAdapter.getItem(position);

                Bundle bundle_Online = new Bundle();
                bundle_Online.putString("id", onlineBean.id);
                bundle_Online.putInt("status",onlineBean.status);
                bundle_Online.putInt("type", onlineBean.type);
                bundle_Online.putInt("UI", ConstantUtils.HELP_PEOPLE_ONLINE);
                startActivity(OrderDetailActivity.class, bundle_Online);
            }
        });
        /**
         * 点击item的头像进入个人信息
         */
        recyclerAdapter.addOnItemAvatarClickListener(new RewardRecyclerAdapter.ItemAvatarClickListener() {
            @Override
            public void onItemAvatarClick(int position, View view) {
                LifeRewardOnlineEntity.OnlineBean onlineBean = recyclerAdapter.getItem(position);
                String user_id = onlineBean.user_id;
                String nickName = onlineBean.nickname;
                Bundle bundle = new Bundle();
                bundle.putString("user_id", user_id);
                bundle.putString("nickName", nickName);
                startActivity(LookOthersMassageActivity.class, bundle);
            }
        });

        recyclerAdapter.addOnItemChatClickListener(new RewardRecyclerAdapter.ItemChatClickListener() {
            @Override
            public void onItemChatClick(String user_id, String nickname) {
                //聊天,申请权限
                int flag = ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.RECORD_AUDIO);
                if (flag!= PackageManager.PERMISSION_GRANTED){
                    requestPermissions(new String[]{Manifest.permission.RECORD_AUDIO},100);
                }else if(flag == PackageManager.PERMISSION_GRANTED){
                    try {
                        //获取权限后执行的动作
                        RongIM.getInstance().startPrivateChat(getContext(), user_id, nickname);
                    } catch (Exception e) {
                        Toast.makeText(getContext(), R.string.record_permision_fail_string, Toast.LENGTH_LONG).show();
                    }
                }
            }
        });




    }

    private void initView() {
        inflater = LayoutInflater.from(getContext());
        ll_ordertype = (LinearLayout) view.findViewById(R.id.ll_ordertype);

        ll_ordertype.post(new Runnable() {
            @Override
            public void run() {
                initDialog(LifeRewardMainActivity.height_title);
            }
        });
        ll_order_intelligent = (LinearLayout) view.findViewById(R.id.ll_order_intelligent);
        ll_order_price = (LinearLayout) view.findViewById(R.id.ll_order_price);
        ll_order_all = (LinearLayout) view.findViewById(R.id.ll_order_all);
        tv_order_intelligent = (TextView) view.findViewById(R.id.tv_order_intelligent);
        tv_order_all = (TextView) view.findViewById(R.id.tv_order_all);
        tv_order_all.setTextColor(color_blue);
        tv_order_price = (TextView) view.findViewById(R.id.tv_order_price);
        iv_order_intelligent = (ImageView) view.findViewById(R.id.iv_order_intelligent);
        iv_order_price = (ImageView) view.findViewById(R.id.iv_order_price);

        /**智能排序对话框*/
        dialogView_OrderIntelligent = inflater.inflate(R.layout.layout_dialog_order_intelligent, null);
        rg_intelligent = (RadioGroup) dialogView_OrderIntelligent.findViewById(R.id.rg_intelligent);
        rb_intelligent = (RadioButton) dialogView_OrderIntelligent.findViewById(R.id.rb_intelligent);
        rb_realtime_hottest = (RadioButton) dialogView_OrderIntelligent.findViewById(R.id.rb_realtime_hottest);
        rb_realtime_newest = (RadioButton) dialogView_OrderIntelligent.findViewById(R.id.rb_realtime_newest);
        rb_realtime_descend = (RadioButton) dialogView_OrderIntelligent.findViewById(R.id.rb_realtime_descend);
        rb_realtime_ascend = (RadioButton) dialogView_OrderIntelligent.findViewById(R.id.rb_realtime_ascend);
        rb_realtime_descend.setVisibility(View.GONE);
        rb_realtime_ascend.setVisibility(View.GONE);

        /**金额选择对话框*/
        dialogView_OrderPrice = inflater.inflate(R.layout.layout_dialog_order_price, null);
        et_min_price = (EditText) dialogView_OrderPrice.findViewById(R.id.et_min_price);
        et_max_price = (EditText) dialogView_OrderPrice.findViewById(R.id.et_max_price);
        bt_confirm_dialog_order_price = (Button) dialogView_OrderPrice.findViewById(R.id.bt_confirm_dialog_order_price);


        swipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_light, android.R.color.holo_red_light, android.R.color.holo_orange_light, android.R.color.holo_green_light);
        layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        pxNum = ScreenTools.instance(getContext()).dip2px(36);
    }

    /**
     *
     * @param filter_price_min 最低价格
     * @param filter_price_max 最高价格
     * @param filter_order 排序规则
     */
    public void setCurrentFilterConditon(int filter_price_min,int filter_price_max,int filter_order){
        this.filter_price_min = filter_price_min;
        this.filter_price_max = filter_price_max;
        this.filter_order = filter_order;
    }


   @Override
    public void onClick(View v) {
        switch (v.getId()) {
            //智能排序
            case R.id.rb_intelligent:
                setCurrentFilterConditon(0,0,0);
                getOnlineRewardData(0,page_size);
                dialog_OrderPrice.dismiss();
                dialog_OrderIntelligent.dismiss();
                break;
            //实时最热
            case R.id.rb_realtime_hottest:
                setCurrentFilterConditon(0,0,1);
                getOnlineRewardData(0,page_size);
                dialog_OrderPrice.dismiss();
                dialog_OrderIntelligent.dismiss();
                break;
            //实时最新
            case R.id.rb_realtime_newest:
                setCurrentFilterConditon(0,0,2);
                getOnlineRewardData(0,page_size);
                dialog_OrderPrice.dismiss();
                dialog_OrderIntelligent.dismiss();
                break;

            //点击全部分类按钮
            case R.id.ll_order_all:
                setCurrentFilterConditon(0,0,0);
                //控制颜色变化
                tv_order_all.setTextColor(color_blue);
                tv_order_intelligent.setTextColor(color_black);
                tv_order_price.setTextColor(color_black);
                //控制图标变化
                iv_order_intelligent.setImageResource(R.mipmap.ic_downopen);
                iv_order_price.setImageResource(R.mipmap.ic_downopen);
                getOnlineRewardData(0,page_size);
                break;
            //智能排序对话框
            case R.id.ll_order_intelligent:
                //控制颜色变化
                tv_order_all.setTextColor(color_black);
                tv_order_intelligent.setTextColor(color_blue);
                tv_order_price.setTextColor(color_black);
                //控制图标变化
                iv_order_intelligent.setImageResource(R.mipmap.ic_upclose);
                iv_order_price.setImageResource(R.mipmap.ic_downopen);
                dialog_OrderIntelligent.show();
                break;
            //金额选择对话框
            case R.id.ll_order_price:
                //控制颜色变化
                tv_order_all.setTextColor(color_black);
                tv_order_intelligent.setTextColor(color_black);
                tv_order_price.setTextColor(color_blue);
                //控制图标变化
                iv_order_intelligent.setImageResource(R.mipmap.ic_downopen);
                iv_order_price.setImageResource(R.mipmap.ic_upclose);
                dialog_OrderPrice.show();
                break;
            //点击金额选择对话框中的确定按钮
            case R.id.bt_confirm_dialog_order_price:
                if (!checkUserInfo()) {
                    return;
                }
                filter_price_min = Integer.parseInt(et_min_price.getText().toString());//筛选条件:最低价格
                filter_price_max = Integer.parseInt(et_max_price.getText().toString());//筛选条件:最高价格
                filter_order = 0;//筛选条件:排序规则
                dialog_OrderPrice.dismiss();
                dialog_OrderPrice.dismiss();
                getOnlineRewardData(0,page_size);
                break;

        }
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
     * 获取帮人信息列表
     */
    public void getOnlineRewardData(final int page_index, final int page_size) {

        String url;
        if (UserInfo.getIsLogin(getContext())) {
            url = ServerAPIConfig.Get_OnlieRewardList + "session_id=" + UserInfo.getSessionID(getActivity()) + "&index=" + page_index + "&size=" + page_size + "&price_min=" + filter_price_min + "&price_max=" + filter_price_max + "&type=" + -1 + "&order=" + filter_order;
        } else {
            url = ServerAPIConfig.Get_OnlieRewardList + "index=" + page_index + "&size=" + page_size + "&price_min=" + filter_price_min + "&price_max=" + filter_price_max + "&type=" + -1 + "&order=" + filter_order;
        }
        LogUtil.e("------获取帮人信息列表-----url------------:"+url);
        AsyncHttpResponseHandler res = new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                isRefresh = false;
                String str_json = new String(bytes);
                try {
                    JSONObject obj = new JSONObject(str_json);
                    if (0 == obj.getInt("code")) {
                        swipeRefreshLayout.setRefreshing(false);
                        Gson gson = new Gson();
                        LifeRewardOnlineEntity onlineEntity = gson.fromJson(obj.getJSONObject("result").toString(), LifeRewardOnlineEntity.class);
                        if(onlineEntity.list != null && onlineEntity.list.size() < page_size){//滑到底了
                            recyclerAdapter.setState(1001);
                            isBottom = true;
                        }else {
                            recyclerAdapter.setState(0);
                        }
                        if(page_index > 0){//加载更多
                            recyclerAdapter.addMoreData(onlineEntity.list);
                        }else {//下拉刷新
                            recyclerAdapter.addAll(onlineEntity.list);
                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                isRefresh = false;
                if (swipeRefreshLayout.isRefreshing()) {
                    swipeRefreshLayout.setRefreshing(false);
                }
                ToastUtil.shortshow(getContext(), R.string.toast_error_net);
            }
        };
        AndroidAsyncHttp.get(url, res);
    }



}
