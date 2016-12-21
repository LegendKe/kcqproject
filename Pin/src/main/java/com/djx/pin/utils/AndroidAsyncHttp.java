package com.djx.pin.utils;
/**
 * AsyncHttp异步网络请求库
 */

import android.content.Context;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.BinaryHttpResponseHandler;
import com.loopj.android.http.FileAsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import cz.msebera.android.httpclient.HttpEntity;

public class AndroidAsyncHttp {
    private static AsyncHttpClient client = new AsyncHttpClient(); // 实例话对象

    static {

        client.setTimeout(10000); // 设置链接超时，如果不设置，默认为10s 
    }

    // 没有参数的get方法 
    public static void get(String urlString, AsyncHttpResponseHandler res) {

        client.get(urlString, res);

    }

    // url里面带参数的get方法 
    public static void get(String urlString, RequestParams params,
                           AsyncHttpResponseHandler res) {

        client.get(urlString, params, res);

    }

    // 不带参数，获取json对象或者数组 
    public static void get(String urlString, JsonHttpResponseHandler res) {
        client.get(urlString, res);

    }

    // 带参数，获取json对象或者数组 
    public static void get(String urlString, RequestParams params, JsonHttpResponseHandler res) {

        client.get(urlString, params, res);

    }

    // 下载数据使用，会返回byte数据 
    public static void get(String uString, BinaryHttpResponseHandler bHandler) {

        client.get(uString, bHandler);

    }

    // 下载数据使用，会返回file文件数据
    public static void get(String uString, FileAsyncHttpResponseHandler fHandler) {

        client.get(uString, fHandler);

    }

    // post方式有参（可上传文件）
    public static void post(String uString, RequestParams params, FileAsyncHttpResponseHandler upHandler) {

        client.post(uString, params, upHandler);

    }

    // post无参
    public static void post(String uString, FileAsyncHttpResponseHandler upHandler) {

        client.post(uString, upHandler);

    }

    public static void post(String urlString, RequestParams params, JsonHttpResponseHandler res) {
        client.post(urlString, params, res);
    }

    //post带参数返回字符串
    public static void post(String urlString, RequestParams params, AsyncHttpResponseHandler res) {

        client.post(urlString, params, res);

    }

    //post 上传Json字符串
    public static void post(Context context, String url, HttpEntity entity, String contentType, AsyncHttpResponseHandler res) {

        client.post(context, url, entity, contentType, res);

    }


    public static AsyncHttpClient getClient() {

        return client;
    }


}
