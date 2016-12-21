package com.djx.pin.improve.helpmap;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.djx.pin.R;
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

import cz.msebera.android.httpclient.Header;

/**
 * Created by 柯传奇 on 2016/10/26 0026.
 */
public class MyListFragment extends Fragment{

    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private LinearLayoutManager linearLayoutManager;
    private SearchSkillAdapter adapter;
    private String session_id;
    private int size = 10;
    private double latitude;
    private double longitude;
    private String key_word;
    private Bundle bundle;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_list, null);
        bundle = getArguments();
        latitude = bundle.getDouble("latitude");
        longitude = bundle.getDouble("longitude");


        recyclerView = ((RecyclerView) view.findViewById(R.id.recyclerView));
        swipeRefreshLayout = ((SwipeRefreshLayout) view.findViewById(R.id.swiperefreshlayout));

        linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(linearLayoutManager);
        adapter = new SearchSkillAdapter(getActivity());
        recyclerView.setAdapter(adapter);
        swipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_light, android.R.color.holo_red_light, android.R.color.holo_orange_light, android.R.color.holo_green_light);

        initData();

        initEvent();

        return view;

    }

    private void initEvent() {

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                initData();
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
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(StaticBean.USER_INFO, Context.MODE_PRIVATE);
        session_id = sharedPreferences.getString("session_id", null);
        RequestParams params = new RequestParams();
        params.put("session_id",session_id);
        params.put("key_word",bundle.getString("key_word"));
        params.put("latitude",latitude);
        params.put("longitude",longitude);
        params.put("index",0);
        params.put("size",size);

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
                        SkillSearchEntity searchEntity = gson.fromJson(obj.getJSONObject("result").toString(), SkillSearchEntity.class);
                        if( searchEntity!= null && searchEntity.list != null){
                            adapter.addAll(searchEntity.list);
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
                ToastUtil.shortshow(getActivity(), R.string.toast_error_net);
            }
        };
        AndroidAsyncHttp.post(ServerAPIConfig.SEARCH_SKILL, params, res);
    }

    public void setOnSearchListenber() {
        bundle = getArguments();
        initData();
    }
}
