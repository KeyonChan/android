package com.tabhost;

import com.tabhost.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class ValidateCode extends Activity {
	private Button getVerifycode;
	private EditText phone, verifycode;
	private int CHANGE_CODE = 0, RESUME_CODE = 1, SHOWTEXT = 2;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_validatecode);
		getVerifycode = (Button) findViewById(R.id.getVerifycode);
		phone = (EditText) findViewById(R.id.phone);
		verifycode = (EditText) findViewById(R.id.verifycode);
	}

	Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			if (msg.what == CHANGE_CODE) {
				getVerifycode.setBackgroundColor(getResources().getColor(
						R.color.gray_nomal));
				getVerifycode.setText((Integer) msg.obj + "秒后重新获取");
				getVerifycode.setClickable(false);
			} else if (msg.what == RESUME_CODE) {
				getVerifycode.setBackgroundColor(getResources().getColor(
						R.color.top));
				getVerifycode.setText("重新获取验证码");
				getVerifycode.setClickable(true);
			} else if (msg.what == SHOWTEXT) {
				Toast.makeText(ValidateCode.this, "验证码有误", 0).show();
			}
		}
	};

	public void getVerifycode(View v) {
		String phoneText = phone.getText().toString().trim();
		if ("".equals(phoneText)) {
			Toast.makeText(this, "手机号不能为空", 0).show();
		} else if (!phoneText.matches("^([1][358]\\d{9})$")) {
			Toast.makeText(this, "手机号格式不正确", 0).show();
		} else {
			new Thread() {
				public void run() {
					for (int i = 30; i > 0; i--) {
						Message msg = Message.obtain();
						msg.what = CHANGE_CODE;
						msg.obj = i;
						handler.sendMessage(msg);
						try {
							Thread.sleep(1000);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
					Message msg = Message.obtain();
					msg.what = RESUME_CODE;
					handler.sendMessage(msg);
				}
			}.start();
		}
	}

	public void submit(View v) {
		String phoneText = phone.getText().toString().trim();
		String verifycodeText = verifycode.getText().toString().trim();
		if ("".equals(phoneText) || "".equals(verifycodeText)) {
			Toast.makeText(ValidateCode.this, "请全部填写，不要留空", 0).show();
		} else {
			Intent intent = new Intent(ValidateCode.this,
					EditPasswordActivity.class);
			intent.putExtra("phone", phoneText);
			startActivity(intent);
		}

	}
}