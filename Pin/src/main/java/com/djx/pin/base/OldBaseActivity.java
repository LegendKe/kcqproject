package com.djx.pin.base;

import android.Manifest;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.djx.pin.R;
import com.djx.pin.beans.StaticBean;
import com.djx.pin.serverapiconfig.ServerAPIConfig;
import com.djx.pin.service.UpLoadSOSLocationService;
import com.djx.pin.utils.AndroidAsyncHttp;
import com.djx.pin.utils.ToastUtil;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.squareup.picasso.Picasso;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;
import uk.co.senab.photoview.PhotoView;


/**
 * Created by Administrator on 2016/4/25.
 */
public class OldBaseActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks {
    private final static String TAG = OldBaseActivity.class.getSimpleName();
    //qqAPPID
    public static final String APPID = "1105544348";
    private static final int CALL_PHONE = 120;
    private Tencent mTencent;
    private RequestQueue queue;
    private static final int CAMERA_PERM = 123;
    private static final int SD_PERM = 125;
    private static final int LOCATION_PERM = 127;
    private static final int RECORD_PERM = 131;//录音权限
    private static final int CONTACTS_PERM = 133;//录音权限


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    /**
     * 获得QQSDK控制对象
     */
    public Tencent getmTencent() {
        if (mTencent == null) {
            mTencent = Tencent.createInstance(APPID, getApplicationContext());
            return mTencent;
        }
        return mTencent;
    }


    /***/

    public RequestQueue getQueue() {
        if (queue == null) {
            queue = Volley.newRequestQueue(getApplicationContext());
            return queue;
        }
        return queue;
    }

    public class BaseUIlisener implements IUiListener {

        @Override
        public void onComplete(Object o) {
            ToastUtil.shortshow(getApplicationContext(), o.toString());
            Log.e("o", o.toString());
        }

        @Override
        public void onError(UiError uiError) {

        }

        @Override
        public void onCancel() {

        }
    }

    /***
     * 实现Activity之间不传值跳转的方法
     */
    public void startActivity(Class<?> Class) {
        Intent intent = new Intent(this, Class);
        this.startActivity(intent);
    }

    /***
     * 实现Activity之间传值跳转
     */
    public void startActivity(Class<?> Class, Bundle bundle) {
        Intent intent = new Intent(this, Class);
        intent.putExtras(bundle);
        this.startActivity(intent);
    }

    /**
     * 用来获取打开图库时的路径,该方法使用Android4.4以下和以上的.
     *
     * @param uri 需要传入图库返回时的Rri.
     */

