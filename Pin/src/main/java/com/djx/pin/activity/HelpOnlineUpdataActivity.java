package com.djx.pin.activity;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.widget.GridLayoutManager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.baidu.mapapi.search.sug.OnGetSuggestionResultListener;
import com.baidu.mapapi.search.sug.SuggestionResult;
import com.baidu.mapapi.search.sug.SuggestionSearch;
import com.djx.pin.R;
import com.djx.pin.adapter.SearchPositionAdapter;
import com.djx.pin.improve.helpmap.adapter.AddPicAdapter;
import com.djx.pin.base.OldBaseActivity;
import com.djx.pin.beans.IDTokenInfo;
import com.djx.pin.beans.StaticBean;
import com.djx.pin.serverapiconfig.ServerAPIConfig;
import com.djx.pin.utils.AndroidAsyncHttp;
import com.djx.pin.utils.BitmapUtil;
import com.djx.pin.utils.DateUtils;
import com.djx.pin.utils.LogUtil;
import com.djx.pin.utils.QiniuUtils;
import com.djx.pin.utils.ToastUtil;
import com.djx.pin.widget.MyRecyclerView;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.qiniu.android.storage.UploadManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;
import cz.msebera.android.httpclient.util.TextUtils;

public class HelpOnlineUpdataActivity extends OldBaseActivity implements View.OnClickListener, OnGetSuggestionResultListener, OnGetGeoCoderResultListener{


    ImageView iv_Help,//点击求助按钮
            iv_Back,//dialog返回
            iv_Clear;//清空输入内容;
    GeoCoder mSearch = null; // 搜索模块，也可去掉地图模块独立使用
    Dialog dialog_AddPic, dialog_AddVideo, dialog_ProgressBar, dialog_Date;
    View dialogView_AddPicVideo,
            dialogView_SearchPosition;//搜索位置Dialog布局;
    RadioGroup rg_help_type;
    RadioButton rb_photo, rb_video, rb_other;
    String currentCity = null,//当前城市
            currentProvince = null;//当前省份
    String path_Camera,//使用相机拍照的图片绝对路径
            path_Camera_Video,//使用相机拍摄视屏的绝对路径
            dir_Camera,//使用相机拍照保存图片的文件夹路径
            videoPath;//用户举报的视屏绝对路径
    boolean isStartTime = true;//是否是开始时间
    // 重用 uploadManager。一般地，只需要创建一个 uploadManager 对象
    UploadManager uploadManager;
    TextView //确定时间
            tv_position,
            tv_start_time,
            et_note,
            tv_end_time;
    Button bt_updatainfo;//提交按钮

    Button bt_net_dialog_area;//取消按钮;
    LinearLayout ll_back;
    int requestCode_Gallery_Pic = 0,//打开图库的请求码,也用来标识要将返回的图片添加到iv_AddPicVideo0,iv_AddPicVideo1,iv_AddPicVideo2中的那一个,默认为iv_AddPicVideo0
            requestCode_Camera_Pic = 1;//调用相机拍照的请求码.

    int requestCode_Gallery_Video = 2,//打开图库的请求码,也用来标识要将返回的图片添加到iv_AddPicVideo0,iv_AddPicVideo1,iv_AddPicVideo2中的那一个,默认为iv_AddPicVideo0
            requestCode_Camera_Video = 3;//调用相机拍照的请求码.

    int iv_current_Position = 0;//用来标记用户点击的是哪一个 iv_AddPic.

    IDTokenInfo idTokenInfo_Pic = null;
    IDTokenInfo idTokenInfo_Video = null;
    RelativeLayout rl_searh_position;
    boolean isVideoFormat = false,//判断用户选择的文件是否是视屏格式,默认false不是视频格式
            isCancelled = false;//是否取消当前上传的文件
    boolean[] isOK_Pic = new boolean[3];//判断全部照片是否上传成功
    boolean[] isOK_Video = new boolean[3];//判断全部照片是否上传成功
    Context CONTEXT = HelpOnlineUpdataActivity.this;
    String[] path_Photo = {"", "", ""},
            path_Video = {"", "", ""};
    Intent intent_Gallery_Pic, intent_Camera_Pic;
    Intent intent_Gallery_Video, intent_Camera_Video;
    BitmapUtil.SizeMessage sizeMessage;

