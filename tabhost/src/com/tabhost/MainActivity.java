package com.tabhost;

import com.tabhost.R;
import com.tabhost.util.CommonUtil;
import com.tabhost.util.MTAUtil;

import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TabHost;
import android.widget.TextView;

public class MainActivity extends TabActivity {
	/** Called when the activity is first created. */
	private TabHost tabHost;
	private TextView msg_count;
	TabHost.TabSpec spec;
	Intent intent;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);
		msg_count = (TextView) findViewById(R.id.msg_count); // 消息数
		msg_count.setVisibility(View.VISIBLE);
		msg_count.setText("10");

		tabHost = (TabHost) findViewById(android.R.id.tabhost);

		intent = new Intent().setClass(this, Tab_IndexActivity.class);
		spec = tabHost.newTabSpec("首页").setIndicator("首页").setContent(intent);
		tabHost.addTab(spec);
		intent = new Intent().setClass(this, Tab_SecondActivity.class);
		spec = tabHost.newTabSpec("第二个").setIndicator("第二个").setContent(intent);
		tabHost.addTab(spec);
		intent = new Intent().setClass(this, Tab_ThirdActivity.class);
		spec = tabHost.newTabSpec("第三个").setIndicator("第三个").setContent(intent);
		tabHost.addTab(spec);
		intent = new Intent().setClass(this, Tab_SettingActivity.class);
		spec = tabHost.newTabSpec("我的").setIndicator("我的").setContent(intent);
		tabHost.addTab(spec);
		// tabHost.setCurrentTab(1); 选中某一个

		RadioGroup radioGroup = (RadioGroup) this.findViewById(R.id.tab_group);
		radioGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				switch (checkedId) {
				case R.id.tab_index:
					tabHost.setCurrentTabByTag("首页"); // 这里的标签要与上面对应
					break;
				case R.id.tab_second:
					tabHost.setCurrentTabByTag("第二个");
					break;
				case R.id.tab_third:
					tabHost.setCurrentTabByTag("第三个");
					break;
				case R.id.tab_setting:
					tabHost.setCurrentTabByTag("我的");
					break;
				default:
					break;
				}
			}
		});
	}
}