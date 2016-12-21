package com.djx.pin.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import com.djx.pin.R;
import com.djx.pin.adapter.DeclarationAdapter;

import com.djx.pin.application.PinApplication;
import com.djx.pin.base.OldBaseActivity;
import com.djx.pin.beans.AppealDetailEntity;
import com.djx.pin.beans.IDTokenInfo;
import com.djx.pin.beans.StaticBean;
import com.djx.pin.serverapiconfig.ServerAPIConfig;
import com.djx.pin.utils.AndroidAsyncHttp;
import com.djx.pin.utils.BitmapUtil;
import com.djx.pin.utils.LogUtil;
import com.djx.pin.utils.ToastUtil;
import com.djx.pin.widget.MyRecyclerView;
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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;
import io.rong.imkit.RongIM;

/**
 * Created by chuanqi on 2016/9/12 0012.
 */
public class DeclarationActivity extends OldBaseActivity implements View.OnClickListener{


    private SharedPreferences sp;
    private RequestQueue queue;
    private StringRequest request;
    private DeclarationAdapter declarationAdapter;
    private ArrayList<String> arrayList;
    private MyRecyclerView recyclerView;
    private ImageView iv_back;
    private RelativeLayout rl_arbitration;//仲裁
    private RelativeLayout rl_appeal;//申诉
    private RelativeLayout rl_communicate;//私信
    private RelativeLayout rl_customer_service_in;
    private EditText dialog_content;
    private ImageView iv_img01,iv_img02,iv_img03,iv_img04;
    private int iv_current_Position;
    private Dialog dialog_AddPic;
    private int curClickId;
    private View dialogView_AddPicVideo;
    private Button bt_camera,bt_photoalbum,bt_cancle;
    private BitmapUtil.SizeMessage sizeMessage;
    private Intent intent_Gallery_Pic;
    private Intent intent_Camera_Pic;
    private String[] path_Photo = {"","","",""};
    private String path_Camera,//使用相机拍照的图片绝对路径
            path_Camera_Video,//使用相机拍摄视屏的绝对路径
            dir_Camera,//使用相机拍照保存图片的文件夹路径
            videoPath;//用户举报的视屏绝对路径
    private boolean isPicFormat = false;//选择的图片格式是否正确
    private UploadManager upLoadManager;
    private String id;
    private String session_id;
    private String process_id;
    private AppealDetailEntity.ResultBean resultBean;
    private int flag =1;//标志当前是发单者还是接单者:1:发单者   2:接单者
    private String chat_nickname;//聊天对方的昵称
    private String chat_id;//聊天对方的id
    private int appeal_result;//申诉处理结果，0-未处理，1-发单者胜，2-抢单者胜
    private double price;
    private RelativeLayout rl_customer_excute_result;//有申诉结果时显示该view
    private TextView tv_result_time;
    private TextView tv_result_name;
    private TextView tv_result_reason;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_declaration);

        initView();

        initDialog();

        initData();


    }

    private void initView() {

        recyclerView = (MyRecyclerView)findViewById(R.id.rv_recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        iv_back = ((ImageView) findViewById(R.id.iv_title_back));
        rl_arbitration = ((RelativeLayout) findViewById(R.id.rl_navigation_01));
        rl_appeal = ((RelativeLayout) findViewById(R.id.rl_navigation_02));
        rl_communicate = ((RelativeLayout) findViewById(R.id.rl_navigation_03));
        rl_customer_service_in = ((RelativeLayout) findViewById(R.id.customer_service_in));
        //申诉结果
        rl_customer_excute_result = ((RelativeLayout) findViewById(R.id.customer_service_excute));
        tv_result_time = ((TextView) rl_customer_excute_result.findViewById(R.id.item_tv_time_declaration));
        tv_result_name = ((TextView) rl_customer_excute_result.findViewById(R.id.item_name_declaration));
        tv_result_reason = ((TextView) rl_customer_excute_result.findViewById(R.id.item_reason_declaration));

        iv_back.setOnClickListener(this);
        rl_appeal.setOnClickListener(this);
        rl_communicate.setOnClickListener(this);

        /**添加图片对话框相关控件*/
        dialogView_AddPicVideo = View.inflate(this,R.layout.layout_dialog_addpicvideo, null);

        bt_camera = (Button) dialogView_AddPicVideo.findViewById(R.id.bt_camera);
        bt_photoalbum = (Button) dialogView_AddPicVideo.findViewById(R.id.bt_photoalbum);
        bt_cancle = (Button) dialogView_AddPicVideo.findViewById(R.id.bt_cancle);
        bt_camera.setOnClickListener(this);
        bt_cancle.setOnClickListener(this);
        bt_photoalbum.setOnClickListener(this);

        sizeMessage = new BitmapUtil.SizeMessage(this, false, 60, 60);
        intent_Gallery_Pic = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent_Gallery_Pic.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        intent_Camera_Pic = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
    }




    private void initData(){

        sp = getSharedPreferences(StaticBean.USER_INFO, MODE_PRIVATE);
        session_id = sp.getString("session_id", null);
        id = getIntent().getExtras().getString("id");
        process_id = getIntent().getExtras().getString("process_id");
        flag = getIntent().getExtras().getInt("flag");

        upLoadManager = new UploadManager();

        StringBuffer sb = new StringBuffer(ServerAPIConfig.APPEAL_DETAIL);
        sb.append(session_id);
        sb.append("&process_id="+process_id);

        final String url = sb.toString();
        Log.i("test","求助申诉详情url："+url);

        queue = PinApplication.getMyApp().getQueue();

        request = new StringRequest(url, new Response.Listener<String>() {


            private long appeal_judge_time;

            @Override
            public void onResponse(String s) {
                if(s != null){
                    Gson gson = new Gson();
                    resultBean = gson.fromJson(s, AppealDetailEntity.class).getResult();
                    appeal_result = resultBean.getAppeal_result();
                    price = resultBean.getPrice();
                    appeal_judge_time = resultBean.getAppeal_judge_time();
                    declarationAdapter = new DeclarationAdapter(DeclarationActivity.this,resultBean);
                    recyclerView.setAdapter(declarationAdapter);
                    declarationAdapter.setItemIMGClickListener(new DeclarationAdapter.ItemIMGClickListener() {//item图片点击
                        @Override
                        public void onIMGClick(int pos) {
                            //TODO
                        }
                    });
                    if(appeal_result != 0){
                        rl_customer_excute_result.setVisibility(View.VISIBLE);
                        tv_result_name.setText("客服裁决,执行方案:");
                        Date d = new Date(appeal_judge_time);
                        SimpleDateFormat sdf = new SimpleDateFormat("MM-dd hh:mm");//"yyyy-MM-dd hh:mm:ss
                        tv_result_time.setText(sdf.format(d));//设置时间
                        if(1 == appeal_result){//1-发单者胜
                            tv_result_name.setText("发单者维权成功,接单者不会收到赏金");
                        }else if(2 == appeal_result){//2-抢单者胜
                            tv_result_name.setText("接单者维权成功,将得到"+price+"元");
                        }
                    }
                    rl_arbitration.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if(appeal_result == 0){
                                rl_customer_service_in.setVisibility(View.VISIBLE);//当还未审判时,让客服介入点击才有效
                            }else if(appeal_result == 1 || appeal_result == 2){
                                ToastUtil.longshow(DeclarationActivity.this,"审判已完成");
                            }
                        }
                    });

                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
            }
        });
        request.setTag("REQUEST");
        queue.add(request);
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
        queue.cancelAll("REQUEST");
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.iv_title_back://返回
                finish();
                break;

            case R.id.rl_navigation_02://申诉
                View view = View.inflate(this, R.layout.dialog_declaration, null);
                dialog_content = ((EditText) view.findViewById(R.id.et_content));
                iv_img01 = ((ImageView) view.findViewById(R.id.iv_img01));
                iv_img02 = ((ImageView) view.findViewById(R.id.iv_img02));
                iv_img03 = ((ImageView) view.findViewById(R.id.iv_img03));
                iv_img04 = ((ImageView) view.findViewById(R.id.iv_img04));
                iv_img01.setOnClickListener(this);
                iv_img02.setOnClickListener(this);
                iv_img03.setOnClickListener(this);
                iv_img04.setOnClickListener(this);
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setView(view)
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                String content = dialog_content.getText().toString().trim();
                                if (content.equals("") && content.length() == 0){
                                    Toast.makeText(DeclarationActivity.this, "吐槽内容不能为空", Toast.LENGTH_SHORT).show();
                                }else {//接单者申诉:

                                    getQiniu(id,content);
                                }
                            }
                        })
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        })
                        .show();

                break;

            case R.id.rl_navigation_03://私信

                //检查用户是否登录,未登录则return;已经实名认证的用户才可以举报
                if (getSharedPreferences(StaticBean.USER_INFO, MODE_PRIVATE).getBoolean("isLogined", false) == false) {
                    ToastUtil.shortshow(this, R.string.toast_non_login);
                    return;
                }
                if(resultBean != null){
                    if(flag == 1){
                        chat_id = resultBean.getReceiver_id();
                        chat_nickname = resultBean.getReceiver_nickname();
                    }else {
                        chat_id = resultBean.getPublisher_id();
                        chat_nickname = resultBean.getPublisher_nickname();
                    }
                    if (chat_id == null && chat_id.equals(sp.getString("user_id", null))) {//检查是否是同一用户
                        ToastUtil.shortshow(DeclarationActivity.this, R.string.toast_error_talk);
                        return;
                    }
                    //启动会话界面
                    if (RongIM.getInstance() != null)
                        startRecordByPermissions();//请求录音权限操作  ---> 获取成功后执行 excuteActionContainRecordPermision()

                }

                break;


            /**
             * 申诉内容添加图片
             */
            case R.id.iv_img01:
                iv_current_Position = 0;
                startReadSDCardByPermissions();
                break;
            case R.id.iv_img02:
                Toast.makeText(DeclarationActivity.this, "iv_img02", Toast.LENGTH_SHORT).show();
                iv_current_Position = 1;
                startReadSDCardByPermissions();
                break;
            case R.id.iv_img03:
                iv_current_Position = 2;
                startReadSDCardByPermissions();
                break;
            case R.id.iv_img04:
                iv_current_Position = 3;
                startReadSDCardByPermissions();
                break;
        }

    }

    @Override
    public void excuteActionContainRecordPermision() {
        RongIM.getInstance().startPrivateChat(DeclarationActivity.this, chat_id, chat_nickname);
    }

    private void initDialog() {
        /**添加图片对话框*/
        dialog_AddPic = new Dialog(this, R.style.dialog_transparent);
        View view = View.inflate(this, R.layout.layout_dialog_addpicvideo, null);
        dialog_AddPic.setContentView(view);
        WindowManager.LayoutParams layoutParams_AddPic = dialog_AddPic.getWindow().getAttributes();
        layoutParams_AddPic.gravity = Gravity.BOTTOM;
        layoutParams_AddPic.width = getWindowManager().getDefaultDisplay().getWidth();
        layoutParams_AddPic.y = 0;
        dialog_AddPic.getWindow().setAttributes(layoutParams_AddPic);
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
            startActivityForResult(intent_Camera_Pic, 100);
            dialog_AddPic.dismiss();
            curClickId = -1;
        }
    }

    /**
     * 选照片
     */
    @Override
    public void startReadSDCard() {
        intent_Gallery_Pic = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent_Gallery_Pic.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        startActivityForResult(intent_Gallery_Pic, 200);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        /**
         * 打开图库获取图片并添加到iv_AddPic上
         *获取图片并显示到对应的imageView上.
         *但是如果前面的imageview没有图片,将优先显示到前面的imageview上
         */
        if (requestCode == 200 && resultCode == Activity.RESULT_OK && null != data) {
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
                    iv_img01.setImageBitmap(BitmapUtil.loadBitmap(picturePath, sizeMessage));
                    break;
                case 1:
                    if (null == path_Photo[0] || 0 == path_Photo[0].length()) {
                        path_Photo[0] = picturePath;
                        iv_img01.setImageBitmap(BitmapUtil.loadBitmap(picturePath, sizeMessage));
                    } else {
                        path_Photo[1] = picturePath;
                        iv_img02.setImageBitmap(BitmapUtil.loadBitmap(picturePath, sizeMessage));
                    }
                    break;
                case 2:
                    if (null == path_Photo[0] || 0 == path_Photo[0].length()) {
                        path_Photo[0] = picturePath;
                        iv_img01.setImageBitmap(BitmapUtil.loadBitmap(picturePath, sizeMessage));
                    } else if (null == path_Photo[1] || 0 == path_Photo[1].length()) {
                        path_Photo[1] = picturePath;
                        iv_img02.setImageBitmap(BitmapUtil.loadBitmap(picturePath, sizeMessage));
                    } else {
                        path_Photo[2] = picturePath;
                        iv_img03.setImageBitmap(BitmapUtil.loadBitmap(picturePath, sizeMessage));
                    }
                    break;
                case 3:
                    if (null == path_Photo[0] || 0 == path_Photo[0].length()) {
                        path_Photo[0] = picturePath;
                        iv_img01.setImageBitmap(BitmapUtil.loadBitmap(picturePath, sizeMessage));
                    } else if (null == path_Photo[1] || 0 == path_Photo[1].length()) {
                        path_Photo[1] = picturePath;
                        iv_img02.setImageBitmap(BitmapUtil.loadBitmap(picturePath, sizeMessage));
                    } else if (null == path_Photo[2] || 0 == path_Photo[2].length()) {
                        path_Photo[2] = picturePath;
                        iv_img03.setImageBitmap(BitmapUtil.loadBitmap(picturePath, sizeMessage));
                    } else {
                        path_Photo[3] = picturePath;
                        iv_img04.setImageBitmap(BitmapUtil.loadBitmap(picturePath, sizeMessage));
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
        if (requestCode == 100 && resultCode == Activity.RESULT_OK) {
            switch (iv_current_Position) {
                case 0:
                    path_Photo[0] = path_Camera;
                    iv_img01.setImageBitmap(BitmapUtil.loadBitmap(path_Camera, sizeMessage));
                    break;
                case 1:
                    if (null == path_Photo[0] || 0 == path_Photo[0].length()) {
                        path_Photo[0] = path_Camera;
                        iv_img01.setImageBitmap(BitmapUtil.loadBitmap(path_Camera, sizeMessage));
                    } else {
                        path_Photo[1] = path_Camera;
                        iv_img02.setImageBitmap(BitmapUtil.loadBitmap(path_Camera, sizeMessage));
                    }
                    break;
                case 2:
                    if (null == path_Photo[0] || 0 == path_Photo[0].length()) {
                        path_Photo[0] = path_Camera;
                        iv_img01.setImageBitmap(BitmapUtil.loadBitmap(path_Camera, sizeMessage));
                    } else if (null == path_Photo[1] || 0 == path_Photo[1].length()) {
                        path_Photo[1] = path_Camera;
                        iv_img02.setImageBitmap(BitmapUtil.loadBitmap(path_Camera, sizeMessage));
                    }
                    else if (null == path_Photo[2] || 0 == path_Photo[2].length()) {
                        path_Photo[2] = path_Camera;
                        iv_img03.setImageBitmap(BitmapUtil.loadBitmap(path_Camera, sizeMessage));
                    }
                    else {
                        path_Photo[3] = path_Camera;
                        iv_img04.setImageBitmap(BitmapUtil.loadBitmap(path_Camera, sizeMessage));
                    }
                    break;

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
    int size = 0;
    int media_type = 1;
    IDTokenInfo idTokenInfo;


    public void getQiniu(final String id, final String content) {
        size = 0;
        if (session_id == null || session_id.equals("")) {
            ToastUtil.shortshow(getApplicationContext(), "请先登录应用");
            return;
        } else {
            for (int i = 0; i < 4; i++) {
                if (path_Photo[i] != null && path_Photo[i].length() > 1) {
                    size = size + 1;
                }
            }
            if (size == 0) {
                Log.i("result","此时size为0，进入SendRewardStop");
                SendRewardStop(session_id,id,content);
                return;
            }
            RequestParams params = new RequestParams();
            params.put("session_id", session_id);
            params.put("size", size);
            params.put("media_type", media_type);

            Log.i("result","session_id:"+session_id+"        size:"+size+"       media_type:"+media_type);

            AsyncHttpResponseHandler upHandler = new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int i, Header[] headers, byte[] bytes) {
                    String str_json = new String(bytes);
                    try {
                        JSONObject obj = new JSONObject(str_json);
                        if (obj.getInt("code") == 0) {
                            Gson gson = new Gson();
                            idTokenInfo = gson.fromJson(obj.getString("result").toString(), IDTokenInfo.class);
                            Log.i("result",obj.getString("result").toString());
                            upIamgeView(session_id,id,content);


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

    public void upIamgeView(final String session_id, final String id, final String content) {


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
                        Log.i("result","trueNumber == size");
                        SendRewardStop(session_id,id,content);

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
            upLoadManager.put(path_Photo[i], key, token, upComplete, null);
        }
    }

    private void SendRewardStop(String session_id,String id,String content) {

        JSONObject jsonObject = new JSONObject();
        JSONArray jsonArray = new JSONArray();
        try {
            jsonObject.put("session_id", session_id);
            jsonObject.put("id", id);
            jsonObject.put("appeal", content);

            Log.i("result","--------size:---"+size);
            for (int i = 0; i < size; i++) {
                List<IDTokenInfo.IDToken> idTokenList = idTokenInfo.list;
                Log.i("result",idTokenList.get(i).id+"----------:idTokenList.get(i).id");
                JSONObject obj = new JSONObject();
                obj.put("id", idTokenList.get(i).id);
                obj.put("media_type", 1);
                jsonArray.put(obj);
            }
            jsonObject.put("media", jsonArray);
            StringEntity stringEntity = new StringEntity(jsonObject.toString(), "utf-8");
            Log.e("stringEntity===", jsonObject.toString());

            AsyncHttpResponseHandler res = new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int i, Header[] headers, byte[] bytes) {
                    String str_json = new String(bytes);
                    try {
                        JSONObject obj = new JSONObject(str_json);
                        int code = obj.getInt("code");
                        if (code == 0) {
                            Log.i("result","发表成功");
                            ToastUtil.shortshow(getApplicationContext(), "发表成功");
                            finish();
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
        }
    }

}