    SharedPreferences sp;
    DatePicker dp;//日期选择器
    private Button bt_camera, bt_photoalbum, bt_cancle;
    EditText et_money, et_people_number;
    SuggestionSearch mSuggestionSearch = null;
    List<SuggestionResult.SuggestionInfo> list = null;
    SearchPositionAdapter adapter;
    LayoutInflater inflater;
    //设定需要添加的自定义变量为Map<String, String>类型 并且放到UploadOptions第一个参数里面
    HashMap<String, String> map = new HashMap<String, String>();
    //定位相关
    private boolean isPicFormat = false;//选择的图片格式是否正确
    private int curClickId;
    private int flag;//标志开始还是结束
    private static final int DECIMAL_DIGITS = 1;
    private TextView tv_help_word;
    private MyRecyclerView recyclerView;
    private AddPicAdapter addPicAdapter;
    private List<String> img_paths = new ArrayList<>();//本地图片路径


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_net_help);
        initView();
        initEvent();
        initDialog();
        initGeoCoder();
        initSuggestionSearch();
    }

    private void initView() {
        recyclerView = ((MyRecyclerView) findViewById(R.id.recyclerView));
        recyclerView.setHasFixedSize(true);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 3,GridLayoutManager.VERTICAL,false);
        recyclerView.setLayoutManager(gridLayoutManager);
        addPicAdapter = new AddPicAdapter(this);
        recyclerView.setAdapter(addPicAdapter);
        addPicAdapter.setAddPidListener(new AddPicAdapter.AddPicListener() {
            @Override
            public void addPic() {
                startReadSDCardByPermissions();
            }
        });
        addPicAdapter.setDeletePicListener(new AddPicAdapter.DeletePicListener() {
            @Override
            public void deletePic(int pos) {
                img_paths.remove(pos);
            }
        });
        sp = getSharedPreferences(StaticBean.USER_INFO, MODE_PRIVATE);
        ll_back = (LinearLayout) findViewById(R.id.ll_back);
        inflater = LayoutInflater.from(this);
        rl_searh_position = (RelativeLayout) findViewById(R.id.rl_searh_position);
        tv_position = (TextView) findViewById(R.id.tv_position);
        rg_help_type = (RadioGroup) findViewById(R.id.rg_help_type);
        rb_photo = (RadioButton) findViewById(R.id.rb_photo);
        rb_photo.setChecked(true);
        rb_video = (RadioButton) findViewById(R.id.rb_video);
        rb_other = (RadioButton) findViewById(R.id.rb_other);
        tv_start_time = (TextView) findViewById(R.id.tv_start_time);
        tv_end_time = (TextView) findViewById(R.id.tv_end_time);
        bt_updatainfo = (Button) findViewById(R.id.bt_updatainfo);
        et_money = (EditText) findViewById(R.id.et_money);
        et_people_number = (EditText) findViewById(R.id.et_people_number);
        et_note = (TextView) findViewById(R.id.et_note);
        tv_help_word = (TextView) findViewById(R.id.tv_help_word);
        tv_help_word.setOnClickListener(this);


        tv_start_time.setText(DateUtils.formatDate(new Date(System.currentTimeMillis()), DateUtils.yyyyMMDD));
        tv_end_time.setText(DateUtils.formatDate(new Date(System.currentTimeMillis() + 86400000), DateUtils.yyyyMMDD));

        /**添加图片对话框相关控件*/
        dialogView_AddPicVideo = inflater.inflate(R.layout.layout_dialog_addpicvideo, null);
        bt_camera = (Button) dialogView_AddPicVideo.findViewById(R.id.bt_camera);
        bt_photoalbum = (Button) dialogView_AddPicVideo.findViewById(R.id.bt_photoalbum);
        bt_cancle = (Button) dialogView_AddPicVideo.findViewById(R.id.bt_cancle);


        intent_Gallery_Pic = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent_Gallery_Pic.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        intent_Camera_Pic = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        intent_Gallery_Video = new Intent(Intent.ACTION_PICK, MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
        intent_Gallery_Video.setDataAndType(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, "video/*");
        intent_Camera_Video = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);

        dir_Camera = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).toString() + "/Camera/";
        sizeMessage = new BitmapUtil.SizeMessage(this, false, 60, 60);
        map.put("x:phone", "12345678");


    }

    private void initEvent() {

        bt_camera.setOnClickListener(this);
        bt_photoalbum.setOnClickListener(this);
        bt_cancle.setOnClickListener(this);
        tv_start_time.setOnClickListener(this);
        tv_end_time.setOnClickListener(this);
        bt_updatainfo.setOnClickListener(this);
        ll_back.setOnClickListener(this);
    }



    /**
     * 上传照片信息至本公司服务器
     * <p/>
     * 所有文件都上传至七牛服务器时,这些文件的信息才会上传至本公司的服务器.否则不会上传这些文件的信息至本公司服务器
     */
    public void updataPicInfo2Server(int type,IDTokenInfo idTokenInfo) {

        LogUtil.e("updataPicInfo2Server");
        JSONObject jsonObject = null;
        JSONArray jsonArray = null;
        StringEntity stringEntity = null;
        try {
            jsonObject = new JSONObject();
            jsonArray = new JSONArray();

            jsonObject.put("session_id", sp.getString("session_id", null));
            jsonObject.put("price", Double.parseDouble(et_money.getText().toString()));
            jsonObject.put("description", et_note.getText().toString());
            jsonObject.put("receiver_limit", Integer.parseInt(et_people_number.getText().toString()));
            long start_time = DateUtils.parseDate(tv_start_time.getText().toString(), DateUtils.yyyyMMDD).getTime();
            jsonObject.put("start_time", start_time);
            long time1 = DateUtils.parseDate(tv_end_time.getText().toString(), DateUtils.yyyyMMDD).getTime();
            long time2 = time1 + 86300000;
            jsonObject.put("end_time", time2);
            jsonObject.put("type", type);//类型，1-照片悬赏，2-视频悬赏，3-文字悬赏（其他悬赏）

            if(idTokenInfo != null){
                for (int i = 0; i < idTokenInfo.list.size(); i++) {
                    JSONObject obj = new JSONObject();
                    obj.put("id", idTokenInfo.list.get(i).id);
                    obj.put("media_type", 1);
                    jsonArray.put(obj);
                }
            }
            jsonObject.put("media", jsonArray);
            stringEntity = new StringEntity(jsonObject.toString(), "UTF-8");
        } catch (Exception e) {
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
                LogUtil.e("str_json 支付信息=" + str_json);
                try {
                    JSONObject obj = new JSONObject(str_json);
                    if (obj.getInt("code") == 0) {
                        obj = obj.getJSONObject("result");
                        Bundle bundle = new Bundle();
                        bundle.putString("id", obj.getString("id"));
                        bundle.putInt("type", 7);
                        double amount = obj.getDouble("amount");
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
            }
        };
        AndroidAsyncHttp.post(CONTEXT, ServerAPIConfig.Updata_Help_Net, stringEntity, "application/json;charset=UTF-8", res);


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

                if(!checkUserInfo()){
                    return;
                }
                if(img_paths.size() == 0){
                    switch (rg_help_type.getCheckedRadioButtonId()) {
                        case R.id.rb_photo:
                            updataPicInfo2Server(1,null);//图片
                            break;
                        case R.id.rb_video:
                            updataPicInfo2Server(2,null);//视频
                            break;
                        case R.id.rb_other:
                            updataPicInfo2Server(3,null);//其它
                            break;
                    }
                    return;
                }else {
                    QiniuUtils.postRequestWithIMGS(HelpOnlineUpdataActivity.this, img_paths,getSession_id(), 1, new QiniuUtils.PostRequestWithPics() {
                        @Override
                        public void postRequest(IDTokenInfo idTokenInfo_Pic) {
                            switch (rg_help_type.getCheckedRadioButtonId()) {
                                case R.id.rb_photo:
                                    updataPicInfo2Server(1,idTokenInfo_Pic);//图片
                                    break;
                                case R.id.rb_video:
                                    updataPicInfo2Server(2,idTokenInfo_Pic);//视频
                                    break;
                                case R.id.rb_other:
                                    updataPicInfo2Server(3,idTokenInfo_Pic);//其它
                                    break;
                            }
                        }
                    });
                }
                break;
            //点击开始时间
            case R.id.tv_start_time:
                flag = 0;
                showDialog(1);
                break;
            //点击结束时间
            case R.id.tv_end_time:
                isStartTime = false;
                flag = 1;
                showDialog(1);
                break;
            //点击对话框取消按钮
            case R.id.bt_cancle:
                dialog_AddPic.dismiss();
                break;
            //点击对话框相机
            case R.id.bt_camera:
                curClickId = R.id.bt_camera;
                startTakePhotoByPermissions();
                break;
            //点击对话框相册
            case R.id.bt_photoalbum:
                curClickId = R.id.bt_photoalbum;
                startReadSDCardByPermissions();
                break;
            //点击对话框中拍摄视屏按钮
            case R.id.bt_camera_video:
                curClickId = R.id.bt_camera_video;
                startTakePhotoByPermissions();
                break;

            //点击相册选择视屏按钮
            case R.id.bt_photoalbum_video:
                curClickId = R.id.bt_photoalbum_video;
                startReadSDCardByPermissions();
                break;
            //点击对话框中的取消按钮
            case R.id.bt_cancle_video:
                dialog_AddVideo.dismiss();
                break;

            case R.id.tv_help_word:
                Intent intent = new Intent(this,TextActivity.class);
                Bundle bundle = new Bundle();
                bundle.putInt("TextContent",7);
                bundle.putString("url","http://www.dujoy.cn/app/step-reward/index.html");
                intent.putExtras(bundle);
                startActivity(intent);
                break;

        }
    }

    /**
     * 检查用户输入的信息是否正确,正确返回true,反之false
     *
     * @return
     */
    private boolean checkUserInfo() {
        //检查金额是否为空
        String money = et_money.getText().toString().trim();
        if (TextUtils.isEmpty(money)) {
            ToastUtil.shortshow(CONTEXT, R.string.toast_et_monery_err);
            return false;
        }
        //检查需要人手是否为空
        String people = et_people_number.getText().toString().trim();
        if (TextUtils.isEmpty(people)) {
            ToastUtil.shortshow(CONTEXT, R.string.toast_non_et_people_number);
            return false;
        }
        return true;
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
                DatePickerDialog datePickerDialog = new DatePickerDialog(this,onDateSetListener, v_year, v_month, v_day);
                return datePickerDialog;
        }
        return null;
    }

    @Override
    public void startTakePhoto() {
        if (curClickId == R.id.bt_camera) {
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
            curClickId = -1;
        } else if (curClickId == R.id.bt_camera_video) {
            File file_Dir_Video = new File(dir_Camera);
            if (file_Dir_Video.exists() == false) {
                file_Dir_Video.mkdirs();
            }
            File file = new File(dir_Camera, String.valueOf(System.currentTimeMillis())
                    + ".mp4");
            path_Camera_Video = file.getPath();
            Uri videoUri = Uri.fromFile(file);
            intent_Camera_Video.putExtra(MediaStore.EXTRA_OUTPUT, videoUri);
            startActivityForResult(intent_Camera_Video, requestCode_Camera_Video);
            dialog_AddVideo.dismiss();
            curClickId = -1;
        }
    }

    @Override
    public void startReadSDCard() {
        startActivityForResult(intent_Gallery_Pic, requestCode_Gallery_Pic);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == requestCode_Gallery_Pic && resultCode == Activity.RESULT_OK && null != data) {
            String picturePath = getPath(this, data.getData());
            img_paths.add(picturePath);
            addPicAdapter.addData(picturePath);
        }
        if (requestCode == requestCode_Camera_Pic && resultCode == Activity.RESULT_OK) {

        }
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
        mSearch.setOnGetGeoCodeResultListener(this);
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
        currentProvince = result.getAddressDetail().province;
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

    /**
     * 用来判断显示图片对话框还是视频对话框
     */
    public void showDialog() {
        dialog_AddPic.show();

    }

}
