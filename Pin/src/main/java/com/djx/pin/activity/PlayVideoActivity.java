package com.djx.pin.activity;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.djx.pin.R;
import com.djx.pin.base.OldBaseActivity;
import com.djx.pin.serverapiconfig.ServerAPIConfig;
import com.djx.pin.utils.AndroidAsyncHttp;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.List;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;

/**
 * Created by Administrator on 2016/8/29 0029.
 */
public class PlayVideoActivity extends OldBaseActivity implements View.OnClickListener {
    protected final static String TAG = PlayVideoActivity.class.getSimpleName();

    LinearLayout ll_Back_PVA;
    WebView wv_PVA;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playvideo);
        initView();
        initEvent();

        WebSettings webSettings = wv_PVA.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDefaultTextEncodingName("UTF-8");
        webSettings.setSupportZoom(true);
        webSettings.setBuiltInZoomControls(true);
        webSettings.setUseWideViewPort(true);
        webSettings.setCacheMode(WebSettings.LOAD_DEFAULT);
        webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setAppCacheEnabled(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setPluginState(WebSettings.PluginState.ON);
        initData();
        //initPlugin();
    }



    private void initData() {


        String id = getIntent().getExtras().getString("id");
        final JSONObject newObj = new JSONObject();
        try {
            newObj.put("size", 1);

            JSONArray array = new JSONArray();
            JSONObject detaileObj = new JSONObject();
            detaileObj.put("id", id);
            detaileObj.put("media_type", 2);
            array.put(detaileObj);
            newObj.put("list", array);
            final StringEntity entity = new StringEntity(newObj.toString(), "utf-8");

            AsyncHttpResponseHandler res = new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int i, Header[] headers, byte[] bytes) {
                    String str = new String(bytes);
                    Log.i(TAG, str);
                    try {
                        JSONObject obj = new JSONObject(str);
                        if (obj.getInt("code")==0){
                            obj = obj.getJSONObject("result");
                            JSONArray array1 = obj.getJSONArray("list");
                            for (int j = 0; j < array1.length(); j++) {
                                obj = array1.getJSONObject(j);
//                                wv_PVA.setWebViewClient(wv_Client);
//                                wv_PVA.loadUrl(obj.getString("url"));
//                                wv_PVA.setWebChromeClient(webChromeClient);
                                Intent outIntent = new Intent(Intent.ACTION_VIEW);
                                outIntent.setData(Uri.parse(obj.getString("url")));
                                startActivity(outIntent);
                                finish();
                                Log.i(TAG, obj.getString("url"));
                            }
                        }else {
                            errorCode(obj.getInt("code"));
                            finish();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                    Log.e("失败", "失败");
                    finish();
                }
            };
            AndroidAsyncHttp.post(getApplicationContext(), ServerAPIConfig.GetQiNiuUrl, entity, " application/json;charset=UTF-8", res);

        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initEvent() {
        ll_Back_PVA.setOnClickListener(this);
    }

    private void initView() {
        ll_Back_PVA = (LinearLayout) findViewById(R.id.ll_Back_PVA);
        wv_PVA = (WebView) findViewById(R.id.wv_PVA);
    }

    /**
     * Called when a view has been clicked.
     *
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_Back_PVA:
                this.finish();
                break;
        }

    }

    WebChromeClient webChromeClient = new WebChromeClient() {
        @Override
        public void onReceivedTitle(WebView view, String title) {
            super.onReceivedTitle(view, title);

        }

    };
    WebViewClient wv_Client = new WebViewClient() {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            // TODO Auto-generated method stub
            //返回值是true的时候控制去WebView打开，为false调用系统浏览器或第三方浏览器
            view.loadUrl(url);
            return true;
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
        }
    };

    void initPlugin()
    {
        PackageManager pm = getPackageManager();
        List<PackageInfo> infoList = pm.getInstalledPackages(PackageManager.GET_SERVICES);
        for (PackageInfo info : infoList) {
            if ("com.adobe.flashplayer".equals(info.packageName)) {
                //已经安装了
                initData();
            }
            else
            {
                Toast.makeText(PlayVideoActivity.this,"plugin is not available !!!",Toast.LENGTH_SHORT).show();
            }
        }
    }
}
