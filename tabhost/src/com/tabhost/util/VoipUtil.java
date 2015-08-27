package com.tabhost.util;


import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.speedtong.sdk.ECDevice;
import com.speedtong.sdk.ECError;
import com.speedtong.sdk.ECInitialize;
import com.speedtong.sdk.VoipCall;
import com.speedtong.sdk.ECDevice.CallType;
import com.speedtong.sdk.ECDevice.InitListener;
import com.speedtong.sdk.ECDevice.OnECDeviceConnectListener;
import com.speedtong.sdk.core.model.VoipCallUserInfo;
import com.speedtong.sdk.core.voip.listener.OnVoipListener;
import com.tabhost.ApplicationDemo;
import com.tabhost.VoipActivity;
import com.tabhost.VoipCallInActivity;
import com.tabhost.VoipCallOutActivity;
import com.tabhost.VoipCallingActivity;
import com.yuntongxun.ecsdk.ECInitParams;
import com.yuntongxun.ecsdk.ECMessage;
import com.yuntongxun.ecsdk.OnChatReceiveListener;
import com.yuntongxun.ecsdk.SdkErrorCode;
import com.yuntongxun.ecsdk.im.group.ECGroupNoticeMessage;

public class VoipUtil {
	public static Context mcontext;
	public static String msid;
	public static String msidToken;
	public static String msubId;
	public static String msubToken;
	public static String mnickName;
	public static String mphoneNum;
	public static Handler handler = new Handler();
	public static int timeout = 0;
	public static Boolean isTimeout = true;

	public static void init(final Context context, final String sid, final String sidToken, final String subId, final String subToken, final String nickName, final String phoneNum){
		InitListener listener = new InitListener() {
			ECInitialize params;//初始化参数、连接sdk必备
			public void onInitialized() {
				// SDK 初始化成功
				params = new ECInitialize();
				params.setServerIP("sandboxapp.cloopen.com");
				params.setServerPort(8883);
				params.setSid(sid);
				params.setSidToken(sidToken);
				params.setSubId(subId);
				params.setSubToken(subToken);
				mcontext = context;
				msid = sid;
				msidToken = sidToken;
				msubId = subId;
				msubToken = subToken;
				mnickName = nickName;
				mphoneNum = phoneNum;
				//{设置连接通讯监听-begin， 
				//1.登录成功，重联成功调用onConnect; 
				//2.登录失败, 断开连接则调用onDisConnect. 
				params.setOnECDeviceConnectListener(new OnECDeviceConnectListener() { 
					@Override
					public void onDisconnect(ECError error) {
						// SDK与云通讯平台连接失败
						System.out.println("--------onDisconnect");
					}
					@Override
					public void onConnect() {
						// SDK与云通讯平台成功
						System.out.println("--------onConnect");
					}
				});
				//设置连接通讯监听-end，
				//设置voip通话的相关信息，一定要放在sdk初始化的回调中
				VoipCallUserInfo voipCallUserInfo = new VoipCallUserInfo();//透传信息 
				voipCallUserInfo.setNickName(nickName);//透传昵称
				voipCallUserInfo.setPhoneNum(phoneNum);//透传电话号码
				params.setVoipCallUserInfo(voipCallUserInfo);

				//设置来电界面呼入界面activity，CallInActivity是来电界面的Activity，开发者需修改
				//可以修改成自己的Activity类。
				Intent intent = new Intent(context, 
						VoipCallInActivity.class);
				PendingIntent pendingIntent = PendingIntent.getActivity(
						context, 0, intent,
						PendingIntent.FLAG_UPDATE_CURRENT);
				params.setPendingIntent(pendingIntent);

				//设置 voip 通话状态监听器,在监听器回调类里面需要实现sdk 的回调法方法。 
				//回调方法1：OnCallEnents---呼叫状态回调 
				//回调方法2：onSwitchCallMediaTypeRequest  ---收到对方请求切换音视频
				//回调方法 3：onSwitchCallMediaTypeResponse ---收到对方响应切换音视
				//回调方法4：onCallVideoRatioChanged---对方视频分辨率发生改变回调 
				//回调方法5：onCallMediaInitFailed---媒体初始化失败回调 
				//回调方法6：onDtmfReceived---收到对方发送 dtmf 回调 
				//回调方法7：onFireWallPolicyEnabled---双方通话如果是 p2p则回调 

				params.setOnECVoipListener(onVoipListener);
				ECDevice.login(params);// 连接云通讯
			}
			@Override 
			public void onError(Exception exception) { 
				// SDK初始化失败释放sdk
				ECDevice.unInitial();
			}
		};
		ECDevice.initial(context,listener);
	}

