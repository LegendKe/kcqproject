package com.djx.pin.ui;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.djx.pin.R;
import com.djx.pin.utils.QiniuUtils;

import uk.co.senab.photoview.PhotoView;

/**
 * Created by dujoy on 2016/8/9.
 */
public class CommonDialog {
    public static void show(Context context,
                            String positiveText,
                            String negativeText,
                            String messageText,
                            Button.OnClickListener listenerPositive,
                            Button.OnClickListener listenerNegative) {
        AlertDialog.Builder builder = getDialog(context);
        final AlertDialog dialog = builder.create();
        final Button.OnClickListener listenerp = listenerPositive;
        final Button.OnClickListener listenern = listenerNegative;
        dialog.show();
        Window window = dialog.getWindow();
        window.setContentView(R.layout.dialog_common);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        TextView tv_message = (TextView) window.findViewById(R.id.tv_dialog_message);
        Button btn_ok = (Button) window.findViewById(R.id.dialog_btn_ok);
        btn_ok.setFocusable(true);
        btn_ok.setFocusableInTouchMode(true);
        btn_ok.requestFocus();
        btn_ok.requestFocusFromTouch();
        Button btn_cancel = (Button) window.findViewById(R.id.dialog_btn_cancel);
        btn_cancel.setFocusable(true);
        btn_ok.setText(positiveText);
        btn_cancel.setText(negativeText);
        btn_ok.setOnClickListener(listenerPositive);
        btn_ok.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (listenerp != null) {
                    listenerp.onClick(view);
                }
                dialog.dismiss();
            }
        });
        btn_cancel.setOnClickListener(listenerNegative);
        btn_cancel.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (listenern != null) {
                    listenern.onClick(view);
                }
                dialog.dismiss();
            }
        });
        tv_message.setText(messageText);
    }

    public static void show2(Context context,
                            String positiveText,
                            String negativeText,
                            String messageText,
                            Button.OnClickListener listenerPositive,
                            Button.OnClickListener listenerNegative, final CheckBoxCheckedListener checkedListener) {
        AlertDialog.Builder builder = getDialog(context);
        final AlertDialog dialog = builder.create();
        final Button.OnClickListener listenerp = listenerPositive;
        final Button.OnClickListener listenern = listenerNegative;
        dialog.show();
        Window window = dialog.getWindow();
        View view = View.inflate(context, R.layout.dialog_common, null);
        CheckBox cb_comment_dialog = (CheckBox) view.findViewById(R.id.cb_comment_dialog);
        cb_comment_dialog.setVisibility(View.VISIBLE);
        window.setContentView(view);
        cb_comment_dialog.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                checkedListener.OnCheckChanged(isChecked);
            }
        });
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        TextView tv_message = (TextView) window.findViewById(R.id.tv_dialog_message);
        Button btn_ok = (Button) window.findViewById(R.id.dialog_btn_ok);
        btn_ok.setFocusable(true);
        btn_ok.setFocusableInTouchMode(true);
        btn_ok.requestFocus();
        btn_ok.requestFocusFromTouch();
        Button btn_cancel = (Button) window.findViewById(R.id.dialog_btn_cancel);
        btn_cancel.setFocusable(true);
        btn_ok.setText(positiveText);
        btn_cancel.setText(negativeText);
        btn_ok.setOnClickListener(listenerPositive);
        btn_ok.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (listenerp != null) {
                    listenerp.onClick(view);
                }
                dialog.dismiss();
            }
        });
        btn_cancel.setOnClickListener(listenerNegative);
        btn_cancel.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (listenern != null) {
                    listenern.onClick(view);
                }
                dialog.dismiss();
            }
        });
        tv_message.setText(messageText);
    }

    public static void dismiss(Context context){
        AlertDialog.Builder builder = getDialog(context);
        final AlertDialog dialog = builder.create();
        dialog.dismiss();
    }
    public static void show(Context context,
                            String positiveText,
                            String negativeText,
                            String messageText,
                            Button.OnClickListener listenerPositive) {
        AlertDialog.Builder builder = getDialog(context);
        final AlertDialog dialog = builder.create();
        final Button.OnClickListener listener = listenerPositive;
        dialog.show();
        Window window = dialog.getWindow();
        window.setContentView(R.layout.dialog_common);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        TextView tv_message = (TextView) window.findViewById(R.id.tv_dialog_message);
        Button btn_ok = (Button) window.findViewById(R.id.dialog_btn_ok);
        btn_ok.setFocusable(true);
        btn_ok.setFocusableInTouchMode(true);
        btn_ok.requestFocus();
        btn_ok.requestFocusFromTouch();
        Button btn_cancel = (Button) window.findViewById(R.id.dialog_btn_cancel);
        btn_cancel.setFocusable(true);
        btn_ok.setText(positiveText);
        btn_cancel.setText(negativeText);
        btn_ok.setOnClickListener(listenerPositive);
        btn_ok.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (listener != null) {
                    listener.onClick(view);
                }
                dialog.dismiss();
            }
        });
        btn_cancel.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        tv_message.setText(messageText);
    }
    public interface CheckBoxCheckedListener{
        void OnCheckChanged(boolean isChecked);
    }
    /***
     * 获取一个dialog
     * @param context
     * @return
     */
    public static AlertDialog.Builder getDialog(Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        return builder;
    }

    public  interface PositiveListener{
        void onClick();
    }
    public  interface NegativeListener{
        void onClick();
    }
    public  interface TextContentOnClickListener{
        void onClick();
    }
    public static void orderCheckShow(Context context,
                                      String positiveText,
                                      String negativeText,
                                      String messageText,
                                      String content,
                                      final PositiveListener positiveListener,
                                      final NegativeListener negativeListener,
                                      final TextContentOnClickListener contentOnClickListener,
                                      boolean isShowImage, String img_id) {
        final AlertDialog alertDialog = getDialog(context).create();
        alertDialog.show();
        Window window = alertDialog.getWindow();
        window.setContentView(R.layout.dialog_check_order);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        TextView tv_message = (TextView) window.findViewById(R.id.tv_dialog_message);
        Button btn_ok = (Button) window.findViewById(R.id.dialog_btn_ok);
        Button btn_cancel = (Button) window.findViewById(R.id.dialog_btn_cancel);
        TextView tv_content = (TextView) window.findViewById(R.id.tv_dialog_content);
        PhotoView photoView = (PhotoView) window.findViewById(R.id.iv_dialog_image);
        if(isShowImage){
            photoView.setVisibility(View.VISIBLE);
            tv_content.setVisibility(View.GONE);
            QiniuUtils.setImageViewByIdFrom7Niu(context,photoView,img_id,900,900,null);
        }else {
            photoView.setVisibility(View.GONE);
            tv_content.setVisibility(View.VISIBLE);
            tv_content.setText(content);
            tv_content.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(contentOnClickListener != null){
                        contentOnClickListener.onClick();
                    }
                }
            });
        }
        btn_ok.setText(positiveText);
        btn_cancel.setText(negativeText);
        tv_message.setText(messageText);

        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                positiveListener.onClick();
            }
        });
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                negativeListener.onClick();
            }
        });
    }
}
