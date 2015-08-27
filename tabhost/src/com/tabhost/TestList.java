package com.tabhost;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.tabhost.view.XListView;
import com.tabhost.view.XListView.IXListViewListener;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;

public class TestList extends Activity implements IXListViewListener {
	XListView testListView;
	public static List<Map<String, String>> testList;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.testlist);
		testListView = (XListView) findViewById(R.id.testListView);
		testList = new ArrayList<Map<String, String>>();
		Map map1 = new HashMap<String, String>();
		Map map2 = new HashMap<String, String>();
		Map map3 = new HashMap<String, String>();
		testList.add(map1);
		testList.add(map2);
		testList.add(map3);
		testListView.setAdapter(new MyAdapter());
	}

	private class MyAdapter extends BaseAdapter {
		@Override
		public int getCount() {
			return testList.size();
		}

		@Override
		public Object getItem(int position) {
			return testList.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int pos, View convertView, ViewGroup parent) {
			View view = View.inflate(getApplicationContext(),
					R.layout.testlist_item, null);

			return view;
		}
	}

	@Override
	public void onRefresh() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onLoadMore() {
		// TODO Auto-generated method stub

	}
}
