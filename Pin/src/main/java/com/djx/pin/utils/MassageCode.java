package com.djx.pin.utils;

import android.content.Context;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;

import com.djx.pin.R;
import com.djx.pin.serverapiconfig.ServerAPIConfig;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;

/**
 * Created by Administrator on 2016/7/13 0013.
 */
public class MassageCode {

    /**
     * {参数
     * "country_code": "0086",
     * "mobile": "13112345678"
     * }
     */
    /**
     * 请求短信验证码
     */

    public static void RequestMassageCode(EditText editText, final TextView tv_SendCode_BPA, final Context context) {

        if (EditTextHelper.getEidtTextLength(editText) > EditTextHelper.getedtLengthAfterTrim(editText)) {
            ToastUtil.shortshow(context, "手机号不能有空格");
            return;
        } else if (EditTextHelper.getedtLengthAfterTrim(editText) == 0) {

            ToastUtil.shortshow(context, "手机号不能为空");
            return;
        } else if (EditTextHelper.getedtLengthAfterTrim(editText) == 11) {

            Log.e("开始请求验证码", "开始请求验证码");
            String url = ServerAPIConfig.RequestMassageCode;
            String country_code = "0086";
            String mobile = editText.getText().toString().trim();

            JSONObject obj = new JSONObject();
            try {
                obj.put("country_code", country_code);
                obj.put("mobile", mobile);

                StringEntity entity = new StringEntity(obj.toString(), "utf-8");


                AsyncHttpResponseHandler res = new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(int i, Header[] headers, byte[] bytes) {

                        String str = new String(bytes);
                        Log.e("str===", str);
                        try {
                            JSONObject object = new JSONObject(str);
                            int code = object.getInt("code");

                            if (code == 0) {
                                CountDownTimerUtils downTimerUtils = new CountDownTimerUtils(tv_SendCode_BPA, 60000, 1000, context);
                                downTimerUtils.start();
                                ToastUtil.shortshow(context, "验证码发送成功");
                            } else if (code == 2003) {
                                ToastUtil.shortshow(context, "手机号已经被占用");
                            } else if (code == 2007) {
                                ToastUtil.shortshow(context, "手机号错误");
                            } else if (code == 5003) {
                                ToastUtil.shortshow(context, "验证码已经发送,请不要重复请求");
                                tv_SendCode_BPA.setText("已获得验证码");
                                tv_SendCode_BPA.setTextColor(context.getResources().getColor(R.color.text_color_normal));
                            } else if (code == 5002) {
                                ToastUtil.shortshow(context, "短信验证码校验失败");
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                        Log.e("验证码请求失败", "验证码请求失败");
                    }
                };
                AndroidAsyncHttp.post(context, url, entity, "application/json;charset=UTF-8", res);
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }

        } else {
            ToastUtil.shortshow(context, "请输入正确的手机号");
            return;
        }


    }



    /**
     * {参数
     * "country_code": "0086",
     * "mobile": "13112345678"
     * }
     */
    /**
     * 请求短信验证码
     */

    public static void RequestMassageCode(TextView textView, final TextView tv_SendCode_BPA, final Context context) {


            String url = ServerAPIConfig.RequestMassageCode;
            String country_code = "0086";
            String mobile = textView.getText().toString().trim();

            JSONObject obj = new JSONObject();
            try {
                obj.put("country_code", country_code);
                obj.put("mobile", mobile);

                StringEntity entity = new StringEntity(obj.toString(), "utf-8");


                AsyncHttpResponseHandler res = new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(int i, Header[] headers, byte[] bytes) {

                        String str = new String(bytes);
                        Log.e("str===", str);
                        try {
                            JSONObject object = new JSONObject(str);
                            int code = object.getInt("code");

                            if (code == 0) {
                                CountDownTimerUtils downTimerUtils = new CountDownTimerUtils(tv_SendCode_BPA, 60000, 1000, context);
                                downTimerUtils.start();
                                ToastUtil.shortshow(context, "验证码发送成功");
                            } else if (code == 2003) {
                                ToastUtil.shortshow(context, "手机号已经被占用");
                            } else if (code == 2007) {
                                ToastUtil.shortshow(context, "手机号错误");
                            } else if (code == 5003) {
                                ToastUtil.shortshow(context, "验证码已经发送,请不要重复请求");
                                tv_SendCode_BPA.setText("已获得验证码");
                                tv_SendCode_BPA.setTextColor(context.getResources().getColor(R.color.text_color_normal));
                            } else if (code == 5002) {
                                ToastUtil.shortshow(context, "短信验证码校验失败");
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                        Log.e("验证码请求失败", "验证码请求失败");
                    }
                };
                AndroidAsyncHttp.post(context, url, entity, "application/json;charset=UTF-8", res);
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }




    }











    /**
     * 请求手机号
     * @param mobile 手机号
     * @param tv_SendCode_BPA 发送验证码按钮
     * @param context 上下文
     */
    public static void RequestMassageCode(String mobile, final TextView tv_SendCode_BPA, final Context context) {
        Log.e("开始请求验证码", "开始请求验证码");

        if (null == mobile || mobile.length() == 0) {
            ToastUtil.shortshow(context, "手机号不能有空格");
            return;
        }
        if (11 != mobile.length()) {
            ToastUtil.shortshow(context, "请输入正确的手机号");
            return;
        }
        String url = ServerAPIConfig.RequestMassageCode;
        String country_code = "0086";

        JSONObject obj = new JSONObject();
        try {
            obj.put("country_code", country_code);
            obj.put("mobile", mobile);

            StringEntity entity = new StringEntity(obj.toString(), "utf-8");


            AsyncHttpResponseHandler res = new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int i, Header[] headers, byte[] bytes) {

                    String str = new String(bytes);
                    Log.e("str===", str);
                    try {
                        JSONObject object = new JSONObject(str);
                        int code = object.getInt("code");

                        if (code == 0) {
                            CountDownTimerUtils downTimerUtils = new CountDownTimerUtils(tv_SendCode_BPA, 60000, 1000, context);
                            downTimerUtils.start();
                            ToastUtil.shortshow(context, "验证码发送成功");
                        } else if (code == 2003) {
                            ToastUtil.shortshow(context, "手机号已经被占用");
                        } else if (code == 2007) {
                            ToastUtil.shortshow(context, "手机号错误");
                        } else if (code == 5003) {
                            ToastUtil.shortshow(context, "验证码已经发送,请不要重复请求");
                            tv_SendCode_BPA.setText("已获得验证码");
                            tv_SendCode_BPA.setTextColor(context.getResources().getColor(R.color.text_color_normal));
                        } else if (code == 5002) {
                            ToastUtil.shortshow(context, "短信验证码校验失败");
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                    Log.e("验证码请求失败", "验证码请求失败");
                }
            };
            AndroidAsyncHttp.post(context, url, entity, "application/json;charset=UTF-8", res);
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /**
     *
     {参数
     "country_code": "0086",
     "mobile": "13112345678",
     "sms_code": "123412"
     }
     */
    /**
     * 验证短信验证码
     */
    public static void VerifyMassageCode(EditText mobile, EditText sms_code, final Context context) {

        if (EditTextHelper.getEidtTextLength(sms_code) > EditTextHelper.getedtLengthAfterTrim(sms_code)) {
            ToastUtil.shortshow(context, "验证码不能有空格");
            return;
        } else if (EditTextHelper.getedtLengthAfterTrim(sms_code) == 0) {

            ToastUtil.shortshow(context, "验证码不能为空");
            return;
        } else if (EditTextHelper.getedtLengthAfterTrim(sms_code) == 6) {

            String url = ServerAPIConfig.VerifyMassageCode;
            String country_code = "0086";
            String mobile1 = mobile.getText().toString().trim();
            String sms_code1 = sms_code.getText().toString().trim();

            JSONObject obj = new JSONObject();
            try {
                obj.put("country_code", country_code);
                obj.put("mobile", mobile1);
                obj.put("sms_code", sms_code1);

                StringEntity entity = new StringEntity(obj.toString(), "utf-8");


                AsyncHttpResponseHandler res = new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(int i, Header[] headers, byte[] bytes) {

                        String str = new String(bytes);
                        try {
                            JSONObject object = new JSONObject(str);
                            int code = object.getInt("code");

                            if (code == 0) {
                                ToastUtil.shortshow(context, "验证码正确");
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.e("验证码验证失败", "验证码验证失败");
                        }
                    }

                    @Override
                    public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                        Log.e("验证码验证失败", "验证码验证失败");
                    }
                };
                AndroidAsyncHttp.post(context, url, entity, "application/json;charset=UTF-8", res);
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            ToastUtil.shortshow(context, "请输入正确的验证码");
            return;
        }


    }
}
