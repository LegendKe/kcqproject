package com.djx.pin.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.djx.pin.R;
import com.djx.pin.utils.LogUtil;

import io.rong.imkit.RongIM;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.Message;
import io.rong.imlib.model.UserInfo;

/**
 * 融云聊天界面,使用静态fragment,不需要在Activity中写其他的
 */
public class ConversationActivity extends AppCompatActivity implements View.OnClickListener {

    LinearLayout ll_back;
    TextView tv_titl;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversation);
        initView();
        initEvent();
        setTitle();
    }

    /**
     * 设置标题
     */
    public void setTitle(){
        tv_titl.setText(getIntent().getData().getQueryParameter("title"));
    }

    private void initEvent() {
        ll_back.setOnClickListener(this);
        RongIM.setConversationBehaviorListener(new RongIM.ConversationBehaviorListener() {
            @Override
            public boolean onUserPortraitClick(Context context, Conversation.ConversationType conversationType, UserInfo userInfo) {
                LogUtil.e("userid: "+userInfo.getUserId() +"      user_portrait:"+userInfo.getPortraitUri());
                Intent intent = new Intent(ConversationActivity.this,LookOthersMassageActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("user_id",userInfo.getUserId());
                intent.putExtras(bundle);
                startActivity(intent);
                return true;
            }

            @Override
            public boolean onUserPortraitLongClick(Context context, Conversation.ConversationType conversationType, UserInfo userInfo) {
                return false;
            }

            @Override
            public boolean onMessageClick(Context context, View view, Message message) {
                return false;
            }

            @Override
            public boolean onMessageLinkClick(Context context, String s) {
                return false;
            }

            @Override
            public boolean onMessageLongClick(Context context, View view, Message message) {
                return false;
            }
        });
    }

    private void initView() {
        ll_back= (LinearLayout) findViewById(R.id.ll_back);
        tv_titl= (TextView) findViewById(R.id.tv_titl);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            //点击返回按钮
            case R.id.ll_back:
                finish();
                break;
        }
    }
}
