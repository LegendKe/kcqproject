package com.djx.pin.improve.helpmap.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.djx.pin.R;
import com.djx.pin.activity.PayTypeActivity;
import com.djx.pin.beans.IDTokenInfo;
import com.djx.pin.beans.StaticBean;
import com.djx.pin.beans.UserInfo;
import com.djx.pin.improve.ZhongMiAPI;
import com.djx.pin.improve.base.fragment.BasePermissionFragment;
import com.djx.pin.improve.helpmap.activity.HelpPublishOfflineActivity;
import com.djx.pin.improve.helpmap.adapter.AddPicAdapter;
import com.djx.pin.improve.utils.DialogUtils;
import com.djx.pin.utils.DateUtils;
import com.djx.pin.utils.LogUtil;
import com.djx.pin.utils.QiniuUtils;
import com.djx.pin.utils.ToastUtil;
import com.djx.pin.utils.myutils.ConstantUtils;
import com.foamtrace.photopicker.PhotoPickerActivity;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;
import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.util.TextUtils;

/**
 * Created by 柯传奇 on 2016/11/28 0028.
 */
public class HelpPublishFragment extends BasePermissionFragment{
  
    @Bind(R.id.et_money)
    EditText et_money;
    @Bind(R.id.et_people_number)
    EditText et_people_number;
    @Bind(R.id.tv_start_time)
    TextView tv_start_time;
    @Bind(R.id.tv_end_time)
    TextView tv_end_time;
    @Bind(R.id.rg_help_type)
    RadioGroup rg_help_type;
    @Bind(R.id.et_content)
    EditText et_content;
    @Bind(R.id.recyclerView)
    RecyclerView recyclerView;
    @Bind(R.id.ll_ordertype)
    LinearLayout ll_ordertype;

    private List<String> img_paths;//选择的图片路径
    private AddPicAdapter adapter;
    private String help_type;//网络悬赏,线下悬赏 区分
    private int flag;//标志开始时间还是结束时间
    private long start_time;
    private long end_time;


    @Override
    protected int setLayoutId() {
        return R.layout.fragment_publish_help;
    }

    @Override
    protected void initView() {
        recyclerView.setHasFixedSize(true);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(context, 3,GridLayoutManager.VERTICAL,false);
        recyclerView.setLayoutManager(gridLayoutManager);
        adapter = new AddPicAdapter(context);
        recyclerView.setAdapter(adapter);
        img_paths = new ArrayList<>();
        img_paths.clear();
        if(help_type == ConstantUtils.HELP_OFFLINE){
            ll_ordertype.setVisibility(View.GONE);
        }
        tv_start_time.setText(DateUtils.formatDate(new Date(System.currentTimeMillis()), DateUtils.yyyyMMddHHmm));
        tv_end_time.setText(DateUtils.formatDate(new Date(System.currentTimeMillis() +86400000), DateUtils.yyyyMMddHHmm));
    }

