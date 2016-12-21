package com.djx.pin.improve.positiveenergy.activity;

import com.baidu.location.BDLocation;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.djx.pin.R;
import com.djx.pin.application.PinApplication;
import com.djx.pin.beans.IDTokenInfo;
import com.djx.pin.beans.UserInfo;
import com.djx.pin.improve.ZhongMiAPI;
import com.djx.pin.utils.QiniuUtils;
import com.djx.pin.utils.ToastUtil;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.util.TextUtils;

/**
 * Created by 柯传奇 on 2016/12/8 0008.
 */
public class WishPublishPresenter{


    private final WishPublishActivity activity;
    private final BDLocation bdLocation;

    public WishPublishPresenter(WishPublishActivity activity) {
        PinApplication.getMyApp();
        bdLocation = PinApplication.getMyApp().getBdLocation();
        this.activity = activity;
        initLocation();
    }

    private void initLocation() {
        GeoCoder mSearch = GeoCoder.newInstance();
        mSearch.setOnGetGeoCodeResultListener(new OnGetGeoCoderResultListener() {
            @Override
            public void onGetGeoCodeResult(GeoCodeResult geoCodeResult) {
            }
            @Override
            public void onGetReverseGeoCodeResult(ReverseGeoCodeResult result) {
                if(result != null){
                    String currentCity = result.getAddressDetail().city;
                    activity.setLocation(currentCity + "  " + result.getAddressDetail().street
                            + "  " + result.getAddressDetail().streetNumber);
                    activity.initAddressInfo(currentCity);
                }else {
                    activity.setLocation("");
                }
            }
        });
        // 反Geo搜索
        mSearch.reverseGeoCode(new ReverseGeoCodeOption()
                .location(new LatLng(bdLocation.getLatitude(), bdLocation.getLongitude())));

    }

    public void sendData(final String content, final Double latitude, final Double longitude, final String location, ArrayList<String> img_paths) {
        if(TextUtils.isEmpty(content)){
            ToastUtil.shortshow(activity,activity.getString(R.string.content_wish_hint));
            return;
        }
        if(img_paths.size() == 0){
            postRequest2Server(content,latitude,longitude,location,null);
            return;
        }else {
            if(img_paths.size()>9){
                img_paths = (ArrayList<String>) img_paths.subList(0,9);
            }
            QiniuUtils.postRequestWithIMGS(activity, img_paths, UserInfo.getSessionID(activity), 1, new QiniuUtils.PostRequestWithPics() {
                @Override
                public void postRequest(IDTokenInfo idTokenInfo_Pic) {
                    postRequest2Server(content,latitude,longitude,location,idTokenInfo_Pic);
                }
            });
        }
    }

    private void postRequest2Server(String content, Double latitude,Double longitude,String location,IDTokenInfo idTokenInfo) {
        if(bdLocation == null){
            ToastUtil.shortshow(activity,"定位中");
            return;
        }
        ZhongMiAPI.postPublishCultureWall(activity, content,latitude,longitude,location,idTokenInfo, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String str_json = new String(responseBody);
                try {
                    JSONObject obj = new JSONObject(str_json);
                    int code = obj.getInt("code");
                    if (code == 0) {
                        ToastUtil.shortshow(activity, "发表成功");
                        activity.finish();
                    } else if(2113 == code){//含敏感词汇
                        ToastUtil.longshow(activity,activity.getString(R.string.sensitive_word));
                    }else {
                        ToastUtil.errorCode(activity,code);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                ToastUtil.shortshow(activity, "发表失败");
            }
        });
    }



}
