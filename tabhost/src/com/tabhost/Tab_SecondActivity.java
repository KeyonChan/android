package com.tabhost;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.tabhost.R;
import com.tabhost.service.NetService;
import com.tabhost.util.CommonUtil;
import com.tabhost.util.DateUtil;
import com.tabhost.util.DialogUtil;
import com.tabhost.util.NetUtil;
import com.tabhost.view.XListView;
import com.tabhost.view.XListView.IXListViewListener;
import android.app.Dialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class Tab_SecondActivity extends BaseActivity implements
		IXListViewListener {

	private XListView testListView;
	public static ArrayList<Map<String, String>> testList;
	private Map<Integer, View> viewCacheMap;
	private ImageView search, release;

	private Button reload;
	private MyAdapter adapter;
	int pageIndex = 0, total = 0, currentCount = -1, dialogCount = 0;

	String type = "load", tradeTime = "";
	private Handler mHandler;
	Dialog dialog;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.tab_second);
		search = (ImageView) findViewById(R.id.search);
		release = (ImageView) findViewById(R.id.release);
		reload = (Button) findViewById(R.id.reload);
		testListView = (XListView) findViewById(R.id.testListView);
		testList = new ArrayList<Map<String, String>>();
		viewCacheMap = new HashMap<Integer, View>();
		search.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
			}
		});
		release.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				/*
				 * Intent intent = new
				 * Intent(Tab_SecondActivity.this,CategoryActivity.class);
				 * startActivity(intent);
				 */
			}
		});
		testListView.setPullLoadEnable(true);
		testListView.setXListViewListener(this);
		mHandler = new Handler();
		dialog = DialogUtil.getProgressDialog(this, "加载中...");
		loadList();
	}

	private void loadList() {
		if (NetUtil.isNetAvailable(this)) {
			// 没有更多
			if (currentCount >= total) {
				Toast.makeText(Tab_SecondActivity.this, "没有更多", 0).show();
				finishLoading();
				testListView.setNoMore();
				testListView.setPullLoadEnable(false);
			} else {
				dialog.show();
				AsyncHttpClient client = new AsyncHttpClient();
				client.setTimeout(10000);
				RequestParams params = new RequestParams();
				params.put("type", "findUserByPage");
				params.put("pageIndex", pageIndex + "");
				params.put("pageSize", ApplicationDemo.PAGESIZE + "");
				// System.out.println(tradeTime+" "+pageIndex);
				client.post(ApplicationDemo.URL_SERVLET, params,
						new AsyncHttpResponseHandler() {
							public void onSuccess(int statusCode, String content) {
								if ("1".equals(NetService
										.getReturnStatus(content))) {
									System.out
											.println("pageIndex================"
													+ pageIndex
													+ "  content================"
													+ content);
									NetService.addToList(testList,
											NetService.getReturnRows(content));
									total = Integer.parseInt(NetService
											.getReturnTotal(content));
									currentCount = (pageIndex + 1)
											* ApplicationDemo.PAGESIZE; // 当前条数
									if ("load".equals(type)) {
										if (pageIndex == 0) {
											adapter = new MyAdapter();
											testListView.setAdapter(adapter);
										} else {
											adapter.notifyDataSetChanged();
										}
										pageIndex++; // 返回成功后+1，不应该放在加载事件中
									} else {
										System.out
												.println("refresh====================================");

										adapter = new MyAdapter();
										testListView.setAdapter(adapter);
										pageIndex++; // 下拉时置为0，下拉后成功+1
										reload.setVisibility(View.GONE);
										testListView
												.setVisibility(View.VISIBLE);
									}
								} else {
									Toast.makeText(Tab_SecondActivity.this,
											NetService.getReturnMsg(content), 0)
											.show();
								}
								finishLoading();
								dialog.dismiss();
								if (dialogCount == 2)
									dialogCount = 0;
							}

							public void onFailure(Throwable error,
									String content) {
								Toast.makeText(Tab_SecondActivity.this,
										"加载异常，请重试", 0).show();
								finishLoading();
								dialog.dismiss();
								reload.setVisibility(View.VISIBLE);
								testListView.setVisibility(View.GONE);
							}
						});
			}
		} else {
			if (testList.size() == 0) { // 一开始就没网的情况，否则保留原列表
				reload.setVisibility(View.VISIBLE);
				testListView.setVisibility(View.GONE);
			}
			// dialog.dismiss();
			finishLoading();
		}
	}

	private void finishLoading() {
		testListView.stopRefresh();
		testListView.stopLoadMore();
		testListView.setRefreshTime(DateUtil.getNow());
	}

	@Override
	public void onRefresh() {
		mHandler.postDelayed(new Runnable() {
			@Override
			public void run() {
				total = 0;
				currentCount = -1;
				type = "refresh";
				testListView.setHasMore(); // 可能全加载完，需重置
				testListView.setPullLoadEnable(true);
				// 有网状态才清空列表、从第一页开始搜索；无网保持原列表、当前页码
				if (NetUtil.isNetAvailable(Tab_SecondActivity.this)) {
					testList.clear();
					pageIndex = 0;
				}
				loadList();
			}
		}, 1000);
	}

	@Override
	public void onLoadMore() {
		// testListView.setPullLoadEnable(false); //全部加载完上拉不可用
		mHandler.postDelayed(new Runnable() {
			@Override
			public void run() {
				type = "load";
				loadList();
			}
		}, 1000);
	}

	public void reload(View view) {
		onRefresh();
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
		public View getView(final int pos, View convertView, ViewGroup parent) {
			View view = viewCacheMap.get(pos);
			if (view == null) {
				view = View.inflate(getApplicationContext(),
						R.layout.item_demand, null);
				TextView username = (TextView) view.findViewById(R.id.username);
				username.setText(testList.get(pos).get("username"));
				view.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View arg0) {
						/*
						 * Intent intent = new
						 * Intent(Tab_ThirdActivity.this,DemandDetailActivity
						 * .class); intent.putExtra("pos", pos);
						 * intent.putExtra("specWeight",
						 * testList.get(pos).get("SpecWeight"));
						 * startActivity(intent);
						 */
					}
				});
				viewCacheMap.put(pos, view);
			} else {
				System.out.println("view缓存=================");
			}
			return view;
		}
	}

}