    @Override
    protected void initEvent() {
        adapter.setAddPidListener(new AddPicAdapter.AddPicListener() {
            @Override
            public void addPic() {
                /**添加图片对话框相关控件*/
                DialogUtils.AddImageDialog(context, new DialogUtils.SlectAlbumListener() {
                    @Override
                    public void onClick() {//相册
                        requestPermissionsStorage();
                    }
                }, new DialogUtils.SlectCameraListener() {
                    @Override
                    public void onClick() {//相机
                        startTakePhotoByPermissions();
                    }
                });
            }
        });
        adapter.setDeletePicListener(new AddPicAdapter.DeletePicListener() {
            @Override
            public void deletePic(int pos) {
                img_paths.remove(pos);
                imagePaths.remove(pos);
            }
        });
        et_content.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // 解决scrollView中嵌套EditText导致不能上下滑动的问题
                v.getParent().requestDisallowInterceptTouchEvent(true);
                switch (event.getAction() & MotionEvent.ACTION_MASK) {
                    case MotionEvent.ACTION_UP:
                        v.getParent().requestDisallowInterceptTouchEvent(false);
                        break;
                }
                return false;
            }
        });
    }

    @OnClick(R.id.btn_send)//发送
    public void send(View v){
        if(!checkUserInfo()){
            return;
        }
        if(img_paths.size() == 0){
            LogUtil.e("----img_paths.size() == 0");
            postRequest2Server(null);
            return;
        }else {
            if(img_paths.size()>9){
                img_paths = img_paths.subList(0,9);
            }
            QiniuUtils.postRequestWithIMGS(context, img_paths, UserInfo.getSessionID(context), 1, new QiniuUtils.PostRequestWithPics() {
                @Override
                public void postRequest(IDTokenInfo idTokenInfo_Pic) {
                    postRequest2Server(idTokenInfo_Pic);
                }
            });
        }
    }

    @OnClick(R.id.tv_end_time)
    public void selectEndTime(View v){
        DialogUtils.selectTimeDialog(context, new DialogUtils.OnTimeSlectedListener() {
            @Override
            public void timeSelected(int year, int month, int day, int hour, int min) {
                tv_end_time.setText(year + "-" + month + "-" + day + "  "+hour+":"+min);
            }
        });
    }
    @OnClick(R.id.tv_start_time)
    public void selectStartTime(View v){
        DialogUtils.selectTimeDialog(context, new DialogUtils.OnTimeSlectedListener() {
            @Override
            public void timeSelected(int year, int month, int day, int hour, int min) {
                tv_start_time.setText(year + "-" + month + "-" + day + "  "+hour+":"+min);
            }
        });
    }

    @Override
    protected void initBundle(Bundle bundle) {
        help_type = bundle.getString("HELP_TYPE");
    }

    private boolean checkUserInfo() {
        //检查金额是否为空
        String money = et_money.getText().toString().trim();
        if (TextUtils.isEmpty(money)) {
            ToastUtil.shortshow(context, R.string.toast_et_monery_err);
            return false;
        }
        //检查需要人手是否为空
        String people = et_people_number.getText().toString().trim();
        if (TextUtils.isEmpty(people)) {
            ToastUtil.shortshow(context, R.string.toast_non_et_people_number);
            return false;
        }
        //悬赏事项
        String content = et_content.getText().toString().trim();
        if(TextUtils.isEmpty(content)){
            ToastUtil.shortshow(context, "请输入悬赏内容");
            return false;
        }
        start_time = DateUtils.parseDate(tv_start_time.getText().toString(), DateUtils.yyyyMMddHHmm).getTime();
        end_time = DateUtils.parseDate(tv_end_time.getText().toString(), DateUtils.yyyyMMddHHmm).getTime();

        LogUtil.e("start_time:"+start_time+"-----------end_time:"+end_time);
        if(end_time <= start_time){
            ToastUtil.shortshow(context,"结束时间必须大于开始时间");
            return false;
        }
        return true;
    }

    private void postRequest2Server(IDTokenInfo idTokenInfo){
        switch (rg_help_type.getCheckedRadioButtonId()) {
            case R.id.rb_photo:
                postData(1,idTokenInfo);//图片
                break;
            case R.id.rb_video:
                postData(2,idTokenInfo);//视频
                break;
            case R.id.rb_other:
                postData(3,idTokenInfo);//其它
                break;
        }
    }
    private void postData(int order_type, IDTokenInfo idTokenInfo) {
        double money = Double.parseDouble(et_money.getText().toString());
        String content = et_content.getText().toString().trim();
        int peopleNum = Integer.parseInt(et_people_number.getText().toString());


        switch (help_type){
            case ConstantUtils.HELP_ONLINE://发布网络悬赏
                LogUtil.e("----------------money:"+money+"-------------peopleNum:"+peopleNum);
                ZhongMiAPI.postPublishOnlineHelp(context, UserInfo.getSessionID(context),money,content,peopleNum,start_time,end_time,order_type,idTokenInfo,mHandler);
                break;
            case ConstantUtils.HELP_OFFLINE://发布线下悬赏
                HelpPublishOfflineActivity publishOfflineActivity = (HelpPublishOfflineActivity) getActivity();
                String address = publishOfflineActivity.tv_location.getText().toString().trim();
                ZhongMiAPI.postPublishOfflineHelp(context, UserInfo.getSessionID(context),money,content,peopleNum,start_time,end_time,publishOfflineActivity.currentProvince,publishOfflineActivity.currentCity,publishOfflineActivity.currentDistrict, address,publishOfflineActivity.currentlatitude,publishOfflineActivity.currentlongitude,idTokenInfo,mHandler);
                break;
        }
    }

    protected AsyncHttpResponseHandler mHandler = new AsyncHttpResponseHandler() {
        @Override
        public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
            String str_json = new String(responseBody);
            JSONObject jsonObject = null;
            LogUtil.e("str_json 支付信息=" + str_json);
            try {
                JSONObject obj = new JSONObject(str_json);
                if (obj.getInt("code") == 0) {
                    obj = obj.getJSONObject("result");
                    Bundle bundle = new Bundle();
                    bundle.putString("id", obj.getString("id"));
                    if(help_type == ConstantUtils.HELP_OFFLINE){
                        bundle.putInt("type", 1);
                    }else {
                        bundle.putInt("type", 7);
                    }
                    double amount = obj.getDouble("amount");
                    BigDecimal val = new BigDecimal(String.valueOf(amount));
                    bundle.putFloat("amount", val.floatValue());
                    Intent intent = new Intent(context,PayTypeActivity.class);
                    intent.putExtras(bundle);
                    startActivity(intent);

                } else if(2113 == obj.getInt("code")){
                    ToastUtil.longshow(context,R.string.sensitive_word);
                }else {
                    ToastUtil.shortshow(context, "上传信息异常");
                    ToastUtil.errorCode(context,obj.getInt("code"));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        @Override
        public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

        }
    };


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        LogUtil.e("onActivityResult"+requestCode);
        if (resultCode == Activity.RESULT_OK ) {
            switch (requestCode) {
                case ConstantUtils.INTENT_IMAGE_CAPTURE:
                    LogUtil.e("拍照onActivityResult---path_Camera-----------:"+path_Camera);
                    img_paths.add(path_Camera);
                    imagePaths.add(path_Camera);
                    adapter.addData(path_Camera);
                    break;

                case ConstantUtils.INTENT_READ_ALBUM:
                    LogUtil.e("相册onActivityResult---photos-----------");
                    if(null != data){
                        ArrayList<String> photos = data.getStringArrayListExtra(PhotoPickerActivity.EXTRA_RESULT);
                        img_paths.clear();
                        img_paths.addAll(photos);
                        adapter.addAll(img_paths);
                        imagePaths.clear();
                        imagePaths.addAll(img_paths);
                    }
                    break;
            }
        }

    }

    public boolean checkImageFormal(String picturePath){
        String picFormat = picturePath.substring(picturePath.lastIndexOf("."));
        boolean isPicFormat = false;
        for (int i = 0; i < StaticBean.PIC_FORMAT.length; i++) {
            if (StaticBean.PIC_FORMAT[i].equals(picFormat)) {
                isPicFormat = true;
            }
        }
        if (!isPicFormat) {
            Toast.makeText(context, R.string.toast_pic_format_error, Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }


}
