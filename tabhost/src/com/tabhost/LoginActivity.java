package com.tabhost;

import java.io.File;
import java.util.HashMap;
import com.tabhost.R;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import com.tabhost.util.CodingUtil;
import com.tabhost.util.DialogUtil;
import com.tabhost.util.FileUtil;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class LoginActivity extends Activity {
	private TextView register, editPassword;
	private EditText username, password;

	private Button getCode;
	private Intent intent;
	HashMap<String, String> map;
	String result = "", phoneText, codeText;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		username = (EditText) findViewById(R.id.username);
		password = (EditText) findViewById(R.id.password);
		register = (TextView) findViewById(R.id.register);
		editPassword = (TextView) findViewById(R.id.editPassword);
		register.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(LoginActivity.this,
						ValidateCode.class);
				startActivity(intent);
			}
		});
		editPassword.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(LoginActivity.this,
						ValidateCode.class);
				startActivity(intent);
			}
		});

	}

	public void login(View v) {
		final String usernameText = username.getText().toString().trim();
		String passwordText = password.getText().toString().trim();
		if ("".equals(usernameText) || "".equals(passwordText)) {
			Toast.makeText(this, "用户名和密码不能为空", 0).show();
		} else {
			/*
			 * Intent intent = new Intent(LoginActivity.this,
			 * MainActivity.class); startActivity(intent); finish();
			 */
			// 网络
			final Dialog dialog = DialogUtil.getProgressDialog(this, "登录中...");
			dialog.show();
			String url = ApplicationDemo.URL_SERVLET;
			AsyncHttpClient client = new AsyncHttpClient();
			RequestParams params = new RequestParams();
			params.put("type", "loginVerify");
			params.put("username", usernameText);
			params.put("password", CodingUtil.getMd5_32(passwordText));
			client.post(url, params, new AsyncHttpResponseHandler() {
				public void onSuccess(int statusCode, String content) {
					System.out.println("result===============" + result);
					FileUtil.deleteSP("userInfo");
					FileUtil.setSp(LoginActivity.this, "userInfo",
							"username", usernameText);
					Intent intent = new Intent(LoginActivity.this,
							MainActivity.class);
					startActivity(intent);
					finish();
				}

				public void onFailure(Throwable error, String content) {
					Toast.makeText(LoginActivity.this, "登录失败，请重试", 0).show();
					dialog.dismiss();
				}
			});
		}
	}

	/*
	 * @Override public boolean onKeyDown(int keyCode, KeyEvent event) { if
	 * (keyCode == KeyEvent.KEYCODE_BACK) { return true;//屏蔽后退键 } return
	 * super.onKeyDown(keyCode, event); }
	 */

	@Override
	protected void onPause() {
		super.onPause();
		// SMSSDK.unregisterEventHandler(smsHandler);
	}

	@Override
	protected void onResume() {
		super.onResume();
		// SMSSDK.registerEventHandler(smsHandler);
	}
}
