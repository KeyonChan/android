package com.tabhost;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.annotation.SuppressLint;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
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
import com.tabhost.view.MyListView;
import com.tabhost.view.MyNetworkImageView;
import com.tabhost.view.RoundedImageView;
import com.tabhost.view.SlideShowView;
import com.tabhost.view.XListView;
/**
 * 回复楼主、层主
 * 回复层主(包括楼主)显示在最后一行
 * 回复“回复层主的人”的评论应该在这些“回复层主的人”的最下面一行
 * 层主回复观众显示在该观众的下面，在自己页面回复
 */
@SuppressLint({ "NewApi", "SetJavaScriptEnabled" })
public class Test extends FragmentActivity{
	XListView listView;
	LinearLayout ll;
	int replyPos = -1;
	String replytoWho = "";
	List<String> urlList;
	Map<Integer,View> cacheMap;
	VolleyHelper volleyHelper;
	TextView tv;
	Handler handler;
	ImageView img2,img3;
	RoundedImageView img1;
	RequestQueue queue;
	private ImageLoader imageLoader = null;
	BitmapUtils bitmapUtils;
	String[] imageUrls;
	GridView emoji_gridview;
	List<Map<String, Object>> emojiList;
	EmojiAdapter emoji_adapter;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.test);
		emoji_gridview = (GridView) findViewById(R.id.emoji_gridview);
		emojiList = new ArrayList<Map<String, Object>>();
		AssetManager mngr = getAssets();
		InputStream in = null;
		Map<String, Object> map;
		try {
			in = mngr.open("emoji/fennu.png");
			map = new HashMap<String, Object>();
			map.put("name", "[愤怒]");
			map.put("bmp", BitmapFactory.decodeStream(in, null, null));
			emojiList.add(map);
			/*map = new HashMap<String, Object>();
			map.put("name", "[开心]");
			map.put("bmp", BitmapFactory.decodeStream(in, null, null));
			emojiList.add(map);
			map = new HashMap<String, Object>();
			map.put("name", "[无语]");
			map.put("bmp", BitmapFactory.decodeStream(in, null, null));
			emojiList.add(map);
			in.close();*/
		} catch (Exception e) {
			e.printStackTrace();
		}
		emoji_adapter = new EmojiAdapter();
		emoji_gridview.setAdapter(emoji_adapter);
		System.out.println(emojiList.size());
	}
	private class EmojiAdapter extends BaseAdapter {
		public int getCount() {
			return emojiList.size();
		}
		public Object getItem(int arg0) {
			return null;
		}
		public long getItemId(int position) {
			return 0;
		}
		// 返回view
		public View getView(final int position, final View convertView,
				ViewGroup parent) {
			final Map<String, Object> map = emojiList.get(position);
			View view = View.inflate(getApplicationContext(),R.layout.item_emoji, null);
			ImageView emoji = (ImageView) view.findViewById(R.id.emoji);
			LinearLayout emoji_ll = (LinearLayout) view.findViewById(R.id.emoji_ll);
			emoji.setImageBitmap((Bitmap) map.get("bmp"));
			emoji_ll.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
				}
			});
			return view;
		}
	}
}
