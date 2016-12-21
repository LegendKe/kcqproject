package com.djx.pin.improve.base.activity;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.djx.pin.R;
import com.djx.pin.utils.myutils.ConstantUtils;
import com.foamtrace.photopicker.SelectModel;
import com.foamtrace.photopicker.intent.PhotoPickerIntent;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

/**
 * Created by 柯传奇 on 2016/11/5 0005.
 */
public  abstract class BasePermissionActivity extends BaseActivity implements EasyPermissions.PermissionCallbacks{

    private static final int CAMERA_PERM = 123;
    private static final int SD_PERM = 125;
    private static final int LOCATION_PERM = 127;
    private static final int RECORD_PERM = 131;//录音权限
    private static final int CONTACTS_PERM = 133;//录音权限
    private static final int CALL_PHONE = 120;
    private final static String TAG = BasePermissionActivity.class.getSimpleName();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 通过注解绑定控件
        ButterKnife.bind(this);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }

    public void startActivity(Class<?> Class) {
        Intent intent = new Intent(this, Class);
        this.startActivity(intent);
    }
    public void startActivity(Class<?> Class, Bundle bundle) {
        Intent intent = new Intent(this, Class);
        intent.putExtras(bundle);
        this.startActivity(intent);
    }




    //1-1选照片
    @AfterPermissionGranted(ConstantUtils.PER_ALBUM_EXTERNAL_STORAGE)
    protected void selectPhotoFromAlbum(){
        if (EasyPermissions.hasPermissions(this, android.Manifest.permission.READ_EXTERNAL_STORAGE)) {
            try {
                selectPhoto();
            }catch (Exception e){
                Toast.makeText(this, R.string.str_request_sd_message, Toast.LENGTH_LONG).show();
            }
        } else {
            EasyPermissions.requestPermissions(this,"为能正常使用,请打开存储权限", ConstantUtils.PER_EXTERNAL_STORAGE,android.Manifest.permission.READ_EXTERNAL_STORAGE);
        }
    }
    protected ArrayList<String> imagePaths = new ArrayList<>();
    /**
     * 1-2选择手机相册
     * 子类在onActivityResult中接收数据
     */
    private void selectPhoto() {
        PhotoPickerIntent intent = new PhotoPickerIntent(this);
        intent.setSelectModel(SelectModel.MULTI);
        intent.setShowCarema(false); // 是否显示拍照， 默认false
        intent.setMaxTotal(9); // 最多选择照片数量，默认为9
        intent.setSelectedPaths(imagePaths); // 已选中的照片地址， 用于回显选中状态
        startActivityForResult(intent, ConstantUtils.INTENT_READ_ALBUM);
    }

    /************************************************************************************/
    //2-1拍照
    @AfterPermissionGranted(ConstantUtils.PER_CAMERA)
    protected void takePhotoByPermission() {
        String[] perms = {Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE};
        if (EasyPermissions.hasPermissions(this, perms)) {
            try {
                takePhoto();
            } catch (Exception e) {
                Toast.makeText(this, R.string.permissions_camera_error, Toast.LENGTH_LONG).show();
            }
        } else {
            EasyPermissions.requestPermissions(this, getResources().getString(R.string.str_request_camera_message), ConstantUtils.PER_CAMERA, perms);
        }
    }

    public String path_Camera;
    /**
     * 2-2拍照
     * 子类在onActivityResult中接收数据
     */
    private void takePhoto() {
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
        startActivityForResult(intent,ConstantUtils.INTENT_IMAGE_CAPTURE);
    }
    /***************************************************************************************/















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
        } else if (requestCode == RECORD_PERM) {//录音
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
        }else if(requestCode == ConstantUtils.PER_ALBUM_EXTERNAL_STORAGE){
            try {
                selectPhoto();
            } catch (Exception e) {
                Toast.makeText(this, "无法读取外部存储,请正确授权", Toast.LENGTH_LONG).show();
            }
        }else if(requestCode == ConstantUtils.PER_CAMERA){
            try {
                takePhoto();
            } catch (Exception e) {
                Toast.makeText(this, "无法读取外部存储,请正确授权", Toast.LENGTH_LONG).show();
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
            // Request one permission
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


}
