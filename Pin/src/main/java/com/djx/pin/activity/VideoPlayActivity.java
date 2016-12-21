package com.djx.pin.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;

import com.djx.pin.R;
import com.djx.pin.beans.LifeRewardOnlineDetailInfo;
import com.djx.pin.beans.PhotoBrowseEntity;
import com.djx.pin.utils.ToastUtil;

import java.util.List;

/**
 * Created by Administrator on 2016/9/27 0027.
 */
public class VideoPlayActivity extends AppCompatActivity{

    private List<LifeRewardOnlineDetailInfo.Result.Receiver> receiverList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_videoplay);

        String video_url = getIntent().getStringExtra("VIDEO_URL").trim();


        WebView webView = (WebView) findViewById(R.id.webview_videopalyer);
        webView.getSettings().setJavaScriptEnabled(true);

        ImageView back = (ImageView) findViewById(R.id.iv_back);

        if (!video_url.startsWith("http")) {
            video_url = new StringBuffer("https://").append(video_url).toString();
            Log.i("test","video_url============================="+video_url);
        }

        webView.loadUrl(video_url);
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return super.shouldOverrideUrlLoading(view, url);
            }
        });

        /**
         * 返回
         */
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }
}
