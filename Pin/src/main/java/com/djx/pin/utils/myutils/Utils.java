package com.djx.pin.utils.myutils;

/**
 * Created by 柯传奇 on 2016/10/24 0024.
 */

public class Utils {
    private static long lastClickTime;

    public static boolean isFastDoubleClick() {
        long time = System.currentTimeMillis();
        long timeD = time - lastClickTime;
        if ( 0 < timeD && timeD < 3000) {
            return true;
        }
        lastClickTime = time;
        return false;
    }
}