package com.djx.pin.fragment;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.djx.pin.R;
import com.djx.pin.activity.LifeRewardMainActivity;
import com.djx.pin.activity.LookOthersMassageActivity;
import com.djx.pin.beans.EventBeans;
import com.djx.pin.business.AppConstants;
import com.djx.pin.utils.LogUtil;
import com.djx.pin.utils.myutils.ConstantUtils;

import java.util.Timer;
import java.util.TimerTask;

import de.greenrobot.event.EventBus;
import de.greenrobot.event.Subscribe;
import de.greenrobot.event.ThreadMode;
import io.rong.imkit.RongIM;
import io.rong.imkit.fragment.ConversationListFragment;
import io.rong.imkit.model.UIConversation;
import io.rong.imlib.model.CSCustomServiceInfo;
import io.rong.imlib.model.Conversation;

/**
 * Created by 陈刘磊 (代号：姜饼（GingerBread）) on 2016/5/10.
 */
public class MessageFragment extends BaseFragment implements View.OnClickListener {

    //view和控件
    private View view;
    private LinearLayout ll_User_MessageFragment,ll_Mf_Reward;
    private ConversationListFragment fragment;
    private SwipeRefreshLayout swipeRefreshLayout;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.e("message","----MessageFragment-----onCreateView()----------");
        if(view==null){
            view=inflater.inflate(R.layout.messagefragment,null);
        }
        initView();
        initEvent();
        EventBus.getDefault().register(this);
        return view;
    }

    @Subscribe(threadMode = ThreadMode.MainThread)
    public void onEvent(EventBeans eventBeans){
        Log.e("message","------MessageFragment---onEvent()----------");
        if(eventBeans.style == ConstantUtils.RE_LOGIN ){//表示从新登陆账号
            Log.e("message","------MessageFragment--- ConstantUtils.RE_LOGIN----------");
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    initConversationList();
                }
            },1000);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.e("message","----MessageFragment-----onStart()----------");
    }


    @Override
    public void onResume() {
        super.onResume();
        Log.e("message","----MessageFragment-----onResume()----------");
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.e("message","----MessageFragment-----onPause()----------");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.e("message","----MessageFragment-----onStop()----------");
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.e("message","----MessageFragment-----onDestroyView()----------");
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e("message","----MessageFragment-----onDestroy()----------");
    }



    private void initEvent() {
        ll_User_MessageFragment.setOnClickListener(this);
        ll_Mf_Reward.setOnClickListener(this);//融云回话列表操作监听
        RongIM.setConversationListBehaviorListener(new RongIM.ConversationListBehaviorListener() {
            //头像点击监听
            @Override
            public boolean onConversationPortraitClick(Context context, Conversation.ConversationType conversationType, String s) {
                LogUtil.e("onConversationPortraitClick:"+s);
                Bundle bundle = new Bundle();
                bundle.putString("user_id",s);
                startActivity(LookOthersMassageActivity.class,bundle);
                return true;
            }
            @Override
            public boolean onConversationPortraitLongClick(Context context, Conversation.ConversationType conversationType, String s) {
                return false;
            }
            @Override
            public boolean onConversationLongClick(Context context, View view, UIConversation uiConversation) {
                return false;
            }
            @Override
            public boolean onConversationClick(Context context, View view, UIConversation uiConversation) {
                return false;
            }
        });
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Log.e("message","---------刷新----------");
                initConversationList();
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    private void initView() {
        ll_User_MessageFragment= (LinearLayout) view.findViewById(R.id.img_User_MessageFragment);
        ll_Mf_Reward= (LinearLayout) view.findViewById(R.id.ll_Mf_Reward);
        view.findViewById(R.id.ll_robot).setOnClickListener(this);
        fragment = new ConversationListFragment();
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.add(R.id.fl_conversation_list, fragment);
        transaction.commit();
        showConversationList();

        swipeRefreshLayout = ((SwipeRefreshLayout) view.findViewById(R.id.swiperefreshlayout));
        swipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_light);
    }

    Timer timer;
    public void showConversationList(){//回话列表昵称显示id问题
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                Log.e("message","---------销毁展示--showConversationList()--------");
                initConversationList();
                timer.purge();
                timer.cancel();
                timer = null;
            }
        },2000);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    public void initConversationList(){
        Log.e("message","---------刷新--initConversationList()--------");
        if(getActivity() == null || getActivity().getApplicationInfo() == null ||  getActivity().getApplicationInfo().packageName == null){
            return;
        }
        String packageName = getActivity().getApplicationInfo().packageName;
        Uri uri = Uri.parse("rong://" + packageName)
                .buildUpon()
                .appendPath("conversationlist")
                .appendQueryParameter(Conversation.ConversationType.PRIVATE.getName(), "false") //设置私聊会话是否聚合显示
                .appendQueryParameter(Conversation.ConversationType.GROUP.getName(), "true")//群组
                .appendQueryParameter(Conversation.ConversationType.DISCUSSION.getName(), "false")//讨论组
                .appendQueryParameter(Conversation.ConversationType.PUBLIC_SERVICE.getName(), "false")//公共服务号
                .appendQueryParameter(Conversation.ConversationType.APP_PUBLIC_SERVICE.getName(), "false")//公共服务号
                .appendQueryParameter(Conversation.ConversationType.SYSTEM.getName(), "false")//系统
                .build();
        if(fragment!= null){
            fragment.setUri(uri);
            fragment.onRestoreUI();
        }
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.img_User_MessageFragment:
                Intent intent = new Intent(AppConstants.INTENT_ACTION_OPENDRAWER);
                getActivity().sendBroadcast(intent);
                break;
            case R.id.ll_Mf_Reward:
                getActivity().startActivity(new Intent(getActivity(), LifeRewardMainActivity.class));
                break;
            case R.id.ll_robot:
                //首先需要构造使用客服者的用户信息
                CSCustomServiceInfo.Builder csBuilder = new CSCustomServiceInfo.Builder();
                CSCustomServiceInfo csInfo = csBuilder.nickName("小美").build();
                RongIM.getInstance().startCustomerServiceChat(getActivity(), "KEFU147928040942516", "逗比机器人", csInfo);
                break;
        }
    }
}
