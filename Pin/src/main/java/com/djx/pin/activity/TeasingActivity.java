package com.djx.pin.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.djx.pin.R;
import com.djx.pin.base.OldBaseActivity;
import com.djx.pin.beans.IDTokenInfo;
import com.djx.pin.beans.StaticBean;
import com.djx.pin.serverapiconfig.ServerAPIConfig;
import com.djx.pin.utils.AndroidAsyncHttp;
import com.djx.pin.utils.BitmapUtil;
import com.djx.pin.utils.LogUtil;
import com.djx.pin.utils.ToastUtil;
import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.qiniu.android.http.ResponseInfo;
import com.qiniu.android.storage.UpCancellationSignal;
import com.qiniu.android.storage.UpCompletionHandler;
import com.qiniu.android.storage.UpProgressHandler;
import com.qiniu.android.storage.UploadManager;
import com.qiniu.android.storage.UploadOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;

/**
 * Created by Administrator on 2016/6/29.
 */
public class TeasingActivity extends OldBaseActivity implements View.OnClickListener {

    private LinearLayout ll_Back_TA,ll_SentMassage_NVA;
    private EditText et_PutMassage_TA;
    private int size_Pic = 0;
    private IDTokenInfo idTokenInfo_Pic = null;
    private String[] path_Photo = {"", "", "",""};
    private SharedPreferences sp;
    private Context CONTEXT;
    boolean[] isOK_Pic = new boolean[4];//判断全部照片是否上传成功
    UploadManager uploadManager;
    //设定需要添加的自定义变量为Map<String, String>类型 并且放到UploadOptions第一个参数里面
    HashMap<String, String> map = new HashMap<String, String>();
    private String teasingMassage;//吐槽的内容
    private ImageView iv_AddPicVideo0,iv_AddPicVideo1,iv_AddPicVideo2,iv_AddPicVideo3;
    private int iv_current_Position;
    private Dialog dialog_AddPic;
    private View dialogView_AddPicVideo;
    private Button bt_camera;
    private Button bt_photoalbum;
    private Button bt_cancle;
    private int curClickId;
    private int requestCode_Gallery_Pic = 100;
    private int requestCode_Camera_Pic = 200;

