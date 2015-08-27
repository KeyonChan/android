package com.tabhost;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.client.HttpRequest;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.loopj.android.http.AsyncHttpClient;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.tabhost.R;
import com.tabhost.util.FileUtil;
import com.tabhost.service.NetService;
import com.tabhost.util.Base64;
import com.tabhost.util.CommonUtil;
import com.tabhost.util.DialogUtil;
import com.tabhost.util.JsonParser;
import com.tabhost.util.MTAUtil;
import com.tabhost.util.DaojishiUtil;
import com.tabhost.util.NetUtil;
import com.tabhost.util.ShareUtil;
import com.tabhost.util.VoiceToText;
import com.tabhost.view.SlideShowView;
import com.tencent.stat.StatService;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.bean.SocializeConfig;
import com.umeng.socialize.controller.UMServiceFactory;
import com.umeng.socialize.controller.UMSocialService;
import com.umeng.socialize.laiwang.media.LWDynamicShareContent;
import com.umeng.socialize.laiwang.media.LWShareContent;
import com.umeng.socialize.media.GooglePlusShareContent;
import com.umeng.socialize.media.MailShareContent;
import com.umeng.socialize.media.QQShareContent;
import com.umeng.socialize.media.QZoneShareContent;
import com.umeng.socialize.media.RenrenShareContent;
import com.umeng.socialize.media.SinaShareContent;
import com.umeng.socialize.media.SmsShareContent;
import com.umeng.socialize.media.TencentWbShareContent;
import com.umeng.socialize.media.TwitterShareContent;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.media.UMVideo;
import com.umeng.socialize.media.UMusic;
import com.umeng.socialize.sso.EmailHandler;
import com.umeng.socialize.sso.QZoneSsoHandler;
import com.umeng.socialize.sso.RenrenSsoHandler;
import com.umeng.socialize.sso.SinaSsoHandler;
import com.umeng.socialize.sso.SmsHandler;
import com.umeng.socialize.sso.TencentWBSsoHandler;
import com.umeng.socialize.sso.UMQQSsoHandler;
import com.umeng.socialize.sso.UMSsoHandler;
import com.umeng.socialize.weixin.controller.UMWXHandler;
import com.umeng.socialize.weixin.media.CircleShareContent;
import com.umeng.socialize.weixin.media.WeiXinShareContent;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.StrictMode;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;
import android.view.ViewGroup.LayoutParams;

import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.RecognizerListener;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechRecognizer;
import com.iflytek.sunflower.FlowerCollector;

public class Tab_IndexActivity extends BaseActivity implements OnClickListener {
	Calendar calendar;
	Dialog dialog;
	SlideShowView slideshowView;

