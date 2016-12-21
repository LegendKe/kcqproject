package com.djx.pin.improve.utils;

import android.app.Dialog;
import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.djx.pin.R;
import com.djx.pin.myview.timepicker.NumericWheelAdapter;
import com.djx.pin.myview.timepicker.WheelView;
import com.djx.pin.utils.QiniuUtils;
import com.djx.pin.utils.ToastUtil;
import com.djx.pin.utils.myutils.ScreenTools;
import com.djx.pin.widget.CustomImageView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import cz.msebera.android.httpclient.util.TextUtils;
import uk.co.senab.photoview.PhotoView;

/**
 * Created by 柯传奇 on 2016/11/28 0028.
 */
public class DialogUtils {

    public  interface SlectAlbumListener{
        void onClick();
    }
    public  interface SlectCameraListener{
        void onClick();
    }

    /**
     * 添加图片对话框
     * @param context
     * @param slectAlbumListener
     * @param slectCameraListener
     */
    public static void AddImageDialog(Context context,
                                      final SlectAlbumListener slectAlbumListener,
                                      final SlectCameraListener slectCameraListener) {
      /*  final AlertDialog alertDialog = new AlertDialog.Builder(context).create();
        alertDialog.show();*/
        final Dialog alertDialog = new Dialog(context,R.style.dialog_transparent);
        alertDialog.show();
        View view = View.inflate(context, R.layout.layout_dialog_addpicvideo, null);
        alertDialog.setContentView(view);

        WindowManager.LayoutParams layoutParams = alertDialog.getWindow().getAttributes();
        layoutParams.gravity = Gravity.BOTTOM;
        layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
        layoutParams.y = 0;
        alertDialog.getWindow().setAttributes(layoutParams);

        //拍照
        view.findViewById(R.id.bt_camera).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                slectCameraListener.onClick();
                alertDialog.dismiss();
            }
        });
        //相册
        view.findViewById(R.id.bt_photoalbum).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                slectAlbumListener.onClick();
                alertDialog.dismiss();
            }
        });
        //取消
        view.findViewById(R.id.bt_cancle).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
    }

    public interface OnItemClickListener{
        void onClick(int price_min,int price_max,int distance,int order);
    }

    /**
     * 智能排序对话框
     * @param context
     * @param onItemClickListener
     */
    public static void AddIntelligentDialog(Context context, final OnItemClickListener onItemClickListener) {

        final Dialog dialog = new Dialog(context,R.style.dialog_non_transparent);
        dialog.show();
        View view = View.inflate(context, R.layout.layout_dialog_order_intelligent, null);
        dialog.setContentView(view);

        WindowManager.LayoutParams layoutParams = dialog.getWindow().getAttributes();
        layoutParams.gravity = Gravity.TOP;
        layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
        layoutParams.y = ScreenTools.instance(context).dip2px(90);
        dialog.getWindow().setAttributes(layoutParams);

        //实时最热
        view.findViewById(R.id.rb_realtime_hottest).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClickListener.onClick(0,0,0,1);
                dialog.dismiss();
            }
        });
        //实时最新
        view.findViewById(R.id.rb_realtime_newest).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClickListener.onClick(0,0,0,2);
                dialog.dismiss();
            }
        });
        //金额升序
        view.findViewById(R.id.rb_realtime_ascend).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClickListener.onClick(0,0,0,3);
                dialog.dismiss();
            }
        });
        //金额升序
        view.findViewById(R.id.rb_realtime_descend).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClickListener.onClick(0,0,0,4);
                dialog.dismiss();
            }
        });
    }

    /**
     * 距离排序对话框
     * @param context
     * @param onItemClickListener
     */
    public static void AddDistanceDialog(Context context, final OnItemClickListener onItemClickListener) {

        final Dialog dialog = new Dialog(context,R.style.dialog_non_transparent);
        dialog.show();
        View view = View.inflate(context, R.layout.layout_dialog_order_distance, null);
        dialog.setContentView(view);

        WindowManager.LayoutParams layoutParams = dialog.getWindow().getAttributes();
        layoutParams.gravity = Gravity.TOP;
        layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
        layoutParams.y = ScreenTools.instance(context).dip2px(90);
        dialog.getWindow().setAttributes(layoutParams);

        //实时最热
        view.findViewById(R.id.rb_near).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClickListener.onClick(0,0,5,0);
                dialog.dismiss();
            }
        });
        //实时最新
        view.findViewById(R.id.rb_3km).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClickListener.onClick(0,0,3,0);
                dialog.dismiss();
            }
        });
        //金额升序
        view.findViewById(R.id.rb_5km).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClickListener.onClick(0,0,5,0);
                dialog.dismiss();
            }
        });
        //金额升序
        view.findViewById(R.id.rb_10km).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClickListener.onClick(0,0,10,0);
                dialog.dismiss();
            }
        });
    }

    /**
     * 金额排序对话框
     * @param context
     * @param onItemClickListener
     */
    public static void AddPriceDialog(final Context context, final OnItemClickListener onItemClickListener) {

        final Dialog dialog = new Dialog(context,R.style.dialog_non_transparent);
        dialog.show();
        View view = View.inflate(context, R.layout.layout_dialog_order_price, null);
        final EditText et_minPrice = (EditText) view.findViewById(R.id.et_min_price);
        final EditText et_maxPrice = (EditText) view.findViewById(R.id.et_max_price);
        dialog.setContentView(view);

        WindowManager.LayoutParams layoutParams = dialog.getWindow().getAttributes();
        layoutParams.gravity = Gravity.TOP;
        layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
        layoutParams.y = ScreenTools.instance(context).dip2px(90);
        dialog.getWindow().setAttributes(layoutParams);


        view.findViewById(R.id.bt_confirm_dialog_order_price).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //实时最热
                final String minPrice = et_minPrice.getText().toString().trim();
                final String maxPrice = et_maxPrice.getText().toString().trim();
                if(TextUtils.isEmpty(minPrice)){
                    ToastUtil.shortshow(context,"请输入最小金额");
                    return;
                }
                if(TextUtils.isEmpty(maxPrice)){
                    ToastUtil.shortshow(context,"请输入最大金额");
                    return;
                }
                Integer mPrice = Integer.valueOf(minPrice);
                Integer xPrice = Integer.valueOf(maxPrice);

                if(mPrice >= xPrice){
                    ToastUtil.shortshow(context,"最大金额需小于最小金额");
                    return;
                }
                onItemClickListener.onClick(mPrice,xPrice,0,0);
                dialog.dismiss();
            }
        });
    }


    public static void selectTimeDialog(final Context context, final OnTimeSlectedListener listener) {

        final Dialog dialog = new Dialog(context,R.style.dialog_transparent);
        dialog.show();
        View view = View.inflate(context, R.layout.dialog_timer_picker, null);
        final WheelView year = (WheelView) view.findViewById(R.id.year);
        final WheelView month = (WheelView) view.findViewById(R.id.month);
        final WheelView day = (WheelView) view.findViewById(R.id.day);
        final WheelView hours = (WheelView) view.findViewById(R.id.hour);
        final WheelView mins = (WheelView) view.findViewById(R.id.min);
        dialog.setContentView(view);

        WindowManager.LayoutParams layoutParams = dialog.getWindow().getAttributes();
        layoutParams.gravity = Gravity.BOTTOM;
        layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
        layoutParams.y = 0;
        dialog.getWindow().setAttributes(layoutParams);

        Calendar calendar = Calendar.getInstance(Locale.CHINA);
        final int currentYear = calendar.get(Calendar.YEAR);

        NumericWheelAdapter yearAdapter = new NumericWheelAdapter(context, currentYear, currentYear+10, "%02d");
        yearAdapter.setItemResource(R.layout.item_dialog_wheel_text);
        yearAdapter.setItemTextResource(R.id.text);
        year.setViewAdapter(yearAdapter);
        year.setCyclic(false);

        //月
        NumericWheelAdapter monthAdapter = new NumericWheelAdapter(context, 1, 12, "%02d");
        monthAdapter.setItemResource(R.layout.item_dialog_wheel_text);
        monthAdapter.setItemTextResource(R.id.text);
        month.setViewAdapter(monthAdapter);
        month.setCyclic(true);

        // 日
        NumericWheelAdapter dayAdapter = new NumericWheelAdapter(context, 1, 30, "%02d");
        dayAdapter.setItemResource(R.layout.item_dialog_wheel_text);
        dayAdapter.setItemTextResource(R.id.text);
        day.setViewAdapter(dayAdapter);
        day.setCyclic(true);

        // 小时
        NumericWheelAdapter hourAdapter = new NumericWheelAdapter(context, 0, 23, "%02d");
        hourAdapter.setItemResource(R.layout.item_dialog_wheel_text);
        hourAdapter.setItemTextResource(R.id.text);
        hours.setViewAdapter(hourAdapter);
        hours.setCyclic(true);

        // 分钟
        NumericWheelAdapter minAdapter = new NumericWheelAdapter(context, 0, 59, "%02d");
        minAdapter.setItemResource(R.layout.item_dialog_wheel_text);
        minAdapter.setItemTextResource(R.id.text);
        mins.setViewAdapter(minAdapter);
        mins.setCyclic(true);

        // 当前时间
        month.setCurrentItem(calendar.get(Calendar.MONTH));
        day.setCurrentItem(calendar.get(Calendar.DAY_OF_MONTH)-1);
        hours.setCurrentItem(calendar.get(Calendar.HOUR_OF_DAY));
        mins.setCurrentItem(calendar.get(Calendar.MINUTE));
        year.setCurrentItem(currentYear);

        view.findViewById(R.id.tv_confirm).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.timeSelected(currentYear+year.getCurrentItem(),month.getCurrentItem()+1,day.getCurrentItem()+1,hours.getCurrentItem(),mins.getCurrentItem());
                dialog.dismiss();
            }
        });
        view.findViewById(R.id.tv_cance).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });


    }

    public interface OnTimeSlectedListener{
        void timeSelected(int year,int month,int day,int hour,int min);
    }



    /**
     * 显示一张图片详情
     * @param context
     */
    public static void showOneImage(Context context,String id, ImageView cacheImageView) {

        final Dialog dialog = new Dialog(context);
        dialog.show();
        View view = View.inflate(context, R.layout.dialog_show_image, null);
        dialog.setContentView(view);

        Window window = dialog.getWindow();
        WindowManager.LayoutParams layoutParams = window.getAttributes();
        layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
        layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT;
        window.setAttributes(layoutParams);

        ImageView imageView = (ImageView) view.findViewById(R.id.iv_imageShow);
        ProgressBar progressBar = (ProgressBar) view.findViewById(R.id.progressBar);
        QiniuUtils.setOneImageByIdFrom7Niu(context,cacheImageView,imageView,progressBar,id);
    }

    /**
     * 显示多张图片详情
     * @param context
     */
    public static void showImages(final Context context, final List<String> ids, final List<CustomImageView> cacheImageViews,int currentPos) {

        final Dialog dialog = new Dialog(context);
        dialog.show();
        View view = View.inflate(context, R.layout.dialog_show_images, null);
        dialog.setContentView(view);

        Window window = dialog.getWindow();
        WindowManager.LayoutParams layoutParams = window.getAttributes();
        layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
        layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT;
        window.setAttributes(layoutParams);
        ViewPager viewPager = (ViewPager) view.findViewById(R.id.viewPager);

        final ArrayList<View> views = new ArrayList<>();
        View view_item;
        for (int i = 0; i < ids.size(); i++) {
            view_item = View.inflate(context, R.layout.progress_photo_view, null);
            views.add(view_item);
        }

        viewPager.setAdapter(new PagerAdapter() {
            @Override
            public int getCount() {
                return ids.size();
            }

            @Override
            public Object instantiateItem(ViewGroup container, int position) {
                final View view = views.get(position);
                final PhotoView photoView = (PhotoView) view.findViewById(R.id.photoview);
                final ProgressBar progressBar = (ProgressBar) view.findViewById(R.id.pb_loading);
                QiniuUtils.setOneImageByIdFrom7Niu(context,cacheImageViews.get(position),photoView,progressBar,ids.get(position));
                container.addView(view);
                return view;
            }
            @Override
            public void destroyItem(ViewGroup container, int position, Object object) {
                container.removeView(views.get(position));
            }
            @Override
            public boolean isViewFromObject(View view, Object object) {
                return view ==object;
            }
        });
        viewPager.setCurrentItem(currentPos);
        viewPager.setOffscreenPageLimit(0);
    }
}
