package com.tabhost;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.VolleyError;
import com.android.volley.Request.Method;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.ImageLoader.ImageCache;
import com.android.volley.toolbox.ImageLoader.ImageListener;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.tabhost.fragment.Fragment1;
import com.tabhost.fragment.Fragment2;
import com.tabhost.fragment.Fragment3;
import com.tabhost.service.NetService;
import com.tabhost.util.VolleyHelper;
import com.tabhost.view.MyViewPager;
import com.tabhost.view.MyNetworkImageView;
import com.tabhost.view.XListView;
import com.tabhost.view.XListView.IXListViewListener;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.LruCache;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.LinearLayout.LayoutParams;
/**
 * 回复楼主、层主
 * 回复层主(包括楼主)显示在最后一行
 * 回复“回复层主的人”的评论应该在这些“回复层主的人”的最下面一行
 * 层主回复观众显示在该观众的下面，在自己页面回复
 */
@SuppressLint({ "NewApi", "SetJavaScriptEnabled" })
public class TestComment extends FragmentActivity{
	ListView commentListView;

	List<Map<String,Object>> commentList;
	LinkedList<Map<String,String>> soncommentList;
	MyAdapter adapter;
	int replyPos = -1;
	String replytoWho = "";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.test_comment);
		commentListView = (ListView) findViewById(R.id.commentListView);
		commentList = new ArrayList<Map<String,Object>>();

		soncommentList = new LinkedList<Map<String,String>>();
		HashMap<String,String> map1_1 = new HashMap<String,String>(); //层中的一行回复，键值对组成
		HashMap<String,String> map1_2 = new HashMap<String,String>();
		HashMap<String,String> map1_3 = new HashMap<String,String>();
		map1_1.put("type", "2");
		map1_1.put("username", "amay");
		map1_1.put("content", "支持层主");
		map1_2.put("type", "3");
		map1_2.put("username", "cjy");
		map1_2.put("replyTo", "amay");
		map1_2.put("content", "谢谢支持");
		map1_3.put("type", "2");
		map1_3.put("username", "bob");
		map1_3.put("content", "支持层主+1");
		soncommentList.add(map1_1);
		soncommentList.add(map1_2);
		soncommentList.add(map1_3);
		Map<String,Object> map1 = new HashMap<String,Object>();
		map1.put("type", "1");
		map1.put("username", "cjy");
		map1.put("content", "很好，支持下");
		map1.put("reply", soncommentList);
		commentList.add(map1);
		adapter = new MyAdapter();
		commentListView.setAdapter(adapter);

	}

	private class MyAdapter extends BaseAdapter {
		public int getCount() {
			return commentList.size();
		}
		public Object getItem(int arg0) {
			return null;
		}
		public long getItemId(int position) {
			return 0;
		}
		//返回view
		public View getView(int position, View convertView, ViewGroup parent) {
			View view = View.inflate(getApplicationContext(),
					R.layout.item_test, null);
			List<Map<String,String>> soncommentList = (List<Map<String, String>>) commentList.get(position).get("reply");

			TextView username = (TextView) view.findViewById(R.id.username);
			TextView content = (TextView) view.findViewById(R.id.content);
			LinearLayout comment_ll = (LinearLayout) view.findViewById(R.id.comment_ll);
			username.setText(commentList.get(position).get("username").toString());
			content.setText(commentList.get(position).get("content").toString());
			for (int i = 0; i < soncommentList.size(); i++) {
				Map<String,String> sonMap = soncommentList.get(i);
				final LinearLayout hll = new LinearLayout(getApplicationContext());
				hll.setOrientation(LinearLayout.HORIZONTAL);
				hll.setTag(i);

				final TextView username2 = new TextView(getApplicationContext());
				TextView content2 = new TextView(getApplicationContext());
				TextView text = new TextView(getApplicationContext());
				username2.setText(sonMap.get("username").toString());
				content2.setText("："+sonMap.get("content"));
				content2.setSingleLine(false);
				hll.addView(username2);
				if("3".equals(sonMap.get("type")))
					text.setText(" 回复 "+sonMap.get("replyTo"));
				hll.addView(text);
				hll.addView(content2);

				username2.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View arg0) {
						replyPos= (Integer) hll.getTag();
						replytoWho = username2.getText().toString();
						System.out.println("replyPos  "+replyPos);
					}
				});

				comment_ll.addView(hll);
			}
			return view;
		}
	}

	public void click1(View v) {
		if("my".equals(replytoWho)){
			Toast.makeText(this, "不能回复自己！", 0).show();
			return;
		}
		//回复“army：支持层主”应该在这一层的最下面，即要包括“cjy回复army”的下面
		System.out.println("开始 replyPos==="+replyPos);
		for (int i = 0; i < soncommentList.size(); i++) {
			Map<String,String> sonMap = soncommentList.get(i);
			/*if("3".equals(sonMap.get("type")) && replytoWho.equals(sonMap.get("replyTo"))){
				replyPos++;
				System.out.println("gaibian replyPos==="+replyPos);
			}*/
			if(replytoWho.equals(sonMap.get("replyTo"))){
				replyPos = i;
				System.out.println("gaibian replyPos==="+replyPos);
			}
		}
		System.out.println("结束 replyPos==="+(replyPos+1));
		HashMap<String,String> map = new HashMap<String,String>();
		map.put("type", "3");
		map.put("username", System.currentTimeMillis()+"");
		map.put("replyTo", replytoWho);
		map.put("content", System.currentTimeMillis()+"");
		soncommentList.add(replyPos+1, map);
		adapter.notifyDataSetChanged();
		soncommentList.get(replyPos);
		replyPos = -1;
	}

	public void click2(View v) {

	}
}
