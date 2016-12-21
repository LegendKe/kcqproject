package com.djx.pin.activity;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.djx.pin.R;
import com.djx.pin.adapter.MyRewardAdapter;
import com.djx.pin.base.OldBaseActivity;
import com.djx.pin.beans.RewardInfo;
import com.djx.pin.beans.StaticBean;
import com.djx.pin.serverapiconfig.ServerAPIConfig;
import com.djx.pin.utils.AndroidAsyncHttp;
import com.djx.pin.utils.ToastUtil;
import com.google.gson.Gson;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.loopj.android.http.AsyncHttpResponseHandler;

import java.util.List;

import cz.msebera.android.httpclient.Header;

/**
 * 助人等赏
 * Created by Administrator on 2016/6/29.
 */
public class RewardActivity extends OldBaseActivity implements View.OnClickListener, MyRewardAdapter.OnFunctionChose, AdapterView.OnItemClickListener {
    protected final static String TAG = RewardActivity.class.getSimpleName();

    private LinearLayout ll_Back_RA, ll_All_RA, ll_Doing_RA, ll_Complete_RA, ll_Rights_RA;
    private View v_All_RA, v_DoingLine_RA, v_CompleteLine_RA, v_RightsLine_RA;
    private TextView tv_All_RA, tv_Doing_RA, tv_Complete_RA, tv_Rights_RA, tv_Title;

