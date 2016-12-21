package com.djx.pin.activity;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.djx.pin.R;
import com.djx.pin.base.OldBaseActivity;
import com.djx.pin.beans.StaticBean;
import com.djx.pin.serverapiconfig.ServerAPIConfig;
import com.djx.pin.utils.AndroidAsyncHttp;
import com.djx.pin.utils.MassageCode;
import com.djx.pin.utils.ToastUtil;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class PayVerifyActivity extends OldBaseActivity implements View.OnClickListener {

    TextView tv_phoneNumber,tv_SendCode;
    EditText edt_Code;
    LinearLayout ll_back;
    SharedPreferences sp;
    Button bt_paybalance;

    Bundle bundle;
    String mobile;
    //悬赏类型1:线下求助;7网络悬赏

    static Activity instance;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payverify);
        initView();
        initEvent();
        bundle=getIntent().getExtras();
        instance=this;

    }

    private void initView(){
        sp=getSharedPreferences(StaticBean.USER_INFO,MODE_PRIVATE);
        tv_phoneNumber= (TextView) findViewById(R.id.tv_phoneNumber);
        tv_SendCode= (TextView) findViewById(R.id.tv_SendCode);
        edt_Code= (EditText) findViewById(R.id.edt_Code);
        ll_back= (LinearLayout) findViewById(R.id.ll_back);
        bt_paybalance= (Button) findViewById(R.id.bt_paybalance);
    }

    private void initEvent(){
        mobile=getSharedPreferences(StaticBean.USER_INFO, Context.MODE_PRIVATE).getString("mobile",null);
        tv_SendCode.setOnClickListener(this);
        bt_paybalance.setOnClickListener(this);
        ll_back.setOnClickListener(this);
        tv_phoneNumber.setText(mobile);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            //发送短信验证码
            case R.id.tv_SendCode:
                MassageCode.RequestMassageCode(mobile,tv_SendCode,this);
                break;
            //发起余额支付网络请求
            case R.id.bt_paybalance:
                payBalance();
                break;
            case R.id.ll_back:
                this.finish();
                break;
        }
    }


    /**
     * 余额支付接口更改
     * {
     "session_id": "4085c9d4f420864c407e",
     "id": "adas8d09as8d0adad",
     "type": 1,
     "country_code": "0086",
     "mobile": "13112345678",
     "sms_code": "231512"
     }
     *
     */
    private void payBalance(){
        String id=bundle.getString("id");
        int type=bundle.getInt("type");
        String url= ServerAPIConfig.PayVerify;

        RequestParams params=new RequestParams();

        params.put("session_id",getSession_id());
        params.put("id",id);
        params.put("type",type);
        params.put("country_code","0086");
        params.put("mobile",mobile);
        params.put("sms_code",edt_Code.getText().toString());

        AsyncHttpResponseHandler res=new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                String str=new String(bytes);
                try {
                    JSONObject obj=new JSONObject(str);
                    if (obj.getInt("code")==0){
                        startActivity(PayVerifySuccessActivity.class);
                        ToastUtil.shortshow(getApplicationContext(),"支付成功");
                        finish();
                    }else {
                        errorCode(obj.getInt("code"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {

            }
        };
        AndroidAsyncHttp.post(url,params,res);
    }
}
