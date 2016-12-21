package com.djx.pin.improve.common;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.djx.pin.R;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import uk.co.senab.photoview.PhotoView;

/**
 * Created by 柯传奇 on 2016/10/18 0018.
 */
public class PhotoViewAdapter extends PagerAdapter {

    private final ArrayList<String> ids;//图片id
    Context context;
    List<String> urls;//图片url
    List<PhotoView> photoViews;
    List<View> views;
    List<String> cache_urls;
    public PhotoViewAdapter(Context context,List<String> urls,ArrayList<String> ids) {
        this.context = context;
        this.urls = urls;
        this.ids = ids;
        photoViews = new ArrayList<>();
        views = new ArrayList<>();
        for (int i = 0; i < urls.size(); i++) {
            View view = View.inflate(context, R.layout.progress_photo_view, null);
            views.add(view);
        }
    }
    public PhotoViewAdapter(Context context,List<String> urls,ArrayList<String> ids,List<String> cache_urls) {
        this.context = context;
        this.urls = urls;
        this.ids = ids;
        photoViews = new ArrayList<>();
        views = new ArrayList<>();
        this.cache_urls = cache_urls;
        for (int i = 0; i < urls.size(); i++) {
            View view = View.inflate(context, R.layout.progress_photo_view, null);
            views.add(view);
        }
        Log.e("photo","----PhotoViewAdapter 不为空---this.cache_urls = cache_urls---");
    }


    public void addAll(List<String> urls){
        this.urls = urls;
        notifyDataSetChanged();
    }
    @Override
    public int getCount() {
        return urls.size();
    }


    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }


    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView(views.get(position));
    }


    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        final View view = views.get(position);
        final PhotoView photoView = (PhotoView) view.findViewById(R.id.photoview);
        final ProgressBar progressBar = (ProgressBar) view.findViewById(R.id.pb_loading);
        final ImageView iv_cache = (ImageView) view.findViewById(R.id.iv_cache);
        /*if(cache_urls != null && cache_urls.size() > 0){
            File cacheDir = context.getCacheDir();
            String fn = cache_urls.get(position).hashCode()+".png";
            String path = cacheDir + File.separator+ fn;
            Bitmap bitmap = BitmapFactory.decodeFile(path);
            Log.e("photo","---------------设置本地图片------------------");
            iv_cache.setImageBitmap(bitmap);
        }*/
        Glide.with(context).load(urls.get(position)).diskCacheStrategy(DiskCacheStrategy.SOURCE).listener(new RequestListener<String, GlideDrawable>() {
            @Override
            public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                return false;
            }
            @Override
            public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                progressBar.setVisibility(View.GONE);
                iv_cache.setVisibility(View.GONE);
                return false;
            }
        }).into(photoView);
        container.addView(view);
        return view;
    }

    ItemClickListener itemClickListener;
    public void setOnItemClickListene(ItemClickListener itemClickListener){
        this.itemClickListener = itemClickListener;
    }
    public interface ItemClickListener{
        void onClicked();
    }
}
