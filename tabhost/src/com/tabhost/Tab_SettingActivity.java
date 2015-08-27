package com.tabhost;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.tabhost.R;
import com.tabhost.util.FileUtil;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class Tab_SettingActivity extends BaseActivity {
	ImageView headphoto;
	TextView order, collection;
	private static final int NONE = 0, PHOTOHRAPH = 1, PHOTOZOOM = 2,
			PHOTORESOULT = 3;
	private static final String IMAGE_UNSPECIFIED = "image/*";
	private String uploadImgName = "upload.jpg";
	private String uploadImgFullName = ApplicationDemo.FILE_PATH + "/"
			+ "upload.jpg";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.tab_setting);
		headphoto = (ImageView) findViewById(R.id.headphoto);
		headphoto.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				String[] items = new String[] { "从相册选择", "拍           照" };
				AlertDialog.Builder builder = new AlertDialog.Builder(
						Tab_SettingActivity.this);
				builder.setItems(items, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int position) {
						// 相册
						if (position == 0) {
							Intent intent = new Intent(Intent.ACTION_PICK, null);
							intent.setDataAndType(
									MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
									IMAGE_UNSPECIFIED);
							startActivityForResult(intent, PHOTOZOOM);
						} else {
							Intent intent = new Intent(
									MediaStore.ACTION_IMAGE_CAPTURE);
							intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri
									.fromFile(new File(Environment
											.getExternalStorageDirectory(),
											uploadImgName))); // 魅族不允许保存截图到项目的文件中
							startActivityForResult(intent, PHOTOHRAPH);
						}
					}
				});
				builder.create().show();
			}
		});
	}

	// 处理图片结果
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// 拍照
		if (requestCode == PHOTOHRAPH) {
			if (resultCode == RESULT_OK) {
				File picture = new File(
						Environment.getExternalStorageDirectory() + "/"
								+ uploadImgName);
				startPhotoZoom(Uri.fromFile(picture));
			} else if (resultCode == RESULT_CANCELED) {
				Toast.makeText(this, "取消截图", 0).show();
			} else {
				Toast.makeText(this, "图像捕获失败", 0).show();
			}
		}
		// 读取相册缩放图片
		if (requestCode == PHOTOZOOM) {
			if (resultCode == RESULT_OK) {
				startPhotoZoom(data.getData());
			} else if (resultCode == RESULT_CANCELED) {
				Toast.makeText(this, "取消截图", 0).show();
			} else {
				Toast.makeText(this, "图像捕获失败", 0).show();
			}
		}

		// 处理结果 ，上述两步截屏界面时后退都会导致intent为空、无响应
		if (requestCode == PHOTORESOULT) {
			if (data == null) {
				return; // 为空时则不再执行
			}
			Bundle extras = data.getExtras();
			if (extras != null) {
				Bitmap bmp = extras.getParcelable("data");
				FileOutputStream fos = null;
				try {
					fos = new FileOutputStream(
							Environment.getExternalStorageDirectory() + "/"
									+ uploadImgName);
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				bmp.compress(Bitmap.CompressFormat.JPEG, 75, fos);// 生成截图到本地，(0
																	// -
																	// 100)压缩文件
				headphoto = (ImageView) findViewById(R.id.headphoto);
				headphoto.setImageBitmap(bmp);

				// 上传图片-------------------------------
				/*
				 * File file = new
				 * File(Environment.getExternalStorageDirectory() +
				 * "/"+uploadImgName); AsyncHttpClient client = new
				 * AsyncHttpClient(); RequestParams params = new
				 * RequestParams(); try { params.put("file", file); } catch
				 * (FileNotFoundException e) { // TODO Auto-generated catch
				 * block e.printStackTrace(); }
				 * client.post(ApplicationDemo.IP_ADDRESS_UPLOAD, params, new
				 * AsyncHttpResponseHandler() { public void onSuccess(int
				 * statusCode, String content) {
				 * Toast.makeText(Tab_SettingActivity.this, "上传成功", 0).show(); }
				 * public void onFailure(Throwable error, String content) {
				 * Toast.makeText(Tab_SettingActivity.this, content, 0).show();
				 * }
				 * 
				 * });
				 */
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	// 截图设置
	public void startPhotoZoom(Uri uri) {
		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setDataAndType(uri, IMAGE_UNSPECIFIED);
		intent.putExtra("crop", "true"); // 滑动选中区域
		intent.putExtra("aspectX", 1); // aspectX aspectY 是宽高的比例
		intent.putExtra("aspectY", 1);
		intent.putExtra("outputX", 80); // 输出的图片宽高
		intent.putExtra("outputY", 80);
		intent.putExtra("return-data", true);
		this.startActivityForResult(intent, PHOTORESOULT);
	}

	public void logout(View view) {
		FileUtil.deleteSP("userInfo");
		Intent intent = new Intent(this, LoginActivity.class);
		startActivity(intent);
	}
}
