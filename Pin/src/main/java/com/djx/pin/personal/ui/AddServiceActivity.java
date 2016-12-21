package com.djx.pin.personal.ui;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.djx.pin.R;
import com.djx.pin.application.PinApplication;
import com.djx.pin.base.OldBaseActivity;
import com.djx.pin.beans.IDTokenInfo;
import com.djx.pin.beans.ServiceEntity;
import com.djx.pin.beans.StaticBean;
import com.djx.pin.serverapiconfig.ServerAPIConfig;
import com.djx.pin.utils.AndroidAsyncHttp;
import com.djx.pin.utils.BitmapUtil;
import com.djx.pin.utils.LogUtil;
import com.djx.pin.utils.QiniuUtils;
import com.djx.pin.utils.ToastUtil;
import com.djx.pin.utils.myutils.Utils;
import com.djx.pin.widget.Image;
import com.loopj.android.http.AsyncHttpResponseHandler;;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;
import cz.msebera.android.httpclient.util.TextUtils;

/**
 * Created by 柯传奇 on 2016/10/21 0021.
 */
public class AddServiceActivity extends OldBaseActivity{


    @Bind(R.id.et_content)
    EditText et_content;
    @Bind(R.id.et_style)
    EditText et_style;
    @Bind(R.id.et_money)
    EditText et_money;
    @Bind(R.id.iv_01)
    ImageView iv_add0;
    @Bind(R.id.iv_02)
    ImageView iv_add01;
    @Bind(R.id.iv_03)
    ImageView iv_add02;
    private int picSize = 0;
    private AddServiceActivity context;
    private String session_id;
    private String[] path_photo = {"","",""};
    private int iv_currentPos;
    private Dialog dialog_AddPic;
    private int curClickId;
    private int requestCode_Gallery_Pic = 200;
    private int requestCode_Camera_Pic = 201;
    private String path_Camera, dir_Camera;
    private TextView tv_complete;
    private int postRequestExcuteNum = 0;
    private ServiceEntity.ServiceBean serviceBean;
    private String uptate_id = null;


    public static String numSlect(String a) {
        String regEx="[^0-9]";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(a);
        return m.replaceAll("").trim();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addservice);
        ButterKnife.bind(this);
        context = AddServiceActivity.this;
        tv_complete = ((TextView) findViewById(R.id.tv_complete));
        session_id = getSharedPreferences(StaticBean.USER_INFO, Context.MODE_PRIVATE).getString("session_id", null);
        Bundle bundle = getIntent().getExtras();
        if(bundle != null){
            serviceBean = ((ServiceEntity.ServiceBean) bundle.getSerializable("serviceBean"));
            if(serviceBean != null){
                uptate_id = serviceBean.id;
                et_content.setText(serviceBean.skill_descr);
                et_style.setText(serviceBean.type);
                et_money.setText(serviceBean.price+"");
                if(serviceBean.media != null){
                    switch (serviceBean.media.size()){
                        case 1:
                            QiniuUtils.setAvatarByIdFrom7Niu(this,iv_add0,serviceBean.media.get(0).getMedia_id());
                            break;
                        case 2:
                            QiniuUtils.setAvatarByIdFrom7Niu(this,iv_add0,serviceBean.media.get(0).getMedia_id());
                            QiniuUtils.setAvatarByIdFrom7Niu(this,iv_add01,serviceBean.media.get(1).getMedia_id());
                            break;
                        case 3:
                            QiniuUtils.setAvatarByIdFrom7Niu(this,iv_add0,serviceBean.media.get(0).getMedia_id());
                            QiniuUtils.setAvatarByIdFrom7Niu(this,iv_add01,serviceBean.media.get(1).getMedia_id());
                            QiniuUtils.setAvatarByIdFrom7Niu(this,iv_add02,serviceBean.media.get(2).getMedia_id());
                            break;
                    }
                }
            }
        }

