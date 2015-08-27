package com.tabhost.util;

import java.io.File;
import java.io.FileOutputStream;

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
import android.os.Environment;
import android.view.Display;
import android.view.WindowManager;
/**
 * 缩放、项目中图片缩放、本地图片转File、圆形图片
 */
public class ImgUtil {
	/**
	 * (废弃，可用centerCrop替代)正方形裁剪，竖的取顶部，横的取中间
	 */
	public static Bitmap getFangBitmap(Bitmap bmp) {
		int width = bmp.getWidth();
		int height = bmp.getHeight();
		int starX = 0;
		if(width>height){ //横的取中间
			starX = width/4;
			width = height;
		}
		return Bitmap.createBitmap(bmp, starX, 0,width, width);
	}

	/**
	 * 缩放至屏幕宽度，图片数据量有一定增减；ImageView无需设置缩放
	 * screen_width -1表示屏幕宽度
	 */
	public static Bitmap getZoomBitmap(Context context, Bitmap bmp, int screen_width) {
		if(screen_width == -1)
			screen_width = getScreenWidth(context);
		System.out.println("getScreenWidth "+getScreenWidth(context));
		int img_width = bmp.getWidth();
		int img_height = bmp.getHeight();
		float scale = (float)screen_width/img_width; //如果图片小,压缩比>1
		System.out.println(img_width+" "+img_height+" 压缩比----"+scale);
		Matrix matrix = new Matrix();
		matrix.postScale(scale, scale);//缩放比例
		bmp = Bitmap.createBitmap(bmp, 0, 0, img_width,
				img_height, matrix, true);
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(Environment.getExternalStorageDirectory() + "/crop.jpg");
			bmp.compress(Bitmap.CompressFormat.JPEG, 100, fos);  //不压缩是100%，实测75
			fos.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return bmp;
	}

	/**
	 * 项目中图片等比缩放，略大于给定的宽度
	 */
	public static Bitmap getBmpFromBf(Context context, Resources res, int resId, int reqWidth) {
		if(reqWidth == -1)
			reqWidth = getScreenWidth(context);
		final BitmapFactory.Options options = new BitmapFactory.Options(); 
		options.inJustDecodeBounds = true; //第一次禁止为bitmap分配内存，返回值也不再是一个Bitmap对象，而是null
		BitmapFactory.decodeResource(res, resId, options); // 调用上面定义的方法计算inSampleSize值
		options.inSampleSize = getZoomRate(options, reqWidth);// 使用inSampleSize缩放、压缩
		options.inJustDecodeBounds = false;
		return BitmapFactory.decodeResource(res, resId, options); //第二次解析
	}
	
	/**
	 * 本地图片转File：先压缩在保存本地在file取出
	 */
	public static File getCompressBitmapToFile(String path) {
		Bitmap bmp = BitmapFactory.decodeFile(path);
		int img_width = bmp.getWidth();
		int img_height = bmp.getHeight();
		int minVal = img_width<img_height?img_width:img_height;
		int maxVal = img_width>img_height?img_width:img_height;
		if(minVal>1000){
			float scale = (float)1000/maxVal; //如果图片小,压缩比>1
			System.out.println(img_width+" "+img_height+" 压缩比----"+scale);
			Matrix matrix = new Matrix();
			matrix.postScale(scale, scale);//缩放比例
			bmp = Bitmap.createBitmap(bmp, 0, 0, img_width,
					img_height, matrix, true);
			path = Environment.getExternalStorageDirectory() + "/"+System.currentTimeMillis()+".jpg";
			FileOutputStream fos = null;
			try {
				fos = new FileOutputStream(path);
				bmp.compress(Bitmap.CompressFormat.JPEG, 100, fos);
				fos.flush();
				fos.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		bmp.recycle(); //回收
		return new File(path);
	}

	public static int getZoomRate(BitmapFactory.Options options, int reqWidth) {
		int width = options.outWidth;
		int height = options.outHeight;
		int maxVal = width>height?width:height;
		int inSampleSize = 1;
		if (width > reqWidth) {
			inSampleSize = Math.round((float) maxVal / (float) reqWidth);
		}
		return inSampleSize;
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
	 * 圆形图片
	 * 
	 * @param bitmap
	 *            传入Bitmap对象
	 * 如果是 width <= height时，则会裁剪高度，裁剪的区域是宽度不变高度从顶部到宽度width的长度；
	 * 如果 width > height，则会裁剪宽度，裁剪的区域是高度不变，宽度是取的图片宽度的中心区域
	 * @return
	 */
	public static Bitmap getRoundBitmap(Bitmap bitmap) {
		int width = bitmap.getWidth();
		int height = bitmap.getHeight();
		float roundPx;
		float left, top, right, bottom, dst_left, dst_top, dst_right, dst_bottom;
		if (width <= height) {
			roundPx = width / 2;
			float clip = (height - width) / 2;
			top = clip;
			bottom = height - clip;
			left = 0;
			right = width;
			height = width;
			dst_left = 0;
			dst_top = 0;
			dst_right = width;
			dst_bottom = width;
		} else {
			roundPx = height / 2;
			float clip = (width - height) / 2;
			left = clip;
			right = width - clip;
			top = 0;
			bottom = height;
			width = height;
			dst_left = 0;
			dst_top = 0;
			dst_right = height;
			dst_bottom = height;
		}
		Bitmap output = Bitmap.createBitmap(width, height, Config.ARGB_8888);
		Canvas canvas = new Canvas(output);
		final int whiteColor = 0xff424242;
		final Paint paint = new Paint();
		//整个被截取的图片
		final Rect src = new Rect((int) left, (int) top, (int) right,
				(int) bottom);
		//整个矩形
		final Rect dst = new Rect((int) dst_left, (int) dst_top,
				(int) dst_right, (int) dst_bottom);
		final RectF rectF = new RectF(dst);

		paint.setAntiAlias(true);
		canvas.drawARGB(0, 0, 0, 0);
		paint.setColor(whiteColor);
		canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		canvas.drawBitmap(bitmap, src, rectF, paint);
		return output;
	}
}
