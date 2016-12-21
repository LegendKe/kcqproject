package com.djx.pin.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.view.View;
import android.view.ViewTreeObserver;

import java.io.ByteArrayOutputStream;

/**
 * BitmapUtil
 * 
 * @author yuanc
 * 
 */
public class BitmapUtil {


	/**
	 * Get bitmap from specified image path
	 *
	 * @param imgPath
	 * @return
	 */
	public static Bitmap getBitmap(String imgPath) {
		// Get bitmap through image path
		BitmapFactory.Options newOpts = new BitmapFactory.Options();
		newOpts.inJustDecodeBounds = false;
		newOpts.inPurgeable = true;
		newOpts.inInputShareable = true;
		// Do not compress
		newOpts.inSampleSize = 1;
		newOpts.inPreferredConfig = Bitmap.Config.RGB_565;
		return BitmapFactory.decodeFile(imgPath, newOpts);
	}
	public static byte[] compressBitmap(Bitmap bm) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		int options = 100;
		bm.compress(Bitmap.CompressFormat.PNG, options, baos);
		while (baos.toByteArray().length/1024 > 100){
			baos.reset();
			options -= 20;
			bm.compress(Bitmap.CompressFormat.JPEG, options, baos);
		}
		return baos.toByteArray();
	}


	public static Bitmap loadBitmap(String pathName, SizeMessage sizeMessage) {
		// 加载的图像目标大小
		int targetw = sizeMessage.getW();
		int targeth = sizeMessage.getH();
		Options options = new Options();
		options.inJustDecodeBounds = true; // 打开"边界处理"
		BitmapFactory.decodeFile(pathName, options);
		int resw = options.outWidth;
		int resh = options.outHeight;
		if (resw <= targetw && resh <= targeth) {
			options.inSampleSize = 1; // 设置加载时的资源比例
		}
		// 比例计算
		else {
			int scalew = resw / targetw;
			int scaleh = resh / targeth;
			int scale = scalew > scaleh ? scalew : scaleh;
			options.inSampleSize = scale;// 设置加载时的资源比例
		}
		options.inJustDecodeBounds = false;// 关闭"边界处理"
		Bitmap bitmap = BitmapFactory.decodeFile(pathName, options);
		return bitmap;
	}
	public static Bitmap loadBitmap(int resID, SizeMessage sizeMessage) {
		// 加载的图像目标大小
		int targetw = sizeMessage.getW();
		int targeth = sizeMessage.getH();
		Context context = sizeMessage.getContext();
		Options options = new Options();
		options.inJustDecodeBounds = true; // 打开"边界处理"
		BitmapFactory.decodeResource(context.getResources(), resID, options);
		int resw = options.outWidth;
		int resh = options.outHeight;
		if (resw <= targetw && resh <= targeth) {
			options.inSampleSize = 1; // 设置加载时的资源比例
		}
		// 比例计算
		else {
			int scalew = resw / targetw;
			int scaleh = resh / targeth;
			int scale = scalew > scaleh ? scalew : scaleh;
			options.inSampleSize = scale;// 设置加载时的资源比例
		}
		options.inJustDecodeBounds = false;// 关闭"边界处理"
		Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), resID, options);
		return bitmap;
	}

	public static class SizeMessage {
		private int w;
		private int h;
		private Context context;

		/**
		 * 大小信息
		 * 
		 * @param context
		 * @param isPx
		 *            是否为像素单位
		 *   dp用于布局，sp用于文字
		 * @param w
		 * @param h
		 */
		public SizeMessage(Context context, boolean isPx, int w, int h) {
			this.context = context;
			if (!isPx) {
				w = DeviceUtil.dp2px(context, w);
				h = DeviceUtil.dp2px(context, h);
			}   
			this.w = w;
			this.h = h;
		}

		public Context getContext() {
			return context;
		}

		public int getW() {
			return w;
		}

		public void setW(int w) {
			this.w = w;
		}

		public int getH() {
			return h;
		}

		public void setH(int h) {
			this.h = h;
		}
	}


	public static byte[] Bitmap2Bytes(Bitmap bm) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		bm.compress(Bitmap.CompressFormat.PNG, 100, baos);
		return baos.toByteArray();
	}

	public static void scaleImage(final Activity activity, final View view, int drawableResId) {

		// 获取屏幕的高宽
		Point outSize = new Point();
		activity.getWindow().getWindowManager().getDefaultDisplay().getSize(outSize);
		// 解析将要被处理的图片
		Bitmap resourceBitmap = BitmapFactory.decodeResource(activity.getResources(), drawableResId);
		if (resourceBitmap == null) {
			return;
		}
		// 开始对图片进行拉伸或者缩放
		// 使用图片的缩放比例计算将要放大的图片的高度
		int bitmapScaledHeight = Math.round(resourceBitmap.getHeight() * outSize.x * 1.0f / resourceBitmap.getWidth());
		// 以屏幕的宽度为基准，如果图片的宽度比屏幕宽，则等比缩小，如果窄，则放大
		final Bitmap scaledBitmap = Bitmap.createScaledBitmap(resourceBitmap, outSize.x, bitmapScaledHeight, false);
		view.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
			@Override
			public boolean onPreDraw() {
				//这里防止图像的重复创建，避免申请不必要的内存空间
				if (scaledBitmap.isRecycled())
					//必须返回true
					return true;
				// 当UI绘制完毕，我们对图片进行处理
				int viewHeight = view.getMeasuredHeight();
				// 计算将要裁剪的图片的顶部以及底部的偏移量
				int offset = (scaledBitmap.getHeight() - viewHeight) / 2;
				// 对图片以中心进行裁剪，裁剪出的图片就是非常适合做引导页的图片了
				Bitmap finallyBitmap = Bitmap.createBitmap(scaledBitmap, 0, offset, scaledBitmap.getWidth(),
						scaledBitmap.getHeight() - offset * 2);
				if (!finallyBitmap.equals(scaledBitmap)) {//如果返回的不是原图，则对原图进行回收
					scaledBitmap.recycle();
					System.gc();
				}
				// 设置图片显示
				view.setBackgroundDrawable(new BitmapDrawable(activity.getResources(), finallyBitmap));
				return true;
			}
		});
	}
}