        initDialog();
    }

    private void initDialog() {
        /**添加图片对话框*/
        View dialogView_AddPicVideo = View.inflate(context, R.layout.layout_dialog_addpicvideo, null);
        Button bt_camera = (Button) dialogView_AddPicVideo.findViewById(R.id.bt_camera);
        Button bt_photoalbum = (Button) dialogView_AddPicVideo.findViewById(R.id.bt_photoalbum);
        Button bt_cancle = (Button) dialogView_AddPicVideo.findViewById(R.id.bt_cancle);
        dialog_AddPic = new Dialog(this, R.style.dialog_transparent);
        dialog_AddPic.setContentView(dialogView_AddPicVideo);
        WindowManager.LayoutParams layoutParams_AddPic = dialog_AddPic.getWindow().getAttributes();
        layoutParams_AddPic.gravity = Gravity.BOTTOM;
        layoutParams_AddPic.width = getWindowManager().getDefaultDisplay().getWidth();
        layoutParams_AddPic.y = 0;
        dialog_AddPic.getWindow().setAttributes(layoutParams_AddPic);

        dir_Camera = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).toString() + "/Camera/";

        bt_photoalbum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                curClickId = R.id.bt_photoalbum;
                startReadSDCardByPermissions();
            }
        });
        bt_camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                curClickId = R.id.bt_camera;
                startTakePhotoByPermissions();
            }
        });
        bt_cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog_AddPic.dismiss();
            }
        });
    }

    /**
     * 选照片
     */
    @Override
    public void startReadSDCard() {
        if (curClickId == R.id.bt_photoalbum) {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
            startActivityForResult(intent, requestCode_Gallery_Pic);
            dialog_AddPic.dismiss();
            curClickId = -1;
        }
    }
    /**
     * 拍照
     */
    @Override
    public void startTakePhoto() {
        LogUtil.e("---------------相机-----------startTakePhoto--------------------");
        if (curClickId == R.id.bt_camera) {//拍照
            File file_Dir = new File(dir_Camera);
            if (file_Dir.exists() == false) {
                file_Dir.mkdirs();
            }
            File file_pic = new File(dir_Camera, String.valueOf(System.currentTimeMillis())
                    + ".jpg");
            path_Camera = file_pic.getPath();
            Uri picUri = Uri.fromFile(file_pic);
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, picUri);
            LogUtil.e("---------------相机-------------------------------");
            startActivityForResult(intent, requestCode_Camera_Pic);
            dialog_AddPic.dismiss();
            curClickId = -1;
        }
    }

    IDTokenInfo idTokenInfo_Pic;
    @OnClick(R.id.iv_back)
    void back(View view){
        finish();
    }
    @OnClick(R.id.tv_complete)
    void complete(View view){//编辑完成

        String s = et_money.getText().toString().trim();
        String style = et_style.getText().toString().trim();
        String content = et_content.getText().toString().trim();

        if(TextUtils.isEmpty(style)){
            ToastUtil.longshow(this,"请输入类型");
            return;
        }
        if(TextUtils.isEmpty(s)){
            ToastUtil.longshow(this,"请输入金额");
            return;
        }
        if(TextUtils.isEmpty(content)){
            ToastUtil.longshow(this,"请输入业务介绍");
            return;
        }
        if (Utils.isFastDoubleClick()) {
            return;
        }
        if(picSize == 0){
            postData(idTokenInfo_Pic);
        }else {
            QiniuUtils.postRequestWithPics(context, picSize, session_id, 1, path_photo, new QiniuUtils.PostRequestWithPics() {
                @Override
                public void postRequest(IDTokenInfo idTokenInfo_Pic) {
                    LogUtil.e(" QiniuUtils.postRequestWithPics   postRequest执行了多少次--------------------------------------");
                    postData(idTokenInfo_Pic);
                }
            });
        }
    }
    @OnClick(R.id.iv_01)//添加图片
    void addPic01(View view){
        iv_currentPos = 0;
        dialog_AddPic.show();
    }
    @OnClick(R.id.iv_02)
    void addPic02(View view){
        iv_currentPos = 1;
        dialog_AddPic.show();
    }
    @OnClick(R.id.iv_03)
    void addPic03(View view){
        iv_currentPos = 2;
        dialog_AddPic.show();
    }



    private void postData(IDTokenInfo tokenIDs) {
        JSONObject jsonObject = null;
        JSONArray jsonArray = null;
        StringEntity stringEntity = null;
        String url = null;
        try {
            jsonObject = new JSONObject();
            if(!TextUtils.isEmpty(uptate_id)){
                jsonObject.put("skill_id", uptate_id);
                url = ServerAPIConfig.UPDATE_SKILL;
            }else {
                url = ServerAPIConfig.ADD_SKILL;
            }
            jsonObject.put("session_id", session_id);
            jsonObject.put("type",et_style.getText().toString().trim());
            jsonObject.put("skill_descr",et_content.getText().toString().trim());
            jsonObject.put("price",et_money.getText().toString().trim());

            jsonArray = new JSONArray();
            if (tokenIDs != null) {//没图片
                for (int i = 0; i < picSize; i++) {
                    JSONObject obj = new JSONObject();
                    if(tokenIDs.list.get(i).id != null && tokenIDs.list.get(i).id.length() > 1){
                        obj.put("id", tokenIDs.list.get(i).id);
                        obj.put("media_type", 1);
                        jsonArray.put(obj);
                    }
                }
            }
            jsonObject.put("media", jsonArray);
            stringEntity = new StringEntity(jsonObject.toString(), "UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (null == jsonArray || null == jsonObject || null == stringEntity) {
            LogUtil.e(context, "数据打包成JSON异常");
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
                        if(!TextUtils.isEmpty(uptate_id)){
                            ToastUtil.longshow(AddServiceActivity.this,"修改成功");
                        }else{
                            ToastUtil.longshow(AddServiceActivity.this,"添加成功");
                        }
                        Intent intent = new Intent();
                        //把返回数据存入Intent
                        AddServiceActivity.this.setResult(RESULT_OK, intent);
                        AddServiceActivity.this.finish();
                    } else if(2113 == obj.getInt("code")){
                        ToastUtil.longshow(getApplicationContext(),getString(R.string.sensitive_word));
                    }else {
                        ToastUtil.shortshow(context, "上传信息异常");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                LogUtil.e(AddServiceActivity.this, "enter onFailure");
            }
        };
        LogUtil.e("----------------url--------------"+url);
        AndroidAsyncHttp.post(this, url, stringEntity, "application/json;charset=UTF-8", res);


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        /**
         * 打开图库获取图片并添加到iv_AddPic上
         *获取图片并显示到对应的imageView上.
         *但是如果前面的imageview没有图片,将优先显示到前面的imageview上
         */
        LogUtil.e("---------------相机2-------------------------------");
        boolean isPicFormat = false;
        BitmapUtil.SizeMessage sizeMessage = new BitmapUtil.SizeMessage(this, false, 60, 60);
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
            switch (iv_currentPos) {
                case 0:
                    path_photo[0] = picturePath;
                    iv_add0.setImageBitmap(BitmapUtil.loadBitmap(picturePath, sizeMessage));
                    picSize++;
                    break;
                case 1:
                    if (null == path_photo[0] || 0 == path_photo[0].length()) {
                        path_photo[0] = picturePath;
                        iv_add0.setImageBitmap(BitmapUtil.loadBitmap(picturePath, sizeMessage));
                    } else {
                        path_photo[1] = picturePath;
                        iv_add01.setImageBitmap(BitmapUtil.loadBitmap(picturePath, sizeMessage));
                    }
                    picSize++;
                    break;
                case 2:
                    if (null == path_photo[0] || 0 == path_photo[0].length()) {
                        path_photo[0] = picturePath;
                        iv_add0.setImageBitmap(BitmapUtil.loadBitmap(picturePath, sizeMessage));
                    } else if (null == path_photo[1] || 0 == path_photo[1].length()) {
                        path_photo[1] = picturePath;
                        iv_add01.setImageBitmap(BitmapUtil.loadBitmap(picturePath, sizeMessage));
                    } else {
                        path_photo[2] = picturePath;
                        iv_add02.setImageBitmap(BitmapUtil.loadBitmap(picturePath, sizeMessage));
                    }
                    picSize++;
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
            switch (iv_currentPos) {
                case 0:
                    path_photo[0] = path_Camera;
                    iv_add0.setImageBitmap(BitmapUtil.loadBitmap(path_Camera, sizeMessage));
                    picSize++;
                    break;
                case 1:
                    if (null == path_photo[0] || 0 == path_photo[0].length()) {
                        path_photo[0] = path_Camera;
                        iv_add0.setImageBitmap(BitmapUtil.loadBitmap(path_Camera, sizeMessage));
                    } else {
                        path_photo[1] = path_Camera;
                        iv_add01.setImageBitmap(BitmapUtil.loadBitmap(path_Camera, sizeMessage));
                    }
                    picSize++;
                    break;
                case 2:
                    if (null == path_photo[0] || 0 == path_photo[0].length()) {
                        path_photo[0] = path_Camera;
                        iv_add0.setImageBitmap(BitmapUtil.loadBitmap(path_Camera, sizeMessage));
                    } else if (null == path_photo[1] || 0 == path_photo[1].length()) {
                        path_photo[1] = path_Camera;
                        iv_add01.setImageBitmap(BitmapUtil.loadBitmap(path_Camera, sizeMessage));
                    } else {
                        path_photo[2] = path_Camera;
                        iv_add02.setImageBitmap(BitmapUtil.loadBitmap(path_Camera, sizeMessage));
                    }
                    picSize++;
                    break;
            }
        }
    }

}
