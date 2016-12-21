package com.djx.pin.utils;
/**
 * 状�?�栏工具�?
 */
import java.lang.reflect.Field;

import android.app.Activity;
import android.view.Window;
import android.view.WindowManager;

public class StatusBar {

	
	public static void setStatus(Activity activity){
		
		// 隐藏标题�?
		activity.requestWindowFeature(Window.FEATURE_NO_TITLE);
				// 透明状�?�栏
		activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
				// 透明导航�?
		activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
		
		try {
			WindowManager.LayoutParams lp = activity.getWindow().getAttributes();
			Field meizuFlags = WindowManager.LayoutParams.class
					.getDeclaredField("meizuFlags");
			int newFlag = meizuFlags.getInt(lp) | 0x200;
			meizuFlags.set(lp, newFlag);
			activity.getWindow().setAttributes(lp);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
