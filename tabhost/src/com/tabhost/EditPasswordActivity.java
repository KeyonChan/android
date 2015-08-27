package com.tabhost;

import com.tabhost.R;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class EditPasswordActivity extends Activity {
	private EditText password, repassword;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_editpassword);
		password = (EditText) findViewById(R.id.password);
		repassword = (EditText) findViewById(R.id.repassword);

	}

	public void submit(View v) {
		String passwordText = password.getText().toString().trim();
		String repasswordText = repassword.getText().toString().trim();
		if ("".equals(passwordText) || "".equals(repasswordText)) {
			Toast.makeText(EditPasswordActivity.this, "请全部填写，不要留空", 0).show();
		} else if (!passwordText.matches("^([a-zA-Z0-9]{6,10})$")) {
			Toast.makeText(EditPasswordActivity.this, "密码格式有误", 0).show();
		} else if (!repasswordText.equals(passwordText)) {
			Toast.makeText(EditPasswordActivity.this, "两次密码输入不一致", 0).show();
		} else {
			// 网络
			String url = ApplicationDemo.URL_SERVLET;
			AsyncHttpClient client = new AsyncHttpClient();
			RequestParams params = new RequestParams();
			params.put("type", "aaaa");
			params.put("username", "aaaa");
			params.put("password", "aaaa");
			client.post(url, params, new AsyncHttpResponseHandler() {
				public void onSuccess(int statusCode, String content) {
					Toast.makeText(EditPasswordActivity.this, "注册成功", 0).show();
					Intent intent = new Intent(EditPasswordActivity.this,
							LoginActivity.class);
					startActivity(intent);
				}

				public void onFailure(Throwable error, String content) {
					Toast.makeText(EditPasswordActivity.this, "注册失败，请重试", 0)
							.show();
				}
			});

		}
	}
}
