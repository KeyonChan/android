package com.tabhost;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;
import com.lidroid.xutils.BitmapUtils;
import com.speedtong.sdk.ECDevice;
import com.speedtong.sdk.ECDevice.OnLogoutListener;
import com.speedtong.sdk.ECError;
import com.speedtong.sdk.ECInitialize;
import com.speedtong.sdk.VoipCall;
import com.speedtong.sdk.ECDevice.CallType;
import com.speedtong.sdk.ECDevice.InitListener;
import com.speedtong.sdk.ECDevice.OnECDeviceConnectListener;
import com.speedtong.sdk.core.model.VoipCallUserInfo;
import com.speedtong.sdk.core.voip.listener.OnVoipListener;
import com.tabhost.other.LruImageCache;
import com.tabhost.util.DialogUtil;
import com.tabhost.util.NetUtil;
import com.tabhost.util.VoipUtil;
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

public class VoipActivity extends Activity{
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
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_voip);
		VoipUtil.init(getApplicationContext(), "88331600000001", "16itkkcg", "8a48b5514e8a7522014ea14ae6c11ba0", "199d72b0c3244f26a603b0466901defa", "黯月轻风", "15067171441");
	}

	public void call(View v){
		final Intent intent = new Intent(this,VoipCallOutActivity.class);
		if(NetUtil.isNetAvailable(this)){
			if(!NetUtil.is34G(this) && !NetUtil.isWifi(this)){
				DialogUtil.showEventDialog(VoipActivity.this, "提示", "当前不是3G、4G或wifi环境，是否直接手机通话！", new DialogInterface.OnClickListener() {  
					public void onClick(DialogInterface dialog, int which) {
						intent.putExtra("account", "13646681160");
						startActivity(intent);
					}
				});
			}else{
				intent.putExtra("account", "88331600000002");
				startActivity(intent);
			}
		}else{
			DialogUtil.showEventDialog(VoipActivity.this, "提示", "当前无网络，是否直接手机通话！", new DialogInterface.OnClickListener() {  
				public void onClick(DialogInterface dialog, int which) {
					intent.putExtra("account", "13646681160");
					startActivity(intent);
				}
			});
		}
	}

/*	@Override
	protected void onRestart() {
		super.onRestart();
		System.out.println("onRestart--------------");
		ECDevice.logout(new OnLogoutListener() { 
			@Override 
			public void onLogout() { 
				ECDevice.unInitial();
			} 
		});
		VoipUtil.init(getApplicationContext(), "88331600000003", "m952nwuq", "8a48b5514e8a7522014ea14ae6e41ba2", "c311931c7a3b4e4e80457c6a1dc65d8b", "黯月轻风", "15067171441");
	}*/

}
