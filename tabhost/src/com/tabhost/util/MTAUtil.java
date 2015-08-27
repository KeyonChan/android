package com.tabhost.util;

import java.util.Properties;

import android.content.Context;

import com.tencent.android.tpush.XGIOperateCallback;
import com.tencent.android.tpush.XGPro;
import com.tencent.android.tpush.XGPushManager;
import com.tencent.stat.StatConfig;
import com.tencent.stat.StatService;

public class MTAUtil {

	public static void mtaInit(Context context) { 
		try {
			// 开启信鸽Pro
			XGPro.enableXGPro(context, true);
			// 开启MTA debug，发布时一定要删除本行或设置为false
			StatConfig.setDebugEnable(true);
		} catch (Exception err) {
			System.out.println("开启信鸽Pro失败");
		}
		StatService.trackCustomEvent(context, "onCreate", "");
		XGPushManager.registerPush(context, "abc",
				new XGIOperateCallback() {
			@Override
			public void onSuccess(Object data, int flag) {
				System.out.println("注册成功，设备token为：" + data);
			}
			@Override
			public void onFail(Object data, int errCode, String msg) {
				System.out.println("注册失败，错误码：" + errCode + ",错误信息：" + msg);
			}
		});
	}
	public static void test(Context context, String test) {
		Properties prop = new Properties(); 
		prop.setProperty("test", test); 
		StatService.trackCustomKVEvent(context, "test", prop); 
	}
	public static void login_success(Context context, String uid) { 
		Properties prop = new Properties(); 
		prop.setProperty("uid", uid); 
		StatService.trackCustomKVEvent(context, "login_success", prop); 
	}
	public static void login_fail_password(Context context) { 
		Properties prop = new Properties(); 
		StatService.trackCustomKVEvent(context, "login_fail_password", prop);
	}
	public static void login_fail_exception(Context context) { 
		Properties prop = new Properties(); 
		StatService.trackCustomKVEvent(context, "login_fail_exception", prop); 
	}
	
	public static void take_order_success(Context context, String uid) { 
		Properties prop = new Properties();
		prop.setProperty("uid", uid);
		StatService.trackCustomKVEvent(context, "take_order_success", prop); 
	}
	public static void take_order_fail(Context context, String uid) { 
		Properties prop = new Properties();
		prop.setProperty("uid", uid);
		StatService.trackCustomKVEvent(context, "take_order_fail", prop); 
	}
	public static void register_success(Context context) { 
		Properties prop = new Properties(); 
		StatService.trackCustomKVEvent(context, "register_success", prop);
	}
	public static void register_fail(Context context) { 
		Properties prop = new Properties(); 
		StatService.trackCustomKVEvent(context, "register_fail", prop);
	}
}
