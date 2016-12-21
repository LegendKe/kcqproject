package com.djx.pin.activity;

import android.app.Activity;
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
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.djx.pin.R;
import com.djx.pin.base.OldBaseActivity;
import com.djx.pin.beans.IDTokenInfo;
import com.djx.pin.beans.StaticBean;
import com.djx.pin.myview.CircleImageView;
import com.djx.pin.serverapiconfig.ServerAPIConfig;
import com.djx.pin.utils.AndroidAsyncHttp;
import com.djx.pin.utils.BitmapUtil;
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

import cz.msebera.android.httpclient.Header;

/**
 * Created by Administrator on 2016/6/5.
 */
public class ImprovePersonalDataActivity extends OldBaseActivity implements View.OnClickListener {
    protected final static String TAG = ImprovePersonalDataActivity.class.getSimpleName();

    private TextView tv_Finish;
    private CircleImageView cimg_MyAvatar;
    private EditText et_NickName;
    private View v_Parent_Cover, dialogView_AddPic;
    private LinearLayout ll_Back;

    private Context THIS = ImprovePersonalDataActivity.this;
    private Button bt_camera,//相机按钮
            bt_photoalbum,//相册按钮
            bt_cancle;//取消按钮
    private Dialog dialog_AddPic;
    Intent intent_Gallery, intent_Camera;

    BitmapUtil.SizeMessage sizeMessage;
    String path_Camera,//使用相机拍照的图片绝对路径
            dir_Camera,//使用相机拍照保存图片的文件夹路径
            picturePath;//照片的路径

