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
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.djx.pin.R;
import com.djx.pin.base.OldBaseActivity;
import com.djx.pin.beans.IDTokenInfo;
import com.djx.pin.beans.StaticBean;
import com.djx.pin.serverapiconfig.ServerAPIConfig;
import com.djx.pin.utils.AndroidAsyncHttp;
import com.djx.pin.utils.LogUtil;
import com.djx.pin.utils.ScreenUtils;
import com.djx.pin.utils.ToastUtil;
import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.qiniu.android.http.ResponseInfo;
import com.qiniu.android.storage.UpCompletionHandler;
import com.qiniu.android.storage.UploadManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;

/**
 * Created by Administrator on 2016/8/11 0011.
 */
public class RewardOffLineAppealActivity extends OldBaseActivity implements View.OnClickListener {
    LinearLayout ll_Back_CA, ll_1, ll_2, ll_3, ll_4;
    CheckBox cb_1, cb_2, cb_3, cb_4;
    TextView tv_1, tv_2, tv_3, tv_4;
    Button bt_Complete_WA;
    EditText edt_otherRemark;
    ImageView img_IWSA_AddImageView1, img_IWSA_AddImageView2, img_IWSA_AddImageView3, img_IWSA_AddImageView4;
    View v_ParentCover_CDA;
    Bundle bundle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rewardofflineappealactivity);
        initView();
        initEvent();
    }
    private void initEvent() {
        ll_Back_CA.setOnClickListener(this);
        ll_1.setOnClickListener(this);
        ll_2.setOnClickListener(this);
        ll_3.setOnClickListener(this);
        ll_4.setOnClickListener(this);

        img_IWSA_AddImageView1.setOnClickListener(this);
        img_IWSA_AddImageView2.setOnClickListener(this);
        img_IWSA_AddImageView3.setOnClickListener(this);
        img_IWSA_AddImageView4.setOnClickListener(this);

        bt_Complete_WA.setOnClickListener(this);

    }

    private void initView() {
        ll_Back_CA = (LinearLayout) findViewById(R.id.ll_Back_CA);

        ll_1 = (LinearLayout) findViewById(R.id.ll_1);
        ll_2 = (LinearLayout) findViewById(R.id.ll_2);
        ll_3 = (LinearLayout) findViewById(R.id.ll_3);
        ll_4 = (LinearLayout) findViewById(R.id.ll_4);

        cb_1 = (CheckBox) findViewById(R.id.cb_1);
        cb_2 = (CheckBox) findViewById(R.id.cb_2);
        cb_3 = (CheckBox) findViewById(R.id.cb_3);
        cb_4 = (CheckBox) findViewById(R.id.cb_4);

        tv_1 = (TextView) findViewById(R.id.tv_1);
        tv_2 = (TextView) findViewById(R.id.tv_2);
        tv_3 = (TextView) findViewById(R.id.tv_3);
        tv_4 = (TextView) findViewById(R.id.tv_4);

        edt_otherRemark = (EditText) findViewById(R.id.edt_otherRemark);

        bt_Complete_WA = (Button) findViewById(R.id.bt_Complete_WA);

        img_IWSA_AddImageView1 = (ImageView) findViewById(R.id.img_IWSA_AddImageView1);
        img_IWSA_AddImageView2 = (ImageView) findViewById(R.id.img_IWSA_AddImageView2);
        img_IWSA_AddImageView3 = (ImageView) findViewById(R.id.img_IWSA_AddImageView3);
        img_IWSA_AddImageView4 = (ImageView) findViewById(R.id.img_IWSA_AddImageView4);
        v_ParentCover_CDA = findViewById(R.id.v_ParentCover_CDA);
    }

    /**
     * Called when a view has been clicked.
     *
     * @param v The view that was clicked.
     */
    boolean is_ll_1 = true;
    boolean is_ll_2 = true;
    boolean is_ll_3 = true;
    boolean is_ll_4 = true;
    String remark = "";
    Intent intent;
    //相机拍照文件存储路径
    private File tempFile = new File(Environment.getExternalStorageDirectory(), getPhotoFileName());

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_Back_CA:
                this.finish();
                break;
            case R.id.ll_1:
                if (is_ll_1) {
                    cb_1.setChecked(true);
                    is_ll_1 = false;
                } else {
                    cb_1.setChecked(false);
                    is_ll_1 = true;
                }
                break;
            case R.id.ll_2:
                if (is_ll_2) {
                    cb_2.setChecked(true);
                    is_ll_2 = false;
                } else {
                    cb_2.setChecked(false);
                    is_ll_2 = true;
                }
                break;
            case R.id.ll_3:
                if (is_ll_3) {
                    cb_3.setChecked(true);
                    is_ll_3 = false;
                } else {
                    cb_3.setChecked(false);
                    is_ll_3 = true;
                }
                break;
            case R.id.ll_4:
                if (is_ll_4) {
                    cb_4.setChecked(true);
                    is_ll_4 = false;
                } else {
                    cb_4.setChecked(false);
                    is_ll_4 = true;
                }
                break;
            case R.id.img_IWSA_AddImageView1:
                showPop(1);
                break;
            case R.id.img_IWSA_AddImageView2:
                showPop(2);
                break;
            case R.id.img_IWSA_AddImageView3:
                showPop(3);
                break;
            case R.id.img_IWSA_AddImageView4:
                showPop(4);
                break;
            case R.id.bt_Complete_WA:
                remark = "";
                String otherRemark = edt_otherRemark.getText().toString();

                if (cb_1.isChecked()) {
                    remark += tv_1.getText().toString() + "\n";
                }
                if (cb_2.isChecked()) {
                    remark += tv_2.getText().toString() + "\n";
                }
                if (cb_3.isChecked()) {
                    remark += tv_3.getText().toString() + "\n";
                }
                if (cb_4.isChecked()) {
                    remark += tv_4.getText().toString() + "\n";
                }
                if (edt_otherRemark.length() != 0) {
                    remark += "其它原因: " + otherRemark + "\n";
                }
                Log.e("remark======", remark);
                if (remark.equals("")) {
                    ToastUtil.shortshow(this, "请选择原因");
                } else {
                    Log.e("remark=====", remark);
                    getQiniu();
                }

                break;

            //图片选择弹窗点击事件
            case R.id.tv_TakePhotots_Popwin:
                intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                // 指定调用相机拍照后照片的储存路径
                intent.putExtra(MediaStore.EXTRA_OUTPUT,
                        Uri.fromFile(tempFile));
                startActivityForResult(intent, 1);
                popupWindoew.dismiss();
                break;
            case R.id.tv_Piktures_Popwin:
                intent = new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent, 0);
                popupWindoew.dismiss();
                break;
            case R.id.tv_Cancel_Popwin:
                popupWindoew.dismiss();
                break;
        }
    }

    // 使用系统当前日期加以调整作为照片的名称
    private String getPhotoFileName() {
        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "'IMG'_yyyyMMdd_HHmmss");
        return dateFormat.format(date) + ".jpg";
    }

    String[] path = {"", "", "", ""};

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            String picPath;
            if (requestCode == 0) {
                ContentResolver contentResolver = getContentResolver();
                picPath = getPath(getApplicationContext(), data.getData());
                Log.e("data.getData()=======", data.getData().toString());
                ToastUtil.shortshow(this, picPath.toString());
                try {
                    Bitmap bitmap = BitmapFactory.decodeStream(contentResolver.openInputStream(data.getData()));
                    switch (whichImage) {
                        case 1:
                            img_IWSA_AddImageView1.setImageBitmap(bitmap);
                            path[0] = picPath;
                            img_IWSA_AddImageView2.setVisibility(View.VISIBLE);
                            break;
                        case 2:
                            img_IWSA_AddImageView2.setImageBitmap(bitmap);
                            path[1] = picPath;
                            img_IWSA_AddImageView3.setVisibility(View.VISIBLE);
                            break;
                        case 3:
                            img_IWSA_AddImageView3.setImageBitmap(bitmap);
                            path[2] = picPath;
                            img_IWSA_AddImageView4.setVisibility(View.VISIBLE);
                            break;
                        case 4:
                            img_IWSA_AddImageView4.setImageBitmap(bitmap);
                            path[3] = picPath;
                            break;
                    }

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            } else if (requestCode == 1) {
                Uri uri = Uri.fromFile(tempFile);
                Log.e("uri=======", uri.toString());
                try {
                    picPath = getPath(getApplicationContext(), uri);
                    Bitmap bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(uri));
                    switch (whichImage) {
                        case 1:
                            img_IWSA_AddImageView1.setImageBitmap(bitmap);
                            path[0] = picPath;
                            Log.e("picpath=====", picPath);
                            Log.e("tempFile=====", tempFile.toString());
                            img_IWSA_AddImageView2.setVisibility(View.VISIBLE);
                            break;
                        case 2:
                            img_IWSA_AddImageView2.setImageBitmap(bitmap);
                            path[1] = picPath;
                            img_IWSA_AddImageView3.setVisibility(View.VISIBLE);
                            break;
                        case 3:
                            img_IWSA_AddImageView3.setImageBitmap(bitmap);
                            path[2] = picPath;
                            img_IWSA_AddImageView4.setVisibility(View.VISIBLE);
                            break;
                        case 4:
                            img_IWSA_AddImageView4.setImageBitmap(bitmap);
                            path[3] = picPath;
                            break;
                    }
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }

            }
        }
        if (path[0] == null) {
            img_IWSA_AddImageView1.setVisibility(View.VISIBLE);
            img_IWSA_AddImageView2.setVisibility(View.INVISIBLE);
            img_IWSA_AddImageView3.setVisibility(View.INVISIBLE);
            img_IWSA_AddImageView4.setVisibility(View.INVISIBLE);

        }
    }

    private PopupWindow popupWindoew;
    private View popView;

    private int whichImage = 0;

    public void showPop(int i) {
        whichImage = i;
        popupWindoew = new PopupWindow();
        popupWindoew.setFocusable(true);
        popupWindoew.setOutsideTouchable(true);
        popupWindoew.setTouchable(true);
        popupWindoew.setWidth(ScreenUtils.getScreenWidth(this));
        popupWindoew.setHeight(ScreenUtils.getScreenHeight(this) / 3);
        popView = LayoutInflater.from(this).inflate(R.layout.popwindow_photochose_ipda, null);
        popupWindoew.setContentView(popView);
        popupWindoew.setBackgroundDrawable(getResources().getDrawable(R.color.transparent));
        parentCover(1);
        //初始化抢单弹窗控件和事件
        initPopView();
        initPopEvent();
        popupWindoew.showAtLocation(ll_Back_CA, Gravity.BOTTOM, 0, 0);
        popupWindoew.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                parentCover(2);
            }
        });
    }

    private void initPopEvent() {
        tv_TakePhotots_Popwin.setOnClickListener(this);
        tv_Piktures_Popwin.setOnClickListener(this);
        tv_Cancel_Popwin.setOnClickListener(this);
    }

    private TextView tv_TakePhotots_Popwin, tv_Piktures_Popwin, tv_Cancel_Popwin;

    private void initPopView() {
        tv_TakePhotots_Popwin = (TextView) popView.findViewById(R.id.tv_TakePhotots_Popwin);
        tv_Piktures_Popwin = (TextView) popView.findViewById(R.id.tv_Piktures_Popwin);
        tv_Cancel_Popwin = (TextView) popView.findViewById(R.id.tv_Cancel_Popwin);
    }

    public void parentCover(int i) {
        switch (i) {
            case 1:
                v_ParentCover_CDA.setVisibility(View.VISIBLE);
                v_ParentCover_CDA.setAlpha(0.5f);
                break;
            case 2:
                v_ParentCover_CDA.setVisibility(View.GONE);
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
    int size = 0;
    String session_id;
    int media_type = 1;
    IDTokenInfo idTokenInfo;


    public void getQiniu() {
        size = 0;
        session_id = getSharedPreferences(StaticBean.USER_INFO, Context.MODE_PRIVATE).getString("session_id", null);
        if (session_id == null || session_id.equals("")) {
            ToastUtil.shortshow(getApplicationContext(), "请先登录应用");
            return;
        } else {
            for (int i = 0; i < 4; i++) {
                if (path[i] != null && path[i].length() != 0) {
                    size = size + 1;
                }
            }
            if (size == 0) {
                SendRewardStop();
                return;
            }
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
                            upIamgeView();

                        } else {
                            errorCode(obj.getInt("code"));
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
            ToastUtil.shortshow(getApplicationContext(), size + "");
        }
    }

    /**
     * 七牛上传图片
     */
//图片上传成功数量
    int trueNumber = 0;

    public void upIamgeView() {


        trueNumber = 0;
        LogUtil.e("进入 upIamgeView");
        final UploadManager upLoadManager = new UploadManager();
        for (int i = 0; i < size; i++) {

            UpCompletionHandler upComplete = new UpCompletionHandler() {
                @Override
                public void complete(String key, ResponseInfo responseInfo, JSONObject jsonObject) {
                    for (int j = 0; j < size; j++) {
                        if (key.equals(idTokenInfo.list.get(j).id)) {
                            if (responseInfo.isOK()) {
                                //如果上传成功,上传成功数就加1
                                trueNumber = trueNumber + 1;
                            } else {
                                Log.e("失败", "失败");
                                //如果上传失败直接结束图片上传
                                ToastUtil.shortshow(getApplicationContext(), "上传失败");
                                return;
                            }
                        }
                    }
                    Log.e("trueNumber0==", trueNumber + "");
                    //如果上传成功数等于图片数量,开始向服务器发布信息
                    if (trueNumber == size) {
                        SendRewardStop();
                    } else {
                        ToastUtil.shortshow(getApplicationContext(), "正在上传");
                    }

                }
            };
            String token = idTokenInfo.list.get(i).token;
            String key = idTokenInfo.list.get(i).id;
//            验证失败的时候
//            if (i==2){
//                token="xxxx";
//            }
            upLoadManager.put(path[i], key, token, upComplete, null);
        }
    }

    private void SendRewardStop() {
        bundle = getIntent().getExtras();
        JSONObject jsonObject = new JSONObject();
        JSONArray jsonArray = new JSONArray();

        LogUtil.e("申诉内容="+remark);

        try {
            jsonObject.put("session_id", getSession_id());
            jsonObject.put("id", bundle.getString("id"));
            jsonObject.put("appeal", remark);
            for (int i = 0; i < size; i++) {
                JSONObject obj = new JSONObject();
                obj.put("id", idTokenInfo.list.get(i).id);
                obj.put("media_type", 1);
                jsonArray.put(obj);
            }
            jsonObject.put("media", jsonArray);
            StringEntity stringEntity = new StringEntity(jsonObject.toString(), "utf-8");
            AsyncHttpResponseHandler res = new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int i, Header[] headers, byte[] bytes) {
                    String str_json = new String(bytes);
                    try {
                        JSONObject obj = new JSONObject(str_json);
                        int code = obj.getInt("code");
                        if (code == 0) {
                            ToastUtil.shortshow(getApplicationContext(), "发表成功");
                            startActivity(RewardActivity.class);

                        } else {
                            errorCode(code);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                }
            };
            String url = ServerAPIConfig.Do_RewardOfflineAppeal;
            AndroidAsyncHttp.post(this, url, stringEntity, "application/json;charset=UTF-8", res);

        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
