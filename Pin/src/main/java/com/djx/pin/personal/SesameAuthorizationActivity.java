package com.djx.pin.personal;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.android.moblie.zmxy.antgroup.creditsdk.app.CreditApp;
import com.android.moblie.zmxy.antgroup.creditsdk.app.ICreditListener;
import com.djx.pin.R;
import com.djx.pin.activity.PurseCreditSesameActivity;
import com.djx.pin.base.baseui.BaseActivity;
import com.djx.pin.beans.StaticBean;
import com.djx.pin.beans.UserInfo;
import com.djx.pin.serverapiconfig.ServerAPIConfig;
import com.djx.pin.ui.CommonDialog;
import com.djx.pin.utils.AndroidAsyncHttp;
import com.djx.pin.utils.CreditAuthHelper;
import com.djx.pin.utils.LogUtil;
import com.djx.pin.utils.ToastUtil;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.regex.Pattern;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;
import cz.msebera.android.httpclient.util.TextUtils;

/**
 * Created by 柯传奇 on 2016/11/8 0008.
 */
public class SesameAuthorizationActivity extends BaseActivity implements View.OnClickListener {


    private EditText et_name;
    private EditText et_id;
    private String session_id;
    private boolean ifUpdateUserInfo;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sesame_authorization);
        initView();
        initData();
    }

    protected void initView() {
        findViewById(R.id.ic_back).setOnClickListener(this);
        findViewById(R.id.btn_confirm).setOnClickListener(this);
        et_name = ((EditText) findViewById(R.id.et_name));
        et_id = ((EditText) findViewById(R.id.et_id));
    }

    protected void initData() {
        SharedPreferences sp = getSharedPreferences(StaticBean.USER_INFO, Context.MODE_PRIVATE);
        String real_name = sp.getString("real_name", null);
        String id_card = sp.getString("id_card", null);
        session_id = sp.getString("session_id", null);
        if(real_name != null && id_card != null){
            et_id.setText(id_card);
            et_name.setText(real_name);
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.ic_back:
                finish();
                break;
            case R.id.btn_confirm:
                if(TextUtils.isEmpty(et_id.getText().toString().trim())){
                    ToastUtil.shortshow(this,"身份证不能为空");
                    return;
                }
                if(TextUtils.isEmpty(et_name.getText().toString().trim())){
                    ToastUtil.shortshow(this,"姓名不能为空");
                    return;
                }
                boolean matches = Pattern.matches("(^\\d{18}$)|(^\\d{15}$)", et_id.getText().toString().trim());
                if(!matches){
                    ToastUtil.shortshow(this,"请正确输入身份证号");
                    return;
                }
                requestZhiMa();

                break;
        }
    }

    private void requestZhiMa() {
        String url = ServerAPIConfig.ZhiMaXinYong;
        JSONObject obj = new JSONObject();
        JSONObject content = new JSONObject();
        JSONObject identity_param = new JSONObject();
        JSONObject biz_params = new JSONObject();
        try {
            obj.put("session_id", session_id);

            String certType = "IDENTITY_CARD";
            identity_param.put("certNo", et_id.getText().toString().trim());
            identity_param.put("certType", certType);
            identity_param.put("name", et_name.getText().toString().trim());
            biz_params.put("auth_code", "M_APPSDK");
            biz_params.put("state", "TEST");

            content.put("identity_type", 2);
            content.put("identity_param", identity_param);
            content.put("biz_params", biz_params);

            obj.put("content", content);

            StringEntity entity = new StringEntity(obj.toString(), "utf-8");
            AsyncHttpResponseHandler res = new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int i, Header[] headers, byte[] bytes) {
                    String str = new String(bytes);
                    try {
                        JSONObject object = new JSONObject(str);
                        if (object.getInt("code") == 0) {
                            object = object.getJSONObject("result");
                            String params = object.getString("cipher");
                            String sign = object.getString("sign");
                            ZhiMaShouQuan(params, sign);
                        } else {
                            ToastUtil.errorCode(SesameAuthorizationActivity.this,object.getInt("code"));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                @Override
                public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {

                }
            };
            AndroidAsyncHttp.post(this, url, entity, "application/json;charset=UTF-8", res);

        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /**
     * 芝麻信用授权申请
     */
    private void ZhiMaShouQuan(final String params, String sign) {
        String appId = "1000590";
        CreditAuthHelper.creditAuth(this, appId, params, sign, null, new ICreditListener() {
            @Override
            public void onComplete(Bundle result) {
                //从result中获取params参数,然后解析params数据,可以获取open_id。
                if (result != null) {
                    LogUtil.e("--------------ZhiMaShouQuan----------------------");
                    ToastUtil.shortshow(getApplicationContext(), "授权成功");
                    String cipher = result.getString("params");
                    String sign = result.getString("sign");
                    requestZhiMaJieMi(cipher, sign);
                }
            }

            @Override
            public void onError(Bundle bundle) {
                ToastUtil.shortshow(getApplicationContext(), "授权错误");
            }

            @Override
            public void onCancel() {
                ToastUtil.shortshow(getApplicationContext(), "授权失败");
            }
        });
    }
    /***
     * 芝麻解密
     * <p/>
     * {
     * "session_id": "03194988f53372d77dfa",
     * "cipher": "REcL+bjqqTwcnFdd15Bl5F/2AQmvRBEaalgvx3gxW5Z5byNab9No1gY9dEomRdLq/JARG+2Alf8PzVyeuRR2pneWYN8hVZNPABSCiQIhPeu8M6mpGullbYi868OGgggJ9ub212ajm7cJWmaoLmq+fCs+eCmBBpvXzky2vs59w+FudRIQKp9CrXP7DO41rSfbi+jkoIRtxCj/N7M7VFHI9GQqMy8jhQoPmINVFDsCAhOjm3M9FuSHBPsCW9mDEOlEZaN46JY3VCupm2CxzcEZKtXjMvc1LxkbGdu3GC49DUfyYJHcYaKUqIqNjDtQfwBI6akigkXdA5LNWSSWwdWqeA==",
     * "sign": "Fd60dNrjplHOlZERjCpzwttdQLQF3rRbunwW95Y90n/x1nTjmfiK8WVkPnA7Zt0EEDeNxHKEYmZLKbF+Dsdxg5uX7N0fs4zIMfKnkhDj09lny5MTrmhQ+wPB256qNQ0ivHdbt6ezzOnhHy9HNmdr4KeoB6rac6QlKD7aVFoTDeg="
     * }
     */
    private void requestZhiMaJieMi(String cipher, String sign) {
        String url = ServerAPIConfig.ZhiMaXinYongDecrypt;
        RequestParams params = new RequestParams();
        params.put("session_id", session_id);
        params.put("cipher", cipher);
        params.put("sign", sign);

        AsyncHttpResponseHandler res = new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                String str = new String(bytes);
                Log.e("str=====", str);
                try {
                    JSONObject obj = new JSONObject(str);
                    if (obj.getInt("code") == 0) {
                        obj = obj.getJSONObject("result");

                        String content = obj.getString("content");

                        String[] result = content.split("[=,&]");
                        String open_id = result[1];
                        boolean sign_verify = obj.getBoolean("sign_verify");
                        if (sign_verify) {
                            String user_id = getSharedPreferences(StaticBean.USER_INFO, Context.MODE_PRIVATE).getString("user_id", null);
                            //芝麻信用授权成功之后,重新获取用户资料
                            requestZhiMaScore(session_id, open_id);
                            ifUpdateUserInfo = true;
                        } else {
                            ToastUtil.shortshow(getApplicationContext(), "芝麻信用open_id不匹配");
                        }
                    } else {
                        ToastUtil.errorCode(SesameAuthorizationActivity.this,obj.getInt("code"));
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

    private void requestZhiMaScore(String session_id, String open_id) {
        String url = ServerAPIConfig.ZhiMaXinYongScore;

        RequestParams params = new RequestParams();
        params.put("session_id", session_id);
        params.put("open_id", open_id);
        AsyncHttpResponseHandler res = new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                String string = new String(bytes);
                try {
                    if (ifUpdateUserInfo) {
                        String session_id = UserInfo.getSessionID(SesameAuthorizationActivity.this);
                        String user_id;
                        user_id = UserInfo.getUserID(SesameAuthorizationActivity.this);
                        UserInfo.getUserInfo(getApplicationContext(), session_id, user_id);
                        String zhima_open_id = UserInfo.getZhiMaOpenID(getApplicationContext());
                       /* if (zhima_open_id == null || zhima_open_id.equals("")) {
                            tv_ZhiMa_State.setText("未绑定");
                        } else {
                            tv_ZhiMa_State.setText("已绑定");
                        }*/
                    }
                    JSONObject object = new JSONObject(string);
                    if (object.getInt("code") == 0) {
                        object = object.getJSONObject("result");
                        int score = object.getInt("zhima_score");
                        Bundle bundle = new Bundle();
                        bundle.putInt("score", score);
                        startActivity(PurseCreditSesameActivity.class, bundle);
                    } else {
                        ToastUtil.shortshow(getApplicationContext(), "获取芝麻分数失败， 您可能已解除绑定");
                        //将用户解除绑定信息推送给后台
                        pushUnbindToServer();
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

    private void pushUnbindToServer() {
        String url = ServerAPIConfig.ZhiMaXinYongScore;

        RequestParams params = new RequestParams();
        params.put("session_id", session_id);
        params.put("open_id", "");
        AsyncHttpResponseHandler res = new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                String string = new String(bytes);
                try {
                    JSONObject object = new JSONObject(string);
                    if (object.getInt("code") == 0) {
                        UserInfo.setZhiMaOpenID(SesameAuthorizationActivity.this, "");
                        String zhima_open_id = UserInfo.getZhiMaOpenID(getApplicationContext());
                     /*   if (zhima_open_id == null || zhima_open_id.equals("")) {
                            tv_ZhiMa_State.setText("未绑定");
                        } else {
                            tv_ZhiMa_State.setText("已绑定");
                        }*/
                        rebindZhiMa();
                    } else {
                        ToastUtil.errorCode(SesameAuthorizationActivity.this,object.getInt("code"));
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
    private void rebindZhiMa() {
        Button.OnClickListener positiveListener = new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                requestZhiMa();
            }
        };
        CommonDialog.show(this, "确定", "取消", "现在重新绑定芝麻信用？", positiveListener);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.e("芝麻授权", "onActivityResult");
        CreditApp.onActivityResult(requestCode, resultCode, data);
    }
}
