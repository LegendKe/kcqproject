package com.djx.pin.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.android.moblie.zmxy.antgroup.creditsdk.app.CreditApp;
import com.android.moblie.zmxy.antgroup.creditsdk.app.ICreditListener;
import com.djx.pin.R;
import com.djx.pin.base.OldBaseActivity;
import com.djx.pin.beans.StaticBean;
import com.djx.pin.beans.UserInfo;
import com.djx.pin.serverapiconfig.ServerAPIConfig;
import com.djx.pin.ui.CommonDialog;
import com.djx.pin.utils.AndroidAsyncHttp;
import com.djx.pin.utils.CreditAuthHelper;
import com.djx.pin.utils.myutils.LogicUtils;
import com.djx.pin.utils.ScreenUtils;
import com.djx.pin.utils.ToastUtil;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;

/**
 * Created by Administrator on 2016/6/30.
 */
public class PurseActivity extends OldBaseActivity implements View.OnClickListener {

    private Float creditBalance;
    protected final static String TAG = PurseActivity.class.getSimpleName();

    private LinearLayout ll_Back_PA, ll_LookDetail_PA, ll_MyIntegral_PA, ll_CreditSesame_PA, ll_Residual_PA, ll_AccidentInsurance_PA;
    private TextView tv_MoneyPointFront_PA, tv_MoneyPointBehind_PA, tv_ZhiMa_State;
    private TextView tv_balance_value, tv_credit_value, tv_credit_unit;
    private SharedPreferences sp;
    private View v_ParentCover_PA;

    String session_id;
    String zhima_open_id;

    final static String creditBalanceKey = "credit_balance";

    private int is_auth;
    private String real_name;
    private String id_card;
    String mobile;

    String user_id;
    private boolean ifUpdateUserInfo = false;

