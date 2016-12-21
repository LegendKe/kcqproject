package com.djx.pin.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.djx.pin.R;
import com.djx.pin.adapter.RewardOnLineUpload_MediaAdapter;
import com.djx.pin.adapter.RewardOnLineUpload_OtherAdapter;
import com.djx.pin.base.OldBaseActivity;
import com.djx.pin.beans.RewardOnlineDetailInfo;
import com.djx.pin.beans.StaticBean;
import com.djx.pin.improve.common.PhotoShowActivity;
import com.djx.pin.myview.CircleImageView;
import com.djx.pin.personal.ui.PersonalDataActivity;
import com.djx.pin.serverapiconfig.ServerAPIConfig;
import com.djx.pin.utils.AndroidAsyncHttp;
import com.djx.pin.utils.GridItemDecoration;
import com.djx.pin.utils.MyItemDecoration;
import com.djx.pin.utils.QiniuUtils;
import com.djx.pin.utils.TurnIntoTime;
import com.djx.pin.widget.CustomImageView;
import com.djx.pin.widget.NineGridLayout;
import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpResponseHandler;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;

/**
 * Created by Administrator on 2016/7/27 0027.
 */
public class RewardOnLineDetailActivity extends OldBaseActivity implements View.OnClickListener, RewardOnLineUpload_MediaAdapter.OnLookImageListener {

    LinearLayout ll_Back_LRDA, ll_ViewGroup2_CDA;
    RewardOnLineUpload_MediaAdapter mediaAdapter;
    RewardOnLineUpload_OtherAdapter otherAdapter;
    RecyclerView rv_recyclerView;
    LinearLayoutManager linearLayoutManager;
    GridLayoutManager gridLayoutManager;
    View v_ParentCover_CDA;

