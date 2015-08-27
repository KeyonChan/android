package com.tabhost.view;

import android.content.Context;
import android.graphics.Canvas;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.MeasureSpec;
/**
 * 增加禁止滑动的方法
 *
 */
public class MyViewPager extends ViewPager {
	private boolean scrollable = true;

	public MyViewPager(Context context) {  
		super(context);  
	}  

	public MyViewPager(Context context, AttributeSet attrs) {  
		super(context, attrs);  
	}  

	public void setScrollable(boolean scrollable){  
		this.scrollable = scrollable;
	}

	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		if (scrollable == false) {
			return false;
		} else {
			return super.onTouchEvent(ev);
		}
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		if (scrollable == false) {
			return false;
		} else {
			return super.onInterceptTouchEvent(ev);
		}

	}


}
