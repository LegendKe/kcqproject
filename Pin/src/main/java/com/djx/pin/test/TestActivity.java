package com.djx.pin.test;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.djx.pin.R;
import com.djx.pin.alipay.AlipayActivity;
import com.djx.pin.application.PinApplication;
import com.djx.pin.serverapiconfig.ServerAPIConfig;
import com.djx.pin.utils.AndroidAsyncHttp;
import com.djx.pin.utils.LogUtil;
import com.djx.pin.utils.ToastUtil;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;
import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.UserInfo;

public class TestActivity extends Activity implements OnClickListener {
    Context CONTEXT = TestActivity.this;
    Button bt_test1, bt_test2, bt_test3, bt_test4, bt_test5, bt_alipay;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        LogUtil.e("进入测试界面");
        bt_test1 = (Button) findViewById(R.id.bt_test1);
        bt_test2 = (Button) findViewById(R.id.bt_test2);
        bt_test3 = (Button) findViewById(R.id.bt_test3);
        bt_test4 = (Button) findViewById(R.id.bt_test4);
        bt_test5 = (Button) findViewById(R.id.bt_test5);
        bt_alipay = (Button) findViewById(R.id.bt_alipay);
        bt_test1.setOnClickListener(this);
        bt_test2.setOnClickListener(this);
        bt_test3.setOnClickListener(this);
        bt_test4.setOnClickListener(this);
        bt_test5.setOnClickListener(this);
        bt_alipay.setOnClickListener(this);

        RongIM.setUserInfoProvider(new RongIM.UserInfoProvider() {
            @Override
            public UserInfo getUserInfo(String s) {
                LogUtil.e("进入getUserInfo="+s);
                AsyncHttpResponseHandler res4Portrait = new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(int i, Header[] headers, byte[] bytes) {
                        String strJson = new String(bytes);
                        LogUtil.e("进入res4Portrait=strJson="+strJson);
                        try {
                            JSONObject obj=new JSONObject(strJson);
                            if(0!=obj.getInt("code")){
                                LogUtil.e(CONTEXT,"code="+obj.getInt("code"));
                                return;
                            }
                            obj=obj.getJSONObject("result");
                            String id=obj.getString("portrait");
                            String user_id=obj.getString("user_id");
                            String nickname=obj.getString("nickname");
                            try {
                                getRongPortrait(user_id,nickname,id,1);
                            } catch (UnsupportedEncodingException e) {
                                LogUtil.e("enter catch");
                                e.printStackTrace();
                            }

                        } catch (JSONException e) {
                            LogUtil.e(CONTEXT,"enter catch");
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                        LogUtil.e(CONTEXT,"网络请求失败");
                        ToastUtil.shortshow(CONTEXT,R.string.toast_error_net);
                    }
                };
                RequestParams params=new RequestParams();
                params.put("user_id",s);
                AndroidAsyncHttp.get(ServerAPIConfig.Get_UserInfo,params,res4Portrait);
                return null;
            }
        }, true);
    }


    /**
     * 请求七牛图片下载地址,只有一个ImageView
     */
    public void getRongPortrait(final String user_id,final String nickname, String id, int media_type) throws UnsupportedEncodingException {

        LogUtil.e("进入getOneImageViewUrl="+user_id+"=="+nickname);
        final JSONObject newObj = new JSONObject();
        try {
            newObj.put("size", 1);

            JSONArray array = new JSONArray();
            JSONObject detaileObj = new JSONObject();
            detaileObj.put("id", id);
            detaileObj.put("media_type", media_type);
            array.put(detaileObj);
            newObj.put("list", array);
            StringEntity entity = new StringEntity(newObj.toString(), "utf-8");

            AsyncHttpResponseHandler res = new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int i, Header[] headers, byte[] bytes) {
                    String str = new String(bytes);
                    LogUtil.e("进入str="+str);
                    try {
                        JSONObject obj = new JSONObject(str);
                        obj = obj.getJSONObject("result");
                        JSONArray array1 = obj.getJSONArray("list");
                        for (int j = 0; j < array1.length(); j++) {
                            obj = array1.getJSONObject(j);
                            RongIM.getInstance().refreshUserInfoCache(new UserInfo(user_id, nickname, Uri.parse( obj.getString("url"))));
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
            AndroidAsyncHttp.post(getApplicationContext(), ServerAPIConfig.GetQiNiuUrl, entity, " application/json;charset=UTF-8", res);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private TextView tv_date_confirm;
    private DatePicker dp;
    Dialog dialog_Date;
    PopupWindow popWindow;

    @Override
    public void onClick(View v) {
        LogUtil.e("开始测试");
        switch (v.getId()) {
            case R.id.bt_alipay:
                LogUtil.e("onclick bt_alipay");
                Intent intent = new Intent(this, AlipayActivity.class);
                Bundle bundle = new Bundle();
                startActivity(intent);
                break;
            case R.id.bt_test1:
                LogUtil.e("onclick bt_test1");
                connect("Zfgs1Me8hGfEglo/puS6q+zs9mzjqkUYBXhWXPB/LYp3II9NnqIGld8pM8ZXnrOqBI/KmuBkJDsI+6/Q1LUG3g==");
                break;
            case R.id.bt_test2:
                LogUtil.e("onclick bt_test2");
                connect("S6Jaka006xvh3iQCqpHHE1eY49UCvlVD+JDf08gHHlZv6SfroS5hP4VlR20qYbRnMpd4aBPBvOf+wxuPZ8ar7A==");
                break;
            case R.id.bt_test3:
                LogUtil.e("onclick bt_test3");
                //启动会话界面
                if (RongIM.getInstance() != null)
                    RongIM.getInstance().startPrivateChat(this, "10086", "中国移动");
                else
                    LogUtil.e("1=RongIM.getInstance() = null");
                break;
            case R.id.bt_test4:
                //启动会话界面
                LogUtil.e("onclick bt_test4");
                if (RongIM.getInstance() != null)
                    RongIM.getInstance().startPrivateChat(this, "10010", "中国联通");
                else
                    LogUtil.e("2=RongIM.getInstance() = null");
                break;
        }

    }

    /**
     * 建立与融云服务器的连接
     *
     * @param token
     */
    private void connect(String token) {

        if (getApplicationInfo().packageName.equals(PinApplication.getCurProcessName(getApplicationContext()))) {

            /**
             * IMKit SDK调用第二步,建立与服务器的连接
             */
            RongIM.connect(token, new RongIMClient.ConnectCallback() {

                /**
                 * Token 错误，在线上环境下主要是因为 Token 已经过期，您需要向 App Server 重新请求一个新的 Token
                 */
                @Override
                public void onTokenIncorrect() {
                    LogUtil.e("--onTokenIncorrect");
                }

                /**
                 * 连接融云成功
                 * @param userid 当前 token
                 */
                @Override
                public void onSuccess(String userid) {
                    LogUtil.e("--onSuccess" + userid);
                    ToastUtil.shortshow(TestActivity.this, "onSuccess\n" + userid);
                }

                /**
                 * 连接融云失败
                 * @param errorCode 错误码，可到官网 查看错误码对应的注释
                 */
                @Override
                public void onError(RongIMClient.ErrorCode errorCode) {
                    LogUtil.e("--onError" + errorCode);
                }
            });
        }
    }
}