    //悬赏内容Id
    String id;
    //网络悬赏类型
    int tsakType;
    //图片数量
    int imageNumber;
    private int status;//订单状态
    private RewardOnlineDetailInfo.Result result;
    private NineGridLayout nineGridLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        id = getIntent().getStringExtra("id");
        status = getIntent().getIntExtra("status",-1);
        setContentView(R.layout.activity_rewarddetailonline);
        initView();
        initEvent();
        //基本资料信息
        requestBaseData();
    }

    private void initEvent() {
        ll_Back_LRDA.setOnClickListener(this);
        rv_recyclerView.addOnScrollListener(scrollListener);
    }
    //当前正在显示的界面,1:分享;2,评论;3,已上传
    int showTab = 3;
    //请求的页数
    int index_upload = 0;
    RecyclerView.OnScrollListener scrollListener = new RecyclerView.OnScrollListener() {

        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
        }
        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            int fristVisiblePosition = 0;
            RecyclerView.LayoutManager manager = recyclerView.getLayoutManager();
            if (manager instanceof LinearLayoutManager) {
                fristVisiblePosition = ((LinearLayoutManager) manager).findFirstVisibleItemPosition();
            } else if (manager instanceof GridLayoutManager) {
                fristVisiblePosition = ((GridLayoutManager) manager).findFirstVisibleItemPosition();
            }
            if (fristVisiblePosition >= 1) {

                if (ll_Top_CivilizationDetail.getParent() != ll_ViewGroup2_CDA) {

                    ll_ViewGroup2_CDA.setVisibility(View.VISIBLE);
                    ll_ViewGroup1_CDA.removeView(ll_Top_CivilizationDetail);
                    ll_ViewGroup2_CDA.addView(ll_Top_CivilizationDetail);
                }
            } else {
                if (ll_Top_CivilizationDetail.getParent() != ll_ViewGroup1_CDA) {
                    ll_ViewGroup2_CDA.setVisibility(View.GONE);
                    ll_ViewGroup2_CDA.removeView(ll_Top_CivilizationDetail);
                    ll_ViewGroup1_CDA.addView(ll_Top_CivilizationDetail);
                }
            }
        }
    };

    /**
     * 头部点击事件
     */
    private void initHeadEnvent() {
        cimg_Avatar_MHDA.setOnClickListener(this);
        ll_ShowAllContent_MHDA.setOnClickListener(this);
        tv_UploadLink_MHDA.setOnClickListener(this);
    }


    private void initView() {
        rv_recyclerView = (RecyclerView) findViewById(R.id.rv_recyclerView);
        ll_Back_LRDA = (LinearLayout) findViewById(R.id.ll_Back_LRDA);
        ll_ViewGroup2_CDA = (LinearLayout) findViewById(R.id.ll_ViewGroup2_CDA);
        v_ParentCover_CDA = findViewById(R.id.v_ParentCover_CDA);
        gridLayoutManager = new GridLayoutManager(this, 3);
        linearLayoutManager = new LinearLayoutManager(this);
        rv_recyclerView.setLayoutManager(gridLayoutManager);
        mediaAdapter = new RewardOnLineUpload_MediaAdapter(this,this);
        mediaAdapter.setStatus(status);
        otherAdapter = new RewardOnLineUpload_OtherAdapter(this);
        otherAdapter.setStatus(status);
    }

    View headView;

    CircleImageView cimg_Avatar_MHDA;
    TextView tv_UserName_MHDA, tv_Time_MHDA, tv_UploadLink_MHDA, tv_TaskReward_MHDA, tv_DoTaskCount_MHDA, tv_Content_MHDA, tv_ShowAllContent_MHDA,
            tv_LookTimes, tv_JoinNumber, tv_TaskLife_MHDA;
    LinearLayout ll_ShowAllContent_MHDA, ll_ViewGroup1_CDA, ll_Top_CivilizationDetail;
    ImageView img_ShowAllContent_MHDA;
    View v_UploadLine_CDA, v_ShareLine_CDA, v_CommentLine_CDA;
    View footerView;
    //悬赏的类型，1:照片，2:视频，3:其他
    /**
     * 添加头布局
     */
    private void addHeadView() {
        /**
         * 给头部设置大小，必须要有
         * */
        AbsListView.LayoutParams layoutParams = new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        headView = LayoutInflater.from(this).inflate(R.layout.headview_rewarddetailonline, rv_recyclerView, false);
        headView.setLayoutParams(layoutParams);
        footerView = LayoutInflater.from(this).inflate(R.layout.footer_loadmore, rv_recyclerView, false);
        footerView.setLayoutParams(layoutParams);

        //头部view和底部view
        initHeaderView();
        initHeadEnvent();
        initFooterView();
        switch (tsakType) {
            case 1:
                requestUploadMedia(0);
                rv_recyclerView.setLayoutManager(gridLayoutManager);
                mediaAdapter.addHeader(headView);
                mediaAdapter.addFooter(footerView);
                rv_recyclerView.setAdapter(mediaAdapter);
                break;
            case 2://视频
                requestUploadMedia(0);
                rv_recyclerView.setLayoutManager(gridLayoutManager);
                mediaAdapter.addHeader(headView);
                mediaAdapter.addFooter(footerView);
                rv_recyclerView.setAdapter(mediaAdapter);
                break;
            case 3:
                requestUploadOther(0);
                rv_recyclerView.setLayoutManager(linearLayoutManager);
                otherAdapter.addHeader(headView);
                otherAdapter.addFooter(footerView);
                rv_recyclerView.setAdapter(otherAdapter);
                break;
        }
        decor = new MyItemDecoration(this);
        gridItemDecoration = new GridItemDecoration(this, true);
        rv_recyclerView.addItemDecoration(decor);
    }
    //底部加载更多控件
    TextView tv_LoadMore;

    private void initFooterView() {
        tv_LoadMore = (TextView) footerView.findViewById(R.id.tv_LoadMore);
        tv_LoadMore.setVisibility(View.GONE);
    }

    MyItemDecoration decor;
    GridItemDecoration gridItemDecoration;

    private void initHeaderView() {
        //头部控件初始化
        cimg_Avatar_MHDA = (CircleImageView) headView.findViewById(R.id.cimg_Avatar_MHDA);
        tv_UserName_MHDA = (TextView) headView.findViewById(R.id.tv_UserName_MHDA);
        tv_Time_MHDA = (TextView) headView.findViewById(R.id.tv_Time_MHDA);
        tv_TaskReward_MHDA = (TextView) headView.findViewById(R.id.tv_TaskReward_MHDA);
        tv_UploadLink_MHDA = (TextView) headView.findViewById(R.id.tv_UploadLink_MHDA);
        tv_DoTaskCount_MHDA = (TextView) headView.findViewById(R.id.tv_DoTaskCount_MHDA);
        tv_Content_MHDA = (TextView) headView.findViewById(R.id.tv_Content_MHDA);
        tv_ShowAllContent_MHDA = (TextView) headView.findViewById(R.id.tv_ShowAllContent_MHDA);
        tv_LookTimes = (TextView) headView.findViewById(R.id.tv_LookTimes);
        tv_JoinNumber = (TextView) headView.findViewById(R.id.tv_JoinNumber);

        tv_TaskLife_MHDA = (TextView) headView.findViewById(R.id.tv_TaskLife_MHDA);

        ll_ShowAllContent_MHDA = (LinearLayout) headView.findViewById(R.id.ll_ShowAllContent_MHDA);

        ll_ViewGroup1_CDA = (LinearLayout) headView.findViewById(R.id.ll_ViewGroup1_CDA);
        ll_Top_CivilizationDetail = (LinearLayout) headView.findViewById(R.id.ll_Top_CivilizationDetail);

        img_ShowAllContent_MHDA = (ImageView) headView.findViewById(R.id.img_ShowAllContent_MHDA);
        v_UploadLine_CDA = headView.findViewById(R.id.v_UploadLine_CDA);
        v_ShareLine_CDA = headView.findViewById(R.id.v_ShareLine_CDA);
        v_CommentLine_CDA = headView.findViewById(R.id.v_CommentLine_CDA);
        nineGridLayout = ((NineGridLayout) headView.findViewById(R.id.imgs_9grid_layout));

        final ArrayList<String> ids = new ArrayList<>();
        if(result.media != null && result.media.size() > 0){
            for (int i = 0; i < result.media.size(); i++) {
                ids.add(result.media.get(i).media_id);
            }
        }
        QiniuUtils.set9GridByIdsFrom7Niu(this,ids,result.id,nineGridLayout);
        nineGridLayout.setImageShowListener(new NineGridLayout.ImageShowListener() {
            @Override
            public void imageShow(int imgPos,ArrayList<CustomImageView> imageViews) {
                if (ids != null) {
                    Intent intent = new Intent(RewardOnLineDetailActivity.this, PhotoShowActivity.class);
                    intent.putExtra("CURRENT_POS", imgPos);
                    intent.putStringArrayListExtra("IDS", ids);
                    startActivity(intent);
                }
            }
        });
    }


    /**
     * 添加已上传数据
     */
    public void requestUploadOther(int index_upload) {
        String session_id = getSharedPreferences(StaticBean.USER_INFO, Context.MODE_PRIVATE).getString("session_id", null);
        final int index = index_upload;
        int size = 9;
        int type = 4;
        String url = ServerAPIConfig.OnlineReward + "session_id=" + session_id + "&id=" + id + "&index=" + index + "&size=" + size + "&type=" + type + "";
        final AsyncHttpResponseHandler res = new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                String str = new String(bytes);
                Gson gson = new Gson();
                RewardOnlineDetailInfo info = gson.fromJson(str, RewardOnlineDetailInfo.class);
                if (info.code == 0) {
                    RewardOnlineDetailInfo.Result result = info.result;
                    receiver_num = result.receiver_num;
                    List<RewardOnlineDetailInfo.Result.Receiver> receiver = result.receiver;
                    initUploadOtherData(receiver);
                } else {
                    errorCode(info.code);
                }
            }
            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
            }
        };
        AndroidAsyncHttp.get(url, res);
    }

    /**
     * 添加已上传数据
     */
    public void requestUploadMedia(int index_upload) {
        String session_id = getSharedPreferences(StaticBean.USER_INFO, Context.MODE_PRIVATE).getString("session_id", null);
        final int index = index_upload;
        int size = 9;
        int type = 4;
        String url = ServerAPIConfig.OnlineReward + "session_id=" + session_id + "&id=" + id + "&index=" + index + "&size=" + size + "&type=" + type + "";
        final AsyncHttpResponseHandler res = new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                String str = new String(bytes);
                Gson gson = new Gson();
                RewardOnlineDetailInfo info = gson.fromJson(str, RewardOnlineDetailInfo.class);
                if (info.code == 0) {
                    RewardOnlineDetailInfo.Result result = info.result;
                    receiver_num = result.receiver_num;
                    List<RewardOnlineDetailInfo.Result.Receiver> receiver = result.receiver;
                    initUploadMediaData(receiver);
                } else {
                    errorCode(info.code);
                }
            }
            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
            }
        };
        AndroidAsyncHttp.get(url, res);
    }

    private void initUploadMediaData(List<RewardOnlineDetailInfo.Result.Receiver> receiver) {
        for (int i = 0; i < receiver.size(); i++) {
            RewardOnlineDetailInfo.Result.Receiver info = receiver.get(i);
            if (info.receiver_id.equals(getUser_id())) {
                mediaAdapter.addData(info);
            }
        }
        mediaAdapter.notifyDataSetChanged();
    }

    /**
     * 添加已上传数据
     */
    String shareUrl;

    public void requestBaseData() {
        String session_id = getSharedPreferences(StaticBean.USER_INFO, Context.MODE_PRIVATE).getString("session_id", null);
        final int index = index_upload;
        int size = 9;
        int type = 4;
        String url = ServerAPIConfig.OnlineReward + "session_id=" + session_id + "&id=" + id + "&index=" + index + "&size=" + size + "&type=" + type + "";
        shareUrl = url;
        final AsyncHttpResponseHandler res = new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                String str = new String(bytes);
                Gson gson = new Gson();
                RewardOnlineDetailInfo info = gson.fromJson(str, RewardOnlineDetailInfo.class);
                if (info.code == 0) {
                    result = info.result;
                    //添加基本信息
                    initBaseDate(result);
                } else {
                    errorCode(info.code);
                }
            }
            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
            }
        };

        AndroidAsyncHttp.get(url, res);
    }
    /**
     * 添加公共部分基本控件
     */
    //已上传人数,分享人数,评论人数
    int receiver_num;
    //被分享内容id
    String target_id;
    //订单状态
    int taskStutes;
    //发单者user_id
    String task_user_id;

    private void initBaseDate(RewardOnlineDetailInfo.Result result) {
        target_id = result.id;
        taskStutes = result.status;
        task_user_id = result.user_id;
        tsakType = result.type;
        List<RewardOnlineDetailInfo.Result.Receiver> receiverList = result.receiver;
        int rewardedNum = 0;
        for (int i = 0; i < receiverList.size(); i++) {
            if(receiverList.get(i).status == 1){//状态，0-已抢单（已回答），1-已打赏，2-拒绝打赏
                rewardedNum++;
            }
        }
        int receiver_limit = result.receiver_limit;
        mediaAdapter.setRewarded2limitNum(rewardedNum,receiver_limit);
        otherAdapter.setRewarded2limitNum(rewardedNum,receiver_limit);

        mediaAdapter.setMediaType(tsakType);//判断视频还是图片
        imageNumber = result.media.size();
        addHeadView();
        QiniuUtils.setAvatarByIdFrom7Niu(this,cimg_Avatar_MHDA,result.portrait);
        tv_UserName_MHDA.setText(result.nickname);
//        int type = result.type;

        if (taskStutes != 2) {
            switch (tsakType) {
                case 1:
                    tv_UploadLink_MHDA.setText("照片上传");
                    break;
                case 2:
                    tv_UploadLink_MHDA.setText("视频链接");
                    break;
                case 3:
                    tv_UploadLink_MHDA.setText("文本上传");
                    break;
            }
        } else {
            tv_UploadLink_MHDA.setText("悬赏已结束");
        }

        tv_TaskReward_MHDA.setText(result.price + "元");
        tv_DoTaskCount_MHDA.setText(result.receiver_limit + "");
        String create_time = TurnIntoTime.getLifeTime(result.create_time);
        String end_time = TurnIntoTime.getLifeTime(result.end_time);
        tv_TaskLife_MHDA.setText(create_time + "至" + end_time);
        tv_Content_MHDA.setText(result.description);
        tv_LookTimes.setText(result.view_num + "次浏览");
        if (result.partake.size() == 0) {
            tv_JoinNumber.setText("暂时无人参与");
        } else {
            tv_JoinNumber.setText(result.partake.get(0).nickname + "等" + result.partake.size() + "人参与");
        }
    }

    /**
     * 添加其它数据
     */
    private void initUploadOtherData(List<RewardOnlineDetailInfo.Result.Receiver> receiver) {
        for (int i = 0; i < receiver.size(); i++) {
            RewardOnlineDetailInfo.Result.Receiver info = receiver.get(i);
            otherAdapter.addData(info);
        }
        otherAdapter.notifyDataSetChanged();
    }

    //是否全文显示
    boolean isShowAll = true;

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            //返回
            case R.id.ll_Back_LRDA:
                this.finish();
                break;

            //点击头像
            case R.id.cimg_Avatar_MHDA:
                String user_id = getUser_id();
                if (task_user_id.equals(user_id)) {
                    startActivity(PersonalDataActivity.class);
                } else {
                    Bundle bundle = new Bundle();
                    bundle.putString("user_id", task_user_id);
                    startActivity(LookOthersMassageActivity.class, bundle);
                }
                break;
            //点击回复
            case R.id.tv_UploadLink_MHDA:

                break;

            //查看上传
            case R.id.tv_Upload_CDA:
                showTab = 3;
                v_UploadLine_CDA.setVisibility(View.VISIBLE);

                v_UploadLine_CDA.setBackgroundColor(getResources().getColor(R.color.text_color_focused));
                v_CommentLine_CDA.setVisibility(View.INVISIBLE);
                v_ShareLine_CDA.setVisibility(View.INVISIBLE);

                //如果悬浮则不显示第一个item
                //如果不是悬浮则直接返回

                if (ll_Top_CivilizationDetail.getParent() == ll_ViewGroup2_CDA) {
                    if (tsakType == 3) {
                        rv_recyclerView.setLayoutManager(linearLayoutManager);
                        rv_recyclerView.setAdapter(otherAdapter);
                    } else {
                        rv_recyclerView.setLayoutManager(gridLayoutManager);
                        rv_recyclerView.setAdapter(mediaAdapter);
                    }
                } else {
                    if (tsakType == 3) {
                        rv_recyclerView.setLayoutManager(linearLayoutManager);
                        rv_recyclerView.setAdapter(otherAdapter);
                    } else {
                        rv_recyclerView.setLayoutManager(gridLayoutManager);
                        rv_recyclerView.setAdapter(mediaAdapter);
                    }
                }
                break;
        }
    }





    public void parentCover(int i) {
        switch (i) {
            case 1:
                v_ParentCover_CDA.setVisibility(View.VISIBLE);
                v_ParentCover_CDA.setAlpha(0.5f);
                break;
            case 2:
                v_ParentCover_CDA.setVisibility(View.GONE);
                break;
        }
    }

    @Override
    public void setOnLookImageListener(View v, int tag) {
        RewardOnlineDetailInfo.Result.Receiver info= (RewardOnlineDetailInfo.Result.Receiver) mediaAdapter.getList().get((Integer) v.getTag());
        switch (tag){
        case 1:
            if(tsakType == 2){//视频
                Intent intent = new Intent(RewardOnLineDetailActivity.this, VideoPlayActivity.class);
                intent.putExtra("VIDEO_URL",info.content);
                startActivity(intent);
            }
    }
    }
}
