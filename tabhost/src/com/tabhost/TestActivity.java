package com.tabhost;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.DbException;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.HttpHandler;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.util.LogUtils;
import com.lidroid.xutils.util.PreferencesCookieStore;
import com.tabhost.download.DownloadManager;
import com.tabhost.download.DownloadService;
import com.tabhost.util.CommonUtil;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;

public class TestActivity extends Activity {
	private PreferencesCookieStore preferencesCookieStore;
	private DownloadManager downloadManager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_test);
	}

	public void download1() {
		HttpUtils http = new HttpUtils();
		HttpHandler handler = http.download(ApplicationDemo.URL + "你.zip",
				ApplicationDemo.FILE_PATH + "你3.zip", true, // 如果目标文件存在，接着未完成的部分继续下载。服务器不支持RANGE时将从新下载。
				true, // 如果从请求返回信息中获取到文件名，下载完成后自动重命名。
				new RequestCallBack<File>() {

					@Override
					public void onStart() {

					}

					@Override
					public void onLoading(long total, long current,
							boolean isUploading) {
						System.out
								.println("download: " + current + "/" + total);
					}

					@Override
					public void onSuccess(ResponseInfo<File> responseInfo) {
						// testTextView.setText("downloaded:" +
						// responseInfo.result.getPath());
					}

					@Override
					public void onFailure(HttpException error, String msg) {
						// testTextView.setText(msg);
					}
				});

	}

	public void download2() {
		downloadManager = DownloadService.getDownloadManager(this);
		String fileName = ApplicationDemo.FILE_PATH + "你2.zip";
		try {
			downloadManager.addNewDownload(ApplicationDemo.URL + "你.zip",
					"力卓文件", // 别名，没用
					fileName, true, // 如果目标文件存在，接着未完成的部分继续下载。服务器不支持RANGE时将从新下载。
					false, // 如果从请求返回信息中获取到文件名，下载完成后自动重命名。
					null);
		} catch (DbException e) {
			LogUtils.e(e.getMessage(), e);
		}
	}

	public void upload() {
		preferencesCookieStore = new PreferencesCookieStore(this);
		// 设置请求参数的编码
		RequestParams params = new RequestParams(); // 默认编码UTF-8，RequestParams
													// params = new
													// RequestParams("GBK");
		// params.addQueryStringParameter("qmsg", "你好");
		// params.addBodyParameter("msg", "测试");
		// 添加文件
		params.addBodyParameter("file", new File(ApplicationDemo.SP_PATH
				+ "userInfo.xml"));
		// params.addBodyParameter("testfile", new File("/sdcard/test2.zip"));
		// // 继续添加文件

		// 用于非multipart表单的单文件上传
		// params.setBodyEntity(new FileUploadEntity(new
		// File("/sdcard/test.zip"), "binary/octet-stream"));

		// 用于非multipart表单的流上传
		// params.setBodyEntity(new InputStreamUploadEntity(stream ,length));

		HttpUtils http = new HttpUtils();

		// 设置返回文本的编码， 默认编码UTF-8
		// http.configResponseTextCharset("GBK");

		// 自动管理 cookie
		http.configCookieStore(preferencesCookieStore);

		http.send(HttpRequest.HttpMethod.POST, ApplicationDemo.URL_UPLOAD,
				params, new RequestCallBack<String>() {

					@Override
					public void onStart() {
						// resultText.setText("conn...");
					}

					@Override
					public void onLoading(long total, long current,
							boolean isUploading) {
						if (isUploading) {
							System.out.println("upload: " + current + "/"
									+ total);
						} else {

							System.out.println("reply: " + current + "/"
									+ total);
						}
					}

					@Override
					public void onSuccess(ResponseInfo<String> responseInfo) {
						System.out.println("reply: " + responseInfo.result);
					}

					@Override
					public void onFailure(HttpException error, String msg) {
					}
				});
	}

}
