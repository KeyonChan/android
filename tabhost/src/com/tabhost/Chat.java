package com.tabhost;

import java.util.List;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.yuntongxun.ecsdk.ECChatManager;
import com.yuntongxun.ecsdk.ECDevice;
import com.yuntongxun.ecsdk.ECError;
import com.yuntongxun.ecsdk.ECInitParams;
import com.yuntongxun.ecsdk.ECMessage;
import com.yuntongxun.ecsdk.OnChatReceiveListener;
import com.yuntongxun.ecsdk.SdkErrorCode;
import com.yuntongxun.ecsdk.ECDevice.ECConnectState;
import com.yuntongxun.ecsdk.im.ECFileMessageBody;
import com.yuntongxun.ecsdk.im.ECImageMessageBody;
import com.yuntongxun.ecsdk.im.ECTextMessageBody;
import com.yuntongxun.ecsdk.im.ECVoiceMessageBody;
import com.yuntongxun.ecsdk.im.group.ECGroupNoticeMessage;

public class Chat extends ActionBarActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.chat);
		InitSDK();

	}

	// @Override
	// public void OnReceivedMessage(ECMessage msg) {
	// if(msg == null) {
	// return ;
	// }
	// // 接收到的IM消息，根据IM消息类型做不同的处理(IM消息类型：ECMessage.Type)
	// ECMessage.Type type = msg.getType();
	// if(type == ECMessage.Type.TXT) {
	// // 在这里处理文本消息
	// ECTextMessageBody textMessageBody = (ECTextMessageBody) msg.getBody();
	// } else {
	//
	// String thumbnailFileUrl = null;
	// String remoteUrl = null;
	// if (type == ECMessage.Type.FILE) {
	// // 在这里处理附件消息
	// ECFileMessageBody fileMsgBody = (ECFileMessageBody) msg.getBody();
	// // 获得下载地址
	// remoteUrl = fileMsgBody.getRemoteUrl();
	// } else if (type == ECMessage.Type.IMAGE) {
	// // 在这里处理图片消息
	// ECImageMessageBody imageMsgBody = (ECImageMessageBody) msg.getBody();
	// // 获得缩略图地址
	// thumbnailFileUrl = imageMsgBody.getThumbnailFileUrl();
	// // 获得原图地址
	// remoteUrl = imageMsgBody.getRemoteUrl();
	// } else if (type == ECMessage.Type.VOICE) {
	// // 在这里处理语音消息
	// ECVoiceMessageBody voiceMsgBody = (ECVoiceMessageBody) msg.getBody();
	// // 获得下载地址
	// remoteUrl = voiceMsgBody.getRemoteUrl();
	// } else {
	// Log.e("ECSDK_Demo" , "Can't handle msgType=" + type.name()
	// + " , then ignore.");
	// // 后续还会支持（地址位置、视频以及自定义等消息类型）
	// }
	//
	// if(TextUtils.isEmpty(remoteUrl)) {
	// return ;
	// }
	// if(!TextUtils.isEmpty(thumbnailFileUrl)) {
	// // 先下载缩略图
	// } else {
	// // 下载附件
	// }
	// }
	// // 根据不同类型处理完消息之后，将消息序列化到本地存储（sqlite）
	// // 通知UI有新消息到达
	//
	// }

	private void liaotian() {
		// TODO Auto-generated method stub
		try {
			// 组建一个待发送的ECMessage
			ECMessage msg = ECMessage.createECMessage(ECMessage.Type.TXT);
			// 设置消息的属性：发出者，接受者，发送时间等
			msg.setForm("$8a48b5514e8a7522014ea14ae6ce1ba1");
			msg.setMsgTime(System.currentTimeMillis());
			// 设置消息接收者
			msg.setTo("$8a48b5514e8a7522014ea14ae6ce1ba1");
			msg.setSessionId("$8a48b5514e8a7522014ea14ae6ce1ba1");
			// 设置消息发送类型（发送或者接收）
			msg.setDirection(ECMessage.Direction.SEND);

			// 创建一个文本消息体，并添加到消息对象中
			ECTextMessageBody msgBody = new ECTextMessageBody("test1");

			// 或者创建一个图片消息体 并且设置附件包体（其实图片也是相当于附件）
			// 比如我们发送SD卡里面的一张Tony_2015.jpg图片
			// ECImageMessageBody msgBody = new ECImageMessageBody();
			// // 设置附件名
			// msgBody.setFileName("Tony_2015.jpg");
			// // 设置附件扩展名
			// msgBody.setFileExt("jpg");
			// // 设置附件本地路径
			// msgBody.setLocalUrl("../Tony_2015.jpg");

			// 或者创建一个创建附件消息体
			// 比如我们发送SD卡里面的一个Tony_2015.zip文件
			// ECFileMessageBody msgBody = new ECFileMessageBody();
			// // 设置附件名
			// msgBody.setFileName("Tony_2015.zip");
			// // 设置附件扩展名
			// msgBody.setFileExt(zip);
			// // 设置附件本地路径
			// msgBody.setLocalUrl("../Tony_2015.zip");
			// // 设置附件长度
			// msgBody.setLength("$Tony_2015.zip文件大小");

			// 将消息体存放到ECMessage中
			msg.setBody(msgBody);
			// 调用SDK发送接口发送消息到服务器
			ECChatManager manager = ECDevice.getECChatManager();
			manager.sendMessage(msg, new ECChatManager.OnSendMessageListener() {
				@Override
				public void onSendMessageComplete(ECError error,
						ECMessage message) {
					// 处理消息发送结果
					if (message == null) {
						return;
					}
					// 将发送的消息更新到本地数据库并刷新UI
					Toast.makeText(getApplicationContext(),
							"发送的消息消息=" + message.toString(), 1).show();
					TextView test=(TextView) findViewById(R.id.test);
					test.setText(message.toString());
				}

				@Override
				public void onProgress(String msgId, int totalByte,
						int progressByte) {
					// 处理文件发送上传进度（尽上传文件、图片时候SDK回调该方法）
				}

				@Override
				public void onComplete(ECError error) {
					// 忽略
				}
			});
		} catch (Exception e) {
			// 处理发送异常
			Log.e("ECSDK_Demo", "send message fail , e=" + e.getMessage());
			Toast.makeText(getApplicationContext(), "消息错误：" + e.toString(), 1)
			.show();
		}

	}

	private void InitSDK() {
		// 第一步：初始化SDK
		// 判断SDK是否已经初始化，如果已经初始化则可以直接调用登陆接口
		// 没有初始化则先进行初始化SDK，然后调用登录接口注册SDK
		if (!ECDevice.isInitialized()) {
			ECDevice.initial(getApplicationContext(),
					new ECDevice.InitListener() {
				@Override
				public void onInitialized() {
					// SDK已经初始化成功
					System.out.println("IM SDK已经初始化成功");
					// 第二步：设置注册参数、设置通知回调监听
					// 构建注册所需要的参数信息
					ECInitParams params = new ECInitParams();
					params.setUserid("88331600000005");
					params.setPwd("ysjr5npy");
					params.setAppKey("8a48b5514e8a7522014ea14ae7091ba4");
					params.setToken("7b59426ea6674c00b9ece8d4b5eb3186");
					// 1代表用户名+密码登陆（可以强制上线，踢掉已经在线的设备）
					// 2代表自动重连注册（如果账号已经在其他设备登录则会提示异地登陆）
					// 3 LoginMode（FORCE_LOGIN AUTO）
					ECInitParams.LoginMode mMode = ECInitParams.LoginMode.AUTO;
					params.setMode(mMode);

					// 设置登陆状态回调
					params.setOnDeviceConnectListener(new ECDevice.OnECDeviceConnectListener() {
						public void onConnect() {
							// 兼容4.0，5.0可不必处理
						}

						@Override
						public void onDisconnect(ECError error) {
							// 兼容4.0，5.0可不必处理
						}

						@Override
						public void onConnectState(
								ECDevice.ECConnectState state,
								ECError error) {
							if (state == ECDevice.ECConnectState.CONNECT_FAILED) {
								if (error.errorCode == SdkErrorCode.SDK_KICKED_OFF) {
									// 账号异地登陆
									System.out.println("IM账号异地登陆");
								} else {
									// 连接状态失败
									System.out.println("IM连接状态失败");
								}
								return;
							} else if (state == ECDevice.ECConnectState.CONNECT_SUCCESS) {
								// 登陆成功
								System.out.println("IM登陆成功");
								liaotian();
							}
						}
					});

					// 设置SDK接收消息回调
					params.setOnChatReceiveListener(new OnChatReceiveListener() {
						@Override
						public void OnReceivedMessage(ECMessage msg) {
							// 收到新消息
							System.out.println("IM收到新消息="+msg.toString());
						}

						@Override
						public void OnReceiveGroupNoticeMessage(
								ECGroupNoticeMessage notice) {
							// 收到群组通知消息（有人加入、退出...）
							// 可以根据ECGroupNoticeMessage.ECGroupMessageType类型区分不同消息类型
						}

						@Override
						public void onOfflineMessageCount(int count) {
							// 登陆成功之后SDK回调该接口通知账号离线消息数
						}

						@Override
						public void onReceiveOfflineMessage(List msgs) {
							// SDK根据应用设置的离线消息拉去规则通知应用离线消息
						}

						@Override
						public void onReceiveOfflineMessageCompletion() {
							// SDK通知应用离线消息拉取完成
						}

						@Override
						public void onServicePersonVersion(int version) {
							// SDK通知应用当前账号的个人信息版本号
						}

						@Override
						public int onGetOfflineMessage() {
							// TODO Auto-generated method stub
							return 0;
						}

						@Override
						public void onReceiveDeskMessage(ECMessage arg0) {
							// TODO Auto-generated method stub

						}

						@Override
						public void onSoftVersion(String arg0, int arg1) {
							// TODO Auto-generated method stub

						}
					});
					// 第三步：验证参数是否正确，注册SDK
					if (params.validate()) {
						// 判断注册参数是否正确
						ECDevice.login(params);

					}
				}

				@Override
				public void onError(Exception exception) {
					System.out.println("IM初始化失败 "+exception.toString());
					// SDK 初始化失败,可能有如下原因造成
					// 1、可能SDK已经处于初始化状态
					// 2、SDK所声明必要的权限未在清单文件（AndroidManifest.xml）里配置、
					// 或者未配置服务属性android:exported="false";
					// 3、当前手机设备系统版本低于ECSDK所支持的最低版本（当前ECSDK支持
					// Android Build.VERSION.SDK_INT 以及以上版本）
				}
			});
		}

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	private void back() {
		// TODO Auto-generated method stub
		ECDevice.logout(new ECDevice.OnLogoutListener() {
			@Override
			public void onLogout() {
				// SDK 回调通知当前登出成功
				// 这里可以做一些（与云通讯IM相关的）应用资源的释放工作
				// 如（关闭数据库，释放界面资源和跳转等）
				// 退出
				Toast.makeText(getApplicationContext(), "退出成功！", 1).show();
			}
		});

	}
}
