package com.djx.pin.improve.common;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.djx.pin.R;
import com.djx.pin.utils.QiniuUtils;
import com.djx.pin.widget.adapters.MyViewPager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 柯传奇 on 2016/10/18 0018.
 */
public class PhotoShowActivity extends AppCompatActivity {

    private MyViewPager vp;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.photo_show_activity);
        vp = ((MyViewPager) findViewById(R.id.vp_photo_show));

        final ArrayList<String> urls = getIntent().getStringArrayListExtra("URLS");
        final ArrayList<String> ids = getIntent().getStringArrayListExtra("IDS");
        final int current_pos1 = getIntent().getIntExtra("CURRENT_POS", 0);
        final ArrayList<String> cache_urls = getIntent().getStringArrayListExtra("CURRENT_CACHE_URL");

        //获取图片url并设置图片
        if(urls != null && urls.size() > 0){
            PhotoViewAdapter adapter = new PhotoViewAdapter(PhotoShowActivity.this, urls,ids);
            vp.setAdapter(adapter);
        }else if(ids != null && ids.size() > 0){
            if(cache_urls != null){
                QiniuUtils.get7NiuIMGUrl(this, ids, 900, 600, 1, new QiniuUtils.GetUrlsCallBack() {
                    @Override
                    public void getUrlCallBack(final List<String> list) {
                        Log.e("photo","----cache_url 不为空------");
                        if (list != null && list.size() > 0) {
                            PhotoViewAdapter adapter = new PhotoViewAdapter(PhotoShowActivity.this, list,ids,cache_urls);
                            vp.setAdapter(adapter);
                            vp.setCurrentItem(current_pos1);
                        }
                    }
                });
            }else {
                QiniuUtils.get7NiuIMGUrl(this, ids, 900, 600, 1, new QiniuUtils.GetUrlsCallBack() {
                    @Override
                    public void getUrlCallBack(final List<String> list) {
                        if (list != null && list.size() > 0) {
                            PhotoViewAdapter adapter = new PhotoViewAdapter(PhotoShowActivity.this, list,ids);
                            vp.setAdapter(adapter);
                            vp.setCurrentItem(current_pos1);
                        }
                    }
                });
            }
        }

    }
}
