package com.djx.pin.improve.base.fragment;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Toast;

import com.djx.pin.R;
import com.djx.pin.ui.CommonDialog;
import com.djx.pin.utils.LogUtil;
import com.djx.pin.utils.myutils.ConstantUtils;
import com.foamtrace.photopicker.SelectModel;
import com.foamtrace.photopicker.intent.PhotoPickerIntent;
import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

/**
 * 含权限申请的fragment
 * Created by 柯传奇 on 2016/11/28 0028.
 */

public abstract class BasePermissionFragment extends BaseFragment implements EasyPermissions.PermissionCallbacks{


    protected ArrayList<String> imagePaths = new ArrayList<>();

    /**********************************************权限申请****************************************************/

    //1-1存储权限申请
    @AfterPermissionGranted(ConstantUtils.PER_ALBUM_EXTERNAL_STORAGE)
    protected void requestPermissionsStorage(){
        if (EasyPermissions.hasPermissions(context, android.Manifest.permission.READ_EXTERNAL_STORAGE)) {
            try {
                selectPhotoFromAlbum();
            }catch (Exception e){
                Toast.makeText(context, R.string.str_request_sd_message, Toast.LENGTH_LONG).show();
            }
        } else {
            EasyPermissions.requestPermissions(this,"为能正常使用,请打开存储权限", ConstantUtils.PER_EXTERNAL_STORAGE,android.Manifest.permission.READ_EXTERNAL_STORAGE);
        }
    }



    //1-2拍照权限申请
    @AfterPermissionGranted(ConstantUtils.PER_CAMERA)
    protected void startTakePhotoByPermissions() {
        String[] perms = {Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE};
        if (EasyPermissions.hasPermissions(context, perms)) {
            try {
                startTakePhoto();
            } catch (Exception e) {
                Toast.makeText(context, R.string.permissions_camera_error, Toast.LENGTH_LONG).show();
            }
        } else {
            EasyPermissions.requestPermissions(this, getResources().getString(R.string.str_request_camera_message), ConstantUtils.PER_CAMERA, perms);
        }
    }
    @AfterPermissionGranted(ConstantUtils.PER_ALBUM_EXTERNAL_STORAGE)
    protected void slectOnePicPermissions(){
        if (EasyPermissions.hasPermissions(context, android.Manifest.permission.READ_EXTERNAL_STORAGE)) {
            try {
                selectOnePhotoFromAlbum();
            }catch (Exception e){
                Toast.makeText(context, R.string.str_request_sd_message, Toast.LENGTH_LONG).show();
            }
        } else {
            EasyPermissions.requestPermissions(this,"为能正常使用,请打开存储权限", ConstantUtils.PER_EXTERNAL_STORAGE,android.Manifest.permission.READ_EXTERNAL_STORAGE);
        }
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {
        switch (requestCode){
            case  ConstantUtils.PER_ALBUM_EXTERNAL_STORAGE:
                try {
                    selectPhotoFromAlbum();
                } catch (Exception e) {
                    Toast.makeText(context, "无法读取外部存储,请正确授权", Toast.LENGTH_LONG).show();
                }
                break;
        }
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
        String reminderWords = "为能正常使用,请打开相关权限";
        switch (requestCode){
            case  ConstantUtils.PER_ALBUM_EXTERNAL_STORAGE:
                reminderWords = "选择图片需要对该应用开启存储权限,是否开启?";
                break;

        }
        CommonDialog.show(context, "去开启", "取消", reminderWords, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Settings.ACTION_APPLICATION_SETTINGS));
            }
        });
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        //Forward results to EasyPermissions结果转发给EasyPermissions
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }




/*************************************************获取权限后的操作**********************************************************/
    /**
     * 选择手机相册
     * 子类在onActivityResult中接收数据
     */
    private void selectPhotoFromAlbum() {
        PhotoPickerIntent intent = new PhotoPickerIntent(context);
        intent.setSelectModel(SelectModel.MULTI);
        intent.setShowCarema(false); // 是否显示拍照， 默认false
        intent.setMaxTotal(9); // 最多选择照片数量，默认为9
        intent.setSelectedPaths(imagePaths); // 已选中的照片地址， 用于回显选中状态
        startActivityForResult(intent, ConstantUtils.INTENT_READ_ALBUM);
    }
    /**
     * 选择手机相册
     * 子类在onActivityResult中接收数据
     */
    private void selectOnePhotoFromAlbum() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent,ConstantUtils.INTENT_READ_ALBUM);
    }

    public String path_Camera;

    protected AsyncHttpResponseHandler mHandler = new AsyncHttpResponseHandler() {
        @Override
        public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
            String str_json = new String(responseBody);
            JSONObject jsonObject = null;
            try {
                jsonObject = new JSONObject(str_json);
                if (0 == jsonObject.getInt("code")) {
                    Gson gson = new Gson();
                    parseData(gson, jsonObject.getJSONObject("result").toString());
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        @Override
        public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

        }
    };

    protected void parseData(Gson gson, String json) {}//解析数据,子类重写

    /**
     * 拍照
     * 子类在onActivityResult中接收数据
     */
    private void startTakePhoto() {
        String dir_Camera = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).toString() + "/Camera/";
        File file_Dir = new File(dir_Camera);
        if (file_Dir.exists() == false) {
            file_Dir.mkdirs();
        }
        File file_pic = new File(dir_Camera, String.valueOf(System.currentTimeMillis()) + ".jpg");
        Uri picUri = Uri.fromFile(file_pic);
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, picUri);
        path_Camera = file_pic.getPath();
        LogUtil.e("拍照startActivityForResult-----------:"+path_Camera);
        startActivityForResult(intent,ConstantUtils.INTENT_IMAGE_CAPTURE);
    }
}