    private Float balance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_purse);
        session_id = UserInfo.getSessionID(this);
        user_id = UserInfo.getUserID(this);
        initView();
        setViewContent();
        initEvent();
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        is_auth = UserInfo.getIsAuth(this);
        real_name = UserInfo.getRealName(this);
        id_card = UserInfo.getIdCard(this);

        UserInfo.getUserInfo(getApplicationContext(), new UserInfo.GetUserInfoCallBack() {
            @Override
            public void callback(boolean state) {
                setViewContent();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        zhima_open_id = getSharedPreferences(StaticBean.USER_INFO, Context.MODE_PRIVATE).getString("zhima_open_id", null);
        Log.e("zhima_open_id", zhima_open_id);
        if (zhima_open_id == null || zhima_open_id.equals("")) {
            tv_ZhiMa_State.setText("未绑定");
        } else {
            tv_ZhiMa_State.setText("已绑定");
        }

        mobile = getSharedPreferences(StaticBean.USER_INFO, Context.MODE_PRIVATE).getString("mobile", null);
        is_auth = getSharedPreferences(StaticBean.USER_INFO, Context.MODE_PRIVATE).getInt("is_auth", 0);
    }

    private void initEvent() {
        ll_Back_PA.setOnClickListener(this);
        ll_LookDetail_PA.setOnClickListener(this);
        ll_CreditSesame_PA.setOnClickListener(this);
        ll_Residual_PA.setOnClickListener(this);
        ll_AccidentInsurance_PA.setOnClickListener(this);
        ll_MyIntegral_PA.setOnClickListener(this);
    }

    private void initView() {
        ll_Back_PA = (LinearLayout) findViewById(R.id.ll_Back_PA);
        ll_LookDetail_PA = (LinearLayout) findViewById(R.id.ll_LookDetail_PA);
        ll_CreditSesame_PA = (LinearLayout) findViewById(R.id.ll_CreditSesame_PA);
        ll_Residual_PA = (LinearLayout) findViewById(R.id.ll_Residual_PA);
        ll_AccidentInsurance_PA = (LinearLayout) findViewById(R.id.ll_AccidentInsurance_PA);

        ll_MyIntegral_PA = (LinearLayout) findViewById(R.id.ll_MyIntegral_PA);
        tv_MoneyPointFront_PA = (TextView) findViewById(R.id.tv_MoneyPointFront_PA);
        tv_MoneyPointBehind_PA = (TextView) findViewById(R.id.tv_MoneyPointBehind_PA);
        tv_ZhiMa_State = (TextView) findViewById(R.id.tv_ZhiMa_State);

        v_ParentCover_PA = findViewById(R.id.v_ParentCover_PA);

        tv_balance_value = (TextView) findViewById(R.id.tv_balance_value);
        tv_credit_value = (TextView) findViewById(R.id.tv_credit_value);
        tv_credit_unit = (TextView) findViewById(R.id.tv_credit_unit);
    }

    private void setViewContent() {

        sp = getSharedPreferences(StaticBean.USER_INFO, MODE_PRIVATE);
        balance = UserInfo.getBalance(this);
        DecimalFormat decimalFormat=new DecimalFormat("0.00");
        tv_balance_value.setText(decimalFormat.format(balance));
        setCreditBalance();
        Float total = balance + creditBalance;
        DecimalFormat totalFormat=new DecimalFormat("0.00");
        String totalStr = totalFormat.format(total);
        int idx = totalStr.lastIndexOf(".");//查找小数点的位置
        tv_MoneyPointFront_PA.setText(totalStr.substring(0, idx));
        tv_MoneyPointBehind_PA.setText(totalStr.substring(idx, totalStr.length()));

    }

    public void setCreditBalance(){
        creditBalance = UserInfo.getCreditBalance(this);
        Log.i(TAG, "creditBalance is " + creditBalance);
        if (creditBalance <= 0) {
            tv_credit_value.setText("未启用");
            tv_credit_unit.setText("");
            creditBalance = Float.valueOf(0);
        } else {
            DecimalFormat format = new DecimalFormat("0.00");
            tv_credit_value.setText(format.format(creditBalance));
            tv_credit_unit.setText("元");
        }

    }

    @Override
    public void onClick(View v) {
        Intent intent;
        Bundle bundle;
        switch (v.getId()) {
            case R.id.ll_Back_PA:
                this.finish();
                break;
            //查看明细
            case R.id.ll_LookDetail_PA:
                startActivity(LookPurseDetailActivity.class);
                break;
            //我的积分
            case R.id.ll_MyIntegral_PA:
                startActivity(MyIntegralActivity.class);
                break;
            //意外保险金
            case R.id.ll_AccidentInsurance_PA:


                if (zhima_open_id == null || zhima_open_id.equals("")) {
                    Button.OnClickListener positiveListener = new Button.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (mobile == null || mobile.equals("")) {
                                //未绑定手机号
                                showPopupWindow(1);
                            }else {
                                //绑定手机和实名认证后,开始芝麻授权
                                requestZhiMa();
                            }
                        }
                    };
                    CommonDialog.show(PurseActivity.this, "确定", "取消", "请先绑定芝麻信用，现在去绑定？", positiveListener);
                } else {
                    if (creditBalance <= 0) {
                        tv_credit_value.setText("未启用");
                        tv_credit_unit.setText("");
                        startActivity(CannotUseInsuranceActivity.class);
                    } else {
                        intent = new Intent();
                        bundle = new Bundle();
                        bundle.putFloat("credit", creditBalance);
                        intent.putExtras(bundle);
                        intent.setClass(PurseActivity.this, PurseAccidentInsuranceActivity.class);
                        startActivity(intent);
                    }
                }
               /* LogicUtils.realNameVerify(this, new LogicUtils.AfterPassedListener() {
                    @Override
                    public void realNameVerifyPassed() {
                        if (zhima_open_id == null || zhima_open_id.equals("")) {
                            Button.OnClickListener positiveListener = new Button.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    if (mobile == null || mobile.equals("")) {
                                        //未绑定手机号
                                        showPopupWindow(1);
                                    } else if (is_auth == 0) {
                                        //未实名认证
                                        showPopupWindow(2);
                                    } else {
                                        //绑定手机和实名认证后,开始芝麻授权
                                        requestZhiMa();
                                    }
                                }
                            };
                            CommonDialog.show(PurseActivity.this, "确定", "取消", "请先绑定芝麻信用，现在去绑定？", positiveListener);
                        } else {
                            if (creditBalance <= 0) {
                                tv_credit_value.setText("未启用");
                                tv_credit_unit.setText("");
                                startActivity(CannotUseInsuranceActivity.class);
                            } else {
                                Intent intent = new Intent();
                                Bundle bundle = new Bundle();
                                bundle.putFloat("credit", creditBalance);
                                intent.putExtras(bundle);
                                intent.setClass(PurseActivity.this, PurseAccidentInsuranceActivity.class);
                                startActivity(intent);
                            }
                        }
                    }
                });*/

                break;
            //余额
            case R.id.ll_Residual_PA:
                intent = new Intent();
                bundle = new Bundle();
                bundle.putFloat("balance", balance);
                intent.putExtras(bundle);
                intent.setClass(this, PurseResidualActivity.class);
                startActivity(intent);
                break;
            //芝麻信用

            case R.id.ll_CreditSesame_PA:
                Log.e(TAG, "onClick:ll_CreditSesame_PA");
                if (zhima_open_id == null || zhima_open_id.equals("")) {
                    if (mobile == null || mobile.equals("")) {
                        //未绑定手机号
                        showPopupWindow(1);
                    } else {
                        requestZhiMa();
                    }
                } else {
                    requestZhiMaScore(session_id, zhima_open_id);
                }
                break;
            //绑定手机号弹窗
            case R.id.tv_Cancel_PA1:
                popupWindoew.dismiss();
                break;
            case R.id.tv_Yes_PA1:
                startActivity(BundlePhoneActivity.class);
                break;

            //绑定手机号弹窗
            case R.id.tv_Cancel_PA2:
                popupWindoew.dismiss();
                break;
            case R.id.tv_Yes_PA2:
                startActivity(IdentityActivity.class);
                break;
        }
    }

    private void requestZhiMa() {
        /**
         *
         * {
         "session_id": "03194988f53372d77dfa",
         "content": {
         "identity_type": 2,
         "identity_param": {
         "certNo": "652701197705103720",
         "certType": "IDENTITY_CARD",
         "name": "eynppv"
         },
         "biz_params": {
         "auth_code": "M_APPSDK",
         "state": "TEST"
         }
         }
         }
         * */

        Log.i(TAG, "requestZhiMa");
        String url = ServerAPIConfig.ZhiMaXinYong;
        JSONObject obj = new JSONObject();
        JSONObject content = new JSONObject();
        JSONObject identity_param = new JSONObject();
        JSONObject biz_params = new JSONObject();
        try {
            obj.put("session_id", session_id);

            content.put("identity_type", 2);

            //身份证号码
            String certNo = getSharedPreferences(StaticBean.USER_INFO, Context.MODE_PRIVATE).getString("id_card", null);
            String certType = "IDENTITY_CARD";
            //真实姓名
            String name = getSharedPreferences(StaticBean.USER_INFO, Context.MODE_PRIVATE).getString("real_name", null);

            identity_param.put("certNo", certNo);
            identity_param.put("certType", certType);
            identity_param.put("name", name);


            biz_params.put("auth_code", "M_APPSDK");
            biz_params.put("state", "TEST");

            content.put("identity_param", identity_param);
            content.put("biz_params", biz_params);

            obj.put("content", content);

            StringEntity entity = new StringEntity(obj.toString(), "utf-8");


            AsyncHttpResponseHandler res = new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int i, Header[] headers, byte[] bytes) {
                    String str = new String(bytes);
                    Log.e(TAG, str);
                    try {
                        JSONObject object = new JSONObject(str);
                        Log.e(TAG, object.toString());
                        if (object.getInt("code") == 0) {
                            object = object.getJSONObject("result");
                            Log.e(TAG, object.toString());
                            String params = object.getString("cipher");
                            String sign = object.getString("sign");
                            Log.e(TAG, params);
                            Log.e(TAG, sign);
                            ZhiMaShouQuan(params, sign);
                        } else {
                            errorCode(object.getInt("code"));
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
        Log.i(TAG, "ZhiMaShouQuan");

        String appId = "1000590";
        CreditAuthHelper.creditAuth(PurseActivity.this, appId, params, sign, null, new ICreditListener() {
            @Override
            public void onComplete(Bundle result) {
                //从result中获取params参数,然后解析params数据,可以获取open_id。
                if (result != null) {
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
        Log.i(TAG, "requestZhiMaJieMi");
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
                        Log.i(TAG, result[1]);
                    } else {
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
        AndroidAsyncHttp.post(url, params, res);

    }

    /**
     * 请求芝麻信用分
     * {
     * "session_id": "8527a2b6a58b4fa963fd",
     * "open_id": "123451858416405071136980128",
     * }
     */
    private void requestZhiMaScore(String session_id, String open_id) {
        Log.i(TAG, "requestZhiMaScore");

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
                        String session_id = UserInfo.getSessionID(PurseActivity.this);
                        UserInfo.getUserInfo(getApplicationContext(), session_id, user_id);
                        zhima_open_id = UserInfo.getZhiMaOpenID(getApplicationContext());
                        if (zhima_open_id == null || zhima_open_id.equals("")) {
                            tv_ZhiMa_State.setText("未绑定");
                        } else {
                            tv_ZhiMa_State.setText("已绑定");
                        }
                    }
                    JSONObject object = new JSONObject(string);
                    if (object.getInt("code") == 0) {
                        object = object.getJSONObject("result");
                        int score = object.getInt("zhima_score");
                        Bundle bundle = new Bundle();
                        bundle.putInt("score", score);
                        startActivity(PurseCreditSesameActivity.class, bundle);
                    } else {
                        Log.i(TAG, "requestZhiMaScore: ret is " + object.getInt("code"));
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

    private void rebindZhiMa() {
        Button.OnClickListener positiveListener = new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                requestZhiMa();
            }
        };
        CommonDialog.show(PurseActivity.this, "确定", "取消", "现在重新绑定芝麻信用？", positiveListener);
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
                        Log.i(TAG, "push unbind into to server OK");
                        UserInfo.setZhiMaOpenID(PurseActivity.this, "");
                        zhima_open_id = UserInfo.getZhiMaOpenID(getApplicationContext());
                        if (zhima_open_id == null || zhima_open_id.equals("")) {
                            tv_ZhiMa_State.setText("未绑定");
                        } else {
                            tv_ZhiMa_State.setText("已绑定");
                        }
                        rebindZhiMa();
                    } else {
                        errorCode(object.getInt("code"));
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

    private PopupWindow popupWindoew;
    private View popView;

    public void showPopupWindow(int i) {
        popupWindoew = new PopupWindow();
        popupWindoew.setFocusable(true);
        popupWindoew.setOutsideTouchable(true);
        popupWindoew.setTouchable(true);
        popupWindoew.setWidth(ScreenUtils.getScreenWidth(this) - (ScreenUtils.getScreenWidth(this) / 9));
        popupWindoew.setHeight(ScreenUtils.getScreenHeight(this) / 5);
        v_ParentCover_PA.setVisibility(View.VISIBLE);
        v_ParentCover_PA.setAlpha(0.5f);
        switch (i) {
            case 1:
                popView = LayoutInflater.from(this).inflate(R.layout.popupwindow_pa_creditsesame1, null);
                initBangDinPhoneView();
                initBangDinPhoneEvent();
                break;
            case 2:
                popView = LayoutInflater.from(this).inflate(R.layout.popupwindow_pa_creditsesame2, null);
                initShiMingView();
                initShiMIngEvent();
                break;

        }
        popupWindoew.setBackgroundDrawable(getResources().getDrawable(R.drawable.shape_popupwindow));
        popupWindoew.setContentView(popView);
        popupWindoew.showAtLocation(v_ParentCover_PA, Gravity.CENTER, 0, 0);
        popupWindoew.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                v_ParentCover_PA.setVisibility(View.GONE);
            }
        });
    }

    private void initShiMIngEvent() {
        tv_Cancel_PA2.setOnClickListener(this);
        tv_Yes_PA2.setOnClickListener(this);
    }

    TextView tv_Cancel_PA2, tv_Yes_PA2;

    private void initShiMingView() {
        tv_Cancel_PA2 = (TextView) popView.findViewById(R.id.tv_Cancel_PA2);
        tv_Yes_PA2 = (TextView) popView.findViewById(R.id.tv_Yes_PA2);
    }

    private void initBangDinPhoneEvent() {
        tv_Cancel_PA1.setOnClickListener(this);
        tv_Yes_PA1.setOnClickListener(this);
    }

    TextView tv_Cancel_PA1, tv_Yes_PA1;

    private void initBangDinPhoneView() {
        tv_Cancel_PA1 = (TextView) popView.findViewById(R.id.tv_Cancel_PA1);
        tv_Yes_PA1 = (TextView) popView.findViewById(R.id.tv_Yes_PA1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("芝麻授权", "DemoActivity.onActivityResult");
        //onActivityResult callback
        CreditApp.onActivityResult(requestCode, resultCode, data);
    }
}