    int requestCode_Gallery = 0,//打开图库的请求码.
            requestCode_Camera = 1;//调用相机拍照的请求码.
    boolean isPicFormat = false;//选择的图片格式是否正确;
    SharedPreferences sp;
    IDTokenInfo idTokenInfo_Pic;
    boolean isOK_Pic;
    UploadManager uploadManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_improve_personaldata);
        initView();
        initEvent();
        initDialog();
    }

    private void initEvent() {
        ll_Back.setOnClickListener(this);
        tv_Finish.setOnClickListener(this);
        cimg_MyAvatar.setOnClickListener(this);
        et_NickName.setOnClickListener(this);

        bt_camera.setOnClickListener(this);
        bt_cancle.setOnClickListener(this);
        bt_photoalbum.setOnClickListener(this);
    }

    private void initView() {
        ll_Back = (LinearLayout) findViewById(R.id.ll_Back);
        tv_Finish = (TextView) findViewById(R.id.tv_Finish);
        cimg_MyAvatar = (CircleImageView) findViewById(R.id.cimg_MyAvatar);
        et_NickName = (EditText) findViewById(R.id.et_NickName);
        v_Parent_Cover = findViewById(R.id.v_Parent_Cover);

        /**添加图片对话框相关控件*/
        dialogView_AddPic = getLayoutInflater().inflate(R.layout.layout_dialog_addpicvideo, null);
        bt_camera = (Button) dialogView_AddPic.findViewById(R.id.bt_camera);
        bt_photoalbum = (Button) dialogView_AddPic.findViewById(R.id.bt_photoalbum);
        bt_cancle = (Button) dialogView_AddPic.findViewById(R.id.bt_cancle);

        intent_Gallery = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent_Gallery.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        intent_Camera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        dir_Camera = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).toString() + "/Camera/";
        sizeMessage = new BitmapUtil.SizeMessage(THIS, false, 60, 60);
        sp = getSharedPreferences(StaticBean.USER_INFO, MODE_PRIVATE);
    }

    /**
     * 获取图片需要的Token,并上传信息至七牛和公司服务器
     */
    public void getIdToken_Pic() {
        idTokenInfo_Pic = null;
        RequestParams params = new RequestParams();
        params.put("size", 1);
        params.put("session_id", sp.getString("session_id", null));
        params.put("media_type", 1);
        AndroidAsyncHttp.post(ServerAPIConfig.GetIDToken, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                String str_json = new String(bytes);
                try {
                    JSONObject obj = new JSONObject(str_json);
                    LogUtil.e("str_json="+str_json);
                    if (0 != obj.getInt("code")) {
                        ToastUtil.shortshow(THIS, R.string.toast_error_server);
                        LogUtil.e(THIS, "服务器返回结果异常");
                        errorCode(obj.getInt("code"));
                        return;
                    }
                    idTokenInfo_Pic = new IDTokenInfo();
                    obj = obj.getJSONObject("result");
                    Gson gson = new Gson();
                    idTokenInfo_Pic = gson.fromJson(obj.toString(), IDTokenInfo.class);
                    updataPic2Qiniu();
                } catch (JSONException e) {
                    LogUtil.e(THIS, "进入catch异常");
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                ToastUtil.shortshow(THIS, R.string.toast_error_net);
                LogUtil.e(THIS, R.string.toast_error_net);
                idTokenInfo_Pic = null;

            }
        });

    }

    /**
     * 上传照片至七牛服务器,
     */
    public void updataPic2Qiniu() {
        if (null == idTokenInfo_Pic){
            return;
        }
        //重置照片是否上传成功的状态为false
        isOK_Pic = false;
        // 重用 uploadManager。一般地，只需要创建一个 uploadManager 对象
        if (null == uploadManager) {
            uploadManager = new UploadManager();
        }
        //上传照片
        uploadManager.put(picturePath, idTokenInfo_Pic.list.get(0).id, idTokenInfo_Pic.list.get(0).token, new UpCompletionHandler() {
            @Override
            public void complete(String key, ResponseInfo responseInfo, JSONObject jsonObject) {

                if (key.equals(idTokenInfo_Pic.list.get(0).id)) {
                    if (responseInfo.isOK()) {
                        isOK_Pic = true;
                        updataInfo2Server();
                    } else {
                        isOK_Pic = false;
                    }
                }

            }
        }, null);
    }


    /**
     * 所有文件都上传至七牛服务器时,将这些文件的信息上传至本公司的服务器.
     * 图片上传中或上传失败 则返回不再上传至公司服务器
     */
    private void updataInfo2Server() {
        if (!isOK_Pic) {
            return;
        }
        AsyncHttpResponseHandler res = new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                String str_json = new String(bytes);
                try {
                    JSONObject obj = new JSONObject(str_json);
                    if (0 != obj.getInt("code")) {
                        LogUtil.e(THIS, "完善用户资料失败");
                        ToastUtil.shortshow(THIS, R.string.toast_add_userinfo_failer);
                        errorCode(obj.getInt("code"));
                        return;
                    }
                    ToastUtil.shortshow(THIS, R.string.toast_add_userinfo_success);
                    startActivity(MainActivity.class);
                } catch (JSONException e) {
                    LogUtil.e(THIS, "enter catch");
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                LogUtil.e(THIS, "网络连接异常");
                ToastUtil.shortshow(THIS, R.string.toast_error_net);
            }
        };

        RequestParams params = new RequestParams();
        params.put("session_id", sp.getString("session_id", null));
        params.put("nickname", et_NickName.getText().toString());
        params.put("portrait", idTokenInfo_Pic.list.get(0).id);
        params.put("gender", sp.getInt("gender", 0));
        params.put("birthday", sp.getString("birthday", null));
        params.put("province", sp.getString("province", null));
        params.put("city", sp.getString("city", null));
        params.put("district", sp.getString("district", null));
        AndroidAsyncHttp.post(ServerAPIConfig.Updata_UserInfo,params,res);


    }

    private void initDialog() {
        /**添加图片对话框*/
        dialog_AddPic = new Dialog(THIS, R.style.dialog_transparent);
        dialog_AddPic.setContentView(dialogView_AddPic);
        WindowManager.LayoutParams layoutParams_AddPic = dialog_AddPic.getWindow().getAttributes();
        layoutParams_AddPic.gravity = Gravity.BOTTOM;
        layoutParams_AddPic.width = getWindowManager().getDefaultDisplay().getWidth();
        layoutParams_AddPic.y = 0;
        dialog_AddPic.getWindow().setAttributes(layoutParams_AddPic);
    }

    /**
     * 检查用户输入的信息是否正确,正确return ture;反之false
     *
     * @return
     */
    private boolean checkUserInfo() {
        //检查头像是否为空
        if (null == picturePath || picturePath.length() == 0) {
            ToastUtil.shortshow(THIS, R.string.toast_non_headImg);
            LogUtil.e(THIS, "头像为空");
            return false;
        }
        //检查用户名长度是否正确
        if (2 > et_NickName.getText().toString().length()) {
            ToastUtil.shortshow(THIS, R.string.toast_non_nickname);
            LogUtil.e(THIS, "昵称长度不正确");
            return false;
        }
        return true;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_Finish:
                //检查用户是否登录,未登录则return;
                if (getSharedPreferences(StaticBean.USER_INFO, MODE_PRIVATE).getBoolean("isLogined", false) == false) {
                    ToastUtil.shortshow(this, R.string.toast_non_login);
                    return;
                }
                if(!checkUserInfo()){
                    return;
                }
                getIdToken_Pic();
                break;
            case R.id.cimg_MyAvatar:
                dialog_AddPic.show();
                break;
            case R.id.ll_Back:
                startActivity(RegisterActivity.class);
                this.finish();
                break;
            //打开相册选择图片
            case R.id.bt_photoalbum:
                startReadSDCardByPermissions();
                dialog_AddPic.dismiss();
                break;
            //打开相机进行拍照,获取图片
            case R.id.bt_camera:
                startTakePhotoByPermissions();
                dialog_AddPic.dismiss();
                break;
            //点击取消
            case R.id.bt_cancle:
                dialog_AddPic.dismiss();
                break;
        }
    }

    @Override
    public void startReadSDCard() {
        Log.i(TAG, "startReadSDCard");
        startActivityForResult(intent_Gallery, requestCode_Gallery);
    }

    @Override
    public void startTakePhoto() {
        Log.i(TAG, "startTakePhoto");
        File file_Dir = new File(dir_Camera);
        if (file_Dir.exists() == false) {
            file_Dir.mkdirs();
        }
        File file = new File(dir_Camera, String.valueOf(System.currentTimeMillis())
                + ".jpg");
        path_Camera = file.getPath();
        Uri imageUri = Uri.fromFile(file);
        intent_Camera.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(intent_Camera, requestCode_Camera);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        /**
         * 打开图库获取图片并添加到iv_AddPic上
         *获取图片并显示到对应的imageView上.
         */
        if (requestCode == 0 && resultCode == Activity.RESULT_OK && null != data) {
            picturePath = getPath(THIS, data.getData());
            String picFormat = picturePath.substring(picturePath.lastIndexOf("."));
            for (int i = 0; i < StaticBean.PIC_FORMAT.length; i++) {
                if (StaticBean.PIC_FORMAT[i].equals(picFormat)) {
                    isPicFormat = true;
                }
            }
            if (!isPicFormat) {
                Toast.makeText(THIS, R.string.toast_pic_format_error, Toast.LENGTH_SHORT).show();
                return;
            }
            cimg_MyAvatar.setImageBitmap(BitmapUtil.loadBitmap(picturePath, sizeMessage));
            isPicFormat = false;
        }
        /**
         * 调用相机拍照返回的结果在此处理
         *获取图片并显示到对应的imageView上.
         */
        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
            picturePath = path_Camera;
            cimg_MyAvatar.setImageBitmap(BitmapUtil.loadBitmap(path_Camera, sizeMessage));
        }
    }


    /**
     * 用来获取打开图库时的路径,该方法使用Android4.4以下和以上的.
     *
     * @param uri 需要传入图库返回时的Rri.
     */
    public static String getPath(final Context context, final Uri uri) {

        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        // DocumentProvider
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }

                // TODO handle non-primary volumes
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
