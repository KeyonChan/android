package com.tabhost;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;
import com.lidroid.xutils.BitmapUtils;
import com.tabhost.other.LruImageCache;
import com.tabhost.util.VolleyHelper;
import com.tabhost.view.MyGridView;
import com.tabhost.view.MyListView;
import com.tabhost.view.MyNetworkImageView;
import com.tabhost.view.RoundedImageView;
import com.tabhost.view.SlideShowView;
import com.tabhost.view.XListView;
import com.tabhost.view.XListView.IXListViewListener;
/**
 * listview 加头部，实现整体下拉刷新
 */
public class TestListview extends Activity implements IXListViewListener{
	XListView listView;
	List<String> xqList, scrollList, gridList;
	Map<Integer,View> cacheMap,cacheMap_grid;
	VolleyHelper volleyHelper;
	TextView tv;
	Handler handler;

	RequestQueue queue;
	private ImageLoader imageLoader = null;
	BitmapUtils bitmapUtils;
	ListAdapter listAdapter;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.test_listview);
		listView = (XListView) findViewById(R.id.listView);
		listView.setPullLoadEnable(true);
		listView.setXListViewListener(this);
		handler = new Handler();
		volleyHelper = VolleyHelper.getInstance(this);
		bitmapUtils = new BitmapUtils(this);
		xqList = new ArrayList<String>();
		scrollList = new ArrayList<String>();
		gridList = new ArrayList<String>();
		cacheMap = new HashMap<Integer,View>();
		cacheMap_grid = new HashMap<Integer,View>();
		xqList.add("http://b.hiphotos.baidu.com/image/pic/item/dcc451da81cb39dbffd1df86d4160924aa1830c8.jpg");
		xqList.add("http://a.hiphotos.baidu.com/image/pic/item/8326cffc1e178a8291f89c70f203738da877e81e.jpg");
		xqList.add("http://e.hiphotos.baidu.com/image/pic/item/730e0cf3d7ca7bcb6e7f28c3ba096b63f724a8d2.jpg");
		xqList.add("http://b.hiphotos.baidu.com/image/pic/item/d439b6003af33a87a0073abec35c10385343b52f.jpg");
		xqList.add("http://c.hiphotos.baidu.com/image/pic/item/8435e5dde71190ef64cdd1decc1b9d16fdfa60f8.jpg");
		xqList.add("http://e.hiphotos.baidu.com/image/pic/item/34fae6cd7b899e517b1461e241a7d933c9950da9.jpg");
		xqList.add("http://f.hiphotos.baidu.com/image/pic/item/83025aafa40f4bfb6e4ff1c3004f78f0f636188d.jpg");
		xqList.add("http://a.hiphotos.baidu.com/image/pic/item/fd039245d688d43f975419567f1ed21b0ff43b90.jpg");
		scrollList.add("http://image.zcool.com.cn/56/35/1303967876491.jpg");
		scrollList.add("http://image.zcool.com.cn/59/54/m_1303967870670.jpg");
		scrollList.add("http://image.zcool.com.cn/47/19/1280115949992.jpg");
		scrollList.add("http://image.zcool.com.cn/59/11/m_1303967844788.jpg");
		gridList.add("http://g.hiphotos.baidu.com/image/pic/item/bd315c6034a85edf2308262a4b540923dd547537.jpg");
		gridList.add("http://c.hiphotos.baidu.com/image/pic/item/7c1ed21b0ef41bd577eb24fd53da81cb39db3d7b.jpg");
		gridList.add("http://g.hiphotos.baidu.com/image/pic/item/0824ab18972bd407e3e5eff479899e510fb3099c.jpg");
		gridList.add("http://imgt6.bdstatic.com/it/u=2,957057594&fm=25&gp=0.jpg");
		gridList.add("http://b.hiphotos.baidu.com/image/pic/item/29381f30e924b89998e8a5256c061d950b7bf6d3.jpg");
		gridList.add("http://h.hiphotos.baidu.com/image/pic/item/32fa828ba61ea8d34a9ab80d950a304e251f587b.jpg");
		gridList.add("http://c.hiphotos.baidu.com/image/pic/item/43a7d933c895d143df00a62a71f082025aaf0740.jpg");
		gridList.add("http://f.hiphotos.baidu.com/image/pic/item/08f790529822720e610006427acb0a46f31fabb8.jpg");
		listAdapter = new ListAdapter();
		listView.setAdapter(listAdapter);
		initview();
	}

	private void initview() {
		//头部（滚图、图片列表）
		View layout_head = View.inflate(getApplicationContext(),R.layout.layout_head, null);
		SlideShowView slideshowView = (SlideShowView) layout_head.findViewById(R.id.slideshowView);
		LinearLayout content = (LinearLayout) layout_head.findViewById(R.id.content);
		slideshowView.myInit(this, scrollList, true, true);
		for (int i = 0; i < xqList.size(); i++) {
			MyNetworkImageView img = new MyNetworkImageView(this);
			volleyHelper.showVolleyImg(img, xqList.get(i), 2, -1);
			content.addView(img);
		}
		listView.addHeaderView(layout_head);
	}

	class ListAdapter extends BaseAdapter{
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return xqList.size();
		}
		@Override
		public Object getItem(int arg0) {
			// TODO Auto-generated method stub
			return null;
		}
		@Override
		public long getItemId(int arg0) {
			// TODO Auto-generated method stub
			return 0;
		}
		@Override
		public View getView(int positon, View convertView, ViewGroup parent) {
			System.out.println("111111111111111111111");
			convertView = cacheMap.get(positon);
			if(convertView == null){

				convertView = View.inflate(getApplicationContext(),R.layout.item_multi_list, null);
				MyGridView gridview = (MyGridView) convertView.findViewById(R.id.gridview);
				gridview.setAdapter(new GridAdapter());
				cacheMap.put(positon, convertView);
			}
			return convertView;
		}
	}

	class GridAdapter extends BaseAdapter{
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return gridList.size();
		}
		@Override
		public Object getItem(int arg0) {
			// TODO Auto-generated method stub
			return null;
		}
		@Override
		public long getItemId(int arg0) {
			// TODO Auto-generated method stub
			return 0;
		}
		@Override
		public View getView(int positon, View convertView, ViewGroup parent) {
			convertView = cacheMap_grid.get(positon);
			if(convertView == null){
				System.out.println("2222222222222222222222");
				convertView = View.inflate(getApplicationContext(),R.layout.item_img_only, null);
				MyNetworkImageView img_grid = (MyNetworkImageView) convertView.findViewById(R.id.img_grid);
				//img_grid.setImageResource(R.drawable.test1);
				//bitmapUtils.display(img_grid, xqList.get(positon));
				volleyHelper.showVolleyImg(img_grid, gridList.get(positon), 1, -1);
				cacheMap_grid.put(positon, convertView);
			}
			return convertView;
		}
	}
	
	private void finishLoading() {
		listView.stopRefresh();
		listView.stopLoadMore();
		listView.setRefreshTime("刚刚");
	}

	@Override
	public void onRefresh() {
		handler.postDelayed(new Runnable() {
			@Override
			public void run() {
				finishLoading();
			}
		}, 2000);
	}

	@Override
	public void onLoadMore() {
		handler.postDelayed(new Runnable() {
			@Override
			public void run() {
				finishLoading();
			}
		}, 2000);
	}
}
