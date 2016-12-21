package com.djx.pin.adapter;

import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 陈刘磊 (代号：姜饼（GingerBread）) on 2016/4/25.
 */
public class MyPageAdapter extends PagerAdapter {

    List<View> list = new ArrayList<View>();

    public void add(View view) {
        list.add(view);
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public float getPageWidth(int position) {

        return 1.0f;

    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {

        View view = list.get(position);
        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView(list.get(position));
    }
}
