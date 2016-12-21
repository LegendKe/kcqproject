package com.djx.pin.activity;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.djx.pin.base.OldBaseActivity;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.gson.Gson;
import com.djx.pin.R;
import com.djx.pin.beans.IDTokenInfo;
import com.djx.pin.beans.StaticBean;
import com.djx.pin.serverapiconfig.ServerAPIConfig;
import com.djx.pin.utils.AndroidAsyncHttp;
import com.djx.pin.utils.BitmapUtil;
import com.djx.pin.utils.DateUtils;
import com.djx.pin.utils.LogUtil;
import com.djx.pin.utils.ToastUtil;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.qiniu.android.http.ResponseInfo;
import com.qiniu.android.storage.UpCompletionHandler;
import com.qiniu.android.storage.UploadManager;

import cz.msebera.android.httpclient.entity.StringEntity;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONArray;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

import cz.msebera.android.httpclient.Header;

public class GoHomeUpdataInfoActivity extends OldBaseActivity implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {
    LinearLayout ll_back,
            ll_guest;//匿名隐藏该视图,实名显示该视图
    CheckBox cb_guest;//实名/匿名按钮
    Button bt_updatainfo;//提交按钮
    TextView
            tv_user_name,//举报人姓名
            tv_user_phone,//举报人联系方式
            tv_user_ID,//举报人省份证
            tv_shooting_time,//违法时间
            tv_date_confirm;//确定时间
    EditText
            et_position,//举报点
            et_note;//备注信息
    DatePicker dp;//日期选择器
    ImageView iv_AddPic0, iv_AddPic1, iv_AddPic2;
    Dialog dialog_AddPic, dialog_Date;
    View dialogView_AddPicVideo, dialogView_Date;
    LayoutInflater inflater;
    private Button bt_camera, bt_photoalbum, bt_cancle;

    boolean isCancelled = false;//是否取消当前上传的文件
    ProgressBar pb;
    String[] path_Photo = {"", "", ""};
    String path_Camera,//使用相机拍照的图片绝对路径
            dir_Camera,//使用相机拍照保存图片的文件夹路径
            videoPath;//用户举报的视屏绝对路径


    BitmapUtil.SizeMessage sizeMessage;

    Intent intent_Gallery_Pic, intent_Camera_Pic;

    int requestCode_Gallery_Pic = 0,//打开图库的请求码,也用来标识要将返回的图片添加到iv_AddPic0,iv_AddPic1,iv_AddPic2中的那一个,默认为iv_AddPic0
            requestCode_Camera_Pic = 1;//调用相机拍照的请求码.

    int iv_current_Position = 0;//用来标记用户点击的是哪一个 iv_AddPic.

    private boolean isPicFormat = false;//选择的图片格式是否正确

    IDTokenInfo idTokenInfo_Pic = null;
    IDTokenInfo idTokenInfo_Video = null;
    Context CONTEXT = GoHomeUpdataInfoActivity.this;

    SharedPreferences sp;

