package com.djx.pin.improve;

import android.content.Context;
import android.util.Log;

import com.baidu.location.BDLocation;
import com.djx.pin.application.PinApplication;
import com.djx.pin.beans.IDTokenInfo;
import com.djx.pin.beans.UserInfo;
import com.djx.pin.serverapiconfig.ServerAPIConfig;
import com.djx.pin.utils.AndroidAsyncHttp;
import com.djx.pin.utils.ToastUtil;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.entity.StringEntity;
import cz.msebera.android.httpclient.util.TextUtils;

/**
 * Created by 柯传奇 on 2016/11/17 0017.
 */
public class ZhongMiAPI {


    public static final int PAGE_SIZE = 10;
    public static final int PAGE_SIZE2 = 50;

    /**
     * main-1:丢失儿童列表
     * @param context
     * @param index 分页页码，从0开始取
     * @param handler
     */
    public static void getLostChildList(Context context , int index, AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        String sessionID = UserInfo.getSessionID(context);
        params.put("session_id", sessionID);
        params.put("index", index);
        params.put("size", PAGE_SIZE);
        AndroidAsyncHttp.get(ServerAPIConfig.LostChildList, params, handler);
    }

    /**
     *
     * @param context
     * @param index
     * @param latitude
     * @param longitude
     * @param handler
     */
    public static void getCultureWallList(Context context , int index,Double latitude,Double longitude, AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        String sessionID = UserInfo.getSessionID(context);
        if(sessionID != null){
            params.put("session_id", sessionID);//用户会话id，当type=2时可选
        }
        params.put("index", index);
        params.put("size", PAGE_SIZE2);
        params.put("type", 2);//类型，1-我发布的，2-附近的
        params.put("latitude", latitude);
        params.put("longitude", longitude);
        AndroidAsyncHttp.get(ServerAPIConfig.LookCivilization, params, handler);
    }
    /**
     * 查看求助详情 .已登录,有会话id
     * @param session_id 用户会话id，可选
     * @param id  求助id
     * @param index 分页页码
     * @param type  类型，1-分享列表，2-评论列表
     * @param handler
     */
    public static void getHelpDetail(String session_id, String id , int index, int type, AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("session_id", session_id);
        params.put("id", id);
        params.put("index", index);
        params.put("type", type);
        params.put("size", PAGE_SIZE);
        AndroidAsyncHttp.get(ServerAPIConfig.Get_HelpDetail, params, handler);
    }
    /**
     * 查看求助详情 .无登录
     * @param id  求助id
     * @param index 分页页码
     * @param type  类型，1-分享列表，2-评论列表
     * @param handler
     */
    public static void getHelpDetail(String id , int index, int type, AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("id", id);
        params.put("index", index);
        params.put("type", type);
        params.put("size", PAGE_SIZE);
        AndroidAsyncHttp.get(ServerAPIConfig.Get_HelpDetail, params, handler);
    }

    /**
     * 网络悬赏详情 .已登录,有会话id
     * @param id  悬赏id
     * @param index 分页页码
     * @param type  类型，1-分享列表，2-评论列表，3-所有接单者列表，4-查看自己抢单
     * @param handler
     */
    public static void getOnlineRewardDetail(String id , int index, int type, AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("id", id);
        params.put("index", index);
        params.put("type", type);
        params.put("size", PAGE_SIZE);
        AndroidAsyncHttp.get(ServerAPIConfig.OnlineReward, params, handler);
    }

    /**
     * 网络悬赏详情 .无登录
     * @param session_id 用户会话id，可选
     * @param id  悬赏id
     * @param index 分页页码
     * @param type  类型，1-分享列表，2-评论列表，3-所有接单者列表，4-查看自己抢单
     * @param handler
     */
    public static void getOnlineRewardDetail(String session_id, String id , int index, int type, AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("session_id", session_id);
        params.put("id", id);
        params.put("index", index);
        params.put("type", type);
        params.put("size", PAGE_SIZE);
        AndroidAsyncHttp.get(ServerAPIConfig.OnlineReward, params, handler);
    }
    /**
     * 我的求助的详情页
     * @param session_id
     * @param id
     * @param index
     * @param type  类型，1-分享列表，2-评论列表，3-所有接单者列表，4-查看自己抢单
     * @param handler
     */
    public static void getHelpMeDetail(String session_id,String id , int index, int type, AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("session_id", session_id);
        params.put("id", id);
        params.put("index", index);
        params.put("type", type);
        params.put("size", PAGE_SIZE);
        AndroidAsyncHttp.get(ServerAPIConfig.OnlineReward, params, handler);
    }




