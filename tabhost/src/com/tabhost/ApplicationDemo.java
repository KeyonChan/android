package com.tabhost;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

import com.baidu.mapapi.SDKInitializer;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechUtility;
import com.speedtong.sdk.ECDevice;
import com.speedtong.sdk.ECDevice.CallType;
import com.speedtong.sdk.ECDevice.InitListener;
import com.speedtong.sdk.ECDevice.OnECDeviceConnectListener;
import com.speedtong.sdk.ECError;
import com.speedtong.sdk.ECInitialize;
import com.speedtong.sdk.VoipCall;
import com.speedtong.sdk.core.model.VoipCallUserInfo;
import com.speedtong.sdk.core.voip.listener.OnVoipListener;
import com.tabhost.util.MTAUtil;

import android.annotation.SuppressLint;
import android.app.Application;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

public class ApplicationDemo extends Application {

	public static final String PACKAGE_NAME = "com.tabhost/";
	public static final String FILE_PATH = "/data/data/" + PACKAGE_NAME
			+ "files/";
	public static final String SP_PATH = "/data/data/" + PACKAGE_NAME
			+ "shared_prefs/";
	public static final String DB_PATH = "/data/data/" + PACKAGE_NAME
			+ "databases/";
	public static final String CACHE_PATH =  Environment.getExternalStorageDirectory()+ "/tabcache";
	public static final String DB_CITY_LIST_PATH = "/data/data/" + PACKAGE_NAME
			+ "databases/" + "city_list.db";

	public static final String WEB_PROJECT_NAME = "tabhost/";
	public static final String SERVLET = "Ser";
	public static final String URL = "http://192.168.191.1:8080/"
			+ WEB_PROJECT_NAME;
	public static final String URL_SERVLET = "http://192.168.191.1:8080/"
			+ WEB_PROJECT_NAME + SERVLET;
	public static final String URL_UPLOAD = "http://192.168.191.1:8080/"
			+ WEB_PROJECT_NAME + "UploadFile";
	public static final String IMG_NET_SHOP_PATH = URL + "img/shop/";
	public static final String IMG_NET_RECOMMEND_PATH = URL + "img/recommend/";
	public static final String IMG_NET_HREDPHOTO_PATH = URL + "img/headPhoto/";
	public static final String IMG_NET_ARTICLE_PATH = URL + "img/article/";
	public static final String FILE_CITY_LIST = URL + "city_list.db";
	
	public static int PAGESIZE = 2;
	public static Context context;

	@Override
	public void onCreate() {
		super.onCreate();
		System.out.println("--------ApplicationDemo");
		context = getApplicationContext();
		SDKInitializer.initialize(this); // 短信验证码
		MTAUtil.mtaInit(getApplicationContext()); // 推送
		SpeechUtility.createUtility(getApplicationContext(),
				SpeechConstant.APPID + "=557ba317"); //
		System.out.println("SpeechConstant.APPID " + SpeechConstant.APPID);
		File file = new File(FILE_PATH); // 手动生成目录
		if (!file.exists())
			file.mkdirs();
		file = new File(SP_PATH);
		if (!file.exists())
			file.mkdirs();
		file = new File(DB_PATH);
		if (!file.exists())
			file.mkdirs();
		file = new File(CACHE_PATH);
		if (!file.exists())
			file.mkdirs();
		/*
		 * file = new File(DB_PATH+"city_list.db"); if(!file.exists()){ new
		 * Thread(){ public void run(){ try { //下载城市列表数据库 URL url = new
		 * URL(File_CITY_LIST); HttpURLConnection conn = (HttpURLConnection)
		 * url.openConnection(); conn.setRequestMethod("GET");
		 * conn.setReadTimeout(5000); int code = conn.getResponseCode(); if
		 * (code == 200) { int length = conn.getContentLength();
		 * System.out.println("文件大小："+length); int blockSize = length
		 * /threadCount; //threadId无需定义 for(int threadId=1;
		 * threadId<=threadCount; threadId++){ int startIndex =
		 * (threadId-1)*blockSize; int endIndex = threadId*blockSize-1;
		 * if(threadId==threadCount) endIndex = length;
		 * System.out.println("线程"+threadId+" 下载:"+startIndex+"-"+endIndex); new
		 * DownloadThread(threadId, startIndex, endIndex,
		 * File_CITY_LIST).start(); } } else { System.out.println("服务器错误"); } }
		 * catch (Exception e) { System.out.println("异常"); e.printStackTrace();
		 * } } }.start(); }
		 */

	}





}
