package com.djx.pin.activity;

import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.djx.pin.R;
import com.djx.pin.base.OldBaseActivity;
import com.djx.pin.beans.StaticBean;
import com.djx.pin.beans.UserInfo;
import com.djx.pin.receiver.MyServiceReceiver;
import com.djx.pin.serverapiconfig.ServerAPIConfig;
import com.djx.pin.utils.AndroidAsyncHttp;
import com.djx.pin.utils.LogUtil;
import com.djx.pin.utils.ScreenUtils;
import com.djx.pin.utils.ToastUtil;
import com.djx.pin.weixin.WXConstants;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.tencent.connect.common.Constants;
import com.tencent.connect.share.QQShare;
import com.tencent.mm.sdk.modelmsg.SendMessageToWX;
import com.tencent.mm.sdk.modelmsg.WXMediaMessage;
import com.tencent.mm.sdk.modelmsg.WXTextObject;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.util.TextUtils;

/**
 * Created by Administrator on 2016/7/16 0016.
 */
public class SettingEmergencyPersonActivity extends OldBaseActivity implements View.OnClickListener {

    private static final int READ_CONTACT = 1011;
    private LinearLayout ll_Back_SEPA, ll_Inform_SEPA;
    private Button bt_FromAddressBook_SEPA, bt_Complete_SEPA;
    private EditText edt_EmergencyName_SEPA, edt_EmergencyPhone_SEPA;
    private View v_parentCover_SEPA;
    String name;//紧急联系人
    String phone;//紧急联系人电话
    private Context CONTEXT = SettingEmergencyPersonActivity.this;
    private String PhoneNumber;//手机号
    private String userName;//用户名
    private String userPhotoNum;//用户电话

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settingemergencyperson);
        name = getSharedPreferences(StaticBean.USER_INFO, Context.MODE_PRIVATE).getString("emergency_name", null);
        phone = getSharedPreferences(StaticBean.USER_INFO, Context.MODE_PRIVATE).getString("emergency_mobile", null);
        userName = getSharedPreferences(StaticBean.USER_INFO, Context.MODE_PRIVATE).getString("nickname", null);
        userPhotoNum = getSharedPreferences(StaticBean.USER_INFO, Context.MODE_PRIVATE).getString("mobile", null);
        Log.e("name===", name);
        Log.e("phone===", phone);
        initView();
        initEvent();

    }

    private void initEvent() {
        ll_Back_SEPA.setOnClickListener(this);
        ll_Inform_SEPA.setOnClickListener(this);
        bt_FromAddressBook_SEPA.setOnClickListener(this);
        bt_Complete_SEPA.setOnClickListener(this);

    }

    private void initView() {
        ll_Back_SEPA = (LinearLayout) findViewById(R.id.ll_Back_SEPA);
        ll_Inform_SEPA = (LinearLayout) findViewById(R.id.ll_Inform_SEPA);
        bt_FromAddressBook_SEPA = (Button) findViewById(R.id.bt_FromAddressBook_SEPA);
        bt_Complete_SEPA = (Button) findViewById(R.id.bt_Complete_SEPA);
        edt_EmergencyName_SEPA = (EditText) findViewById(R.id.edt_EmergencyName_SEPA);
        edt_EmergencyPhone_SEPA = (EditText) findViewById(R.id.edt_EmergencyPhone_SEPA);
        v_parentCover_SEPA = findViewById(R.id.v_parentCover_SEPA);
        edt_EmergencyName_SEPA.setText(name);
        edt_EmergencyPhone_SEPA.setText(phone);
    }


    @Override
    protected void onStart() {
        super.onStart();
        LogUtil.e("onStart()-------------------------------------");
      /*  name = getSharedPreferences(StaticBean.ContactPerson, Context.MODE_PRIVATE).getString("name", null);
        PhoneNumber = getSharedPreferences(StaticBean.ContactPerson, Context.MODE_PRIVATE).getString("PhoneNumber", null);
        if (name == null || name.equals("")) {
            return;
        } else {
            edt_EmergencyName_SEPA.setText(name);
            edt_EmergencyPhone_SEPA.setText(PhoneNumber);
        }*/
    }

    BasseIUiListener iUiListener = new BasseIUiListener();

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_Back_SEPA:
                this.finish();
                break;
            case R.id.ll_Inform_SEPA:
                ShowPop();
                break;
            case R.id.bt_FromAddressBook_SEPA:
                startReadContactsByPermissions();
                break;
            case R.id.bt_Complete_SEPA:
                SendEmergency();
                break;
            case R.id.img_QQ_Inform_SEPA:
                getmTencent().shareToQQ(this, getBundle(), iUiListener);
                ToastUtil.shortshow(this, "qq");
                break;
            case R.id.img_SMS_Inform_SEPA:
                SendSMS();
                popupWindow.dismiss();
                break;
            case R.id.img_WeiXin_Inform_SEPA:
                regToWx();
                getWXInfo();
                //检查是否安装微信
                if (!isWXAppInstalled)
                    return;
                //检查用户是否登录,未登录则return;
                if (getSharedPreferences(StaticBean.USER_INFO, MODE_PRIVATE).getBoolean("isLogined", false) == false) {
                    ToastUtil.shortshow(CONTEXT, R.string.toast_non_login);
                    return;
                }
                WXShareText("众觅紧急联系人" + "\n" + getSendString(userName,userPhotoNum));
                popupWindow.dismiss();
                break;
            case R.id.tv_Cancle_Inform_SEPA:
                popupWindow.dismiss();
                break;
        }
    }

    @Override
    public void actionReadContacts() {//跳转至系统通讯录
        Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
        startActivityForResult(intent, READ_CONTACT);
    }


    // 字段flag是用来判断分享类型的,true表示分享至朋友圈,false表示分享至好友.
    Boolean wxflag = false;

    // 微信分享文字,
    public void WXShareText(String text) {
        // 初始化一个wxTextOject对象,填写分享的文本内容
        WXTextObject textObj = new WXTextObject();
        textObj.text = text;
        // 用WXTextObject对象初始化一个WXMediaMessage对象
        WXMediaMessage msg = new WXMediaMessage();
        msg.mediaObject = textObj;
        msg.description = text;
        // 够着一个Req
        SendMessageToWX.Req req = new SendMessageToWX.Req();
        // transaction用于唯一标识一个请求
        req.transaction = "文字" + String.valueOf(System.currentTimeMillis());
        req.message = msg;
        // 判断flag的值
        req.scene = wxflag ? SendMessageToWX.Req.WXSceneTimeline
                : SendMessageToWX.Req.WXSceneSession;
        // 只有一种情况不能发送请求,即用户选择发送至朋友圈,但微信版本不支持.所以对该情况进行判断.其他情况都需要发送请求
        if (wxflag == true && isWXAppSupportAPI == false) {
            LogUtil.e(CONTEXT, "当前微信版本较低,暂不支持分享至朋友圈");
            ToastUtil.shortshow(CONTEXT, "当前微信版本较低,暂不支持分享至朋友圈");
            return;
        }
        wxapi.sendReq(req);

    }


    // IWXAPI是第三方app和微信通信的openapi接口
    private IWXAPI wxapi;

    // 将应用的appid注册到微信
    private void regToWx() {
        // 通过WXAPIFactory工厂,获取IWXAPI的实例
        wxapi = WXAPIFactory.createWXAPI(this, WXConstants.APP_ID, true);
        // 将应用的appid注册到微信
        wxapi.registerApp(WXConstants.APP_ID);

    }

    Boolean isWXAppInstalled = false;// 用来判断是否安装了微信
    Boolean isWXAppSupportAPI = false;// 判断是否支持分享到朋友圈

    // 获取用户是否安装微信 以及安装的微信版本是否支持分享到朋友圈
    public void getWXInfo() {
        // 检查是否安装微信
        if (wxapi.isWXAppInstalled()) {
            isWXAppInstalled = true;
        } else {
            LogUtil.e(CONTEXT, "未安装微信");
            ToastUtil.shortshow(CONTEXT, R.string.toast_weixin_share_uninstalled);
            isWXAppInstalled = false;
        }
        // 检查微信是否支持分享到朋友圈
        if (wxapi.getWXAppSupportAPI() >= 0x21020001) {
            isWXAppSupportAPI = true;
        } else {
            isWXAppSupportAPI = false;
        }

    }


    private void SendSMS() {
        if (getEidtTextLength(edt_EmergencyName_SEPA) > getedtLengthAfterTrim(edt_EmergencyName_SEPA)) {
            ToastUtil.shortshow(this, "姓名不能有空格");
        } else if (getedtLengthAfterTrim(edt_EmergencyName_SEPA) == 0 || getedtLengthAfterTrim(edt_EmergencyPhone_SEPA) == 0) {
            ToastUtil.shortshow(this, "内容不能为空");
        } else if (getedtLengthAfterTrim(edt_EmergencyPhone_SEPA) == 11) {
            SendMassage();
        } else {
            ToastUtil.shortshow(this, "手机号错误");
        }


    }

    private String SMS_SEND_ACTIOIN = "SMS_SEND_ACTIOIN";
    private String SMS_DELIVERED_ACTION = "SMS_DELIVERED_ACTION";

    MyServiceReceiver mReceiver02, mReceiver01;

    private void SendMassage() {
        /* 自定义IntentFilter为SENT_SMS_ACTIOIN Receiver */
        IntentFilter mFilter01;
        mFilter01 = new IntentFilter(SMS_SEND_ACTIOIN);
        mReceiver01 = new MyServiceReceiver();
        registerReceiver(mReceiver01, mFilter01);

/* 自定义IntentFilter为DELIVERED_SMS_ACTION Receiver */
        mFilter01 = new IntentFilter(SMS_DELIVERED_ACTION);
        mReceiver02 = new MyServiceReceiver();
        registerReceiver(mReceiver02, mFilter01);


/* 创建自定义Action常数的Intent(给PendingIntent参数之用) */
        Intent itSend = new Intent(SMS_SEND_ACTIOIN);

/* sentIntent参数为传送后接受的广播信息PendingIntent */
        PendingIntent mSendPI = PendingIntent.getBroadcast(
                getApplicationContext(),
                (int) System.currentTimeMillis(), itSend,
                PendingIntent.FLAG_UPDATE_CURRENT);

        Intent itDeliver = new Intent(SMS_DELIVERED_ACTION);
    /* deliveryIntent参数为送达后接受的广播信息PendingIntent */
        PendingIntent mDeliverPI = PendingIntent.getBroadcast(
                getApplicationContext(),
                (int) System.currentTimeMillis(), itDeliver,
                PendingIntent.FLAG_UPDATE_CURRENT);


        // 发送短信
        SmsManager smsManager = SmsManager.getDefault();
        String phoneNumber = getedtStringAfterTrim(edt_EmergencyPhone_SEPA);

        String massage = getSendStringBySMS(userName,userPhotoNum);

        ArrayList<String> list = smsManager.divideMessage(massage);
//        PendingIntent pIntent = PendingIntent.getActivity(this, 0, new Intent(this, SettingEmergencyPersonActivity.class), 0);
        if (massage.length() > 70) {
            //拆分短信
            ArrayList<String> phoneList = smsManager.divideMessage(massage);
            ArrayList<PendingIntent> mSendPIList = new ArrayList<>();
            ArrayList<PendingIntent> mDeliverPIList = new ArrayList<>();
            mSendPIList.add(mSendPI);
            mDeliverPIList.add(mDeliverPI);
            //发送短信
            smsManager.sendMultipartTextMessage(phoneNumber, null, phoneList, mSendPIList, mDeliverPIList);
        } else {
            //不超过70字时使用sendTextMessage发送
            smsManager.sendTextMessage(phoneNumber, null, massage, mSendPI, mDeliverPI);
        }
    }

    /**
     * 设置发送的信息内容(微信,QQ)
     * @param userName
     * @param myPhoneNum
     * @return
     */
    private String getSendString(String userName,String myPhoneNum) {
        String sendString = "【众觅】Hi，我是"+userName+"(手机尾号"+myPhoneNum.substring(myPhoneNum.length()-4,myPhoneNum.length())+")，正在使用『众觅』应用，把你设置成了我的紧急联系人，当我遇到危险的时候，『众觅』会把我的求助信息发送给你；更多内容你可以回复我以做进一步了解。http://www.dujoy.cn";
        return sendString;
    }
    /**
     * 设置发送的信息内容(短信分享)
     * @param userName
     * @param myPhoneNum
     * @return
     */
    private String getSendStringBySMS(String userName,String myPhoneNum) {
        String sendString = "【众觅】Hi，我是"+userName+"(手机尾号"+myPhoneNum.substring(myPhoneNum.length()-4,myPhoneNum.length())+")，正在使用『众觅』应用，把你设置成了我的紧急联系人，当我遇到危险的时候，『众觅』会把我的求助信息发送给你；更多内容你可以回复我以做进一步了解。";
        return sendString;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        /* 取消注册自定义Receiver */
        if (mReceiver01 != null && mReceiver02 != null) {
            unregisterReceiver(mReceiver01);
            unregisterReceiver(mReceiver02);
        }
    }

    /**
     * {
     * "session_id": "8527a2b6a58b4fa963fd",
     * "emergency_country_code": "0086",
     * "emergency_mobile": "13112345678",
     * "emergency_name": "张三",
     * }
     */
    String session_id;
    String emergency_country_code;
    String emergency_mobile;
    String emergency_name;

    private void SendEmergency() {
        if (getEidtTextLength(edt_EmergencyName_SEPA) > getedtLengthAfterTrim(edt_EmergencyName_SEPA)) {
            ToastUtil.shortshow(this, "姓名不能有空格");
        } else if (getedtLengthAfterTrim(edt_EmergencyName_SEPA) == 0 || getedtLengthAfterTrim(edt_EmergencyPhone_SEPA) == 0) {
            ToastUtil.shortshow(this, "内容不能为空");
        } else if (getedtLengthAfterTrim(edt_EmergencyPhone_SEPA) == 11) {
            RequestEmergency();
        } else {
            ToastUtil.shortshow(this, "手机号错误");
        }
    }

    private void RequestEmergency() {

        Log.e("obj=====", "开始请求");
        AsyncHttpResponseHandler res = new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                String str = new String(bytes);

                try {
                    JSONObject obj = new JSONObject(str);
                    int code = obj.getInt("code");
                    if (code == 0) {
                        getSharedPreferences(StaticBean.ContactPerson, Context.MODE_PRIVATE).edit().putString("name", getedtStringAfterTrim(edt_EmergencyName_SEPA)).commit();
                        getSharedPreferences(StaticBean.ContactPerson, Context.MODE_PRIVATE).edit().putString("PhoneNumber", getedtStringAfterTrim(edt_EmergencyPhone_SEPA)).commit();
                        //紧急联系人设置修改成功
                        getSharedPreferences(StaticBean.ContactPerson, Context.MODE_PRIVATE).edit().putBoolean("isEmergencyChanged", true).commit();
                        ToastUtil.shortshow(getApplicationContext(), "完成设置");
                        UserInfo.getUserInfo(getApplicationContext(), new UserInfo.GetUserInfoCallBack() {
                            @Override
                            public void callback(boolean state) {
                                SettingEmergencyPersonActivity.this.finish();
                            }
                        });
                    } else {
                        errorCode(code);
                        getSharedPreferences(StaticBean.ContactPerson, Context.MODE_PRIVATE).edit().putBoolean("isEmergencyChanged", false).commit();
                    }
                    Log.e("obj=====", obj.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                Log.e("失败", "失败");
            }
        };

        String url = ServerAPIConfig.RequestEmergency;
        session_id = getSharedPreferences(StaticBean.USER_INFO, Context.MODE_PRIVATE).getString("session_id", null);
        emergency_country_code = "0086";
        emergency_mobile = getedtStringAfterTrim(edt_EmergencyPhone_SEPA);
        emergency_name = getedtStringAfterTrim(edt_EmergencyName_SEPA);

        RequestParams params = new RequestParams();
        params.put("session_id", session_id);
        params.put("emergency_country_code", emergency_country_code);
        params.put("emergency_mobile", emergency_mobile);
        params.put("emergency_name", emergency_name);

        AndroidAsyncHttp.post(url, params, res);
    }

    PopupWindow popupWindow;
    View popView;

    private void ShowPop() {
        popupWindow = new PopupWindow();
        popupWindow.setBackgroundDrawable(getResources().getDrawable(R.color.transparent));
        popupWindow.setWidth(ScreenUtils.getScreenWidth(this));
        popupWindow.setHeight(ScreenUtils.getScreenHeight(this) / 3);
        popupWindow.setTouchable(true);
        popupWindow.setOutsideTouchable(true);
        popupWindow.setFocusable(true);
        popView = LayoutInflater.from(this).inflate(R.layout.popupwindow_sepa_inform, null);

        popupWindow.setContentView(popView);
        v_parentCover_SEPA.setVisibility(View.VISIBLE);
        v_parentCover_SEPA.setAlpha(0.5f);
        popupWindow.showAtLocation(bt_Complete_SEPA, Gravity.BOTTOM, 0, 0);

        initPopView();
        initPopEvent();
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                v_parentCover_SEPA.setVisibility(View.GONE);
            }
        });
    }

    private void initPopEvent() {
        img_QQ_Inform_SEPA.setOnClickListener(this);
        img_SMS_Inform_SEPA.setOnClickListener(this);
        img_WeiXin_Inform_SEPA.setOnClickListener(this);
        tv_Cancle_Inform_SEPA.setOnClickListener(this);
    }

    ImageView img_WeiXin_Inform_SEPA, img_SMS_Inform_SEPA, img_QQ_Inform_SEPA;
    TextView tv_Cancle_Inform_SEPA;

    private void initPopView() {
        img_QQ_Inform_SEPA = (ImageView) popView.findViewById(R.id.img_QQ_Inform_SEPA);
        img_SMS_Inform_SEPA = (ImageView) popView.findViewById(R.id.img_SMS_Inform_SEPA);
        img_WeiXin_Inform_SEPA = (ImageView) popView.findViewById(R.id.img_WeiXin_Inform_SEPA);
        tv_Cancle_Inform_SEPA = (TextView) popView.findViewById(R.id.tv_Cancle_Inform_SEPA);
    }


    private Bundle getBundle() {
        Bundle params = new Bundle();
        params.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, QQShare.SHARE_TO_QQ_TYPE_DEFAULT);
        params.putString(QQShare.SHARE_TO_QQ_TITLE, "众觅紧急联系人");
        params.putString(QQShare.SHARE_TO_QQ_SUMMARY, getSendString(userName,userPhotoNum));
        params.putString(QQShare.SHARE_TO_QQ_TARGET_URL, "http://www.dujoy.cn"+getSendStringBySMS(userName,userPhotoNum));
        params.putString(QQShare.SHARE_TO_QQ_APP_NAME, "众觅");
        return params;
    }

    //特别注意：一定要添加以下代码，才可以从回调listener中获取到消息。
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode){
            case Constants.REQUEST_QQ_SHARE:
                Tencent.onActivityResultData(requestCode, resultCode, data, iUiListener);
                break;
            case READ_CONTACT:
                if(data==null)
                {return;}
                //处理返回的data,获取选择的联系人信息
                Uri uri=data.getData();
                String[] contacts=getPhoneContacts(uri);

                if(contacts != null){

                    if (TextUtils.isEmpty(contacts[0])) {
                        return;
                    } else {
                        LogUtil.e(" contacts[0]---------------else------:"+contacts[0]);
                        edt_EmergencyName_SEPA.setText(contacts[0]);
                        edt_EmergencyPhone_SEPA.setText(contacts[1]);
                    }
                }
                break;
        }

    }


    private String[] getPhoneContacts(Uri uri){
        String[] contact=new String[2];
        //得到ContentResolver对象
        ContentResolver cr = getContentResolver();
        //取得电话本中开始一项的光标
        Cursor cursor=cr.query(uri,null,null,null,null);
        if(cursor.moveToFirst())
        {
            //取得联系人姓名
            int nameFieldColumnIndex=cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME);
            // 将光标移至开头 ，这个很重要，不小心很容易引起越界
            cursor.moveToFirst();
            contact[0]=cursor.getString(nameFieldColumnIndex);
            //取得电话号码
            String ContactId = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
            Cursor phone = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "=" + ContactId, null, null);
            if(phone.moveToFirst()){
                int columnIndex = phone.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
                LogUtil.e("columnIndex---------:"+columnIndex+"            phone"+phone.getColumnCount());
                // 将光标移至开头 ，这个很重要，不小心很容易引起越界
                phone.moveToFirst();
                String string = phone.getString(columnIndex);
                LogUtil.e("string---------"+string+"            contact.length:"+contact.length);
                contact[1] = string;
            }
           /* phone.close();
            cursor.close();*/
        }
        else
        {
            return null;
        }
        LogUtil.e(contact[0]);
        return contact;
    }

    public class BasseIUiListener implements IUiListener {

        @Override
        public void onComplete(Object o) {
            ToastUtil.shortshow(getApplicationContext(), o.toString());
            JSONObject obj = null;
            try {
                obj = new JSONObject(o.toString());
                Log.e("obj==", obj.toString());
                int ret = obj.getInt("ret");
                Log.e("ret", ret + "");
                if (ret == 0) {
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

        @Override
        public void onError(UiError uiError) {

        }

        @Override
        public void onCancel() {

        }
    }
}
