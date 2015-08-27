package com.tabhost.view;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;

import com.android.volley.toolbox.ImageLoader;
import com.tabhost.R;
import com.tabhost.util.VolleyHelper;

/**
 * @version 创建时间：2015年4月22日 下午4:16:31 类说明 ViewPager实现的轮播图广告自定义视图，如京东首页的广告轮播图效果；
 *          既支持自动轮播页面也支持手势滑动切换页面
 */
public class SlideShowView extends FrameLayout {


	//轮播图图片数量
	private final static int IMAGE_COUNT = 5;
	//自动轮播的时间间隔
	private final static int TIME_INTERVAL = 5;
	//自动轮播启用开关
	private static boolean isAutoPlay = true;
	//是否加点
	private static boolean isDot = true;
	//自定义轮播图的资源
	private List<String> imageUrls;
	//放轮播图片的ImageView 的list
	private List<MyNetworkImageView> imageViewsList;
	//放圆点的View的list
	private List<View> dotViewsList;

	private ViewPager viewPager;
	//当前轮播页
	private int currentItem  = 0;
	//定时任务
	private ScheduledExecutorService scheduledExecutorService;

	private Context context;

	private SlideShowTask slideShowTask;
	VolleyHelper volleyHelper;
	//Handler
	private Handler handler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			viewPager.setCurrentItem(currentItem);
		}
	};

	public SlideShowView(Context context) {
		this(context,null);
		// TODO Auto-generated constructor stub
	}
	public SlideShowView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
		// TODO Auto-generated constructor stub
	}
	public SlideShowView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		/*this.context = context;
        initImageLoader(context);
        initData();
        if(isAutoPlay){ //自动播放
            startPlay();
        }*/
	}

	//自定义初始化，原来是在构造方法中初始化
	public void myInit(Context context, List<String> imageUrls, boolean isAutoPlay, boolean isDot){
		this.context = context;
		this.isAutoPlay = isAutoPlay;
		this.imageUrls = imageUrls;
		this.isDot = isDot;
		volleyHelper = VolleyHelper.getInstance(context);
		initUI(context);
		if(isAutoPlay){ //自动播放
			startPlay();
		}
	}

	/**
	 * 开始轮播图切换
	 */
	private void startPlay(){
		scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
		slideShowTask=new SlideShowTask();
		scheduledExecutorService.scheduleAtFixedRate(slideShowTask, 1, 4, TimeUnit.SECONDS);
	}
	/**
	 * 停止轮播图切换
	 */
	private void stopPlay(){
		scheduledExecutorService.shutdown();
	}
	/**
	 * 初始化Views等UI
	 */
	private void initUI(Context context){
		imageViewsList = new ArrayList<MyNetworkImageView>();
		dotViewsList = new ArrayList<View>();
		if(imageUrls == null || imageUrls.size() == 0)
			return;
		LayoutInflater.from(context).inflate(R.layout.layout_slideshow, this, true);
		LinearLayout dotLayout = (LinearLayout)findViewById(R.id.dotLayout);
		dotLayout.removeAllViews();

		// 热点个数与图片个数相等
		for (int i = 0; i < imageUrls.size(); i++) {
			MyNetworkImageView view =  new MyNetworkImageView(context);
			view.setTag(imageUrls.get(i));
			view.setBackgroundResource(R.drawable.loading); //默认图片
			view.setScaleType(ImageView.ScaleType.FIT_XY);
			imageViewsList.add(view);

			ImageView dotView =  new ImageView(context);
			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
			params.leftMargin = 4;
			params.rightMargin = 4;
			if(isDot){
				//第一图片加点
				if(i==0)
					dotView.setBackgroundResource(R.drawable.dot_focus);
				else
					dotView.setBackgroundResource(R.drawable.dot_blur);
			}
			dotLayout.addView(dotView, params);
			dotViewsList.add(dotView);
		}
		viewPager = (ViewPager) findViewById(R.id.viewPager);
		viewPager.setFocusable(true);

		viewPager.setAdapter(new MyPagerAdapter());
		viewPager.setOnPageChangeListener(new MyPageChangeListener());
	}

	/**
	 * 填充ViewPager的页面适配器
	 *
	 */
	private class MyPagerAdapter extends PagerAdapter {
		@Override
		public void destroyItem(View container, int position, Object object) {
			// TODO Auto-generated method stub
			//((ViewPag.er)container).removeView((View)object);
			((ViewPager)container).removeView(imageViewsList.get(position));
		}
		@Override
		public Object instantiateItem(View container, int position) {
			MyNetworkImageView imageView = imageViewsList.get(position);
			volleyHelper.showVolleyImg(imageView, imageView.getTag()+"",0,-1);
			((ViewPager)container).addView(imageViewsList.get(position));
			return imageViewsList.get(position);
		}
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return imageViewsList.size();
		}
		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			// TODO Auto-generated method stub
			return arg0 == arg1;
		}
		@Override
		public void restoreState(Parcelable arg0, ClassLoader arg1) {
			// TODO Auto-generated method stub

		}
		@Override
		public Parcelable saveState() {
			// TODO Auto-generated method stub
			return null;
		}
	}
	/**
	 * ViewPager的监听器
	 * 当ViewPager中页面的状态发生改变时调用
	 *
	 */
	private class MyPageChangeListener implements ViewPager.OnPageChangeListener{

		boolean isAutoPlay = false;

		@Override
		public void onPageScrollStateChanged(int arg0) {
			// TODO Auto-generated method stub
			switch (arg0) {
			case 1:// 手势滑动，空闲中
				isAutoPlay = false;
				break;
			case 2:// 界面切换中
				isAutoPlay = true;
				break;
			case 0:// 滑动结束，即切换完毕或者加载完毕
				// 当前为最后一张，此时从右向左滑，则切换到第一张
				if (viewPager.getCurrentItem() == viewPager.getAdapter().getCount() - 1 && !isAutoPlay) {
					viewPager.setCurrentItem(0);
				}
				// 当前为第一张，此时从左向右滑，则切换到最后一张
				else if (viewPager.getCurrentItem() == 0 && !isAutoPlay) {
					viewPager.setCurrentItem(viewPager.getAdapter().getCount() - 1);
				}
				break;
			}
		}
		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
			// TODO Auto-generated method stub

		}
		@Override
		public void onPageSelected(int pos) {
			// TODO Auto-generated method stub
			currentItem = pos;
			if(isDot){
				for(int i=0;i < dotViewsList.size();i++){
					if(i == pos){
						((View)dotViewsList.get(pos)).setBackgroundResource(R.drawable.dot_focus);
					}else {
						((View)dotViewsList.get(i)).setBackgroundResource(R.drawable.dot_blur);
					}
				}
			}
		}
	}

	/**
	 *执行轮播图切换任务
	 *
	 */
	private class SlideShowTask implements Runnable{
		@Override
		public void run() {
			// TODO Auto-generated method stub
			synchronized (viewPager) {
				currentItem = (currentItem+1)%imageViewsList.size();
				handler.obtainMessage().sendToTarget();
			}
		}

	}

	/**
	 * 销毁ImageView资源，回收内存
	 *
	 */
	public void destoryBitmaps() {
		for (int i = 0; i < IMAGE_COUNT; i++) {
			ImageView imageView = imageViewsList.get(i);
			Drawable drawable = imageView.getDrawable();
			if (drawable != null) {
				//解除drawable对view的引用
				drawable.setCallback(null);
			}
		}
	}


	/**
	 * 异步任务,获取数据
	 *
	 */
	/*class GetListTask extends AsyncTask<String, Integer, Boolean> {

        @Override
        protected Boolean doInBackground(String... params) {
            try {
                // 这里一般调用服务端接口获取一组轮播图片，下面是从百度找的几个图片

                imageUrls = new String[]{
                        "http://image.zcool.com.cn/56/35/1303967876491.jpg",
                        "http://image.zcool.com.cn/59/54/m_1303967870670.jpg",
                        "http://image.zcool.com.cn/47/19/1280115949992.jpg",
                        "http://image.zcool.com.cn/59/11/m_1303967844788.jpg"
                };
                return true;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            if (result) {
                initUI(context);
            }
        }
    }*/
}