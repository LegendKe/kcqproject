package com.djx.pin.base;

import android.app.Application;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

/**
 * Created by 陈刘磊 (代号：姜饼（GingerBread）) on 2016/5/26.
 */

public class OldBaseApplication extends Application {


    /**
     * 定义全局变量Volley
     */
    private static RequestQueue requestQueue;

    @Override
    public void onCreate() {
        super.onCreate();
        if (requestQueue==null){
            requestQueue = Volley.newRequestQueue(getApplicationContext());

        }
    }
    /**
     * 获取全局变量Volley
     */
    public static RequestQueue getRequestQueue() {
        return requestQueue;
    }
}
