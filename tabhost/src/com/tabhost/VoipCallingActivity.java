package com.tabhost;

import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.lidroid.xutils.BitmapUtils;

import com.speedtong.sdk.ECDevice;
import com.speedtong.sdk.ECDevice.CallType;
import com.speedtong.sdk.platformtools.VoiceUtil;
import com.tabhost.util.DateUtil;
import com.tabhost.util.VolleyHelper;
import com.tabhost.view.RoundedImageView;
import com.tabhost.view.XListView;
/**
 * 回复楼主、层主
 * 回复层主(包括楼主)显示在最后一行
 * 回复“回复层主的人”的评论应该在这些“回复层主的人”的最下面一行
 * 层主回复观众显示在该观众的下面，在自己页面回复
 */

public class VoipCallingActivity extends Activity implements OnClickListener{
	static TextView info, time;
	public static TextView status;
	Button hangup, mute, handfree;
	ECDevice.CallType mCallType;
	String mCurrentCallId, mPhoneNumberOrNickName;
	public static Boolean isRunning = true;
	Boolean isMute = false;
	Boolean isHandsfree = false;
	int sec=0;
	static VoipCallingActivity instance;
	Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			time.setText(DateUtil.getJishi((Integer)msg.obj));
		}
	};
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_voip_ing);
		info = (TextView) findViewById(R.id.info);
		time = (TextView) findViewById(R.id.time);
		status = (TextView) findViewById(R.id.status);
		hangup = (Button) findViewById(R.id.hangup);
		mute = (Button) findViewById(R.id.mute);
		handfree = (Button) findViewById(R.id.handfree);
		hangup.setOnClickListener(this);
		mute.setOnClickListener(this);
		handfree.setOnClickListener(this);
		mPhoneNumberOrNickName = getIntent().getStringExtra("mPhoneNumberOrNickName");
		mCurrentCallId = getIntent().getStringExtra("mCurrentCallId");
		info.setText(mPhoneNumberOrNickName);
		ECDevice.getECVoipCallManager().acceptCall(mCurrentCallId);
		instance = this;
		isRunning = true;
		jishi();
	}

	public static VoipCallingActivity getInstance(){
		return instance;
	}
	
	private void jishi() {
		new Thread(new Runnable(){
			@Override
			public void run() {
				while(isRunning){
					Message msg = Message.obtain();
					msg.obj = sec;
					handler.sendMessage(msg);

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

	/**
	 * 设置静音
	 */
	public void setMuteUI() {
		try {

			ECDevice.getECVoipSetManager().setMute(!isMute);

			isMute = ECDevice.getECVoipSetManager().getMuteStatus();

			/*if (isMute) {
				mCallMute.setImageResource(R.drawable.call_interface_mute_on);
			} else {
				mCallMute.setImageResource(R.drawable.call_interface_mute);
			}*/
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 设置免提
	 */
	public void sethandfreeUI() {
		try {

			ECDevice.getECVoipSetManager().enableLoudSpeaker(!isHandsfree);
			isHandsfree = ECDevice.getECVoipSetManager().getLoudSpeakerStatus();
			/*if (isHandsfree) {
				mCallHandFree
						.setImageResource(R.drawable.call_interface_hands_free_on);
			} else {
				mCallHandFree
						.setImageResource(R.drawable.call_interface_hands_free);
			}*/
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.mute:
			setMuteUI();
			break;
		case R.id.hangup:
			ECDevice.getECVoipCallManager().releaseCall(mCurrentCallId); //通话过程中挂断
			//ECDevice.getECVoipCallManager().rejectCall(mCurrentCallId,3);
			isRunning = false;
			break;
		case R.id.handfree:
			sethandfreeUI();
			break;
		}
	}
	@Override
	public void onDestroy(){
		super.onDestroy();
		ECDevice.getECVoipCallManager().releaseCall(mCurrentCallId);
		//ECDevice.getECVoipCallManager().rejectCall(mCurrentCallId,3);
	}
	
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
        	finish();
            return false;
        }
        return super.onKeyDown(keyCode, event);
    } 
}
