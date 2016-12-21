package com.djx.pin.improve.helpmap;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.djx.pin.R;
import com.djx.pin.activity.LookOthersMassageActivity;
import com.djx.pin.beans.SkillSearchEntity;
import com.djx.pin.beans.StaticBean;
import com.djx.pin.improve.helpmap.adapter.SearchSkillAdapter;
import com.djx.pin.serverapiconfig.ServerAPIConfig;
import com.djx.pin.utils.AndroidAsyncHttp;
import com.djx.pin.utils.ToastUtil;
import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.util.TextUtils;

/**
 * Created by 柯传奇 on 2016/10/26 0026.
 */
public class SearchActivity extends AppCompatActivity{

    private EditText et_search;
    private TextView tv_cance;
    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private LinearLayoutManager linearLayoutManager;
    private SearchSkillAdapter adapter;
    private Bundle intentBundle;
    private ProgressDialog progressDialog;//显示进度
    private TextView tv_no_data;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);


        et_search = ((EditText) findViewById(R.id.et_search));
        tv_cance = ((TextView) findViewById(R.id.tv_search));
        recyclerView = ((RecyclerView) findViewById(R.id.recyclerView));
        tv_no_data = ((TextView) findViewById(R.id.tv_no_data));
        swipeRefreshLayout = ((SwipeRefreshLayout) findViewById(R.id.swiperefreshlayout));
        linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        adapter = new SearchSkillAdapter(this);
        recyclerView.setAdapter(adapter);
        swipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_light, android.R.color.holo_red_light, android.R.color.holo_orange_light, android.R.color.holo_green_light);

        intentBundle = getIntent().getExtras();//上界面传过来的经纬度数据

        initEvent();

    }

    private void initEvent() {

        //刷新
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                initData();
            }
        });
        //加载更多
        adapter.setItemClickListener(new SearchSkillAdapter.ItemClickListener() {
            @Override
            public void onItemClick(int position, View itemView) {
                Bundle bundle = new Bundle();
                bundle.putString("user_id", adapter.getItem(position).user_id);
                bundle.putString("nickName", adapter.getItem(position).nickname);
                bundle.putDouble("distance", adapter.getItem(position).distance);
                Intent intent = new Intent(SearchActivity.this,LookOthersMassageActivity.class);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
        tv_cance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(tv_cance.getText().toString().trim().equals("取消")){
                    finish();
                }else if(tv_cance.getText().toString().trim().equals("搜索")){
                    initData();
                }
            }
        });
        et_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                 if(TextUtils.isEmpty(s)){
                     tv_cance.setText("取消");
                 }else if(tv_cance.getText().toString().trim().equals("取消")){
                     tv_cance.setText("搜索");
                 }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }
    /**
     *
     "session_id": "4085c9d4f420864c407e",
     "key_word": "搬家",
     "latitude": 35,
     "longitude": 117,
     "index": 0,
     "size": 10
     */
    private void initData() {
        if(progressDialog == null){
            progressDialog = new ProgressDialog(this);
        }
        progressDialog.setMessage("加载中");
        progressDialog.show();
        SharedPreferences sharedPreferences = getSharedPreferences(StaticBean.USER_INFO, Context.MODE_PRIVATE);
        String session_id = sharedPreferences.getString("session_id", null);
        RequestParams params = new RequestParams();
        params.put("session_id",session_id);
        params.put("key_word",et_search.getText().toString().trim());
        params.put("latitude",intentBundle.getDouble("latitude"));
        params.put("longitude",intentBundle.getDouble("longitude"));
        params.put("index",0);
        params.put("size",10);
        AsyncHttpResponseHandler res = new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                progressDialog.dismiss();
                String str = new String(bytes);
                Log.e("str=====", str);
                try {
                    JSONObject obj = new JSONObject(str);
                    if (obj.getInt("code") == 0) {
                        swipeRefreshLayout.setRefreshing(false);
                        Gson gson = new Gson();
                        SkillSearchEntity searchEntity = gson.fromJson(obj.getJSONObject("result").toString(), SkillSearchEntity.class);
                        List<SkillSearchEntity.SkillBean> list = searchEntity.list;
                        if(list == null || list.size() == 0){
                            tv_no_data.setVisibility(View.VISIBLE);
                        }else {
                            tv_no_data.setVisibility(View.GONE);
                            adapter.addAll(list);
                        }
                    } else {
                        ToastUtil.errorCode(SearchActivity.this,obj.getInt("code"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                progressDialog.dismiss();
                if (swipeRefreshLayout.isRefreshing()) {
                    swipeRefreshLayout.setRefreshing(false);
                }
                ToastUtil.shortshow(SearchActivity.this, R.string.toast_error_net);
            }
        };
        AndroidAsyncHttp.post(ServerAPIConfig.SEARCH_SKILL, params, res);
    }



}
