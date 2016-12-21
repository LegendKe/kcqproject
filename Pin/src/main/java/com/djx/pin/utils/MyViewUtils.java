package com.djx.pin.utils;

import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.djx.pin.R;

/**
 * Created by Administrator on 2016/9/30 0030.
 */
public class MyViewUtils {


    /**
     * 根据textview行数控制显示
     * @param view
     * @param textView
     */
    public static void viewShowAccordingLines(final View view, final TextView textView){
        ViewTreeObserver vto = textView.getViewTreeObserver();
        vto.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                int lineCount = textView.getLineCount();
                if(lineCount >= 6){//大于五行时显示
                    view.setVisibility(View.VISIBLE);
                }
                LogUtil.e("行数:"+lineCount);
                return true;
            }
        });
    }

    /**
     * 根据textview行数控制显示
     * @param view
     * @param textView
     */
    public static void viewShowAccording8Lines(final View view, final TextView textView){
        ViewTreeObserver vto = textView.getViewTreeObserver();
        vto.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                int lineCount = textView.getLineCount();
                if(lineCount >= 8){//大于五行时显示
                    view.setVisibility(View.VISIBLE);
                }
                LogUtil.e("行数:"+lineCount);
                return true;
            }
        });
    }
}
