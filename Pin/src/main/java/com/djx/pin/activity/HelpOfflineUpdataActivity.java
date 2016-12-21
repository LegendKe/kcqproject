package com.djx.pin.activity;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeOption;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.baidu.mapapi.search.sug.OnGetSuggestionResultListener;
import com.baidu.mapapi.search.sug.SuggestionResult;
import com.baidu.mapapi.search.sug.SuggestionSearch;
import com.baidu.mapapi.search.sug.SuggestionSearchOption;
import com.djx.pin.R;
import com.djx.pin.adapter.SearchPositionAdapter;
import com.djx.pin.application.PinApplication;
import com.djx.pin.base.OldBaseActivity;
import com.djx.pin.beans.IDTokenInfo;
import com.djx.pin.beans.StaticBean;
import com.djx.pin.serverapiconfig.ServerAPIConfig;
import com.djx.pin.utils.AndroidAsyncHttp;
import com.djx.pin.utils.BitmapUtil;
import com.djx.pin.utils.DateUtils;
import com.djx.pin.utils.LogUtil;
import com.djx.pin.utils.ToastUtil;
import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.qiniu.android.http.ResponseInfo;
import com.qiniu.android.storage.UpCompletionHandler;
import com.qiniu.android.storage.UploadManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;

public class HelpOfflineUpdataActivity extends OldBaseActivity implements DialogInterface.OnShowListener, View.OnClickListener, AdapterView.OnItemClickListener,TextWatcher, OnGetSuggestionResultListener, OnGetGeoCoderResultListener, DialogInterface.OnDismissListener {
    protected final static String TAG = HelpOfflineUpdataActivity.class.getSimpleName();

    Context CONTEXT = HelpOfflineUpdataActivity.this;
    ImageView iv_Back,//dialog返回
            iv_Clear,//清空输入内容
            iv_AddPic0, iv_AddPic1, iv_AddPic2;
    GeoCoder mSearch = null; // 搜索模块，也可去掉地图模块独立使用
    Dialog dialog_SearchPosition,//搜索位置Dialog
            dialog_AddPic, dialog_ProgressBar, dialog_Date;
    View dialogView_AddPicVideo, dialogView_AddVideo, dialogView_ProgressBar, dialogView_Date,
            dialogView_SearchPosition;//搜索位置Dialog布局;
    String currentCity = null,//当前城市
            currentDistrict = null,//当前区
            currentProvince = null;//当前省份
    String path_Camera,//使用相机拍照的图片绝对路径
            dir_Camera;//使用相机拍照保存图片的文件夹路径
    String[] path_Photo = {"", "", ""};
    SharedPreferences sp;
    TextView tv_position,
            tv_start_time,
            tv_end_time,
            tv_date_confirm,
            tv_progressbar;//用来显示进度条对话框中的进度
    IDTokenInfo idTokenInfo_Pic = null;
    Button bt_camera,//相机按钮
            bt_photoalbum,//相册按钮
            bt_cancle,//取消按钮
            bt_updatainfo;//取消按钮;
    int requestCode_Gallery_Pic = 0,//打开图库的请求码,也用来标识要将返回的图片添加到iv_AddPic0,iv_AddPic1,iv_AddPic2中的那一个,默认为iv_AddPic0
            requestCode_Camera_Pic = 1;//调用相机拍照的请求码.

    EditText et_money, et_people_number, et_note;

    int iv_current_Position = 0;//用来标记用户点击的是哪一个 iv_AddPic.

    Intent intent_Gallery_Pic, intent_Camera_Pic;
    RelativeLayout rl_searh_position;
    LinearLayout ll_back;
    boolean isStartTime = true,//是否是开始时间
            isCancelled = false;//是否取消当前上传的文件

    int size_Pic = 0;
    UploadManager uploadManager;
    DatePicker dp;//日期选择器
    BitmapUtil.SizeMessage sizeMessage;

