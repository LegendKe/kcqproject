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
import android.text.method.LinkMovementMethod;
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

import com.djx.pin.beans.UserInfo;
import com.google.gson.Gson;
import com.djx.pin.R;
import com.djx.pin.base.OldBaseActivity;
import com.djx.pin.beans.IDTokenInfo;
import com.djx.pin.beans.StaticBean;
import com.djx.pin.serverapiconfig.ServerAPIConfig;
import com.djx.pin.utils.AndroidAsyncHttp;
import com.djx.pin.utils.ScreenUtils;
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
import java.text.SimpleDateFormat;
import java.util.Date;

import cz.msebera.android.httpclient.Header;

/**
 * Created by Administrator on 2016/6/21.
 */
public class IdentityActivity extends OldBaseActivity implements View.OnClickListener {
    private final static String TAG = IdentityActivity.class.getSimpleName();

    private ImageView img_IA_IdentityPhoto;
    private EditText et_IA_RealName, et_IA_IdentityNumber;
    private Button bt_IA_Put;
    private View v_Parent_Cover_IA;
    private LinearLayout ll_IA_Back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_identity);
        initView();
        initEvent();
    }

    private void initEvent() {

        img_IA_IdentityPhoto.setOnClickListener(this);
        bt_IA_Put.setOnClickListener(this);
        ll_IA_Back.setOnClickListener(this);

    }

    private void initView() {
        img_IA_IdentityPhoto = (ImageView) findViewById(R.id.img_IA_IdentityPhoto);
        et_IA_RealName = (EditText) findViewById(R.id.et_IA_RealName);
        et_IA_IdentityNumber = (EditText) findViewById(R.id.et_IA_IdentityNumber);
        bt_IA_Put = (Button) findViewById(R.id.bt_IA_Put);
        v_Parent_Cover_IA = findViewById(R.id.v_Parent_Cover_IA);
        ll_IA_Back = (LinearLayout) findViewById(R.id.ll_IA_Back);
        ((TextView)findViewById(R.id.tv_verify_specification)).setOnClickListener(this);
    }


    Intent intent;
    private File tempFile = new File(Environment.getExternalStorageDirectory(), getPhotoFileName());

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_IA_Back:
                this.finish();
                break;
            case R.id.img_IA_IdentityPhoto:
                showPopWin();
                break;
            case R.id.bt_IA_Put:
                String identityNumber = getedtStringAfterTrim(et_IA_IdentityNumber);
                String realName = getedtStringAfterTrim(et_IA_RealName);
                if (picPath == null || picPath.equals("")) {
                    ToastUtil.shortshow(this, "请上传身份证照片");
                } else if (getedtLengthAfterTrim(et_IA_RealName) == getEidtTextLength(et_IA_RealName)) {
                    if (realName.equals("") || realName == null || identityNumber.equals("") || identityNumber == null) {
                        ToastUtil.shortshow(this, "输入不能为空");
                    } else if (getedtLengthAfterTrim(et_IA_IdentityNumber) == 18) {
                        if (picPath == null) {
                            ToastUtil.shortshow(getApplicationContext(), "图片并没有改变");
                        } else {
                            getQiniu();
                        }

                    } else {
                        ToastUtil.shortshow(this, "请输入18位身份证号");
                    }
                } else {
                    ToastUtil.shortshow(this, "不能输入空格");
                }
                break;
            //弹框点击事件
            case R.id.tv_TakePhotots_Popwin:
                startTakePhotoByPermissions();
                popWindow.dismiss();
                break;
            case R.id.tv_Piktures_Popwin:
                startReadSDCardByPermissions();
                popWindow.dismiss();
                break;
            case R.id.tv_Cancel_Popwin:
                popWindow.dismiss();
                break;
            case R.id.tv_verify_specification:
                Intent intent = new Intent(this,TextActivity.class);
                Bundle bundle = new Bundle();
                bundle.putInt("TextContent",6);
                bundle.putString("url","http://www.dujoy.cn/app/identification/index.html");
                intent.putExtras(bundle);
                startActivity(intent);
                break;
        }
    }
    private static final int CAMERA_PERM = 1;

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
            Log.i(TAG, "resultCode is OK");
            if (requestCode == 0) {
                picPath = getPath(getApplicationContext(), data.getData());

                ContentResolver contentResolver = getContentResolver();
                try {
                    Bitmap bitmap = BitmapFactory.decodeStream(contentResolver.openInputStream(data.getData()));
                    img_IA_IdentityPhoto.setImageBitmap(bitmap);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            } else if (requestCode == 1) {
                Uri uri = Uri.fromFile(tempFile);
                picPath = getPath(getApplicationContext(), uri);
                try {

                    Bitmap bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(uri));
                    img_IA_IdentityPhoto.setImageBitmap(bitmap);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private PopupWindow popWindow;
    private TextView tv_TakePhotots_Popwin, tv_Piktures_Popwin, tv_Cancel_Popwin;
    private View popWindow_View;

    private void showPopWin() {
        popWindow = new PopupWindow();
        popWindow.setWidth(ScreenUtils.getScreenWidth(this));
        popWindow.setHeight(ScreenUtils.getScreenHeight(this) / 3);
        popWindow.setOutsideTouchable(true);
        popWindow.setFocusable(true);
        popWindow.setBackgroundDrawable(getResources().getDrawable(R.color.transparent));
        popWindow_View = LayoutInflater.from(this).inflate(R.layout.popwindow_photochose_ipda, null);
        popWindow.setContentView(popWindow_View);
        popWindow.showAtLocation(img_IA_IdentityPhoto, Gravity.BOTTOM | Gravity.CENTER, 0, 10);
        bankGroundColor(1);
        popWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                bankGroundColor(2);
            }
        });
        initPopView();
        initPopEvent();
    }

    private void initPopEvent() {
        tv_TakePhotots_Popwin.setOnClickListener(this);
        tv_Piktures_Popwin.setOnClickListener(this);
        tv_Cancel_Popwin.setOnClickListener(this);
    }

    private void initPopView() {

        tv_TakePhotots_Popwin = (TextView) popWindow_View.findViewById(R.id.tv_TakePhotots_Popwin);
        tv_Piktures_Popwin = (TextView) popWindow_View.findViewById(R.id.tv_Piktures_Popwin);
        tv_Cancel_Popwin = (TextView) popWindow_View.findViewById(R.id.tv_Cancel_Popwin);
    }

    public void bankGroundColor(int i) {
        switch (i) {
            case 1:

                v_Parent_Cover_IA.setVisibility(View.VISIBLE);
                v_Parent_Cover_IA.setAlpha(0.5f);

                break;
            case 2:

                v_Parent_Cover_IA.setVisibility(View.INVISIBLE);

                break;
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

                    try {
                        JSONObject obj = new JSONObject(str_json);
                        if (obj.getInt("code") == 0) {
                            Gson gson = new Gson();
                            idTokenInfo = gson.fromJson(obj.getString("result").toString(), IDTokenInfo.class);
                            for (int j = 0; j < size; j++) {

                            }

                            upIamgeView();
                        } else {

                            ToastUtil.shortshow(getApplicationContext(), "服务器返回结果异常");
                            errorCode(obj.getInt("code"));
                            return;
                        }
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
//            ToastUtil.shortshow(getApplicationContext(), size + "");
        }
    }

    /**
     * 七牛上传图片
     */
    public void upIamgeView() {
        final UploadManager upLoadManager = new UploadManager();
        for (int i = 0; i < size; i++) {

            UpCompletionHandler upComplete = new UpCompletionHandler() {
                @Override
                public void complete(String key, ResponseInfo responseInfo, JSONObject jsonObject) {
                    for (int j = 0; j < size; j++) {
                        if (key.equals(idTokenInfo.list.get(j).id)) {
                            if (responseInfo.isOK()) {
                                ToastUtil.shortshow(IdentityActivity.this, "图片上传成功");
                                SendMassage();
                            } else {

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
            ToastUtil.shortshow(IdentityActivity.this, "正在上传图片，请等待");
            upLoadManager.put(picPath, key, token, upComplete, null);
        }

    }

    /**
     * {
     * "session_id": "8527a2b6a58b4fa963fd",
     * "real_name": "",
     * "id_card": "21001199101010000",
     * "id_card_pic": "13k12j3k12kl31sal2"
     * }
     * 向服务器发送数据
     */
    public void SendMassage() {
        String id_card_pic = idTokenInfo.list.get(0).id;
        String real_name = getedtStringAfterTrim(et_IA_RealName);
        String id_card = getedtStringAfterTrim(et_IA_IdentityNumber);
        String url = ServerAPIConfig.RealNameAuth;
        RequestParams params = new RequestParams();
        params.put("session_id", session_id);
        params.put("real_name", real_name);
        params.put("id_card", id_card);
        params.put("id_card_pic", id_card_pic);

        AsyncHttpResponseHandler upHandler = new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {

                String str = new String(bytes);

                try {
                    JSONObject object = new JSONObject(str);

                    if (object.getInt("code") == 0) {
                        ToastUtil.shortshow(getApplicationContext(), "已提交，请等待审核");
                        UserInfo.getUserInfo(getApplicationContext(), getSession_id(), getUser_id());
                        getSharedPreferences(StaticBean.USER_INFO,Context.MODE_PRIVATE).edit().putBoolean("is_identityChanged",true).commit();
                        finish();
                    }else {
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
        AndroidAsyncHttp.post(url, params, upHandler);
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
}
