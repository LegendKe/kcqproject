package com.djx.pin.myview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.djx.pin.R;
/**
 * Created by 陈刘磊 (代号：姜饼（GingerBread）) on 2016/4/26.
 */
public class MyTitleView extends LinearLayout {
    private ImageView img_left;
    private TextView tv_Title;

    public MyTitleView(Context context, AttributeSet attrs) {
        super(context, attrs);
        inflate(context, R.layout.mytitleview_layout, this);
        img_left = (ImageView) findViewById(R.id.img_Left);
        tv_Title = (TextView) findViewById(R.id.tv_tltle);

    }

    /****
     *为标题栏添加内容
     * 参数：1：左边图片ID。2：标题内容。3右标题内容。4：右标题背景
     */

    public void initTitle(int left_Id, String title, View.OnClickListener l) {
        if (left_Id>-1){
            img_left.setImageResource(left_Id);
        }else {
            img_left.setImageResource(R.drawable.user_admin_name);
            img_left.setOnClickListener(l);
        }

        tv_Title.setText(title);
    }
}
