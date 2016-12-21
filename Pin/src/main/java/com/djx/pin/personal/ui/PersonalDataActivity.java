package com.djx.pin.personal.ui;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
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
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.moblie.zmxy.antgroup.creditsdk.app.CreditApp;
import com.android.moblie.zmxy.antgroup.creditsdk.app.ICreditListener;
import com.djx.pin.R;
import com.djx.pin.activity.PurseCreditSesameActivity;
import com.djx.pin.base.OldBaseActivity;
import com.djx.pin.beans.CityModel;
import com.djx.pin.beans.DistrictModel;
import com.djx.pin.beans.IDTokenInfo;
import com.djx.pin.beans.ProvinceModule;
import com.djx.pin.beans.StaticBean;
import com.djx.pin.beans.UserInfo;
import com.djx.pin.business.AppConstants;
import com.djx.pin.myview.CircleImageView;
import com.djx.pin.personal.RealNameVerified;
import com.djx.pin.personal.SesameAuthorizationActivity;
import com.djx.pin.serverapiconfig.ServerAPIConfig;
import com.djx.pin.ui.CommonDialog;
import com.djx.pin.utils.AndroidAsyncHttp;
import com.djx.pin.utils.CreditAuthHelper;
import com.djx.pin.utils.LogUtil;
import com.djx.pin.utils.QiniuUtils;
import com.djx.pin.utils.ScreenUtils;
import com.djx.pin.utils.ToastUtil;
import com.djx.pin.utils.myutils.LogicUtils;
import com.djx.pin.widget.OnWheelChangedListener;
import com.djx.pin.widget.WheelView;
import com.djx.pin.widget.adapters.ArrayWheelAdapter;
import com.djx.pin.xmlprase.XmlParserHandler;
import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.qiniu.android.http.ResponseInfo;
import com.qiniu.android.storage.UpCompletionHandler;
import com.qiniu.android.storage.UploadManager;

import org.json.JSONException;
import org.json.JSONObject;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import butterknife.ButterKnife;
import butterknife.OnClick;
import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;
import cz.msebera.android.httpclient.util.TextUtils;

/**
 * Created by Administrator on 2016/6/8.
 */
public class PersonalDataActivity extends OldBaseActivity implements View.OnClickListener, OnWheelChangedListener {
    protected final static String TAG = PersonalDataActivity.class.getSimpleName();

    private LinearLayout ll_PDA_Back, ll_PDA_Finish, ll_PDA_SetAvatar, ll_PDA_NickName, ll_PDA_Identity, ll_PDA_Sex, ll_PDA_Location;
    private CircleImageView cimg_PDA_ImgAvatar;
    private TextView tv_PDA_Identity, tv_PDA_Sex, tv_PDA_Location, tv_PDA_birthday;
    private EditText ed_PDA_NickName;
    private View v_PDA_ParentCover;
    private String userSex;
    private RelativeLayout ll_PDA_Birthday;


    //基本信息
    int is_auth;
    String real_name;
    String id_card;
    String nickname;
    String portrait;
    String birthday;
    String province;
    String city;
    String district;
    int gender;
    String picPath;
    private EditText et_wish;
    private String wish0;
    private TextView tv_ZhiMa_State;
    private RelativeLayout rl_CreditSesame_PA;


    @OnClick(R.id.rl_service)
    void onServiceClick(View view) {
        startActivity(ServiceActivity.class);
    }

