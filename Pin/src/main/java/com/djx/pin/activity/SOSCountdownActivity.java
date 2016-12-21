package com.djx.pin.activity;

import android.Manifest;
import android.os.Handler;
import android.os.Message;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.djx.pin.R;
import com.djx.pin.base.OldBaseActivity;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

public class SOSCountdownActivity extends OldBaseActivity implements View.OnClickListener {
    protected final static String TAG = SOSCountdownActivity.class.getSimpleName();

    TextView tv_countdown;
    Button bt_cancle;
    Handler handler;
    TimerTask timerTask;
    int second = 5;
    private static final int RECORD_PERM = 131;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sos_countdown);
        initView();
        initEvent();
        countDown();
    }

    private void initView() {
        tv_countdown = (TextView) findViewById(R.id.tv_countdown);
        bt_cancle = (Button) findViewById(R.id.bt_cancle);
        timerTask = new TimerTask() {
            @Override
            public void run() {
                handler.sendEmptyMessage(0);
            }
        };
        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                tv_countdown.setText( ""+second--);
                if (-1 == second) {
                    startActivityByPermissions();
                    timerTask.cancel();
                }
            }
        };
    }

    private void initEvent() {
        bt_cancle.setOnClickListener(this);
    }
    //倒计时功能,完成之后自动跳到录音界面
    private void countDown() {
        new Timer().schedule(timerTask, 0, 1000);
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            //点击不需要求助按钮
            case R.id.bt_cancle:
                timerTask.cancel();
                finish();
                break;
        }
    }

    @AfterPermissionGranted(RECORD_PERM)
    protected void startActivityByPermissions() {
        String[] perms = { Manifest.permission.SEND_SMS, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO};
        if (EasyPermissions.hasPermissions(this, perms)) {
            try {
                startActivity(SOSRecordActivity.class);
            } catch (Exception e) {
                Toast.makeText(this, R.string.permissions_record_error, Toast.LENGTH_LONG).show();
            }
        } else {
            // Request one permission
            EasyPermissions.requestPermissions(this,
                    getResources().getString(R.string.str_request_record_message),
                    RECORD_PERM, perms);
        }
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {
        String[] targetPerms = { Manifest.permission.SEND_SMS, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO};
        if (requestCode == RECORD_PERM) {
            try {
                if (EasyPermissions.hasPermissions(this, targetPerms)) {
                    try {
                        startActivity(SOSRecordActivity.class);
                    } catch (Exception e) {
//                        Toast.makeText(this, R.string.permissions_record_error, Toast.LENGTH_LONG).show();
                    }
                }
            } catch (Exception e) {
                Toast.makeText(this, R.string.permissions_record_error, Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
        Log.i(TAG, "onPermissionsDenied");
        if (requestCode == RECORD_PERM) {
            Toast.makeText(this, R.string.permissions_record_error, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
            Log.i(TAG, "do nothing");
            return true;
        } else {
            return super.onKeyDown(keyCode, event);
        }
    }
}
