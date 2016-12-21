package com.djx.pin.utils;

import android.widget.EditText;

/**
 * Created by Administrator on 2016/7/13 0013.
 */
public class EditTextHelper {

    /**edittext中的长度*/
    public static int getEidtTextLength(EditText editText){
        int length=editText.length();
        return length;
    }

    /**edittext中去掉空格后的字符串长度 */
    public static int getedtLengthAfterTrim(EditText editText){
        int length=editText.getText().toString().trim().length();
        return length;
    }
}
