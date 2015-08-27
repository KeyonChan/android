package com.tabhost.util;

import java.util.Map;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.util.LruCache;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.Request.Method;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.android.volley.toolbox.ImageLoader.ImageContainer;
import com.android.volley.toolbox.ImageLoader.ImageListener;
import com.lidroid.xutils.BitmapUtils;
import com.renn.rennsdk.param.GetAppParam;
import com.tabhost.R;
import com.tabhost.other.LruImageCache;
import com.tabhost.view.MyNetworkImageView;
import com.tabhost.view.RoundedImageView;

/**
 * volley工具类，单例
 */
public class VolleyHelper {
	private static VolleyHelper volleyHelper;
	private static  RequestQueue mRequestQueue;
	private LruImageCache mLruImageCache;
	private  ImageLoader mImageLoader;
	private static  Context context;
	private  Bitmap defaultBmp, errorBmp, returnBmp;
	static BitmapUtils bitmapUtils;
	private VolleyHelper(Context context) {
		this.context = context;
		mRequestQueue = Volley.newRequestQueue(context);
		mLruImageCache = LruImageCache.getInstance();
		mImageLoader = new ImageLoader(mRequestQueue,mLruImageCache);
		if (defaultBmp == null) {
			defaultBmp = BitmapFactory.decodeResource(context.getResources(), R.drawable.loading);
		}
		if (errorBmp == null) {
			errorBmp = BitmapFactory.decodeResource(context.getResources(), R.drawable.img_error);
		}
	}

	public static VolleyHelper getInstance(Context context) {
		if (volleyHelper == null) {
			bitmapUtils = new BitmapUtils(context);

			synchronized (VolleyHelper.class) {
				if (volleyHelper == null) {
					volleyHelper = new VolleyHelper(context);
				}
			}
		}
		return volleyHelper;
	}

	/*public static void addToRequestQueue(StringRequest req) {
		mRequestQueue.add(req);
	}*/

	public static void getVolleyReq(String url, final Map<String, String> param, int timeout, Response.Listener<String> sucListener,final String errorTip) {
		StringRequest request = new StringRequest(Method.POST, url,
				sucListener, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError arg0) {
				if(errorTip!=null && !"".equals(errorTip))
					Toast.makeText(context, errorTip, 0).show();
			}
		}) {
			@Override
			protected Map<String, String> getParams()
					throws AuthFailureError {
				return param;
			}
		};
		request.setRetryPolicy(new DefaultRetryPolicy(1000*timeout, 1, 1.0f));
		mRequestQueue.add(request);
	}

	/**
	 * 缩放、圆形
	 */
	public void showVolleyImg(MyNetworkImageView img, String url, int type, int reqWidth){
		img.setDefaultImageResId(R.drawable.loading);
		img.setErrorImageResId(R.drawable.img_error);
		img.setImageUrl(url, mImageLoader, type, reqWidth);
	}
	
	public void showRoundImg(ImageView img, String url){
		bitmapUtils.display(img, url);
	}
}
