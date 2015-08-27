package com.tabhost;

import com.tabhost.util.FileUtil;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;

/**
 * @version 创建时间：2015年4月21日 下午11:38:31 类说明 过度Activity
 */
public class SplashActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mHandler.sendEmptyMessageDelayed(0, 200);
	}

	@SuppressLint("HandlerLeak")
	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			// 判断登录状态
			Intent intent = new Intent();
			if (FileUtil.isSpExist("userInfo")) {
				intent.setClass(SplashActivity.this, MainActivity.class);
				startActivity(intent);
			} else {
				intent.setClass(SplashActivity.this, LoginActivity.class);
				startActivity(intent);
			}
			finish();
		}
	};

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			return false;
		}
		return false;
	}
}