    private PullToRefreshListView lv_RA;
    private MyRewardAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reward);
        initView();
        initEvent();
        //参数：1，上下文；2，点击回调接口
        adapter = new MyRewardAdapter(this, this);
        initData(-1, index_all);
        lv_RA.setAdapter(adapter);
        lv_RA.setMode(PullToRefreshBase.Mode.BOTH);
        lv_RA.setOnRefreshListener(refreshListener);
    }

    @Override
    protected void onPause() {
        super.onPause();
        adapter.clear();

        index_all=0;
        index_commplet=0;
        index_complaint=0;
        index_do=0;

        initData(-1, index_all);



    }

    PullToRefreshBase.OnRefreshListener<ListView> refreshListener = new PullToRefreshBase.OnRefreshListener<ListView>() {
        @Override
        public void onRefresh(PullToRefreshBase<ListView> refreshView) {
            switch (content_Type) {
                case 0:
                    index_all++;
                    initData(-1, index_all);
                    break;
                case 1:
                    index_do++;
                    initData(1, index_do);
                    break;
                case 2:
                    index_commplet++;
                    initData(2, index_commplet);
                    break;
                case 3:
                    index_complaint++;
                    initData(3, index_complaint);
                    break;
            }
        }
    };
    /**
     * content_type 	Number
     * 内容类别，-1-all，1-进行中，2-已完成，3-申诉
     * v1/user/help/list/book?session_id=asdf9610eaawaaf9cae7&index=0&size=10&content_type=-1
     */
    int index_all = 0;
    int index_do = 0;
    int index_commplet = 0;
    int index_complaint = 0;

    private void initData(int content_type, int index_) {
        String session_id = getSharedPreferences(StaticBean.USER_INFO, Context.MODE_PRIVATE).getString("session_id", null);
        int size = 10;
        int index = index_;

        String url = ServerAPIConfig.RewardWait + "session_id=" + session_id + "&index=" + index + "&size=" + size + "&content_type=" + content_type + "";
        Log.i(TAG,"--------------url:-----------   "+url);
        AsyncHttpResponseHandler res = new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                String string = new String(bytes);
                Gson gson = new Gson();
                RewardInfo info = gson.fromJson(string, RewardInfo.class);
                if (info.code == 0) {
                    List<RewardInfo.Result.Lists> list = info.result.list;
                    for (int j = 0; j < list.size(); j++) {
                        RewardInfo.Result.Lists listInfo = list.get(j);
                        adapter.addData(listInfo);
                    }
                    adapter.notifyDataSetChanged();
                    lv_RA.onRefreshComplete();
                } else {
                    ToastUtil.shortshow(getApplicationContext(), "没有更多内容");
                    lv_RA.onRefreshComplete();
                }
            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                Log.e("str=======", "失败");
            }
        };
        AndroidAsyncHttp.get(url, res);

    }

    private void initEvent() {
        ll_Back_RA.setOnClickListener(this);
        ll_All_RA.setOnClickListener(this);
        ll_Complete_RA.setOnClickListener(this);
        ll_Rights_RA.setOnClickListener(this);
        ll_Doing_RA.setOnClickListener(this);
        tv_Title.setText("助人等赏");
        lv_RA.setOnItemClickListener(this);
    }

    private void initView() {
        ll_Back_RA = (LinearLayout) findViewById(R.id.ll_Back_RA);
        ll_All_RA = (LinearLayout) findViewById(R.id.ll_All_RA);
        ll_Doing_RA = (LinearLayout) findViewById(R.id.ll_Doing_RA);
        ll_Complete_RA = (LinearLayout) findViewById(R.id.ll_Complete_RA);
        ll_Rights_RA = (LinearLayout) findViewById(R.id.ll_Rights_RA);

        tv_All_RA = (TextView) findViewById(R.id.tv_All_RA);
        tv_Doing_RA = (TextView) findViewById(R.id.tv_Doing_RA);
        tv_Complete_RA = (TextView) findViewById(R.id.tv_Complete_RA);
        tv_Rights_RA = (TextView) findViewById(R.id.tv_Rights_RA);
        tv_Title = (TextView) findViewById(R.id.tv_Title);

        v_All_RA = findViewById(R.id.v_All_RA);
        v_DoingLine_RA = findViewById(R.id.v_DoingLine_RA);
        v_CompleteLine_RA = findViewById(R.id.v_CompleteLine_RA);
        v_RightsLine_RA = findViewById(R.id.v_RightsLine_RA);

        lv_RA = (PullToRefreshListView) findViewById(R.id.lv_RA);
    }

    //正在显示的类型
    //0:所有;1:进行中;2已完成;3:申诉
    int content_Type = 0;

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_Back_RA:
                this.finish();
                break;
            case R.id.ll_All_RA:
                index_all = 0;


                content_Type = 0;
                adapter.clear();
                initData(-1, index_all);
                tv_All_RA.setTextColor(getResources().getColor(R.color.text_color_focused));
                tv_Doing_RA.setTextColor(getResources().getColor(R.color.text_color_black));
                tv_Complete_RA.setTextColor(getResources().getColor(R.color.text_color_black));
                tv_Rights_RA.setTextColor(getResources().getColor(R.color.text_color_black));

                v_All_RA.setVisibility(View.VISIBLE);
                v_DoingLine_RA.setVisibility(View.INVISIBLE);
                v_CompleteLine_RA.setVisibility(View.INVISIBLE);
                v_RightsLine_RA.setVisibility(View.INVISIBLE);

                break;
            case R.id.ll_Doing_RA:
                index_do = 0;
                content_Type = 1;
                adapter.clear();
                initData(1, index_do);
                tv_All_RA.setTextColor(getResources().getColor(R.color.text_color_black));
                tv_Doing_RA.setTextColor(getResources().getColor(R.color.text_color_focused));
                tv_Complete_RA.setTextColor(getResources().getColor(R.color.text_color_black));
                tv_Rights_RA.setTextColor(getResources().getColor(R.color.text_color_black));

                v_All_RA.setVisibility(View.INVISIBLE);
                v_DoingLine_RA.setVisibility(View.VISIBLE);
                v_CompleteLine_RA.setVisibility(View.INVISIBLE);
                v_RightsLine_RA.setVisibility(View.INVISIBLE);
                break;
            case R.id.ll_Complete_RA:
                index_commplet = 0;
                content_Type = 2;
                adapter.clear();
                initData(2, index_commplet);
                tv_All_RA.setTextColor(getResources().getColor(R.color.text_color_black));
                tv_Doing_RA.setTextColor(getResources().getColor(R.color.text_color_black));
                tv_Complete_RA.setTextColor(getResources().getColor(R.color.text_color_focused));
                tv_Rights_RA.setTextColor(getResources().getColor(R.color.text_color_black));

                v_All_RA.setVisibility(View.INVISIBLE);
                v_DoingLine_RA.setVisibility(View.INVISIBLE);
                v_CompleteLine_RA.setVisibility(View.VISIBLE);
                v_RightsLine_RA.setVisibility(View.INVISIBLE);

                break;
            case R.id.ll_Rights_RA:
                index_complaint = 0;

                content_Type = 3;
                adapter.clear();
                initData(3, index_complaint);
                tv_All_RA.setTextColor(getResources().getColor(R.color.text_color_black));
                tv_Doing_RA.setTextColor(getResources().getColor(R.color.text_color_black));
                tv_Complete_RA.setTextColor(getResources().getColor(R.color.text_color_black));
                tv_Rights_RA.setTextColor(getResources().getColor(R.color.text_color_focused));

                v_All_RA.setVisibility(View.INVISIBLE);
                v_DoingLine_RA.setVisibility(View.INVISIBLE);
                v_CompleteLine_RA.setVisibility(View.INVISIBLE);
                v_RightsLine_RA.setVisibility(View.VISIBLE);
                break;

        }
    }

    /**
     * 功能选择，参数：1，个申诉维权；2，取消订单；3，确认完成
     */
    @Override
    public void setOnFunctionChose(int which, View v) {
        int position = (int) v.getTag();
        RewardInfo.Result.Lists info = adapter.getItem(position);
        switch (which) {
            case 1:
                ToastUtil.shortshow(this, "点击了第" + position + "个申诉维权");
                startActivity(ComplaintActivity.class);
                break;
            case 2:
                ToastUtil.shortshow(this, "点击了第" + position + "取消订单");
                break;
            case 3:
                ToastUtil.shortshow(this, "点击了第" + position + "确认完成");
                break;
        }
    }

    /**
     * Callback method to be invoked when an item in this AdapterView has
     * been clicked.
     * <p/>
     * Implementers can call getItemAtPosition(position) if they need
     * to access the data associated with the selected item.
     *
     * @param parent   The AdapterView where the click happened.
     * @param view     The view within the AdapterView that was clicked (this
     *                 will be a view provided by the adapter)
     * @param position The position of the view in the adapter.
     * @param id       The row id of the item that was clicked.
     */
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        RewardInfo.Result.Lists info = adapter.getList().get(position - 1);
        int task_type = info.content_type;
        Bundle bundle=new Bundle();
        switch (task_type) {
            //线下悬赏
            case 1:
                Bundle bundle_Offline=new Bundle();
                bundle_Offline.putString("id",info.id);
                bundle_Offline.putString("receiver_appeal",info.receiver_appeal);
                bundle_Offline.putString("process_id",adapter.getItem(position - 1).id);
                bundle_Offline.putInt("status",info.status);
                startActivity(RewardOffLineDetailActivity.class,bundle_Offline);
                break;
            //sos
            case 5:
                Log.i(TAG, "onItemClick: case 5");
//                //中途放弃
//                if(5==info.content_type){
//                    Bundle bundle_SOS=new Bundle();
//                    bundle_SOS.putString("id",info.id);
//                    bundle_SOS.putInt("type",2);
//                    Log.i(TAG, "start SOSNavigationActivity");
//                    startActivity(SOSNavigationActivity.class,bundle_SOS);
//                    return;
//                }
                Bundle bundle_SOS=new Bundle();
                bundle_SOS.putString("id",info.id);
                bundle_SOS.putInt("type",2);
                Log.i(TAG, "start RewardSOSDetailActivity");
                startActivity(RewardNewSOSDetailActivity.class,bundle_SOS);
                break;
            //网络悬赏
            case 7:
                bundle.putString("id",info.id);
                bundle.putInt("status",info.status);//0-发布未支付，1-发单成功，2-订单结束（停止招募）
                bundle.putInt("process_status",info.process_status);
                startActivity(RewardOnLineDetailActivity.class,bundle);

                break;
        }

    }
}