    @OnClick(R.id.rl_wish)
    void onWishClick(View view) {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personaldata);
        ButterKnife.bind(this);
        initView();
        initEVent();
        initBaseData();
    }

    @Override
    protected void onDestroy() {
        ButterKnife.unbind(this);
        super.onDestroy();
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        UserInfo.getUserInfo(this, new UserInfo.GetUserInfoCallBack() {

            @Override
            public void callback(boolean state) {
                if (!state) {
                    ToastUtil.shortshow(PersonalDataActivity.this, "请求数据出错");
                }
                initBaseData();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        boolean is_identityChanged = getSharedPreferences(StaticBean.USER_INFO, Context.MODE_PRIVATE).getBoolean("is_identityChanged", false);
        if (is_identityChanged) {
            initBaseData();
        }
    }

    //修改头像时的key
    String key;

    private void initBaseData() {
        is_auth = getSharedPreferences(StaticBean.USER_INFO, Context.MODE_PRIVATE).getInt("is_auth", 0);
        gender = getSharedPreferences(StaticBean.USER_INFO, Context.MODE_PRIVATE).getInt("gender", 0);
        real_name = getSharedPreferences(StaticBean.USER_INFO, Context.MODE_PRIVATE).getString("real_name", null);
        id_card = getSharedPreferences(StaticBean.USER_INFO, Context.MODE_PRIVATE).getString("id_card", null);
        nickname = getSharedPreferences(StaticBean.USER_INFO, Context.MODE_PRIVATE).getString("nickname", null);
        portrait = getSharedPreferences(StaticBean.USER_INFO, Context.MODE_PRIVATE).getString("portrait", null);
        birthday = getSharedPreferences(StaticBean.USER_INFO, Context.MODE_PRIVATE).getString("birthday", null);
        province = getSharedPreferences(StaticBean.USER_INFO, Context.MODE_PRIVATE).getString("province", null);
        city = getSharedPreferences(StaticBean.USER_INFO, Context.MODE_PRIVATE).getString("city", null);
        district = getSharedPreferences(StaticBean.USER_INFO, Context.MODE_PRIVATE).getString("district", null);
        wish0 = getSharedPreferences(StaticBean.USER_INFO, Context.MODE_PRIVATE).getString("wish", null);
        session_id = UserInfo.getSessionID(this);

        key = getSharedPreferences(StaticBean.KEY, Context.MODE_PRIVATE).getString("key", null);

        QiniuUtils.setAvatarByIdFrom7Niu(this, cimg_PDA_ImgAvatar, portrait);

        ed_PDA_NickName.setText(nickname);
        et_wish.setText(wish0);

        if (gender == 0) {
            tv_PDA_Sex.setText("未知");
        } else if (gender == 1) {
            tv_PDA_Sex.setText("男");
        } else if (gender == 2) {
            tv_PDA_Sex.setText("女");
        }
        if (birthday == null || birthday.equals("")) {
            tv_PDA_birthday.setText("未知");
        } else {
            tv_PDA_birthday.setText(birthday);
        }
        if (province == null || province.equals("") || city == null || city.equals("") || district == null || district.equals("")) {
            tv_PDA_Location.setText("未知");
        } else {
            tv_PDA_Location.setText(province + city + district);
        }

        Log.e("sadagsdgfdg======", province + city + district + portrait);
        if (is_auth == 0) {
            if (real_name != null && !real_name.equals("") && id_card != null && !id_card.equals("")) {
                tv_PDA_Identity.setText(R.string.real_name_authentication_verifying);
            } else if (real_name.equals("") || real_name != null || !id_card.equals("") || id_card != null) {
                tv_PDA_Identity.setText(R.string.real_name_authentication_tobe_verify);
            }
        } else if (is_auth == 2) {
            tv_PDA_Identity.setText(R.string.real_name_authenticaton_not_pass);
        } else if (is_auth == 1) {
            tv_PDA_Identity.setText(R.string.real_name_authentication_ok);
        }
        Log.e("is_auth==", is_auth + "");
    }

    private void initEVent() {
        ll_PDA_Back.setOnClickListener(this);
        ll_PDA_Finish.setOnClickListener(this);
        ll_PDA_SetAvatar.setOnClickListener(this);
        ll_PDA_Identity.setOnClickListener(this);
        ll_PDA_Sex.setOnClickListener(this);
        ll_PDA_Location.setOnClickListener(this);
        ll_PDA_Birthday.setOnClickListener(this);
        rl_CreditSesame_PA.setOnClickListener(this);
    }

    private void initView() {
        ll_PDA_Back = (LinearLayout) findViewById(R.id.ll_PDA_Back);
        ll_PDA_Finish = (LinearLayout) findViewById(R.id.ll_PDA_Finish);
        ll_PDA_SetAvatar = (LinearLayout) findViewById(R.id.ll_PDA_SetAvatar);
        ll_PDA_NickName = (LinearLayout) findViewById(R.id.ll_PDA_NickName);
        ll_PDA_Identity = (LinearLayout) findViewById(R.id.ll_PDA_Identity);
        ll_PDA_Sex = (LinearLayout) findViewById(R.id.ll_PDA_Sex);
        ll_PDA_Location = (LinearLayout) findViewById(R.id.ll_PDA_Location);
        ll_PDA_Birthday = (RelativeLayout) findViewById(R.id.ll_PDA_Birthday);
        et_wish = ((EditText) findViewById(R.id.et_wish));


        cimg_PDA_ImgAvatar = (CircleImageView) findViewById(R.id.cimg_PDA_ImgAvatar);

        ed_PDA_NickName = (EditText) findViewById(R.id.ed_PDA_NickName);

        tv_PDA_Identity = (TextView) findViewById(R.id.tv_PDA_Identity);
        tv_PDA_Sex = (TextView) findViewById(R.id.tv_PDA_Sex);
        tv_PDA_Location = (TextView) findViewById(R.id.tv_PDA_Location);
        tv_PDA_birthday = (TextView) findViewById(R.id.tv_PDA_birthday);

        v_PDA_ParentCover = findViewById(R.id.v_PDA_ParentCover);
        tv_ZhiMa_State = (TextView) findViewById(R.id.tv_ZhiMa_State);
        if (!TextUtils.isEmpty(UserInfo.getZhiMaOpenID(this))) {
            if(UserInfo.getCredit(this) == -1){
                tv_ZhiMa_State.setText("未绑定");
            }else {
                tv_ZhiMa_State.setText("已绑定");
            }
        }else {
            tv_ZhiMa_State.setText("未绑定");
        }
        rl_CreditSesame_PA = (RelativeLayout) findViewById(R.id.rl_CreditSesame_PA);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.rl_CreditSesame_PA:
                String zhima_open_id = getSharedPreferences(StaticBean.USER_INFO, Context.MODE_PRIVATE).getString("zhima_open_id", null);
                if (TextUtils.isEmpty(zhima_open_id)) {
                    Log.e(TAG, "onClick:zhima_open_id" + zhima_open_id);
                    String mobile = getSharedPreferences(StaticBean.USER_INFO, Context.MODE_PRIVATE).getString("mobile", null);
                    if (mobile == null || mobile.equals("")) {
                        ToastUtil.shortshow(PersonalDataActivity.this, "请绑定手机号");
                    } else {
                        //跳转至芝麻信用授权展示界面
                        startActivity(SesameAuthorizationActivity.class);
                        //requestZhiMa();
                    }
                } else {
                    requestZhiMaScore(session_id, zhima_open_id);
                }
                break;
            case R.id.ll_PDA_Back:
                getSharedPreferences(StaticBean.KEY, Context.MODE_PRIVATE).edit().putString("key", "").commit();
                this.finish();
                break;
            case R.id.ll_PDA_Finish:
                SendNewData();
                break;
            case R.id.ll_PDA_SetAvatar:
                showPopwin(3);
//                startActivity(SetAvatarActivity.class);
//                this.finish();
                break;
            //设置生日
            case R.id.ll_PDA_Birthday:
                //showPopwin(3);
                showDialog(1);
                break;
            case R.id.ll_PDA_Identity:
                LogUtil.e("is_auth-----------" + is_auth);

                LogicUtils.realNameVerify2(this, new LogicUtils.AfterPassedListener() {
                    @Override
                    public void realNameVerifyPassed() {
                        startActivity(RealNameVerified.class);
                    }
                });
                break;
            //性别的弹窗
            case R.id.ll_PDA_Sex:
                userSex = tv_PDA_Sex.getText().toString();
                showPopwin(1);
                break;
            //籍贯的弹窗
            case R.id.ll_PDA_Location:
                showPopwin(2);
                break;
            //性别popwindow点击事件
            case R.id.tv_PDA_Sex_Women:
                tv_PDA_Sex.setText(tv_PDA_Sex_Women.getText().toString());
                popWindow.dismiss();
                break;
            case R.id.tv_PDA_Sex_Man:
                tv_PDA_Sex.setText(tv_PDA_Sex_Man.getText().toString());
                popWindow.dismiss();
                break;
            case R.id.tv_PDA_Sex_Cancel:

                tv_PDA_Sex.setText(userSex);
                popWindow.dismiss();
                break;
            //头像popwindow点击事件
            case R.id.tv_use_camera:
                startTakePhotoByPermissions();
                popWindow.dismiss();
                break;
            case R.id.tv_use_gallery:
                startReadSDCardByPermissions();
                popWindow.dismiss();
                break;
            case R.id.tv_avater_cancel:
                popWindow.dismiss();
                break;
            //籍贯popwindow点击事件
            case R.id.tv_PAD_Location_Cancel:
                popWindow.dismiss();
                break;
            case R.id.tv_PAD_Location_Yes:
                getSharedPreferences(StaticBean.USER_INFO, Context.MODE_PRIVATE).edit().putString("newProvince", mCurrentProviceName).commit();
                getSharedPreferences(StaticBean.USER_INFO, Context.MODE_PRIVATE).edit().putString("newCity", mCurrentCityName).commit();
                getSharedPreferences(StaticBean.USER_INFO, Context.MODE_PRIVATE).edit().putString("newDistrict", mCurrentDistrictName).commit();
                Log.i("test", "tv_PAD_Location_Yes确定按钮:  " + mCurrentDistrictName);
                tv_PDA_Location.setText(mCurrentProviceName + mCurrentCityName + mCurrentDistrictName);
                popWindow.dismiss();
                break;

            //生日选择(确定)
            case R.id.tv_PDA_Pop_Birthday_Yes:
                int moth = (dp_PDA_Pop_Birthday.getMonth() + 1);
                String smoth;
                if (moth < 10 && moth > 0) {
                    smoth = "0" + moth + "";
                } else {
                    smoth = moth + "";
                }
                int day = dp_PDA_Pop_Birthday.getDayOfMonth();
                String sday;
                if (day < 10 && day > 0) {
                    sday = "0" + day + "";
                } else {
                    sday = day + "";
                }
                //tv_PDA_birthday.setText(dp_PDA_Pop_Birthday.getYear() + "-" + smoth + "-" + sday);
                popWindow.dismiss();
                break;
        }
    }

    private PopupWindow popWindow;
    private View v;
    private TextView tv_PDA_Sex_Women, tv_PDA_Sex_Man, tv_PDA_Sex_Cancel, tv_PAD_Location_Cancel, tv_PAD_Location_Yes;
    private TextView tv_use_camera, tv_use_gallery, tv_avater_cancel;
    private WheelView wv_Province, wv_City, wv_Area;

    private void showPopwin(int j) {
        popWindow = new PopupWindow();
        popWindow.setWidth(ScreenUtils.getScreenWidth(this));
        popWindow.setHeight(ScreenUtils.getScreenHeight(this) / 3);
        //性别选择对话框
        if (j == 1) {
            v = LayoutInflater.from(this).inflate(R.layout.popupwindow_pda_sex, null);
            popSexView();
            popSexEvent();
            popWindow.setBackgroundDrawable(getResources().getDrawable(R.color.transparent));
            //地区选择对话框
        } else if (j == 2) {
            v = LayoutInflater.from(this).inflate(R.layout.popupwindow_pda_location, null);
            popWindow.setBackgroundDrawable(getResources().getDrawable(R.color.white));
            popLocationView();
            popLocationEvent();
            //头像选择对话框
        } else if (j == 3) {
            v = LayoutInflater.from(this).inflate(R.layout.popupwindow_pda_avater, null);
            popAvaterView();
            popAvaterEvent();
            popWindow.setBackgroundDrawable(getResources().getDrawable(R.color.transparent));
        }
        popWindow.setContentView(v);
        popWindow.setFocusable(true);
        popWindow.setOutsideTouchable(true);
        popWindow.setTouchable(true);
        if (j == 1) {
            popWindow.showAtLocation(ll_PDA_Sex, Gravity.BOTTOM | Gravity.CENTER, 0, 10);
        }
        if (j == 2) {
            popWindow.showAtLocation(ll_PDA_Sex, Gravity.BOTTOM | Gravity.CENTER, 0, 0);
        }
        if (j == 3) {
            popWindow.showAtLocation(ll_PDA_Sex, Gravity.BOTTOM | Gravity.CENTER, 0, 0);
        }
        parentCover(1);
        popWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                parentCover(2);
            }
        });

    }

    //监听器，用户监听用户点下DatePikerDialog的set按钮时，所设置的年月日
    DatePickerDialog.OnDateSetListener onDateSetListener = new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {

            tv_PDA_birthday.setText(year + "-" + (monthOfYear + 1) + "-" + dayOfMonth);
        }
    };

    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case 1:
                Calendar dateAndTime = Calendar.getInstance(Locale.CHINA);//? --获取一个日历对象；
                int v_year = dateAndTime.get(Calendar.YEAR);
                int v_month = dateAndTime.get(Calendar.MONTH);
                int v_day = dateAndTime.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog datePickerDialog = new DatePickerDialog(this, onDateSetListener, v_year, v_month, v_day);
                return datePickerDialog;
        }
        return null;
    }


    private TextView tv_PDA_Pop_Birthday_Yes;
    private DatePicker dp_PDA_Pop_Birthday;

    private void popBirthdayView() {
        tv_PDA_Pop_Birthday_Yes = (TextView) v.findViewById(R.id.tv_PDA_Pop_Birthday_Yes);
        dp_PDA_Pop_Birthday = (DatePicker) v.findViewById(R.id.dp_PDA_Pop_Birthday);

    }

    private void popBirthdayEvent() {
        tv_PDA_Pop_Birthday_Yes.setOnClickListener(this);
    }

    /**
     * 当前省的名称
     */
    private String mCurrentProviceName;
    /**
     * 当前市的名称
     */
    private String mCurrentCityName;
    /**
     * 当前区的名称
     */
    private String mCurrentDistrictName;

    /**
     * 省
     */
    private String[] provinceArray;
    /**
     * 省-市
     */
    private Map<String, String[]> citiesMap = new HashMap<String, String[]>();
    /**
     * 市-区县
     */
    private Map<String, String[]> areasMap = new HashMap<String, String[]>();

    private void popLocationEvent() {
        tv_PAD_Location_Cancel.setOnClickListener(this);
        tv_PAD_Location_Yes.setOnClickListener(this);
        initProvinceDatas();
        wv_Province.setViewAdapter(new ArrayWheelAdapter<String>(PersonalDataActivity.this, provinceArray));
        //设置可见条目数量
        wv_Province.setVisibleItems(4);
        wv_City.setVisibleItems(7);
        wv_Area.setVisibleItems(7);
        // 添加change事件
        wv_Province.addChangingListener(this);
        // 添加change事件
        wv_City.addChangingListener(this);
        // 添加change事件
        wv_Area.addChangingListener(this);
        updateCities();
        updateAreas();
    }

    private void popLocationView() {
        tv_PAD_Location_Cancel = (TextView) v.findViewById(R.id.tv_PAD_Location_Cancel);
        tv_PAD_Location_Yes = (TextView) v.findViewById(R.id.tv_PAD_Location_Yes);
        wv_Province = (WheelView) v.findViewById(R.id.wv_Province);
        wv_City = (WheelView) v.findViewById(R.id.wv_City);
        wv_Area = (WheelView) v.findViewById(R.id.wv_Area);
    }

    private void popSexView() {
        tv_PDA_Sex_Women = (TextView) v.findViewById(R.id.tv_PDA_Sex_Women);
        tv_PDA_Sex_Man = (TextView) v.findViewById(R.id.tv_PDA_Sex_Man);
        tv_PDA_Sex_Cancel = (TextView) v.findViewById(R.id.tv_PDA_Sex_Cancel);
    }

    private void popSexEvent() {
        tv_PDA_Sex_Women.setOnClickListener(this);
        tv_PDA_Sex_Man.setOnClickListener(this);
        tv_PDA_Sex_Cancel.setOnClickListener(this);
    }

    private void popAvaterView() {
        tv_avater_cancel = (TextView) v.findViewById(R.id.tv_avater_cancel);
        tv_use_camera = (TextView) v.findViewById(R.id.tv_use_camera);
        tv_use_gallery = (TextView) v.findViewById(R.id.tv_use_gallery);
    }

    private void popAvaterEvent() {
        tv_avater_cancel.setOnClickListener(this);
        tv_use_camera.setOnClickListener(this);
        tv_use_gallery.setOnClickListener(this);
    }

    public void parentCover(int i) {
        switch (i) {
            case 1:
                v_PDA_ParentCover.setVisibility(View.VISIBLE);
                v_PDA_ParentCover.setAlpha(0.3f);
                break;
            case 2:
                v_PDA_ParentCover.setVisibility(View.INVISIBLE);
                break;
        }
    }

    /**
     * 解析xml获取省市区
     */
    public void initProvinceDatas() {
        List<ProvinceModule> provinceList = null;
        AssetManager assets = getAssets();
        try {
            InputStream input = assets.open("province_data.xml");
            //创建一个解析XML工程的对象
            SAXParserFactory spf = SAXParserFactory.newInstance();
            //解析xml
            SAXParser parser = spf.newSAXParser();
            XmlParserHandler xph = new XmlParserHandler();
            parser.parse(input, xph);
            input.close();
            provinceList = xph.getDataList();
            //*/ 初始化默认选中的省、市、区
            if (provinceList != null && !provinceList.isEmpty()) {
                mCurrentProviceName = provinceList.get(0).getName();
                List<CityModel> cityList = provinceList.get(0).getCityList();
                if (cityList != null && !cityList.isEmpty()) {
                    mCurrentCityName = cityList.get(0).getName();
                    List<DistrictModel> districtList = cityList.get(0).getDistrictList();
                    mCurrentDistrictName = districtList.get(0).getName();
                }
            }
            provinceArray = new String[provinceList.size()];
            for (int i = 0; i < provinceList.size(); i++) {
                provinceArray[i] = provinceList.get(i).getName();
                List<CityModel> cityModelList = provinceList.get(i).getCityList();
                String[] cityName = new String[cityModelList.size()];
                for (int j = 0; j < cityModelList.size(); j++) {
                    cityName[j] = cityModelList.get(j).getName();
                    List<DistrictModel> districtModelList = cityModelList.get(j).getDistrictList();
                    String[] districName = new String[districtModelList.size()];
                    for (int k = 0; k < districtModelList.size(); k++) {
                        districName[k] = districtModelList.get(k).getName();
                    }
                    areasMap.put(cityName[j], districName);
                }
                citiesMap.put(provinceArray[i], cityName);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onChanged(WheelView wheel, int oldValue, int newValue) {
        switch (wheel.getId()) {
            case R.id.wv_Province:
                Log.i("test", "onChanged-----------------wv_Province");
                updateCities();
                break;
            case R.id.wv_City:
                Log.i("test", "onChanged-----------------wv_City");
                updateAreas();
                break;
            case R.id.wv_Area:
                mCurrentDistrictName = areasMap.get(mCurrentCityName)[newValue];
                Log.i("test", "onChanged-----------------wv_Area:" + areasMap.get(mCurrentCityName)[newValue]);
                break;
        }
    }

    /**
     * 根据当前的市，更新区WheelView的信息
     */
    private void updateAreas() {
        int pCurrent = wv_City.getCurrentItem();
        mCurrentCityName = citiesMap.get(mCurrentProviceName)[pCurrent];
        String[] areas = areasMap.get(mCurrentCityName);

        if (areas == null) {
            areas = new String[]{""};
        }
        wv_Area.setViewAdapter(new ArrayWheelAdapter<String>(this, areas));
        wv_Area.setCurrentItem(0);
        mCurrentDistrictName = areas[0];
        Log.i("test", "mCurrentDistrictName:------------" + areas[0]);
    }

    /**
     * 根据当前的省，更新市WheelView的信息
     */
    private void updateCities() {
        int pCurrent = wv_Province.getCurrentItem();
        mCurrentProviceName = provinceArray[pCurrent];
        String[] cities = citiesMap.get(mCurrentProviceName);
        if (cities == null) {
            cities = new String[]{""};
        }
        wv_City.setViewAdapter(new ArrayWheelAdapter<String>(this, cities));
        wv_City.setCurrentItem(0);
        updateAreas();
    }

    /**
     * {
     * "session_id": "8527a2b6a58b4fa963fd",
     * "nickname": "昵称",
     * "portrait": "13k12j3k12kl31l2",
     * "gender": 1,
     * "birthday": "1991-01-01"
     * "province": "江苏省",
     * "city": "南京市",
     * "district": "玄武区",
     * }
     * <p/>
     * 向服务器发送新资料
     */

    public void SendNewData() {

        String session_id = getSharedPreferences(StaticBean.USER_INFO, Context.MODE_PRIVATE).getString("session_id", null);
        if (session_id == null || session_id.equals("")) {
            ToastUtil.shortshow(getApplicationContext(), "请先登录应用");
            return;
        }
        {
            String newnickname = getedtStringAfterTrim(ed_PDA_NickName);
            String wish = getedtStringAfterTrim(et_wish);

            String newportrait;
            if (key == null || key.equals("")) {
                newportrait = portrait;
            } else {
                newportrait = key;
            }


            String sex = tv_PDA_Sex.getText().toString();
            int newgender = 0;
            if (sex.equals("未知")) {
                newgender = 0;
            } else if (sex.equals("男")) {
                newgender = 1;
            } else if (sex.equals("女")) {
                newgender = 2;
            }

            String newbirthday = tv_PDA_birthday.getText().toString();
            if (newbirthday == null || newbirthday.equals("")) {
                newbirthday = "";
            }

            String newprovince = getSharedPreferences(StaticBean.USER_INFO, Context.MODE_PRIVATE).getString("newProvince", null);
            String newcity = getSharedPreferences(StaticBean.USER_INFO, Context.MODE_PRIVATE).getString("newCity", null);
            String newdistrict = getSharedPreferences(StaticBean.USER_INFO, Context.MODE_PRIVATE).getString("newDistrict", null);

            if (newprovince == null || newprovince.equals("")) {
                newprovince = province;
            }
            if (newcity == null || newcity.equals("")) {
                newcity = city;
            }
            if (newdistrict == null || newdistrict.equals("")) {
                newdistrict = district;
            }
            String url = ServerAPIConfig.ChangeNewData;
            Log.e("location=======", newprovince + newcity + newdistrict);
            if (newgender == gender && newportrait.equals(portrait) && newprovince.equals(province) && newcity.equals(city) && newdistrict.equals(district) && newbirthday.equals(birthday)
                    && newnickname.equals(nickname) && wish.equals(wish0)) {
                ToastUtil.shortshow(getApplicationContext(), "内容并没有改变");
            } else {
                RequestParams params = new RequestParams();
                params.put("session_id", session_id);
                params.put("nickname", newnickname);
                params.put("portrait", newportrait);
                params.put("gender", newgender);
                params.put("birthday", newbirthday);
                params.put("province", newprovince);
                params.put("city", newcity);
                params.put("district", newdistrict);
                params.put("wish", wish);


                AsyncHttpResponseHandler upHandler = new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(int i, Header[] headers, byte[] bytes) {
                        String str = new String(bytes);
                        try {
                            JSONObject obj = new JSONObject(str);
                            int code = obj.getInt("code");
                            if (code == 0) {
                                ToastUtil.shortshow(getApplicationContext(), "资料修改成功");
                                getSharedPreferences(StaticBean.KEY, Context.MODE_PRIVATE).edit().putString("key", "").commit();
                                getSharedPreferences(StaticBean.KEY, Context.MODE_PRIVATE).edit().putBoolean("is_DataChanged", true).commit();
                                PersonalDataActivity.this.finish();
                                Intent intent = new Intent(AppConstants.INTENT_ACTION_MODIFY_USERINFO);
                                sendBroadcast(intent);
                            } else {
                                getSharedPreferences(StaticBean.KEY, Context.MODE_PRIVATE).edit().putBoolean("is_DataChanged", false).commit();
                                ToastUtil.shortshow(PersonalDataActivity.this, "修改资料失败");
                                errorCode(obj.getInt("code"));
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
        }
    }

    private File tempFile = new File(Environment.getExternalStorageDirectory(), getPhotoFileName());

    //子类重写该方法
    @Override
    public void startTakePhoto() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // 指定调用相机拍照后照片的储存路径
        intent.putExtra(MediaStore.EXTRA_OUTPUT,
                Uri.fromFile(tempFile));
        startActivityForResult(intent, 1);

        ToastUtil.shortshow(this, "正在打开相机");
    }

    @Override
    public void startReadSDCard() {
        Intent intent = new Intent();
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
                    cimg_PDA_ImgAvatar.setImageBitmap(bitmap);
                    getQiniu();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            } else if (requestCode == 1) {
                Uri uri = Uri.fromFile(tempFile);
                picPath = getPath(getApplicationContext(), uri);
                try {

                    Bitmap bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(uri));
                    cimg_PDA_ImgAvatar.setImageBitmap(bitmap);
                    getQiniu();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            } else {
                Log.e("芝麻授权", "onActivityResult");
                CreditApp.onActivityResult(requestCode, resultCode, data);
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
        Log.i(TAG, "进入getQiniu");

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
                    Log.i(TAG, "str_json=" + str_json);
                    try {
                        JSONObject obj = new JSONObject(str_json);
                        if (obj.getInt("code") == 0) {
                            Gson gson = new Gson();
                            idTokenInfo = gson.fromJson(obj.getString("result").toString(), IDTokenInfo.class);
                            if (picPath == null) {
                                ToastUtil.shortshow(getApplicationContext(), "头像并没有改变");
                            } else {
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
        }
    }

    /**
     * 七牛上传图片
     */
    public void upIamgeView() {

        Log.i(TAG, "进入 upIamgeView");
        final UploadManager upLoadManager = new UploadManager();
        for (int i = 0; i < size; i++) {

            UpCompletionHandler upComplete = new UpCompletionHandler() {
                @Override
                public void complete(String retkey, ResponseInfo responseInfo, JSONObject jsonObject) {
                    for (int j = 0; j < size; j++) {
                        if (retkey.equals(idTokenInfo.list.get(j).id)) {
                            if (responseInfo.isOK()) {
                                Log.i(TAG, "上传成功");
                                ToastUtil.shortshow(PersonalDataActivity.this, "头像上传成功");
                                key = retkey;
                                getSharedPreferences(StaticBean.KEY, Context.MODE_PRIVATE).edit().putString("key", key).commit();
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
            key = idTokenInfo.list.get(i).id;
            ToastUtil.shortshow(PersonalDataActivity.this, "正在上传头像，请等待");
            upLoadManager.put(picPath, key, token, upComplete, null);
        }

    }


    private void requestZhiMa() {
        Log.i(TAG, "---------------------requestZhiMa------------------");
        String url = ServerAPIConfig.ZhiMaXinYong;
        JSONObject obj = new JSONObject();
        JSONObject content = new JSONObject();
        JSONObject identity_param = new JSONObject();
        JSONObject biz_params = new JSONObject();
        try {
            obj.put("session_id", session_id);

            content.put("identity_type", 2);

            //身份证号码
            String certNo = getSharedPreferences(StaticBean.USER_INFO, Context.MODE_PRIVATE).getString("id_card", null);
            String certType = "IDENTITY_CARD";
            if (TextUtils.isEmpty(certNo)) {
                ToastUtil.shortshow(PersonalDataActivity.this, "请先实名制");
                return;
            }
            //真实姓名
            String name = getSharedPreferences(StaticBean.USER_INFO, Context.MODE_PRIVATE).getString("real_name", null);

            identity_param.put("certNo", certNo);
            identity_param.put("certType", certType);
            identity_param.put("name", name);


            biz_params.put("auth_code", "M_APPSDK");
            biz_params.put("state", "TEST");

            content.put("identity_param", identity_param);
            content.put("biz_params", biz_params);

            obj.put("content", content);

            StringEntity entity = new StringEntity(obj.toString(), "utf-8");


            AsyncHttpResponseHandler res = new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int i, Header[] headers, byte[] bytes) {
                    String str = new String(bytes);
                    Log.e(TAG, str);
                    try {
                        JSONObject object = new JSONObject(str);
                        Log.e(TAG, object.toString());
                        if (object.getInt("code") == 0) {
                            object = object.getJSONObject("result");
                            Log.e(TAG, object.toString());
                            String params = object.getString("cipher");
                            String sign = object.getString("sign");
                            Log.e(TAG, params);
                            Log.e(TAG, sign);
                            ZhiMaShouQuan(params, sign);
                        } else {
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
            AndroidAsyncHttp.post(this, url, entity, "application/json;charset=UTF-8", res);

        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 芝麻信用授权申请
     */
    private void ZhiMaShouQuan(final String params, String sign) {
        Log.i(TAG, "ZhiMaShouQuan");

        String appId = "1000590";
        CreditAuthHelper.creditAuth(this, appId, params, sign, null, new ICreditListener() {
            @Override
            public void onComplete(Bundle result) {
                //从result中获取params参数,然后解析params数据,可以获取open_id。
                if (result != null) {
                    LogUtil.e("--------------ZhiMaShouQuan----------------------");
                    ToastUtil.shortshow(getApplicationContext(), "授权成功");
                    String cipher = result.getString("params");
                    String sign = result.getString("sign");
                    requestZhiMaJieMi(cipher, sign);
                }
            }

            @Override
            public void onError(Bundle bundle) {
                ToastUtil.shortshow(getApplicationContext(), "授权错误");
            }

            @Override
            public void onCancel() {
                ToastUtil.shortshow(getApplicationContext(), "授权失败");
            }
        });
    }


    /***
     * 芝麻解密
     * <p/>
     * {
     * "session_id": "03194988f53372d77dfa",
     * "cipher": "REcL+bjqqTwcnFdd15Bl5F/2AQmvRBEaalgvx3gxW5Z5byNab9No1gY9dEomRdLq/JARG+2Alf8PzVyeuRR2pneWYN8hVZNPABSCiQIhPeu8M6mpGullbYi868OGgggJ9ub212ajm7cJWmaoLmq+fCs+eCmBBpvXzky2vs59w+FudRIQKp9CrXP7DO41rSfbi+jkoIRtxCj/N7M7VFHI9GQqMy8jhQoPmINVFDsCAhOjm3M9FuSHBPsCW9mDEOlEZaN46JY3VCupm2CxzcEZKtXjMvc1LxkbGdu3GC49DUfyYJHcYaKUqIqNjDtQfwBI6akigkXdA5LNWSSWwdWqeA==",
     * "sign": "Fd60dNrjplHOlZERjCpzwttdQLQF3rRbunwW95Y90n/x1nTjmfiK8WVkPnA7Zt0EEDeNxHKEYmZLKbF+Dsdxg5uX7N0fs4zIMfKnkhDj09lny5MTrmhQ+wPB256qNQ0ivHdbt6ezzOnhHy9HNmdr4KeoB6rac6QlKD7aVFoTDeg="
     * }
     */
    private void requestZhiMaJieMi(String cipher, String sign) {
        Log.i(TAG, "requestZhiMaJieMi");
        String url = ServerAPIConfig.ZhiMaXinYongDecrypt;
        RequestParams params = new RequestParams();
        params.put("session_id", session_id);
        params.put("cipher", cipher);
        params.put("sign", sign);

        AsyncHttpResponseHandler res = new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                String str = new String(bytes);
                Log.e("str=====", str);
                try {
                    JSONObject obj = new JSONObject(str);
                    if (obj.getInt("code") == 0) {
                        obj = obj.getJSONObject("result");

                        String content = obj.getString("content");

                        String[] result = content.split("[=,&]");
                        String open_id = result[1];
                        boolean sign_verify = obj.getBoolean("sign_verify");
                        if (sign_verify) {
                            String user_id = getSharedPreferences(StaticBean.USER_INFO, Context.MODE_PRIVATE).getString("user_id", null);
                            //芝麻信用授权成功之后,重新获取用户资料
                            requestZhiMaScore(session_id, open_id);
                            ifUpdateUserInfo = true;
                        } else {
                            ToastUtil.shortshow(getApplicationContext(), "芝麻信用open_id不匹配");
                        }
                        Log.i(TAG, result[1]);
                    } else {
                        errorCode(obj.getInt("code"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }


            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {

            }
        };
        AndroidAsyncHttp.post(url, params, res);

    }

    private boolean ifUpdateUserInfo = false;

    /**
     * 请求芝麻信用分
     * {
     * "session_id": "8527a2b6a58b4fa963fd",
     * "open_id": "123451858416405071136980128",
     * }
     */
    private void requestZhiMaScore(String session_id, String open_id) {
        Log.i(TAG, "requestZhiMaScore");

        String url = ServerAPIConfig.ZhiMaXinYongScore;

        RequestParams params = new RequestParams();
        params.put("session_id", session_id);
        params.put("open_id", open_id);
        AsyncHttpResponseHandler res = new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                String string = new String(bytes);
                try {
                    String session_id = UserInfo.getSessionID(PersonalDataActivity.this);
                    String user_id;
                    user_id = UserInfo.getUserID(PersonalDataActivity.this);
                    UserInfo.getUserInfo(getApplicationContext(), session_id, user_id);
                    String zhima_open_id = UserInfo.getZhiMaOpenID(getApplicationContext());
                    if (zhima_open_id == null) {
                        tv_ZhiMa_State.setText("未绑定");
                    } else {
                        tv_ZhiMa_State.setText("已绑定");
                    }
                    JSONObject object = new JSONObject(string);
                    if (object.getInt("code") == 0) {
                        object = object.getJSONObject("result");
                        int score = object.getInt("zhima_score");
                        Bundle bundle = new Bundle();
                        bundle.putInt("score", score);
                        startActivity(PurseCreditSesameActivity.class, bundle);
                    } else {
                        Log.i(TAG, "requestZhiMaScore: ret is " + object.getInt("code"));
                        ToastUtil.shortshow(getApplicationContext(), "获取芝麻分数失败， 您可能已解除绑定");
                        //将用户解除绑定信息推送给后台
                        pushUnbindToServer();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {

            }
        };
        AndroidAsyncHttp.post(url, params, res);
    }

    private void pushUnbindToServer() {
        String url = ServerAPIConfig.ZhiMaXinYongScore;

        RequestParams params = new RequestParams();
        params.put("session_id", session_id);
        params.put("open_id", "");
        AsyncHttpResponseHandler res = new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                String string = new String(bytes);
                try {
                    JSONObject object = new JSONObject(string);
                    if (object.getInt("code") == 0) {
                        Log.i(TAG, "push unbind into to server OK");
                        UserInfo.setZhiMaOpenID(PersonalDataActivity.this, "");
                        String zhima_open_id = UserInfo.getZhiMaOpenID(getApplicationContext());
                        if (zhima_open_id == null) {
                            tv_ZhiMa_State.setText("未绑定");
                        } else {
                            tv_ZhiMa_State.setText("已绑定");
                        }
                        rebindZhiMa();
                    } else {
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
        AndroidAsyncHttp.post(url, params, res);
    }

    private void rebindZhiMa() {
        Button.OnClickListener positiveListener = new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                requestZhiMa();
            }
        };
        CommonDialog.show(PersonalDataActivity.this, "确定", "取消", "现在重新绑定芝麻信用？", positiveListener);
    }

}
