package com.djx.pin.utils;

import android.content.Context;
import android.view.WindowManager;


/** �豸 ������,��װ��һЩ���豸�ϵĳ��ò��� */
public class DeviceUtil {
	/** ��λת�� */
	public static int dp2px(Context context, int dp) {
		float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dp * scale + 0.5f);
	}

	/** ��λת�� */
	public static int px2dp(Context context, int px) {
		float scale = context.getResources().getDisplayMetrics().density;
		return (int) (px / scale + 0.5f);
	}

	/** ��Ļ�� */
	public static int getScreenWidthPX(Context context) {
		WindowManager mWindowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		return mWindowManager.getDefaultDisplay().getWidth();
	}

	/** ��Ļ�� */
	public static int getScreenHeightPX(Context context) {
		WindowManager mWindowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		return mWindowManager.getDefaultDisplay().getHeight();
	}

}
