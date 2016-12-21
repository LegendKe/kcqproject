package com.djx.pin.utils;

import android.content.Context;
import android.os.CountDownTimer;
import android.widget.TextView;

import com.djx.pin.R;

/**
 * Created by Administrator on 2016/7/13 0013.
 */
public class CountDownTimerUtils extends CountDownTimer {
    /**
     * @param millisInFuture    The number of millis in the future from the call
     *                          to {@link #start()} until the countdown is done and {@link #onFinish()}
     *                          is called.
     * @param countDownInterval The interval along the way to receive
     *                          {@link #onTick(long)} callbacks.
     *
     * @param textView  The textView
     */

    private TextView textView;
    private Context context;
    public CountDownTimerUtils(TextView textView, long millisInFuture, long countDownInterval, Context context) {
        super(millisInFuture, countDownInterval);

        this.textView=textView;
        this.context=context;

    }

    @Override
    public void onTick(long millisUntilFinished) {

        textView.setClickable(false); //设置不可点击
        textView.setText(millisUntilFinished / 1000 + "s后重新获取");  //设置倒计时时间
        textView.setTextColor(context.getResources().getColor(R.color.text_color_normal)); //设置按钮为灰色，这时是不能点击的

//        /**
//         * 超链接 URLSpan
//         * 文字背景颜色 BackgroundColorSpan
//         * 文字颜色 ForegroundColorSpan
//         * 字体大小 AbsoluteSizeSpan
//         * 粗体、斜体 StyleSpan
//         * 删除线 StrikethroughSpan
//         * 下划线 UnderlineSpan
//         * 图片 ImageSpan
//         * http://blog.csdn.net/ah200614435/article/details/7914459
//         */
//        SpannableString spannableString = new SpannableString(textView.getText().toString());  //获取按钮上的文字
//        ForegroundColorSpan span = new ForegroundColorSpan(Color.RED);
//        /**
//         * public void setSpan(Object what, int start, int end, int flags) {
//         * 主要是start跟end，start是起始位置,无论中英文，都算一个。
//         * 从0开始计算起。end是结束位置，所以处理的文字，包含开始位置，但不包含结束位置。
//         */
//        spannableString.setSpan(span, 0, 2, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);//将倒计时的时间设置为红色
//        textView.setText(spannableString);
    }


    @Override
    public void onFinish() {
        textView.setText("重新获取");
        textView.setClickable(true);//重新获得点击
        textView.setTextColor(context.getResources().getColor(R.color.text_color_focused));  //还原背景色
    }
}
