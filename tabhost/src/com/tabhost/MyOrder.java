package com.tabhost;

import java.util.Calendar;
import java.util.Locale;

import org.json.JSONException;
import org.json.JSONObject;

import com.tabhost.util.MTAUtil;
import com.tencent.android.tpush.XGPushClickedResult;
import com.tencent.android.tpush.XGPushManager;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Toast;

public class MyOrder extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.myorder);

	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		XGPushClickedResult click = XGPushManager.onActivityStarted(this);
		if (click != null) { // 判断是否来自信鸽的打开方式
			String customContent = click.getCustomContent(); // 自定义key-value
			if (customContent != null && customContent.length() != 0) {
				try {
					JSONObject jo = new JSONObject(customContent);
					Toast.makeText(this,
							"username:" + jo.getString("username"),
							Toast.LENGTH_LONG).show();
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		XGPushManager.onActivityStoped(this);
	}
}