	public static String callout(String voip){
		return ECDevice.getECVoipCallManager().makeCall( 
				CallType.VOICE, voip); 
	}

	static OnVoipListener onVoipListener = new OnVoipListener(){
		@Override
		public void onCallEvents(final VoipCall voipCall) {
			final String callid = voipCall.getCallId(); //对方的状态
			int reason = voipCall.getReason();
			System.out.println("reason "+reason);
			switch (voipCall.getEcCallState()) {
			case ECallReleased:
				//无论是哪一方主动结束通话，双方都会进入到此回调
				System.out.println("通话结束");
				ECDevice.getECVoipCallManager().releaseCall(callid);
				if(VoipCallOutActivity.getInstance() != null){
					VoipCallOutActivity.isRunning = false; //呼出时对方拒接
					VoipCallOutActivity.status.setText("通话结束");
				}
				handler.postDelayed(new Runnable(){ //对方接听后马上挂断可能VoipCallingActivity来不及跳转
					@Override
					public void run() {
						if(VoipCallingActivity.getInstance() != null){
							VoipCallingActivity.isRunning = false;
							VoipCallingActivity.status.setText("通话结束");
						}
					}
				}, 1000);
				break;
			case ECallProceeding:
				System.out.println("呼叫中");
				break;
			case ECallAlerting:
				System.out.println("对方振铃");
				VoipCallOutActivity.callId = voipCall.getCallId();
				break;
			case ECallAnswered:
				System.out.println("双方接听");
				//isTimeout = false;
				if(VoipCallOutActivity.getInstance() != null){
					VoipCallOutActivity.isRunning = true;
					VoipCallOutActivity.jishi();
					VoipCallOutActivity.info.setText(voipCall.getCallee());
					VoipCallOutActivity.status.setText("通话中");
				}
				break;
			case ECallPaused:
				System.out.println("自己暂停通话");
				break;
			case ECallPausedByRemote:
				System.out.println("对方暂停通话");
				break;
			case ECallFailed:
				System.out.println("呼叫失败"); //呼出时对方拒接
				ECDevice.getECVoipCallManager().releaseCall(callid); //不加可能会显示对方正忙
				if(VoipCallOutActivity.getInstance() != null){
					VoipCallOutActivity.isRunning = false; //呼出时对方拒接
					VoipCallOutActivity.status.setText("通话结束");
				}
				break;
			default:
				break;
			}

		}
		//媒体初始化失败回调
		@Override
		public void onCallMediaInitFailed(String callid, int reason) {
			// TODO Auto-generated method stub

		}
		@Override
		public void onCallVideoRatioChanged(String arg0, int arg1,
				int arg2) {
			// TODO Auto-generated method stub

		}
		//接收dtmf回调
		@Override
		public void onDtmfReceived(String callid, char dtmf) {
			// TODO Auto-generated method stub

		}
		//p2p回调
		@Override
		public void onFirewallPolicyEnabled() {
			// TODO Auto-generated method stub

		}
		@Override
		public void onSwitchCallMediaTypeRequest(String callid, CallType callType) {
			ECDevice.getECVoipCallManager().responseSwitchCallMediaType(callid, 1);

		}
		@Override
		public void onSwitchCallMediaTypeResponse(String callid, CallType callType) {

		}
	};
}
