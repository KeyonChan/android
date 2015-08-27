package com.tabhost.util;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Bitmap.Config;
import android.graphics.PorterDuff.Mode;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Environment;
import android.telephony.TelephonyManager;
import android.view.Display;
import android.view.WindowManager;
import android.widget.Toast;
/**
 * 获取文件扩展名，两位小数，像素转化dp，屏幕宽高，状态栏的高度，sd卡状态，音频时长
 */
public class CommonUtil {
	/**
	 * 音频时长
	 */
	public static int getAudioTime(Context context, String path) {
		MediaPlayer mp = MediaPlayer.create(context, Uri.parse(path));
		return mp.getDuration();
	}

	/**
	 * 获取文件扩展名
	 */
	public static String getExt(String path) {
		return path.substring(path.lastIndexOf(".")+1, path.length());
	}
	/**
	 * 两位小数
	 */
	public static String get2weixiaoshu(String price){
		return String.format("%.2f", Float.parseFloat(price));
	}
	/**
	 * 像素转化dp
	 */
	public static int px2dip(Context context, float pxValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (pxValue / scale + 0.5f);
	}


	/**
	 * 缩放至屏幕宽度，图片数据量有一定增减；ImageView无需设置缩放
	 * screen_width -1表示屏幕宽度
	 */
	public static Bitmap getZoomBitmap(Context context, Bitmap bmp, int screen_width) {
		if(screen_width == -1)
			screen_width = getScreenWidth(context);
		int img_width = bmp.getWidth();
		int img_height = bmp.getHeight();
		float scale = (float)screen_width/img_width; //如果图片小,压缩比>1
		System.out.println(img_width+" "+img_height+" 压缩比----"+scale);
		Matrix matrix = new Matrix();
		matrix.postScale(scale, scale);//缩放比例
		return Bitmap.createBitmap(bmp, 0, 0, img_width,
				img_height, matrix, true);
	}
	
	public static int getScreenWidth(Context context) {
		WindowManager manager = (WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE);
		Display display = manager.getDefaultDisplay();
		return display.getWidth();
	}

	public static int getScreenHeight(Context context) {
		WindowManager manager = (WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE);
		Display display = manager.getDefaultDisplay();
		return display.getHeight();
	}

	/**
	 * 状态栏的高度
	 */
	private int getStatusHeight(Activity activity) {
		int statusHeight = 0;
		Rect localRect = new Rect();
		activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(localRect);
		statusHeight = localRect.top;
		if (0 == statusHeight) {
			Class<?> localClass;
			try {
				localClass = Class.forName("com.android.internal.R$dimen");
				Object localObject = localClass.newInstance();
				int i5 = Integer.parseInt(localClass.getField("status_bar_height").get(localObject).toString());
				statusHeight = activity.getResources().getDimensionPixelSize(i5);
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (NumberFormatException e) {
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (SecurityException e) {
				e.printStackTrace();
			} catch (NoSuchFieldException e) {
				e.printStackTrace();
			}
		}
		return statusHeight;
	}
	private Boolean isSDcardEnable(){
		if (!Environment.getExternalStorageDirectory().exists()) {
			return false;
		}
		return true;
	}

}