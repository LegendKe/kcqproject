package com.djx.pin.personal.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.View;

import com.djx.pin.R;
import com.djx.pin.beans.ServiceEntity;
import com.djx.pin.beans.StaticBean;
import com.djx.pin.beans.UserInfo;
import com.djx.pin.personal.adapter.ServiceAdapter;
import com.djx.pin.serverapiconfig.ServerAPIConfig;
import com.djx.pin.ui.CommonDialog;
import com.djx.pin.utils.AndroidAsyncHttp;
import com.djx.pin.utils.LogUtil;
import com.djx.pin.utils.ToastUtil;
import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cz.msebera.android.httpclient.Header;

/**
 * Created by 柯传奇 on 2016/10/21 0021.
 */
public class ServiceActivity extends AppCompatActivity{

    @Bind(R.id.recyclerView)
    RecyclerView recyclerView;
    @Bind(R.id.swiperefreshlayout)
    SwipeRefreshLayout swipeRefreshLayout;

    private LinearLayoutManager linearLayoutManager;
    private ServiceAdapter adapter;
    private String session_id;
    private int lastVisibleItem;
    private List<ServiceEntity.ServiceBean> beanList;
    private int total;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service);
        ButterKnife.bind(this);
        recyclerView.setHasFixedSize(true);
        linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        adapter = new ServiceAdapter(this);
        recyclerView.setAdapter(adapter);
        swipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_light, android.R.color.holo_red_light, android.R.color.holo_orange_light, android.R.color.holo_green_light);

        initData();

        initEvent();

    }

    private void initEvent() {
        //0则不执行拖动或者滑动
        ItemTouchHelper.Callback mCallback = new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP|ItemTouchHelper.DOWN,ItemTouchHelper.RIGHT|ItemTouchHelper.LEFT) {

            /**
             * @param recyclerView
             * @param viewHolder 拖动的ViewHolder
             * @param target 目标位置的ViewHolder
             * @return
             */
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
              return false;
            }
            @Override
            public void onSwiped(final RecyclerView.ViewHolder viewHolder, int direction) {
                final int position = viewHolder.getAdapterPosition();
               CommonDialog.show(ServiceActivity.this, "确定", "取消", "移除该技能?", new View.OnClickListener() {
                   @Override
                   public void onClick(View v) {
                       String id = adapter.getItem(position).id;
                       adapter.remove(position);
                       deleteSkill(id);
                   }
               }, new View.OnClickListener() {
                   @Override
                   public void onClick(View v) {
                       CommonDialog.dismiss(ServiceActivity.this);
                       initData();
                   }
               });

            }
        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(mCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);


        //下拉刷新
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                initData();
            }
        });


        //上拉加载更多
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                lastVisibleItem =linearLayoutManager.findLastVisibleItemPosition();
            }
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                if (newState ==RecyclerView.SCROLL_STATE_IDLE && lastVisibleItem + 1 ==adapter.getItemCount()) {

                    if(total <= 10){
                        ToastUtil.longshow(ServiceActivity.this,"暂无更多数据");
                    } else if(lastVisibleItem + 1 == total){
                        ToastUtil.longshow(ServiceActivity.this,"暂无更多数据");
                    } else{
                        LogUtil.e("滑动独步----------------------------------");
                        swipeRefreshLayout.setRefreshing(true);
                        //adapter.changeMoreStatus(RefreshFootAdapter.LOADING_MORE);
                        if(total>20){
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    swipeRefreshLayout.setRefreshing(false);
                                    adapter.addAll(beanList.subList(0,20));
                                }
                            }, 2000);

                        }else {

                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    swipeRefreshLayout.setRefreshing(false);
                                    adapter.addAll(beanList);
                                }
                            }, 2000);
                        }

                    }
                }
            }
        });
        //技能修改
        adapter.setSkillModifyListener(new ServiceAdapter.SkillModifyListener() {
            @Override
            public void skillModify(ServiceEntity.ServiceBean serviceBean) {
                if (UserInfo.getIsLogin(ServiceActivity.this)) {//已登录
                    Intent intent = new Intent(ServiceActivity.this, AddServiceActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("serviceBean",serviceBean);
                    intent.putExtras(bundle);
                    startActivityForResult(intent,100);
                }else {
                    ToastUtil.longshow(ServiceActivity.this,"请登录");
                }
            }
        });
    }

    private void deleteSkill(String skill_id) {

        String url = ServerAPIConfig.DELETE_SKILL_LIST;
        RequestParams params = new RequestParams();
        params.put("session_id",session_id);
        params.put("skill_id",skill_id);

        AsyncHttpResponseHandler res = new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                String str = new String(bytes);
                Log.e("str=====", str);
                try {
                    JSONObject obj = new JSONObject(str);
                    if (obj.getInt("code") == 0) {
                    ToastUtil.longshow(ServiceActivity.this,"移除成功");
                    } else {
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {

            }
        };
        AndroidAsyncHttp.post(url, params, res);
    }

    @OnClick(R.id.tv_add)
    void addService(View view){
        if (UserInfo.getIsLogin(this)) {//已登录
            Intent intent = new Intent(this, AddServiceActivity.class);
            startActivityForResult(intent,100);
        }else {
            ToastUtil.longshow(this,"请登录");
        }
    }


    @OnClick(R.id.iv_back)
    void back(View v){
        finish();
    }
    private void initData() {

        session_id = getSharedPreferences(StaticBean.USER_INFO, Context.MODE_PRIVATE).getString("session_id", null);
        final String user_id = getSharedPreferences(StaticBean.USER_INFO, Context.MODE_PRIVATE).getString("user_id", null);

        String url = ServerAPIConfig.SKILL_LIST;
        RequestParams params = new RequestParams();
        params.put("session_id",session_id);
        params.put("user_id",user_id);

        AsyncHttpResponseHandler res = new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                String str = new String(bytes);
                Log.e("str=====", str);
                try {
                    JSONObject obj = new JSONObject(str);
                    if (obj.getInt("code") == 0) {
                        swipeRefreshLayout.setRefreshing(false);
                        Gson gson = new Gson();
                        ServiceEntity serviceEntity = gson.fromJson(obj.getJSONObject("result").toString(), ServiceEntity.class);
                        total = serviceEntity.total;
                        beanList = serviceEntity.list;
                        if(beanList.size() > 10){
                            adapter.addAll(beanList.subList(0,10));
                        }else {
                            adapter.addAll(beanList);
                        }

                    } else {
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                if (swipeRefreshLayout.isRefreshing()) {
                    swipeRefreshLayout.setRefreshing(false);
                }
                ToastUtil.shortshow(ServiceActivity.this, R.string.toast_error_net);
            }
        };
        AndroidAsyncHttp.post(url, params, res);
    }




    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == RESULT_OK){
            initData();
        }
    }


}
