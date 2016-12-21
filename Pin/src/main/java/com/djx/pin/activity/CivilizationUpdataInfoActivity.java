package com.djx.pin.activity;

import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.baidu.mapapi.search.sug.OnGetSuggestionResultListener;
import com.baidu.mapapi.search.sug.SuggestionResult;
import com.baidu.mapapi.search.sug.SuggestionSearch;
import com.baidu.mapapi.search.sug.SuggestionSearchOption;
import com.djx.pin.R;
import com.djx.pin.adapter.SearchPositionAdapter;
import com.djx.pin.application.PinApplication;
import com.djx.pin.base.OldBaseActivity;
import com.djx.pin.beans.EventBeans;
import com.djx.pin.beans.IDTokenInfo;
import com.djx.pin.beans.StaticBean;
import com.djx.pin.serverapiconfig.ServerAPIConfig;
import com.djx.pin.utils.AndroidAsyncHttp;
import com.djx.pin.utils.ScreenUtils;
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
import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;
import de.greenrobot.event.Subscribe;
import de.greenrobot.event.ThreadMode;

/**
 * Created by Administrator on 2016/6/22.
 */
public class CivilizationUpdataInfoActivity extends OldBaseActivity implements View.OnClickListener, OnGetGeoCoderResultListener, DialogInterface.OnShowListener, TextWatcher, OnGetSuggestionResultListener, AdapterView.OnItemClickListener {

    private TextView tv_IWSA_Cancel, tv_IWSA_Send, tv_ChoseLocation_IWSA;
    private EditText edt_IWSA_Massage;
    private ImageView img_IWSA_AddImageView1, img_IWSA_AddImageView2, img_IWSA_AddImageView3, img_IWSA_AddImageView4;
    private View v_ParentCover_CDA;

    Dialog dialog_SearchPosition;//搜索位置Dialog
    View dialogView_SearchPosition;//搜索位置Dialog布局

    ImageView iv_Back,//dialog返回
            iv_Clear;//清空输入内容
    EditText et_SearchPosition;//对话框中的edittext
    ListView lv_Position;//对话框中的listview
    List<SuggestionResult.SuggestionInfo> list = null;
    SearchPositionAdapter adapter;
    String currentCity = null;//当前城市
    //百度地图百度地图百度地图百度地图百度地图百度地图百度地图百度地图百度地图百度地图百度地图百度地图百度地图百度地图百度地图百度地图百度地图百度地图百度地图百度地图百度地图百度地图百度地图
    SuggestionSearch mSuggestionSearch = null;
    /**
     * 用户位置的经纬度
     */
    private double mCurrentLantitude = 0;
    private double mCurrentLongitude = 0;
    GeoCoder mSearch = null; // 搜索模块，也可去掉地图模块独立使用

    //百度地图百度地图百度地图百度地图百度地图百度地图百度地图百度地图百度地图百度地图百度地图百度地图百度地图百度地图百度地图百度地图百度地图百度地图百度地图百度地图百度地图百度地图百度地图


