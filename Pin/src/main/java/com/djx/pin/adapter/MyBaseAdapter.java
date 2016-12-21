package com.djx.pin.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.djx.pin.R;
import com.djx.pin.serverapiconfig.ServerAPIConfig;
import com.djx.pin.utils.AndroidAsyncHttp;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;

public abstract class MyBaseAdapter<T> extends BaseAdapter {
    protected List<T> list;
    protected Context context;
    protected Map urlMap;

    public List<T> getList() {
        return list;
    }

    public void setList(List<T> list) {
        this.list = list;
    }

    protected LayoutInflater inflater;

    public void addData(T t) {
        list.add(t);
    }
    public void addData2Refresh(T t) {
        list.removeAll(list);
        list.add(t);
    }
    public void addData(int position, T t) {
        list.add(position, t);
    }


    public void addDataList(Collection<? extends T> t) {
        list.addAll(t);
    }



    public void deletData(T t) {
        list.remove(t);
    }

    public void deletDataList(Collection<? extends T> t) {
        list.removeAll(t);
    }

    public void clear() {
        list.clear();
    }

    public MyBaseAdapter(Context context) {
        this.context = context;
        list = new ArrayList<T>();
        inflater = LayoutInflater.from(context);
        urlMap = new HashMap();
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return list.size();
    }

    @Override
    public T getItem(int arg0) {
        // TODO Auto-generated method stub
        return list.get(arg0);
    }

    @Override
    public long getItemId(int arg0) {
        // TODO Auto-generated method stub
        return arg0;
    }

    @Override
    public abstract View getView(int position, View converView, ViewGroup parent);


    /**
     * 请求七牛图片下载地址
     */

    public void getImageViewUrl(final List<ImageView> imageViewList, final int size, final List<String> ids, int media_type) throws UnsupportedEncodingException {
        String id = null;
        String url = null;

        final JSONObject newObj = new JSONObject();
        try {

            int i = 0;
            JSONArray array = new JSONArray();
            for (int l = 0; l < size; l++) {
                id = ids.get(l);

                url = (String) urlMap.get(id);
                JSONObject detaileObj = new JSONObject();
                if (url != null) {
                    Picasso.with(context).load(url).into(imageViewList.get(ids.indexOf(id)));
                } else {
                    detaileObj.put("id", id);
                    detaileObj.put("media_type", media_type);
//                detaileObj.put("height", height);
//                detaileObj.put("width", width);
                    array.put(detaileObj);
                    i++;
                }
            }
            if (i==0){
                return;
            }
            newObj.put("size", i);
            newObj.put("list", array);
            StringEntity entity = new StringEntity(newObj.toString(), "utf-8");

            AsyncHttpResponseHandler res = new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int i, Header[] headers, byte[] bytes) {
                    String str = new String(bytes);
                    try {
                        JSONObject obj = new JSONObject(str);
                        obj = obj.getJSONObject("result");
                        JSONArray array1 = obj.getJSONArray("list");
                        for (int j = 0; j < array1.length(); j++) {

                            obj = array1.getJSONObject(j);
                            urlMap.put(obj.getString("id"), obj.getString("url"));
                            Picasso.with(context)
                                    .load(obj.getString("url"))
                                    .config(Bitmap.Config.RGB_565)
                                    .into(imageViewList.get(ids.indexOf(obj.getString("id"))));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                    Log.e("失败", "失败");
                }
            };
            AndroidAsyncHttp.post(context, ServerAPIConfig.GetQiNiuUrl, entity, " application/json;charset=UTF-8", res);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 请求七牛图片下载地址,只有一个ImageView
     */
    public void getOneImageViewUrl(final ImageView imageView, String id, int media_type, int height, int width) throws UnsupportedEncodingException {
        String url = (String) urlMap.get(id);
        if (url != null) {
            Picasso.with(context).load(url).error(R.mipmap.ic_loading).into(imageView);
            return;
        }
        final JSONObject newObj = new JSONObject();
        try {
            newObj.put("size", 1);

            JSONArray array = new JSONArray();
            JSONObject detaileObj = new JSONObject();
            detaileObj.put("id", id);
            detaileObj.put("media_type", media_type);
            detaileObj.put("height", height);
            detaileObj.put("width", width);
            array.put(detaileObj);
            newObj.put("list", array);
            StringEntity entity = new StringEntity(newObj.toString(), "utf-8");
            AsyncHttpResponseHandler res = new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int i, Header[] headers, byte[] bytes) {
                    String str = new String(bytes);
                    try {
                        JSONObject obj = new JSONObject(str);
                        obj = obj.getJSONObject("result");
                        JSONArray array1 = obj.getJSONArray("list");
                        for (int j = 0; j < array1.length(); j++) {
                            obj = array1.getJSONObject(j);
                            urlMap.put(obj.getString("id"), obj.getString("url"));
                            Picasso.with(context)
                                    .load(obj.getString("url"))
                                    .config(Bitmap.Config.RGB_565)
                                    .error(R.mipmap.ic_loading)
                                    .into(imageView);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                    Log.e("失败", "失败");
                }
            };
            AndroidAsyncHttp.post(context, ServerAPIConfig.GetQiNiuUrl, entity, " application/json;charset=UTF-8", res);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 请求七牛图片下载地址,只有一个ImageView
     */
    public void getOneImageViewUrl(final ImageView imageView, String id, int media_type) throws UnsupportedEncodingException {
        String url = (String) urlMap.get(id);
        if (url != null) {
            Picasso.with(context)
                    .load(url)
                    .error(R.mipmap.ic_loading)
                    .into(imageView);
            return;
        }
        final JSONObject newObj = new JSONObject();
        try {
            newObj.put("size", 1);
            JSONArray array = new JSONArray();
            JSONObject detaileObj = new JSONObject();
            detaileObj.put("id", id);
            detaileObj.put("media_type", media_type);
            detaileObj.put("height", 300);
            detaileObj.put("width", 240);
            array.put(detaileObj);
            newObj.put("list", array);
            StringEntity entity = new StringEntity(newObj.toString(), "utf-8");
            AsyncHttpResponseHandler res = new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int i, Header[] headers, byte[] bytes) {
                    String str = new String(bytes);
                    try {
                        JSONObject obj = new JSONObject(str);
                        obj = obj.getJSONObject("result");
                        JSONArray array1 = obj.getJSONArray("list");
                        for (int j = 0; j < array1.length(); j++) {
                            obj = array1.getJSONObject(j);
                            urlMap.put(obj.getString("id"), obj.getString("url"));
                            Picasso.with(context).load(obj.getString("url")).into(imageView);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                    Log.e("失败", "失败");
                }
            };
            AndroidAsyncHttp.post(context, ServerAPIConfig.GetQiNiuUrl, entity, " application/json;charset=UTF-8", res);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
