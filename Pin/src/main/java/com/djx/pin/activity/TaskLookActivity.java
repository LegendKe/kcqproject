package com.djx.pin.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.djx.pin.R;
import com.djx.pin.adapter.TLA_GotRewardAdapter;
import com.djx.pin.adapter.TLA_TaskDetailAdapter;
import com.djx.pin.base.OldBaseActivity;
import com.djx.pin.beans.GotRewardInfo;
import com.djx.pin.beans.TaskDetailInfo;

/**
 * Created by Administrator on 2016/6/29.
 */
public class TaskLookActivity extends OldBaseActivity implements View.OnClickListener {

    private LinearLayout ll_Back_TLA;
    private TextView tv_TaskDetail_TLA, tv_GotReward_TLA;
    private View v_TaskDetailLine_TLA, v_GotRewardLine_TLA;
    private ListView lv_TaskDetail_TLA, lv_GotReward_TLA;

    private TLA_TaskDetailAdapter tla_taskDetailAdapter;
    private TLA_GotRewardAdapter tla_gotRewardAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tasklook);
        initView();
        initEvent();
        tla_taskDetailAdapter = new TLA_TaskDetailAdapter(this);
        tla_gotRewardAdapter = new TLA_GotRewardAdapter(this);
        initTaskDetailData();
        lv_TaskDetail_TLA.setAdapter(tla_taskDetailAdapter);
        initGotRewardData();
        lv_GotReward_TLA.setAdapter(tla_gotRewardAdapter);

    }

    private void initGotRewardData() {
        GotRewardInfo info = new GotRewardInfo(R.mipmap.ic_defualtavater, "小狗掉水里了，小猫去救了", "100", "已完成", "1小时前");

        tla_gotRewardAdapter.addData(info);
        tla_gotRewardAdapter.addData(info);
        tla_gotRewardAdapter.addData(info);
        tla_gotRewardAdapter.addData(info);
        tla_gotRewardAdapter.addData(info);
        tla_gotRewardAdapter.addData(info);
        tla_gotRewardAdapter.addData(info);
    }

    private void initTaskDetailData() {
        TaskDetailInfo info = new TaskDetailInfo(R.mipmap.ic_defualtavater, "小狗掉水里了，小猫去救了", "上海东莞", "已完成", "1小时前");
        TaskDetailInfo info1 = new TaskDetailInfo(R.mipmap.ic_defualtavater, "小狗掉水里了，小猫去救了", "上海东莞", "正在服务", "1小时前");
        TaskDetailInfo info2 = new TaskDetailInfo(R.mipmap.ic_defualtavater, "小狗掉水里了，小猫去救了", "上海东莞", "完成等赏", "1小时前");

        tla_taskDetailAdapter.addData(info);
        tla_taskDetailAdapter.addData(info1);
        tla_taskDetailAdapter.addData(info2);
        tla_taskDetailAdapter.addData(info);
        tla_taskDetailAdapter.addData(info1);
        tla_taskDetailAdapter.addData(info2);
        tla_taskDetailAdapter.addData(info);
    }

    private void initEvent() {
        ll_Back_TLA.setOnClickListener(this);
        tv_TaskDetail_TLA.setOnClickListener(this);
        tv_GotReward_TLA.setOnClickListener(this);
    }

    private void initView() {
        ll_Back_TLA = (LinearLayout) findViewById(R.id.ll_Back_TLA);
        tv_TaskDetail_TLA = (TextView) findViewById(R.id.tv_TaskDetail_TLA);
        tv_GotReward_TLA = (TextView) findViewById(R.id.tv_GotReward_TLA);
        v_TaskDetailLine_TLA = findViewById(R.id.v_TaskDetailLine_TLA);
        v_GotRewardLine_TLA = findViewById(R.id.v_GotRewardLine_TLA);
        lv_TaskDetail_TLA = (ListView) findViewById(R.id.lv_TaskDetail_TLA);
        lv_GotReward_TLA = (ListView) findViewById(R.id.lv_GotReward_TLA);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_Back_TLA:
                this.finish();
                break;
            case R.id.tv_TaskDetail_TLA:
                tv_TaskDetail_TLA.setTextColor(getResources().getColor(R.color.text_color_focused));
                tv_GotReward_TLA.setTextColor(getResources().getColor(R.color.text_color_black));
                v_TaskDetailLine_TLA.setVisibility(View.VISIBLE);
                v_GotRewardLine_TLA.setVisibility(View.GONE);
                lv_TaskDetail_TLA.setVisibility(View.VISIBLE);
                lv_GotReward_TLA.setVisibility(View.GONE);
                break;
            case R.id.tv_GotReward_TLA:
                tv_TaskDetail_TLA.setTextColor(getResources().getColor(R.color.text_color_black));
                tv_GotReward_TLA.setTextColor(getResources().getColor(R.color.text_color_focused));
                v_TaskDetailLine_TLA.setVisibility(View.GONE);
                v_GotRewardLine_TLA.setVisibility(View.VISIBLE);
                lv_TaskDetail_TLA.setVisibility(View.GONE);
                lv_GotReward_TLA.setVisibility(View.VISIBLE);

                break;
        }
    }
}