    public static String getPath(final Context context, final Uri uri) {

        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && DocumentsContract.isDocumentUri(context, uri)) {

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


    /**
     * 请求七牛图片下载地址,图片集合是PhotoView
     */

    public void getPhotoViewUrl(final List<PhotoView> imageViewList, final int size, List<String> ids, int media_type) throws UnsupportedEncodingException {

        Log.i("test","imageViewList.size(): ------------------------------ "+imageViewList.size());
        final String[] url = new String[size];
        String id = null;
        final JSONObject newObj = new JSONObject();
        try {
            newObj.put("size", size);
            JSONArray array = new JSONArray();
            for (int l = 0; l < size; l++) {
                if(ids.size() > l){//如果list长度大于角标
                    id = ids.get(l);
                    JSONObject detaileObj = new JSONObject();
                    detaileObj.put("id", id);
                    detaileObj.put("media_type", media_type);
                    detaileObj.put("height", 400);
                    detaileObj.put("width", 400);
                    array.put(detaileObj);
                }
            }
            newObj.put("list", array);
            StringEntity entity = new StringEntity(newObj.toString(), "utf-8");

            AsyncHttpResponseHandler res = new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int i, Header[] headers, byte[] bytes) {
                    String str = new String(bytes);


                    try {
                        JSONObject obj = new JSONObject(str);

                        obj = obj.getJSONObject("result");
                        JSONArray array1 = obj.getJSONArray("list");
                        for (int j = 0; j < array1.length() && j < imageViewList.size(); j++) {

                            obj = array1.getJSONObject(j);
                            //其中memoryPolicy的NO_CACHE是指图片加载时放弃在内存缓存中查找，NO_STORE是指图片加载完不缓存在内存中。
                            Picasso.with(getApplicationContext())
                                    .load(obj.getString("url"))
                                    .config(Bitmap.Config.RGB_565)
                                    .error(R.mipmap.ic_loading)
                                    .placeholder(R.mipmap.ic_loading)
                                    .into(imageViewList.get(j));

                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                    Log.e("失败", "失败");
                }
            };
            AndroidAsyncHttp.post(getApplicationContext(), ServerAPIConfig.GetQiNiuUrl, entity, " application/json;charset=UTF-8", res);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 请求七牛图片下载地址,图片集合是ImageView
     */

    public void getImageViewUrl(final List<ImageView> imageViewList, final int size, final List<String> ids, int media_type) throws UnsupportedEncodingException {

        String id = null;
        int i=0;
        final JSONObject newObj = new JSONObject();
        try {
            JSONArray array = new JSONArray();
            for (int l = 0; l < size; l++) {
                id = ids.get(l);
                JSONObject detaileObj = new JSONObject();
                String url = (String) urlMap.get(id);
                if (url != null) {
                    Picasso.with(getApplicationContext())
                            .load(url)
                            .error(R.mipmap.ic_loading)
                            .placeholder(R.mipmap.ic_loading)
                            .into(imageViewList.get(ids.indexOf(id)));
                }else {
                    detaileObj.put("id", id);
                    detaileObj.put("media_type", media_type);
//                detaileObj.put("height", height);
//                detaileObj.put("width", width);
                    array.put(detaileObj);
                    i++;
                }
            }
            if (i==0){
                return;
            }
            newObj.put("size",i);
            newObj.put("list", array);
            StringEntity entity = new StringEntity(newObj.toString(), "utf-8");

            AsyncHttpResponseHandler res = new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int i, Header[] headers, byte[] bytes) {
                    String str = new String(bytes);


                    try {
                        JSONObject obj = new JSONObject(str);

                        obj = obj.getJSONObject("result");
                        JSONArray array1 = obj.getJSONArray("list");
                        for (int j = 0; j < array1.length(); j++) {

                            obj = array1.getJSONObject(j);
                            Picasso.with(getApplicationContext())
                                    .load(obj.getString("url"))
                                    .error(R.mipmap.ic_loading)
                                    .placeholder(R.mipmap.ic_loading)
                                    .config(Bitmap.Config.RGB_565)
                                    .into(imageViewList.get(j));
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                    Log.e("失败", "失败");
                }
            };
            AndroidAsyncHttp.post(getApplicationContext(), ServerAPIConfig.GetQiNiuUrl, entity, " application/json;charset=UTF-8", res);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    /**
     * 请求七牛图片下载地址,只有一个ImageView
     */
    public void getOneImageViewUrl(final ImageView imageView, final String id, int media_type) throws UnsupportedEncodingException {

        String url = (String) urlMap.get(id);

        if (url != null) {
            Picasso.with(getApplicationContext()).load(url).error(R.mipmap.ic_defaultcontact).into(imageView);
            return;
        }

        final JSONObject newObj = new JSONObject();
        try {
            newObj.put("size", 1);
            JSONArray array = new JSONArray();
            JSONObject detaileObj = new JSONObject();
            detaileObj.put("id", id);
            detaileObj.put("media_type", media_type);
            array.put(detaileObj);
            newObj.put("list", array);

            StringEntity entity = new StringEntity(newObj.toString(), "utf-8");

            AsyncHttpResponseHandler res = new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int i, Header[] headers, byte[] bytes) {
                    String str = new String(bytes);
                    Log.i("chuanqi","七牛云数据:"+str);
                    try {
                        JSONObject obj = new JSONObject(str);
                        obj = obj.getJSONObject("result");
                        JSONArray array1 = obj.getJSONArray("list");
                        for (int j = 0; j < array1.length(); j++) {
                            obj = array1.getJSONObject(j);
                            urlMap.put(obj.getString("id"), obj.getString("url"));
                            Picasso.with(getApplicationContext())
                                    .load(obj.getString("url"))
                                    .config(Bitmap.Config.RGB_565)
                                    .error(R.mipmap.ic_loading)
                                    .placeholder(R.mipmap.ic_loading)
                                    .into(imageView);
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                    Log.e("失败", "失败");
                }
            };
            AndroidAsyncHttp.post(getApplicationContext(), ServerAPIConfig.GetQiNiuUrl, entity, " application/json;charset=UTF-8", res);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 请求七牛图片下载地址,只有一个ImageView
     */
    public void getOneImageViewUrl(final ImageView imageView, final String id, int media_type,int width,int heigth) throws UnsupportedEncodingException {

        String url = (String) urlMap.get(id);

        if (url != null) {
            Picasso.with(getApplicationContext()).load(url).error(R.mipmap.ic_defaultcontact).into(imageView);
            return;
        }

        final JSONObject newObj = new JSONObject();
        try {
            newObj.put("size", 1);
            JSONArray array = new JSONArray();
            JSONObject detaileObj = new JSONObject();
            detaileObj.put("id", id);
            detaileObj.put("media_type", media_type);
            array.put(detaileObj);
            newObj.put("list", array);
            newObj.put("size", width);
            newObj.put("size", heigth);
            StringEntity entity = new StringEntity(newObj.toString(), "utf-8");

            AsyncHttpResponseHandler res = new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int i, Header[] headers, byte[] bytes) {
                    String str = new String(bytes);
                    Log.i("chuanqi","七牛云数据:"+str);
                    try {
                        JSONObject obj = new JSONObject(str);
                        obj = obj.getJSONObject("result");
                        JSONArray array1 = obj.getJSONArray("list");
                        for (int j = 0; j < array1.length(); j++) {
                            obj = array1.getJSONObject(j);
                            urlMap.put(obj.getString("id"), obj.getString("url"));
                            Picasso.with(getApplicationContext())
                                    .load(obj.getString("url"))
                                    .config(Bitmap.Config.RGB_565)
                                    .error(R.mipmap.ic_loading)
                                    .placeholder(R.mipmap.ic_loading)
                                    .into(imageView);
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                @Override
                public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                    Log.e("失败", "失败");
                }
            };
            AndroidAsyncHttp.post(getApplicationContext(), ServerAPIConfig.GetQiNiuUrl, entity, " application/json;charset=UTF-8", res);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * edittext中的长度
     */
    public static int getEidtTextLength(EditText editText) {
        int length = editText.length();
        return length;
    }

    /**
     * edittext中去掉空格后的字符串长度
     */
    public static int getedtLengthAfterTrim(EditText editText) {
        int length = editText.getText().toString().trim().length();
        return length;
    }

    /**
     * edittext中去掉空格后的字符串
     */
    public static String getedtStringAfterTrim(EditText editText) {
        return editText.getText().toString().trim();
    }

    protected Intent openUploadLocationService;
    Map urlMap;


    @Override
    protected void onPause() {
        super.onPause();
        //MobclickAgent.onPause(this);//友盟统计
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        startInitBaiduSDKByPermissions();
        urlMap = new HashMap();
        openUploadLocationService = new Intent(this, UpLoadSOSLocationService.class);
    }

    protected void errorCode(int code) {
        switch (code) {
            case 1001:
                ToastUtil.shortshow(this, "请求参数列表不完整或错误");
                break;
            case 4001:
                ToastUtil.shortshow(this, "数据库错误");
                break;
            case 4003:
                ToastUtil.shortshow(this, "数据库连接错误");
                break;
            case 4004:
                ToastUtil.shortshow(this, "数据库参数错误");
                break;
            case 2001:
                ToastUtil.shortshow(this, "请登录");
                break;
            case 2002:
                ToastUtil.shortshow(this, "第三方账号已被使用");
                break;
            case 2003:
                ToastUtil.shortshow(this, "该手机号已注册可直接登陆");
                break;
            case 2004:
                ToastUtil.shortshow(this, "昵称重复");
                break;
            case 2005:
                ToastUtil.shortshow(this, "身份证号已被占用");
                break;
            case 2006:
                ToastUtil.shortshow(this, "密码错误");
                break;
            case 2007:
                ToastUtil.shortshow(this, "手机号错误");
                break;
            case 2008:
                ToastUtil.shortshow(this, "重复实名认证");
                break;
            case 2009:
                ToastUtil.shortshow(this, "用户未实名认证");
                break;
            case 2011:
                ToastUtil.shortshow(this, "第三方账号token无效");
                break;
            case 2012:
                ToastUtil.shortshow(this, "用户未设置紧急联系人");
                break;
            case 2013:
                ToastUtil.shortshow(this, "用户未绑定芝麻信用或信用金为0");
                break;
            case 2014:
                ToastUtil.shortshow(this, "用户余额不足");
                break;
            case 2021:
                ToastUtil.shortshow(this, "用户无效或用户无法进行相应操作");
                break;
            case 2022:
                ToastUtil.shortshow(this, "用户与内容id不匹配");
                break;
            case 3001:
                ToastUtil.shortshow(this, "内容id无效");
                break;
            case 3101:
                ToastUtil.shortshow(this, "重复参与同一个SOS订单");
                break;
            case 3102:
                ToastUtil.shortshow(this, "有未完成的SOS订单，重复发布SOS");
                break;
            case 3103:
                ToastUtil.shortshow(this, "接单者完成SOS订单失败（距离没达到要求）");
                break;
            case 3104:
                ToastUtil.shortshow(this, "SOS当前状态不能进行该操作");
                break;
            case 4101:
                ToastUtil.shortshow(this, "重复PIN求助");
                break;
            case 4102:
                ToastUtil.shortshow(this, "重复接求助订单");
                break;
            case 4103:
                ToastUtil.shortshow(this, "签单者超过上限");
                break;
            case 4104:
                ToastUtil.shortshow(this, "您已提交申诉，请耐心等待申诉结果");
                break;
            case 4105:
                ToastUtil.shortshow(this, "求助订单上一节点用户无效");
                break;
            case 4201:
                ToastUtil.shortshow(this, "网络悬赏重复分享");
                break;
            case 4202:
                ToastUtil.shortshow(this, "网络悬赏重复抢单");
                break;
            case 4204:
                ToastUtil.shortshow(this, "网络悬赏当前状态不能进行该操作");
                break;
            case 5001:
                ToastUtil.shortshow(this, "短信发送失败");
                break;
            case 5002:
                ToastUtil.shortshow(this, "短信验证码校验失败");
                break;
            case 5003:
                ToastUtil.shortshow(this, "重复请求短信验证码");
                break;
            case 5004:
                ToastUtil.shortshow(this, "短信服务商登录失败");
                break;
            case 5011:
                ToastUtil.shortshow(this, "融云token请求失败");
                break;
            case 5012:
                ToastUtil.shortshow(this, "融云信息刷新失败");
                break;
            case 5021:
                ToastUtil.shortshow(this, "芝麻信用授权查询失败");
                break;
            case 5022:
                ToastUtil.shortshow(this, "芝麻信用获取OpenId失败");
                break;
            case 5023:
                ToastUtil.shortshow(this, "芝麻信用签名校验不通过");
                break;
            case 5024:
                ToastUtil.shortshow(this, "芝麻信用签名校验不通过");
                break;
        }
    }


    protected String getSession_id() {
        boolean isLogined = getSharedPreferences(StaticBean.USER_INFO, Context.MODE_PRIVATE).getBoolean("isLogined", false);
        if (isLogined) {
            return getSharedPreferences(StaticBean.USER_INFO, Context.MODE_PRIVATE).getString("session_id", null);
        }
        return null;
    }

    protected String getUser_id() {
        boolean isLogined = getSharedPreferences(StaticBean.USER_INFO, Context.MODE_PRIVATE).getBoolean("isLogined", false);
        if (isLogined) {
            return getSharedPreferences(StaticBean.USER_INFO, Context.MODE_PRIVATE).getString("user_id", null);
        }
        return null;
    }

    protected String toURLEncoded(String paramString) {
        if (paramString == null || paramString.equals("")) {
            Log.e("toURLEncoded error:", paramString);
            return "";
        }

        try {
            String str = new String(paramString.getBytes(), "UTF-8");
            str = URLEncoder.encode(str, "UTF-8");
            return str;
        } catch (Exception localException) {
            Log.e("toURLEncoded error:", paramString, localException);
        }

        return "";
    }

    @Override
    protected void onResume() {
        super.onResume();
        //MobclickAgent.onResume(this);//友盟统计
    }

    @AfterPermissionGranted(CONTACTS_PERM)
    protected void startReadContactsByPermissions() {
        String[] perms = {Manifest.permission.READ_CONTACTS,Manifest.permission.WRITE_CONTACTS,Manifest.permission.READ_PHONE_STATE};
        if (EasyPermissions.hasPermissions(this, perms)) {
            try {
                actionReadContacts();
            } catch (Exception e) {
                Toast.makeText(this, R.string.permissions_readcontacts_error, Toast.LENGTH_LONG).show();
            }
        } else {
            EasyPermissions.requestPermissions(this,
                    getResources().getString(R.string.str_request_readcontacts_message),
                    CONTACTS_PERM, perms);
        }
    }

    @AfterPermissionGranted(LOCATION_PERM)
    protected void startInitBaiduSDKByPermissions() {

        String[] perms3 = {Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.WRITE_EXTERNAL_STORAGE};

        final int permissionCount = perms3.length;
        ArrayList<String> arrayList = new ArrayList<>();
        for (int i = 0; i < permissionCount; i++) {
            if(!EasyPermissions.hasPermissions(this,perms3[i])){
                arrayList.add(perms3[i]);
            }
        }
        if(arrayList.size() == 0){//都已授权
            try {
                //initBaiduMap();
                appPermissionOK();
            } catch (Exception e) {
                Toast.makeText(this, R.string.permissions_location_error, Toast.LENGTH_LONG).show();
            }
        }else {
            int size=arrayList.size();
            String[] noPerms = (String[])arrayList.toArray(new String[size]);
            ActivityCompat.requestPermissions(this, noPerms, LOCATION_PERM);
        }
    }

    @AfterPermissionGranted(CAMERA_PERM)
    protected void startTakePhotoByPermissions() {
        String[] perms = {Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE};
        if (EasyPermissions.hasPermissions(this, perms)) {
            try {
               startTakePhoto();
            } catch (Exception e) {
                Toast.makeText(this, R.string.permissions_camera_error, Toast.LENGTH_LONG).show();
            }
        } else {
            EasyPermissions.requestPermissions(this,
                    getResources().getString(R.string.str_request_camera_message),
                    CAMERA_PERM, perms);
        }
    }
    /**
     * 执行含有录音权限的操作---> 获取权限
     */
    @AfterPermissionGranted(RECORD_PERM)//录音权限
    public void startRecordByPermissions() {
        String[] perms = {Manifest.permission.RECORD_AUDIO};
        if (EasyPermissions.hasPermissions(this, perms)) {//如果已经有这个权限
            try {
                excuteActionContainRecordPermision();//获取权限后执行的动作
            } catch (Exception e) {
                Toast.makeText(this, R.string.record_permision_fail_string, Toast.LENGTH_LONG).show();
            }
        } else {
            EasyPermissions.requestPermissions(this,
                    getResources().getString(R.string.record_permision_fail_string),
                    RECORD_PERM, perms);
        }
    }

    @AfterPermissionGranted(CALL_PHONE)
    protected void startCallPhoneByPermissions() {
        String[] perms = {Manifest.permission.CALL_PHONE};
        if (EasyPermissions.hasPermissions(this, perms)) {
            try {
                actionCallPhone();
            } catch (Exception e) {
                Toast.makeText(this, "无法拨打电话,请您正确授权", Toast.LENGTH_LONG).show();
            }
        } else {
            EasyPermissions.requestPermissions(this,
                    "应用程序需要授予拨打电话权限",
                    CALL_PHONE, perms);
        }
    }

    protected void actionCallPhone() {
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {
        Log.i("baseactivity", "" + requestCode);
        if (requestCode == CAMERA_PERM) {
            try {
                startTakePhoto();
            } catch (Exception e) {
                Toast.makeText(this, R.string.permissions_camera_error, Toast.LENGTH_LONG).show();
            }
        } else if (requestCode == SD_PERM) {
            try {
                startReadSDCard();
            } catch (Exception e) {
                Toast.makeText(this, R.string.permissions_sd_error, Toast.LENGTH_LONG).show();
            }
        } else if (requestCode == LOCATION_PERM) {

            String[] targetPerms = {Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.WRITE_EXTERNAL_STORAGE};
            try {
                if (EasyPermissions.hasPermissions(this, targetPerms)) {
                    try {
                        //initBaiduMap();
                        appPermissionOK();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            } catch (Exception e) {
                Toast.makeText(this, R.string.app_permision_fail_string, Toast.LENGTH_LONG).show();
            }

        }else if (requestCode == RECORD_PERM) {//录音
            String[] targetPerms = {Manifest.permission.RECORD_AUDIO};
            try {
                if (EasyPermissions.hasPermissions(this, targetPerms)) {
                    try {
                        excuteActionContainRecordPermision();
                    } catch (Exception e) {
                    }
                }
            } catch (Exception e) {
                Toast.makeText(this, R.string.record_permision_fail_string, Toast.LENGTH_LONG).show();
            }
        }else if (requestCode == CONTACTS_PERM) {//通讯录
            String[] targetPerms = {Manifest.permission.READ_CONTACTS};
            try {
                if (EasyPermissions.hasPermissions(this, targetPerms)) {
                    try {
                        actionReadContacts();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            } catch (Exception e) {
                Toast.makeText(this, R.string.readcontacts_permision_fail_string, Toast.LENGTH_LONG).show();
            }
        }else if(requestCode == CALL_PHONE){
            try {
                actionCallPhone();
            } catch (Exception e) {
                Toast.makeText(this, "拨打电话失败,请授权", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
        if (requestCode == CAMERA_PERM) {
            Toast.makeText(this, R.string.permissions_camera_error, Toast.LENGTH_LONG).show();
        } else if (requestCode == SD_PERM) {
            Toast.makeText(this, R.string.permissions_sd_error, Toast.LENGTH_LONG).show();
        } else if (requestCode == LOCATION_PERM) {
            appPermissionDeny();
        } else if(requestCode == RECORD_PERM){
            Toast.makeText(this, R.string.record_permision_fail_string, Toast.LENGTH_LONG).show();
        }else if(requestCode == CALL_PHONE){
            Toast.makeText(this, "请打开拨打电话权限", Toast.LENGTH_LONG).show();
        }
    }

    public void appPermissionDeny() {
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    //子类重写该方法
    public void startTakePhoto() {
        Log.i(TAG, "startTakePhoto");
    }

    //子类重写该方法
    public void actionReadContacts() {
        Log.i(TAG, "actionReadContacts");
    }

    @AfterPermissionGranted(SD_PERM)
    public void startReadSDCardByPermissions() {
        String[] perms = {Manifest.permission.READ_EXTERNAL_STORAGE};
        if (EasyPermissions.hasPermissions(this, perms)) {
            try {
                startReadSDCard();
            } catch (Exception e) {
                Toast.makeText(this, R.string.permissions_sd_error, Toast.LENGTH_LONG).show();
            }
        } else {
            EasyPermissions.requestPermissions(this,
                    getResources().getString(R.string.str_request_sd_message),
                    SD_PERM, perms);
        }
    }

    //子类重写该方法
    public void startReadSDCard() {
        Log.i(TAG, "startReadSDCard");
    }


    /**
     * 子类重写该方法,获取录音权限后执行相关操作
     */
    public void excuteActionContainRecordPermision() {}

    /**
     * 子类重写该方法,获取录音权限后执行相关操作
     */
    public void appPermissionOK() {}

}