    String[] path = {"", "", "", ""};
    private PinApplication myApp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_civilizationupdatainfo);
        initView();
        myApp = PinApplication.getMyApp();
        initEvent();
        initDialog();
        initBaiduMap();
        initSuggestionSearch();

    }

    @Subscribe(threadMode = ThreadMode.MainThread)
    public void onEvent(EventBeans eventBeans){
        if(eventBeans.style == 1){//表示已更新一次定位
//            if(!myApp.isMapOnLocation || myApp.){//如果地图页正在进行30秒倒计时定位,则不关闭
//                myApp.stopLocationClient();
//            }
        }
    }

    private void initEvent() {

        tv_IWSA_Cancel.setOnClickListener(this);
        tv_IWSA_Send.setOnClickListener(this);
        tv_ChoseLocation_IWSA.setOnClickListener(this);

        img_IWSA_AddImageView1.setOnClickListener(this);
        img_IWSA_AddImageView2.setOnClickListener(this);
        img_IWSA_AddImageView3.setOnClickListener(this);
        img_IWSA_AddImageView4.setOnClickListener(this);

        et_SearchPosition.addTextChangedListener(this);
        lv_Position.setOnItemClickListener(this);
    }

    private void initView() {
        tv_IWSA_Cancel = (TextView) findViewById(R.id.tv_IWSA_Cancel);
        tv_IWSA_Send = (TextView) findViewById(R.id.tv_IWSA_Send);
        tv_ChoseLocation_IWSA = (TextView) findViewById(R.id.tv_ChoseLocation_IWSA);
        edt_IWSA_Massage = (EditText) findViewById(R.id.edt_IWSA_Massage);
        img_IWSA_AddImageView1 = (ImageView) findViewById(R.id.img_IWSA_AddImageView1);
        img_IWSA_AddImageView2 = (ImageView) findViewById(R.id.img_IWSA_AddImageView2);
        img_IWSA_AddImageView3 = (ImageView) findViewById(R.id.img_IWSA_AddImageView3);
        img_IWSA_AddImageView4 = (ImageView) findViewById(R.id.img_IWSA_AddImageView4);

        v_ParentCover_CDA = findViewById(R.id.v_ParentCover_CDA);

        /**搜索位置对话框相关控件*/
        dialogView_SearchPosition = getLayoutInflater().inflate(R.layout.layout_dialog_searchposition, null);
        iv_Back = (ImageView) dialogView_SearchPosition.findViewById(R.id.iv_Back);
        iv_Back.setOnClickListener(this);
        iv_Clear = (ImageView) dialogView_SearchPosition.findViewById(R.id.iv_Clear);
        iv_Clear.setOnClickListener(this);
        et_SearchPosition = (EditText) dialogView_SearchPosition.findViewById(R.id.et_SearchPosition);
        lv_Position = (ListView) dialogView_SearchPosition.findViewById(R.id.lv_Position);
        list = new ArrayList<SuggestionResult.SuggestionInfo>();
        adapter = new SearchPositionAdapter(this);
        lv_Position.setAdapter(adapter);
    }


    /**
     * 初始化Dialog
     */
    private void initDialog() {

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


    public void initBaiduMap() {

       /* PinApplication.getMyApp().startLocationClient();*/
        mSearch = GeoCoder.newInstance();
        mSearch.setOnGetGeoCodeResultListener(this);
        // 反Geo搜索
        mSearch.reverseGeoCode(new ReverseGeoCodeOption()
                .location(new LatLng(myApp.getBdLocation().getLatitude(), myApp.getBdLocation().getLongitude())));
    }

    /**
     * 初始化搜索位置模块
     */
    private void initSuggestionSearch() {
        mSuggestionSearch = SuggestionSearch.newInstance();
        mSuggestionSearch.setOnGetSuggestionResultListener(this);
    }


    Intent intent;
    //相机拍照文件存储路径
    private File tempFile = new File(Environment.getExternalStorageDirectory(), getPhotoFileName());

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_Back:
                dialog_SearchPosition.dismiss();
                break;
            case R.id.iv_Clear:
                et_SearchPosition.setText("");
                break;
            case R.id.tv_IWSA_Cancel:
                this.finish();
                break;
            case R.id.tv_IWSA_Send:
                if(TextUtils.isEmpty(edt_IWSA_Massage.getText().toString().trim())){
                    ToastUtil.shortshow(this,"内容不能为空");
                    return;
                }
                getQiniu();
                break;
            case R.id.tv_ChoseLocation_IWSA:
                dialog_SearchPosition.show();
                break;
            case R.id.img_IWSA_AddImageView1:
                showPop(1);
                break;
            case R.id.img_IWSA_AddImageView2:
                showPop(2);
                break;
            case R.id.img_IWSA_AddImageView3:
                showPop(3);
                break;
            case R.id.img_IWSA_AddImageView4:
                showPop(4);
                break;
            //图片选择弹窗点击事件
            case R.id.tv_TakePhotots_Popwin:
                startTakePhotoByPermissions();
                popupWindoew.dismiss();
                break;
            case R.id.tv_Piktures_Popwin:
                startReadSDCardByPermissions();
                popupWindoew.dismiss();
                break;
            case R.id.tv_Cancel_Popwin:
                popupWindoew.dismiss();
                break;
        }
    }

    @Override
    public void startTakePhoto() {
        intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // 指定调用相机拍照后照片的储存路径
        intent.putExtra(MediaStore.EXTRA_OUTPUT,
                Uri.fromFile(tempFile));
        startActivityForResult(intent, 1);
    }

    @Override
    public void startReadSDCard() {
        intent = new Intent();
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, 0);
    }

    // 使用系统当前日期加以调整作为照片的名称
    private String getPhotoFileName() {
        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "'IMG'_yyyyMMdd_HHmmss");
        return dateFormat.format(date) + ".jpg";
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            String picPath;
            if (requestCode == 0) {
                ContentResolver contentResolver = getContentResolver();
                picPath = getPath(getApplicationContext(), data.getData());
                ToastUtil.shortshow(this, picPath.toString());
                try {
                    Bitmap bitmap = BitmapFactory.decodeStream(contentResolver.openInputStream(data.getData()));
                    switch (whichImage) {
                        case 1:
                            img_IWSA_AddImageView1.setImageBitmap(bitmap);
                            path[0] = picPath;
                            img_IWSA_AddImageView2.setVisibility(View.VISIBLE);
                            break;
                        case 2:
                            img_IWSA_AddImageView2.setImageBitmap(bitmap);
                            path[1] = picPath;
                            img_IWSA_AddImageView3.setVisibility(View.VISIBLE);
                            break;
                        case 3:
                            img_IWSA_AddImageView3.setImageBitmap(bitmap);
                            path[2] = picPath;
                            img_IWSA_AddImageView4.setVisibility(View.VISIBLE);
                            break;
                        case 4:
                            img_IWSA_AddImageView4.setImageBitmap(bitmap);
                            path[3] = picPath;
                            break;
                    }

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            } else if (requestCode == 1) {
                Uri uri = Uri.fromFile(tempFile);
                try {
                    picPath = getPath(getApplicationContext(), uri);
                    Bitmap bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(uri));
                    switch (whichImage) {
                        case 1:
                            img_IWSA_AddImageView1.setImageBitmap(bitmap);
                            path[0] = picPath;
                            img_IWSA_AddImageView2.setVisibility(View.VISIBLE);
                            break;
                        case 2:
                            img_IWSA_AddImageView2.setImageBitmap(bitmap);
                            path[1] = picPath;
                            img_IWSA_AddImageView3.setVisibility(View.VISIBLE);
                            break;
                        case 3:
                            img_IWSA_AddImageView3.setImageBitmap(bitmap);
                            path[2] = picPath;
                            img_IWSA_AddImageView4.setVisibility(View.VISIBLE);
                            break;
                        case 4:
                            img_IWSA_AddImageView4.setImageBitmap(bitmap);
                            path[3] = picPath;
                            break;
                    }
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }

            }
        }
        if (path[0] == null) {
            img_IWSA_AddImageView1.setVisibility(View.VISIBLE);
            img_IWSA_AddImageView2.setVisibility(View.INVISIBLE);
            img_IWSA_AddImageView3.setVisibility(View.INVISIBLE);
            img_IWSA_AddImageView4.setVisibility(View.INVISIBLE);

        }
    }

    private PopupWindow popupWindoew;
    private View popView;

    private int whichImage = 0;

    public void showPop(int i) {
        whichImage = i;
        popupWindoew = new PopupWindow();
        popupWindoew.setFocusable(true);
        popupWindoew.setOutsideTouchable(true);
        popupWindoew.setTouchable(true);
        popupWindoew.setWidth(ScreenUtils.getScreenWidth(this));
        popupWindoew.setHeight(ScreenUtils.getScreenHeight(this) / 3);
        popView = LayoutInflater.from(this).inflate(R.layout.popwindow_photochose_ipda, null);
        popupWindoew.setContentView(popView);
        popupWindoew.setBackgroundDrawable(getResources().getDrawable(R.color.transparent));
        parentCover(1);
        //初始化抢单弹窗控件和事件
        initPopView();
        initPopEvent();
        popupWindoew.showAtLocation(tv_IWSA_Cancel, Gravity.BOTTOM, 0, 0);
        popupWindoew.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                parentCover(2);
            }
        });
    }

    private void initPopEvent() {
        tv_TakePhotots_Popwin.setOnClickListener(this);
        tv_Piktures_Popwin.setOnClickListener(this);
        tv_Cancel_Popwin.setOnClickListener(this);
    }

    private TextView tv_TakePhotots_Popwin, tv_Piktures_Popwin, tv_Cancel_Popwin;

    private void initPopView() {
        tv_TakePhotots_Popwin = (TextView) popView.findViewById(R.id.tv_TakePhotots_Popwin);
        tv_Piktures_Popwin = (TextView) popView.findViewById(R.id.tv_Piktures_Popwin);
        tv_Cancel_Popwin = (TextView) popView.findViewById(R.id.tv_Cancel_Popwin);
    }

    public void parentCover(int i) {
        switch (i) {
            case 1:
                v_ParentCover_CDA.setVisibility(View.VISIBLE);
                v_ParentCover_CDA.setAlpha(0.5f);
                break;
            case 2:
                v_ParentCover_CDA.setVisibility(View.GONE);
                break;
        }
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
        if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
            Toast.makeText(this, "抱歉，未能找到结果", Toast.LENGTH_LONG)
                    .show();
            return;
        }
        currentCity = result.getAddressDetail().city;
        tv_ChoseLocation_IWSA.setText(result.getAddressDetail().city + "  " + result.getAddressDetail().street + "  " + result.getAddressDetail().streetNumber);
    }

    @Override
    public void onShow(DialogInterface dialog) {
        if (dialog == dialog_SearchPosition) {
            et_SearchPosition.setText("");
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        /**如果当前城市currentCity为空或长度为0  或者用户输入的字符串为空或长度为0 都直接return.*/
        if (null == currentCity || currentCity.length() == 0 || null == s || s.length() == 0) {
            iv_Clear.setVisibility(View.INVISIBLE);
            list.clear();
            adapter.notifyDataSetChanged();
            return;
        }
        iv_Clear.setVisibility(View.VISIBLE);
        mSuggestionSearch.requestSuggestion((new SuggestionSearchOption())
                .city(currentCity).keyword(s.toString()));
    }

    @Override
    public void afterTextChanged(Editable s) {
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
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        mCurrentLantitude = adapter.getItem(position).pt.latitude;
        mCurrentLongitude = adapter.getItem(position).pt.longitude;
        tv_ChoseLocation_IWSA.setText(adapter.getItem(position).city + "  " + adapter.getItem(position).district + "  " + adapter.getItem(position).key);
        dialog_SearchPosition.dismiss();
    }


    /**
     * 获取七牛上传id/token
     * 参数：session_id String 用户会话id
     * size Number 需要id/token的数量
     * media_type Number 资源类型，1-图片，2-视频
     */
    //图片数量
    int size = 0;
    String session_id;
    int media_type = 1;
    IDTokenInfo idTokenInfo;


    public void getQiniu() {

        size = 0;
        session_id = getSharedPreferences(StaticBean.USER_INFO, Context.MODE_PRIVATE).getString("session_id", null);
        if (session_id == null || session_id.equals("")) {
            ToastUtil.shortshow(getApplicationContext(), "请先登录应用");
            return;
        } else {
            for (int i = 0; i < 4; i++) {
                if (path[i] != null && path[i].length() != 0) {
                    size = size + 1;
                }
            }
            if (size == 0) {
                sendMassage();
                return;
            }
            RequestParams params = new RequestParams();
            params.put("session_id", session_id);
            params.put("size", size);
            params.put("media_type", media_type);


            AsyncHttpResponseHandler upHandler = new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int i, Header[] headers, byte[] bytes) {
                    String str_json = new String(bytes);
                    try {
                        JSONObject obj = new JSONObject(str_json);
                        if (obj.getInt("code") != 0) {
                            ToastUtil.shortshow(CivilizationUpdataInfoActivity.this, "服务器返回结果异常");
                            errorCode(obj.getInt("code"));
                            return;
                        }
                        Gson gson = new Gson();
                        idTokenInfo = gson.fromJson(obj.getString("result").toString(), IDTokenInfo.class);
                        upIamgeView();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                    ToastUtil.shortshow(getApplicationContext(), "失败");
                }
            };
            AndroidAsyncHttp.post(ServerAPIConfig.GetIDToken, params, upHandler);
            ToastUtil.shortshow(getApplicationContext(), size + "");
        }
    }

    /**
     * 七牛上传图片
     */
