package com.tabhost.util;

import com.tabhost.R;
import android.os.CountDownTimer;
import android.widget.Button;

/**
 * 调用new Daojishi(30000, 1000, hide).start();
 *
 */
public class DaojishiUtil extends CountDownTimer {
	
	private Button btn;
	private int normalColor = R.color.top, timingColor = R.color.gray_nomal;// 未计时的文字颜色，计时期间的文字颜色

	public DaojishiUtil(long millisInFuture, long countDownInterval, Button btn) {
		super(millisInFuture, countDownInterval);
		this.btn = btn;
	}

	// 计时完毕时触发
	@Override
	public void onFinish() {
		if (normalColor > 0) {
			btn.setTextColor(normalColor);
		}
		btn.setText("点击重新发送");
		btn.setEnabled(true);
	}

	// 计时过程显示
	@Override
	public void onTick(long millisUntilFinished) {
		btn.setEnabled(false);
		btn.setText(millisUntilFinished / 1000 + "秒后重新发送");
	}

}