    EditText et_SearchPosition,//对话框中的edittext
            et_RewardPrice,//悬赏金额
            et_SOSInfo;//编辑求助信息
    ListView lv_Position;//对话框中的listview
    boolean[] isOK_Pic = new boolean[3];//判断全部照片是否上传成功
    SuggestionSearch mSuggestionSearch = null;
    List<SuggestionResult.SuggestionInfo> list = null;
    SearchPositionAdapter adapter;
    LayoutInflater inflater;
    SuggestionResult.SuggestionInfo suggestionInfo;
    BDLocation bdLocation;//用户当前的位置
    private boolean isPicFormat = false;//选择的图片格式是否正确
    private int flag;//标志开始还是结束时间
    private static final int DECIMAL_DIGITS = 1;
    private double latitude;
    private double longitude;
    private boolean lacationSlectFlag = false;//是通过选择位置发布
    private ReverseGeoCodeResult.AddressComponent addressDetail;
    private Bundle bundle;
    private TextView tv_help_word;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_real_help);
        bundle = getIntent().getExtras();
        bdLocation = PinApplication.getMyApp().getBdLocation();
        addressDetail = (ReverseGeoCodeResult.AddressComponent) bundle.getParcelable("addressDetail");
        currentCity = addressDetail.city;
        currentDistrict = addressDetail.district;
        currentProvince = addressDetail.province;

        initView();
        initEvent();
        initDialog();
        initGeoCoder();
        initSuggestionSearch();
    }

    private void initView() {
        inflater = LayoutInflater.from(this);
        sp = getSharedPreferences(StaticBean.USER_INFO, MODE_PRIVATE);
        rl_searh_position = (RelativeLayout) findViewById(R.id.rl_searh_position);
        ll_back= (LinearLayout) findViewById(R.id.ll_back);
        tv_position = (TextView) findViewById(R.id.tv_position);
        tv_start_time = (TextView) findViewById(R.id.tv_start_time);
        tv_end_time = (TextView) findViewById(R.id.tv_end_time);
        tv_start_time.setText(DateUtils.formatDate(new Date(System.currentTimeMillis()), DateUtils.yyyyMMDD));
        tv_end_time.setText(DateUtils.formatDate(new Date(System.currentTimeMillis() +86400000), DateUtils.yyyyMMDD));

        tv_help_word = (TextView) findViewById(R.id.tv_help_word);
        tv_help_word.setOnClickListener(this);

        et_money = (EditText) findViewById(R.id.et_money);
        et_people_number = (EditText) findViewById(R.id.et_people_number);
        et_note = (EditText) findViewById(R.id.et_note);
        bt_updatainfo = (Button) findViewById(R.id.bt_updatainfo);

        iv_AddPic0 = (ImageView) findViewById(R.id.iv_AddPic0);
        iv_AddPic1 = (ImageView) findViewById(R.id.iv_AddPic1);
        iv_AddPic2 = (ImageView) findViewById(R.id.iv_AddPic2);

        /**搜索位置对话框相关控件*/
        dialogView_SearchPosition = inflater.inflate(R.layout.layout_dialog_searchposition, null);
        iv_Back = (ImageView) dialogView_SearchPosition.findViewById(R.id.iv_Back);
        iv_Back.setOnClickListener(this);
        iv_Clear = (ImageView) dialogView_SearchPosition.findViewById(R.id.iv_Clear);
        et_SearchPosition = (EditText) dialogView_SearchPosition.findViewById(R.id.et_SearchPosition);
        et_SearchPosition.addTextChangedListener(this);

        lv_Position = (ListView) dialogView_SearchPosition.findViewById(R.id.lv_Position);
        list = new ArrayList<SuggestionResult.SuggestionInfo>();
        adapter = new SearchPositionAdapter(this);
        lv_Position.setAdapter(adapter);

        /**添加图片对话框相关控件*/
        dialogView_AddPicVideo = inflater.inflate(R.layout.layout_dialog_addpicvideo, null);
        bt_camera = (Button) dialogView_AddPicVideo.findViewById(R.id.bt_camera);
        bt_photoalbum = (Button) dialogView_AddPicVideo.findViewById(R.id.bt_photoalbum);
        bt_cancle = (Button) dialogView_AddPicVideo.findViewById(R.id.bt_cancle);

        /**选择时间对话框*/
        dialogView_Date = inflater.inflate(R.layout.layout_dialog_date, null);
        tv_date_confirm = (TextView) dialogView_Date.findViewById(R.id.tv_date_confirm);
        dp = (DatePicker) dialogView_Date.findViewById(R.id.dp);

        intent_Gallery_Pic = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent_Gallery_Pic.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        intent_Camera_Pic = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        dir_Camera = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).toString() + "/Camera/";
        sizeMessage = new BitmapUtil.SizeMessage(this, false, 60, 60);

        tv_position.setText(addressDetail.city + "  " + addressDetail.street + "  " + addressDetail.streetNumber);
    }

    private void initEvent() {
        rl_searh_position.setOnClickListener(this);
        ll_back.setOnClickListener(this);


        iv_Clear.setOnClickListener(this);
        lv_Position.setOnItemClickListener(this);

        bt_camera.setOnClickListener(this);
        bt_cancle.setOnClickListener(this);
        bt_photoalbum.setOnClickListener(this);

        iv_AddPic0.setOnClickListener(this);
        iv_AddPic1.setOnClickListener(this);
        iv_AddPic2.setOnClickListener(this);

        tv_start_time.setOnClickListener(this);
        tv_end_time.setOnClickListener(this);

        tv_date_confirm.setOnClickListener(this);

        bt_updatainfo.setOnClickListener(this);

        et_money.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().contains(".")) {
                    if (s.length() - 1 - s.toString().indexOf(".") > DECIMAL_DIGITS) {
                        s = s.toString().subSequence(0,
                                s.toString().indexOf(".") + DECIMAL_DIGITS + 1);
                        et_money.setText(s);
                        et_money.setSelection(s.length());
                    }
                }
                if (s.toString().trim().substring(0).equals(".")) {
                    s = "0" + s;
                    et_money.setText(s);
                    et_money.setSelection(2);
                }
                if (s.toString().startsWith("0")
                        && s.toString().trim().length() > 1) {
                    if (!s.toString().substring(1, 2).equals(".")) {
                        et_money.setText(s.subSequence(0, 1));
                        et_money.setSelection(1);
                        return;
                    }
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }


    /**
     * 检查用户输入的信息是否正确,正确返回true,反之false
     *
     * @return
     */
    private boolean checkUserInfo() {

        LogUtil.e("enter checkUserInfo");

        //检查位置
        if (null == tv_position.getText() || 0 == tv_position.getText().toString().length()) {
            ToastUtil.shortshow(CONTEXT, R.string.toast_tv_location_err);
            LogUtil.e(CONTEXT, "位置错误");
            return false;
        }
        //检查金额是否为空
        String money = et_money.getText().toString().trim();
        if (null == et_money || money.length() == 0) {
            ToastUtil.shortshow(CONTEXT, R.string.toast_et_monery_err);
            LogUtil.e(CONTEXT, "金额错误");
            return false;
        }

        //检查需要人手是否为空
        String people = et_people_number.getText().toString().trim();
        if (null == et_people_number.getText() || people.length() == 0) {
            ToastUtil.shortshow(CONTEXT, R.string.toast_non_et_people_number);
            LogUtil.e(CONTEXT, "需要人手错误");
            return false;
        }
        //检查开始时间是否正确
        if (null == tv_start_time.getText() || 0 == tv_start_time.getText().toString().length()) {
            ToastUtil.shortshow(CONTEXT, R.string.toast_non_start_time);
            LogUtil.e(CONTEXT, "开始时间错误");
            return false;
        }
        //检查开始时间是否正确
        if (null == tv_end_time.getText() || 0 == tv_end_time.getText().toString().length()) {
            ToastUtil.shortshow(CONTEXT, R.string.toast_non_end_time);
            LogUtil.e(CONTEXT, "结束时间错误");
            return false;
        }
        LogUtil.e("enter checkUserInfo ----  return true");
        return true;
    }

    /**
     * 上传照片至七牛服务器,
     * 由于视屏 照片上传是多线程,不能确定哪一个最后上传完成,
     * 所以当一个文件上传成功后调用updataPicInfo2Server()函数尝试上传至公司服务器.
     * 在updataPicInfo2Server()函数中已经实现了检查是否所有文件都上传至七牛服务器,
     * 只有在所有文件都上传至七牛服务器时才会上传这些信息至公司服务器,且只会上传一次
     */
    public void updataPic2Qiniu() {

        LogUtil.e("enter updataPic2Qiniu");

        if (null == idTokenInfo_Pic)
            return;
        //重置所有照片的是否上传成功的状态为false
        for (int i = 0; i < isOK_Pic.length; i++) {
            isOK_Pic[i] = false;
        }
        // 重用 uploadManager。一般地，只需要创建一个 uploadManager 对象
        if (null == uploadManager) {
            uploadManager = new UploadManager();
        }
        //上传照片
        for (int j = 0; j < size_Pic; j++) {
            uploadManager.put(path_Photo[j], idTokenInfo_Pic.list.get(j).id, idTokenInfo_Pic.list.get(j).token, new UpCompletionHandler() {
                @Override
                public void complete(String key, ResponseInfo responseInfo, JSONObject jsonObject) {
                    for (int k = 0; k < size_Pic; k++) {
                        if (key.equals(idTokenInfo_Pic.list.get(k).id)) {
                            if (responseInfo.isOK()) {
                                isOK_Pic[k] = true;
                                updataPicInfo2Server();
                            } else {
                                isOK_Pic[k] = false;
                                ToastUtil.shortshow(CONTEXT, R.string.toast_updata_failer);
                            }
                        }
                    }
                }
            }, null);
        }
    }


    /**
     * 获取图片需要的Token,并上传信息至七牛和公司服务器
     */
    public void getIdToken_Pic() {
        size_Pic = 0;//重置
        idTokenInfo_Pic = null;//重置
        //获取照片长度
        for (String e : path_Photo) {
            if (e.length() > 0)
                size_Pic = size_Pic + 1;//此时size是照片的数量
        }
        //如果没有图片则不获取图片id和token,并且不上传图片,直接进入updataPicInfo2Server();
        if (size_Pic == 0) {
            updataPicInfo2Server();
            return;
        }
        RequestParams params = new RequestParams();
        params.put("size", size_Pic);
        params.put("session_id", sp.getString("session_id", null));
        params.put("media_type", 1);
        AndroidAsyncHttp.post(ServerAPIConfig.GetIDToken, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                String str_json = new String(bytes);
                try {
                    JSONObject obj = new JSONObject(str_json);
                    if (0 != obj.getInt("code")) {
                        ToastUtil.shortshow(CONTEXT, R.string.toast_error_server);
                        errorCode(obj.getInt("code"));
                        LogUtil.e(CONTEXT, "服务器返回结果异常");
                        return;
                    }
                    idTokenInfo_Pic = new IDTokenInfo();
                    obj = obj.getJSONObject("result");
                    Gson gson = new Gson();
                    idTokenInfo_Pic = gson.fromJson(obj.toString(), IDTokenInfo.class);
                    isCancelled = false;
                    LogUtil.e("获取照片idTokenInfo_Pic成功");
                    updataPic2Qiniu();
                } catch (JSONException e) {
                    LogUtil.e(CONTEXT, "进入catch异常");
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                ToastUtil.shortshow(CONTEXT, R.string.toast_error_net);
                LogUtil.e(CONTEXT, R.string.toast_error_net);
                idTokenInfo_Pic = null;
            }
        });

    }

    /**
     * 上传照片信息至本公司服务器
     * <p/>
     * 所有文件都上传至七牛服务器时,这些文件的信息才会上传至本公司的服务器.否则不会上传这些文件的信息至本公司服务器
     */
    public void updataPicInfo2Server() {

        LogUtil.e("enter updataPicInfo2Server");
        for (int i = 0; i < size_Pic; i++) {
            if (!isOK_Pic[i]) {
                LogUtil.e("照片上传失败");
                return;
            }
        }
        JSONObject jsonObject = null;
        JSONArray jsonArray = null;
        StringEntity stringEntity = null;

        try {
            jsonObject = new JSONObject();
            jsonArray = new JSONArray();

            jsonObject.put("session_id", sp.getString("session_id", null));
            jsonObject.put("price", Double.parseDouble(et_money.getText().toString()));
            jsonObject.put("receiver_limit", Integer.parseInt(et_people_number.getText().toString()));
            jsonObject.put("description", et_note.getText().toString());
            jsonObject.put("start_time", DateUtils.parseDate(tv_start_time.getText().toString(), DateUtils.yyyyMMDD).getTime());

            long time1 = DateUtils.parseDate(tv_end_time.getText().toString(), DateUtils.yyyyMMDD).getTime();
            long time2 = time1 + 86300000;
            jsonObject.put("end_time", time2);

            jsonObject.put("province", currentProvince == null?"":currentProvince);
            jsonObject.put("district", currentDistrict == null?"":currentDistrict);
            jsonObject.put("city", currentCity == null?"":currentCity);
            String address = tv_position.getText().toString();
            jsonObject.put("address", address == null?"":address);

          /*  if(lacationSlectFlag == false){
                jsonObject.put("latitude", latitude);
                jsonObject.put("longitude", longitude);
            }*/
            jsonObject.put("latitude", latitude);
            jsonObject.put("longitude", longitude);


            for (int i = 0; i < size_Pic; i++) {
                JSONObject obj = new JSONObject();
                obj.put("id", idTokenInfo_Pic.list.get(i).id);
                obj.put("media_type", 1);
                jsonArray.put(obj);
            }
            jsonObject.put("media", jsonArray);
            LogUtil.e("求助信息=" + jsonObject.toString());
            stringEntity = new StringEntity(jsonObject.toString(), "UTF-8");
        } catch (Exception e) {
            LogUtil.e(CONTEXT, "enter catch");
            e.printStackTrace();
        }
        if (null == jsonArray || null == jsonObject || null == stringEntity) {
            LogUtil.e(CONTEXT, "数据打包成JSON异常");
            return;
        }
        AsyncHttpResponseHandler res = new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                String str_json = new String(bytes);
                try {
                    JSONObject obj = new JSONObject(str_json);
                    if (obj.getInt("code") == 0) {
                        obj = obj.getJSONObject("result");
                        Bundle bundle = new Bundle();
                        bundle.putString("id", obj.getString("id"));
                        bundle.putInt("type", 1);
                        Double amount = obj.getDouble("amount");
                        BigDecimal val = new BigDecimal(String.valueOf(amount));
                        bundle.putFloat("amount", val.floatValue());
                        startActivity(PayTypeActivity.class, bundle);
                    } else if(2113 == obj.getInt("code")){
                        ToastUtil.longshow(getApplicationContext(),R.string.sensitive_word);
                    }else {
                        ToastUtil.shortshow(CONTEXT, "上传信息异常");
                        errorCode(obj.getInt("code"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                LogUtil.e(CONTEXT, "enter onFailure");
                ToastUtil.shortshow(CONTEXT, R.string.toast_error_net);
            }
        };
        AndroidAsyncHttp.post(CONTEXT, ServerAPIConfig.Updata_Help, stringEntity, "application/json;charset=UTF-8", res);
    }


    /**
     * 初始化Dialog
     */
    private void initDialog() {
        /**添加图片对话框*/
        dialog_AddPic = new Dialog(this, R.style.dialog_transparent);
        dialog_AddPic.setContentView(dialogView_AddPicVideo);
        WindowManager.LayoutParams layoutParams_AddPic = dialog_AddPic.getWindow().getAttributes();
        layoutParams_AddPic.gravity = Gravity.BOTTOM;
        layoutParams_AddPic.width = getWindowManager().getDefaultDisplay().getWidth();
        layoutParams_AddPic.y = 0;
        dialog_AddPic.getWindow().setAttributes(layoutParams_AddPic);

        /**选择时间对话框*/
        dialog_Date = new Dialog(this, R.style.dialog_transparent);
        dialog_Date.setContentView(dialogView_Date);
        WindowManager.LayoutParams layoutParams_Date = dialog_Date.getWindow().getAttributes();
        layoutParams_Date.width = WindowManager.LayoutParams.MATCH_PARENT;
        layoutParams_Date.height = getWindowManager().getDefaultDisplay().getHeight() / 3;
        layoutParams_Date.gravity = Gravity.BOTTOM;
        dialog_Date.getWindow().setAttributes(layoutParams_Date);

        /**搜索位置的对话框*/
        dialog_SearchPosition = new Dialog(this, R.style.dialog_transparent);
        dialog_SearchPosition.setContentView(dialogView_SearchPosition);
        WindowManager.LayoutParams layoutParams_SearchPosition = dialog_SearchPosition.getWindow().getAttributes();
        layoutParams_SearchPosition.width = (int) (getWindowManager().getDefaultDisplay().getWidth() * 0.9);
        layoutParams_SearchPosition.height = (int) (getWindowManager().getDefaultDisplay().getHeight() * 0.8);
        layoutParams_SearchPosition.gravity = Gravity.CENTER;
        dialog_SearchPosition.getWindow().setAttributes(layoutParams_SearchPosition);
        dialog_SearchPosition.setOnShowListener(this);
    }

    @Override
    public void onShow(DialogInterface dialog) {
        if (dialog.equals(dialog_SearchPosition)) {
//            et_SearchPosition.setText("");
        }
    }




    public void getLocation(String city, String address) {
        LogUtil.e("enter getLocation");
        if (TextUtils.isEmpty(city) || TextUtils.isEmpty(address)) {
            return;
        }
        GeoCodeOption mGeoCodeOption = new GeoCodeOption();
        mGeoCodeOption.address(address);
        mGeoCodeOption.city(city);
        mSearch.geocode(mGeoCodeOption);
        // 设置查询结果监听者
        LogUtil.e("enter mSearch.geocode(mGeoCodeOption)");
        mSearch.setOnGetGeoCodeResultListener(new OnGetGeoCoderResultListener() {
            @Override
            public void onGetGeoCodeResult(GeoCodeResult geoCodeResult) {
                LatLng location = geoCodeResult.getLocation();
                latitude = location.latitude;
                longitude = location.longitude;
                bdLocation.setLatitude(latitude);
                bdLocation.setLongitude(longitude);
                LogUtil.e("----------2-----latitude----------"+latitude);
                LogUtil.e("----------2-----longitude----------"+longitude);
                LogUtil.e("----------2----address----------"+geoCodeResult.getAddress());
                getIdToken_Pic();
            }

            @Override
            public void onGetReverseGeoCodeResult(ReverseGeoCodeResult reverseGeoCodeResult) {

            }
        });
    }



    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            //点击返回按钮
            case R.id.ll_back:
                this.finish();
                break;
            //点击发送按钮
            case R.id.bt_updatainfo:

                if (checkUserInfo()) {
                    //先进行获取新的位置的经纬度
                  /*  if (mLocClient.isStarted() == false) {
                        mLocClient.start();
                    }*/

                    if(lacationSlectFlag == true){//1.列表选择的
                       LogUtil.e("enter -------------lacationSlectFlag == true");
                        //getLocation(suggestionInfo.city,tv_position.getText().toString());
                        getIdToken_Pic();
                    } else {//2.上一界面传过来的
                        LogUtil.e("enter -------------lacationSlectFlag == false");
                       /* getLocation(addressDetail.city,tv_position.getText().toString());*/


                        longitude =  bundle.getDouble("longitude");
                        latitude = bundle.getDouble("latitude");
                        getIdToken_Pic();
                    }
                }
                break;
            //点击日期确定按钮
            case R.id.tv_date_confirm:
                if (isStartTime) {
                    tv_start_time.setText(dp.getYear() + "-" + (dp.getMonth() + 1) + "-" + dp.getDayOfMonth());
                } else {
                    tv_end_time.setText(dp.getYear() + "-" + (dp.getMonth() + 1) + "-" + dp.getDayOfMonth());
                }
                dialog_Date.dismiss();
                break;
            //点击开始时间
            case R.id.tv_start_time:
               /* isStartTime = true;
                dialog_Date.show();*/
                flag = 0;
                showDialog(1);
                break;
            //点击结束时间
            case R.id.tv_end_time:
               /* isStartTime = false;
                dialog_Date.show();*/
                flag = 1;
                showDialog(1);
                break;

            //点击添加图片按钮
            case R.id.iv_AddPic0:
                iv_current_Position = 0;
                dialog_AddPic.show();
                break;
            //点击添加图片按钮
            case R.id.iv_AddPic1:
                iv_current_Position = 1;
                dialog_AddPic.show();
                break;
            //点击添加图片按钮
            case R.id.iv_AddPic2:
                iv_current_Position = 2;
                dialog_AddPic.show();
                break;
            //点击对话框取消按钮
            case R.id.bt_cancle:
                dialog_AddPic.dismiss();
                //点击搜索位置按钮
            case R.id.rl_searh_position:
                Log.i(TAG, tv_position.getText().toString());
                et_SearchPosition.setText(tv_position.getText());
                dialog_SearchPosition.show();
                break;
            //点击对话框相机
            case R.id.bt_camera:
                startTakePhotoByPermissions();
                break;
            //点击对话框相册
            case R.id.bt_photoalbum:
                startReadSDCardByPermissions();
                break;
            case R.id.iv_Clear:
                et_SearchPosition.setText("");
                break;
            case R.id.tv_help_word:
                Intent intent = new Intent(this,TextActivity.class);
                Bundle bundle = new Bundle();
                bundle.putInt("TextContent",7);
                bundle.putString("url","http://www.dujoy.cn/app/step-reward/index.html");
                intent.putExtras(bundle);
                startActivity(intent);
                break;
            case R.id.iv_Back:
                LogUtil.e("返回监听--------------");
                dialog_SearchPosition.dismiss();
                break;
        }
    }

    //监听器，用户监听用户点下DatePikerDialog的set按钮时，所设置的年月日
    DatePickerDialog.OnDateSetListener onDateSetListener = new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            if(flag == 0){
                tv_start_time.setText(year + "-" + (monthOfYear+1) + "-" + dayOfMonth);
            }else if(flag == 1){
                tv_end_time.setText(year + "-" + (monthOfYear+1) + "-" + dayOfMonth);
            }
        }
    };

    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case 1:
                Calendar dateAndTime = Calendar.getInstance(Locale.CHINA);//? --获取一个日历对象；
                int v_year = dateAndTime.get(Calendar.YEAR);
                int v_month = dateAndTime.get(Calendar.MONTH);
                int v_day = dateAndTime.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog datePickerDialog = new DatePickerDialog(this, onDateSetListener, v_year, v_month, v_day);
                return datePickerDialog;

        }
        return null;
    }
    @Override
    public void startTakePhoto() {
        File file_Dir = new File(dir_Camera);
        if (file_Dir.exists() == false) {
            file_Dir.mkdirs();
        }
        File file_pic = new File(dir_Camera, String.valueOf(System.currentTimeMillis())
                + ".jpg");
        path_Camera = file_pic.getPath();
        Uri picUri = Uri.fromFile(file_pic);
        intent_Camera_Pic.putExtra(MediaStore.EXTRA_OUTPUT, picUri);
        startActivityForResult(intent_Camera_Pic, requestCode_Camera_Pic);
        dialog_AddPic.dismiss();
    }

    @Override
    public void startReadSDCard() {
        startActivityForResult(intent_Gallery_Pic, requestCode_Gallery_Pic);
        dialog_AddPic.dismiss();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        /**
         * 打开图库获取图片并添加到iv_AddPic上
         *获取图片并显示到对应的imageView上.
         *但是如果前面的imageview没有图片,将优先显示到前面的imageview上
         */
        if (requestCode == requestCode_Gallery_Pic && resultCode == Activity.RESULT_OK && null != data) {
            String picturePath = getPath(this, data.getData());
            String picFormat = picturePath.substring(picturePath.lastIndexOf("."));
            for (int i = 0; i < StaticBean.PIC_FORMAT.length; i++) {
                if (StaticBean.PIC_FORMAT[i].equals(picFormat)) {
                    isPicFormat = true;
                }
            }
            if (!isPicFormat) {
                Toast.makeText(this, R.string.toast_pic_format_error, Toast.LENGTH_SHORT).show();
                return;
            }
            switch (iv_current_Position) {
                case 0:
                    path_Photo[0] = picturePath;
                    iv_AddPic0.setImageBitmap(BitmapUtil.loadBitmap(picturePath, sizeMessage));
                    break;
                case 1:
                    if (null == path_Photo[0] || 0 == path_Photo[0].length()) {
                        path_Photo[0] = picturePath;
                        iv_AddPic0.setImageBitmap(BitmapUtil.loadBitmap(picturePath, sizeMessage));
                    } else {
                        path_Photo[1] = picturePath;
                        iv_AddPic1.setImageBitmap(BitmapUtil.loadBitmap(picturePath, sizeMessage));
                    }
                    break;
                case 2:
                    if (null == path_Photo[0] || 0 == path_Photo[0].length()) {
                        path_Photo[0] = picturePath;
                        iv_AddPic0.setImageBitmap(BitmapUtil.loadBitmap(picturePath, sizeMessage));
                    } else if (null == path_Photo[1] || 0 == path_Photo[1].length()) {
                        path_Photo[1] = picturePath;
                        iv_AddPic1.setImageBitmap(BitmapUtil.loadBitmap(picturePath, sizeMessage));
                    } else {
                        path_Photo[2] = picturePath;
                        iv_AddPic2.setImageBitmap(BitmapUtil.loadBitmap(picturePath, sizeMessage));
                    }
                    break;
            }
            isPicFormat = false;
        }
        /**
         * 调用相机拍照返回的结果在此处理
         *获取图片并显示到对应的imageView上.
         *但是如果前面的imageview没有图片,将优先显示到前面的imageview上
         */
        if (requestCode == requestCode_Camera_Pic && resultCode == Activity.RESULT_OK) {
            switch (iv_current_Position) {
                case 0:
                    path_Photo[0] = path_Camera;
                    iv_AddPic0.setImageBitmap(BitmapUtil.loadBitmap(path_Camera, sizeMessage));
                    break;
                case 1:
                    if (null == path_Photo[0] || 0 == path_Photo[0].length()) {
                        path_Photo[0] = path_Camera;
                        iv_AddPic0.setImageBitmap(BitmapUtil.loadBitmap(path_Camera, sizeMessage));
                    } else {
                        path_Photo[1] = path_Camera;
                        iv_AddPic1.setImageBitmap(BitmapUtil.loadBitmap(path_Camera, sizeMessage));
                    }
                    break;
                case 2:
                    if (null == path_Photo[0] || 0 == path_Photo[0].length()) {
                        path_Photo[0] = path_Camera;
                        iv_AddPic0.setImageBitmap(BitmapUtil.loadBitmap(path_Camera, sizeMessage));
                    } else if (null == path_Photo[1] || 0 == path_Photo[1].length()) {
                        path_Photo[1] = path_Camera;
                        iv_AddPic1.setImageBitmap(BitmapUtil.loadBitmap(path_Camera, sizeMessage));
                    } else {
                        path_Photo[2] = path_Camera;
                        iv_AddPic2.setImageBitmap(BitmapUtil.loadBitmap(path_Camera, sizeMessage));
                    }
                    break;
            }

        }
    }


    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        /**如果当前城市currentCity为空或长度为0  或者用户输入的字符串为空或长度为0 都直接return.*/

        LogUtil.e("onTextChanged()--------------------currentCity:"+currentCity+"-----------------------s:"+s);

        if (TextUtils.isEmpty(currentCity) || TextUtils.isEmpty(s)) {
            iv_Clear.setVisibility(View.INVISIBLE);
            list.clear();
            adapter.notifyDataSetChanged();
            return;
        }
        iv_Clear.setVisibility(View.VISIBLE);
        mSuggestionSearch.requestSuggestion((new SuggestionSearchOption()).city(currentCity).keyword(s.toString()));
    }



    @Override
    public void afterTextChanged(Editable s) {
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        suggestionInfo = adapter.getItem(position);
        lacationSlectFlag = true;
        tv_position.setText(suggestionInfo.city + "  " + suggestionInfo.district + "  " + suggestionInfo.key);
        currentDistrict = suggestionInfo.district;
        currentProvince = "";
        currentCity = suggestionInfo.city;
        latitude = suggestionInfo.pt.latitude;
        longitude = suggestionInfo.pt.longitude;
        dialog_SearchPosition.dismiss();
    }

    /**
     * 初始化搜索位置模块
     */
    private void initSuggestionSearch() {
        mSuggestionSearch = SuggestionSearch.newInstance();
        mSuggestionSearch.setOnGetSuggestionResultListener(this);
    }


    /**
     * // 初始化经纬度与地理位置互转模块，注册事件监听
     */
    private void initGeoCoder() {
        mSearch = GeoCoder.newInstance();
       //mSearch.setOnGetGeoCodeResultListener(this);


    }

    /**
     * GEO编码,将文字性的地理位置转化为经纬度
     *
     * @param geoCodeResult
     */
    @Override
    public void onGetGeoCodeResult(GeoCodeResult geoCodeResult) {

    }

    /**
     * 反GEO编码,将经纬度转化为文字性的地理位置
     *
     * @param result
     */
    @Override
    public void onGetReverseGeoCodeResult(ReverseGeoCodeResult result) {
        LogUtil.e("经纬度转化为文字性的地理位置:onGetReverseGeoCodeResult(ReverseGeoCodeResult result)");
        if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
            Toast.makeText(this, "抱歉，未能找到结果", Toast.LENGTH_LONG)
                    .show();
            return;
        }
        currentDistrict = result.getAddressDetail().district;
        currentCity = result.getAddressDetail().city;
        currentProvince = result.getAddressDetail().province;
        tv_position.setText(result.getAddressDetail().city + "  " + result.getAddressDetail().street + "  " + result.getAddressDetail().streetNumber);
    }

    /**
     * 获取用户搜索的位置
     */
    @Override
    public void onGetSuggestionResult(SuggestionResult suggestionResult) {

        list.clear();
        //未找到相关结果
        if (suggestionResult == null || suggestionResult.getAllSuggestions() == null) {
        } else {
            //如果搜索到的uid city district 其中有一个为空的话则过滤掉.不显示给用户
            for (SuggestionResult.SuggestionInfo e :
                    suggestionResult.getAllSuggestions()) {
                if (e.uid.length() == 0 || e.city.length() == 0 || e.district.length() == 0) {
                    continue;
                }
                list.add(e);
            }
        }
        adapter.setList(list);
        adapter.notifyDataSetChanged();

    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        if (dialog == dialog_ProgressBar) {
            /**不管上传是否完成,当dialog_ProgressBar消失时,都取消上传,如果上传完成*/
            tv_progressbar.setText(0 + "%");
            isCancelled = true;
        }
    }
}
