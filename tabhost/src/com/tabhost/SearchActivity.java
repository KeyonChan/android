package com.tabhost;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.tabhost.util.FileUtil;
import com.tabhost.view.XListView;
import com.tabhost.view.XListView.IXListViewListener;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.Toast;

public class SearchActivity extends Activity {
	ListView historyListView;
	AutoCompleteTextView search;
	Handler mHandler;
	long currentTimestamp = System.currentTimeMillis();;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search);
		historyListView = (ListView) findViewById(R.id.historyListView);
		search = (AutoCompleteTextView) findViewById(R.id.search);
		mHandler = new Handler();
		FileUtil.setSp(this, "userInfo", "history", "乐事,康师傅");
		String[] dataArr = FileUtil.getSp(this, "userInfo", "history")
				.split(",");
		System.out.println(dataArr[0]);
		ArrayAdapter adapter = new ArrayAdapter(this,
				android.R.layout.simple_list_item_1, dataArr);
		historyListView.setAdapter(adapter);

		search.addTextChangedListener(new TextWatcher() {
			public void afterTextChanged(final Editable text) {
				if (System.currentTimeMillis() - currentTimestamp > 2000) { // 超过2秒，一输入就触发
					mHandler.postDelayed(new Runnable() {
						@Override
						public void run() {
							Toast.makeText(getApplicationContext(),
									"变化后:" + text, Toast.LENGTH_SHORT).show();
						}
					}, 1500); // 满足2秒条件，在等待1.5秒提交
				}
				currentTimestamp = System.currentTimeMillis(); // 确保连续输入不被触发
			}

			public void beforeTextChanged(CharSequence text, int start,
					int count, int after) {
			}

			public void onTextChanged(CharSequence text, int start, int before,
					int count) {

				/*
				 * mHandler.postDelayed(new Runnable() {
				 * 
				 * @Override public void run() {
				 * Toast.makeText(getApplicationContext(), "变化后:"+text,
				 * Toast.LENGTH_SHORT).show(); } }, 1500);
				 */
			}
		});
	}

}
