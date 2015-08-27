package com.tabhost;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
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
import com.speedtong.sdk.ECDevice;
import com.tabhost.other.LruImageCache;
import com.tabhost.util.DateUtil;
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

public class VoipCallOutActivity extends Activity implements OnClickListener{
	static TextView time;
	public static TextView info,status;
	Button hangup;
	static VoipCallOutActivity instance;
	public static Boolean isRunning = true;
	static int sec = 0;
	public static String callId;

	static Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			time.setText(DateUtil.getJishi((Integer)msg.obj));
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_voip_out);
		info = (TextView) findViewById(R.id.info);
		time = (TextView) findViewById(R.id.time);
		status = (TextView) findViewById(R.id.status);
		hangup = (Button) findViewById(R.id.hangup);
		System.out.println("account     "+getIntent().getStringExtra("account"));
		VoipUtil.callout(getIntent().getStringExtra("account"));
		hangup.setOnClickListener(this);
		instance = this;
	}

	public static VoipCallOutActivity getInstance(){
		return instance;
	}

	public static void jishi() {
		new Thread(new Runnable(){
			@Override
			public void run() {
				while(isRunning){
					Message msg = Message.obtain();
					msg.obj = sec;
					handler.sendMessage(msg);
					System.out.println(sec);
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					sec++;
				}
			}
		}).start();
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.hangup: {
			ECDevice.getECVoipCallManager().releaseCall(callId); //通话过程中挂断
			//ECDevice.getECVoipCallManager().rejectCall(mCurrentCallId,3);
			isRunning = false;
			return;
		}
		}
	}

	public void onDestroy(){
		super.onDestroy();
		if(callId != null)
			ECDevice.getECVoipCallManager().releaseCall(callId);
		//ECDevice.getECVoipCallManager().rejectCall(mCurrentCallId,3);
	}
	
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
        	if(callId != null){
        		ECDevice.getECVoipCallManager().releaseCall(callId);
        		System.out.println("releaseCall");
        	}
        	finish();
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }
}