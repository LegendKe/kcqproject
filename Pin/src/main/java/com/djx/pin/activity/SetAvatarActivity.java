package com.djx.pin.activity;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.djx.pin.personal.ui.PersonalDataActivity;
import com.djx.pin.utils.QiniuUtils;
import com.google.gson.Gson;
import com.djx.pin.R;
import com.djx.pin.base.OldBaseActivity;
import com.djx.pin.beans.IDTokenInfo;
import com.djx.pin.beans.StaticBean;
import com.djx.pin.myview.CircleImageView;
import com.djx.pin.serverapiconfig.ServerAPIConfig;
import com.djx.pin.utils.AndroidAsyncHttp;
import com.djx.pin.utils.LogUtil;
import com.djx.pin.utils.ToastUtil;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.qiniu.android.http.ResponseInfo;
import com.qiniu.android.storage.UpCompletionHandler;
import com.qiniu.android.storage.UploadManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;

import cz.msebera.android.httpclient.Header;

/**
 * Created by Administrator on 2016/6/12.
 */
public class SetAvatarActivity extends OldBaseActivity implements View.OnClickListener {

    private LinearLayout ll_SAA_Back, ll_SAA_Finish;
    private TextView tv_SAA_Camera, tv_SAA_Photo;
    private CircleImageView cimg_SAA_Avatar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setavatar);
        iniView();
        initEvent();
        String portrait = getSharedPreferences(StaticBean.USER_INFO, Context.MODE_PRIVATE).getString("portrait", null);
        if (!portrait.equals("") || portrait != null) {
            QiniuUtils.setAvatarByIdFrom7Niu(this,cimg_SAA_Avatar,portrait);
        }

    }

    private void iniView() {
        ll_SAA_Back = (LinearLayout) findViewById(R.id.ll_SAA_Back);
        ll_SAA_Finish = (LinearLayout) findViewById(R.id.ll_SAA_Finish);
        tv_SAA_Camera = (TextView) findViewById(R.id.tv_SAA_Camera);
        tv_SAA_Photo = (TextView) findViewById(R.id.tv_SAA_Photo);
        cimg_SAA_Avatar = (CircleImageView) findViewById(R.id.cimg_SAA_Avatar);
    }

    private void initEvent() {
        ll_SAA_Back.setOnClickListener(this);
        ll_SAA_Finish.setOnClickListener(this);
        tv_SAA_Camera.setOnClickListener(this);
        tv_SAA_Photo.setOnClickListener(this);
    }

    Intent intent;
    private File tempFile = new File(Environment.getExternalStorageDirectory(), getPhotoFileName());

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.ll_SAA_Back:
                getSharedPreferences(StaticBean.KEY, Context.MODE_PRIVATE).edit().putString("key", "").commit();
                startActivity(PersonalDataActivity.class);
                this.finish();
                break;
            case R.id.ll_SAA_Finish:
                getQiniu();
                break;
            case R.id.tv_SAA_Camera:
                startTakePhotoByPermissions();
                break;
            case R.id.tv_SAA_Photo:
                startReadSDCardByPermissions();
                break;
        }
    }

    //子类重写该方法
    @Override
    public void startTakePhoto() {
        intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // 指定调用相机拍照后照片的储存路径
        intent.putExtra(MediaStore.EXTRA_OUTPUT,
                Uri.fromFile(tempFile));
        startActivityForResult(intent, 1);

        ToastUtil.shortshow(this, "正在打开相机");
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

    String picPath;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        picPath = null;
        if (resultCode == RESULT_OK) {
            if (requestCode == 0) {
                picPath = getPath(getApplicationContext(), data.getData());
                ToastUtil.shortshow(this, picPath.toString());
                ContentResolver contentResolver = getContentResolver();
                try {
                    Bitmap bitmap = BitmapFactory.decodeStream(contentResolver.openInputStream(data.getData()));
                    cimg_SAA_Avatar.setImageBitmap(bitmap);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }else if (requestCode==1){
                Uri uri=Uri.fromFile(tempFile);
                picPath=getPath(getApplicationContext(),uri);
                try {

                    Bitmap bitmap=BitmapFactory.decodeStream(getContentResolver().openInputStream(uri));
                    cimg_SAA_Avatar.setImageBitmap(bitmap);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 获取七牛上传id/token
     * 参数：session_id String 用户会话id
     * size Number 需要id/token的数量
     * media_type Number 资源类型，1-图片，2-视频
     */
    //图片数量
    int size = 1;
    String session_id;
    int media_type = 1;
    IDTokenInfo idTokenInfo;


    public void getQiniu() {

        LogUtil.e("进入getQiniu");
        session_id = getSharedPreferences(StaticBean.USER_INFO, Context.MODE_PRIVATE).getString("session_id", null);
        if (session_id == null || session_id.equals("")) {
            ToastUtil.shortshow(getApplicationContext(), "请先登录应用");
            return;
        } else {
            RequestParams params = new RequestParams();
            params.put("session_id", session_id);
            params.put("size", size);
            params.put("media_type", media_type);

            AsyncHttpResponseHandler upHandler = new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int i, Header[] headers, byte[] bytes) {
                    String str_json = new String(bytes);
                    LogUtil.e("str_json=" + str_json);
                    try {
                        JSONObject obj = new JSONObject(str_json);
                        if (obj.getInt("code") == 0) {
                            Gson gson = new Gson();
                            idTokenInfo = gson.fromJson(obj.getString("result").toString(), IDTokenInfo.class);
                            for (int j = 0; j < size; j++) {
                                LogUtil.e("222222222=" + idTokenInfo.list.get(j).id + "=j=" + j);
                            }
                            LogUtil.e("11111111111111111111");
                            if (picPath==null){
                                ToastUtil.shortshow(getApplicationContext(),"头像并没有改变");
                            }else {
                                upIamgeView();
                            }

                        } else {
                            ToastUtil.shortshow(getApplicationContext(), "服务器返回结果异常");
                            errorCode(obj.getInt("code"));
                            return;
                        }
                    } catch (JSONException e) {
                        LogUtil.e(getApplicationContext(), "进入catch");
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                    ToastUtil.shortshow(getApplicationContext(), "失败");
                    LogUtil.e(getApplicationContext(), "onFailure");
                }
            };
            AndroidAsyncHttp.post(ServerAPIConfig.GetIDToken, params, upHandler);
//            ToastUtil.shortshow(getApplicationContext(), size + "");
        }
    }

    /**
     * 七牛上传图片
     */
    public void upIamgeView() {

        LogUtil.e("进入 upIamgeView");
        final UploadManager upLoadManager = new UploadManager();
        for (int i = 0; i < size; i++) {

            UpCompletionHandler upComplete = new UpCompletionHandler() {
                @Override
                public void complete(String key, ResponseInfo responseInfo, JSONObject jsonObject) {
                    for (int j = 0; j < size; j++) {
                        if (key.equals(idTokenInfo.list.get(j).id)) {
                            if (responseInfo.isOK()) {
                                Log.e("成功", "上传成功");
                                getSharedPreferences(StaticBean.KEY, Context.MODE_PRIVATE).edit().putString("key", key).commit();
                                startActivity(PersonalDataActivity.class);
                                SetAvatarActivity.this.finish();
                            } else {
                                Log.e("失败", "失败");
                                //如果上传失败直接结束图片上传
                                ToastUtil.shortshow(getApplicationContext(), "上传失败");
                                return;
                            }
                        }
                    }


                }
            };
            String token = idTokenInfo.list.get(i).token;
            String key = idTokenInfo.list.get(i).id;
            upLoadManager.put(picPath, key, token, upComplete, null);
        }

    }
}