	ImageView img;
	Button hide;
	TextView search;
	List<String> imageUrls;
	SpeechRecognizer mIat;
	VoiceToText voice;
	UMSocialService mController = UMServiceFactory
			.getUMSocialService("com.umeng.share");

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.tab_index);
		slideshowView = (SlideShowView) findViewById(R.id.slideshowView);
		hide = (Button) findViewById(R.id.hide);
		img = (ImageView) findViewById(R.id.img);
		search = (TextView) findViewById(R.id.search);
		imageUrls = new ArrayList<String>();
		imageUrls.add("http://image.zcool.com.cn/56/35/1303967876491.jpg");
		imageUrls.add("http://image.zcool.com.cn/59/54/m_1303967870670.jpg");
		imageUrls.add("http://image.zcool.com.cn/47/19/1280115949992.jpg");
		imageUrls.add("http://image.zcool.com.cn/59/11/m_1303967844788.jpg");
		slideshowView.myInit(this, imageUrls, true, true);
		calendar = Calendar.getInstance(Locale.CHINA);
		voice = new VoiceToText(this);
	}

	private void showPopupWindow(View view) {
		// 一个自定义的布局，作为显示的内容
		View contentView = LayoutInflater.from(this).inflate(
				R.layout.popup_window, null);
		// 设置按钮的点击事件
		Button button = (Button) contentView.findViewById(R.id.button1);
		button.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Toast.makeText(Tab_IndexActivity.this, "button is pressed",
						Toast.LENGTH_SHORT).show();
			}
		});

		final PopupWindow popupWindow = new PopupWindow(contentView,
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, true);

		popupWindow.setTouchable(true);
		// 实例化一个ColorDrawable颜色为半透明
		ColorDrawable dw = new ColorDrawable(0xb0000000);
		// 设置SelectPicPopupWindow弹出窗体的背景
		popupWindow.setBackgroundDrawable(dw);
		popupWindow.setTouchInterceptor(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// 这里如果返回true，touch事件将被拦截，点击空白处无法自动隐藏
				return false;
			}
		});
		// 如果不设置PopupWindow的背景，无论是点击外部区域还是Back键都无法dismiss弹框
		// 我觉得这里是API的一个bug
		popupWindow.setBackgroundDrawable(getResources().getDrawable(
				R.drawable.splash_img));

		// 设置好参数之后再show
		popupWindow.showAsDropDown(view);
	}

	/**
	 * 初始化监听器。
	 */
	private InitListener mInitListener = new InitListener() {
		@Override
		public void onInit(int code) {
			if (code != ErrorCode.SUCCESS) {
				Toast.makeText(Tab_IndexActivity.this, "初始化失败，错误码：",
						Toast.LENGTH_SHORT).show();
			}
		}
	};

	/**
	 * 听写监听器，写在具体页
	 */
	private RecognizerListener recognizerListener = new RecognizerListener() {
		@Override
		public void onBeginOfSpeech() {
			Toast.makeText(Tab_IndexActivity.this, "开始说话", Toast.LENGTH_SHORT)
					.show();
		}

		@Override
		public void onError(SpeechError error) {
			// Tips：
			// 错误码：10118(您没有说话)，可能是录音机权限被禁，需要提示用户打开应用的录音权限。
			// 如果使用本地功能（语音+）需要提示用户开启语音+的录音权限。
			Toast.makeText(Tab_IndexActivity.this,
					error.getPlainDescription(true), Toast.LENGTH_SHORT).show();
		}

		@Override
		public void onEndOfSpeech() {
			Toast.makeText(Tab_IndexActivity.this, "结束说话", Toast.LENGTH_SHORT)
					.show();
		}

		@Override
		public void onResult(RecognizerResult results, boolean isLast) {
			Toast.makeText(Tab_IndexActivity.this, results.getResultString(),
					Toast.LENGTH_SHORT).show();
			voice.printResult(results);
			if (isLast) {
				// TODO 最后的结果
			}
		}

		@Override
		public void onVolumeChanged(int volume) {
			Toast.makeText(Tab_IndexActivity.this, "当前正在说话，音量大小：" + volume,
					Toast.LENGTH_SHORT).show();
		}

		@Override
		public void onEvent(int eventType, int arg1, int arg2, Bundle obj) {
		}
	};

	public void show(View view) {

		// Bitmap srcbmp = BitmapFactory.decodeResource(this.getResources(),
		// R.drawable.ccc);
		/*
		 * Bitmap srcbmp = BitmapFactory.decodeResource(this.getResources(),
		 * R.drawable.ccc); Bitmap newbmp = CommonUtil.getFangBitmap(srcbmp);
		 * img.setImageBitmap(newbmp); FileOutputStream fos = null; try { fos =
		 * new
		 * FileOutputStream(Environment.getExternalStorageDirectory()+"/result.jpg"
		 * ); } catch (FileNotFoundException e) { e.printStackTrace(); }
		 */
		// newbmp.compress(Bitmap.CompressFormat.JPEG, 100, fos);
		// //不压缩是100%，实测75

		/*
		 * BitmapFactory.Options options = new BitmapFactory.Options();
		 * options.inJustDecodeBounds = true;
		 * BitmapFactory.decodeResource(getResources(), R.drawable.ccc,
		 * options); int imageHeight = options.outHeight; int imageWidth =
		 * options.outWidth; String imageType = options.outMimeType;
		 * 
		 * options.inSampleSize = calculateInSampleSize(options, 720, 360);
		 */

		Bitmap srcbmp = decodeSampledBitmapFromResource(getResources(),
				R.drawable.ccc, 720, 360);
		img.setImageBitmap(srcbmp);
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(
					"/data/data/com.tabhost/files/result---------.jpg");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		srcbmp.compress(Bitmap.CompressFormat.JPEG, 100, fos);
		voice.voice(recognizerListener);
		// photoRefresh(view);
		// Toast.makeText(Tab_IndexActivity.this,
		// NetUtil.getLocalIpAddress(this), 0).show();
		Properties prop = new Properties();
		prop.setProperty("uid", "cjy");
		StatService.trackCustomBeginKVEvent(this, "test_time", prop);
	}

	public static Bitmap decodeSampledBitmapFromResource(Resources res,
			int resId, int reqWidth, int reqHeight) {
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true; // 第一次禁止为bitmap分配内存，返回值也不再是一个Bitmap对象，而是null
		BitmapFactory.decodeResource(res, resId, options); // 调用上面定义的方法计算inSampleSize值
		// 使用inSampleSize缩放、压缩
		options.inSampleSize = calculateInSampleSize(options, reqWidth,
				reqHeight);
		options.inJustDecodeBounds = false;
		return BitmapFactory.decodeResource(res, resId, options); // 第二次解析
	}

	public static int calculateInSampleSize(BitmapFactory.Options options,
			int reqWidth, int reqHeight) {
		final int height = options.outHeight;
		final int width = options.outWidth;
		int inSampleSize = 1;
		if (height > reqHeight || width > reqWidth) {
			// 计算出实际宽高和目标宽高的比率
			final int heightRatio = Math.round((float) height
					/ (float) reqHeight);
			final int widthRatio = Math.round((float) width / (float) reqWidth);
			// 选择宽和高中最小的比率作为inSampleSize的值，这样可以保证最终图片的宽和高
			// 一定都会大于等于目标的宽和高。
			inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
		}
		return inSampleSize;
	}

	public void hide(View view) {
		Properties prop = new Properties();
		prop.setProperty("uid", "123");
		StatService.trackCustomEndKVEvent(this, "test_time", prop);
		/*
		 * Properties prop = new Properties(); prop.setProperty("username",
		 * "cjy"); StatService.trackCustomEndKVEvent(this, "test_time", prop);
		 */
		// dialog.dismiss();
		/*
		 * if(NetUtil.is34G(this)) Toast.makeText(Tab_IndexActivity.this,
		 * "is34G", 0).show(); else Toast.makeText(Tab_IndexActivity.this,
		 * "is2G", 0).show(); if(NetUtil.isWifi(this))
		 * Toast.makeText(Tab_IndexActivity.this, "isWifi", 0).show();
		 */
		/*
		 * Intent intent = new Intent(); intent.setClass(Tab_IndexActivity.this,
		 * TestActivity.class); startActivity(intent);
		 */
	}

	@SuppressLint("NewApi")
	public void click3(View view) {
		Intent intent = new Intent(this, TestList.class);
		startActivity(intent);
		// String path = Environment.getExternalStorageDirectory().getPath();
		// //Don't use "/sdcard/" here
		/*
		 * String path = getApplicationContext().getFilesDir().toString();
		 * String fileName = "upload.jpg"; //报文中的文件名参数 String uploadFile = path
		 * + "/" + fileName; //待上传的文件路径 String postUrl =
		 * "http://192.168.191.1/php/upload.php?user_id=5212&format=jpg&action=xys_upload_photos"
		 * ; //处理POST请求的页面 Toast.makeText(Tab_IndexActivity.this,
		 * NetService.uploadToPhp(postUrl, uploadFile),
		 * Toast.LENGTH_SHORT).show();//Post成功
		 */
		String postUrl = "http://www.xys.ren/app_api/user.php";
		ArrayList<NameValuePair> parameters = new ArrayList<NameValuePair>();
		// parameters.add(new BasicNameValuePair("user_id", "5212"));
		parameters.add(new BasicNameValuePair("action", "xys_upload_photos"));
		// parameters.add(new BasicNameValuePair("format", "jpg"));
		Toast.makeText(Tab_IndexActivity.this,
				NetService.clientPost(postUrl, parameters), Toast.LENGTH_SHORT)
				.show();
	}

	public void share(View view) {
		configPlatforms(); // 配置需要各平台sso
		setShareContent(); // 设置各平台的分享内容，与下面的平台对应
		/*
		 * mController.setShareContent("cjy"); mController.setShareMedia(new
		 * UMImage(Tab_IndexActivity.this,
		 * "http://www.umeng.com/images/pic/banner_module_social.png"));
		 */
		/*mController.getConfig().setPlatforms(SHARE_MEDIA.WEIXIN,
				SHARE_MEDIA.WEIXIN_CIRCLE, SHARE_MEDIA.QQ, SHARE_MEDIA.QZONE,
				SHARE_MEDIA.SINA, SHARE_MEDIA.TENCENT, SHARE_MEDIA.DOUBAN,
				SHARE_MEDIA.RENREN, SHARE_MEDIA.SMS, SHARE_MEDIA.SMS,
				SHARE_MEDIA.EMAIL);*/
		mController.getConfig().setPlatforms(SHARE_MEDIA.QQ, SHARE_MEDIA.WEIXIN, SHARE_MEDIA.SINA,
				SHARE_MEDIA.WEIXIN_CIRCLE, SHARE_MEDIA.QZONE);
		mController.openShare(this, false);
		// 首先在您的Activity中添加如下成员变量
		/*
		 * final UMSocialService mController =
		 * UMServiceFactory.getUMSocialService("com.umeng.share");
		 * mController.openShare(Tab_IndexActivity.this, false); //
		 * 是否只有已登录用户才能打开分享选择页
		 */
	}

	/**
	 * 配置分享平台参数
	 */
	private void configPlatforms() {
		// 添加新浪SSO授权
		mController.getConfig().setSsoHandler(new SinaSsoHandler());
		// 添加腾讯微博SSO授权
		mController.getConfig().setSsoHandler(new TencentWBSsoHandler());
		// 添加人人网SSO授权
		RenrenSsoHandler renrenSsoHandler = new RenrenSsoHandler(this,
				"201874", "28401c0964f04a72a14c812d6132fcef",

				"3bf66e42db1e4fa9829b955cc300b737");
		mController.getConfig().setSsoHandler(renrenSsoHandler);

		addQQQZonePlatform(); // 添加QQ、QZone平台
		addWXPlatform(); // 添加微信、微信朋友圈平台
		addSMS(); // 添加短信平台
		addEmail(); // 添加email平台
	}

	private void addWXPlatform() {
		// 注意：在微信授权的时候，必须传递appSecret
		// wx967daebe835fbeac是你在微信开发平台注册应用的AppID, 这里需要替换成你注册的AppID
		String appId = "wx967daebe835fbeac";
		String appSecret = "5bb696d9ccd75a38c8a0bfe0675559b3";
		// 添加微信平台
		UMWXHandler wxHandler = new UMWXHandler(this, appId, appSecret);
		wxHandler.addToSocialSDK();
		// 支持微信朋友圈
		UMWXHandler wxCircleHandler = new UMWXHandler(this, appId, appSecret);
		wxCircleHandler.setToCircle(true);
		wxCircleHandler.addToSocialSDK();
	}

	/**
	 * @功能描述 : 添加QQ平台支持 QQ分享的内容， 包含四种类型， 即单纯的文字、图片、音乐、视频. 参数说明 : title, summary,
	 *       image url中必须至少设置一个, targetUrl必须设置,网页地址必须以"http://"开头 . title :
	 *       要分享标题 summary : 要分享的文字概述 image url : 图片地址 [以上三个参数至少填写一个] targetUrl
	 *       : 用户点击该分享时跳转到的目标地址 [必填] ( 若不填写则默认设置为友盟主页 )
	 * @return
	 */
	private void addQQQZonePlatform() {
		String appId = "100424468";
		String appKey = "c7394704798a158208a74ab60104f0ba";
		// 添加QQ支持, 并且设置QQ分享内容的target url
		UMQQSsoHandler qqSsoHandler = new UMQQSsoHandler(this, appId, appKey);
		qqSsoHandler.setTargetUrl("http://www.umeng.com/social");
		qqSsoHandler.addToSocialSDK();

		// 添加QZone平台
		QZoneSsoHandler qZoneSsoHandler = new QZoneSsoHandler(this, appId,
				appKey);
		qZoneSsoHandler.addToSocialSDK();
	}

	/**
	 * 添加短信平台</br>
	 */
	private void addSMS() {
		// 添加短信
		SmsHandler smsHandler = new SmsHandler();
		smsHandler.addToSocialSDK();
	}

	/**
	 * 添加Email平台</br>
	 */
	private void addEmail() {
		// 添加email
		EmailHandler emailHandler = new EmailHandler();
		emailHandler.addToSocialSDK();
	}

	/**
	 * 根据不同的平台设置不同的分享内容</br>
	 */
	private void setShareContent() {
		// 配置SSO
		mController.getConfig().setSsoHandler(new SinaSsoHandler());
		mController.getConfig().setSsoHandler(new TencentWBSsoHandler());
		QZoneSsoHandler qZoneSsoHandler = new QZoneSsoHandler(this,
				"100424468", "c7394704798a158208a74ab60104f0ba");
		qZoneSsoHandler.addToSocialSDK();
		mController
				.setShareContent("友盟社会化组件（SDK）让移动应用快速整合社交分享功能。http://www.umeng.com/social");
		// APP ID：201874, API
		// * KEY：28401c0964f04a72a14c812d6132fcef, Secret
		// * Key：3bf66e42db1e4fa9829b955cc300b737.
		RenrenSsoHandler renrenSsoHandler = new RenrenSsoHandler(this,
				"201874", "28401c0964f04a72a14c812d6132fcef",
				"3bf66e42db1e4fa9829b955cc300b737");
		mController.getConfig().setSsoHandler(renrenSsoHandler);

		UMImage localImage = new UMImage(this, R.drawable.device);
		UMImage urlImage = new UMImage(this,
				"http://www.umeng.com/images/pic/social/integrated_3.png");
		// UMImage resImage = new UMImage(this, R.drawable.icon);

		// 视频分享
		UMVideo video = new UMVideo(
				"http://v.youku.com/v_show/id_XNTc0ODM4OTM2.html");
		// vedio.setThumb("http://www.umeng.com/images/pic/home/social/img-1.png");
		video.setTitle("友盟社会化组件视频");
		video.setThumb(urlImage);

		UMusic uMusic = new UMusic(
				"http://music.huoxing.com/upload/20130330/1364651263157_1085.mp3");
		uMusic.setAuthor("umeng");
		uMusic.setTitle("天籁之音");
		// uMusic.setThumb(urlImage);
		uMusic.setThumb("http://www.umeng.com/images/pic/social/chart_1.png");

		// UMEmoji emoji = new UMEmoji(this,
		// "http://www.pc6.com/uploadimages/2010214917283624.gif");
		// UMEmoji emoji = new UMEmoji(this,
		// "/storage/sdcard0/emoji.gif");

		WeiXinShareContent weixinContent = new WeiXinShareContent();
		weixinContent
				.setShareContent("来自友盟社会化组件（SDK）让移动应用快速整合社交分享功能-微信。http://www.umeng.com/social");
		weixinContent.setTitle("友盟社会化分享组件-微信");
		weixinContent.setTargetUrl("http://www.umeng.com/social");
		weixinContent.setShareMedia(urlImage);
		mController.setShareMedia(weixinContent);

		// 设置朋友圈分享的内容
		CircleShareContent circleMedia = new CircleShareContent();
		circleMedia
				.setShareContent("来自友盟社会化组件（SDK）让移动应用快速整合社交分享功能-朋友圈。http://www.umeng.com/social");
		circleMedia.setTitle("友盟社会化分享组件-朋友圈");
		circleMedia.setShareMedia(urlImage);
		// circleMedia.setShareMedia(uMusic);
		// circleMedia.setShareMedia(video);
		circleMedia.setTargetUrl("http://www.umeng.com/social");
		mController.setShareMedia(circleMedia);

		// 设置renren分享内容
		RenrenShareContent renrenShareContent = new RenrenShareContent();
		renrenShareContent.setShareContent("人人分享内容");
		UMImage image = new UMImage(this, BitmapFactory.decodeResource(
				getResources(), R.drawable.device));
		image.setTitle("thumb title");
		image.setThumb("http://www.umeng.com/images/pic/social/integrated_3.png");
		renrenShareContent.setShareImage(image);
		renrenShareContent.setAppWebSite("http://www.umeng.com/social");
		mController.setShareMedia(renrenShareContent);

		UMImage qzoneImage = new UMImage(this,
				"http://www.umeng.com/images/pic/social/integrated_3.png");
		qzoneImage
				.setTargetUrl("http://www.umeng.com/images/pic/social/integrated_3.png");

		// 设置QQ空间分享内容
		QZoneShareContent qzone = new QZoneShareContent();
		qzone.setShareContent("share test");
		qzone.setTargetUrl("http://www.umeng.com");
		qzone.setTitle("QZone title");
		qzone.setShareMedia(urlImage);
		// qzone.setShareMedia(uMusic);
		mController.setShareMedia(qzone);

		video.setThumb(new UMImage(this, BitmapFactory.decodeResource(
				getResources(), R.drawable.device)));

		QQShareContent qqShareContent = new QQShareContent();
		qqShareContent.setShareContent("来自友盟社会化组件（SDK）让移动应用快速整合社交分享功能 -- QQ");
		qqShareContent.setTitle("hello, title");
		qqShareContent.setShareMedia(image);
		qqShareContent.setTargetUrl("http://www.umeng.com/social");
		mController.setShareMedia(qqShareContent);

		// 视频分享
		UMVideo umVideo = new UMVideo(
				"http://v.youku.com/v_show/id_XNTc0ODM4OTM2.html");
		umVideo.setThumb("http://www.umeng.com/images/pic/home/social/img-1.png");
		umVideo.setTitle("友盟社会化组件视频");

		TencentWbShareContent tencent = new TencentWbShareContent();
		tencent.setShareContent("来自友盟社会化组件（SDK）让移动应用快速整合社交分享功能-腾讯微博。http://www.umeng.com/social");
		// 设置tencent分享内容
		mController.setShareMedia(tencent);

		// 设置邮件分享内容， 如果需要分享图片则只支持本地图片
		MailShareContent mail = new MailShareContent(localImage);
		mail.setTitle("share form umeng social sdk");
		mail.setShareContent("来自友盟社会化组件（SDK）让移动应用快速整合社交分享功能-email。http://www.umeng.com/social");
		// 设置tencent分享内容
		mController.setShareMedia(mail);

		// 设置短信分享内容
		SmsShareContent sms = new SmsShareContent();
		sms.setShareContent("来自友盟社会化组件（SDK）让移动应用快速整合社交分享功能-短信。http://www.umeng.com/social");
		// sms.setShareImage(urlImage);
		mController.setShareMedia(sms);

		SinaShareContent sinaContent = new SinaShareContent();
		sinaContent
				.setShareContent("来自友盟社会化组件（SDK）让移动应用快速整合社交分享功能-新浪微博。http://www.umeng.com/social");
		mController.setShareMedia(sinaContent);

		TwitterShareContent twitterShareContent = new TwitterShareContent();
		twitterShareContent
				.setShareContent("来自友盟社会化组件（SDK）让移动应用快速整合社交分享功能-TWITTER。http://www.umeng.com/social");
		twitterShareContent.setShareMedia(new UMImage(this, new File(
				"/storage/sdcard0/emoji.gif")));
		mController.setShareMedia(twitterShareContent);

		GooglePlusShareContent googlePlusShareContent = new GooglePlusShareContent();
		googlePlusShareContent
				.setShareContent("来自友盟社会化组件（SDK）让移动应用快速整合社交分享功能-G+。http://www.umeng.com/social");
		googlePlusShareContent.setShareMedia(localImage);
		mController.setShareMedia(googlePlusShareContent);

		// 来往分享内容
		LWShareContent lwShareContent = new LWShareContent();
		// lwShareContent.setShareImage(urlImage);
		// lwShareContent.setShareMedia(uMusic);
		lwShareContent.setShareMedia(umVideo);
		lwShareContent.setTitle("友盟社会化分享组件-来往");
		lwShareContent.setMessageFrom("友盟分享组件");
		lwShareContent
				.setShareContent("来自友盟社会化组件（SDK）让移动应用快速整合社交分享功能-来往。http://www.umeng.com/social");
		mController.setShareMedia(lwShareContent);

		// 来往动态分享内容
		LWDynamicShareContent lwDynamicShareContent = new LWDynamicShareContent();
		// lwDynamicShareContent.setShareImage(urlImage);
		// lwDynamicShareContent.setShareMedia(uMusic);
		lwDynamicShareContent.setShareMedia(umVideo);
		lwDynamicShareContent.setTitle("友盟社会化分享组件-来往动态");
		lwDynamicShareContent.setMessageFrom("来自友盟");
		lwDynamicShareContent
				.setShareContent("来自友盟社会化组件（SDK）让移动应用快速整合社交分享功能-来往动态。http://www.umeng.com/social");
		lwDynamicShareContent.setTargetUrl("http://www.umeng.com/social");
		mController.setShareMedia(lwDynamicShareContent);

	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		UMSsoHandler ssoHandler = SocializeConfig.getSocializeConfig()
				.getSsoHandler(requestCode);
		if (ssoHandler != null) {
			ssoHandler.authorizeCallBack(requestCode, resultCode, data);
		}
	}

	public static String encodeBase64File(String path) {
		try {
			File file = new File(path);
			FileInputStream inputFile = new FileInputStream(file);
			byte[] buffer = new byte[(int) file.length()];
			inputFile.read(buffer);
			inputFile.close();
			return Base64.encode(buffer);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}

	// 传入InputStream，ByteArrayOutputStream输出.
	public static String readInputStream(InputStream is) {
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			byte[] buffer = new byte[1024];
			int len = 0;
			// read()输入流中每次读取最多1024字节，并将其存储在数组buffer 中,返回int
			while ((len = is.read(buffer)) != -1) {
				System.out.println("buffer.length===" + buffer.length + " len="
						+ len);
				baos.write(buffer, 0, len); // 将每次新生成的buffer拼接到输出流
			}
			is.close();
			byte[] result = baos.toByteArray();
			return new String(result);
		} catch (Exception e) {
			e.printStackTrace();
			return "转换失败";
		}
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		StatService.trackBeginPage(this, "index");
		// 移动数据统计分析
		FlowerCollector.onResume(Tab_IndexActivity.this);
		FlowerCollector.onPageStart("Tab_IndexActivity");
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		StatService.trackEndPage(this, "index");
		// 移动数据统计分析
		FlowerCollector.onPause(Tab_IndexActivity.this);
		FlowerCollector.onPageEnd("Tab_IndexActivity");
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		// 退出时释放连接
		mIat.cancel();
		mIat.destroy();
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.search:

			break;
		}
	}

}