//图片上传成功数量
    int trueNumber = 0;

    public void upIamgeView() {
        trueNumber = 0;
        final UploadManager upLoadManager = new UploadManager();
        for (int i = 0; i < size; i++) {

            UpCompletionHandler upComplete = new UpCompletionHandler() {
                @Override
                public void complete(String key, ResponseInfo responseInfo, JSONObject jsonObject) {
                    for (int j = 0; j < size; j++) {
                        if (key.equals(idTokenInfo.list.get(j).id)) {
                            if (responseInfo.isOK()) {
                                //如果上传成功,上传成功数就加1
                                trueNumber = trueNumber + 1;
                            } else {
                                //如果上传失败直接结束图片上传
                                ToastUtil.shortshow(getApplicationContext(), "上传失败");
                                return;
                            }
                        }
                    }
                    //如果上传成功数等于图片数量,开始向服务器发布信息
                    if (trueNumber == size) {
                        sendMassage();
                    } else {
                        ToastUtil.shortshow(getApplicationContext(), "正在上传");
                    }

                }
            };
            String token = idTokenInfo.list.get(i).token;
            String key = idTokenInfo.list.get(i).id;
//            验证失败的时候
//            if (i==2){
//                token="xxxx";
//            }

            upLoadManager.put(path[i], key, token, upComplete, null);
        }

    }

    /**
     * "session_id": "4085c9d4f420864c407e",
     * "latitude": 0,
     * "longitude": 0,
     * "location": "上海市徐汇区",
     * "description": "描述内容",
     * "media": [
     * {
     * "id": "ccbd1e04fcfb36182ead",
     * "media_type": 1
     * },
     * {
     * "id": "ccbd1e04fcfb36182ead",
     * "media_type": 2
     * }
     * ]
     */
    public void sendMassage() {

        BDLocation bdLocation = PinApplication.getMyApp().getBdLocation();
        if(bdLocation == null){
            ToastUtil.shortshow(this,"定位中");
            return;
        }
        session_id = getSharedPreferences(StaticBean.USER_INFO, Context.MODE_PRIVATE).getString("session_id", null);
        String location = tv_ChoseLocation_IWSA.getText().toString();
        String description = edt_IWSA_Massage.getText().toString();
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("session_id", session_id);
            jsonObject.put("latitude", bdLocation.getLatitude());
            jsonObject.put("longitude", bdLocation.getLongitude());
            jsonObject.put("location", location);
            jsonObject.put("description", description);

            JSONArray jsonArray = new JSONArray();
            for (int i = 0; i < size; i++) {
                JSONObject obj = new JSONObject();
                obj.put("id", idTokenInfo.list.get(i).id);
                obj.put("media_type", 1);
                jsonArray.put(obj);
            }

            jsonObject.put("media", jsonArray);
            StringEntity stringEntity = new StringEntity(jsonObject.toString(), "utf-8");

            AsyncHttpResponseHandler res = new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int i, Header[] headers, byte[] bytes) {
                    String str_json = new String(bytes);
                    try {
                        JSONObject obj = new JSONObject(str_json);
                        int code = obj.getInt("code");

                        Log.i("code","code:  ========"+ code);
                        if (code == 0) {
                            ToastUtil.shortshow(getApplicationContext(), "发表成功");
                            CivilizationUpdataInfoActivity.this.finish();
                        } else if(2113 == code){//含敏感词汇
                            ToastUtil.longshow(getApplicationContext(),getString(R.string.sensitive_word));
                        }else {
                            errorCode(code);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                }
            };
            AndroidAsyncHttp.post(CivilizationUpdataInfoActivity.this, ServerAPIConfig.SendCivilizationMassage, stringEntity, "application/json;charset=UTF-8", res);

        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
