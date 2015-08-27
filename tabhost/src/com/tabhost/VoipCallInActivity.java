package com.tabhost;

import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.lidroid.xutils.BitmapUtils;
import com.speedtong.sdk.ECDevice;
import com.speedtong.sdk.ECDevice.CallType;
import com.speedtong.sdk.platformtools.VoiceUtil;
import com.tabhost.util.VolleyHelper;
import com.tabhost.view.RoundedImageView;
import com.tabhost.view.XListView;
/**
 * 回复楼主、层主
 * 回复层主(包括楼主)显示在最后一行
 * 回复“回复层主的人”的评论应该在这些“回复层主的人”的最下面一行
 * 层主回复观众显示在该观众的下面，在自己页面回复
 */
public class VoipCallInActivity extends Activity implements OnClickListener{
	TextView info, status;
	Button hangon, hangup;
	ECDevice.CallType mCallType;
	String mVoipAccount, mCurrentCallId, mPhoneNumberOrNickName;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		System.out.println("VoipCallInActivity-------------");
		Bundle extras =  getIntent().getExtras();
		if (extras == null) {
			finish();
			return;
		}
		//获取对方voip账号
		mVoipAccount = extras.getString(ECDevice.CALLER); 
		//获取来电的通话callId   
		mCurrentCallId = extras.getString(ECDevice.CALLID); 
		//获取呼入的呼叫类型 
		mCallType = (CallType) extras.get(ECDevice.CALLTYPE);
		// 传入数据是否有误 
		if (mVoipAccount == null || mCurrentCallId == null) {
			finish();
			return;
		}
		// 获取透传信息 
		String[] infos = extras.getStringArray(ECDevice.REMOTE);
		if (infos != null && infos.length > 0) {
			for (String str : infos) {
				System.out.println("infos "+str);
				if (str.startsWith("tel")) {
					mPhoneNumberOrNickName = VoiceUtil.getLastwords(str, "=");
				} else if (str.startsWith("nickname")) {
					mPhoneNumberOrNickName = VoiceUtil.getLastwords(str, "=");
				}
			}
		}
		setContentView(R.layout.activity_voip_in);
		info = (TextView) findViewById(R.id.info);
		status = (TextView) findViewById(R.id.status);
		hangon = (Button) findViewById(R.id.hangon);
		hangup = (Button) findViewById(R.id.hangup);
		hangon.setOnClickListener(this);
		hangup.setOnClickListener(this);
		info.setText(mPhoneNumberOrNickName);
		//info.setText("mVoipAccount="+mVoipAccount+" mCurrentCallId="+ mCurrentCallId+" mCallType.getId()="+mCallType.getId()+" mCallType.getValue()="+mCallType.getValue());
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.hangon:
			Intent intent = new Intent(VoipCallInActivity.this,VoipCallingActivity.class);
			intent.putExtra("mPhoneNumberOrNickName", mPhoneNumberOrNickName);
			intent.putExtra("mCurrentCallId", mCurrentCallId);
			startActivity(intent);
			break;
		case R.id.hangup:
			//ECDevice.getECVoipCallManager().releaseCall(mCurrentCallId); //通话过程中挂断
			ECDevice.getECVoipCallManager().rejectCall(mCurrentCallId,6); 
			break; 
		}
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