    int size_Pic = 0;
    // 重用 uploadManager。一般地，只需要创建一个 uploadManager 对象
    UploadManager uploadManager;
    boolean[] isOK_Pic = new boolean[3];//判断全部照片是否上传成功
    //设定需要添加的自定义变量为Map<String, String>类型 并且放到UploadOptions第一个参数里面
    HashMap<String, String> map = new HashMap<String, String>();
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gohomeupdatainfo);
        initView();
        initEvent();
        initDialog();
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

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
    }


    private void initView() {
        inflater = LayoutInflater.from(this);
        ll_back = (LinearLayout) findViewById(R.id.ll_back);
        ll_guest = (LinearLayout) findViewById(R.id.ll_guest);
        cb_guest = (CheckBox) findViewById(R.id.cb_guest);
        bt_updatainfo = (Button) findViewById(R.id.bt_updatainfo);

        tv_user_name = (TextView) findViewById(R.id.tv_user_name);
        tv_user_phone = (TextView) findViewById(R.id.tv_user_phone);
        tv_user_ID = (TextView) findViewById(R.id.tv_user_ID);
        tv_shooting_time = (TextView) findViewById(R.id.tv_shooting_time);
        et_position = (EditText) findViewById(R.id.et_position);
        et_note = (EditText) findViewById(R.id.et_note);

        iv_AddPic0 = (ImageView) findViewById(R.id.iv_AddPic0);
        iv_AddPic1 = (ImageView) findViewById(R.id.iv_AddPic1);
        iv_AddPic2 = (ImageView) findViewById(R.id.iv_AddPic2);

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
        map.put("x:phone", "12345678");


        sp = getSharedPreferences(StaticBean.USER_INFO, MODE_PRIVATE);

        tv_user_phone.setText(sp.getString("mobile", "获取用户手机号异常"));
        tv_user_name.setText(sp.getString("real_name", "获取用户名异常"));
        tv_user_ID.setText(sp.getString("id_card", "获取用户身份证异常"));

    }

    private void initEvent() {
        ll_back.setOnClickListener(this);
        cb_guest.setOnCheckedChangeListener(this);
        cb_guest.setChecked(true);
        bt_updatainfo.setOnClickListener(this);

        iv_AddPic0.setOnClickListener(this);
        iv_AddPic1.setOnClickListener(this);
        iv_AddPic2.setOnClickListener(this);

        bt_camera.setOnClickListener(this);
        bt_photoalbum.setOnClickListener(this);
        bt_cancle.setOnClickListener(this);

        tv_date_confirm.setOnClickListener(this);
        tv_shooting_time.setOnClickListener(this);

    }



    //监听器，用户监听用户点下DatePikerDialog的set按钮时，所设置的年月日
    DatePickerDialog.OnDateSetListener onDateSetListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            tv_shooting_time.setText(year + "-" + (monthOfYear+1) + "-" + dayOfMonth);
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
                DatePickerDialog datePickerDialog = new DatePickerDialog(this,onDateSetListener, v_year, v_month, v_day);
                return datePickerDialog;
        }
        return null;
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            //点击选择违法时间
            case R.id.tv_shooting_time:
                showDialog(1);
                break;



            case R.id.tv_date_confirm:
                tv_shooting_time.setText(dp.getYear() + "-" + (dp.getMonth()+1) + "-" + dp.getDayOfMonth());
                dialog_Date.dismiss();
                break;
            //点击返回按钮
            case R.id.ll_back:
                finish();
                break;
            //点击提交按钮
            case R.id.bt_updatainfo:
                LogUtil.e("lenght="+path_Photo[0].length());
                //检查用户输入的信息是否正确,不正确则return;
                if (!checkUserInfo()) {
                    return;
                }
                getIdToken_Pic();
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
                break;
            //点击对话框相机
            case R.id.bt_camera:
                startTakePhotoByPermissions();
                dialog_AddPic.dismiss();
                break;
            //点击对话框相册
            case R.id.bt_photoalbum:
                startReadSDCardByPermissions();
                dialog_AddPic.dismiss();
                break;
        }
    }

    @Override
    public void startReadSDCard() {
        startActivityForResult(intent_Gallery_Pic, requestCode_Gallery_Pic);
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

    /**
     * 所有文件都上传至七牛服务器时,将这些文件的信息上传至本公司的服务器.
     * 视屏文件上传中或上传失败,图片上传中或上传失败 则返回不再上传至公司服务器
     */
    private void updataInfo2Server() {
        for (int i = 0; i < size_Pic; i++) {
            if (!isOK_Pic[i]) {
                return;
            }
        }
        //打包成json字符串
        JSONObject obj = new JSONObject();
        JSONArray jsonArray = new JSONArray();
        try {
            obj.put("session_id", sp.getString("session_id", ""));
            if (cb_guest.isChecked()) {
                obj.put("is_real", 0);//实名举报
            } else {
                obj.put("is_real", 1);//匿名举报
            }
            obj.put("event_time", DateUtils.parseDate(tv_shooting_time.getText().toString(), DateUtils.yyyyMMDD).getTime());
            obj.put("location", et_position.getText().toString());
            obj.put("description", et_note.getText().toString() + "");
            //打包图片到json里
            for (int i = 0; i < size_Pic; i++) {
                JSONObject obj_Pic = new JSONObject();
                obj_Pic.put("id", idTokenInfo_Pic.list.get(i).id);
                obj_Pic.put("media_type", 1);
                jsonArray.put(obj_Pic);
            }
            obj.put("media", jsonArray);
        } catch (JSONException e) {
            LogUtil.e(this, "enter catch:json打包异常");
            e.printStackTrace();
        }

        //发送到服务器
        try {
            StringEntity stringEntity = new StringEntity(obj.toString(), "utf-8");
            AndroidAsyncHttp.post(this, ServerAPIConfig.Updata_GoHome, stringEntity, "application/json;charset=UTF-8", new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int i, Header[] headers, byte[] bytes) {
                    String str_json = new String(bytes);
                    try {
                        JSONObject obj = new JSONObject(str_json);
                        if (0 == obj.getInt("code")) {
                            startActivity(new Intent(GoHomeUpdataInfoActivity.this, GoHomeUpdataSuccessActivity.class));
                            GoHomeUpdataInfoActivity.this.finish();
                        }else if(2113 == obj.getInt("code")){
                            ToastUtil.longshow(getApplicationContext(),getString(R.string.sensitive_word));
                        } else {
                            errorCode(obj.getInt("code"));
                        }
                    } catch (JSONException e) {
                        LogUtil.e(GoHomeUpdataInfoActivity.this, "enter catch");
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                    LogUtil.e(GoHomeUpdataInfoActivity.this, "网络连接异常");
                    ToastUtil.shortshow(GoHomeUpdataInfoActivity.this, R.string.toast_error_net);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            LogUtil.e("enter cathc:发送到服务器异常");
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
        //如果没有图片则不获取图片id和token
        if (size_Pic == 0) {
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
     * 上传照片至七牛服务器,
     * 由于视屏 照片上传是多线程,不能确定哪一个最后上传完成,
     * 所以当一个文件上传成功后调用updataInfo()函数尝试上传至公司服务器.
     * 在updataInfo()函数中已经实现了检查是否所有文件都上传至七牛服务器,
     * 只有在所有文件都上传至七牛服务器时才会上传这些信息至公司服务器,且只会上传一次
     */
    public void updataPic2Qiniu() {
        if (null == idTokenInfo_Pic)
            return;
        //重置所有照片的是否上传成功的状态为false
        for (int i=0;i<isOK_Pic.length;i++) {
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
                                updataInfo2Server();
                            } else {
                                isOK_Pic[k] = false;
                                ToastUtil.shortshow(CONTEXT,R.string.toast_updata_failer);
                            }
                        }

                    }
                }
            }, null);
        }
    }


    //检查用户输入的信息是否都正确,全部正确返回true,否则返回false
    public boolean checkUserInfo() {
        //检查时间是否为空
        if (null == tv_shooting_time.getText() || tv_shooting_time.getText().toString().length() == 0) {
            ToastUtil.shortshow(this, R.string.toast_non_car_time);
            return false;
        }
        //检查时间是否超过今天
        if (DateUtils.parseDate(tv_shooting_time.getText().toString(), DateUtils.yyyyMMDD).getTime() - System.currentTimeMillis() > 0) {
            ToastUtil.shortshow(this, R.string.toast_error_car_time);
            return false;
        }

        //检查举报地点是否为空
        if (null == et_position.getText() || et_position.getText().toString().length() == 0) {
            ToastUtil.shortshow(this, R.string.toast_non_position);
            return false;
        }
        //检查至少选择一张图片
        if (null == path_Photo[0] || path_Photo[0].length() == 0) {
            LogUtil.e("11111111111111");
            ToastUtil.shortshow(this, R.string.toast_one_pic);
            return false;
        }

        return true;
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (isChecked) {
            tv_user_phone.setText(sp.getString("mobile", "获取用户手机号异常"));
            tv_user_name.setText(sp.getString("real_name", "获取用户名异常"));
            tv_user_ID.setText(sp.getString("id_card", "获取用户身份证异常"));
            cb_guest.setText("匿名");
            ll_guest.setVisibility(View.VISIBLE);

        } else {
            cb_guest.setText("实名");
            ll_guest.setVisibility(View.GONE);
        }
    }


    /**
     * 用来获取打开图库时的路径,该方法使用Android4.4以下和以上的.
     *
     * @param uri 需要传入图库返回时的Rri.
     */
    public static String getPath(final Context context, final Uri uri) {

        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {

                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{
                        split[1]
                };

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {

            // Return the remote address
            if (isGooglePhotosUri(uri))
                return uri.getLastPathSegment();

            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }

    /**
     * Get the value of the data column for this Uri. This is useful for
     * MediaStore Uris, and other file-based ContentProviders.
     *
     * @param context       The context.
     * @param uri           The Uri to query.
     * @param selection     (Optional) Filter used in the query.
     * @param selectionArgs (Optional) Selection arguments used in the query.
     * @return The value of the _data column, which is typically a file path.
     */
    public static String getDataColumn(Context context, Uri uri, String selection,
                                       String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {
                column
        };

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }


    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is Google Photos.
     */
    public static boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri.getAuthority());
    }

}


