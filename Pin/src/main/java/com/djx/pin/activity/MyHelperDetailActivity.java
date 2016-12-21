package com.djx.pin.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.djx.pin.R;
import com.djx.pin.adapter.MyHelperDetailAdapter;
import com.djx.pin.base.OldBaseActivity;
import com.djx.pin.beans.MyHelperDetailInfo;
import com.djx.pin.myview.CircleImageView;
import com.djx.pin.personal.ui.PersonalDataActivity;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

/**
 * Created by Administrator on 2016/7/21 0021.
 */
public class MyHelperDetailActivity extends OldBaseActivity implements View.OnClickListener,MyHelperDetailAdapter.SetListener {

    LinearLayout ll_Back_MHDA, ll_ShowAllContent_MHDA;
    TextView tv_StopTask_MHDA, tv_UserName_MHDA, tv_Time_MHDA, tv_TaskCode_MHDA, tv_TaskReward_MHDA,
            tv_DoTaskCount_MHDA, tv_TaskLife_MHDA, tv_Content_MHDA, tv_ShowAllContent_MHDA, tv_TaskDo_MHDA,
            tv_TaskComplete_MHDA, tv_QiangDanCount_MHDA;
    CircleImageView cimg_Avatar_MHDA;
    ImageView img_TaskState_MHDA,img_ShowAllContent_MHDA;
    PullToRefreshListView lv_DoTaskPerson_MHDA;

    MyHelperDetailAdapter adapter;

    boolean isShowAll=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_myhelperdetail);
        initView();
        initEvent();

        adapter = new MyHelperDetailAdapter(this,this);
        initData();
        lv_DoTaskPerson_MHDA.setAdapter(adapter);
        lv_DoTaskPerson_MHDA.setMode(PullToRefreshBase.Mode.PULL_FROM_END);

        lv_DoTaskPerson_MHDA.setOnRefreshListener(refreshListener);
    }

    PullToRefreshBase.OnRefreshListener<ListView> refreshListener = new PullToRefreshBase.OnRefreshListener<ListView>() {
        @Override
        public void onRefresh(PullToRefreshBase<ListView> refreshView) {

        }
    };


    private void initEvent() {
        ll_Back_MHDA.setOnClickListener(this);
        ll_ShowAllContent_MHDA.setOnClickListener(this);
        tv_StopTask_MHDA.setOnClickListener(this);

        //头布局点击事件
        cimg_Avatar_MHDA.setOnClickListener(this);
        ll_ShowAllContent_MHDA.setOnClickListener(this);
    }


    private void initData() {
        MyHelperDetailInfo info = new MyHelperDetailInfo("小黑");
//        adapter.addData(info);
//        adapter.addData(info);
//        adapter.addData(info);
//        adapter.addData(info);
//        adapter.addData(info);
//        adapter.addData(info);
        adapter.notifyDataSetChanged();
    }

    private void initView() {

        tv_StopTask_MHDA = (TextView) findViewById(R.id.tv_StopTask_MHDA);
        ll_Back_MHDA = (LinearLayout) findViewById(R.id.ll_Back_MHDA);
        lv_DoTaskPerson_MHDA = (PullToRefreshListView) findViewById(R.id.lv_DoTaskPerson_MHDA);
        addHeadView();
    }

    View headView;

    private void addHeadView() {
        ListView lv = lv_DoTaskPerson_MHDA.getRefreshableView();
        AbsListView.LayoutParams layoutParams = new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT, AbsListView.LayoutParams.WRAP_CONTENT);

        headView = LayoutInflater.from(this).inflate(R.layout.headview_myhelperdetail, lv_DoTaskPerson_MHDA, false);
        headView.setLayoutParams(layoutParams);
        lv.addHeaderView(headView);

        ll_ShowAllContent_MHDA = (LinearLayout) headView.findViewById(R.id.ll_ShowAllContent_MHDA);

        tv_UserName_MHDA = (TextView) headView.findViewById(R.id.tv_UserName_MHDA);
        tv_Time_MHDA = (TextView) headView.findViewById(R.id.tv_Time_MHDA);
//        tv_TaskCode_MHDA = (TextView) headView.findViewById(R.id.tv_TaskCode_MHDA);
        tv_TaskReward_MHDA = (TextView) headView.findViewById(R.id.tv_TaskReward_MHDA);
        tv_DoTaskCount_MHDA = (TextView) headView.findViewById(R.id.tv_DoTaskCount_MHDA);
        tv_TaskLife_MHDA = (TextView) headView.findViewById(R.id.tv_TaskLife_MHDA);
        tv_Content_MHDA = (TextView) headView.findViewById(R.id.tv_Content_MHDA);
        tv_ShowAllContent_MHDA = (TextView) headView.findViewById(R.id.tv_ShowAllContent_MHDA);
        tv_TaskDo_MHDA = (TextView) headView.findViewById(R.id.tv_TaskDo_MHDA);
        tv_TaskComplete_MHDA = (TextView) headView.findViewById(R.id.tv_TaskComplete_MHDA);
        tv_QiangDanCount_MHDA = (TextView) headView.findViewById(R.id.tv_QiangDanCount_MHDA);

        cimg_Avatar_MHDA = (CircleImageView) headView.findViewById(R.id.cimg_Avatar_MHDA);

        img_TaskState_MHDA = (ImageView) headView.findViewById(R.id.img_TaskState_MHDA);
        img_ShowAllContent_MHDA = (ImageView) headView.findViewById(R.id.img_ShowAllContent_MHDA);
    }


    /**
     * Called when a view has been clicked.
     *
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_Back_MHDA:
                this.finish();
                break;
            case R.id.tv_StopTask_MHDA:

                break;
            case R.id.cimg_Avatar_MHDA:
                startActivity(PersonalDataActivity.class);
                break;
            case R.id.ll_ShowAllContent_MHDA:

                if (isShowAll){
                    tv_Content_MHDA.setMaxLines(2);
                    isShowAll=false;
                    tv_ShowAllContent_MHDA.setText("展开全文");
                    img_ShowAllContent_MHDA.setImageResource(R.mipmap.ic_downopen);
                }else {
                    tv_Content_MHDA.setMaxLines(100);
                    tv_ShowAllContent_MHDA.setText(R.string.content_pack_up);
                    img_ShowAllContent_MHDA.setImageResource(R.mipmap.ic_upclose);
                    isShowAll=true;
                }
                break;
        }

    }

    @Override
    public void clickListener(int tag, View v) {
        switch (tag){
            case 1:
                TextView textView= (TextView) v;
                Log.e("textview===",textView.getText().toString());
                textView.setText("确认人员");
                break;
           case 2:
                startActivity(MyHelperTaskSourceActivity.class);
                break;

            case 3://申诉
               /* Bundle bundle = new Bundle();
                bundle.putString("process_id",);
                startActivity(DeclarationActivity.class,bundle);*/
                break;
        }
    }
}