    private boolean isPicFormat = false;//选择的图片格式是否正确
    private BitmapUtil.SizeMessage sizeMessage;
    private String path_Camera,//使用相机拍照的图片绝对路径
            path_Camera_Video,//使用相机拍摄视屏的绝对路径
            dir_Camera,//使用相机拍照保存图片的文件夹路径
            videoPath;//用户举报的视屏绝对路径
    private Intent intent_Gallery_Pic;
    private Intent intent_Camera_Pic;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teasing);

        sp = getSharedPreferences(StaticBean.USER_INFO, MODE_PRIVATE);
        map.put("x:phone", "12345678");//?

        CONTEXT = TeasingActivity.this;
        initView();

        initEvent();

        initDialog();

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
    }

    private void initEvent() {
        ll_Back_TA.setOnClickListener(this);
        ll_SentMassage_NVA.setOnClickListener(this);
        iv_AddPicVideo0.setOnClickListener(this);
        iv_AddPicVideo1.setOnClickListener(this);
        iv_AddPicVideo2.setOnClickListener(this);
        iv_AddPicVideo3.setOnClickListener(this);

        bt_camera.setOnClickListener(this);
        bt_cancle.setOnClickListener(this);
        bt_photoalbum.setOnClickListener(this);
    }

    private void initView() {
        ll_Back_TA= (LinearLayout) findViewById(R.id.ll_Back_TA);
        ll_SentMassage_NVA= (LinearLayout) findViewById(R.id.ll_SentMassage_NVA);
        et_PutMassage_TA= (EditText) findViewById(R.id.et_PutMassage_TA);
        iv_AddPicVideo0 = ((ImageView) findViewById(R.id.iv_add0));
        iv_AddPicVideo1 = ((ImageView) findViewById(R.id.iv_add1));
        iv_AddPicVideo2 = ((ImageView) findViewById(R.id.iv_add2));
        iv_AddPicVideo3 = ((ImageView) findViewById(R.id.iv_add3));

        /**添加图片对话框相关控件*/
        dialogView_AddPicVideo = View.inflate(CONTEXT,R.layout.layout_dialog_addpicvideo, null);

        bt_camera = (Button) dialogView_AddPicVideo.findViewById(R.id.bt_camera);
        bt_photoalbum = (Button) dialogView_AddPicVideo.findViewById(R.id.bt_photoalbum);
        bt_cancle = (Button) dialogView_AddPicVideo.findViewById(R.id.bt_cancle);

        sizeMessage = new BitmapUtil.SizeMessage(this, false, 60, 60);

        intent_Gallery_Pic = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent_Gallery_Pic.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        intent_Camera_Pic = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.ll_Back_TA:
                this.finish();
                break;
            //点击添加图片按钮
            case R.id.iv_add0:
                iv_current_Position = 0;
                showDialog();
                break;
            //点击添加图片按钮
            case R.id.iv_add1:
                iv_current_Position = 1;
                showDialog();
                break;
            //点击添加图片按钮
            case R.id.iv_add2:
                iv_current_Position = 2;
                showDialog();
                break;
            //点击添加图片按钮
            case R.id.iv_add3:
                iv_current_Position = 3;
                showDialog();
                break;

            case R.id.ll_SentMassage_NVA://发送按钮
                teasingMassage=et_PutMassage_TA.getText().toString().trim();
                if (teasingMassage.equals("")&&teasingMassage.length()==0){
                    ToastUtil.shortshow(this,"吐槽内容不能为空");
                }else {
                    getIdToken_Pic();
                    finish();
                }
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
        }
    }


    /**
     * 拍照
     */
    @Override
    public void startTakePhoto() {
        if (curClickId == R.id.bt_camera) {//拍照
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
        }
    }

    /**
     * 选照片
     */
    @Override
    public void startReadSDCard() {
        if (curClickId == R.id.bt_photoalbum) {
            startActivityForResult(intent_Gallery_Pic, requestCode_Gallery_Pic);
            dialog_AddPic.dismiss();
            curClickId = -1;
        }
    }








    /**
     * 1.获取图片需要的Token,并上传信息至七牛和公司服务器
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
            updataPicInfo2Server(1);
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
                        LogUtil.e(TeasingActivity.this, "服务器返回结果异常");
                        return;
                    }
                    idTokenInfo_Pic = new IDTokenInfo();
                    obj = obj.getJSONObject("result");
                    Gson gson = new Gson();
                    idTokenInfo_Pic = gson.fromJson(obj.toString(), IDTokenInfo.class);
                    //isCancelled = false;
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
     * 2.上传照片至七牛服务器,
     * 由于视屏 照片上传是多线程,不能确定哪一个最后上传完成,
     * 所以当一个文件上传成功后调用updataPicInfo2Server()函数尝试上传至公司服务器.
     * 在updataPicInfo2Server()函数中已经实现了检查是否所有文件都上传至七牛服务器,
     * 只有在所有文件都上传至七牛服务器时才会上传这些信息至公司服务器,且只会上传一次
     */
    public void updataPic2Qiniu() {//idTokenInfo_Pic
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
        if(null == idTokenInfo_Pic.list){
            return;
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
                                updataPicInfo2Server(1);
                            } else {
                                isOK_Pic[k] = false;
                                ToastUtil.shortshow(CONTEXT, R.string.toast_updata_failer);
                            }
                        }
                    }
                }
            }, new UploadOptions(map, null, false, new UpProgressHandler() {
                @Override
                public void progress(String key, double percent) {

                }
            }, new UpCancellationSignal() {
                @Override
                public boolean isCancelled() {
                    return false;
                }
            }));
        }
    }





    /**
     * 3.上传照片信息至本公司服务器
     * <p/>
     * 所有文件都上传至七牛服务器时,这些文件的信息才会上传至本公司的服务器.否则不会上传这些文件的信息至本公司服务器
     */
    public void updataPicInfo2Server(int type) {

        LogUtil.e("updataPicInfo2Server");

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

            jsonObject.put("session_id", sp.getString("session_id", null));
            jsonObject.put("content", teasingMassage);

            jsonArray = new JSONArray();
            for (int i = 0; i < size_Pic; i++) {
                JSONObject obj = new JSONObject();
                obj.put("id", idTokenInfo_Pic.list.get(i).id);
                obj.put("media_type", 1);
                jsonArray.put(obj);
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
                LogUtil.e("str_json=" + str_json);
                try {
                    JSONObject obj = new JSONObject(str_json);
                    if (obj.getInt("code") == 0) {
                        ToastUtil.shortshow(getApplicationContext(),"发送成功，感谢您对我们平台的吐槽");
                    } else if(2113 == obj.getInt("code")){
                        ToastUtil.longshow(getApplicationContext(),getString(R.string.sensitive_word));
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
        AndroidAsyncHttp.post(CONTEXT, ServerAPIConfig.UPdata_Teasing, stringEntity, "application/json;charset=UTF-8", res);
    }





    /**
     * 用来判断显示图片对话框还是视频对话框
     */
    public void showDialog() {



        dialog_AddPic.show();

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
                    iv_AddPicVideo0.setImageBitmap(BitmapUtil.loadBitmap(picturePath, sizeMessage));
                    break;
                case 1:
                    if (null == path_Photo[0] || 0 == path_Photo[0].length()) {
                        path_Photo[0] = picturePath;
                        iv_AddPicVideo0.setImageBitmap(BitmapUtil.loadBitmap(picturePath, sizeMessage));
                    } else {
                        path_Photo[1] = picturePath;
                        iv_AddPicVideo1.setImageBitmap(BitmapUtil.loadBitmap(picturePath, sizeMessage));
                    }
                    break;
                case 2:
                    if (null == path_Photo[0] || 0 == path_Photo[0].length()) {
                        path_Photo[0] = picturePath;
                        iv_AddPicVideo0.setImageBitmap(BitmapUtil.loadBitmap(picturePath, sizeMessage));
                    } else if (null == path_Photo[1] || 0 == path_Photo[1].length()) {
                        path_Photo[1] = picturePath;
                        iv_AddPicVideo1.setImageBitmap(BitmapUtil.loadBitmap(picturePath, sizeMessage));
                    } else {
                        path_Photo[2] = picturePath;
                        iv_AddPicVideo2.setImageBitmap(BitmapUtil.loadBitmap(picturePath, sizeMessage));
                    }
                    break;
                case 3:
                    if (null == path_Photo[0] || 0 == path_Photo[0].length()) {
                        path_Photo[0] = picturePath;
                        iv_AddPicVideo0.setImageBitmap(BitmapUtil.loadBitmap(picturePath, sizeMessage));
                    } else if (null == path_Photo[1] || 0 == path_Photo[1].length()) {
                        path_Photo[1] = picturePath;
                        iv_AddPicVideo1.setImageBitmap(BitmapUtil.loadBitmap(picturePath, sizeMessage));
                    } else if (null == path_Photo[2] || 0 == path_Photo[2].length()) {
                        path_Photo[2] = picturePath;
                        iv_AddPicVideo2.setImageBitmap(BitmapUtil.loadBitmap(picturePath, sizeMessage));
                    } else {
                        path_Photo[3] = picturePath;
                        iv_AddPicVideo3.setImageBitmap(BitmapUtil.loadBitmap(picturePath, sizeMessage));
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
                    iv_AddPicVideo0.setImageBitmap(BitmapUtil.loadBitmap(path_Camera, sizeMessage));
                    break;
                case 1:
                    if (null == path_Photo[0] || 0 == path_Photo[0].length()) {
                        path_Photo[0] = path_Camera;
                        iv_AddPicVideo0.setImageBitmap(BitmapUtil.loadBitmap(path_Camera, sizeMessage));
                    } else {
                        path_Photo[1] = path_Camera;
                        iv_AddPicVideo1.setImageBitmap(BitmapUtil.loadBitmap(path_Camera, sizeMessage));
                    }
                    break;
                case 2:
                    if (null == path_Photo[0] || 0 == path_Photo[0].length()) {
                        path_Photo[0] = path_Camera;
                        iv_AddPicVideo0.setImageBitmap(BitmapUtil.loadBitmap(path_Camera, sizeMessage));
                    } else if (null == path_Photo[1] || 0 == path_Photo[1].length()) {
                        path_Photo[1] = path_Camera;
                        iv_AddPicVideo1.setImageBitmap(BitmapUtil.loadBitmap(path_Camera, sizeMessage));
                    } else {
                        path_Photo[2] = path_Camera;
                        iv_AddPicVideo2.setImageBitmap(BitmapUtil.loadBitmap(path_Camera, sizeMessage));
                    }
                    break;

                case 3:
                    if (null == path_Photo[0] || 0 == path_Photo[0].length()) {
                        path_Photo[0] = path_Camera;
                        iv_AddPicVideo0.setImageBitmap(BitmapUtil.loadBitmap(path_Camera, sizeMessage));
                    } else if (null == path_Photo[1] || 0 == path_Photo[1].length()) {
                        path_Photo[1] = path_Camera;
                        iv_AddPicVideo1.setImageBitmap(BitmapUtil.loadBitmap(path_Camera, sizeMessage));
                    } else if (null == path_Photo[2] || 0 == path_Photo[2].length()) {
                        path_Photo[2] = path_Camera;
                        iv_AddPicVideo2.setImageBitmap(BitmapUtil.loadBitmap(path_Camera, sizeMessage));
                    }else {
                        path_Photo[3] = path_Camera;
                        iv_AddPicVideo3.setImageBitmap(BitmapUtil.loadBitmap(path_Camera, sizeMessage));
                    }
                    break;
            }
        }
    }

}
