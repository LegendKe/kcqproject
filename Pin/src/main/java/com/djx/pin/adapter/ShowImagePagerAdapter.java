package com.djx.pin.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import uk.co.senab.photoview.PhotoView;

/**
 * Created by Administrator on 2016/6/27.
 */
public class ShowImagePagerAdapter extends PagerAdapter{
    List<PhotoView> list =new ArrayList<PhotoView>();
    Context context;
    ViewPagerItemClickListener pagerItemClickListener;

    public ShowImagePagerAdapter(Context context) {
        this.context=context;
    }


    public void add(PhotoView photoView){
        list.add(photoView);
    }
    public void addDatas(List<PhotoView> photoViews){
        list.removeAll(list);
        list.addAll(photoViews);
        notifyDataSetChanged();
    }

    public void clear(){
        list.clear();
    }
    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view==object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position) {
        PhotoView photoView = list.get(position);
        photoView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pagerItemClickListener.onItemClickListener();
            }
        });
        container.addView(photoView);
        return photoView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView(list.get(position));
    }

   public interface ViewPagerItemClickListener{
        void onItemClickListener();
    }

    public void setPagerItemClickListener(ViewPagerItemClickListener pagerItemClickListener) {
        this.pagerItemClickListener = pagerItemClickListener;
    }
}
