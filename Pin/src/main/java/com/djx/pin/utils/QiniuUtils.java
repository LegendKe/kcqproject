package com.djx.pin.utils;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.djx.pin.R;
import com.djx.pin.application.PinApplication;
import com.djx.pin.beans.IDTokenInfo;
import com.djx.pin.serverapiconfig.ServerAPIConfig;
import com.djx.pin.sql.MySQLLiteOpenHelper;
import com.djx.pin.widget.Image;
import com.djx.pin.widget.NineGridLayout;
import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.qiniu.android.http.ResponseInfo;
import com.qiniu.android.storage.UpCancellationSignal;
import com.qiniu.android.storage.UpCompletionHandler;
import com.qiniu.android.storage.UpProgressHandler;
import com.qiniu.android.storage.UploadManager;
import com.qiniu.android.storage.UploadOptions;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;
import cz.msebera.android.httpclient.util.TextUtils;

/**
 * Created by kechuanqi on 2016/9/18 0018.
 */
public class QiniuUtils {


    private static int postRequestExcuteNum = 0;
    private static MySQLLiteOpenHelper sqlLiteOpenHelper = PinApplication.getMyApp().getMySQLLiteOpenHelper();

    /** 1-1
     * 根据图片资源id获取一张七牛图片,并设置在ImageView上,并设置压缩的宽高
     * <p>
     * imageView:设置在imageview上
     * id:资源id
     * width,height:(压缩的尺寸)
     */
    public static void setImageViewByIdFrom7Niu(final Context context, final ImageView imageView, String id, int width, int height,final ImageShowListener imageShowListener) {

        final String img_url = queryDataBase(id);
        if (img_url != null) {
            Glide.with(context).load(img_url)
                    .centerCrop()
                    .placeholder(R.drawable.bg_normal)
                    .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                    .into(imageView);
            if(imageShowListener != null){
                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        imageShowListener.imageShow(img_url);
                    }
                });
            }
        } else {
            request1ImageFrom7N(context,imageView,id,width,height, imageShowListener);
        }
    }


    /** 1-2
     * 设置头像 240 x 240,设置头像专用
     * <p>
     * imageView:设置在imageview上
     * id:资源id
     */
    public static void setAvatarByIdFrom7Niu(final Context context, final ImageView imageView, String id) {

        if(TextUtils.isEmpty(id) || id.trim().length() < 1){
            Log.e("json","setAvatarByIdFrom7Niu头像为空:"+id);
            imageView.setBackgroundResource(R.mipmap.ic_defualtavater);
        }else {
            String url = queryDataBase(id);
            if(!TextUtils.isEmpty(url)){
                Picasso.with(context).load(url)
                        .config(Bitmap.Config.RGB_565)
                        .placeholder(R.mipmap.ic_defualtavater)
                        .error(R.mipmap.ic_defualtavater)
                        .into(imageView);
            }else {
                requestAvatarFrom7N(context,imageView,id);
            }
        }

    }

    /** 1-3
     * imageView:   设置原图
     * id:资源id
     */
    public static void setOneImageByIdFrom7Niu(final Context context, final ImageView imageView, String id) {

        String url = queryDataBase(id);
        if(!TextUtils.isEmpty(url)){
            Glide.with(context).load(url)
                    .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                    .placeholder(R.drawable.bg_normal)
                    .into(imageView);
        }else {
            request1BigIMGFrom7N(context,imageView,id);
        }
    }

    /** 1-4
     * imageView:   获取多图url,用于设置九宫格多图url
     * id:资源id
     */
    public static void set9GridByIdsFrom7Niu(final Context context, final List<String> ids,  String cacheId,NineGridLayout nineGridLayout) {

        String json = queryDataBase(cacheId);
        if(!TextUtils.isEmpty(json)){
            JSONObject obj = null;
            try {
                obj = new JSONObject(json);
                obj = obj.getJSONObject("result");
                JSONArray array1 = obj.getJSONArray("list");
                if(array1 != null && array1.length() > 0){
                    ArrayList<Image> imageList2 = new ArrayList<>();
                    Image image;
                    for (int j = 0; j < array1.length() ; j++) {
                        obj = array1.getJSONObject(j);
                        if(!TextUtils.isEmpty(obj.getString("url"))){
                            image = new Image(obj.getString("url"), 280, 280);
                            imageList2.add(image);
                        }
                    }
                    nineGridLayout.setImagesData(imageList2);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }else {

            requestIMGSFrom7N(context,ids,cacheId,nineGridLayout);
        }

    }

    private static void requestIMGSFrom7N(Context context, final List<String> ids, final String cacheId, final NineGridLayout nineGridLayout) {
        String id;
        final int size = ids.size();
        final JSONObject newObj = new JSONObject();
        try {
            JSONArray array = new JSONArray();
            for (int i = 0; i < size; i++) {
                id = ids.get(i);
                JSONObject detaileObj = new JSONObject();
                if (id == null) {
                    detaileObj.put("id", "");
                } else {
                    detaileObj.put("id", id);
                }
                detaileObj.put("media_type", 1);
                detaileObj.put("height",280);
                detaileObj.put("width",280);
                array.put(detaileObj);
            }
            newObj.put("size", size);
            newObj.put("list", array);
            StringEntity entity = new StringEntity(newObj.toString(), "utf-8");

            AsyncHttpResponseHandler res = new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int i, Header[] headers, byte[] bytes) {
                    String str = new String(bytes);
                    if (str != null) {
                        try {
                            JSONObject obj = new JSONObject(str);
                            obj = obj.getJSONObject("result");
                            JSONArray array1 = obj.getJSONArray("list");
                            if(array1 != null && array1.length() > 0){
                                ArrayList<Image> imageList = new ArrayList<>();
                                Image image;
                                for (int j = 0; j < array1.length() && j < size; j++) {
                                    obj = array1.getJSONObject(j);
                                    if(!TextUtils.isEmpty(obj.getString("url"))){
                                        image = new Image(obj.getString("url"), 280, 280);
                                        imageList.add(image);
                                    }
                                }
                                nineGridLayout.setImagesData(imageList);
                            }

                            ContentValues contentValues = new ContentValues();
                            contentValues.put("img_url", str);
                            contentValues.put("img_id", cacheId);
                            sqlLiteOpenHelper.insert(contentValues);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
                @Override
                public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                    Log.e("失败", "失败");
                }
            };
            AndroidAsyncHttp.post(context, ServerAPIConfig.GetQiNiuUrl, entity, " application/json;charset=UTF-8", res);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
















    /**
     * 从七牛请求图片url
     * @param context
     * @param imageView  设置在imageview上
     * @param id      图片id
     * @param width
     * @param height
     */
    private static void request1ImageFrom7N(final Context context, final ImageView imageView, final String id, int width, int height, final ImageShowListener imageShowListener) {

        final JSONObject newObj = new JSONObject();
        try {
            newObj.put("size", 1);
            JSONArray array = new JSONArray();
            JSONObject detaileObj = new JSONObject();
            detaileObj.put("id", id);
            detaileObj.put("media_type", 1);
            detaileObj.put("width", width);
            detaileObj.put("height", height);
            array.put(detaileObj);
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
                            final String url = obj.getString("url");
                            Glide.with(context).load(url)
                                    .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                                    .placeholder(R.drawable.bg_normal)
                                    .centerCrop()
                                    .into(imageView);
                            ContentValues contentValues = new ContentValues();
                            contentValues.put("img_url", url);
                            contentValues.put("img_id", id);
                            sqlLiteOpenHelper.insert(contentValues);
                            if(imageShowListener != null){
                                imageView.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        imageShowListener.imageShow(url);
                                    }
                                });
                            }
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
            AndroidAsyncHttp.post(context, ServerAPIConfig.GetQiNiuUrl, entity, " application/json;charset=UTF-8", res);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    static ImageShowListener imageShowListener;
    public void setImageShowListener(ImageShowListener imageShowListener) {
        this.imageShowListener = imageShowListener;
    }
    public interface ImageShowListener{
        void imageShow(String img_url);
    }

    private static void requestAvatarFrom7N(final Context context, final ImageView imageView, final String id) {
        final JSONObject newObj = new JSONObject();
        try {
            newObj.put("size", 1);
            JSONArray array = new JSONArray();
            JSONObject detaileObj = new JSONObject();
            detaileObj.put("id", id);
            detaileObj.put("media_type", 1);
            detaileObj.put("width", 160);
            detaileObj.put("height", 160);
            array.put(detaileObj);
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
                            String url = obj.getString("url");
                            Picasso.with(context)
                                    .load(url)
                                    .config(Bitmap.Config.RGB_565)
                                    .placeholder(R.mipmap.ic_defualtavater)
                                    .error(R.mipmap.ic_defualtavater)
                                    .into(imageView);
                            ContentValues contentValues = new ContentValues();
                            contentValues.put("img_url", url);
                            contentValues.put("img_id", id);
                            sqlLiteOpenHelper.insert(contentValues);
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
            AndroidAsyncHttp.post(context, ServerAPIConfig.GetQiNiuUrl, entity, " application/json;charset=UTF-8", res);

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    /**
     * 请求原图
     * @param context
     * @param imageView
     * @param id
     */
    private static void request1BigIMGFrom7N(final Context context, final ImageView imageView, final String id) {
        final JSONObject newObj = new JSONObject();
        try {
            newObj.put("size", 1);
            JSONArray array = new JSONArray();
            JSONObject detaileObj = new JSONObject();
            detaileObj.put("id", id);
            detaileObj.put("media_type", 1);
            array.put(detaileObj);
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
                            String url = obj.getString("url");
                            Log.e("pic","--------------Glide.with(context).load(url)---url:--"+url);
                            Glide.with(context).load(url)
                                    .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                                    .placeholder(R.drawable.bg_normal)
                                    .into(imageView);
                            ContentValues contentValues = new ContentValues();
                            contentValues.put("img_url", url);
                            contentValues.put("img_id", id);
                            sqlLiteOpenHelper.insert(contentValues);
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
            AndroidAsyncHttp.post(context, ServerAPIConfig.GetQiNiuUrl, entity, " application/json;charset=UTF-8", res);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    /** 1-3
     * imageView:   设置一张原图
     * id:资源id
     */
    public static void setOneImageByIdFrom7Niu(final Context context, final ImageView cacheImageView, final ImageView imageView, final ProgressBar progressBar, final String id) {

        String url = queryDataBase(id);
        if(!TextUtils.isEmpty(url)){
            Glide.with(context).load(url)
                    .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                    .placeholder(cacheImageView.getDrawable())
                    .dontAnimate()
                    .into(imageView);
            progressBar.setVisibility(View.GONE);
        }else {
            request1BigIMGFrom7N(context,cacheImageView,imageView,progressBar,id);
        }
    }
    /**
     * 请求原图 缓存显示之前图片（一张）
     * @param context
     * @param cacheImageView 缓存图
     * @param imageView 设置的view
     * @param progressBar 进度圈
     * @param id
     */
    private static void request1BigIMGFrom7N(final Context context, final ImageView cacheImageView, final ImageView imageView, final ProgressBar progressBar, final String id) {
        final JSONObject newObj = new JSONObject();
        try {
            newObj.put("size", 1);
            JSONArray array = new JSONArray();
            JSONObject detaileObj = new JSONObject();
            detaileObj.put("id", id);
            detaileObj.put("media_type", 1);
            array.put(detaileObj);
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
                            String url = obj.getString("url");
                            Glide.with(context).load(url)
                                    .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                                    .placeholder(cacheImageView.getDrawable())
                                    .dontAnimate()
                                    .listener(new RequestListener<String, GlideDrawable>() {
                                        @Override
                                        public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                                            return false;
                                        }
                                        @Override
                                        public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                                            progressBar.setVisibility(View.GONE);
                                            return false;
                                        }
                                    })
                                    .into(imageView);
                            ContentValues contentValues = new ContentValues();
                            contentValues.put("img_url", url);
                            contentValues.put("img_id", id);
                            sqlLiteOpenHelper.insert(contentValues);
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
            AndroidAsyncHttp.post(context, ServerAPIConfig.GetQiNiuUrl, entity, " application/json;charset=UTF-8", res);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    /**
     * 从本地查询
     * @param id  七牛图片id
     * @return  返回图片url
     */
    private static String queryDataBase(String id) {
        Cursor cursor = sqlLiteOpenHelper.query();
        String img_url = null;
        while (cursor.moveToNext()) {
            //使用GetString获取列中的值。参数为使用cursor.getColumnIndex("name")获取的序号。
            String img_id = cursor.getString(cursor.getColumnIndex("img_id"));
            if (img_id.equals(id)) {
                img_url = cursor.getString(cursor.getColumnIndex("img_url"));
            }
        }
        cursor.close();
        return img_url;
    }


    //*************************************************************************************************************************
    /**
     * @param context
     * @param ids
     * @param height
     * @param width
     * @param media_type
     * @param getUrlsCall
     */
    public static void get7NiuIMGUrl(final Context context, final List<String> ids, int height, int width, int media_type, final GetUrlsCallBack getUrlsCall) {

        String id;
        final int size = ids.size();
        final JSONObject newObj = new JSONObject();
        try {
            JSONArray array = new JSONArray();
            for (int i = 0; i < size; i++) {
                id = ids.get(i);
                JSONObject detaileObj = new JSONObject();
                if (id == null) {
                    detaileObj.put("id", "");
                } else {
                    detaileObj.put("id", id);
                }
                detaileObj.put("media_type", media_type);
                array.put(detaileObj);
            }
            newObj.put("size", size);
            newObj.put("list", array);
            StringEntity entity = new StringEntity(newObj.toString(), "utf-8");

            AsyncHttpResponseHandler res = new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int i, Header[] headers, byte[] bytes) {
                    String str = new String(bytes);
                    if (str != null) {
                        try {
                            JSONObject obj = new JSONObject(str);
                            obj = obj.getJSONObject("result");
                            JSONArray array1 = obj.getJSONArray("list");
                            ArrayList<String> strings = new ArrayList<>();
                            for (int j = 0; j < array1.length() && j < size; j++) {
                                obj = array1.getJSONObject(j);
                                strings.add(obj.getString("url"));
                            }
                            getUrlsCall.getUrlCallBack(strings);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
                @Override
                public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                    Log.e("失败", "失败");
                }
            };
            AndroidAsyncHttp.post(context, ServerAPIConfig.GetQiNiuUrl, entity, " application/json;charset=UTF-8", res);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * @param context
     * @param id
     * @param getUrlsCall
     */
    public static void getOne7NiuIMGUrl(final Context context, final String id, final GetOneUrlCallBack getUrlsCall) {

        final JSONObject newObj = new JSONObject();
        try {
            JSONArray array = new JSONArray();

            JSONObject detaileObj = new JSONObject();

            if (id == null) {
                detaileObj.put("id", "");
            } else {
                detaileObj.put("id", id);
            }
            detaileObj.put("media_type", 1);
            detaileObj.put("height", 160);
            detaileObj.put("width", 160);
            array.put(detaileObj);

            newObj.put("size", 1);
            newObj.put("list", array);
            StringEntity entity = new StringEntity(newObj.toString(), "utf-8");

            AsyncHttpResponseHandler res = new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int i, Header[] headers, byte[] bytes) {
                    String str = new String(bytes);
                    if (str != null) {
                        try {
                            JSONObject obj = new JSONObject(str);
                            obj = obj.getJSONObject("result");
                            JSONArray array1 = obj.getJSONArray("list");
                            obj = array1.getJSONObject(0);
                            getUrlsCall.getUrlCallBack(obj.getString("url"));

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
                @Override
                public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                    Log.e("失败", "失败");
                }
            };
            AndroidAsyncHttp.post(context, ServerAPIConfig.GetQiNiuUrl, entity, " application/json;charset=UTF-8", res);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public interface GetOneUrlCallBack {
        public void getUrlCallBack(String imgUrl);
    }


    public interface GetUrlsCallBack {
        public void getUrlCallBack(List<String> list);
    }



    /**
     * 2-1.获取图片需要的Token,并上传信息至七牛和公司服务器
     */
    public static void postRequestWithPics(final Context context, final int sizePic, final String session_id, final int media_type, final String[] path_Photo, final PostRequestWithPics requestWithPics) {
        //如果没有图片则不获取图片id和token
        if (sizePic == 0) {
            return;
        }
        RequestParams params = new RequestParams();
        params.put("size", sizePic);
        params.put("session_id", session_id);
        params.put("media_type", media_type);

        AndroidAsyncHttp.post(ServerAPIConfig.GetIDToken, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                String str_json = new String(bytes);
                try {
                    JSONObject obj = new JSONObject(str_json);
                    if (0 != obj.getInt("code")) {
                        ToastUtil.shortshow(context, R.string.toast_error_server);
                        LogUtil.e(context, "服务器返回结果异常");
                        return;
                    }
                    obj = obj.getJSONObject("result");
                    Gson gson = new Gson();
                    IDTokenInfo idTokenInfo_Pic = gson.fromJson(obj.toString(), IDTokenInfo.class);
                    HashMap<String, String> map = new HashMap<String, String>();
                    map.put("x:phone", "12345678");

                    updataPic2Qiniu(context, sizePic, idTokenInfo_Pic, map, path_Photo, requestWithPics);//2.上传照片至七牛服务器,

                } catch (JSONException e) {
                    LogUtil.e(context, "进入catch异常");
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                ToastUtil.shortshow(context, R.string.toast_error_net);
                LogUtil.e(context, R.string.toast_error_net);
            }
        });

    }

    /**
     * 2-2.上传照片至七牛服务器,
     * 由于视屏 照片上传是多线程,不能确定哪一个最后上传完成,
     * 所以当一个文件上传成功后调用updataPicInfo2Server()函数尝试上传至公司服务器.
     * 在updataPicInfo2Server()函数中已经实现了检查是否所有文件都上传至七牛服务器,
     * 只有在所有文件都上传至七牛服务器时才会上传这些信息至公司服务器,且只会上传一次
     */
    public static void updataPic2Qiniu(final Context context, final int sizePic, final IDTokenInfo idTokenInfo_Pic, HashMap<String, String> hashMap, String[] path_Photo, final PostRequestWithPics requestWithPics) {//idTokenInfo_Pic
        if (null == idTokenInfo_Pic)
            return;
        UploadManager uploadManager = null;
        if (null == uploadManager) {
            uploadManager = new UploadManager();
        }
        if (null == idTokenInfo_Pic.list) {
            return;
        }
        //上传照片
        for (int j = 0; j < sizePic; j++) {
            uploadManager.put(path_Photo[j], idTokenInfo_Pic.list.get(j).id, idTokenInfo_Pic.list.get(j).token, new UpCompletionHandler() {
                @Override
                public void complete(String key, ResponseInfo responseInfo, JSONObject jsonObject) {

                    for (int k = 0; k < sizePic; k++) {
                        if (key.equals(idTokenInfo_Pic.list.get(k).id)) {
                            if (responseInfo.isOK()) {
                                postRequestExcuteNum++;
                                Log.i("postRequestExcuteNum", postRequestExcuteNum + "-------------------------");
                                if (postRequestExcuteNum == sizePic) {
                                    requestWithPics.postRequest(idTokenInfo_Pic);
                                    postRequestExcuteNum = 0;
                                }
                            } else {
                                ToastUtil.shortshow(context, R.string.toast_updata_failer);
                            }
                        }
                    }
                }
            }, new UploadOptions(hashMap, null, false, new UpProgressHandler() {
                @Override
                public void progress(String key, double percent) {

                }
            }, new UpCancellationSignal() {
                @Override
                public boolean isCancelled() {
                    return false;
                }
            }));
        }
    }


    public interface PostRequestWithPics {
        void postRequest(IDTokenInfo idTokenInfo_Pic);
    }

    /**
     * 2-1.获取图片需要的Token,并上传信息至七牛和公司服务器
     */
    public static void postRequestWithIMGS(final Context context, final List<String> img_paths, final String session_id, final int media_type, final PostRequestWithPics requestWithPics) {
        //如果没有图片则不获取图片id和token
        int sizePic = img_paths.size();
        RequestParams params = new RequestParams();
        params.put("size", sizePic);
        params.put("session_id", session_id);
        params.put("media_type", media_type);
        AndroidAsyncHttp.post(ServerAPIConfig.GetIDToken, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                String str_json = new String(bytes);
                try {
                    JSONObject obj = new JSONObject(str_json);
                    Log.e("img","str_json:-------"+str_json);
                    if (0 == obj.getInt("code")) {
                        obj = obj.getJSONObject("result");
                        Gson gson = new Gson();
                        IDTokenInfo idTokenInfo = gson.fromJson(obj.toString(), IDTokenInfo.class);
                        if(idTokenInfo != null && idTokenInfo.list != null && idTokenInfo.list.size() >0){
                            updataPic2Qiniu2(context, img_paths,idTokenInfo, requestWithPics);//2.上传照片至七牛服务器,
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
            }
        });
    }
    public static void updataPic2Qiniu2(final Context context, final List<String> img_paths, final IDTokenInfo idTokenInfo_Pic, final PostRequestWithPics requestWithPics) {//idTokenInfo_Pic
        UploadManager  uploadManager = new UploadManager();
        final ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.setMax(100);
        progressDialog.setProgress(0); // 设置当前默认进度为 0
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL); // 设置为矩形进度条
        progressDialog.setTitle("提示");
        progressDialog.setMessage("正在上传图片,请稍等..");
        progressDialog.show();
        //上传照片
        final int size = img_paths.size();
        for (int j = 0; j < size; j++) {
            uploadManager.put(img_paths.get(j), idTokenInfo_Pic.list.get(j).id, idTokenInfo_Pic.list.get(j).token, new UpCompletionHandler() {
                @Override
                public void complete(String key, ResponseInfo responseInfo, JSONObject jsonObject) {
                    for (int k = 0; k < size; k++) {
                        if (key.equals(idTokenInfo_Pic.list.get(k).id)) {
                            if (responseInfo.isOK()) {
                                postRequestExcuteNum++;
                                progressDialog.setProgress((int)(postRequestExcuteNum*100)/size);
                                if (postRequestExcuteNum == size) {
                                    requestWithPics.postRequest(idTokenInfo_Pic);
                                    progressDialog.setProgress(100);
                                    progressDialog.dismiss();
                                    postRequestExcuteNum = 0;
                                }
                            } else {
                                ToastUtil.shortshow(context, R.string.toast_updata_failer);
                            }
                        }
                    }
                }
            },new UploadOptions(null, null, false, new UpProgressHandler() {//上产进度
                @Override
                public void progress(String key, double persent) {
                    if (postRequestExcuteNum == 0) {
                        progressDialog.setProgress((int) (persent * 100) / size);
                        Log.e("qiniu", "七牛图片上传进度key:" + key + "   persent:" + (int) (persent * 100) / size);
                    }
                }
            }, new UpCancellationSignal() {//取消上传信息,当返回TRUE时取消上传
                @Override
                public boolean isCancelled() {
                    return false;
                }
            }));

        }
    }

}
