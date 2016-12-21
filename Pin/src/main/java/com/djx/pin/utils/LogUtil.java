package com.djx.pin.utils;

import android.content.Context;
import android.util.Log;

public class LogUtil {
	private static Boolean isDebug = true;

	public static void e(String String) {
		if (isDebug == true)
			Log.e("==========", "==========" + String);
	}

	public static void e(Context context, String String) {
		if (isDebug == true && context != null)
			Log.e("=========="+context.getClass().getSimpleName(), "==========" + String);
	}

	public static void e(Context context, int Int) {
		if (isDebug == true)
			Log.e("=========="+context.getClass().getSimpleName(), "==========" + Int);
	}

	public static void e(Context context, float Float) {
		if (isDebug == true)
			Log.e("=========="+context.getClass().getSimpleName(), "==========" + Float);
	}

	public static void e(Context context, double Double) {
		if (isDebug == true)
			Log.e("=========="+context.getClass().getSimpleName(), "==========" + Double);
	}
}
