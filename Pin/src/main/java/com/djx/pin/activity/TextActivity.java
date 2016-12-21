package com.djx.pin.activity;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.djx.pin.R;
import com.djx.pin.base.OldBaseActivity;

/**
 * Created by Administrator on 2016/6/28.
 */
public class TextActivity extends OldBaseActivity implements View.OnClickListener {

    private LinearLayout ll_Back_TA;
    private TextView tv_Title;
    private WebView wv_Content;
    Bundle bundle;
    int TextContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.anctivity_text);
        initView();
        initEvent();
        initContentTitleText();
    }

    private void initEvent() {
        ll_Back_TA.setOnClickListener(this);

    }

    private void initView() {
        tv_Title = (TextView) findViewById(R.id.tv_Title);
        ll_Back_TA = (LinearLayout) findViewById(R.id.ll_Back_TA);

        wv_Content = (WebView) findViewById(R.id.wv_Content);
        WebSettings webSettings = wv_Content.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDefaultTextEncodingName("UTF-8");


    }


    //textContent不同的链接类型
    //1:关于我们;2:法律条例;3:用户指南
    String url;
    private void initContentTitleText() {
        bundle = getIntent().getExtras();
        TextContent = bundle.getInt("TextContent");
        url =bundle.getString("url");
        switch (TextContent) {
            case 1:
                wv_Content.loadUrl("http://www.dujoy.cn");
                break;
            case 2:
                wv_Content.loadUrl("http://www.dujoy.cn/app/set-legal/index.html");

                break;
            case 3:
                tv_Title.setText("用户指南");
                wv_Content.loadUrl("http://www.dujoy.cn/app/user-guide/index.html");
                break;
            case 4:
                tv_Title.setText("网络悬赏");
                String[] result=url.split("[.]");
                if (result[0].equals("www")){
                    url="http://"+url;
                    wv_Content.loadUrl(url);
                }else {
                    wv_Content.loadUrl(url);
                }
//                wv_Content.loadUrl("http://www.iqiyi.com/v_19rrm75rks.html?fv=93cad3dd4fadc7d5067404c5b16b4aff");
                break;
            case 5:
                tv_Title.setText("积分规则");
                wv_Content.loadUrl("http://www.dujoy.cn/app/integral/index.html");
                break;
            case 6:
                tv_Title.setText("实名认证说明");
                wv_Content.loadUrl("http://www.dujoy.cn/app/identification/index.html");
                break;
            case 7:
                tv_Title.setText("悬赏操作步骤");
                wv_Content.loadUrl("http://www.dujoy.cn/app/step-reward/index.html ");
        }
        wv_Content.setWebViewClient(wv_Client);
        wv_Content.setWebChromeClient(webChromeClient);
    }


    WebChromeClient webChromeClient = new WebChromeClient() {
        @Override
        public void onReceivedTitle(WebView view, String title) {
            super.onReceivedTitle(view, title);

            tv_Title.setText(title);
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

            tv_Title.setText(view.getTitle());
        }
    };

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_Back_TA:
                if (wv_Content.canGoBack()) {
                    wv_Content.goBack();//返回上一页面

                } else {
                    this.finish();//退出程序
                }
                break;
        }
    }

    //改写物理按键——返回的逻辑
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (wv_Content.canGoBack()) {
                wv_Content.goBack();//返回上一页面
                return true;
            } else {
                this.finish();//退出程序
            }
        }
        return super.onKeyDown(keyCode, event);
    }
}