    /**
     * 文明墙详情页
     * @param id
     * @param index
     * @param type  类型，1-分享列表，2-评论列表
     * @param handler
     */
    public static void getCultureWallDetail(Context context,String id , int index, int type, AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("session_id", UserInfo.getSessionID(context));
        params.put("id", id);
        params.put("index", index);
        params.put("type", type);
        params.put("size", PAGE_SIZE);
        AndroidAsyncHttp.get(ServerAPIConfig.LookCivilizationDetail, params, handler);
    }

    /**
     * 文明墙发布
     * @param context
     * @param description
     * @param location  地点
     * @param idTokenInfo
     * @param handler
     */
    public static void postPublishCultureWall(Context context,String description,Double latitude,Double longitude, String location,IDTokenInfo idTokenInfo, AsyncHttpResponseHandler handler) {
        BDLocation bdLocation = PinApplication.getMyApp().getBdLocation();
        JSONObject jsonObject = new JSONObject();
        JSONArray jsonArray =  new JSONArray();
        StringEntity stringEntity = null;
        try {
            jsonObject.put("session_id", UserInfo.getSessionID(context));
            jsonObject.put("latitude", latitude);
            jsonObject.put("longitude", longitude);
            jsonObject.put("location", location);
            jsonObject.put("description", description);

            if(idTokenInfo != null){
                for (int i = 0; i < idTokenInfo.list.size(); i++) {
                    JSONObject obj = new JSONObject();
                    obj.put("id", idTokenInfo.list.get(i).id);
                    obj.put("media_type", 1);
                    jsonArray.put(obj);
                }
            }
            jsonObject.put("media", jsonArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        stringEntity = new StringEntity(jsonObject.toString(), "UTF-8");
        AndroidAsyncHttp.post(context,ServerAPIConfig.SendCivilizationMassage, stringEntity,"application/json;charset=UTF-8", handler);
    }



    /**
     *
     * @param context
     * @param session_id
     * @param price
     * @param description
     * @param receiver_limit
     * @param start_time
     * @param end_time
     * @param type  //类型，1-照片悬赏，2-视频悬赏，3-文字悬赏（其他悬赏）
     * @param idTokenInfo //七牛图片id
     * @param handler
     */
    public static void postPublishOnlineHelp(Context context,String session_id, double price , String description, int receiver_limit, long start_time, long end_time, int type, IDTokenInfo idTokenInfo, AsyncHttpResponseHandler handler) {
        JSONObject jsonObject = new JSONObject();
        JSONArray jsonArray =  new JSONArray();
        StringEntity stringEntity = null;
        try {
            jsonObject.put("session_id", session_id);
            jsonObject.put("price", price);
            jsonObject.put("description", description);
            jsonObject.put("receiver_limit", receiver_limit);
            jsonObject.put("start_time", start_time);
            jsonObject.put("end_time", end_time);
            jsonObject.put("type", type);

            if(idTokenInfo != null){
                for (int i = 0; i < idTokenInfo.list.size(); i++) {
                    JSONObject obj = new JSONObject();
                    obj.put("id", idTokenInfo.list.get(i).id);
                    obj.put("media_type", 1);
                    jsonArray.put(obj);
                }
            }
            jsonObject.put("media", jsonArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        stringEntity = new StringEntity(jsonObject.toString(), "UTF-8");
        AndroidAsyncHttp.post(context,ServerAPIConfig.Updata_Help_Net, stringEntity,"application/json;charset=UTF-8", handler);
    }



    public static void postPublishOfflineHelp(Context context,String session_id, double price , String description, int receiver_limit, long start_time, long end_time, String province,String city
            ,String district,String address,Double latitude,Double longitude, IDTokenInfo idTokenInfo, AsyncHttpResponseHandler handler) {
        JSONObject jsonObject = new JSONObject();
        JSONArray jsonArray = new JSONArray();
        StringEntity stringEntity = null;
        try {
            jsonObject.put("session_id", session_id);
            jsonObject.put("price", price);
            jsonObject.put("receiver_limit", receiver_limit);
            jsonObject.put("description", description);
            jsonObject.put("start_time", start_time);
            jsonObject.put("end_time", end_time);
            jsonObject.put("province", province);
            jsonObject.put("city", city);
            jsonObject.put("district", district);
            jsonObject.put("address", address);
            jsonObject.put("latitude", latitude);
            jsonObject.put("longitude", longitude);
            if (idTokenInfo != null) {
                for (int i = 0; i < idTokenInfo.list.size(); i++) {
                    JSONObject obj = new JSONObject();
                    obj.put("id", idTokenInfo.list.get(i).id);
                    obj.put("media_type", 1);
                    jsonArray.put(obj);
                }
            }
            jsonObject.put("media", jsonArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        stringEntity = new StringEntity(jsonObject.toString(), "UTF-8");
        AndroidAsyncHttp.post(context,ServerAPIConfig.Updata_Help, stringEntity,"application/json;charset=UTF-8", handler);
    }

    /**
     *
     * @param context
     * @param pageIndex 分页页码，从0开始取
     * @param price_min
     * @param price_max
     * @param distance 距离，0-不设限，1-1KM，2-2km，3-3km，4-5km，5-10km，6-50km
     * @param order 排序规则，0-无（默认按发布时间降序），1-最热（按分享+评论数降序），2-最新(按开始时间降序)，3-按金额升序，4-按金额降序
     * @param handler
     */
    public static void getHelpPeopleOfflineList(Context context,Double latitude,Double longitude,int pageIndex, int price_min,int price_max,
                                                int distance,int order,AsyncHttpResponseHandler handler) {

        BDLocation bdLocation = PinApplication.getMyApp().getBdLocation();
        RequestParams params = new RequestParams();
        String sessionID = UserInfo.getSessionID(context);
        if(!TextUtils.isEmpty(sessionID)){
            params.put("session_id", sessionID);
        }
        params.put("index", pageIndex);
        params.put("size", PAGE_SIZE);
        params.put("latitude", latitude);
        params.put("longitude", longitude);

        params.put("price_min", price_min);
        params.put("price_max", price_max);
        params.put("distance", distance);
        params.put("order", order);

        AndroidAsyncHttp.get(ServerAPIConfig.Get_HelpList, params, handler);
    }


    public static void getHelpPeopleSOSList(Context context,AsyncHttpResponseHandler handler) {

        BDLocation bdLocation = PinApplication.getMyApp().getBdLocation();
        RequestParams params = new RequestParams();
        String sessionID = UserInfo.getSessionID(context);
        if(!TextUtils.isEmpty(sessionID)){
            params.put("session_id", sessionID);
        }
        params.put("size", 100);//默认一次性拉取(后期如果用户量非常庞大时,建议将SOS分离出去)
        params.put("index", 0);
        params.put("latitude", bdLocation.getLatitude());
        params.put("longitude", bdLocation.getLongitude());

        AndroidAsyncHttp.get(ServerAPIConfig.Get_SOSList, params, handler);
    }


    public static void postAcceptOnlineOrder(Context context,String id,IDTokenInfo idTokenInfo,AsyncHttpResponseHandler handler) {
        Log.e("api","postAcceptOnlineOrder:----id:"+id+"-----idTokenInfo:"+idTokenInfo.list.get(0).id+"-----");
        String sessionID = UserInfo.getSessionID(context);
        JSONObject jsonObject = new JSONObject();
        if(TextUtils.isEmpty(sessionID)){
            ToastUtil.shortshow(context,"请登录");
            return;
        }
        try {
            JSONArray jsonArray = new JSONArray();
            jsonObject.put("session_id", sessionID);
            jsonObject.put("id", id);
            jsonObject.put("content", "");
            if (idTokenInfo != null) {
                for (int i = 0; i < idTokenInfo.list.size(); i++) {
                    JSONObject obj = new JSONObject();
                    obj.put("id", idTokenInfo.list.get(i).id);
                    obj.put("media_type", 1);
                    jsonArray.put(obj);
                }
            }
            jsonObject.put("media", jsonArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        StringEntity stringEntity = new StringEntity(jsonObject.toString(), "UTF-8");
        AndroidAsyncHttp.post(context,ServerAPIConfig.RewardBook, stringEntity,"application/json;charset=UTF-8", handler);
    }
}
